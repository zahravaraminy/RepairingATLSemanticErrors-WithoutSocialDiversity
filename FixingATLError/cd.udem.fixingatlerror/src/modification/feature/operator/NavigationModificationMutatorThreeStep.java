package modification.feature.operator;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import witness.generator.MetaModel;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.InPatternElement;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.OCL.Attribute;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.Operation;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.atl.model.ATLModel;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import jmetal.core.Solution;
import jmetal.metaheuristics.nsgaII.NSGAIIThreeStep;

public class NavigationModificationMutatorThreeStep extends AbstractFeatureModificationMutator {
	int upperbound = 0;
	List<EStructuralFeature> typecandidateattribute = new ArrayList<EStructuralFeature>();
	private boolean econtainerlhs;
	private String newInpattern=null;
	int idnavigation=0;
	int index1 = -1;
	int index2 = -1;
	int indexmax1 = -1;
	int indexmax2 = -1;
	int randomInt2 = -2;
	List<Object> replacements;
	String[] array;
	boolean locatedinfilter = false;
	EDataTypeEList<String> comments = null;
	boolean checkmutationapply = false;
	private ArrayList<Integer> candidaterandomInt2 = new ArrayList<Integer>();
	
	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,
			ATLModel wrapper, Solution solution,CommonFunctionOperators cp) {

		List<Object> ReturnResult = new ArrayList<Object>();
		List<VariableExp> variables = (List<VariableExp>) wrapper.allObjectsOf(VariableExp.class);
		ReturnResult.add(wrapper);
		ReturnResult.add(atlModel);
		String comment = null;
		Module module = wrapper.getModule();
		if (module != null) {
			EStructuralFeature feature = wrapper.source(module).eClass().getEStructuralFeature("commentsAfter");
			comments = (EDataTypeEList<String>) wrapper.source(module).eGet(feature);
		}
		// we will add a comment to the module, documenting the mutation
		int randomInt = -2;	
		ArrayList<Integer> candidaterandomInt = new ArrayList<Integer>();
		while (!checkmutationapply) {

			List<Integer> Result = cp.ReturnFirstIndex(randomInt, variables.size(), checkmutationapply, solution,variables,null);
			randomInt = Result.get(0);
			if(Result.get(2)>0 )
				candidaterandomInt.add(Result.get(2));
			if(!candidaterandomInt.contains( randomInt))
				candidaterandomInt.add(randomInt );
			 this.array = variables.get(randomInt).getLocation().split(":", 2);
			// for crossover, next generation
			if (solution.getpreviousgeneration()) {

				ReturnResult = OperationPreviousGenerationModefyBindingNavigation(randomInt, solution, atlModel,
						inputMM, variables, wrapper, null, comments, ReturnResult, outputMM);
				checkmutationapply = true;

			}
			// for initial population and mutation
			else if (variables.size() > 0 && solution.getpreviousgeneration() == false
					&& Integer.parseInt(this.array[0]) > NSGAIIThreeStep.faultrule.get(0) 
					) {
					
					ReturnResult=NavigationModification(variables,randomInt,wrapper,ReturnResult,
							cp, solution, comment,atlModel);
				   }
			
			if(candidaterandomInt.size()==variables.size())
				checkmutationapply = true;
			}
		return ReturnResult;
	}

private List<Object> NavigationModification(List<VariableExp> variables, int randomInt, ATLModel wrapper, List<Object> ReturnResult, CommonFunctionOperators cp, Solution solution, String comment, EMFModel atlModel) {
		// TODO Auto-generated method stub
		
	   EObject navigationExpression = variables.get(randomInt).eContainer();
	if (navigationExpression instanceof NavigationOrAttributeCallExp) {
		EStructuralFeature featureDefinition = wrapper.source(navigationExpression).eClass()
				.getEStructuralFeature("name");
		String navigation=findOldNavigation(wrapper,navigationExpression,featureDefinition);
		Object object2modify_src3 = findNextNavigation(variables,randomInt,wrapper);// if possible
		int indexrule = -1;
		indexrule = cp.FindRule(this.array);
		findLeftAttributeforNavigation(solution,indexrule);
		EObject oldFeatureValue = cp.FindSpecificInpattern(indexrule, wrapper);
	    this.replacements = this.replacementsnavigation(toString(oldFeatureValue)
				);
		if (!NSGAIIThreeStep.parameterlocation.contains(Integer.parseInt(this.array[0]))) {

			boolean countusing = false;
			this.locatedinfilter=checkIflocatedFilter();
			updatevariables();
			if (indexrule < NSGAIIThreeStep.preconditionlocation.size()) {
				if (NSGAIIThreeStep.preconditionlocation.get(indexrule) > Integer.parseInt(this.array[0])) {
					countusing = true;
				}
			}
			if (!countusing) {
				String typeoutput = null;
				if (this.index2 == -2) {
					typeoutput = "example";

				} else {
					
					typeoutput = findTypeandUpperboundleftSide(this.index1, this.index2, solution);
					// in filter,typeoutput="example";
					if (typeoutput == null)
						typeoutput = "example";
				}
				String typeoldnavigation=ReturnTypeoldNavigation(navigation,wrapper);
				boolean unitfind = false;
				 unitfind =ChooseSecondParameter(unitfind, cp,typeoutput,object2modify_src3,typeoldnavigation
							,navigation,wrapper,indexrule,solution,randomInt);
					
				if (unitfind) {
					
					updatelineattributerefinement(navigation,Integer.parseInt(this.array[0]),solution);
					solution.RHSattributerefinement.set(this.idnavigation, this.replacements.get(this.randomInt2).toString());
					if(Integer.parseInt(this.array[0])< NSGAIIThreeStep.locationfilter.get(indexrule))
					solution.filterSolution=cp.updateFilterRules(solution, indexrule,navigation,this.randomInt2,this.replacements) ;
					wrapper.source(navigationExpression).eSet(featureDefinition,
							this.replacements.get(this.randomInt2));
					cp.StoreTwoIndex(randomInt, this.randomInt2);	
					comments.add(AddCommentOnTransformation(navigationExpression,variables, randomInt,navigation
							));
					ReturnResult=applyChangeCreatingBinding(navigationExpression, variables,randomInt
							,navigation,ReturnResult,comment,wrapper,atlModel);	
					checkmutationapply = true;
					

				}
			}

		}

	}
  return ReturnResult;
	}

private boolean ChooseSecondParameter(boolean unitfind, CommonFunctionOperators cp, String typeoutput,
		Object object2modify_src3, String typeoldnavigation, String navigation, ATLModel wrapper, int indexrule,
		Solution solution, int randomInt) {
	
	this.candidaterandomInt2.clear();
	this.candidaterandomInt2 = new ArrayList<Integer>();
	while (!unitfind) {
		if(this.replacements.size()>0) {
		this.randomInt2 = cp.FindSecondInd(this.randomInt2, this.replacements.size());
		// choose navigation compatible with LHs
		unitfind=LHRisCompatiblewithNavigation(typeoutput,object2modify_src3,typeoldnavigation
				,navigation,wrapper,indexrule,solution,randomInt,cp); 
		if(navigation.equals( this.replacements.get(this.randomInt2).toString()))
			unitfind=false;
		if(!this.candidaterandomInt2.contains( this.randomInt2))
			this.candidaterandomInt2.add(this.randomInt2 );
		if(this.candidaterandomInt2.size()>=this.replacements.size())
			break;
	}
		if(this.replacements.size()==0)
			break;
	}
	
	return unitfind;
}

private String AddCommentOnTransformation(EObject navigationExpression, List<VariableExp> variables, int randomInt, String navigation) {
		// TODO Auto-generated method stub
	return "\n-- MUTATION \"" + this.getDescription() + "\" from "
	+ toString(navigationExpression, variables.get(randomInt)) + navigation + " to "
	+ toString(navigationExpression, variables.get(randomInt))
	+ this.replacements.get(this.randomInt2) + " (line "
	+ ((LocatedElement) navigationExpression).getLocation()
	+ " of original transformation)\n";
	}

private Object findNextNavigation(List<VariableExp> variables, int randomInt, ATLModel wrapper) {
		
	EObject navigationExpression2=null;
	EStructuralFeature featureDefinition2 = null;
	if((randomInt+1)<variables.size()) {
	navigationExpression2 = variables.get(randomInt+1).eContainer();
	featureDefinition2 = wrapper.source(navigationExpression2).eClass()
			.getEStructuralFeature("name");
	}
	if(featureDefinition2!=null) {
	return wrapper.source(navigationExpression2).eGet(featureDefinition2);
	}
		return null;
	}

private String findOldNavigation(ATLModel wrapper, EObject navigationExpression, EStructuralFeature featureDefinition) {
		// TODO Auto-generated method stub
	
	Object object2modify_src2 = wrapper.source(navigationExpression).eGet(featureDefinition);
	return object2modify_src2.toString();
	}

private boolean LHRisCompatiblewithNavigation(String typeoutput, Object object2modify_src3,
			String typeoldnavigation, String navigation, ATLModel wrapper, int indexrule, Solution solution, int randomInt, CommonFunctionOperators cp) {
		
		boolean unitfind=false;
		if (this.locatedinfilter) {
			// toye filter bashe vali hamin khat iterationha bashan
			if (NSGAIIThreeStep.iterationcall.contains(Integer.parseInt(this.array[0]))) {
				unitfind=ifNotStringType(unitfind);
				if (unitfind==true
						&& this.typecandidateattribute.get(this.randomInt2).getUpperBound()==-1) {
					return true;
				}

			} else { // filter, not iteration,  a.isMultiValued
							
				return newNavigationTypeisCompatibleoldType(typeoldnavigation,unitfind,navigation,this.replacements.get(this.randomInt2).toString(),solution,indexrule,cp);
				}
			}
		
		else if (typeoutput.equals("EString") || typeoutput.equals("String") ) {
			    // nex line = next binding, qumecall= contain '', next navigatin=null
				// inpattern name <- a.name + 'Id',
				if(object2modify_src3==null || typeoldnavigation.equals("EString") || typeoldnavigation.equals("String")) {
					unitfind=ifisStringType();
				   if (unitfind==true && this.typecandidateattribute.get(this.randomInt2)
										.getUpperBound() == 1) {
							return true;			
				   }
				}
				else { // oldnavigation not string
					return	unitfind=ifNotBooleanType(unitfind);
					
				}
			}

		else if (typeoutput.equals("EBoolean")) {
		if (this.typecandidateattribute.get(this.randomInt2).getEType().getName()
				.equals("EBoolean"))
			if (!navigation.equals(this.replacements.get(this.randomInt2).toString()))
				return true;
		}	
		// left is not string and not boolean, not filter
		else if (!typeoutput.equals("EString") && !typeoutput.equals("String")
			&& !typeoutput.equals("EBoolean") && this.locatedinfilter == false
			) {
				return NavigationNotBooleanNotString(navigation, solution, wrapper
						, cp, indexrule, unitfind, typeoldnavigation);	
		}

		return false;
	}

private boolean NavigationNotBooleanNotString(String navigation, Solution solution, ATLModel wrapper, CommonFunctionOperators cp, int indexrule, boolean unitfind, String typeoldnavigation) {
	
	// rls: not '' find oldnavigation in wholelineattributerefinement
	updatelineattributerefinement(navigation,Integer.parseInt(this.array[0]),solution);
	// from p:
	List<InPatternElement> modifiable = (List<InPatternElement>) wrapper.allObjectsOf(InPatternElement.class);
	String fromelement=toString(modifiable.get(indexrule));
	boolean find=false;
	// (Class, attribute) e e odlnavigation (change replacement)
	if(!NSGAIIThreeStep.wholelineattributerefinement.get(this.idnavigation-1).equals(fromelement))
		{   
			ArrayList<String> mandatory_attribut_newInpattern = Find_Mandatory_Attribute_New_Inpattern(solution,cp,fromelement);
			this.replacements=findCorrectInpattern(fromelement);
			while(!find) {
				this.randomInt2 = cp.FindSecondInd(this.randomInt2, this.replacements.size());
				if(!this.candidaterandomInt2.contains( this.randomInt2))
					this.candidaterandomInt2.add(this.randomInt2 );
				find=newNavigationTypeisCompatibleoldType(typeoldnavigation,unitfind,navigation,this.replacements.get(this.randomInt2).toString(), solution, indexrule,cp);
				if(find) {
					String type_new_navigation=cp.find_Type_Attribute_in_Inpattern(this.newInpattern , this.replacements.get(this.randomInt2).toString()); 
					for(int i=0;i<mandatory_attribut_newInpattern.size();i++) {
						boolean attr_Accesible=cp.attributeAccesibleInpattern(type_new_navigation, mandatory_attribut_newInpattern.get(i));
						if (!attr_Accesible)
							return false;
						}
					}
				if(this.candidaterandomInt2.size()>=this.replacements.size())
					break;
				}
			return find;
		}
	else { // n.type (replacements=correct)
		// new navigation :not string, not boolean
		unitfind=ifNotBooleanType(unitfind);
		if(unitfind)
			unitfind=ifNotStringType(unitfind);
		if (unitfind) {
				// if line contains (select, collect), must upper=-1 
			if (NSGAIIThreeStep.iterationcall.contains(Integer.parseInt(this.array[0]))) {
				if (
						this.typecandidateattribute.get(this.randomInt2)
									.getUpperBound() == -1)
						return true;
				else
						return false;
		
				} else {
					// not iteration,econtainer lhs== econtainer rhs 
				 unitfind=compareContaineroldnewnavigation();
				 if ( unitfind==true &&
						this.typecandidateattribute.get(this.randomInt2)
							.getLowerBound() != 0 )
					 	return true;
					}
			}
 	}

	return false;
}

private ArrayList<String> Find_Mandatory_Attribute_New_Inpattern(Solution solution, CommonFunctionOperators cp,
		String fromelement) {
	// TODO Auto-generated method stub
	
	ArrayList<String> mandatory_attribut_newInpattern = new ArrayList<String>();
	for(int k=this.idnavigation+1;k<solution.RHSattributerefinement.size();k++)
	{
		int foo=cp.NextElementofRHSattribute(solution,k);
		if(solution.RHSattributelocation.contains( foo) || solution.RHSattributerefinement.get(k).equals(fromelement) ) { // if element is integer (reach next binding), 
			break;
		}
		else {
			if( k< solution.RHSattributerefinement.size()-1)
			if(solution.RHSattributerefinement.get(k).equals(solution.RHSattributerefinement.get(k+1))
					&& solution.RHSattributerefinement.get(k).length()==1 
					) {
				mandatory_attribut_newInpattern.add(solution.RHSattributerefinement.get(k+2) );
				}
		}
	}
	return mandatory_attribut_newInpattern;
}

private void updatevariables() {
		// TODO Auto-generated method stub
	if (this.index1 == -1 && this.index2 == -1) {
		this.index1 = this.indexmax1;
		this.index2 = this.indexmax2 - 1;

	}
	}

private void findLeftAttributeforNavigation(Solution solution, int indexrule) {
		// TODO Auto-generated method stub
		
	for (int i = 0; i < solution.getCoSolutionThreeStep().getOp().listpropertynamelocation
			.size(); i++) {
		
		for (int j = 0; j < solution.getCoSolutionThreeStep().getOp().listpropertynamelocation
				.get(i).size(); j++) {
			if(j+1 <solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(i).size())
			{// compare line with each LHS line: if is >= lhs(j) but < next LHS
			if( Integer.parseInt(this.array[0]) >=solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(i).get(j) 
					&& Integer.parseInt(this.array[0])<solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(i).get(j+1)
					&& Integer.parseInt(this.array[0])> NSGAIIThreeStep.locationfilter.get(indexrule)) {
			 // if line is only in binding assign index1, index2 else put -1
				this.index1 = i;
				this.index2 = j;
				break;
			}
		}
		else if(j+1 ==solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(i).size()
				) {
			if( Integer.parseInt(this.array[0]) >=solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(i).get(j) 
				&& Integer.parseInt(this.array[0])> NSGAIIThreeStep.locationfilter.get(indexrule) ){
				// line is not in filter, only in binding 
			this.index1 = i;
			this.index2 = solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(i).size()-1;
			break;
			}}
		
		}
	}
}

private boolean checkIflocatedFilter() {
		// TODO Auto-generated method stub
	for (int i = 0; i < NSGAIIThreeStep.locationfrom.size(); i++) {
		if (Integer.parseInt(this.array[0]) > NSGAIIThreeStep.locationfrom.get(i)
				&& Integer.parseInt(this.array[0]) < NSGAIIThreeStep.locationfilter.get(i))
			return true;

	}
		return false;
	}

private boolean newNavigationTypeisCompatibleoldType(String typeoldnavigation, boolean unitfind, String navigation, String newnavigation, Solution solution, int indexrule
		, CommonFunctionOperators cp) {

// TODO Auto-generated method stub
	if(typeoldnavigation!=null) {
		if(typeoldnavigation.equals("BooleanType") || typeoldnavigation
				.equals("EBoolean"))
			{ // boolean
				return ifisBooleanType(unitfind);
			}
		else { // filter, not iteration, not boolean
				unitfind=ifNotStringType(unitfind);// new navigation not string (filter)
				if(unitfind)
					unitfind=ifNotBooleanType(unitfind);// new navigation not boolean(filter)
				if(unitfind)
					unitfind= notEqualOldnavigation(navigation,newnavigation);
				if(unitfind)
				{	String str=solution.filterSolution.get(indexrule );	
					if(!str.equals("empty")) {	
						String replacestr=str.replace(navigation, newnavigation);
						return cp.newFilterCompatibleOtherRules(indexrule,navigation,newnavigation,solution,str,replacestr);
					}
					else
						return true;
				}
       
			}
		}
		return false;
	}

	private List<Object> findCorrectInpattern(String fromelement) {
		List<Object> newreplacement = new ArrayList<Object>();
		
		for(int i=this.idnavigation-1;i>0;i--) {
			
			//   attribute of source
			if(NSGAIIThreeStep.totallistattribute.contains( NSGAIIThreeStep.wholelineattributerefinement.get(i))) {
				CommonFunctionOperators cf=new CommonFunctionOperators();
				String className=cf.findClassNameattribute(NSGAIIThreeStep.wholelineattributerefinement.get(i));
				if(className!=null) {
				 newreplacement = this.replacementsnavigation( className
						);
				 this.newInpattern=className;
				return newreplacement;
				}
		
			}
			// class of source
			else if(NSGAIIThreeStep.classnamestring.contains( NSGAIIThreeStep.wholelineattributerefinement.get(i))) {
				
				newreplacement = this.replacementsnavigation(NSGAIIThreeStep.wholelineattributerefinement.get(i)
						);
				this.newInpattern=NSGAIIThreeStep.wholelineattributerefinement.get(i);
				return newreplacement;
					
			}
		}
		
		return null;
	}

	private void updatelineattributerefinement(String navigation, int line, Solution solution) {
		// TODO Auto-generated method stub
		int id=solution.RHSattributerefinement.indexOf( Integer.toString(line));
		for(int i=id+1;i<solution.RHSattributerefinement.size();i++) {
			if(solution.RHSattributerefinement.get(i).equals(navigation)) {
				this.idnavigation=i;
				break;
			}
			
		}
		
	}

	private boolean compareContaineroldnewnavigation() {
		// TODO Auto-generated method stub
		if (this.econtainerlhs==
		 ((EReference) this.typecandidateattribute.get(this.randomInt2)).isContainer())
		     return true;
		return false;
	}

	private boolean notEqualOldnavigation(String navigation, String newnavigation) {
		// TODO Auto-generated method stub
		if (!navigation.equals(newnavigation))
			return true;
		return false;
	}

	private String ReturnTypeoldNavigation(String navigation, ATLModel wrapper) {
		// TODO Auto-generated method stub
		
			CommonFunctionOperators cf=new CommonFunctionOperators();
		       String typeattribute=cf.findAttributeType(navigation);
			
			List<anatlyzer.atlext.ATL.Helper> helper = (List<Helper>) wrapper
					.allObjectsOf(anatlyzer.atlext.ATL.Helper.class);
			
			for (int i = 0; i < helper.size(); i++) {
			String helper_name = helper.get(i).getDefinition().getFeature() instanceof Operation
					? ((Operation) helper.get(i).getDefinition().getFeature()).getName()
					: ((Attribute) helper.get(i).getDefinition().getFeature()).getName();
					if(navigation.equals(helper_name))
						 return (helper.get(i).getDefinition().getFeature() instanceof Operation
					? toString(((Operation) helper.get(i).getDefinition().getFeature()).getReturnType())
					: toString(((Attribute) helper.get(i).getDefinition().getFeature()).getType()));

			}
		return typeattribute;
	}

	private List<Object> applyChangeCreatingBinding(EObject navigationExpression, List<VariableExp> variables,
			int randomInt, String navigation, List<Object> ReturnResult, String comment, ATLModel wrapper, EMFModel atlModel) {
		// TODO Auto-generated method stub
		
		comment = "\n-- MUTATION \"" + this.getDescription() + "\" from "
				+ toString(navigationExpression, variables.get(randomInt)) + navigation + " to "
				+ toString(navigationExpression, variables.get(randomInt))
				+ this.replacements.get(this.randomInt2) + " (line "
				+ ((LocatedElement) navigationExpression).getLocation()
				+ " of original transformation)\n";
		
		NSGAIIThreeStep.numop = NSGAIIThreeStep.numop + 1;
		ReturnResult.set(0, wrapper);
		ReturnResult.set(1, atlModel);
		ReturnResult.add(comment);
		
		return ReturnResult;
	}

	private boolean ifisStringType() {
		// TODO Auto-generated method stub
	
		if (this.typecandidateattribute.get(this.randomInt2).getEType().getName()
						.equals("String")
						|| this.typecandidateattribute.get(this.randomInt2).getEType().getName()
								.equals("EString")) {
			return true;

		}
		return false;
	}
	private boolean ifisBooleanType(boolean unitfind) {
		// TODO Auto-generated method stub
	
		if (this.typecandidateattribute.get(this.randomInt2).getEType().getName()
						.equals("Boolean")
						|| this.typecandidateattribute.get(this.randomInt2).getEType().getName()
								.equals("EBoolean")) {
			return true;

		}
		return false;
	}
	private boolean ifNotBooleanType( boolean unitfind) {
		// TODO Auto-generated method stub
	
		if (!this.typecandidateattribute.get(this.randomInt2).getEType().getName()
						.equals("Boolean")
						&& !this.typecandidateattribute.get(this.randomInt2).getEType().getName()
								.equals("EBoolean")) {
			return true;

		}
		return false;
	}

	private boolean ifNotStringType( boolean unitfind) {
		// TODO Auto-generated method stub
		
		if (!this.typecandidateattribute.get(this.randomInt2).getEType().getName()
				.equals("String")
				&& !this.typecandidateattribute.get(this.randomInt2).getEType().getName()
						.equals("EString")
						) {
			return true;

		}
		return false;
	}

	private String findTypeandUpperboundleftSide(int index1, int index2, Solution solution) {
		// TODO Auto-generated method stub

		String typeoutput = null;
		for (int i = 0; i < NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.size(); i++)
			
			{ //LHs<-  : Type and Upper bound LHS 
			if (solution.getCoSolutionThreeStep().getOp().listpropertyname.get(index1).get(index2)
					.equals(NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(i).getName())) {
				typeoutput = NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(i).getEType().getName();
				this.upperbound = NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(i).getUpperBound();
				if (!typeoutput.equals("EString") && !typeoutput.equals("String"))
				this.econtainerlhs=((EReference) NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(i)).isContainer();

			}
	}
		return typeoutput;
	}

	private List<Object> OperationPreviousGenerationModefyBindingNavigation(int randomInt, Solution solution,
			EMFModel atlModel, MetaModel inputMM, List<VariableExp> variables, ATLModel wrapper, Object object,
			EDataTypeEList<String> comments, List<Object> ReturnResult, MetaModel outputMM) {
	
			EObject navigationExpression = variables.get(randomInt).eContainer();
		if (navigationExpression instanceof NavigationOrAttributeCallExp) {
			CommonFunctionOperators cf=new CommonFunctionOperators();
			EStructuralFeature featureDefinition = wrapper.source(navigationExpression).eClass()
					.getEStructuralFeature("name");
			Object object2modify_src2 = wrapper.source(navigationExpression).eGet(featureDefinition);
			String navigation = object2modify_src2.toString(); //oldnavigation
			this.array = variables.get(randomInt).getLocation().split(":", 2);
			boolean locatedinfilter=checkIflocatedFilter();
			EObject inpattern = null ;
			List<InPatternElement> modifiable = (List<InPatternElement>) wrapper
						.allObjectsOf(InPatternElement.class);
			int indexrule = -1;
			indexrule = cf.FindRule(this.array);
			EStructuralFeature featureDefinition2 = wrapper.source(modifiable.get(indexrule)).eClass()
						.getEStructuralFeature("type");
			EObject object2modifyinpattern2 = wrapper.source(modifiable.get(indexrule));
			inpattern = (EObject) object2modifyinpattern2.eGet(featureDefinition2);//inpattern
			String comment = null;
			updatelineattributerefinement(navigation,Integer.parseInt(this.array[0]),solution);
			this.replacements = this.replacementsnavigation( toString(inpattern));
			this.randomInt2 = -2;
			this.randomInt2 =cf. FindSecondIndex(this.randomInt2, this.replacements.size());
			String fromelement=toString(modifiable.get(indexrule));
			// (Class, attribute) e e odlnavigation (change replacement)
			if(this.idnavigation>0)
			if(!NSGAIIThreeStep.wholelineattributerefinement.get(this.idnavigation-1).equals(fromelement))
			  {   // new replacements
				
				  this.replacements=findCorrectInpattern(fromelement);
				  this.randomInt2 = cf.FindSecondIndex(this.randomInt2, this.replacements.size());
			  
			 }
			
			boolean check = false;
			 if(locatedinfilter) {
				 String str=solution.filterSolution.get(indexrule);	
					if(!str.equals("empty")) {	
					String replacestr=str.replace(navigation, this.replacements.get(this.randomInt2).toString());
					check= cf.newFilterCompatibleOtherRules(indexrule,navigation,this.replacements.get(this.randomInt2).toString(),solution,str,replacestr);
					}
					
			 }
			
			if (!check) {
					wrapper.source(navigationExpression).eSet(featureDefinition, this.replacements.get(this.randomInt2));
					cf.StoreTwoIndex(randomInt, this.randomInt2);
					solution.RHSattributerefinement.set(this.idnavigation, this.replacements.get(this.randomInt2).toString());
					if(Integer.parseInt(this.array[0])< NSGAIIThreeStep.locationfilter.get(indexrule))
						solution.filterSolution=cf.updateFilterRules(solution, indexrule,navigation,this.randomInt2,this.replacements) ;
					comments.add(AddCommentOnTransformation(navigationExpression, variables, randomInt, navigation));
					ReturnResult=applyChangeCreatingBinding(navigationExpression, variables,randomInt
						,navigation,ReturnResult,comment,wrapper,atlModel);
				
			} else {

				cf.setvariable(-2, -2, -2);

			}

		}
		return ReturnResult;
	}

	
	private List<Object> replacementsnavigation( String inpattern
			) {

		List<Object> mainclass = new ArrayList<Object>();
		CommonFunctionOperators cp = new CommonFunctionOperators();
		this.typecandidateattribute = cp.candidateReferenceSpecificInpattern((inpattern));
		for (EStructuralFeature o : this.typecandidateattribute) {
			if (o != null) {
				mainclass.add(o.getName());
				
			}
		}

		// TODO Auto-generated method stub
		return mainclass;
	}

	@Override
	public String getDescription() {
		return "Navigation Modification";
	}

	/**
	 * @param containee
	 * @param container
	 */
	private String toString(EObject container, EObject containee) {
		EObject next = containee;
		String string = "";
		do {
			string += toString(next) + ".";
			next = next.eContainer();
		} while (next != container && next != null);
		return string;
	}

	@Override
	protected List<Object> replacements(EMFModel atlModel, EClass oldFeatureValue, EObject object2modify,
			String currentAttributeValue, MetaModel metamodel) {
		// TODO Auto-generated method stub

		return null;
	}

}
