package creation.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import transML.utils.modeling.EMFUtils;
import witness.generator.MetaModel;
import anatlyzer.atlext.ATL.ATLFactory;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.LazyRule;
import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.atlext.OCL.Attribute;
import anatlyzer.atlext.OCL.BooleanExp;
import anatlyzer.atlext.OCL.IntegerExp;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.Operation;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.atlext.OCL.RealExp;
import anatlyzer.atlext.OCL.StringExp;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.atl.model.ATLModel;
import evaluation.mutator.AbstractMutator;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.IntSolutionTypeThreeStep;
import jmetal.metaheuristics.nsgaII.NSGAIIThreeStep;
import jmetal.problems.MyProblemThreeStep;

public class BindingCreationMutatorThreeStep extends AbstractMutator {

	private String comment = null;
	private boolean checkmutationapply = false;
	private ArrayList<Integer> numOutpattern = new ArrayList<Integer>();
	EDataTypeEList<String> comments ;
	private boolean getcheckmutationapply() {
		return this.checkmutationapply;
	}

	private void setcheckmutationapply(boolean a) {
		this.checkmutationapply=a;
	}
	private ArrayList<Integer> getnumOutpattern() {
		return this.numOutpattern;
	}

	private void setnumOutpattern(int a) {
		this.numOutpattern.add(a);
	}

	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,
			ATLModel wrapper, Solution solution,CommonFunctionOperators cp) {
		
		CreationFunctions cf=new CreationFunctions(wrapper);
		List<Object> ReturnResult = new ArrayList<Object>();
		List<Rule> modifiable = (List<Rule>) wrapper.allObjectsOf(Rule.class);
		ReturnResult.add(wrapper);
		ReturnResult.add(atlModel);
		this.comments = cp.ReturnInitialComments(wrapper);
		int randomInt = -2;
		ArrayList<Integer> candidaterandomInt = new ArrayList<Integer>();
		while (this.getcheckmutationapply() == false) {

			updatevariables();
			// choose one rule randomly
			List<Integer> Result = cp.ReturnFirstIndex(randomInt, modifiable.size(), this.getcheckmutationapply(), solution,null,null);
			randomInt = Result.get(0);
			candidaterandomInt=AddNewrandomInttoList(candidaterandomInt,randomInt );
			if (solution.getpreviousgeneration()) {  // for crossover
				
				ReturnResult = OperationPreviousGenerationDeletion(randomInt, solution, atlModel, modifiable, wrapper,
						this.comments, ReturnResult, outputMM,inputMM,cp);
				this.setcheckmutationapply(true);
			}
			// for initial population and mutation
			else if (!solution.getpreviousgeneration()) {
				
				List<? extends VariableDeclaration> ivariables = getVariableDeclarations(modifiable.get(randomInt));
				//choose outpattern
				int randomInt2 = -2;
				randomInt2 = cp.FindSecondIndex(randomInt2,
						modifiable.get(randomInt).getOutPattern().getElements().size());
				// find the specific line
				String[] array = modifiable.get(randomInt).getOutPattern().getElements().get(randomInt2).getLocation()
						.split(":", 2);
				int indexrule = cp.FindRule(array);

				if (ivariables.size() > 0) {
							
					String[] array2 = modifiable.get(randomInt).getOutPattern().getElements().get(randomInt2)
							.getLocation().split(":", 2);
					if (!this.getnumOutpattern().contains(Integer.parseInt(array2[0])))
						this.setnumOutpattern(Integer.parseInt(array2[0]));
					
						List<Binding> realbindings = cf.getExistingBinding(modifiable, randomInt,randomInt2);			
						int row = 0;
						row=cp.FindLeftSidebindingsSpecificOutpattern(solution,array2);			
						String outpattern=cf.findOutpattern(wrapper,row);
						ReturnResult=checkIfThereisCompatiblerightAttribute(realbindings,outpattern, outputMM, modifiable,randomInt
								,randomInt2,solution, ivariables, row, indexrule, wrapper,ReturnResult,atlModel,inputMM,cp,array2);
					
				}

			}
			TerminateCreation(candidaterandomInt, modifiable);
			 
		}

		return ReturnResult;
	}

	private ArrayList<Integer> AddNewrandomInttoList(ArrayList<Integer> candidaterandomInt, int randomInt) {
		
		if(!candidaterandomInt.contains( randomInt))
			candidaterandomInt.add(randomInt );
		return candidaterandomInt;
	}

	private void TerminateCreation(ArrayList<Integer> candidaterandomInt, List<Rule> modifiable) {
	
		if(candidaterandomInt.size()==modifiable.size())
			checkmutationapply = true;
	}

	private void updatevariables() {
		
		NSGAIIThreeStep.mandatoryoutpatternoperation.clear();
		NSGAIIThreeStep.mandatoryoutpatternoperation = new ArrayList<String>();
		NSGAIIThreeStep.mandatoryoutpatternlocation.clear();
		NSGAIIThreeStep.mandatoryoutpatternlocation = new ArrayList<Integer>();
		
	}

	private List<Object> checkIfThereisCompatiblerightAttribute(List<Binding> realbindings, String outpattern,
			MetaModel outputMM, List<Rule> modifiable, int randomInt, int randomInt2, Solution solution,
			List<? extends VariableDeclaration> ivariables, int row, int indexrule, ATLModel wrapper, List<Object> ReturnResult, EMFModel atlModel, MetaModel inputMM
			, CommonFunctionOperators cp, String[] array2) {
			
			List<Binding> newbindings = new ArrayList<Binding>();
			// this.getBinding6: create new binding
			newbindings.add(this.getBinding6(
				       outpattern, outputMM, modifiable.get(randomInt).getOutPattern()
							.getElements().get(randomInt2).getBindings(),
					solution, ivariables, row, indexrule, wrapper,inputMM, cp,array2)); // binding for property of subclass
																		// (correct value)

			// if number of checked outpattern is equal to number of out pattern in
			// transformation (listpropertynamelocation) then this operation must be
			// finished
			if (this.getnumOutpattern()
					.size() == solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.size()
							- 1)
				this.setcheckmutationapply(true);
			// if already not created this new binding 
			
			ReturnResult=createNewbinding(newbindings,realbindings,solution,row,randomInt, randomInt2,modifiable
					,wrapper,ReturnResult,atlModel);
			
		return ReturnResult;
	}

	private List<Object> createNewbinding(List<Binding> newbindings, List<Binding> realbindings, Solution solution,
			int row, int randomInt, int randomInt2, List<Rule> modifiable, ATLModel wrapper, List<Object> ReturnResult, EMFModel atlModel) {
		
		CommonFunctionOperators cp=new CommonFunctionOperators();
		if (newbindings.get(0) != null) {
			for (Binding binding : newbindings) {
				CreationFunctions cf=new CreationFunctions(wrapper);
				boolean checkforcreate = false;
				checkforcreate =cp.checkIfAlreadyCreatedbinding(realbindings,binding,solution,row);
				
				// if this is new binding that already not created
				if (binding != null && checkforcreate == false && (NSGAIIThreeStep.mandatoryoutpatternoperation.size()==0 || 
						MyProblemThreeStep.indexoperation!=IntSolutionTypeThreeStep.max_operations_size) ) {
					
					UpdateSolutionCreation(solution, binding, row,randomInt, randomInt2, cp );	
					realbindings.add(binding);
					this.comments.add(AddNewCommentsOnTransformation(binding, modifiable, randomInt, randomInt2 ));
					ClearMandatoryOeration();
					ReturnResult=cf.applyChange(randomInt, modifiable
							.get(randomInt).getOutPattern().getElements().get(randomInt2), ReturnResult, randomInt2, comment, wrapper, atlModel,toString(binding));
					this.setcheckmutationapply(true);

				}

			}
		}

		return ReturnResult;
	}

	private void UpdateSolutionCreation(Solution solution, Binding binding, int row, int randomInt, int randomInt2, CommonFunctionOperators cp) {
		
		solution.newbindings.set(MyProblemThreeStep.indexoperation - 1, binding);
		solution.getCoSolutionThreeStep().getOp().originalwrapper.get(row).add(1);
		cp.StoreTwoIndex(randomInt, randomInt2);
		
	}

	private void ClearMandatoryOeration() {
		
		NSGAIIThreeStep.mandatorycreationoperation.clear();
		NSGAIIThreeStep.mandatorycreationlocation.clear();
	}

	private String AddNewCommentsOnTransformation(Binding binding, List<Rule> modifiable, int randomInt, int randomInt2) {
	 return	"\n-- MUTATION \"" + this.getDescription() + "\" "
				+ toString(binding) + " in "
				+ toString(modifiable
						.get(randomInt).getOutPattern().getElements().get(randomInt2))
				+ " (line "
				+ modifiable.get(randomInt).getOutPattern().getElements()
						.get(randomInt2).getLocation()
				+ " of original transformation)\n";
		
	}
	private String AddNewCommentsOnTransformationCrossover(String str, Rule modifiable2, int randomInt2) {
		
		return "\n-- MUTATION \"" + this.getDescription() + "\" " + str + " in "
				+ toString(modifiable2.getOutPattern().getElements().get(randomInt2)) + " (line "
				+ modifiable2.getOutPattern().getElements().get(randomInt2).getLocation()
				+ " of original transformation)\n";
		
	}
	

	private List<Object> OperationPreviousGenerationDeletion(int randomInt, Solution solution, EMFModel atlModel,
			List<Rule> modifiable, ATLModel wrapper, EDataTypeEList<String> comments, List<Object> ReturnResult,
			MetaModel outputMM, MetaModel inputMM,CommonFunctionOperators cp) {
		
		CreationFunctions cf=new CreationFunctions(wrapper);
		String comment = null;
		Rule modifiable2 = modifiable.get(randomInt);
		int randomInt2 = -2; // which outpattern
		randomInt2 = cp.FindSecondIndex(randomInt2, modifiable2.getOutPattern().getElements().size());
		String[] array2 = modifiable2.getOutPattern().getElements().get(randomInt2).getLocation().split(":", 2);
		int row = cp.FindLeftSidebindingsSpecificOutpattern(solution, array2);
		EStructuralFeature feature = wrapper.source(modifiable2.getOutPattern().getElements().get(randomInt2)).eClass()
				.getEStructuralFeature("bindings");
		List<Binding> realbindings = (List<Binding>) wrapper
				.source(modifiable2.getOutPattern().getElements().get(randomInt2)).eGet(feature);
		String str = solution.newstring.get(MyProblemThreeStep.indexoperation - 1);
		Binding b2 = ATLFactory.eINSTANCE.createBinding();
		b2.setPropertyName(str);		
		// find lHS attr in output metamodel
		EStructuralFeature newfeature = findlhsattributmetamodel(outputMM,str); 
		List<? extends VariableDeclaration> ivariables = getVariableDeclarations(modifiable2);
		if(ivariables.size()==0)
			ivariables = getVariableDeclarationsLazy(modifiable2);
			b2.setValue(getCompatibleValue(newfeature  ,
					ivariables, wrapper, randomInt,inputMM,cp));
		boolean checkforcreate = false;
		checkforcreate =cp.checkIfAlreadyCreatedbinding(realbindings,b2,solution,row);

	
		if (!checkforcreate) {
			
			EObject oldFeatureValue = cp.FindSpecificOutpattern(row, wrapper);// outpattern in the rule
			updateArrayNextOperation(oldFeatureValue,solution,row);
			
			if(NSGAIIThreeStep.mandatoryoutpatternoperation.size()==0 || 
					MyProblemThreeStep.indexoperation!=IntSolutionTypeThreeStep.max_operations_size) {
			// next operation is outpattern
			updatelocationArrayNextoutpattern(cp,atlModel,oldFeatureValue,outputMM,solution);	
			realbindings.add(b2);	
			updateSolutionCrossover(row,str,solution,newfeature,randomInt,array2);
			cp.StoreTwoIndex(randomInt, randomInt2);
			comments.add(AddNewCommentsOnTransformationCrossover(str, modifiable2, randomInt2 ));
			ClearMandatoryOeration();
			ReturnResult=cf.applyChange(randomInt, modifiable
					.get(randomInt).getOutPattern().getElements().get(randomInt2), ReturnResult, randomInt2, comment, wrapper, atlModel,str);
			solution.newbindings.set(MyProblemThreeStep.indexoperation - 1, b2);
			}
			IfnextOperationIsOutpattern(solution, cp);
				
		} else {
			
			notApplyCreatingBindingOperation(solution,cp);
			

		}

	
		return ReturnResult;
	}

	

	private void IfnextOperationIsOutpattern(Solution solution, CommonFunctionOperators cp) {
		
		if(NSGAIIThreeStep.mandatoryoutpatternoperation.size()>0 &&
				MyProblemThreeStep.indexoperation==IntSolutionTypeThreeStep.max_operations_size){
			NSGAIIThreeStep.mandatoryoutpatternoperation.clear();
			notApplyCreatingBindingOperation(solution,cp);
		}
		
	}

	private EStructuralFeature findlhsattributmetamodel(MetaModel outputMM, String str) {
	
		EStructuralFeature newfeature =null;
		for (EClassifier classifier : outputMM.getEClassifiers()) {
			if (classifier instanceof EClass) {
				EClass child = ((EClass) classifier);
				for (int i = 0; i < child.getEStructuralFeatures().size(); i++) {
					if (child.getEStructuralFeatures().get(i).getName().equals(str)) {
						newfeature = child.getEStructuralFeatures().get(i);
						break;
					}
				}
			}
		}
		return newfeature;
	}

	private void updateSolutionCrossover(int row, String str, Solution solution, EStructuralFeature newfeature, int indexrule, String[] array2) {
		
		solution.getCoSolutionThreeStep().getOp().listpropertyname.get(row).add(str);
		solution.getCoSolutionThreeStep().getOp().listpropertytype.get(row).add(newfeature.getName());
		
		if(solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(row).size()>0)
			solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(row)
					.add(solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(row)
							.get(solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(row).size() - 1)
							+ 1);
			else
				solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(row)
					.add(Integer.parseInt(array2[0])+1);
		
		solution.inorout.set(MyProblemThreeStep.indexoperation - 1, "out");
		solution.getCoSolutionThreeStep().getOp().originalwrapper.get(row).add(1);
	}

	private void updatelocationArrayNextoutpattern(CommonFunctionOperators cp, EMFModel atlModel,
			EObject oldFeatureValue, MetaModel outputMM, Solution solution) {
	
		if(NSGAIIThreeStep.mandatoryoutpatternoperation.size()>0)
		{
			List<EObject> replacements = cp.replacements(atlModel, (EObject) oldFeatureValue,
					outputMM);// all outpattern class
			int id=0;
			for(EObject replace : replacements) { // replacement: list outpattern
				if(toString(replace).equals( solution.modifypropertyname.get( MyProblemThreeStep.indexoperation - 1))) {
					NSGAIIThreeStep.mandatoryoutpatternlocation.add(id);
					
				}
				id++;
			}

		}

		
	}

	private void updateArrayNextOperation(EObject oldFeatureValue, Solution solution, int row) {
		
		if(!toString(oldFeatureValue).equals( solution.modifypropertyname.get( MyProblemThreeStep.indexoperation - 1)))
		{
			NSGAIIThreeStep.mandatoryoutpatternoperation.add(solution.modifypropertyname.get( MyProblemThreeStep.indexoperation - 1) );
			NSGAIIThreeStep.mandatoryoutpatternlocation.add(row);
		}
	}

	private void notApplyCreatingBindingOperation(Solution solution, CommonFunctionOperators cp) {

		solution.newbindings.set(MyProblemThreeStep.indexoperation - 1, null);
		solution.inorout.set(MyProblemThreeStep.indexoperation - 1, "empty");
		cp.StoreTwoIndex(-2, -2);
	}

	private OclExpression createNewExpressionhelper2(ATLModel wrapper, int randomInt2,
			List<? extends VariableDeclaration> variables) {
		
				OclExpression expression =OCLFactory.eINSTANCE.createVariableExp();
				OperationCallExp nc2 = OCLFactory.eINSTANCE.createOperationCallExp();
				nc2.setIsStaticCall(false);
				nc2.setImplicitlyCasted(false);
				List<anatlyzer.atlext.ATL.Helper> helper = (List<Helper>) wrapper
						.allObjectsOf(anatlyzer.atlext.ATL.Helper.class);
				String helper_name2=null ;
				for (int i = 0; i < helper.size(); i++) {
					String helper_type =helper.get(i).getDefinition().getFeature() instanceof Operation
					? toString(((Operation) helper.get(i).getDefinition().getFeature()).getReturnType())
					: toString(((Attribute) helper.get(i).getDefinition().getFeature()).getType());
				
						if(!helper_type.equals("BooleanType")
								&& !helper_type.equals("StringType")) {
						}		
							if(helper_type.equals("StringType"))
								helper_name2 = helper.get(i).getDefinition().getFeature() instanceof Operation
								? ((Operation) helper.get(i).getDefinition().getFeature()).getName()
								: ((Attribute) helper.get(i).getDefinition().getFeature()).getName();
							
				}
				
				nc2.setOperationName(helper_name2);
				VariableDeclaration vDec= OCLFactory.eINSTANCE.createVariableDeclaration();
				String str = variables.get(0).getVarName();
				vDec.setVarName(str);
				VariableExp v2= OCLFactory.eINSTANCE.createVariableExp();
				v2.setReferredVariable(variables.get(0));
				nc2.setSource(v2); 
				nc2.setInitializedVariable(vDec);
				(expression) = (vDec.getInitExpression());
				
		return expression;
	}
		
	
		
	private OclExpression createNewExpressionhelper(ATLModel wrapper, int randomInt2,
			List<? extends VariableDeclaration> variables) {
				
				List<anatlyzer.atlext.ATL.Helper> helper = (List<Helper>) wrapper
						.allObjectsOf(anatlyzer.atlext.ATL.Helper.class);
				OclExpression expression =OCLFactory.eINSTANCE.createVariableExp();
				String helper_name=null ;
				String helper_name2=null ;
				for (int i = 0; i < helper.size(); i++) {
					String helper_type =helper.get(i).getDefinition().getFeature() instanceof Operation
					? toString(((Operation) helper.get(i).getDefinition().getFeature()).getReturnType())
					: toString(((Attribute) helper.get(i).getDefinition().getFeature()).getType());
				
						if(!helper_type.equals("BooleanType")
								&& !helper_type.equals("StringType"))
							helper_name = helper.get(i).getDefinition().getFeature() instanceof Operation
							? ((Operation) helper.get(i).getDefinition().getFeature()).getName()
							: ((Attribute) helper.get(i).getDefinition().getFeature()).getName();
							
							if(helper_type.equals("StringType"))
								helper_name2 = helper.get(i).getDefinition().getFeature() instanceof Operation
								? ((Operation) helper.get(i).getDefinition().getFeature()).getName()
								: ((Attribute) helper.get(i).getDefinition().getFeature()).getName();
							

				}
				if(helper.size()>0)
				{
					expression=GenerateHelperExpression(helper_name, helper_name2, expression);
					
				}
		return expression;
	}
	
	private OclExpression GenerateHelperExpression(String helper_name, String helper_name2, OclExpression expression) {
		
				OperationCallExp nc = OCLFactory.eINSTANCE.createOperationCallExp();
				NavigationOrAttributeCallExp nc2 = OCLFactory.eINSTANCE.createNavigationOrAttributeCallExp();
				nc.setIsStaticCall(false);
				nc.setImplicitlyCasted(false);
				nc.setOperationName(helper_name);
				nc2.setName(helper_name2);
				VariableExp v= OCLFactory.eINSTANCE.createVariableExp();
				VariableDeclaration vDec= OCLFactory.eINSTANCE.createVariableDeclaration();
				v.setReferredVariable(vDec);
				vDec.setVarName("thisModule");
				VariableDeclaration vDec2= OCLFactory.eINSTANCE.createVariableDeclaration();
				VariableExp v2= OCLFactory.eINSTANCE.createVariableExp();
				v2.setReferredVariable(vDec2);
				vDec2.setVarName("thisModule");
				nc2.setSource(v2);		
				nc.setSource(v);
				nc.setInitializedVariable(vDec);
				nc2.setInitializedVariable(vDec2);
				nc.getArguments().add(nc2); // add thisModule.defaultType as argument to thisModule.objectIdType
				(expression) = (vDec.getInitExpression());
				return expression;
	}

	private OclExpression createNewExpression(List<EStructuralFeature> maintarget, int randomInt2,
			List<? extends VariableDeclaration> variables) {
	
				OclExpression expression =OCLFactory.eINSTANCE.createVariableExp();
				NavigationOrAttributeCallExp nc = OCLFactory.eINSTANCE.createNavigationOrAttributeCallExp();
				nc.setIsStaticCall(false);
				nc.setImplicitlyCasted(false);
				nc.setName(maintarget.get(randomInt2).getName());
				VariableDeclaration sel = OCLFactory.eINSTANCE.createVariableDeclaration();
				String str = variables.get(0).getVarName();
				sel.setVarName(str);
				VariableExp vExp = OCLFactory.eINSTANCE.createVariableExp();
				vExp.setReferredVariable(variables.get(0));
				nc.setSource(vExp);
				nc.setInitializedVariable(sel);
				(expression) = (sel.getInitExpression());
			
				
		return expression;
	}

	public List<? extends VariableDeclaration> getVariableDeclarations(Rule rule) {
		if (rule instanceof MatchedRule && ((MatchedRule) rule).getInPattern() != null)
			return ((MatchedRule) rule).getInPattern().getElements();
		if (rule.getVariables() != null)
			return rule.getVariables();
		return new ArrayList<>();
	}
	public List<? extends VariableDeclaration> getVariableDeclarationsLazy(Rule rule) {
		if (rule instanceof LazyRule && ((LazyRule) rule).getInPattern() != null)
			return ((LazyRule) rule).getInPattern().getElements();
		if (rule.getVariables() != null)
			return rule.getVariables();
		return new ArrayList<>();
	}

	@Override
	public String getDescription() {
		return "Creation of Binding";
	}

	
	
	/**
	 * It returns a binding for a property defined in a subclass and correct value.
	 * 
	 * @param outpattern      out class for binding
	 * @param metamodel  output metamodel
	 * @param solution
	 * @param ivariables input variable declarations
	 * @param wrapper
	 * @param indexrule
	 * @param inputMM 
	 * @param array2
	 * @param location
	 */
	private Binding getBinding6(String outpattern, MetaModel metamodel, List<Binding> bindings, Solution solution,
			List<? extends VariableDeclaration> ivariables, int row, int indexrule, ATLModel wrapper, MetaModel inputMM,CommonFunctionOperators cp
			, String[] array2) {
	
		Binding binding = null;
		CreationFunctions cf=new CreationFunctions(wrapper);
		// add all attributhaye outpattern (clazz) in featurelist
		ArrayList<EStructuralFeature> featurelist = cp.candidateReferenceSpecificOutpattern(outpattern);
		boolean checkfirstside = false;
		int number = -1;
		EStructuralFeature feature = null;
		// if cannot find non existing attribute for outpattern choose another outpattern
		boolean foundAttr = cf.existNonExistingattributeOutpattern(featurelist,solution,row);
		if (!foundAttr)
			return null;
		// choose attribute for left side randomly, that not be in the transformation
		while (!checkfirstside) {
			number = new Random().nextInt(featurelist.size());
			feature = featurelist.get(number);
			if (!solution.getCoSolutionThreeStep().getOp().listpropertyname.get(row).contains(feature.getName())
					&& !feature.getName().equals("orderedMessages") )//feature.getLowerBound()==1
				checkfirstside = true;

		}
		// choose attribute for right side
		OclExpression exp = getCompatibleValue(feature, ivariables, wrapper, indexrule,inputMM,cp);
		// if cannot find attribute with correct upper bound
		// then cannot create exp and return null
		if (exp == null) {
			return null;
		}
        updateSolution(solution,feature,row,outpattern,indexrule,array2);
        binding=createNewBinding(feature,exp,binding,cp);
		return binding;
	}

	private Binding createNewBinding(EStructuralFeature feature, OclExpression exp, Binding binding,CommonFunctionOperators cp) {
		
		binding = ATLFactory.eINSTANCE.createBinding();
		binding.setPropertyName(feature.getName());
		binding.setValue(exp);
		
		return binding;
	}

	private void updateSolution(Solution solution, EStructuralFeature feature, int row, String outpattern, int indexrule
			, String[] array2) {
		
		solution.modifypropertyname.set(MyProblemThreeStep.indexoperation - 1, outpattern);
		solution.newstring.set(MyProblemThreeStep.indexoperation - 1, feature.getName());
		solution.inorout.set(MyProblemThreeStep.indexoperation - 1, "out");
		solution.getCoSolutionThreeStep().getOp().listpropertyname.get(row).add(feature.getName());
		solution.getCoSolutionThreeStep().getOp().listpropertytype.get(row).add(feature.getEType().getName());
		if(solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(row).size()>0)
			solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(row)
					.add(solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(row).get(
							solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(row).size() - 1) + 1);
			else
				solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(row)
				.add(Integer.parseInt(array2[0])+1);
	}

	/**
	 * It returns a compatible ocl expression for the received feature.
	 * 
	 * @param feature
	 * @param variables (used when the feature has a non-primitive type)
	 * @param indexrule
	 * @param wrapper
	 * @param metamodel 
	 */
	private OclExpression getCompatibleValue(EStructuralFeature feature, List<? extends VariableDeclaration> variables,
			ATLModel wrapper, int indexrule, MetaModel inputMM, CommonFunctionOperators cp) {
		
		return getCompatibleValue(feature.getEType().getName(), true, true, variables, feature.getUpperBound(), wrapper,
				indexrule,inputMM,cp);
	}

	/**
	 * It returns a compatible ocl expression for the received type.
	 * 
	 * @param type
	 * @param monovalued
	 * @param ordered    (used in case of collections, i.e., when monovalued==true)
	 * @param variables  (used when the type is not primitive)
	 * @param bound
	 * @param indexrule
	 * @param wrapper
	 * @param metamodel 
	 */
	private OclExpression getCompatibleValue(String type, boolean monovalued, boolean ordered,
			List<? extends VariableDeclaration> variables, int bound, ATLModel wrapper, int indexrule, MetaModel inputMM,CommonFunctionOperators cp) {
		OclExpression expression = null;
	
		if (monovalued) {
			if (EMFUtils.isString(type)) {
				expression=GenerateStringBinding(cp,indexrule,wrapper, variables);
				} else if (EMFUtils.isInteger(type)) {
				expression = OCLFactory.eINSTANCE.createIntegerExp();
				((IntegerExp) expression).setIntegerSymbol(0);
			} else if (EMFUtils.isBoolean(type)) {
				expression = OCLFactory.eINSTANCE.createBooleanExp();
				((BooleanExp) expression).setBooleanSymbol(false);
			} else if (EMFUtils.isFloating(type)) {
				expression = OCLFactory.eINSTANCE.createRealExp();
				((RealExp) expression).setRealSymbol(0);
			} else {
				
				EObject oldFeatureValue= cp.FindSpecificInpattern(indexrule,wrapper);
				List<EStructuralFeature> maintarget = cp.candidateReferenceSpecificInpattern(toString(oldFeatureValue));
				
				if(maintarget.size()>0)
				{
				ArrayList<Integer> inferredRule=cp.FindRuleInferedOutpattern(wrapper,type);
				ArrayList<String> listinferredinpattern = new ArrayList<String>();
				for(int i=0;i<inferredRule.size();i++) { //find inferred inpattern 
				EObject inferredinpattern= cp.FindSpecificInpattern(inferredRule.get(i), wrapper);
				listinferredinpattern.add(toString(inferredinpattern) );
				}
				ArrayList<Integer> attrlist = new ArrayList<Integer>();
				boolean check = false;
				boolean rhswithsequence=false;
				int randomInt2 = -1;// choosing attributes for right side of binding
				while (!check ) {
					
					Random number_generator = new Random();
					randomInt2 = number_generator.nextInt(maintarget.size());
					if (!attrlist.contains(randomInt2))
						attrlist.add(randomInt2);
					
					boolean inferredType=true;
					if (maintarget.get(randomInt2).getUpperBound() == bound)  // Lhs, Rhs: equal upperbound
						if ((maintarget.get(randomInt2).getUpperBound() == 1)) { // lhs, Rhs upperbound=1
							if ((maintarget.get(randomInt2).getLowerBound() != 0)
								&& inferredType==true) //
							{
								check = true;
								break;
							}
						} if(inferredType==true) 
						{
							check = true;
							break;
						}
					if(!maintarget.get(randomInt2).getEType().getName().equals("Boolean")
							&& !maintarget.get(randomInt2).getEType().getName().equals("EBoolean")
							&& !maintarget.get(randomInt2).getEType().getName().equals("EString")) {
					}
					//upper lhs:-1, upper Rhs; 1 : create sequence { Rhs attribute}
					if (attrlist.size() == maintarget.size() && check == false)
					{
						check=true;
						rhswithsequence=true;
					}
				}
			
				// create right of the binding, if there is attribute
				if(!rhswithsequence) {
				cp.StoreThirdIndex(randomInt2);
				List<anatlyzer.atlext.ATL.Helper> helper = (List<Helper>) wrapper
						.allObjectsOf(anatlyzer.atlext.ATL.Helper.class);
				double dr=(Math.random());
				if(helper.size()==0)
					dr=0;
				if (dr<=0.5)
				expression=createNewExpression(maintarget,randomInt2,variables);
				else
				expression=createNewExpressionhelper(wrapper,randomInt2,variables);
				}
				else {
				expression=createNewExpressionhelper(wrapper,randomInt2,variables);
				}
				
				}	
			}
		}

		else {
			
			expression = OCLFactory.eINSTANCE.createSequenceExp();
			}

		return expression;
	}

	private OclExpression GenerateStringBinding(CommonFunctionOperators cp, int indexrule, ATLModel wrapper, List<? extends VariableDeclaration> variables) {
		
		OclExpression expression=null;
		expression = OCLFactory.eINSTANCE.createStringExp();
		EObject oldFeatureValue= cp.FindSpecificInpattern(indexrule,wrapper);
		List<EStructuralFeature> maintarget = cp.candidateReferenceSpecificInpattern(toString(oldFeatureValue));
		ArrayList<Integer> candidaterandomInt2 = new ArrayList<Integer>();
		boolean ch=false;
		String str=null;
		while(!ch) {
			Random number_generator = new Random();
			   if(maintarget.size()==0)
				   break;
				int randomInt3 = number_generator.nextInt(maintarget.size());
				if(!candidaterandomInt2.contains( randomInt3))
					         candidaterandomInt2.add(randomInt3 );
				if(maintarget.get(randomInt3).getEType().getName().equals("EString")
						|| maintarget.get(randomInt3).getEType().getName().equals("String"))
				{
					str=maintarget.get(randomInt3).getName();
					break;
				}
		if(candidaterandomInt2.size()==maintarget.size())
					break;
		}
		if(str!=null)
		{
			double dr=(Math.random());
			
			List<anatlyzer.atlext.ATL.Helper> helper = (List<Helper>) wrapper
					.allObjectsOf(anatlyzer.atlext.ATL.Helper.class);
			if(helper.size()==0)
				dr=0;
			 if (dr<0.5)
			
				expression=FilloutParameterofExpression(variables, str); 
			 
			 else 	
				expression=createNewExpressionhelper2(wrapper,-1,variables);
			
				}
				else {
				
				 double dr=(Math.random());
				 List<anatlyzer.atlext.ATL.Helper> helper = (List<Helper>) wrapper
							.allObjectsOf(anatlyzer.atlext.ATL.Helper.class);
					if(helper.size()==0)
						dr=0;
				 if (dr<0.5)
					 ((StringExp) expression).setStringSymbol("dummy");
				 else
					 expression=createNewExpressionhelper2(wrapper,-1,variables);
				 
				}
	
		return expression;
	}

	private OclExpression FilloutParameterofExpression(List<? extends VariableDeclaration> variables, String str) {
		
			OclExpression expression=null;
			NavigationOrAttributeCallExp nc = OCLFactory.eINSTANCE.createNavigationOrAttributeCallExp();
			nc.setIsStaticCall(false);
			nc.setImplicitlyCasted(false);
			nc.setName(str);
			VariableDeclaration sel = OCLFactory.eINSTANCE.createVariableDeclaration();
			String var = variables.get(0).getVarName();
			sel.setVarName(var);
			VariableExp vExp = OCLFactory.eINSTANCE.createVariableExp();
			vExp.setReferredVariable(variables.get(0));
			nc.setSource(vExp);
			nc.setInitializedVariable(sel);
			(expression) = (sel.getInitExpression());
		
		return expression;
	}
}
