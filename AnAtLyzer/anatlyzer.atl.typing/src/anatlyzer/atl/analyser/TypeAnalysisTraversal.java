package anatlyzer.atl.analyser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import anatlyzer.atl.analyser.namespaces.CollectionNamespace;
import anatlyzer.atl.analyser.namespaces.GlobalNamespace;
import anatlyzer.atl.analyser.namespaces.ITypeNamespace;
import anatlyzer.atl.analyser.namespaces.MetamodelNamespace;
import anatlyzer.atl.analyser.recovery.IRecoveryAction;
import anatlyzer.atl.errors.atl_error.LocalProblem;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.model.ErrorModel;
import anatlyzer.atl.types.CollectionType;
import anatlyzer.atl.types.EmptyCollectionType;
import anatlyzer.atl.types.EnumType;
import anatlyzer.atl.types.ThisModuleType;
import anatlyzer.atl.types.Type;
import anatlyzer.atl.types.TypeError;
import anatlyzer.atl.types.Unknown;
import anatlyzer.atl.util.ATLUtils;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.CalledRule;
import anatlyzer.atlext.ATL.ContextHelper;
import anatlyzer.atlext.ATL.ForEachOutPatternElement;
import anatlyzer.atlext.ATL.ForStat;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.LazyRule;
import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.ModuleElement;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.atlext.ATL.RuleVariableDeclaration;
import anatlyzer.atlext.ATL.SimpleInPatternElement;
import anatlyzer.atlext.ATL.SimpleOutPatternElement;
import anatlyzer.atlext.ATL.Unit;
import anatlyzer.atlext.OCL.Attribute;
import anatlyzer.atlext.OCL.BooleanExp;
import anatlyzer.atlext.OCL.CollectionOperationCallExp;
import anatlyzer.atlext.OCL.EnumLiteralExp;
import anatlyzer.atlext.OCL.IfExp;
import anatlyzer.atlext.OCL.IntegerExp;
import anatlyzer.atlext.OCL.IterateExp;
import anatlyzer.atlext.OCL.Iterator;
import anatlyzer.atlext.OCL.IteratorExp;
import anatlyzer.atlext.OCL.JavaBody;
import anatlyzer.atlext.OCL.LetExp;
import anatlyzer.atlext.OCL.LoopExp;
import anatlyzer.atlext.OCL.MapExp;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclContextDefinition;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.OclFeature;
import anatlyzer.atlext.OCL.OclFeatureDefinition;
import anatlyzer.atlext.OCL.OclUndefinedExp;
import anatlyzer.atlext.OCL.Operation;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.atlext.OCL.OperatorCallExp;
import anatlyzer.atlext.OCL.OrderedSetExp;
import anatlyzer.atlext.OCL.RealExp;
import anatlyzer.atlext.OCL.ResolveTempResolution;
import anatlyzer.atlext.OCL.SequenceExp;
import anatlyzer.atlext.OCL.SetExp;
import anatlyzer.atlext.OCL.StringExp;
import anatlyzer.atlext.OCL.TupleExp;
import anatlyzer.atlext.OCL.TuplePart;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;

public class TypeAnalysisTraversal extends AbstractAnalyserVisitor {

	public TypeAnalysisTraversal(ATLModel model, GlobalNamespace mm, Unit root) {
		super(model, mm, root);
	
		attr = new ComputedAttributes(this);
		// mm.setDependencies(new EcoreTypeConverter(typ), errors);
	}
		
	
	public void perform() {
		
		// 1. Get meta-model elements for the explicitly named types
		ExplicitTypeTraversal explicit = new ExplicitTypeTraversal(model, mm, root);
		explicit.perform(attr);
		
		// 2. Process helpers and operations
		TopLevelTraversal helperOperations = new TopLevelTraversal(model, mm, root);
		helperOperations.perform(attr);
		
		// 3. Assign types
		startVisiting(root);
		
		// 4. Ocl Compliance
		OclCompliance compliance = new OclCompliance(model, mm, root);
		
		compliance.perform(attr);
		
		ComputeResolvers computeResolvers = new ComputeResolvers(model, mm, root);
		computeResolvers.perform(attr);
		
		RuleAnalysis ruleAnalysis = new RuleAnalysis(model, mm, root);
		ruleAnalysis.perform(attr);
		
	}
	
	
	private ThisModuleType thisModuleType;

	private ThisModuleType getThisModuleType() {
		if ( thisModuleType == null ) {
			thisModuleType = typ().createThisModuleType();
			thisModuleType.setMetamodelRef(mm.getTransformationNamespace());
		}
		return thisModuleType;
	}

	
	public VisitingActions preModule(anatlyzer.atlext.ATL.Module self) { 
		return actions("libraries", "inModels", "outModels", 
				filter("getHelpers", self), filter("getRules", self));
	} 
	
	@Override
	public VisitingActions preMatchedRule(MatchedRule self) {
		return actions("inPattern", "variables", "outPattern" , "actionBlock"); 
	}
	
	@Override
	public VisitingActions preLazyRule(LazyRule self) {
		return actions("inPattern", "variables", "outPattern" , "actionBlock"); 
	}

	@Override
	public VisitingActions preCalledRule(CalledRule self) {
		return actions("parameters", "variables", "outPattern" , "actionBlock"); 
	}
		
	public List<Helper> getHelpers(anatlyzer.atlext.ATL.Module self) {
		LinkedList<Helper> helpers = new LinkedList<Helper>();
		for (ModuleElement me : self.getElements()) {
			if ( me instanceof Helper ) {
				Helper h = (Helper) me;
				OclFeature f = h.getDefinition().getFeature();
				if ( f instanceof Operation ) {
					if ( attr.typeOf( ((Operation) f).getReturnType() ) instanceof Unknown ) {
						helpers.addFirst(h);
						continue;
					} 
				} else if ( f instanceof Attribute ) {
					if ( attr.typeOf( ((Attribute) f).getType() ) instanceof Unknown ) {
						helpers.addFirst(h);
						continue;						
					}
				}
				
				helpers.addLast((Helper) me);				
			}
		}
		return helpers;
	}

	public List<Rule> getRules(anatlyzer.atlext.ATL.Module self) {
		ArrayList<Rule> rules = new ArrayList<Rule>();
		for (ModuleElement me : self.getElements()) {
			if ( me instanceof Rule ) 
				rules.add((Rule) me);
		}
		return rules;
	}
	
	@Override
	public void inOperation(Operation self) {
		Type declared = attr.typeOf(self.getReturnType());
		Type inferred = attr.typeOf(self.getBody());
		
		if ( declared instanceof Unknown ) {
			TopLevelTraversal.extendTypeForOperation(self, mm, attr, inferred);
		}
		
		Helper h = (Helper) self.eContainer().eContainer();
		h.setStaticReturnType(declared);
		h.setInferredReturnType(inferred);
	}
	
	@Override
	public void inAttribute(Attribute self) {
		Type declared = attr.typeOf(self.getType());
		Type inferred = attr.typeOf(self.getInitExpression());

		if ( declared instanceof Unknown ) {
			TopLevelTraversal.extendTypeForAttribute(self, mm, attr, inferred);
		}

		Helper h = (Helper) self.eContainer().eContainer();
		h.setStaticReturnType(declared);
		h.setInferredReturnType(inferred);
	}
	
	

	public VisitingActions preLetExp(anatlyzer.atlext.OCL.LetExp self) { return actions("type" , "variable" , "in_"); } 

	
	@Override
	public void inBinding(Binding self) {
	//	System.out.println("Binding: " + self.getLocation() + " is " + TypeUtils.typeToString(attr.typeOf(self.getValue())) + " - " + attr.typeOf(self.getValue()));
	}
	
	@Override
	public void inLetExp(LetExp self) {
		attr.linkExprType( attr.typeOf( self.getIn_() ) );
	}
	
	@Override
	public void inVariableDeclaration(VariableDeclaration self) {
		treatVariableDeclaration(self);
	}


	private void treatVariableDeclaration(VariableDeclaration self) {
		Type exprType = attr.typeOf( self.getInitExpression() );
		if ( self.getType() == null ) {
			attr.linkExprType(exprType);
		} else {
			Type declared = attr.typeOf( self.getType() );

			if ( ! typ().assignableTypes(declared, exprType) ) {
				errors().warningVarDclIncoherentTypes(exprType, declared, self);
			
				if ( AnalyserContext.isVarDclInferencePreferred() && typ().moreConcrete(declared, exprType) == exprType ) {
					attr.linkExprType(exprType);					
				} else {
					attr.linkExprType(declared);
				}
			} else {
				Type t = typ().determineVariableType(declared, exprType,  AnalyserContext.isVarDclInferencePreferred());				
				attr.linkExprType(t);
				// attr.linkExprType(exprType);
			}
			
		}
	}
	
	@Override
	public void inRuleVariableDeclaration(RuleVariableDeclaration self) {
		treatVariableDeclaration(self);
		/*
		Type t = attr.typeOf( self.getInitExpression() );
		if ( self.getType() != null ) {
			// Decide about the most concrete type
			// TODO: WARNING MAY ARISE IF THEY DO NO COINCIDE
		} else {
			attr.linkExprType(t);
		}
		*/
	}


	@Override
	public VisitingActions preIfExp(IfExp self) {
		return actions("type", 
				method("createIfScope", self, true),
				"condition", 
				"thenExpression" , 
				method("createIfScope", self, false), 
				"elseExpression");
	}	
	
	
	@Override
	public void inIfExp(IfExp self) {
		final Type thenPart = attr.typeOf(self.getThenExpression());
		final Type elsePart = attr.typeOf(self.getElseExpression());
				
		// TODO: Perhaps not the same type but compatible types!
		if ( ! typ().equalTypes(thenPart, elsePart) ) {
			if ( thenPart instanceof CollectionType && elsePart instanceof CollectionType ) {
				CollectionType ctThen = (CollectionType) thenPart;
				CollectionType ctElse = (CollectionType) elsePart;
				
				if ( ctThen.getContainedType() instanceof EmptyCollectionType && ! (ctElse.getContainedType() instanceof EmptyCollectionType) ) {
					attr.linkExprType( elsePart );
					return;
				} else if ( ctElse.getContainedType() instanceof EmptyCollectionType && ! (ctThen.getContainedType() instanceof EmptyCollectionType) ) {
					attr.linkExprType( thenPart );					
					return;
				}
			} else if ( thenPart instanceof TypeError ) {
				attr.linkExprType( elsePart );					
				return;
			} else if ( elsePart instanceof TypeError ) {
				attr.linkExprType( thenPart );					
				return;
			} 

			Type recovered = typ().getCommonType(thenPart, elsePart);
			// TODO: Do this better because this generates a lot of false errors()...
			/*
			Type recovered = errors().signalDifferentBranchTypes(thenPart, elsePart, self, new IRecoveryAction() {
				@Override
				public Type recover(ErrorModel m, LocalProblem p) {
					// TODO: Launch a greedy algorithm to decide the best option
					//       between thenPart and elsePart										
					return thenPart;
				}
			});
			*/
			
			attr.linkExprType( recovered );
		} else {			
			attr.linkExprType( thenPart );
		}
	}
	
	// 
	// Navigation
	//
	
	@Override
	public VisitingActions preIteratorExp(IteratorExp self) {
		return actions("source", "iterators", "body");
	}
	
	
	
	@Override
	public void inIterator(Iterator self) {
		if ( self.eContainer() instanceof LoopExp ) { // IteratorExp & IterateExp
			Type collType = attr.typeOf(((LoopExp) self.eContainer()).getSource());
			Type t  = null;
			if ( !(collType instanceof CollectionType) ) {
				t = errors().signalIteratorOverNoCollectionType(collType, (LoopExp) self.eContainer());
			} else {
				t = ((CollectionNamespace) collType.getMetamodelRef()).unwrap();
			}
	
			attr.linkExprType( t );
		// } else if ( self.container_() instanceof IterateExp ) {
			
		} else if ( self.eContainer() instanceof ForEachOutPatternElement ){
			ForEachOutPatternElement e = (ForEachOutPatternElement) self.eContainer();
			Type t = attr.typeOf(e.getCollection());
			if ( ! (t instanceof CollectionType) ) {
				t = AnalyserContext.getErrorModel().signalExpectedCollectionInForEachOutputPattern(e);
			} else {
				t = ((CollectionType) t).getContainedType();
			}
			attr.linkExprType(self, t);
		} else {
			ForStat fs = (ForStat) self.eContainer();
			Type t = attr.typeOf(fs.getCollection());
			if ( ! (t instanceof CollectionType) ) {
				t = AnalyserContext.getErrorModel().signalExpectedCollectionInForStat(fs);
			} else {
				t = ((CollectionType) t).getContainedType();
			}
			attr.linkExprType(self, t);
			
		}
	}
	
	@Override
	public VisitingActions preIterateExp(IterateExp self) {
		return actions("source", "iterators" , "result", "type" , "body" );
	}
	
	@Override
	public VisitingActions preForEachOutPatternElement(ForEachOutPatternElement self) {
		return actions("type" , "initExpression" , "collection", "iterator", "bindings" ); 
	}
	
	@Override
	public VisitingActions preForStat(ForStat self) {
		return actions("collection" , "iterator" , "statements" ); 
	}
	
	
	@Override
	public void inIterateExp(IterateExp self) {
		attr.linkExprType( attr.typeOf( self.getResult() ) );
	}
	
	@Override
	public void inIteratorExp(IteratorExp self) {

		Type srcType =  attr.typeOf( self.getSource() );
		if ( !(srcType instanceof CollectionType) ) {
			Type t = errors().signalIteratorOverNoCollectionType(srcType, self.getSource());
			attr.linkExprType(t);
		} else {
			Type bodyType = attr.typeOf( self.getBody() ); 
			
			CollectionNamespace cspace = (CollectionNamespace) srcType.getMetamodelRef();
			Type iteratorType = cspace.getIteratorType(self.getName(), bodyType, self);
			attr.linkExprType( iteratorType );

			if ( iteratorType.getNoCastedType() != null ) {				
				typ().markImplicitlyCasted(self, iteratorType);
			}
		}
		
	}
	
	@Override
	public void inNavigationOrAttributeCallExp(NavigationOrAttributeCallExp self) {
		Type t = attr.typeOf( self.getSource() );
		
		// TODO: FIX: Warn about navigation of collection with "." (ATL just crash)
		ITypeNamespace tspace = (ITypeNamespace) t.getMetamodelRef();
		Type t2 = tspace.getFeatureType(self.getName(), self);
	
		attr.linkExprType(t2);
		/*
		// Get the navigated feature, cached by featureType
		navFeature[self] <- self.getLastNavigatedFeature
		*/
		
		if ( attr.wasCasted(self.getSource()) ){
			typ().markImplicitlyCasted(self.getSource(), t);
		}
	}
	
	@Override
	public void inOperationCallExp(OperationCallExp self) {
		if ( self.getOperationName().equals("resolveTemp") ) {
			resolveResolveTemp(self);
			return;
		} else if ( self.getOperationName().equals("oclAsType") ) {
			// Special addition, not supported by ATL
			attr.linkExprType(attr.typeOf( self.getArguments().get(0) ));
			return;
		}
		
		// Treating oclIsKindOf
		if ( self.getOperationName().equals("oclIsKindOf") || self.getOperationName().equals("oclIsTypeOf") ) {
			Type exprType = attr.typeOf(self.getArguments().get(0));
			
			// Discard those with a negation
			boolean hasNegation = false;
			EObject parent = self.eContainer();
			while ( parent instanceof OclExpression && ! hasNegation ) {
				if ( parent instanceof OperatorCallExp ) {
					hasNegation = ((OperatorCallExp) parent).getOperationName().equals("not");
				}
				parent = parent.eContainer();
			}
			
			if ( ! hasNegation ) {
				VariableExp ve = VariableScope.findStartingVarExp(self);
				attr.getVarScope().putKindOf(ve.getReferredVariable(), self.getSource(), exprType);
			}
		}
		
		Type t = attr.typeOf( self.getSource() );
		Type[] arguments  = new Type[self.getArguments().size()];
		for(int i = 0; i < self.getArguments().size(); i++) {
			arguments[i] = attr.typeOf(self.getArguments().get(i));
		}
	
		if ( t instanceof CollectionType && AnalyserContext.isOclStrict() ) {
			errors().signalOperationOverCollectionType(self);
		}
		
		ITypeNamespace tspace = (ITypeNamespace) t.getMetamodelRef();		
		attr.linkExprType( tspace.getOperationType(self.getOperationName(), arguments, self) );
		
		if ( attr.wasCasted(self.getSource()) ){
			typ().markImplicitlyCasted(self.getSource(), t);
		}
	}
	
	private void resolveResolveTemp(OperationCallExp self) {
		if ( ! (root instanceof Module ) ) {
			errors().signalNoRecoverableError("resolveTemp only available in transformation modules", self);
		}
		
		if ( self.getArguments().size() != 2 ) {
			errors().signalNoRecoverableError("resolveTemp expects two arguments", self);
		}

		if ( ! (self.getArguments().get(1) instanceof StringExp ) ) {
			System.out.println("Cannot deal with resolveTemp with second argument not being string: " + self.getLocation());
			// attr.linkExprType(typ().newUnknownType());
			// attr.linkExprType(typ().newTypeErrorType(null));
			attr.linkExprType(typ().newUnknownType());
			return;
		}
		
		OclExpression resolvedObj = self.getArguments().get(0);
		String expectedVarName = ((StringExp) self.getArguments().get(1)).getStringSymbol();
		ArrayList<MatchedRule> compatibleRules = new ArrayList<MatchedRule>();
		String withSameVarRules = ""; // TODO: Convert into a collection

		Type selectedType = null;
		Type type_ = attr.typeOf(resolvedObj);
		
		boolean sourceCompatibleRuleFound = false;
		Module m = (Module) root;
		for(ModuleElement e : m.getElements()) {
			if ( e instanceof MatchedRule && ! ((MatchedRule) e).isIsAbstract()) {
				MatchedRule mr = (MatchedRule) e;
				if ( mr.getInPattern().getElements().size() == 1 ) {
					SimpleInPatternElement pe = (SimpleInPatternElement) mr.getInPattern().getElements().get(0);
					Type subtype = attr.typeOf(pe.getType());
		
					if ( typ().isCompatible(subtype, type_) ) {
						sourceCompatibleRuleFound = true;
						
						compatibleRules.add(mr);
						
						// This is the rule!
						for(OutPatternElement ope : ATLUtils.getAllOutputPatternElement(mr) ) {
							SimpleOutPatternElement sope = (SimpleOutPatternElement) ope;
							if ( sope.getVarName().equals(expectedVarName) ) {
								Type t = attr.typeOf(sope.getType());
								
								withSameVarRules += mr.getName() + ", ";
								if ( selectedType != null && ! typ().equalTypes(t, selectedType)) {
									errors().signalResolveTempGetsDifferentTargetTypes("Several rules may resolve the same resolveTemp with different target types: " + withSameVarRules, self);
								}
								
								// Create a resolution info object, even when what it is resolved may be
								// conflicting. Perhaps it could be marked what conflicts with what!
								ResolveTempResolution resolution = OCLFactory.eINSTANCE.createResolveTempResolution();
								resolution.setRule(mr);
								resolution.setElement(sope);
								self.getResolveTempResolvedBy().add(resolution);
								
								selectedType = t;								
							}
						}				
						
					}
				}
			}
		}
		
		if ( selectedType != null ) {
			attr.linkExprType(selectedType);
			return;
		}
		
		if ( ! sourceCompatibleRuleFound ) {
			Type r = errors().signalResolveTempWithoutRule(self, type_); 
			attr.linkExprType(r);
		} else {
			Type r = errors().signalResolveTempOutputPatternElementNotFound(self, type_, expectedVarName, compatibleRules);
			attr.linkExprType(r);
		}
	}


	@Override
	public void inVariableExp(VariableExp self) {
		if ( self.getReferredVariable().getVarName().equals("self") ) {
			// Find the container that defines the self's type
			EObject container = self.eContainer();
			while ( container != null ) {
				if ( container instanceof OclFeatureDefinition ) {
					OclContextDefinition ctx = ((OclFeatureDefinition) container).getContext_();	
					if ( ctx == null ) {
						attr.linkExprType( getThisModuleType() ); // self may refer to thisModule in a global helper...						
					} else {
						attr.linkExprType( attr.typeOf(ctx.getContext_() ));
					}
					break;
				}
				container = container.eContainer();
			}
			
			if ( container == null ) {
				throw new IllegalStateException("Could not find context for self " + self.getLocation());
			}
			
		} else if ( self.getReferredVariable().getVarName().equals("thisModule") ) {
			attr.linkExprType( getThisModuleType() );
		} else {
			attr.linkExprType( attr.typeOf(self.getReferredVariable()) );
		}
	}
	
	@Override
	public void inCollectionOperationCallExp(final CollectionOperationCallExp self) {
		final Type receptorType = attr.typeOf(self.getSource());
		final Type[] arguments  = new Type[self.getArguments().size()];
		for(int i = 0; i < self.getArguments().size(); i++) {
			arguments[i] = attr.typeOf(self.getArguments().get(i));
		}
		
		if ( ! ( receptorType instanceof CollectionType ) ) {
			final ITypeNamespace tspace = (ITypeNamespace) receptorType.getMetamodelRef();	
			if ( AnalyserContext.isOclStrict() ) {
				Type recType = errors().signalCollectionOperationOverNoCollectionType(receptorType, self, new IRecoveryAction() {				
					@Override
					public Type recover(ErrorModel m, LocalProblem p) {
						return tspace.getOperationType(self.getOperationName(), arguments, self);
					}
				});

				attr.linkExprType( recType );
			} else {
				Type t = tspace.getOperationType(self.getOperationName(), arguments, self);
				attr.linkExprType( t );
			}
			
		} else {
			CollectionNamespace namespace = (CollectionNamespace) receptorType.getMetamodelRef();
			String          operationName = self.getOperationName();
			
			Type t = namespace.getOperationType(operationName, arguments, self);
			attr.linkExprType(t);
		}
	}

	@Override
	public void inOperatorCallExp(OperatorCallExp self) {
		Type t = attr.typeOf(self.getSource());
		Type optional = null;
		if ( self.getArguments().size() > 0 )
			optional = attr.typeOf(self.getArguments().get(0));

		if ( optional instanceof TypeError ) {
			// propagate error
			attr.linkExprType(optional);
			return;
		}
		
		ITypeNamespace tspace = (ITypeNamespace) t.getMetamodelRef();
		attr.linkExprType(tspace.getOperatorType(self.getOperationName(), optional, self));
	}
	// 
	// Literal values
	// 
	
	@Override
	public void inEnumLiteralExp(EnumLiteralExp self) {
		for(MetamodelNamespace ns : mm.getMetamodels()) {
			EnumType enum_ = ns.findEnumLiteral(self.getName());
			if ( enum_ != null ) {
				attr.linkExprType( enum_ );
				return;
			}
		}
		
		errors().signalNoEnumLiteral(self.getName(), self);
	}
	
	@Override
	public void inStringExp(StringExp obj) {
		attr.linkExprType(typ().newStringType());
	}

	@Override
	public void inIntegerExp(IntegerExp obj) {
		attr.linkExprType(typ().newIntegerType());
	}

	@Override
	public void inRealExp(RealExp obj) {
		attr.linkExprType(typ().newFloatType());
	}

	@Override
	public void inBooleanExp(BooleanExp self) {
		attr.linkExprType(typ().newBooleanType());
	}
	
	@Override
	public void inOclUndefinedExp(OclUndefinedExp obj) {
		attr.linkExprType(typ().newOclUndefinedType());		
	};

	@Override
	public void inSequenceExp(SequenceExp self) {
		// Three cases:
		//   - Non empty inicialization -> types is the union of all the expressions (elements reference)
		//   - Empty inicialization within VarDcl -> type of the VarDcl (may create circular dep if done naively!)
		//   - Unknown

		if ( self.getElements().isEmpty() ) {
			/*
			self._refContainer.kind_of(atl!VariableDeclaration).if_else({
				type[self] <- type[self._refContainer.type]
			}, {
				t = typ!EmptyCollection.new
						t.multivalued = true
						type[self] <- t
			})
			*/
			attr.linkExprType( typ().newSequenceType( typ().newEmptyCollectionType() ) );
		} else {
			// TODO: Generalize computing the union of all expression elements
			// For the moment just taking the first
			OclExpression representative = self.getElements().get(0);
			attr.linkExprType( typ().newSequenceType( attr.typeOf(representative) ) );
		}		
	}
		
	/* Same as SequenceExp */
	@Override
	public void inSetExp(SetExp self) {
		if ( self.getElements().isEmpty() ) {
			attr.linkExprType( typ().newSetType( typ().newUnknownType() ) );
		} else {
			OclExpression representative = self.getElements().get(0);
			attr.linkExprType( typ().newSetType( attr.typeOf(representative) ) );
		}		
	}
	

	/* Same as Copied from ordered set, using SET as OrderedSet! */
	@Override
	public void inOrderedSetExp(OrderedSetExp self) {
		if ( self.getElements().isEmpty() ) {
			attr.linkExprType( typ().newSetType( typ().newUnknownType() ) );
		} else {
			OclExpression representative = self.getElements().get(0);
			attr.linkExprType( typ().newSetType( attr.typeOf(representative) ) );
		}		
	}

	@Override
	public void inMapExp(MapExp self) {
		if ( self.getElements().size() != 0 ) {
			// throw new UnsupportedOperationException("TODO: Implement map initialization with elements" + self.getLocation());
			List<Type> keys   = new ArrayList<Type>();
			List<Type> values = new ArrayList<Type>();
			for(int i = 0; i < self.getElements().size(); i++) {
				keys.add(attr.typeOf(self.getElements().get(i).getKey()));
				values.add(attr.typeOf(self.getElements().get(i).getValue()));
			}
			
			attr.linkExprType( typ().newMapType( typ().getCommonType(keys), typ().getCommonType(values)) );
		} else {
			attr.linkExprType( typ().newMapType( typ().newUnknownType(), typ().newUnknownType()) );
		}
	}
	
	@Override
	public void inTupleExp(TupleExp self) {
		Type[] attTypes   = new Type[self.getTuplePart().size()];
		String[] attNames = new String[self.getTuplePart().size()];
		
		int i = 0;
		for(TuplePart tp : self.getTuplePart()) {
			// TODO: As with var. declarations, check if type and initializer are compatible...
			if ( tp.getType() != null ) {
				attTypes[i] = attr.typeOf(tp.getType());
			} else {
				attTypes[i] = attr.typeOf(tp.getInitExpression());				
			}
			attNames[i] = tp.getVarName();
			i++;
		}
		attr.linkExprType( typ().newTupleTuple(attNames, attTypes) );
	}
	
	// Begin-of Scopes
	@Override
	public void beforeMatchedRule(MatchedRule self) {
		attr.getVarScope().openScope();
	}
	
	@Override
	public void afterMatchedRule(MatchedRule self) {
		attr.getVarScope().closeScope();
	}
	
	@Override
	public void beforeContextHelper(ContextHelper self) {
		attr.getVarScope().openScope();
		attr.getVarScope().putVariable("self", ATLUtils.getHelperType(self).getInferredType());
	}
	
	public void afterContexHelper(ContextHelper self) {
		attr.getVarScope().closeScope();
	}
	
	public void createIfScope(IfExp self, java.lang.Boolean open) {
		if ( open ) {
			attr.getVarScope().openScope();
		} else {
			attr.getVarScope().closeScope();
		}
		
		/*
		if ( attr.typeOf(self.getCondition()) instanceof BooleanType ) {		
			BooleanType t = (BooleanType) attr.typeOf(self.getCondition());
			if ( t.getKindOfTypes().isEmpty() ) 
				return;
		}
		*/
		
		
		// System.out.println(t);
		//if ( open ) attr.pushScope();
		//else        attr.popScope();
	}

	@Override
	public void beforeIteratorExp(IteratorExp self) {
		attr.getVarScope().openScope();
	}
	
	@Override
	public void afterIteratorExp(IteratorExp self) {
		attr.getVarScope().closeScope();
	}
	

	// End-of Scopes
	
	
	// Generated code and profile support
	
	@Override
	public void inJavaBody(JavaBody self) {
		// There is no inference for Java code, we just use the type
		// declared in the operation as the inferred type
		Operation op = (Operation) self.eContainer();
		attr.linkExprType( attr.typeOf(op.getReturnType()) );
	}
	
	// End-of generated code
	
}
