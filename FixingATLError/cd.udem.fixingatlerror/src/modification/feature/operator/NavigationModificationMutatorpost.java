package modification.feature.operator;

import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFReferenceModel;
import org.eclipse.m2m.atl.engine.parser.AtlParser;
import witness.generator.MetaModel;
import anatlyzer.atlext.ATL.ForEachOutPatternElement;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.InPatternElement;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atlext.OCL.Attribute;
import anatlyzer.atlext.OCL.CollectionType;
import anatlyzer.atlext.OCL.Iterator;
import anatlyzer.atlext.OCL.LoopExp;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.OclModelElement;
import anatlyzer.atlext.OCL.Operation;
import anatlyzer.atlext.OCL.PropertyCallExp;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.atl.model.ATLModel;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import jmetal.core.Solution;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.nsgaII.NSGAIIpostprocessing;

public class NavigationModificationMutatorpost extends AbstractFeatureModificationMutator {
	private String m="C:/Users/varaminz/Downloads/Project/workspaceTEST34/anatlyzer.atl.tests.api/trafo2/zahra2mutants/finalresult.atl";
	 private EMFModel atlModel3;
	private int indexrulespecificinput=-1;
	 private EMFModel loadTransformationModel (String atlTransformationFile) throws ATLCoreException {
			ModelFactory      modelFactory = new EMFModelFactory();
			
			EMFReferenceModel atlMetamodel = (EMFReferenceModel)modelFactory.getBuiltInResource("ATL.ecore");
			
			//EMFReferenceModel atlMetamodel = (EMFReferenceModel)modelFactory.getBuiltInResource("C:\\Users\\wael\\OneDrive\\workspaceATLoperations\\mutants\\anatlyzer.evaluation.mutants\\ATL.ecore");
			AtlParser         atlParser    = new AtlParser();		
			EMFModel          atlModel     = (EMFModel)modelFactory.newModel(atlMetamodel);
			atlParser.inject (atlModel, atlTransformationFile);	
			atlModel.setIsTarget(true);				
			
			return atlModel;
		}
		

	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,ATLModel wrapper, Solution solution
			,CommonFunctionOperators cp) {
		
			return null;
	}

	


	private List<Object> AnalysisNavigationBinding(Solution solution, ATLModel wrapper, MetaModel inputMM,
			MetaModel outputMM, EMFModel atlModel, EDataTypeEList<String> comments, List<Object> ReturnResult,
			List<VariableExp> variables, String outputFolder) {
		
		String feature="type";
		List<InPatternElement> modifiable = (List<InPatternElement>)wrapper.allObjectsOf(InPatternElement.class);
for (int h = 0; h < solution.getCoSolutionpostprocessing().getOp().listpropertyname.size(); h++) {
			
			
			String specificoutput=ReturnSpecificOutput(wrapper, feature, h);
			String specificinput=ReturnSpecificInput(wrapper, feature, h,modifiable);
			
			System.out.println("index9");
			System.out.println(NSGAIIpostprocessing.errorrule);
			System.out.println(this.indexrulespecificinput);
			if(NSGAIIpostprocessing.errorrule.contains(this.indexrulespecificinput)) {
				System.out.println("out");
				System.out.println(specificoutput);
				System.out.println(specificinput);
				for (int h2 = 0; h2 < solution.getCoSolutionpostprocessing().getOp().listpropertyname.get(h).size(); h2++) {
				//	
					
					String typhelper=IfRightHandSideISHelperReturnHelperType2( wrapper, h, h2);
					String usingcondition=null;
					if(typhelper==null)
				     usingcondition=IfRightHandSideIsUsingCondition(wrapper,h,h2);
					String extractedtypemeta=null;
					if(typhelper==null && usingcondition==null)
					 extractedtypemeta=ExtractTypeRightHandSideIfThereisnotHelper( h, h2,  specificinput);
					
					ReturnResult=IfRightHandSideBindingIsBooleanType(typhelper,usingcondition,extractedtypemeta
							,
							solution,  wrapper
							,  inputMM,outputMM,  atlModel, comments,
							 ReturnResult,  modifiable,  outputFolder,variables , h, h2,specificoutput, specificinput);
					
				
				
				}
		
}
		
	}		
		
		
		
		// TODO Auto-generated method stub
		return ReturnResult;
	}

	private String ReturnTypeLeftHandSideBinding(String specificoutput, String leftsidebinding) {
			return null;
	}

	private List<Object> IfRightHandSideBindingIsBooleanType(String typhelper, String usingcondition,
			String extractedtypemeta, Solution solution, ATLModel wrapper, MetaModel inputMM, MetaModel outputMM,
			EMFModel atlModel, EDataTypeEList<String> comments, List<Object> ReturnResult,
			List<InPatternElement> modifiable, String outputFolder, List<VariableExp> variables, int h, int h2, String specificoutput, String specificinput) {
		
		System.out.println("typright");
		System.out.println(typhelper);
		System.out.println(usingcondition);
		System.out.println(extractedtypemeta);
		if(typhelper!=null) {
			
			System.out.println(typhelper);
			
			if(typhelper.equals("BooleanType")) {
				String typeleftside=ReturnTypeLeftHandSideBinding(specificoutput,solution.getCoSolutionpostprocessing().getOp().listpropertyname.get(h).get(h2));
				ReturnResult=ChooseAttributeAndModifyNavigation(typeleftside,specificinput,wrapper,h,h2,solution
						, solution.getCoSolutionpostprocessing().getOp().listpropertyname.get(h).get(h2),
						inputMM,  outputMM,  atlModel, comments,
						 ReturnResult,  modifiable,  variables);
				
				System.out.println(typhelper);
			}
		}
		
		if(usingcondition!=null) {
			
			System.out.println(usingcondition);
			
			if(usingcondition.equals("EBoolean") || usingcondition.equals("Boolean")) {
				String typeleftside=ReturnTypeLeftHandSideBinding(specificoutput,solution.getCoSolutionpostprocessing().getOp().listpropertyname.get(h).get(h2));
				ReturnResult=ChooseAttributeAndModifyNavigation(typeleftside,specificinput,wrapper,h,h2,solution
						, solution.getCoSolutionpostprocessing().getOp().listpropertyname.get(h).get(h2),
						inputMM,  outputMM,  atlModel, comments,
						 ReturnResult,  modifiable,  variables);
				
				System.out.println(usingcondition);
			}
		}
		
		
		if(extractedtypemeta!=null) {
			
			System.out.println(extractedtypemeta);
			
			if(extractedtypemeta.equals("EBoolean") || extractedtypemeta.equals("Boolean")) {
				String typeleftside=ReturnTypeLeftHandSideBinding(specificoutput,solution.getCoSolutionpostprocessing().getOp().listpropertyname.get(h).get(h2));
				
				ReturnResult=ChooseAttributeAndModifyNavigation(typeleftside,specificinput,wrapper,h,h2,solution
						, solution.getCoSolutionpostprocessing().getOp().listpropertyname.get(h).get(h2),
						inputMM,  outputMM,  atlModel, comments,
						 ReturnResult,  modifiable,  variables);
				
				System.out.println(extractedtypemeta);
				System.out.println("boolean3");
		
			}
		}
		// TODO Auto-generated method stub
		return ReturnResult;
	}

	private List<Object> ChooseAttributeAndModifyNavigation(String typeleftside, String specificinput, ATLModel wrapper,
			int h, int h2, Solution solution, String string, MetaModel inputMM, MetaModel outputMM, EMFModel atlModel,
			EDataTypeEList<String> comments, List<Object> ReturnResult, List<InPatternElement> modifiable,
			List<VariableExp> variables) {
		// TODO Auto-generated method stub
		if(!typeleftside.equals("EBoolean") && !typeleftside.equals("Boolean")) {
			
			List<String> allattrleft =ExtractwholeAttrRightSide(specificinput,wrapper,h,h2,solution);
			int randomInt=ChooseRandomnotBooleanattrType( allattrleft, solution.getCoSolutionpostprocessing().getOp().listpropertyname.get(h).get(h2), h, h2);
			ReturnResult=ModifyNavigation( solution,  wrapper
					,  inputMM,  outputMM,  atlModel, comments,
					 ReturnResult,  modifiable,  variables, solution.getCoSolutionpostprocessing().getOp().listpropertynamelocation.get(h).get(h2),  allattrleft.get( randomInt ), h, h2);
		
			System.out.println(allattrleft);
		}
		return ReturnResult;
	}


	private List<Object> ModifyNavigation(Solution solution, ATLModel wrapper, MetaModel inputMM, MetaModel outputMM,
			EMFModel atlModel, EDataTypeEList<String> comments, List<Object> ReturnResult,
			List<InPatternElement> modifiable, List<VariableExp> variables, Integer location, String candidatenavigation, int h,
			int h2) {
		// TODO Auto-generated method stub
		
		
		for(int i=0; i<variables.size();i++) {
		String[] array = variables.get(i).getLocation().split(":", 2);
		EObject navigationExpression = variables.get(i).eContainer();
		
		
		if (navigationExpression instanceof NavigationOrAttributeCallExp && 
				(Integer.parseInt(array[0])==location) ) {
			
			EStructuralFeature featureDefinition = wrapper.source(navigationExpression).eClass().getEStructuralFeature("name");
			Object object2modify_src2 = wrapper.source(navigationExpression).eGet(featureDefinition);
			String navigation = object2modify_src2.toString();
			List<Object> replacements = this.replacementsnavigation(atlModel, null,null, navigation, inputMM);
			
			int index=-1;
			for(int j=0;j<replacements.size();j++) {
				System.out.println((replacements.get(j)).toString());
				if((replacements.get(j)).toString().equals(candidatenavigation) ) {
					index=j;
					
				}
				
			}
			System.out.println("oldnavigation");
			System.out.println(navigation);
			System.out.println(replacements.get(index));
			
			
			if(!navigation.equals( replacements.get(index).toString())) {
			wrapper.source(navigationExpression).eSet(featureDefinition, replacements.get(index));
			//StoreTwoIndex(randomInt, randomInt2, -2);
			System.out.println("\n-- MUTATION \"" + this.getDescription() + "\" from " + toString(navigationExpression, variables.get(i)) +
					navigation + " to " + toString(navigationExpression, variables.get(i)) + replacements.get(index)   +
					" (line " + ((LocatedElement)navigationExpression).getLocation() + " of original transformation)\n");
			comments.add("\n-- MUTATION \"" + this.getDescription() + "\" from " + toString(navigationExpression, variables.get(i)) +
					navigation + " to " + toString(navigationExpression, variables.get(i)) + replacements.get(index)   +
					" (line " + ((LocatedElement)navigationExpression).getLocation() + " of original transformation)\n");
			String comment = "\n-- MUTATION \"" + this.getDescription() + "\" from " + toString(navigationExpression, variables.get(i)) +
					navigation + " to " + toString(navigationExpression, variables.get(i)) + replacements.get(index)   +
					" (line " + ((LocatedElement)navigationExpression).getLocation() + " of original transformation)\n";
			NSGAII.numop = NSGAII.numop + 1;
			
			ReturnResult.set(0, wrapper);
			ReturnResult.set(1, atlModel);
			ReturnResult.add(comment);
			
		}
		}
		
		
		}
		
		return ReturnResult;
	}


	private int ChooseRandomnotBooleanattrType(List<String> allattrleft, String leftsideattribute, int row, int column) {
		
		
		int indexattr=0;
		//List<Integer> indexattrwithabsresult = new ArrayList<Integer>();
		//indexattrwithabsresult.add(indexattr );
		//indexattrwithabsresult.add(Math.abs(allattrleft.get(indexattr).compareTo(  
		//		leftsideattribute)	) );
		System.out.println("attlist");
		//for(int i=0;i<allattrleft.size();i++) {
			// j=j+2 choon 2 ta 2 ta bayad bere jolo avali hamishe attribute hast dovomi hamishe type attribute hast
			for(int j=0;j<allattrleft.size();j=j+2) {
				if(!allattrleft.get(j+1).equals("Boolean")
						&& !allattrleft.get(j+1).equals("EBoolean")
						) {
				if(Math.abs(allattrleft.get(j).compareTo(
						leftsideattribute) )<
						Math.abs(allattrleft.get(indexattr).compareTo(  
								leftsideattribute)	)) {
					indexattr=j;
				//	indexattrwithabsresult.set(0,indexattr );
				//	indexattrwithabsresult.set(1,Math.abs(allattrleft.get(indexattr).compareTo(  
				//			leftsideattribute)	) );
				
					System.out.println(allattrleft.get(j).compareTo(leftsideattribute));
					System.out.println(allattrleft.get(j));
					
					
			}
			}
			}
		//}
		return indexattr;
	}

	private List<String> ExtractwholeAttrRightSide(String specificinput,ATLModel wrapper, int h, int h2, Solution solution) {
				return null;
	}


	private String ExtractTypeRightHandSideIfThereisnotHelper(int h, int h2, String specificinput) {
			return null;
	}

	private String IfRightHandSideIsUsingCondition(ATLModel wrapper, int h, int h2) {
		// TODO Auto-generated method stub
			return null;
	}

	private String IfRightHandSideISHelperReturnHelperType2(ATLModel wrapper, int h, int h2) {
			return null;
		
		
	}

	private String ReturnHelpetType2(ATLModel wrapper, List<Helper> helper, int h, int h2, int i, int j) {
		// TODO Auto-generated method stub
		String helper_return  = helper.get(i).getDefinition().getFeature() instanceof Operation? 
                toString(((Operation)helper.get(i).getDefinition().getFeature()).getReturnType()):
                toString(((Attribute)helper.get(i).getDefinition().getFeature()).getType());
                
        System.out.println(helper_return); 
        
        	
        		
         
		return helper_return;
        	
	}
	private String ReturnSpecificInput(ATLModel wrapper, String featureName, int h, List<InPatternElement> modifiable) {
		// TODO Auto-generated method stub
		int indexrule = -1;
		List<OutPatternElement> variables = (List<OutPatternElement>)wrapper.allObjectsOf(OutPatternElement.class);
		String[] array = variables.get(h).getLocation().split(":", 2);
		for (int j = 0; j < NSGAIIpostprocessing.faultrule.size(); j++) {
			if (Integer.parseInt(array[0]) > NSGAIIpostprocessing.faultrule.get(j)
					&& Integer.parseInt(array[0]) < NSGAIIpostprocessing.finalrule.get(j))

				indexrule = j;

		}
		this.indexrulespecificinput=indexrule;
		EStructuralFeature featureDefinition = wrapper.source(modifiable.get(indexrule)).eClass()
						.getEStructuralFeature(featureName);
		EObject object2modify_src = wrapper.source(modifiable.get(indexrule));
		EObject oldFeatureValue = (EObject) object2modify_src.eGet(featureDefinition);
		
		return toString(oldFeatureValue);

	}


	private String ReturnSpecificOutput(ATLModel wrapper, String featureName, int h) {
		// TODO Auto-generated method stub
		List<OutPatternElement> variables = (List<OutPatternElement>)wrapper.allObjectsOf(OutPatternElement.class);
		EStructuralFeature featureDefinitionout = wrapper.source(variables.get(h)).eClass()
				.getEStructuralFeature(featureName);
		EObject object2modify_src2 = wrapper.source(variables.get(h));
		EObject oldFeatureValue2 = (EObject) object2modify_src2.eGet(featureDefinitionout);
		return toString(oldFeatureValue2);
		
	}


	private List<Object> OperationPreviousGenerationModefyBindingNavigation(int randomInt, Solution solution,
			EMFModel atlModel, MetaModel inputMM, List<VariableExp> variables, ATLModel wrapper, Object object,
			EDataTypeEList<String> comments, List<Object> ReturnResult, MetaModel outputMM) {
			return ReturnResult;
	}
	private void setvariable(int randomInt, int randomInt2, int randomInt3) {
		// TODO Auto-generated method stub

			}

	private List<Object> replacementsnavigation(EMFModel atlModel, EClass oldFeatureValue, String type,
			String navigation, MetaModel inputMM) {
			return null;
	}


	@Override
	public String getDescription() {
		return "Navigation Modification";
	}

	/**
	 * @param containee
	 * @param container
	 */
	private String toString (EObject container, EObject containee) {
		EObject next   = containee;
		String  string = "";
		do {
			string += toString(next) + ".";
			next    = next.eContainer(); 
		} while (next!=container && next!=null);
		return string;
	}

	/**
	 * It navigates from the variable "containee" to the navigation expression "container", and returns the type of "container".
	 * @param container
	 * @param containee
	 * @param inputMM
	 * @param outputMM
	 * @return
	 */
	private String getType (EObject container, VariableExp containee, MetaModel inputMM, MetaModel outputMM) {
		EClassifier         c   = null;
		VariableDeclaration def = containee.getReferredVariable();
		
		// obtain type (classifier) of variable expression ..............................
		// case 1 -> in pattern element
		if(def!=null) {
		if (def instanceof InPatternElement) {
			System.out.println("1111111111111");
			c = inputMM.getEClassifier(def.getType().getName());
			System.out.println(c);
		}
		// case 2 -> for each out pattern element
		else if (def instanceof ForEachOutPatternElement) {
			System.out.println("2222222222222");
			def = ((ForEachOutPatternElement)def).getIterator();
			if (def.eContainer() instanceof OutPatternElement) {
				OutPatternElement element = (OutPatternElement)def.eContainer();
				if (element.getType() instanceof OclModelElement)
					c = outputMM.getEClassifier(((OclModelElement)element.getType()).getName());
			}
		}
		// case 3 -> iterator
		else if (def instanceof Iterator) {
			System.out.println("3333333333333");
			if (def.eContainer() instanceof LoopExp) {
				LoopExp  iterator = (LoopExp)def.eContainer();
				OclExpression exp = iterator.getSource();
				while (c==null && exp!=null) {
					if (exp instanceof OclModelElement)  {
						c   = inputMM.getEClassifier(((OclModelElement)exp).getName());
						exp = null;
					}
					else if (exp instanceof PropertyCallExp) {
						exp = ((PropertyCallExp)exp).getSource();
					}
					else if (exp instanceof VariableExp) {
						c = inputMM.getEClassifier(getType(container, (VariableExp)exp, inputMM, outputMM));
						exp = null;
					}
					else exp = null;
				}
			}
			System.out.println(c);
		}
		// case 4 -> variable declaration
		else {
			if (toString(def).equals("self")) {
				System.out.println("4444444444");
				EObject helper = containee;
				while (helper!=null && !(helper instanceof Helper)) helper = helper.eContainer();
				if (helper instanceof Helper) {
					if (((Helper)helper).getDefinition().getContext_()!=null &&
						((Helper)helper).getDefinition().getContext_().getContext_()!=null &&
					    ((Helper)helper).getDefinition().getContext_().getContext_() instanceof OclModelElement)
						c = inputMM.getEClassifier(((OclModelElement)((Helper)helper).getDefinition().getContext_().getContext_()).getName());
				}
			}
			else if (((VariableDeclaration)def).getType() instanceof OclModelElement) {
				System.out.println("5555555555");
				c = inputMM.getEClassifier(((VariableDeclaration)def).getType().getName());
			}
			else if (((VariableDeclaration)def).getType() instanceof CollectionType) {
				System.out.println("6666666666");
				c = inputMM.getEClassifier( ((CollectionType)((VariableDeclaration)def).getType()).getElementType().getName());
			}
		}
			
		// obtain type (classifier) of container ........................................
		EObject  next = containee.eContainer();
		while (c!=null && next!=null && next!=container) {
			
			if (c instanceof EClass) {
				EStructuralFeature name      = next.eClass().getEStructuralFeature("name");
				EStructuralFeature feature   = null;
				
				if ( name != null ) {
					String             nameValue = next.eGet(name).toString();
					feature = ((EClass)c).getEStructuralFeature(nameValue);
				} else {
					System.out.println("Warning: " + next.eClass().getName() + " " + "with null name feature ");
				}
				if (feature!=null) {
					c    = feature.getEType();
					next = next.eContainer();
				}
				else next=null;
			}
		}
	}
				return c!=null? c.getName() : null;
		
	}


	@Override
	protected List<Object> replacements(EMFModel atlModel, EClass oldFeatureValue, EObject object2modify,
			String currentAttributeValue, MetaModel metamodel) {
		// TODO Auto-generated method stub
		
		System.out.println("replacement navigation");
		return null;
	}



	


	

	
	
}
