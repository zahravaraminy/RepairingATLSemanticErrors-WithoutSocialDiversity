package modification.type.operator.abstractclass;



// REMARKS
// => any subtype of a class is compatible with the class
// => no class has incompatible supertypes
// => the information of the static analysis could be used to select an incompatible
//    class of interest, e.g. a class that does not define a feature used by the rule

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import witness.generator.MetaModel;
import anatlyzer.atlext.ATL.ForEachOutPatternElement;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.InPatternElement;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atlext.OCL.CollectionType;
import anatlyzer.atlext.OCL.LoopExp;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.OclModelElement;
import anatlyzer.atlext.OCL.PropertyCallExp;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.atl.model.ATLModel;
import evaluation.mutator.AbstractMutator;
import cd.udem.fixingatlerror.Class2Rel;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import cd.udem.fixingatlerror.OperationsThreeStep;
import cd.udem.fixingatlerror.Setting;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.IntSolutionTypeThreeStep;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.nsgaII.NSGAIIThreeStep;
import jmetal.problems.MyProblemThreeStep;
import transML.exceptions.transException;

public abstract class AbstractTypeModificationMutatorThreeStep extends AbstractMutator {

	/**
	 * Generic type modification. It allows subtypes of the container class.
	 * 
	 * @param atlModel
	 * @param outputFolder
	 * @param ToModifyClass container class of the class of objects to modify
	 *                      (example Parameter)
	 * @param featureName   feature to modify (example type)
	 * @param metamodel     metamodel containing the candidate types for the
	 *                      modification
	 */

	
	private int indextype = -1;
	List<String> listattrforcheck = new ArrayList<String>();
	private boolean type_next_attr=false;
	private String type_next_operation;
	private EDataTypeEList<String> comments ;
	private boolean checkmutationapply = false;
	
	protected <ToModify extends LocatedElement> List<Object> genericTypeModification(EMFModel atlModel,
			String outputFolder, Class<ToModify> ToModifyClass, String featureName, MetaModel[] metamodels,
			ATLModel wrapper, Solution solution, MetaModel metamodels1, MetaModel metamodels2,CommonFunctionOperators cp) {
		return this.genericTypeModification(atlModel, outputFolder, ToModifyClass, featureName, metamodels, false,
				wrapper, solution, metamodels1, metamodels2,cp);

	}

	protected <ToModify extends LocatedElement> List<Object> genericTypeModification(EMFModel atlModel,
			String outputFolder, Class<ToModify> ToModifyClass, String featureName, MetaModel[] metamodels,
			boolean exactToModifyType, ATLModel wrapper, Solution solution, MetaModel metamodels1,
			MetaModel metamodels2,CommonFunctionOperators cp) {
		
		// operations 6,7,1; InElement,OutElement/Argument
		List<Object> ReturnResult = new ArrayList<Object>();// ToModify: List outpatternElement
		List<ToModify> modifiable = (List<ToModify>) wrapper.allObjectsOf(ToModifyClass);	
		ReturnResult.add(wrapper);
		ReturnResult.add(atlModel);
		
		if (modifiable.size() > 0) {

			if(OperationsThreeStep.statecase.equals("case7"))
				filterSubtypes(modifiable, ToModifyClass);
			this.comments = cp.ReturnInitialComments(wrapper);	
			int randomInt = -2;
			String comment = null;
			ArrayList<Integer> candidaterandomInt = new ArrayList<Integer>();
			while (!checkmutationapply ) {
				
				updatestaticvariables();
				List<Integer> Result = cp.ReturnFirstIndex(randomInt, modifiable.size(), checkmutationapply,
						solution,null,modifiable);
				randomInt = Result.get(0);	
				this.type_next_attr=false;
				this.type_next_operation="non";
				boolean runable=false;
				if(Result.get(2)>0 )
				{
					candidaterandomInt.add(Result.get(2));
					runable=true;
				}
				if(!candidaterandomInt.contains( randomInt))
				{
					candidaterandomInt.add(randomInt );
					runable=true;
				}
				
				if (solution.getpreviousgeneration()) { // for crossover
					
					ReturnResult = OperationPreviousGeneration(randomInt, solution, atlModel, metamodels, modifiable,
							wrapper, featureName, this.comments, ReturnResult, metamodels1, metamodels2, cp);
					this.checkmutationapply = true;
				}
				// for initial population, mutation
				else if (solution.getpreviousgeneration() == false && runable==true) {
					
					String[] array =null;
					array = modifiable.get(randomInt).getLocation().split(":", 2);
					int indexrule = -1;
					indexrule = cp.FindRule(array);
					boolean ch=true;
					if(OperationsThreeStep.statecase.equals("case7") )
						  ch=false;
					if (modifiable.size() > 0 && (indexrule >= 0)
							  && ch==true) {

						EStructuralFeature featureDefinition = wrapper.source(modifiable.get(randomInt)).eClass()
								.getEStructuralFeature(featureName);
						ReturnResult=InOutPatternElementModification(featureDefinition,wrapper,modifiable, randomInt
								,metamodels, atlModel, comment,solution, cp,candidaterandomInt,ReturnResult, metamodels1, metamodels2,indexrule );
						ReturnResult=ArgumentModification(featureDefinition,wrapper,modifiable,randomInt,cp,
								metamodels, atlModel, comment,solution,candidaterandomInt,ReturnResult, metamodels1, metamodels2,array, indexrule);
		
					}
				}
				if(candidaterandomInt.size()>=modifiable.size())
					this.checkmutationapply = true;
			}

		}
		return ReturnResult;
		
	}

	
	private <ToModify extends LocatedElement> List<Object> ArgumentModification(EStructuralFeature featureDefinition, ATLModel wrapper,
			List<ToModify> modifiable, int randomInt, CommonFunctionOperators cp, MetaModel[] metamodels,
			EMFModel atlModel, String comment, Solution solution, ArrayList<Integer> candidaterandomInt,
			List<Object> ReturnResult, MetaModel metamodels1, MetaModel metamodels2, String[] array, int indexrule) {
		
		if (featureDefinition != null && OperationsThreeStep.statecase.equals("case7")) {
			// 7:Operation Argument Type Modification for OCLISKindof
			
				List<EObject> value = (List<EObject>) wrapper.source(modifiable.get(randomInt))
						.eGet(featureDefinition);
				if (value.size() == 0)
					this.checkmutationapply = false;
				if (value.size() > 0) {
					int randomInt3 = cp.calculaterandomInt2(value);
					EObject oldFeatureValue2 = value.get(randomInt3);
					boolean nextisattribute = IfNextElementIsAttribute(oldFeatureValue2, array,solution);
					if (oldFeatureValue2.eClass().getEPackage().getEClassifier("OclModelElement")
							.isInstance(oldFeatureValue2)) {
						
						List<EObject> replacements = this.replacements(atlModel,
								(EObject) oldFeatureValue2, metamodels);
						boolean findreplace = false;
						int randomInt4 = -1;
						ArrayList<Integer> candidaterandomInt2 = new ArrayList<Integer>();
						while (!findreplace) {
							randomInt4 = cp.calculaterandomIn(replacements);
							candidaterandomInt2=AddCandidateReplacementtoCandidatelist(candidaterandomInt2,
									randomInt4);
							findreplace = ChooseCorrectReplacementElement(randomInt4, findreplace,
									oldFeatureValue2, replacements, nextisattribute,indexrule, solution,wrapper,array);
							findreplace=NewInOutPatternNotEqualOldpattern(replacements,randomInt4,findreplace,oldFeatureValue2);
							if(candidaterandomInt2.size()>=replacements.size())
								break;
						}

						
						if (findreplace) {
							
						UpdateArgumrntParameters(replacements,randomInt4, cp, randomInt, randomInt3,oldFeatureValue2,value, indexrule );
						this.comments.add(AddNewCommentsOnTransformation(modifiable,randomInt,oldFeatureValue2,replacements,randomInt4 ));						
						if (this.indextype > 0) // put newType in RHSattribute
							solution.RHSattributerefinement.set(this.indextype,
									toString(replacements.get(randomInt4)));
						ReturnResult = applyChange(randomInt, modifiable, ReturnResult, replacements,
								randomInt4, oldFeatureValue2, comment, wrapper, atlModel);
						this.checkmutationapply = true;
						} 
						
					}
				}
		}

		return ReturnResult;
	}

	private ArrayList<Integer> AddCandidateReplacementtoCandidatelist(ArrayList<Integer> candidaterandomInt2,
			int randomInt4) {
		
		if(!candidaterandomInt2.contains( randomInt4))
			candidaterandomInt2.add(randomInt4 );
		return candidaterandomInt2;
	}

	private void UpdateArgumrntParameters(List<EObject> replacements, int randomInt4, CommonFunctionOperators cp,
			int randomInt, int randomInt3, EObject oldFeatureValue2, List<EObject> value, int indexrule) {
		
		NSGAIIThreeStep.argumentlist.add(String.valueOf(indexrule));
		NSGAIIThreeStep.argumentlist.add(toString(replacements.get(randomInt4)));
		cp.setvariable(randomInt, randomInt4, randomInt3);
		copyFeatures(oldFeatureValue2, replacements.get(randomInt4));
		value.set(randomInt3, replacements.get(randomInt4));
		
		
	}

	private <ToModify extends LocatedElement> List<Object> InOutPatternElementModification(EStructuralFeature featureDefinition, ATLModel wrapper,
			List<ToModify> modifiable, int randomInt, MetaModel[] metamodels, EMFModel atlModel, String comment,
			Solution solution, CommonFunctionOperators cp, ArrayList<Integer> candidaterandomInt,
			List<Object> ReturnResult,MetaModel metamodels1, MetaModel metamodels2, int indexrule) {
		
		if (featureDefinition != null && featureDefinition.getUpperBound() == 1 && (OperationsThreeStep.statecase.equals("case6")
				|| OperationsThreeStep.statecase.equals("case1"))) {
			
			EObject object2modify_src = wrapper.source(modifiable.get(randomInt));
			EObject oldFeatureValue = (EObject) object2modify_src.eGet(featureDefinition);
			boolean choosen_outpattern=true;
			if(OperationsThreeStep.statecase.equals("case1"))
			  choosen_outpattern=cp.Check_choosen_Outpattern_allow_for_Modification(solution,oldFeatureValue,wrapper,indexrule);	
			if( (OperationsThreeStep.statecase.equals("case1") && choosen_outpattern==true) ||  !OperationsThreeStep.statecase.equals("case1") )
			{	
			boolean findreplace = false;
			List<EObject> replacements = this.replacements(atlModel, (EObject) oldFeatureValue,
					metamodels);// all inpattern/outpattern class
				
			int randomInt2 = -2;
			ArrayList<Integer> candidaterandomInt2 = new ArrayList<Integer>();
			while (!findreplace) {
				
				randomInt2 = cp.FindSecondIndex(randomInt2, replacements.size()); // choose candidate outpattern
				candidaterandomInt2=AddCandidateReplacementtoCandidatelist(candidaterandomInt2,
						randomInt2);
				if (OperationsThreeStep.statecase.equals("case6")) { // In Pattern Modification
					
					findreplace=TestCandidateInPatternonTransformation(wrapper,metamodels1,metamodels2,indexrule,replacements,randomInt2,oldFeatureValue);
					
				} else {// for Outpatttern Element Modification
					
					findreplace=TestCandidateOutPatternonTransformation(randomInt,indexrule,solution,replacements, randomInt2,oldFeatureValue,findreplace, cp, wrapper);
				}
				findreplace=NewInOutPatternNotEqualOldpattern(replacements,randomInt2,findreplace,oldFeatureValue);	
				if(candidaterandomInt2.size()==replacements.size())
					break;
				
			}
			//Apply In/Out Pattern on Transformation
			if ( findreplace==true &&  
					 ( NSGAIIThreeStep.mandatorycreationoperation.size()==0 || 
							(MyProblemThreeStep.indexoperation+NSGAIIThreeStep.mandatorycreationoperation.size()+NSGAIIThreeStep.mandatorydeletionattribute.size())<=IntSolutionTypeThreeStep.max_operations_size)) { // is not last operation	
				
				UpdateTransformation(oldFeatureValue, replacements, randomInt2, randomInt, featureDefinition , object2modify_src, cp);
				this.comments.add(AddNewCommentsOnTransformation(modifiable,randomInt,oldFeatureValue,replacements,randomInt2 ));
				ReturnResult = applyChange(randomInt, modifiable, ReturnResult, replacements,
						randomInt2, oldFeatureValue, comment, wrapper, atlModel);
				this.checkmutationapply = true;
			} 
			this.checkmutationapply=UpdateMandatoryCreationStatus(this.checkmutationapply);					
			if(candidaterandomInt.size()==modifiable.size()) {
				this.checkmutationapply = true;
			}
		}

		}
		return ReturnResult;
	}

	private boolean UpdateMandatoryCreationStatus(boolean checkmutationapply) {
		
		if( (
				(MyProblemThreeStep.indexoperation+NSGAIIThreeStep.mandatorycreationoperation.size()+NSGAIIThreeStep.mandatorydeletionattribute.size())>IntSolutionTypeThreeStep.max_operations_size)) { // when inpatten modification cannot be applied
			 	 checkmutationapply = true;
				 NSGAIIThreeStep.mandatorycreationoperation.clear();
				 NSGAIIThreeStep.mandatorydeletionattribute.clear();
		
		}
		return checkmutationapply;
	}

	private void UpdateTransformation(EObject oldFeatureValue, List<EObject> replacements, int randomInt2,
			int randomInt, EStructuralFeature featureDefinition, EObject object2modify_src, CommonFunctionOperators cp) {
		
		copyFeatures(oldFeatureValue, replacements.get(randomInt2));
		cp.StoreTwoIndex(randomInt, randomInt2);
		object2modify_src.eSet(featureDefinition, replacements.get(randomInt2));
		
		
	}

	
	private <ToModify extends LocatedElement>  String AddNewCommentsOnTransformation(List<ToModify> modifiable, int randomInt, EObject oldFeatureValue,
			List<EObject> replacements, int randomInt2) {
	
		return "\n-- MUTATION \"" + this.getDescription() + "\" from "
		+ toString(modifiable.get(randomInt)) + ":" + toString(oldFeatureValue) + " to "
		+ toString(modifiable.get(randomInt)) + ":"
		+ toString(replacements.get(randomInt2)) + " (line "
		+ modifiable.get(randomInt).getLocation() + " of original transformation)\n";
	}

	private boolean TestCandidateOutPatternonTransformation(int randomInt, int indexrule, Solution solution,
			List<EObject> replacements, int randomInt2, EObject oldFeatureValue, boolean findreplace, CommonFunctionOperators cp, ATLModel wrapper) {
		
		findreplace=outParrenModification(randomInt,indexrule,solution,replacements, randomInt2,toString(oldFeatureValue),findreplace);
		
		Setting s=new Setting();
		findreplace=IfNewOutpatternSuperclassOtherrules(s,cp,wrapper,replacements,randomInt2, indexrule);  	
    	return findreplace;
	}

	private boolean IfNewOutpatternSuperclassOtherrules(Setting s, CommonFunctionOperators cp, ATLModel wrapper,
			List<EObject> replacements, int randomInt2, int indexrule) {
		
				boolean issuperclass=false;
				boolean enter=false;
				String str2=s.getruleName().get( indexrule);
				for(int i=0; i<s.getextendsruleName().size();i++) {
					if(s.getextendsruleName().get(i).equals(str2)) {
						enter=true;
						EObject outpatternObject=cp.FindSpecificOutpattern(i, wrapper);
						String outpatternElement=toString(outpatternObject);
						System.out.println(outpatternElement);	
						issuperclass=NewOutPatternSuperClassSpecificRule(replacements, randomInt2, s,outpatternElement );
					}
				}
				if(!issuperclass && enter)
					return false;
				String str=s.getextendsruleName().get( indexrule);
				int indexof=s.getruleName().indexOf( str);
    			EObject outpatternObject=cp.FindSpecificOutpattern(indexof, wrapper);
    			String outpatternElement=toString(outpatternObject);
    			issuperclass=NewOutPatternSuperClassSpecificRule(replacements, randomInt2, s,outpatternElement );
    			
    			
    					
		return issuperclass;
	}

	private boolean NewOutPatternSuperClassSpecificRule(List<EObject> replacements, int randomInt2, Setting s,
			String outpatternElement) {
		
		for(int i=0;i<s.subcalss_supperclasstarget.size();i++) {	
			System.out.println(toString(replacements.get(randomInt2)));
				if(s.subcalss_supperclasstarget.get(i).get(0).equals( toString(replacements.get(randomInt2)))) {
				
					for(int j=1; j<s.subcalss_supperclasstarget.get(i).size();j++) {
						if(s.subcalss_supperclasstarget.get(i).get(j).equals( outpatternElement)
								&& !NSGAIIThreeStep.abstractclassname.contains(toString(replacements.get(randomInt2)) )) {
							return true;
						}
				}
					return false;
			}
		}
		return false;
	}

	private boolean NewInOutPatternNotEqualOldpattern(List<EObject> replacements, int randomInt2, boolean findreplace,
			EObject oldFeatureValue) {
		
		if(toString(oldFeatureValue).equals(toString(replacements.get(randomInt2))) && findreplace==true)
			return false;
		return findreplace;
	}

	private boolean TestCandidateInPatternonTransformation(ATLModel wrapper, MetaModel metamodels1,
			MetaModel metamodels2, int indexrule, List<EObject> replacements, int randomInt2,
			EObject oldFeatureValue) {
		
		// if iterations operation exits, return all attribute (navigation) for it 
		List<String> ManyCardinarityAttribute=findAttributewithManyCardinality(wrapper,metamodels1,metamodels2,indexrule);	
		boolean findreplace=checkReplacementChooseCorrectly(indexrule,replacements,randomInt2,oldFeatureValue);	
		// iteration operation exist, cardinality inpattern must>1 
		if (ManyCardinarityAttribute!=null) {
			if(ManyCardinarityAttribute.size()>0)
			findreplace=checkUpperboundAttributeNewInpattern(replacements,randomInt2,ManyCardinarityAttribute
					,findreplace);
		}
		return findreplace;
	}

	private void updatestaticvariables() {
		
		NSGAIIThreeStep.mandatorycreationoperation.clear();
		NSGAIIThreeStep.mandatorycreationoperation = new ArrayList<String>();
		NSGAIIThreeStep.mandatorycreationlocation.clear();
		NSGAIIThreeStep.mandatorycreationlocation = new ArrayList<Integer>();
		NSGAIIThreeStep.mandatorydeletionattribute.clear();
		NSGAIIThreeStep.mandatorydeletionattribute = new ArrayList<String>();
		NSGAIIThreeStep.mandatorydeletionlocation.clear();
		NSGAIIThreeStep.mandatorydeletionlocation = new ArrayList<Integer>();
		
	}

	private boolean checkUpperboundAttributeNewInpattern(List<EObject> replacements, int randomInt2,
			List<String> manyCardinarityAttribute, boolean findreplace) {
	

		// find inpattern , if upper bound==1 return false
		int indexclassname = NSGAIIThreeStep.classnamestring
				.indexOf(toString(replacements.get(randomInt2)));

		for (int j = 0; j < manyCardinarityAttribute.size(); j++) {
			for (int i = NSGAIIThreeStep.classnamestartpoint
					.get(indexclassname); i < NSGAIIThreeStep.classnamestartpoint
							.get(indexclassname)
							+ NSGAIIThreeStep.classnamelength
									.get(indexclassname); i++) {
				if (NSGAIIThreeStep.listinheritattributesourcemetamodel.get(i).getName()
						.equals(manyCardinarityAttribute.get(j))
						&& NSGAIIThreeStep.listinheritattributesourcemetamodel.get(i)
								.getUpperBound() == 1) {
					findreplace=false;

				}
			}

		}
		
		return findreplace;
	}

	private boolean checkReplacementChooseCorrectly(int indexrule, List<EObject> replacements, int randomInt2,
			EObject oldFeatureValue) {
		
		boolean correctfind=false;
		// there is filter in the rule
		if(indexrule>=0)
		if (NSGAIIThreeStep.inpatternhasfilter.get(indexrule) == 1) {

			if (!NSGAIIThreeStep.inpatternstringlocation
						.contains(toString(replacements.get(randomInt2))))
					correctfind = true;
				else {
					//in filter, new in pattern is equal with one of existing inpattern, 
					// existing inpattern is also in filter,  
					int indexof = NSGAIIThreeStep.inpatternstringlocation
							.indexOf(toString(replacements.get(randomInt2)));
				
						correctfind = true;
					  }
			} else { // no filter, new inpattern not equal with existing inpattern and their superclassess

					if(!toString(oldFeatureValue).equals(toString(replacements.get(randomInt2))))
					{
						if ( !NSGAIIThreeStep.inpatternstringlocation
												.contains(toString(replacements.get(randomInt2))))
							if(!NSGAIIThreeStep.lazyrulelocation.contains(indexrule)
									&& !NSGAIIThreeStep.abstractsourceclassname.contains(toString(replacements.get(randomInt2))))	
							{
					
							boolean superclassavail=SuperClassofClassEqualExistingInpatterns(toString(replacements.get(randomInt2)));				
							if(!superclassavail)
								correctfind = true;
							}
					}
				else {
				
				correctfind = true;
					}

			}

		return correctfind;
	}

	private boolean SuperClassofClassEqualExistingInpatterns(String newinpattern) {
		
		boolean superclassavail=false;
		for(int i=0; i<NSGAIIThreeStep.subcalss_supperclass.size();i++) {
			if(NSGAIIThreeStep.subcalss_supperclass.get(i).get(0).equals(newinpattern)
					) {
				
				for(int j=1; j<NSGAIIThreeStep.subcalss_supperclass.get(i).size();j++ ) {
					 
					if( NSGAIIThreeStep.inpatternstringlocation.contains(NSGAIIThreeStep.subcalss_supperclass.get(i).get(j) )
							 ) {
						return true;
					}
				}
				
			}
			
		}
		return superclassavail;
	}

	private List<String> findAttributewithManyCardinality(ATLModel wrapper, MetaModel metamodels1, MetaModel metamodels2,
			int indexrule) {
		
		List<String> liststringiniteration = new ArrayList<String>();
		
		List<VariableExp> variables = (List<VariableExp>) wrapper
				.allObjectsOf(VariableExp.class); // variables: navigation list
		
		for (int i = 0; i < variables.size(); i++) {
			
			EObject navigationExpression = variables.get(i).eContainer();
			if (navigationExpression instanceof NavigationOrAttributeCallExp) {
				
				EStructuralFeature featureDefinition2 = wrapper.source(navigationExpression)
						.eClass().getEStructuralFeature("name");
				Object object2modify_src2 = wrapper.source(navigationExpression)
						.eGet(featureDefinition2);
				String[] array2 = variables.get(i).getLocation().split(":", 2);
				
				String type = getType(navigationExpression, variables.get(i), metamodels1,
						metamodels2);
				if (type != null) {

					// list attribute ke ro khat iteration hast va toye in rule hast begir
					// chon roye khat iteration hast pas cardinality * dare
					if (NSGAIIThreeStep.iterationcall.contains(Integer.parseInt(array2[0]))
							&& Integer.parseInt(array2[0]) < NSGAIIThreeStep.finalrule
									.get(indexrule)
							&& Integer.parseInt(array2[0]) > NSGAIIThreeStep.faultrule
									.get(indexrule)) {
						liststringiniteration.add(object2modify_src2.toString());

					}

				}
			}
		}

		return liststringiniteration;
	}

	private boolean outParrenModification(int randomInt, int indexrule, Solution solution, List<EObject> replacements,
			int randomInt2, String oldoutpattern, boolean findreplace) {
		// TODO Auto-generated method stub
		ArrayList<String> listmandatoryattributeoutpattern = new ArrayList<String>();
		listmandatoryattributeoutpattern = ExtractMandatoryAttributeForoutElement(
				solution
				.getCoSolutionThreeStep().getOp().listpropertyname, randomInt);
		
		return CheckIFOUTPatternElementaccessAllattributes(replacements, randomInt2,
				listmandatoryattributeoutpattern, oldoutpattern, findreplace);

		}

	private <ToModify extends LocatedElement> List<Object> applyChange(int randomInt, List<ToModify> modifiable,
			List<Object> ReturnResult, List<EObject> replacements, int randomInt2, EObject oldFeatureValue,
			String comment, ATLModel wrapper, EMFModel atlModel) {
		// TODO Auto-generated method stub

		comment = AddNewCommentsOnTransformation(modifiable,randomInt,oldFeatureValue,replacements,randomInt2);						
		NSGAIIThreeStep.numop = NSGAIIThreeStep.numop + 1;
		ReturnResult.set(0, wrapper);
		ReturnResult.set(1, atlModel);
		ReturnResult.add(comment);

		return ReturnResult;

	}

	private ArrayList<String> ExtractMandatoryAttributeForoutElement(
			ArrayList<ArrayList<String>> listattributoutpatternelement, int randomInt) {
		// TODO Auto-generated method stub
		ArrayList<String> listmandatoryattributeoutpattern = new ArrayList<String>();
		for (int i = 0; i < listattributoutpatternelement.get(randomInt).size(); i++) {
			if(listattributoutpatternelement.get(randomInt).get(i)!=null)
			listmandatoryattributeoutpattern.add(listattributoutpatternelement.get(randomInt).get(i));
		}

		return listmandatoryattributeoutpattern;
	}

	private boolean CheckIFOUTPatternElementaccessAllattributes(List<EObject> replacements, int randomInt2,
			ArrayList<String> listmandatoryattributeOutpattern, String oldelement, boolean findreplace) {
	
		findreplace = true; // Candidate outpattern not Abstract
		if(NSGAIIThreeStep.abstractclassname.contains(toString(replacements.get(randomInt2)) ))		{
		
			return false;
		}
		else {
			
			}
		return findreplace;
		}

	private boolean ChooseCorrectReplacementElement(int randomInt4, boolean findreplace, EObject oldFeatureValue2,
			List<EObject> replacements, boolean nextisattribute, int indexrule2, Solution solution, ATLModel wrapper, String[] array) {
	
		if ( nextisattribute ) {

			int indexclass = NSGAIIThreeStep.classnamestring.indexOf(toString(replacements.get(randomInt4)));
			for (int u = NSGAIIThreeStep.classnamestartpoint.get(indexclass); u < NSGAIIThreeStep.classnamestartpoint
					.get(indexclass) + NSGAIIThreeStep.classnamelength.get(indexclass); u++) {
				
				if (NSGAIIThreeStep.listinheritattributesourcemetamodel.get(u).getName()
						.equals(solution.RHSattributerefinement.get(this.indextype + 1))) {
					findreplace = true;
				}
			}

		}
		
		if (  !nextisattribute ) { // agar// do// ta	// attribut	// badi/ barabar// basha / mesle	/	// e
			int foo=NextElementofRHSattribute(solution);
			// next is integer or filter 
			if(solution.RHSattributelocation.contains( foo)
					|| solution.RHSattributerefinement.get(this.indextype + 1).equals("and")) {
				
				findreplace=NewFilterCompatibleOthersFilters(solution,findreplace,replacements,randomInt4,oldFeatureValue2,indexrule2,wrapper,array);				 				
				
			}
		else{// ,e
                // e e attribute , next is attribute
			findreplace=hasAttributeAfterType(solution,replacements,randomInt4,findreplace,wrapper,indexrule2);
			}
						
		}

		return findreplace;
	}

private int NextElementofRHSattribute(Solution solution) {
		
	int foo;
	try { //after class
		   foo = Integer.parseInt(solution.RHSattributerefinement.get(this.indextype + 1));
		}
		catch (NumberFormatException e)
		{
		   foo = 0;
		}
		return foo;
	}

private boolean NewFilterCompatibleOthersFilters(Solution solution, boolean findreplace, List<EObject> replacements, int randomInt4, EObject oldFeatureValue2, int indexrule2, ATLModel wrapper, String[] array) {
	
	CommonFunctionOperators cp=new CommonFunctionOperators();	
	// if argument type happens in filter check with other filters
	if(Integer.parseInt(array[0])< NSGAIIThreeStep.locationfilter.get(indexrule2))
	{
		
		String str=solution.filterSolution.get(indexrule2 );
		String target =  toString(oldFeatureValue2);//oldType
		String replacement = toString(replacements.get(randomInt4));
		String replacestr=str.replace(target, replacement);
		boolean cmfilter=cp.newFilterCompatibleOtherRules(indexrule2,target,replacement,solution,str,replacestr);
		if(!cmfilter)
			return false;
		cmfilter=cp.compareNewInheritfilterwithOtherFilters(solution, indexrule2, replacement, str,target);
		if(!cmfilter)
			return false;
		solution.filterSolution.set(indexrule2, replacestr); // new filter
		return true;
	
	}
	else // not in filter
		return true;

	}



private boolean hasAttributeAfterType(Solution solution, List<EObject> replacements, int randomInt4, boolean findreplace, ATLModel wrapper, int indexrule2) {

	CommonFunctionOperators cp=new CommonFunctionOperators();
	String fromelement=cp.findFromElementInpattern(indexrule2,wrapper);
	if (solution.RHSattributerefinement.get(this.indextype + 1)
			.equals(solution.RHSattributerefinement.get(this.indextype + 2))) {

		for (int u = this.indextype+1; u < solution.RHSattributerefinement.size(); u++) {

			// new type access to attribute
			if(solution.RHSattributerefinement.get(u).equals(solution.RHSattributerefinement.get(u+1))
						&& solution.RHSattributerefinement.get(u).length()==1
						&& NSGAIIThreeStep.totallistattribute.contains(solution.RHSattributerefinement.get(u+2)) && this.type_next_attr==false)
		
			findreplace=ifNewTypeaccessAttribute(toString(replacements.get(randomInt4)),u+2,solution);		
			if(!findreplace)
				break;
		
			// not attribute, if helper name new Type is correct
			for (int u3 = 0; u3 < NSGAIIThreeStep.listattrhelper.size(); u3++) {
				if (NSGAIIThreeStep.listattrhelper.get(u3).get(0).trim()
						.equals(solution.RHSattributerefinement.get(u).trim()))
					findreplace = true;
				}

			int val;
			try { // not attribute, not helper name, integer
				val = Integer.parseInt(solution.RHSattributerefinement.get(u + 3));
			} catch (NumberFormatException e) {
			
				val = -1; 
			}
			// reach integer, choose  another new type
			if (solution.RHSattributelocation.contains(val) ||
					solution.RHSattributerefinement.get(u+3).equals(fromelement)) {
					break;
			}
		}

	} else
		findreplace = FindCorrectAttributeFromPreviousLine(this.indextype, replacements, randomInt4,
				findreplace,solution);
	
		return findreplace;
	}

	private boolean ifNewTypeaccessAttribute(String newtype, int u, Solution solution) {
	
		int indexclass = NSGAIIThreeStep.classnamestring.indexOf(newtype);
		for (int u2 = NSGAIIThreeStep.classnamestartpoint
				.get(indexclass); u2 < NSGAIIThreeStep.classnamestartpoint.get(indexclass)
						+ NSGAIIThreeStep.classnamelength.get(indexclass); u2++) {
			if (NSGAIIThreeStep.listinheritattributesourcemetamodel.get(u2).getName()
					.equals(solution.RHSattributerefinement.get(u))) {
				if(!NSGAIIThreeStep.listinheritattributesourcemetamodel.get(u2).getEType().getName().equals("EBoolean")
						&& !NSGAIIThreeStep.listinheritattributesourcemetamodel.get(u2).getEType().getName().equals("Boolean"))
					this.type_next_attr=true;
				
				return true;
			}
		}
		return false;
	}

	private boolean FindCorrectAttributeFromPreviousLine(int indextype2, List<EObject> replacements, int randomInt4,
			boolean findreplace, Solution solution) {
		
		int numberpreviousattr = 0;
		for (int u = this.indextype - 1; u > 0; u--) {
			// az hamoon ja ke attribut hast be aghab bargarde ta avalin ja ke attribut
			// shoro shod ra peida kone/
			// bad dobare bere jolo ta check kone ghable oclkindof chan ta attribut dare
			if (solution.RHSattributerefinement.get(u)
					.equals(solution.RHSattributerefinement.get(this.indextype + 1))) {

				for (int u3 = u; u3 < this.indextype - 2; u3++) {
					numberpreviousattr = numberpreviousattr + 1;

				}
				break;
			}

		}
		// index dorost attribut mishe (this.indextype+numberpreviousattr+1)
		findreplace=ifNewTypeaccessAttribute( toString(replacements.get(randomInt4)),  this.indextype + numberpreviousattr + 1,  solution) ;	
		boolean checkisattr = false;
		for (int u3 = 0; u3 < NSGAIIThreeStep.listsourcemetamodel.size(); u3++) {
			if (NSGAIIThreeStep.listsourcemetamodel.get(u3).getName()
					.equals(solution.RHSattributerefinement.get(this.indextype + numberpreviousattr + 1)))
				checkisattr = true;
		}
		// momkene on attribut nabashe / age nabashe dige lazem nist type badi peyda
		// kone
		if (!checkisattr)
			findreplace = true;

		return findreplace;
	}

	private void indexArgumentTypeinRefinement(Solution solution,String[] array,EObject oldFeatureValue2) {
		
		int indexwholelineattribut = solution.RHSattributerefinement.indexOf((array[0]));
		int indexwholelineattributlocation = solution.RHSattributelocation
				.indexOf(Integer.parseInt(array[0]));
		int startlocation = solution.RHSattributelocation.get(indexwholelineattributlocation + 1);
		int endlocation = solution.RHSattributelocation.get(indexwholelineattributlocation + 2);
		this.indextype = -1;
		
		if (indexwholelineattribut >= 0) {

			for (int u = startlocation; u <= endlocation; u++) {
				// find oldType in RHSattribute
				if (solution.RHSattributerefinement.get(u).equals(toString(oldFeatureValue2))) {

					this.indextype = u;

				}
			}
		}}
	private boolean IfNextElementIsAttribute(EObject oldFeatureValue2, String[] array, Solution solution) {
		
		int indexwholelineattribut = solution.RHSattributerefinement.indexOf((array[0]));
		int startlocation = -1;
		int endlocation = -1;
		
		for(int i=0; i<solution.RHSattributelocation.size();i=i+3) {
			if(solution.RHSattributelocation.get(i)==Integer.parseInt(array[0])) {
				
				startlocation =solution.RHSattributelocation.get(i+1);
				endlocation =solution.RHSattributelocation.get(i+2);
				break;
			}
		}
		
		this.indextype = -1;
		boolean nextisattribute = false;
		if (indexwholelineattribut >= 0) {
			
			FindOldTypeinRightSideAttributes(startlocation,endlocation, oldFeatureValue2, solution );	
			boolean attributisoclletter = false; // if next of Type is Attribute
			for (int u = 0; u < NSGAIIThreeStep.listsourcemetamodel.size(); u++) {
				if(this.indextype + 1<solution.RHSattributerefinement.size()) {
				if (NSGAIIThreeStep.listsourcemetamodel.get(u).getName()
						.equals(solution.RHSattributerefinement.get(this.indextype + 1))) {
					
					attributisoclletter=IfAttributeIsOCLLetter(indexwholelineattribut,
							attributisoclletter, solution);
					
					if (attributisoclletter == false)
						nextisattribute = true;
					break;

				}
				
			}
		}

	}

		return nextisattribute;
 }



	private boolean IfAttributeIsOCLLetter(int indexwholelineattribut, boolean attributisoclletter, Solution solution) {
		
		for (int y = indexwholelineattribut; y > indexwholelineattribut - 5; y--) {
			for (int y2 = 0; y2 < NSGAIIThreeStep.oclletter.size(); y2 = y2 + 2) {
				if (NSGAIIThreeStep.oclletter.get(y2)
						.equals(solution.RHSattributerefinement.get(y))
						&& NSGAIIThreeStep.oclletter.get(y2 + 1).equals(
								solution.RHSattributerefinement.get(this.indextype + 1))) {
					attributisoclletter = true;

				}
			}
		}
		for (int y = indexwholelineattribut + 1; y < indexwholelineattribut + 2; y++) {
			for (int y2 = 0; y2 < NSGAIIThreeStep.oclletter.size(); y2 = y2 + 2) {
				if (NSGAIIThreeStep.oclletter.get(y2)
						.equals(solution.RHSattributerefinement.get(y))
						&& NSGAIIThreeStep.oclletter.get(y2 + 1).equals(
								solution.RHSattributerefinement.get(this.indextype + 1))) {
					attributisoclletter = true;

				}
			}
		}

		return attributisoclletter;
	}

	private void FindOldTypeinRightSideAttributes(int startlocation, int endlocation, EObject oldFeatureValue2, Solution solution) {
		for (int u = startlocation; u <= endlocation; u++) {
			// find oldType in RHSattribute
			if (solution.RHSattributerefinement.get(u).equals(toString(oldFeatureValue2))) {
				this.indextype = u;

			}

		}
	}

	private String getType(EObject container, VariableExp containee, MetaModel inputMM, MetaModel outputMM) {
	

		EClassifier c = null;
		VariableDeclaration def = containee.getReferredVariable();

		// obtain type (classifier) of variable expression
		// ..............................
		// case 1 -> in pattern element
		if (def != null) {
			if (def instanceof InPatternElement) {

				c = inputMM.getEClassifier(def.getType().getName());
				
			}
			// case 2 -> for each out pattern element
			else if (def instanceof ForEachOutPatternElement) {
		
				def = ((ForEachOutPatternElement) def).getIterator();
				if (def.eContainer() instanceof OutPatternElement) {
					OutPatternElement element = (OutPatternElement) def.eContainer();
					if (element.getType() instanceof OclModelElement)
						c = outputMM.getEClassifier(((OclModelElement) element.getType()).getName());
				}
			}
			// case 3 -> iterator
			else if (def instanceof Iterator) {
				
				if (def.eContainer() instanceof LoopExp) {
					LoopExp iterator = (LoopExp) def.eContainer();
					OclExpression exp = iterator.getSource();
					while (c == null && exp != null) {
						if (exp instanceof OclModelElement) {
							c = inputMM.getEClassifier(((OclModelElement) exp).getName());
							exp = null;
						} else if (exp instanceof PropertyCallExp) {
							exp = ((PropertyCallExp) exp).getSource();
						} else if (exp instanceof VariableExp) {
							c = inputMM.getEClassifier(getType(container, (VariableExp) exp, inputMM, outputMM));
							exp = null;
						} else
							exp = null;
					}
				}
				
			}
			// case 4 -> variable declaration
			else {
				if (toString(def).equals("self")) {
					
					EObject helper = containee;
					while (helper != null && !(helper instanceof Helper))
						helper = helper.eContainer();
					if (helper instanceof Helper) {
						if (((Helper) helper).getDefinition().getContext_() != null
								&& ((Helper) helper).getDefinition().getContext_().getContext_() != null
								&& ((Helper) helper).getDefinition().getContext_()
										.getContext_() instanceof OclModelElement)
							c = inputMM.getEClassifier(
									((OclModelElement) ((Helper) helper).getDefinition().getContext_().getContext_())
											.getName());
					}
				} else if (((VariableDeclaration) def).getType() instanceof OclModelElement) {
					
					c = inputMM.getEClassifier(((VariableDeclaration) def).getType().getName());
				} else if (((VariableDeclaration) def).getType() instanceof CollectionType) {
					
					c = inputMM.getEClassifier(
							((CollectionType) ((VariableDeclaration) def).getType()).getElementType().getName());
				}
			}

			// obtain type (classifier) of container
			// ........................................
			EObject next = containee.eContainer();
			while (c != null && next != null && next != container) {
				if (c instanceof EClass) {
					EStructuralFeature name = next.eClass().getEStructuralFeature("name");
					EStructuralFeature feature = null;
					if (name != null) {
						String nameValue = next.eGet(name).toString();
						feature = ((EClass) c).getEStructuralFeature(nameValue);
					} else {
						System.out.println("Warning: " + next.eClass().getName() + " " + "with null name feature ");
					}
					if (feature != null) {
						c = feature.getEType();
						next = next.eContainer();
					} else
						next = null;
				}
			}
		}
		return c != null ? c.getName() : null;

	}

	private <ToModify extends LocatedElement> List<Object> OperationPreviousGeneration(int randomInt, Solution solution,
			EMFModel atlModel, MetaModel[] metamodels, List<ToModify> modifiable, ATLModel wrapper, String featureName,
			EDataTypeEList<String> comments, List<Object> ReturnResult, MetaModel metamodel1, MetaModel metamodel2, CommonFunctionOperators cp) {
		
		ToModify modifiable2 = modifiable.get(randomInt);
		String[] array = modifiable2.getLocation().split(":", 2);
		if (OperationsThreeStep.statecase.equals("case7")) {
			if (!solution.listlineofcodes.contains(Integer.parseInt(array[0]))) {
				ReturnResult = ApplyFromPreviousCase7(randomInt, solution, atlModel, metamodels, modifiable2, wrapper,
						featureName, comments, ReturnResult,array, cp, modifiable);
			} else {
				cp.setvariable(-2, -2, -2);
			}
		} else {

			ReturnResult = ApplyFromPreviousOtherCase(randomInt, solution, atlModel, metamodels, modifiable2, wrapper,
					featureName, comments, ReturnResult, metamodel1, metamodel2, array,modifiable, cp);

		}
		return ReturnResult;
	}

	

	private <ToModify extends LocatedElement> List<Object> ApplyFromPreviousOtherCase(int randomInt, Solution solution,
			EMFModel atlModel, MetaModel[] metamodels, ToModify modifiable2, ATLModel wrapper, String featureName,
			EDataTypeEList<String> comments, List<Object> ReturnResult, MetaModel metamodel1, MetaModel metamodel2,
			String[] array,List<ToModify> modifiable, CommonFunctionOperators cp) {
		
		List<String> liststringiniteration = new ArrayList<String>();
		liststringiniteration=UpdateInpatternParametersCrossover(wrapper, metamodel1, metamodel2, array,cp
				, liststringiniteration);
				String comment = null;
		EStructuralFeature featureDefinition = wrapper.source(modifiable2).eClass().getEStructuralFeature(featureName);
		EObject object2modify_src = wrapper.source(modifiable2);
		EObject oldFeatureValue = (EObject) object2modify_src.eGet(featureDefinition);
		List<EObject> replacements = this.replacements(atlModel, (EObject) oldFeatureValue, metamodels);
		int randomInt2 = -2;
		randomInt2 = cp.FindSecondIndex(randomInt2, replacements.size());	
		boolean check = false;
		check=IfCardinalityIsOneDisableModification(liststringiniteration, check,
				replacements,randomInt2, cp);
		
		if (check == false) {
			if (!oldFeatureValue.toString().equals(replacements.get(randomInt2).toString())
					&& (   this.type_next_operation.equals("non") || (this.type_next_operation.equals("deletion")
							|| this.type_next_operation.equals("creation") &&
					(MyProblemThreeStep.indexoperation+NSGAIIThreeStep.mandatorycreationoperation.size()+NSGAIIThreeStep.mandatorydeletionattribute.size())<=IntSolutionTypeThreeStep.max_operations_size) )) {
				
				UpdateTransformation(oldFeatureValue, replacements, randomInt2, randomInt, featureDefinition , object2modify_src, cp);
				 int indexrule = -1;
				if(NSGAIIThreeStep.mandatorycreationoperation.size()>0)
				    indexrule = cp.FindRule(array);
				 ReturnResult = applyChange(randomInt, modifiable, ReturnResult, replacements,
						randomInt2, oldFeatureValue, comment, wrapper, atlModel);
				comments.add(AddNewCommentsOnTransformation(modifiable,randomInt,oldFeatureValue,replacements,randomInt2));
				ClearMandatoryOutPatternList();
			} 
			ClearMandatoryCreationOperation(cp, randomInt, randomInt2);
		}

		return ReturnResult;
	}
 private void ClearMandatoryCreationOperation(CommonFunctionOperators cp, int randomInt, int randomInt2) {
	
	 if((this.type_next_operation.equals("deletion")
				|| this.type_next_operation.equals("creation") )&&
				(MyProblemThreeStep.indexoperation+NSGAIIThreeStep.mandatorycreationoperation.size()+NSGAIIThreeStep.mandatorydeletionattribute.size())>IntSolutionTypeThreeStep.max_operations_size) {
			cp.StoreTwoIndex(randomInt, randomInt2);
			NSGAIIThreeStep.mandatorycreationoperation.clear();
			NSGAIIThreeStep.mandatorydeletionattribute.clear();
		}
	}

private void ClearMandatoryOutPatternList() {
	 
	 NSGAIIThreeStep.mandatoryoutpatternoperation.clear();
	 NSGAIIThreeStep.mandatoryoutpatternlocation.clear();
		
	}

private boolean IfCardinalityIsOneDisableModification(List<String> liststringiniteration, boolean check,
			List<EObject> replacements, int randomInt2, CommonFunctionOperators cp) {
		
	 if (liststringiniteration.size() > 0) {
			// to arraye ke list classha hast esme inpattern ra toye list peyda kon ba
			// indexesh
			int indexclassname = NSGAII.classnamestring.indexOf(toString(replacements.get(randomInt2)));

			for (int j = 0; j < liststringiniteration.size(); j++) {
				for (int i = NSGAIIThreeStep.classnamestartpoint.get(indexclassname); i < NSGAIIThreeStep.classnamestartpoint
						.get(indexclassname) + NSGAIIThreeStep.classnamelength.get(indexclassname); i++) {
					
					if (NSGAIIThreeStep.listinheritattributesourcemetamodel.get(i).getName().equals(liststringiniteration.get(j))
							&& NSGAIIThreeStep.listinheritattributesourcemetamodel.get(i).getUpperBound() == 1) {
						cp.setvariable(-2, -2, -2);
						check = true;
					}
				}
			}
		}
		return check;
	}

private List<String> UpdateInpatternParametersCrossover(ATLModel wrapper, MetaModel metamodel1,
			MetaModel metamodel2, String[] array, CommonFunctionOperators cp, List<String> liststringiniteration) {
	 if (OperationsThreeStep.statecase.equals("case6")) {
			List<VariableExp> variables = (List<VariableExp>) wrapper.allObjectsOf(VariableExp.class);
			
			for (int i = 0; i < variables.size(); i++) {
				EObject navigationExpression = variables.get(i).eContainer();
				if (navigationExpression instanceof NavigationOrAttributeCallExp) {
					
					EStructuralFeature featureDefinition2 = wrapper.source(navigationExpression).eClass()
							.getEStructuralFeature("name");
					Object object2modify_src2 = wrapper.source(navigationExpression).eGet(featureDefinition2);
					String[] array2 = variables.get(i).getLocation().split(":", 2);
					String type = getType(navigationExpression, variables.get(i), metamodel1, metamodel2);
					int indexrule = cp.FindRule(array);
					
					if (type != null) {

						// list attribute ke ro khat iteration hast va toye in rule hast begir chon roye
						// khat iteration hast pas cardinality * dare
						if (NSGAIIThreeStep.iterationcall.contains(Integer.parseInt(array2[0]))
								&& Integer.parseInt(array2[0]) < NSGAIIThreeStep.finalrule.get(indexrule)
								&& Integer.parseInt(array2[0]) > NSGAIIThreeStep.faultrule.get(indexrule)) {
							liststringiniteration.add(object2modify_src2.toString());

						}
					}
				}
			}
		}

		return liststringiniteration;
	}

	//crossover argument type 
	private <ToModify extends LocatedElement> List<Object> ApplyFromPreviousCase7(int randomInt, Solution solution,
			EMFModel atlModel, MetaModel[] metamodels, ToModify modifiable2, ATLModel wrapper, String featureName,
			EDataTypeEList<String> comments, List<Object> ReturnResult,String[] array, CommonFunctionOperators cp,
			List<ToModify> modifiable) {

		String comment = null;
		EStructuralFeature featureDefinition = wrapper.source(modifiable2).eClass().getEStructuralFeature(featureName);
		List<EObject> value = (List<EObject>) wrapper.source(modifiable2).eGet(featureDefinition);
		int randomInt3 = cp.calculaterandomInt2(value);
		EObject oldFeatureValue2 = value.get(randomInt3);
		List<EObject> replacements = this.replacements(atlModel, (EObject) oldFeatureValue2, metamodels);
		int randomInt4 = cp.calculaterandomInt(replacements);
		boolean findreplace=true;
		int indexrule =-1;
		if (!oldFeatureValue2.toString().equals(replacements.get(randomInt4).toString())) {
			indexrule = cp.FindRule(array);
			findreplace=NewFilterCompatibleOthersFilters(solution,findreplace,replacements,randomInt4,oldFeatureValue2,indexrule,wrapper,array);				 				
			if(findreplace==true)
			{
			AddArgumentModificationParameters(oldFeatureValue2, replacements,randomInt4
					, randomInt, randomInt3, cp, value);
			indexArgumentTypeinRefinement(solution,array,oldFeatureValue2);
			if (this.indextype > 0) // put newType in RHSattribute
				solution.RHSattributerefinement.set(this.indextype,
						toString(replacements.get(randomInt4)));
			
			comments.add(AddNewCommentsOnTransformation(modifiable,randomInt,oldFeatureValue2,replacements,randomInt4));
			comment = AddNewCommentsOnTransformation(modifiable,randomInt,oldFeatureValue2,replacements,randomInt4);
			System.out.println("\n-- MUTATION \"" + this.getDescription() + "\" from " + toString(modifiable2) + ":"
					+ toString(oldFeatureValue2) + " to " + toString(modifiable2) + ":"
					+ toString(replacements.get(randomInt4)) + " (line " + modifiable2.getLocation()
					+ " of original transformation)\n");
			NSGAIIThreeStep.numop = NSGAIIThreeStep.numop + 1; 
			ReturnResult.set(0, wrapper);
			ReturnResult.set(1, atlModel);
			ReturnResult.add(comment);
		}
			else {
				cp.setvariable(-2, -2, -2);
			}

		}
		return ReturnResult;
	}

	
	private void AddArgumentModificationParameters(EObject oldFeatureValue2, List<EObject> replacements, int randomInt4,
			int randomInt, int randomInt3, CommonFunctionOperators cp, List<EObject> value) {
		
		copyFeatures(oldFeatureValue2, replacements.get(randomInt4));
		cp.setvariable(randomInt, randomInt4, randomInt3);
		value.set(randomInt3, replacements.get(randomInt4));
		
	}

	public List<EPackage> retPackMM(Resource resourceMM) {
		ResourceSet resourceSet = resourceMM.getResourceSet();
		List<EPackage> metamodel = new ArrayList<EPackage>();
		for (EObject obj : resourceMM.getContents()) {
			if (obj instanceof EPackage) {
				EPackage.Registry.INSTANCE.put(((EPackage) obj).getNsURI(),
						((EPackage) obj).getEFactoryInstance().getEPackage());
				resourceSet.getPackageRegistry().put(((EPackage) obj).getNsURI(),
						((EPackage) obj).getEFactoryInstance().getEPackage());
				metamodel.add((EPackage) obj);
			}
		}
		return metamodel;
	}

	public Resource retPackResouceMM(String MMPath) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new EcoreResourceFactoryImpl());
		URI fileURI = URI.createFileURI(MMPath);// ecore.getFullPath().toOSString());
		Resource resource = resourceSet.getResource(fileURI, true);
		return resource;
	}

	/**
	 * It returns the list of classes that replace a given one: a compatible
	 * subclass, a compatible superclass, a compatible unrelated class, an
	 * incompatible subclass, and an incompatible unrelated class.
	 * 
	 * @param toReplace
	 * @param metamodel
	 * @return
	 */
	private List<EObject> replacements(EMFModel atlModel, EObject toReplace, MetaModel[] metamodels) {
		List<EObject> replacements = new ArrayList<EObject>();

		EPackage pack = toReplace.eClass().getEPackage();
		EClass mmtype = (EClass) pack.getEClassifier("OclModelElement");
		EClass collection = (EClass) pack.getEClassifier("CollectionType");
		EClass collectionExp = (EClass) pack.getEClassifier("CollectionExp");
		EClass primitive = (EClass) pack.getEClassifier("Primitive");
		Setting s = new Setting();
		// OCL MODEL ELEMENT
		// .......................................................................
		if (mmtype.isInstance(toReplace)) {
			for (MetaModel metamodel : metamodels) {
				String MMRootPath3 = null;
				if (OperationsThreeStep.statecase.equals("case1")) {
					MMRootPath3 = s.getsourcemetamodel();
				} else {
					MMRootPath3 = s.gettargetmetamodel();
				}
				MetaModel meta = null;
				try {
					meta = new MetaModel(MMRootPath3);
				} catch (transException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				EStructuralFeature featureName = toReplace.eClass().getEStructuralFeature("name");
				String featureValue = toReplace.eGet(featureName).toString();
				EClassifier classToReplace = meta.getEClassifier(featureValue);	
				List<EClass> options = new ArrayList<EClass>();
				if (Class2Rel.typeoperation.equals("argu") || Class2Rel.typeoperation.equals("outelement")
						|| Class2Rel.typeoperation.equals("inelement")) {

					List<EClass> mainclass = new ArrayList<EClass>();
					for (EClassifier classifier : meta.getEClassifiers()) {
						if (classifier instanceof EClass) {
							if (OperationsThreeStep.statecase.equals("case1")
									&& OperationsThreeStep.statecase.equals("case7")) {
								if (((EClass) classifier).isAbstract() == false)
									mainclass.add((EClass) classifier);
							}

							else {

								if (NSGAIIThreeStep.lazyrulelocation.size() == 0) {
									// next line was for first step of project
									mainclass.add((EClass) classifier);

								} else
									mainclass.add((EClass) classifier);
							}
						}
					}
					for (int ii = 0; ii < mainclass.size(); ii++) {
						options.add(mainclass.get(ii));

					}

				}
				// metamodel ra be meta tabdil kardam
				else {
					if (classToReplace != null && classToReplace instanceof EClass) {
						EClass replace = (EClass) classToReplace;
						List<EClass> copy1 = new ArrayList<EClass>();
						List<EClass> copy2 = new ArrayList<EClass>();
						List<EClass> copy3 = new ArrayList<EClass>();
						List<EClass> copy4 = new ArrayList<EClass>();
						List<EClass> copy5 = new ArrayList<EClass>();
						copy1 = getCompatibleSubclass(replace, meta);
						for (int ii = 0; ii < copy1.size(); ii++) {
							options.add(copy1.get(ii));

						}
						
						copy1.clear();
						copy2 = getCompatibleSuperclass(replace);
						if (copy2 != null) {
							for (int ii = 0; ii < copy2.size(); ii++)
								options.add(copy2.get(ii));
						}
						copy3 = getCompatibleUnrelatedClass(replace, meta);
						for (int ii = 0; ii < copy3.size(); ii++)
							options.add(copy3.get(ii));
						copy3.clear();
						copy4 = getIncompatibleSubclass(replace, meta);
						for (int ii = 0; ii < copy4.size(); ii++)
							options.add(copy4.get(ii));
						copy4.clear();
						copy5 = getIncompatibleUnrelatedClass(replace, meta);
						for (int ii = 0; ii < copy5.size(); ii++)
							options.add(copy5.get(ii));

					}
				}
				
				for (EClass option : options) {
					if (option != null) {
						EObject object = (EObject) atlModel.newElement(mmtype);
						object.eSet(mmtype.getEStructuralFeature("name"), option.getName());
						replacements.add(object);
					}
				}

				break; // khodam add kardam
			}
		}

		// COLLECTION TYPE
		// .........................................................................

		else if (collection.isInstance(toReplace)) {

			// replace by each different collection type
			String[] options = { "SequenceType", "SetType", "BagType", "OrderedSetType" };
			for (String option : options) {
				if (!((EClass) pack.getEClassifier(option)).isInstance(toReplace)) {
					EClass collectionType = (EClass) pack.getEClassifier(option);
					replacements.add((EObject) atlModel.newElement(collectionType));
				}
			}
		}

		// COLLECTION EXPRESSION TYPE
		// ..............................................................

		else if (collectionExp.isInstance(toReplace)) {

			// replace by each different collection type
			String[] options = { "SequenceExp", "SetExp", "BagExp", "OrderedSetExp" };
			for (String option : options) {
				if (!((EClass) pack.getEClassifier(option)).isInstance(toReplace)) {
					EClass collectionExpType = (EClass) pack.getEClassifier(option);
					replacements.add((EObject) atlModel.newElement(collectionExpType));
				}
			}
		}

		// PRIMITIVE TYPE
		// ..........................................................................

		else if (primitive.isInstance(toReplace)) {

			// replace by each different collection type
			String[] options = { "StringType", "BooleanType", "IntegerType", "RealType" };
			for (String option : options) {
				if (!((EClass) pack.getEClassifier(option)).isInstance(toReplace)) {
					EClass primitiveType = (EClass) pack.getEClassifier(option);
					replacements.add((EObject) atlModel.newElement(primitiveType));
				}
			}
		}

		return replacements;
	}

	/**
	 * Given a class, it returns a compatible superclass (i.e. with the same
	 * features).
	 * 
	 * @param c
	 * @return a compatible superclass, null if there is none
	 */
	
	protected List<EClass> getCompatibleSuperclass(EClass c) {
		List<EClass> superclasses = c.getEAllSuperTypes();
		return c.getEStructuralFeatures().size() > 0 ? getCompatible(c, superclasses) : null;
	}

	/**
	 * Given a class, it returns a compatible subclass (i.e. with the same
	 * features).
	 * 
	 * @param c
	 * @param mm
	 * @return a compatible subclass, null if there is none
	 */
	protected List<EClass> getCompatibleSubclass(EClass c, MetaModel mm) {
		List<EClass> subclasses = subclasses(c, mm);
		return getCompatible(c, subclasses);
	}

	/**
	 * Given a class, it returns a compatible class that is neither a superclass nor
	 * a subclass.
	 * 
	 * @param c
	 * @param mm
	 * @return a compatible unrelated class, null if there is none
	 */
	
	protected List<EClass> getCompatibleUnrelatedClass(EClass c, MetaModel mm) {
		List<EClass> unrelatedClasses = unrelatedClasses(c, mm);
		return getCompatible(c, unrelatedClasses);
	}

	/**
	 * Given a class, it returns an incompatible subclass (i.e. with different
	 * features).
	 * 
	 * @param c
	 * @param mm
	 * @return an incompatible subclass, null if there is none
	 */
	
	protected List<EClass> getIncompatibleSubclass(EClass c, MetaModel mm) {
		List<EClass> subclasses = subclasses(c, mm);
		return getIncompatible(c, subclasses);
	}

	/**
	 * Given a class, it returns an incompatible class that is neither a superclass
	 * nor a subclass.
	 * 
	 * @param c
	 * @param mm
	 * @return an incompatible unrelated class, null if there is none
	 */
	
	protected List<EClass> getIncompatibleUnrelatedClass(EClass c, MetaModel mm) {
		List<EClass> unrelatedClasses = unrelatedClasses(c, mm);
		return getIncompatible(c, unrelatedClasses);
	}

	// -------------------------------------------------------------------------------------
	// AUXILIARY METHODS
	// -------------------------------------------------------------------------------------

	/**
	 * Given a class, it returns a compatible class (i.e. with the same features)
	 * from a list of candidates.
	 * 
	 * @param c
	 * @param candidates
	 * @return a compatible class, null if there is none
	 */
	
	private List<EClass> getCompatible(EClass c, List<EClass> candidates) {

		List<EClass> compatible = new ArrayList<EClass>();
		for (int i = 0; i < candidates.size(); i++) {
			EClass c2 = candidates.get(i);
			if (isCompatibleWith(c2, c))
				compatible.add(c2);
		}
		return compatible;
	}

	private List<EClass> getIncompatible(EClass c, List<EClass> candidates) {
		List<EClass> incompatible = new ArrayList<EClass>();
		;
		for (int i = 0; i < candidates.size(); i++) {
			EClass c2 = candidates.get(i);
			if (!isCompatibleWith(c2, c))
				incompatible.add(c2);
		}
		return incompatible;
	}

	/**
	 * It checks whether a class c1 is compatible with (i.e. it can substitute
	 * safely) another class c2. c1 is compatible with c2 if c1 defines at least all
	 * features that c2 defines (it can define more).
	 * 
	 * @param c1 class
	 * @param c2 class
	 * @return
	 */
	private boolean isCompatibleWith(EClass c1, EClass c2) {
		boolean compatible = true;

		for (int i = 0; i < c2.getEAllStructuralFeatures().size() && compatible; i++) {
			EStructuralFeature feature2 = c2.getEAllStructuralFeatures().get(i);
			EStructuralFeature feature1 = c1.getEStructuralFeature(feature2.getName());
			// c1 cannot substitute c2 if:
			// - c1 lacks one of the features of c2
			// - c1 has a feature with same name but different type
			// - c1 has a feature with same name but it is monovalued, while the one in c1
			// is multivalued (or viceversa)
			if (feature1 == null || feature1.getEType() != feature2.getEType()
					|| (feature1.getUpperBound() == 1 && feature2.getUpperBound() != 1)
					|| (feature1.getUpperBound() != 1 && feature2.getUpperBound() == 1))
				compatible = false;
		}

		return compatible;
	}

	/**
	 * It returns the list of classes that are neither superclasses nor subclasses
	 * of a given class.
	 * 
	 * @param c
	 * @param mm
	 * @return
	 */
	private List<EClass> unrelatedClasses(EClass c, MetaModel mm) {
		List<EClass> subclasses = subclasses(c, mm);
		List<EClass> unrelatedClasses = new ArrayList<EClass>();
		for (EClassifier classifier : mm.getEClassifiers()) {
			if (classifier instanceof EClass && classifier != c && !c.getEAllSuperTypes().contains(classifier)
					&& !subclasses.contains(classifier))
				unrelatedClasses.add((EClass) classifier);
		}
		
		return unrelatedClasses;
	}

	/**
	 * It copies all features (except name) "from" an object "to" another object.
	 * 
	 * @param from
	 * @param to
	 * @param features
	 */
	private void copyFeatures(EObject from, EObject to) {
		for (EStructuralFeature feature : from.eClass().getEAllStructuralFeatures()) {
			if (!feature.getName().equals("name") && to.eClass().getEAllStructuralFeatures().contains(feature))
				to.eSet(feature, from.eGet(feature));
		}
	}



}
