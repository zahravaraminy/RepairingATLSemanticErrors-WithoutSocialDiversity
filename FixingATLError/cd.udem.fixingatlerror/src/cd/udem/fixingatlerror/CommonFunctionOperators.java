package cd.udem.fixingatlerror;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.InPatternElement;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.atlext.OCL.Attribute;
import anatlyzer.atlext.OCL.Operation;
import anatlyzer.atlext.OCL.VariableExp;
import evaluation.mutator.AbstractMutator;
import jmetal.core.Solution;
import jmetal.metaheuristics.nsgaII.NSGAIIThreeStep;
import jmetal.problems.MyProblemThreeStep;
import transML.exceptions.transException;
import witness.generator.MetaModel;


public class CommonFunctionOperators extends AbstractMutator {
	 
	int idoutput = -1;
	int idbinding = -1;
	private boolean lazychoosen=false;
	
	 public CommonFunctionOperators() 
	    {
	    	
	    }
	    
	   public int FindRule(String[] array) {
		   int indexrule=-1;
		   for (int j = 0; j < NSGAIIThreeStep.faultrule.size(); j++) {
				if (Integer.parseInt(array[0]) > NSGAIIThreeStep.faultrule.get(j)
						&& Integer.parseInt(array[0]) < NSGAIIThreeStep.finalrule.get(j))

					indexrule = j;

			}
		   return indexrule;
	   }
	   public int FindRuleInteger(int line) {
		   int indexrule=-1;
		   for (int j = 0; j < NSGAIIThreeStep.faultrule.size(); j++) {
				if (line > NSGAIIThreeStep.faultrule.get(j)
						&& line < NSGAIIThreeStep.finalrule.get(j))

					indexrule = j;

			}
		   return indexrule;
	   }

	public int FindLeftSidebindingsSpecificOutpattern(Solution solution, String[] array2) {
		
		return NSGAIIThreeStep.locationfilter.indexOf(Integer.parseInt(array2[0])-1);
		
	}

	public boolean checkIfAlreadyCreatedbinding(List<Binding> realbindings, Binding binding, Solution solution,
			int row) {
		boolean checkforcreate =false;
		for (int i = 0; i < realbindings.size(); i++) {
			if (binding != null) {
				if (solution.getCoSolutionThreeStep().getOp().originalwrapper.get(row)
						.get(i) == 1
						&& realbindings.get(i).getValue().getClass()
								.equals(binding.getValue().getClass())
						&& realbindings.get(i).getPropertyName()
								.equals(binding.getPropertyName())) {
					checkforcreate = true;

				}
			}
		}

		return checkforcreate;
	}
	  
	private <ToModify extends LocatedElement>  String AddNewCommentsOnTransformation(List<ToModify> modifiable, int randomInt, EObject oldFeatureValue,
			List<EObject> replacements, int randomInt2) {
	
		return "\n-- MUTATION \"" + this.getDescription() + "\" from "
		+ toString(modifiable.get(randomInt)) + ":" + toString(oldFeatureValue) + " to "
		+ toString(modifiable.get(randomInt)) + ":"
		+ toString(replacements.get(randomInt2)) + " (line "
		+ modifiable.get(randomInt).getLocation() + " of original transformation)\n";
	}

	public <ToModify extends LocatedElement> List<Object> applyChange(int randomInt, List<ToModify> modifiable,
			List<Object> ReturnResult, List<EObject> replacements, int randomInt2, EObject oldFeatureValue,
			String comment, ATLModel wrapper, EMFModel atlModel) {
		
		
		comment = AddNewCommentsOnTransformation(modifiable,randomInt,oldFeatureValue,replacements,randomInt2);
		NSGAIIThreeStep.numop = NSGAIIThreeStep.numop + 1;
		ReturnResult.set(0, wrapper);
		ReturnResult.set(1, atlModel);
		ReturnResult.add(comment);

		return ReturnResult;

	}

	

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Creation of Binding";
	}

	public EDataTypeEList<String> ReturnInitialComments(ATLModel wrapper) {
		// TODO Auto-generated method stub
		
		Module module = wrapper.getModule();
		if (module != null) {
			EStructuralFeature feature = wrapper.source(module).eClass().getEStructuralFeature("commentsAfter");
			return ( (EDataTypeEList<String>) wrapper.source(module).eGet(feature));
		}
		return null;
	}
	
	
	public <ToModify extends LocatedElement> List<Integer> ReturnFirstIndex(int randomInt, int size, boolean checkmutationapply, Solution solution, List<VariableExp> variables,List<ToModify> modifiable) {
		
		if (NSGAIIThreeStep.statemutcrossinitial.equals("mutation")) {

			switch (MyProblemThreeStep.indexoperation) {

			case 1:
				randomInt=GenerateRandomNumber(MyProblemThreeStep.oldoperation1 ,randomInt, size, solution);
				break;
			case 2:
				randomInt=GenerateRandomNumber(MyProblemThreeStep.oldoperation2 ,randomInt, size, solution);

				break;
			case 3:
				randomInt=GenerateRandomNumber(MyProblemThreeStep.oldoperation3 ,randomInt, size, solution);
				break;
			case 4:
				randomInt=GenerateRandomNumber(MyProblemThreeStep.oldoperation4 ,randomInt, size, solution);
				break;
			case 5:
				randomInt=GenerateRandomNumber(MyProblemThreeStep.oldoperation5 ,randomInt, size, solution);

				break;
			case 6:
				randomInt=GenerateRandomNumber(MyProblemThreeStep.oldoperation6 ,randomInt, size, solution);

				break;
			case 7:
				randomInt=GenerateRandomNumber(MyProblemThreeStep.oldoperation7 ,randomInt, size, solution);
				break;
			case 8:
				randomInt=GenerateRandomNumber(MyProblemThreeStep.oldoperation8 ,randomInt, size, solution);
				break;
			
			}

		} else {
			
				Random number_generator = new Random();
				randomInt = number_generator.nextInt(size);
				checkmutationapply = true;
				solution.setpreviousgeneration(false);
		}
		boolean ch=false;
		if(NSGAIIThreeStep.mandatorycreationoperation.size()>0 && ch==false)
		{
			ch=true;
      		randomInt = NSGAIIThreeStep.mandatorycreationlocation.get(0);
			solution.setpreviousgeneration(true);
		}
		if(NSGAIIThreeStep.mandatoryoutpatternoperation.size()>0) {
			randomInt = NSGAIIThreeStep.mandatoryoutpatternlocation.get(0);
			solution.setpreviousgeneration(true);
			
		}
		
		if(NSGAIIThreeStep.mandatorydeletionattribute.size()>0 && ch==false &&  !OperationsThreeStep.statecase.equals("case10")) {
			randomInt = NSGAIIThreeStep.mandatorydeletionlocation.get(1);// which outpattern choose for deleting
			solution.setpreviousgeneration(true);
			
		}
	
		List<Integer> Result = ReturnRandomIntcheckmutationapply(randomInt,checkmutationapply,0);
		
		return Result;

	}

	private int GenerateRandomNumber(int oldoperation, int randomInt, int size, Solution solution) {
		
		if (oldoperation != -1) {
			randomInt = (int) (oldoperation);
			solution.setpreviousgeneration(true);
		} else

		{
			Random number_generator = new Random();
			if (size > 1)
				randomInt = number_generator.nextInt(size);
			else
				randomInt = 0;
			solution.setpreviousgeneration(false);

		}
	  return randomInt;	
	}

private int GenerateThirdRandomNumber(int oldoperation, int randomInt, int size) {
		
		if (oldoperation != -1) {
			randomInt = (int) (oldoperation);
			
		} else

		{
			Random number_generator = new Random();
			randomInt = number_generator.nextInt(size);
			
		}
		

	  return randomInt;	
	}
	protected <Type> void filterSubtypes (List<Type> objects, Class<Type> type) {
		
		List<Type> subtypes = new ArrayList<Type>();
		for (Type container : objects) {
			boolean isSubtype = true;
			for (Class<?> n : container.getClass().getInterfaces())
				if (type.getName().equals(n.getName()))
					isSubtype = false;
			if (isSubtype) subtypes.add(container);
		}
		objects.removeAll(subtypes);
	}	
	public int NextElementofRHSattribute(Solution solution,int id) {
		// TODO Auto-generated method stub
	int foo;
	try { //after class
		   foo = Integer.parseInt(solution.RHSattributerefinement.get(id));
		}
		catch (NumberFormatException e)
		{
		   foo = 0;
		}
		return foo;
	}
	
		
 private List<Integer> ReturnRandomIntcheckmutationapply(int randomInt, boolean checkmutationapply, int numdeletion) {
		// TODO Auto-generated method stub
	 List<Integer> Result = new ArrayList<Integer>();
		Result.add(randomInt);
		if (checkmutationapply == true)
			Result.add(1);
		else
			Result.add(0);
		
		Result.add(numdeletion);
		return Result;
	}

 private void findLeftAttributeforNavigation(Solution solution, String[] array) {
		// TODO Auto-generated method stub
		
	for (int i = 0; i < solution.getCoSolutionThreeStep().getOp().listpropertynamelocation
			.size(); i++) {
		
		for (int j = 0; j < solution.getCoSolutionThreeStep().getOp().listpropertynamelocation
				.get(i).size(); j++) {
			if(j+1 <solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(i).size())
			{
			if( Integer.parseInt(array[0]) >=solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(i).get(j) 
					&& Integer.parseInt(array[0])<solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(i).get(j+1)) {
			
				this.idoutput = i;
				this.idbinding = j;
				break;
			}
		}
		else if(j+1 ==solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(i).size()
				) {
			if( Integer.parseInt(array[0]) >=solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(i).get(j) 
				){
				
			this.idoutput = i;
			this.idbinding = solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(i).size()-1;
			break;
			}}
		
			
		}
		
	}
	

}

public ArrayList<String> updateFilterRules(Solution solution, int indexrule,CharSequence oldnavigation,int randomInt2,List<Object> replacements) {
	
	String str=solution.filterSolution.get(indexrule);
	String replaceString=str.replace(oldnavigation, replacements.get(randomInt2).toString());
	solution.filterSolution.set(indexrule, replaceString); 
	return solution.filterSolution;
}
public ArrayList<String> updateFilterArgument(Solution solution, int indexrule,String oldnavigation,String newType) {
	String str=solution.filterSolution.get(indexrule );
	StringBuilder builder = new StringBuilder();
	int start = str.lastIndexOf(oldnavigation);
	builder.append(newType);// add newType to builder
	builder.append(str.substring(start + oldnavigation.length()));
	solution.filterSolution.set(indexrule, builder.toString()); 
	return solution.filterSolution;
}
public <ToModify extends LocatedElement> List<Integer> findNotdeletionNavigation(List<VariableExp> variables, int randomInt, Solution solution, int size, boolean checkmutationapply,List<ToModify> modifiable) {
	String[] line = null;
	//randomInt=2;
	  line=CalculateNewLineOfVariable(variables, randomInt,modifiable);  
	 findLeftAttributeforNavigation(solution,line);
	 boolean notdeletion=false;// if Binding deleted
	 int numdeletion=0;
	 if(this.idoutput>=0 && this.idbinding>=0)
	 if(solution.getCoSolutionThreeStep().getOp().listpropertyname.get(this.idoutput).get(this.idbinding)==null)
	 {
		while(notdeletion==false) {
			{
				numdeletion++;
				randomInt=GenerateNewRandomInt(size,randomInt);		
				checkmutationapply = true;
				solution.setpreviousgeneration(false);
				line=CalculateNewLineOfVariable(variables, randomInt,modifiable);  
				findLeftAttributeforNavigation(solution,line);
				if(solution.getCoSolutionThreeStep().getOp().listpropertyname.get(this.idoutput).get(this.idbinding)!=null)
					notdeletion=true;
				if(size==1 && solution.getCoSolutionThreeStep().getOp().listpropertyname.get(this.idoutput).get(this.idbinding)==null)
					randomInt = -1;
			}
		}
		
		
	 }
	 List<Integer> Result = ReturnRandomIntcheckmutationapply(randomInt,checkmutationapply,numdeletion);
		return Result;
		
 }
	private <ToModify extends LocatedElement> String[] CalculateNewLineOfVariable(List<VariableExp> variables, int randomInt, List<ToModify> modifiable) {
	// TODO Auto-generated method stub
		if(OperationsThreeStep.statecase.equals("case9"))
		  	return variables.get(randomInt).getLocation().split(":", 2);
		else if (OperationsThreeStep.statecase.equals("case7")
				  || OperationsThreeStep.statecase.equals("case3") || OperationsThreeStep.statecase.equals("case10") 
				  || OperationsThreeStep.statecase.equals("case5"))
			  	return modifiable.get(randomInt).getLocation().split(":", 2);
		return null;
		  
}

	

	private int GenerateNewRandomInt(int size, int randomInt) {
		
		if(OperationsThreeStep.statecase.equals("case10")
				|| OperationsThreeStep.statecase.equals("case2")
				|| OperationsThreeStep.statecase.equals("case7")
				|| OperationsThreeStep.statecase.equals("case5")
				|| OperationsThreeStep.statecase.equals("case9")
				|| OperationsThreeStep.statecase.equals("case3")) //Binding modification
		{
			Random number_generator = new Random();
			randomInt = number_generator.nextInt(size);
			
	
		}
	return randomInt;
}

	public int FindThirdIndex(int randomInt2, int size) {
		// TODO Auto-generated method stub
		if (NSGAIIThreeStep.statemutcrossinitial == "mutation") {

			switch (MyProblemThreeStep.indexoperation) {
			case 1:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation1, randomInt2,  size);
				
				break;
			case 2:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation2, randomInt2,  size);
				break;
			case 3:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation3, randomInt2,  size);
				break;
			case 4:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation4, randomInt2,  size);
				break;
			case 5:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation5, randomInt2,  size);
				break;
			case 6:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation6, randomInt2,  size);
				break;
			case 7:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation7, randomInt2,  size);
				break;
			case 8:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation8, randomInt2,  size);
				break;
			case 9:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation9, randomInt2,  size);
				break;
			
			}

		} else {
			Random number_generator = new Random();
			randomInt2 = number_generator.nextInt(size);
			
		}

		return randomInt2;

	}
	public int FindSecondInd(int randomInt2, int size) {
		Random number_generator = new Random();
		randomInt2 = number_generator.nextInt(size);
		if(NSGAIIThreeStep.mandatorycreationoperation.size()>0)
		{
		
		    randomInt2 = NSGAIIThreeStep.mandatorycreationlocation.get(1);
			
		}
		if(NSGAIIThreeStep.mandatoryoutpatternoperation.size()>0) {
			randomInt2 = NSGAIIThreeStep.mandatoryoutpatternlocation.get(1);
		}
		return randomInt2;
	}

	public int FindSecondIndex(int randomInt2, int size) {
		// TODO Auto-generated method stub
		if (NSGAIIThreeStep.statemutcrossinitial == "mutation") {

			switch (MyProblemThreeStep.indexoperation) {
			case 1:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation1, randomInt2,  size);
				break;
			case 2:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation2, randomInt2,  size);
				break;
			case 3:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation3, randomInt2,  size);
				break;
			case 4:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation4, randomInt2,  size);
				break;
			case 5:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation5, randomInt2,  size);
				break;
			case 6:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation6, randomInt2,  size);
				break;
			case 7:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation7, randomInt2,  size);
				break;
			case 8:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation8, randomInt2,  size);
				break;
			case 9:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation9, randomInt2,  size);
				break;
			}

		} else {
			Random number_generator = new Random();
			randomInt2 = number_generator.nextInt(size);

		}

		if(NSGAIIThreeStep.mandatorycreationoperation.size()>0)
		{
			randomInt2 = NSGAIIThreeStep.mandatorycreationlocation.get(1);
			
		}
		if(NSGAIIThreeStep.mandatoryoutpatternoperation.size()>0) {
			randomInt2 = NSGAIIThreeStep.mandatoryoutpatternlocation.get(1);
		}
		return randomInt2;

	}

	public List<EObject> replacements(EMFModel atlModel, EObject toReplace, MetaModel metamodels) {
		
		List<EClass> mainclass = new ArrayList<EClass>();
		List<EClass> options = new ArrayList<EClass>();
		EPackage pack = toReplace.eClass().getEPackage();
		List<EObject> replacements = new ArrayList<EObject>();
		
		EClass mmtype = (EClass) pack.getEClassifier("OclModelElement");
		
		for (EClassifier classifier : metamodels.getEClassifiers()) {
			if (classifier instanceof EClass) {
				
				mainclass.add((EClass) classifier);
			}
			}
		for (int ii = 0; ii < mainclass.size(); ii++) {
			options.add(mainclass.get(ii));

		}
		for (EClass option : options) {
			if (option != null) {
				EObject object = (EObject) atlModel.newElement(mmtype);
				object.eSet(mmtype.getEStructuralFeature("name"), option.getName());
				replacements.add(object);
			}
		}
		
		
		return replacements;
		
	}
	public void StoreThirdIndex(int randomInt) {
		// TODO Auto-generated method stub

		switch (MyProblemThreeStep.indexoperation) {
		case 1:
			MyProblemThreeStep.secondoldoperation1 = randomInt;

			break;
		case 2:
			MyProblemThreeStep.secondoldoperation2 = randomInt;

			break;
		case 3:
			MyProblemThreeStep.secondoldoperation3 = randomInt;

			break;
		case 4:
			MyProblemThreeStep.secondoldoperation4 = randomInt;

			break;
		case 5:
			MyProblemThreeStep.secondoldoperation5 = randomInt;

			break;
		case 6:
			MyProblemThreeStep.secondoldoperation6 = randomInt;

			break;
		case 7:
			MyProblemThreeStep.secondoldoperation7 = randomInt;

			break;
		case 8:
			MyProblemThreeStep.secondoldoperation8 = randomInt;

			break;
		case 9:
			MyProblemThreeStep.secondoldoperation9 = randomInt;

			break;
				}

	}
	public int calculaterandomIn(List<EObject> replacements) {
		int randomInt2 = -2;
		Random number_generator = new Random();
		randomInt2 = number_generator.nextInt(replacements.size());
		
	
	return randomInt2;
	}
	public int calculaterandomInt(List<EObject> replacements) {
		// TODO Auto-generated method stub

		int randomInt2 = -2;
		if (NSGAIIThreeStep.statemutcrossinitial == "mutation") {

			switch (MyProblemThreeStep.indexoperation) {
			case 1:
				GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation1, randomInt2,  replacements.size());
				break;
			case 2:
				GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation2, randomInt2,  replacements.size());				break;
			case 3:
				GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation3, randomInt2,  replacements.size());				break;
			case 4:
				GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation4, randomInt2,  replacements.size());
				break;
			case 5:
				GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation5, randomInt2,  replacements.size());				break;
			case 6:
				GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation6, randomInt2,  replacements.size());
				break;
			case 7:
				GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation7, randomInt2,  replacements.size());
				break;
			case 8:
				GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation8, randomInt2,  replacements.size());
				break;
			case 9:
				GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation9, randomInt2,  replacements.size());
				break;
			case 10:
				GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation10, randomInt2,  replacements.size());
				break;
				}

		} else {
			Random number_generator = new Random();
			randomInt2 = number_generator.nextInt(replacements.size());
			
		}
		return randomInt2;

	}


	public int calculaterandomInt2(List<EObject> value) {
		// TODO Auto-generated method stub
		int randomInt = -2;
		if (NSGAIIThreeStep.statemutcrossinitial == "mutation") {

			switch (MyProblemThreeStep.indexoperation) {
			case 1:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation1, randomInt,  value.size());
				
				break;
			case 2:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation2, randomInt,  value.size());
				break;
			case 3:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation3, randomInt,  value.size());
				break;
			case 4:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation4, randomInt,  value.size());
				break;
			case 5:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation5, randomInt,  value.size());
				break;
			case 6:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation6, randomInt,  value.size());
				break;
			case 7:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation7, randomInt,  value.size());
				break;
			case 8:
				GenerateThirdRandomNumber(MyProblemThreeStep.secondoldoperation8, randomInt,  value.size());
				break;
			
			}

		} else {
			Random number_generator = new Random();
			randomInt = number_generator.nextInt(value.size());
			
		}
		return randomInt;

	}


	public void setvariable(int randomInt, int randomInt2, int randomInt3) {
		// TODO Auto-generated method stub

		switch (MyProblemThreeStep.indexoperation) {
		case 1:
			MyProblemThreeStep.oldoperation1 = randomInt;
			MyProblemThreeStep.replaceoperation1 = randomInt2;
			MyProblemThreeStep.secondoldoperation1 = randomInt3;
			break;
		case 2:
			MyProblemThreeStep.oldoperation2 = randomInt;
			MyProblemThreeStep.replaceoperation2 = randomInt2;
			MyProblemThreeStep.secondoldoperation2 = randomInt3;

			break;
		case 3:
			MyProblemThreeStep.oldoperation3 = randomInt;
			MyProblemThreeStep.replaceoperation3 = randomInt2;
			MyProblemThreeStep.secondoldoperation3 = randomInt3;

			break;
		case 4:
			MyProblemThreeStep.oldoperation4 = randomInt;
			MyProblemThreeStep.replaceoperation4 = randomInt2;
			MyProblemThreeStep.secondoldoperation4 = randomInt3;

			break;
		case 5:
			MyProblemThreeStep.oldoperation5 = randomInt;
			MyProblemThreeStep.replaceoperation5 = randomInt2;
			MyProblemThreeStep.secondoldoperation5 = randomInt3;

			break;
		case 6:
			MyProblemThreeStep.oldoperation6 = randomInt;
			MyProblemThreeStep.replaceoperation6 = randomInt2;
			MyProblemThreeStep.secondoldoperation6 = randomInt3;

			break;
		case 7:
			MyProblemThreeStep.oldoperation7 = randomInt;
			MyProblemThreeStep.replaceoperation7 = randomInt2;
			MyProblemThreeStep.secondoldoperation7 = randomInt3;

			break;
		case 8:
			MyProblemThreeStep.oldoperation8 = randomInt;
			MyProblemThreeStep.replaceoperation8 = randomInt2;
			MyProblemThreeStep.secondoldoperation8 = randomInt3;

			break;
		case 9:
			MyProblemThreeStep.oldoperation9 = randomInt;
			MyProblemThreeStep.replaceoperation9 = randomInt2;
			MyProblemThreeStep.secondoldoperation9 = randomInt3;

			break;
		case 10:
			MyProblemThreeStep.oldoperation10 = randomInt;
			MyProblemThreeStep.replaceoperation10 = randomInt2;
			MyProblemThreeStep.secondoldoperation10 = randomInt3;

			break;
		case 11:
			MyProblemThreeStep.oldoperation11 = randomInt;
			MyProblemThreeStep.replaceoperation11 = randomInt2;
			MyProblemThreeStep.secondoldoperation11 = randomInt3;

			break;
		case 12:
			MyProblemThreeStep.oldoperation12 = randomInt;
			MyProblemThreeStep.replaceoperation12 = randomInt2;
			MyProblemThreeStep.secondoldoperation12 = randomInt3;

			break;
		case 13:
			MyProblemThreeStep.oldoperation13 = randomInt;
			MyProblemThreeStep.replaceoperation13 = randomInt2;
			MyProblemThreeStep.secondoldoperation13 = randomInt3;

			break;
		case 14:
			MyProblemThreeStep.oldoperation14 = randomInt;
			MyProblemThreeStep.replaceoperation14 = randomInt2;
			MyProblemThreeStep.secondoldoperation14 = randomInt3;

			break;
		}
	}

	
	public void StoreTwoIndex(int randomInt, int randomInt2) {
		// TODO Auto-generated method stub

		switch (MyProblemThreeStep.indexoperation) {
		case 1:
			MyProblemThreeStep.oldoperation1 = randomInt;
			MyProblemThreeStep.replaceoperation1 = randomInt2;
			

			break;
		case 2:
			MyProblemThreeStep.oldoperation2 = randomInt;
			MyProblemThreeStep.replaceoperation2 = randomInt2;

			break;
		case 3:
			MyProblemThreeStep.oldoperation3 = randomInt;
			MyProblemThreeStep.replaceoperation3 = randomInt2;

			break;
		case 4:
			MyProblemThreeStep.oldoperation4 = randomInt;
			MyProblemThreeStep.replaceoperation4 = randomInt2;

			break;
		case 5:
			MyProblemThreeStep.oldoperation5 = randomInt;
			MyProblemThreeStep.replaceoperation5 = randomInt2;

			break;
		case 6:
			MyProblemThreeStep.oldoperation6 = randomInt;
			MyProblemThreeStep.replaceoperation6 = randomInt2;

			break;
		case 7:
			MyProblemThreeStep.oldoperation7 = randomInt;
			MyProblemThreeStep.replaceoperation7 = randomInt2;

			break;
		case 8:
			MyProblemThreeStep.oldoperation8 = randomInt;
			MyProblemThreeStep.replaceoperation8 = randomInt2;

			break;
		case 9:
			MyProblemThreeStep.oldoperation9 = randomInt;
			MyProblemThreeStep.replaceoperation9 = randomInt2;

			break;
		case 10:
			MyProblemThreeStep.oldoperation10 = randomInt;
			MyProblemThreeStep.replaceoperation10 = randomInt2;

			break;
		case 11:
			MyProblemThreeStep.oldoperation11 = randomInt;
			MyProblemThreeStep.replaceoperation11 = randomInt2;

			break;
		case 12:
			MyProblemThreeStep.oldoperation12 = randomInt;
			MyProblemThreeStep.replaceoperation12 = randomInt2;

			break;
		case 13:
			MyProblemThreeStep.oldoperation13 = randomInt;
			MyProblemThreeStep.replaceoperation13 = randomInt2;

			break;
		case 14:
			MyProblemThreeStep.oldoperation14 = randomInt;
			MyProblemThreeStep.replaceoperation14 = randomInt2;

			break;
		case 15:
			MyProblemThreeStep.oldoperation15 = randomInt;
			MyProblemThreeStep.replaceoperation15 = randomInt2;

			break;
		case 16:
			MyProblemThreeStep.oldoperation16 = randomInt;
			MyProblemThreeStep.replaceoperation16 = randomInt2;

			break;
		case 17:
			MyProblemThreeStep.oldoperation17 = randomInt;
			MyProblemThreeStep.replaceoperation17 = randomInt2;

			break;
		case 18:
			MyProblemThreeStep.oldoperation18 = randomInt;
			MyProblemThreeStep.replaceoperation18 = randomInt2;

			break;
		case 19:
			MyProblemThreeStep.oldoperation19 = randomInt;
			MyProblemThreeStep.replaceoperation19 = randomInt2;

			break;
		case 20:
			MyProblemThreeStep.oldoperation20 = randomInt;
			MyProblemThreeStep.replaceoperation20 = randomInt2;

			break;
		case 21:
			MyProblemThreeStep.oldoperation21 = randomInt;
			MyProblemThreeStep.replaceoperation21 = randomInt2;

			break;
		case 22:
			MyProblemThreeStep.oldoperation22 = randomInt;
			MyProblemThreeStep.replaceoperation22 = randomInt2;

			break;

		}

	}

	public ArrayList<EStructuralFeature> candidateReferenceSpecificOutpattern(String outpattern) {
		
		ArrayList<EStructuralFeature> featurelist = new ArrayList<EStructuralFeature>();
		int indexclassname = NSGAIIThreeStep.classnamestringtarget.indexOf(outpattern);
		for (int i = NSGAIIThreeStep.classnamestartpointtarget
				.get(indexclassname); i < NSGAIIThreeStep.classnamestartpointtarget.get(indexclassname)
						+ NSGAIIThreeStep.classnamelengthtarget.get(indexclassname); i++) {
		
			featurelist.add(NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(i));
			

		}
		
		return featurelist;
	}

	
	public ArrayList<Integer> FindRuleInferedOutpattern( ATLModel wrapper,String outpattern) {
	
		List<OutPatternElement> listoutpattern = (List<OutPatternElement>) wrapper
				.allObjectsOf(OutPatternElement.class);
		
		ArrayList<Integer> rule = new ArrayList<Integer>();
		
		for(int i=0; i<listoutpattern.size();i++ ) {
			
			EStructuralFeature featureDefinition = wrapper.source(listoutpattern.get(i)).eClass()
					.getEStructuralFeature("type");
			EObject object2modify_src = wrapper.source(listoutpattern.get(i));
			EObject oldFeatureValue = (EObject) object2modify_src.eGet(featureDefinition);

			if(outpattern.equals( toString(oldFeatureValue)))
			{
				String[] array2 = listoutpattern.get(i).getLocation().split(":", 2);
				rule.add(FindRule(array2));
				
			}
		 }
		
		return rule;
	}
	public EObject FindSpecificInpattern(int randomInt, ATLModel wrapper) {
		
		List<InPatternElement> listinpattern = (List<InPatternElement>) wrapper
				.allObjectsOf(InPatternElement.class);
		EObject oldFeatureValue = null;
		if(randomInt>=0)
		{
		EStructuralFeature featureDefinition = wrapper.source(listinpattern.get(randomInt)).eClass()
				.getEStructuralFeature("type");
		EObject object2modify_src = wrapper.source(listinpattern.get(randomInt));
		oldFeatureValue = (EObject) object2modify_src.eGet(featureDefinition);
		}
		return oldFeatureValue;
	}

	public EObject FindSpecificOutpattern(int randomInt, ATLModel wrapper) {
		
		EObject oldFeatureValue = null;
		List<OutPatternElement> listoutpattern = (List<OutPatternElement>) wrapper
				.allObjectsOf(OutPatternElement.class);
		if(randomInt >=0) {
		EStructuralFeature featureDefinition = wrapper.source(listoutpattern.get(randomInt)).eClass()
				.getEStructuralFeature("type");
		EObject object2modify_src = wrapper.source(listoutpattern.get(randomInt));
		oldFeatureValue = (EObject) object2modify_src.eGet(featureDefinition);
		}
		return oldFeatureValue;
	}


	public List<EStructuralFeature> candidateReferenceSpecificInpattern(String inpattern) {
		
		List<EStructuralFeature> maintarget = new ArrayList<EStructuralFeature>();
		int indexclassname = NSGAIIThreeStep.classnamestring.indexOf(inpattern);
		if(indexclassname>= 0)
		for (int i = NSGAIIThreeStep.classnamestartpoint
				.get(indexclassname); i < NSGAIIThreeStep.classnamestartpoint.get(indexclassname)
						+ NSGAIIThreeStep.classnamelength.get(indexclassname); i++) {

			maintarget.add(NSGAIIThreeStep.listinheritattributesourcemetamodel.get(i));

		}
		return maintarget;
	}
	
	
	public String find_Type_Attribute_in_Inpattern(String inpattern , String attribute) {
		
		int indexclassname = NSGAIIThreeStep.classnamestring.indexOf(inpattern);
		for (int i = NSGAIIThreeStep.classnamestartpoint
				.get(indexclassname); i < NSGAIIThreeStep.classnamestartpoint.get(indexclassname)
						+ NSGAIIThreeStep.classnamelength.get(indexclassname); i++) {
			if(NSGAIIThreeStep.listinheritattributesourcemetamodel.get(i).getName().equals(attribute) && 
					!NSGAIIThreeStep.listinheritattributesourcemetamodel.get(i).getEType().getName().equals("EBoolean")
					&& !NSGAIIThreeStep.listinheritattributesourcemetamodel.get(i).getEType().getName().equals("EString"))

				return (NSGAIIThreeStep.listinheritattributesourcemetamodel.get(i).getEType().getName());
		}
		return inpattern;
	}

	public String findFromElementInpattern(int indexrule, ATLModel wrapper) {
		
		List<InPatternElement> listinpattern = (List<InPatternElement>) wrapper.allObjectsOf(InPatternElement.class);
		return toString(listinpattern.get(indexrule));
		
	}
	public boolean attributeAccesibleInpattern(String inpattern, String attribute) {
	
		int indexclassname = NSGAIIThreeStep.classnamestring.indexOf(inpattern);
		for (int i = NSGAIIThreeStep.classnamestartpoint
				.get(indexclassname); i < NSGAIIThreeStep.classnamestartpoint.get(indexclassname)
						+ NSGAIIThreeStep.classnamelength.get(indexclassname); i++) {

			if(NSGAIIThreeStep.listinheritattributesourcemetamodel.get(i).getName().equals(attribute))
			     return true;
		}
		return false;
	}
	public String findClassNameattribute(String attribute) {
		
		int id=NSGAIIThreeStep.totallistattribute.indexOf(attribute);
		if(!NSGAIIThreeStep.listnavigationtype.get(id).equals("EBoolean") && !NSGAIIThreeStep.listnavigationtype.get(id).equals("Boolean"))
		return NSGAIIThreeStep.listnavigationtype.get(id);
		return null;
	
	}

	public String findAttributeType(String navigation) {
		// TODO Auto-generated method stub
		
		for (int i = 0; i < NSGAIIThreeStep.listinheritattributesourcemetamodel.size(); i++)
			
			{
			if (navigation
					.equals(NSGAIIThreeStep.listinheritattributesourcemetamodel.get(i).getName())) {
				return NSGAIIThreeStep.listinheritattributesourcemetamodel.get(i).getEType().getName();
				
			}
	}
		return null;
	}
	
	public int findOutPatternAttributeUpperBound(String navigation) {
		
		for (int i = 0; i < NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.size(); i++)
			
			{

			if (navigation
					.equals(NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(i).getName())) {
				return NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(i).getUpperBound();
				
			}
	}
		return -1;
	}

	public boolean checksuperclass(String firstclass, String target) {
	
		Setting s = new Setting();
		String MMRootPath2 = s.gettargetmetamodel();
		MetaModel metatarget = null;
		try {
			 metatarget = new MetaModel(MMRootPath2);
		} catch (transException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EClass firstelement = null;
		EClass secondelement = null;
		for (EClassifier classifier : metatarget.getEClassifiers()) {

			if (classifier instanceof EClass) {

				EClass child = ((EClass) classifier);

				if (child.getName().equals(firstclass)) {
					firstelement=child;
					
				}
				if (child.getName().equals(target)) {
					secondelement=child;
					
				}
		
			}
		}
		
		if(firstelement != null && secondelement != null)
		if (firstelement.isSuperTypeOf(secondelement)) {
			return true;
		}
		
		return false;
	}

	public StringBuilder calculateSemanticFilter(String str, String[] partition) {
		
		StringBuilder builderfilter = new StringBuilder();
		if(!str.equals("empty")) {
		for(int i=0;i<partition.length;i++) {
			String[] partition2= partition[i].split("\\!|\\)");
			if(partition[i].contains("oclIsKindOf") || partition[i].contains("oclIsTypeOf")) {
				String[] splittedfilter= partition[i].split("\\.|\\s+");
				ArrayList <String> newfilter = new ArrayList<String>();
				for(int j=0;j<splittedfilter.length;j++) {
					
					if(splittedfilter[j].startsWith("oclIsKindOf")|| splittedfilter[j].startsWith("oclIsTypeOf")) {
						if(newfilter.size()> 0) {
						if(newfilter.get( newfilter.size()-1).equals(partition2[1]))	
							newfilter.set(newfilter.size()-1,"true");
						else {
							boolean relationclass=checksuperclass(newfilter.get( newfilter.size()-1),partition2[1]);
							if(relationclass==true)
							newfilter.set(newfilter.size()-1,partition2[1].trim());
							else
							newfilter.set(newfilter.size()-1,"false");
							break;
						}
						}
						else {
							newfilter.add(partition2[1].trim());
						}
						}
					else if(!splittedfilter[j].isEmpty() && splittedfilter[j]!=null ) {
						String attrtype=findAttributeType(splittedfilter[j]);
						if (attrtype!=null)
						newfilter.add( attrtype.trim());
					}
					
					}
				 if(newfilter.size()>0)
					builderfilter.append(newfilter.get(newfilter.size()-1));
				
			}
			if(partition[i].contains("oclIsUndefined")) {
				builderfilter.append("true");
			}
			else if(!partition[i].contains("oclIsKindOf") && !partition[i].contains("oclIsTypeOf")
					&&  !partition[i].contains("oclIsUndefined")  ) {
				builderfilter.append(partition[i]);
			}
			
			
		}
	}
		return builderfilter;
	}

	public boolean compareNewfilterwithOtherFilters(Solution solution, int indexrule2, String str,
			StringBuilder builderfilter) {
	
		for(int i=0;i<solution.filterSolution.size();i++) {// compare new filter with other filters
			if(i!=indexrule2) {	
			String strt=solution.filterSolution.get(i );	
			String[] partitiont= strt.split("and |or ");
			StringBuilder builderfiltertemp =calculateSemanticFilter(strt,partitiont);
			if(builderfilter.toString().trim().equals(builderfiltertemp.toString().trim()) && !builderfiltertemp.toString().trim().isEmpty())
				return false;
			if(builderfiltertemp.toString().contains("true"))
				return false;
			}
		}
		return true;
	}

	public boolean compareNewInheritfilterwithOtherFilters(Solution solution, int indexrule2, String replacement,
			String str, String target) {
		//inheritClass:[Classifier, DataType, Class], first:classname, others inherit from first one 
		for(int i=0;i<NSGAIIThreeStep.inheritClass.size();i++) { //for all subclass of new type, compare new filter with other filters
			if(NSGAIIThreeStep.inheritClass.get(i).get(0).equals(replacement)) {
			for(int j=1;j<NSGAIIThreeStep.inheritClass.get(i).size();j++) {
				
					String subclass=NSGAIIThreeStep.inheritClass.get(i).get(j);
					String replacestrinnherit=str.replace(target, subclass);
					String[] partitiont= replacestrinnherit.split("and |or ");
					StringBuilder builderfiltertemp =calculateSemanticFilter(replacestrinnherit,partitiont);
					if(builderfiltertemp.toString().contains("true"))
						return false;
					boolean cmfilterinherit=compareNewfilterwithOtherFilters(solution,indexrule2,str,builderfiltertemp);
					if(!cmfilterinherit)
						return false;
					
			}
			}
		} 
		return true;
	}

	public boolean newFilterCompatibleOtherRules(int indexrule2, String target, String replacement, Solution solution, String str, String replacestr) {
		// TODO Auto-generated method stub
		
		String[] partition= replacestr.split("and |or ");
		StringBuilder builderfilter =calculateSemanticFilter(replacestr,partition);
		if(builderfilter.toString().contains("true"))
			return false;
		//System.out.println(solution.filterSolution);
		boolean cmfilter=compareNewfilterwithOtherFilters(solution,indexrule2,str,builderfilter);
		if(!cmfilter)
		  return false;
		return true;
	}

	public <Container extends LocatedElement, ToDelete/* extends LocatedElement */> List<Binding>  extractAllBindingOutpattern(ATLModel wrapper, List<Container> containers, int randomInt) {
		// TODO Auto-generated method stub
		EStructuralFeature feature2 = wrapper.source(containers.get(randomInt))
				.eClass().getEStructuralFeature("bindings");
		List<Binding> realbindings = (List<Binding>) wrapper
				.source(containers.get(randomInt)).eGet(feature2);
		return realbindings;
	}
	public List<Binding>  extractAllBindingOutpattern2(ATLModel wrapper, int randomInt) {
		// TODO Auto-generated method stub
		List<OutPatternElement> containers = (List<OutPatternElement>) wrapper.allObjectsOf(OutPatternElement.class);
		EStructuralFeature feature2 = wrapper.source(containers.get(randomInt))
				.eClass().getEStructuralFeature("bindings");
		List<Binding> realbindings = (List<Binding>) wrapper
				.source(containers.get(randomInt)).eGet(feature2);
		return realbindings;
	}

	public boolean Check_choosen_Outpattern_allow_for_Modification(Solution solution, EObject oldFeatureValue, ATLModel wrapper, int indexrule) {
		// TODO Auto-generated method stub
		EObject inpattern2=FindSpecificInpattern(indexrule , wrapper);
		List<Rule> modifiable = (List<Rule>) wrapper.allObjectsOf(Rule.class);
		for(int i=0;i<modifiable.size();i++) {
			if(indexrule!=i) {
				EObject inpattern3=FindSpecificInpattern(i , wrapper);
				
				if(toString(inpattern3).equals(toString(inpattern2) )) {
				
					for(int j=0;j<modifiable.get(indexrule).getOutPattern().getElements().size();j++) {
						if(modifiable.get(indexrule).getOutPattern().getElements().get(j).getType().getName().equals(  toString(oldFeatureValue ))) {
							return true;
						}
						
						
					}
					
				}
			}
			
		}
		
		for(int i=0;i<solution
				.getCoSolutionThreeStep().getOp().listpropertytype.size();i++ ) // search in all bindings
			for(int j=0;j<solution
					.getCoSolutionThreeStep().getOp().listpropertytype.get(i).size();j++ )
			{
				
		if(solution
				.getCoSolutionThreeStep().getOp().listpropertytype.get(i).get(j).equals( toString(oldFeatureValue ))) {
			// if old out pattern is equeal with Type LHS of one binding in ATL find line of that binding to return Type RHS of the binding
			int linebinding=solution
					.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(i).get(j);
			this.lazychoosen=false;
			NSGAIIThreeStep.temprule = FindRuleInteger( (linebinding));
			// find index of starting this binding in wholelineattributerefinement
			for(int k=0;k<solution.RHSattributelocation.size();k=k+3) // solution.RHSattributelocation: right hand side of binding
				if (solution.RHSattributelocation.get(k).equals(linebinding))
				{
					int startindex=solution.RHSattributelocation.get(k+1); // start right hand side of the binding in array refinement
					
					if(startindex>0) {
						
						List<InPatternElement> inpatternlist = (List<InPatternElement>) wrapper
								.allObjectsOf(InPatternElement.class);
						String attr_type=null; // all this section is for Type of right hand side of the binding
						String fromelement=toString(inpatternlist.get( NSGAIIThreeStep.temprule ));
						for(int u=startindex;u<solution.RHSattributerefinement.size();u++) {
							int foo=NextElementofRHSattribute(solution,u);
							if(solution.RHSattributelocation.contains( foo)) { // if element is integer (reach next binding), 
								break;
							}
							else {
								
								attr_type=TypeAttribute(solution,u, fromelement, wrapper, attr_type  );
								EObject inpattern=FindSpecificInpattern(indexrule , wrapper);
								if(attr_type!=null)
								if(toString(inpattern).equals(attr_type)) { // if Type of RHS is equal Inpattern 
									
									return false;
								}
										
							}
							
						}
						
						
					}
					
				break;
				}
					
		}
		}
		return true;
	}

	private String TypeAttribute(Solution solution, int u, String fromelement, ATLModel wrapper, String attr_type) {
		
		// case fromelement.attribute
		
		if(  solution.RHSattributerefinement.get(u).equals( fromelement) && this.lazychoosen==false) {
			
			EObject inpattern=FindSpecificInpattern(NSGAIIThreeStep.temprule , wrapper);
			
			attr_type=find_Type_Attribute_in_Inpattern(toString(inpattern) , solution.RHSattributerefinement.get(u+1));
				
		}
		
		// case e e ocliskind of
		
		else if(solution.RHSattributerefinement.get(u).equals("oclIsTypeOf")
				|| solution.RHSattributerefinement.get(u).equals("oclIsKindOf")) {
			
			attr_type=solution.RHSattributerefinement.get(u+2);
				
		}
		
		// case e e attribute
		else if( u< solution.RHSattributerefinement.size()-1) {
		if(solution.RHSattributerefinement.get(u).equals(solution.RHSattributerefinement.get(u+1))
				&& solution.RHSattributerefinement.get(u).length()==1 && !solution.RHSattributerefinement.get(u).equals( fromelement)
				) {
			
			attr_type=find_Type_Attribute_in_Inpattern(attr_type , solution.RHSattributerefinement.get(u+2));
		}
		}
		//case helper
		attr_type=ElementIsHelper(wrapper,solution,u,attr_type);
	    attr_type=ElementIsLazyRule(wrapper,solution,u,attr_type);		
	    
	    return attr_type;
		
	}

	private String ElementIsLazyRule(ATLModel wrapper, Solution solution, int u, String attr_type) {
	
		List<Rule> ruleList = (List<Rule>) wrapper.allObjectsOf(Rule.class);
		for(int ii=0;ii<NSGAIIThreeStep.lazyrulelocation.size();ii++) {
			String lazyruleoutpattern=ruleList.get(NSGAIIThreeStep.lazyrulelocation.get(ii)).getOutPattern().getElements().get(0).getType().getName();

				if(solution.RHSattributerefinement.get(u).equals( ruleList.get(NSGAIIThreeStep.lazyrulelocation.get(ii)).getName())) {
					this.lazychoosen=true;
					attr_type=lazyruleoutpattern;
			}
		}
		
		return attr_type;
	}

	private String ElementIsHelper(ATLModel wrapper, Solution solution, int u, String attr_type) {
		
		List<anatlyzer.atlext.ATL.Helper> helper = (List<Helper>) wrapper
				.allObjectsOf(anatlyzer.atlext.ATL.Helper.class);
		
		
		for (int ii = 0; ii < helper.size(); ii++) {
			String helper_name = helper.get(ii).getDefinition().getFeature() instanceof Operation
					? ((Operation) helper.get(ii).getDefinition().getFeature()).getName()
					: ((Attribute) helper.get(ii).getDefinition().getFeature()).getName();
					if(solution.RHSattributerefinement.get(u).equals(helper_name))
						return (helper.get(ii).getDefinition().getFeature() instanceof Operation
					? toString(((Operation) helper.get(ii).getDefinition().getFeature()).getReturnType())
					: toString(((Attribute) helper.get(ii).getDefinition().getFeature()).getType()));

			}
		return attr_type;
	}

	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,
			ATLModel wrapper, Solution solution, CommonFunctionOperators cp) {
		
		System.out.println("commonfunctionn");
		return null;
	}

	
	

	 
}
