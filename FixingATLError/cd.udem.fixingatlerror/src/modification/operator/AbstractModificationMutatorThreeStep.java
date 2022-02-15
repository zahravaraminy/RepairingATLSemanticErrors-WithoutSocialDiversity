package modification.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import witness.generator.MetaModel;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.InPatternElement;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atl.model.ATLModel;
import evaluation.mutator.AbstractMutator;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import cd.udem.fixingatlerror.OperationsThreeStep;
import jmetal.core.Solution;
import jmetal.metaheuristics.nsgaII.NSGAIIThreeStep;
import jmetal.problems.MyProblemThreeStep;

public abstract class AbstractModificationMutatorThreeStep extends AbstractMutator {

	/**
	 * It receives the object to modify, and the value of the attribute to modify.
	 * It returns a set of valid replacements for the attribute value. To be
	 * implemented only by subclasses that call to genericAttributeModification.
	 * 
	 * @param object2modify
	 * @param currentAttributeValue
	 * @param metamodel
	 * @return set of valid replacements for the attribute value.
	 */
	boolean mutationapply=false;
	boolean unitfindtarget = false;
	private int indextype=-1;
	private int indexAttributIteration=-1;
	private String lastAttributIteration=null;
	private ArrayList<String> typeAttribut = new ArrayList<String>();
	private EDataTypeEList<String> comments = null;
	boolean checkmutationapply = false;
	private List<Object> replacements = new ArrayList<Object>();
	private List<String> replacementstype = new ArrayList<String>();
	protected abstract List<Object> replacements(EMFModel atlModel, EClass oldFeatureValue, EObject object2modify,
			String currentAttributeValue, MetaModel metamodel);

	/**
	 * Generic modification. It allows subtypes of the class to modify.
	 * 
	 * @param atlModel
	 * @param outputFolder
	 * @param ToModifyClass class of objects to modify (example Binding)
	 * @param featureName   feature to modify (example propertyName)
	 * @param metamodel     metamodel containing the candidate types for the
	 *                      modification
	 * @return
	 */
	protected <ToModify extends LocatedElement> List<Object> genericAttributeModification(EMFModel atlModel,
			String outputFolder, Class<ToModify> ToModifyClass, String feature, MetaModel metamodel, ATLModel wrapper,
			Solution solution,CommonFunctionOperators cp) {
		try {
			return genericAttributeModification(atlModel, outputFolder, ToModifyClass, feature, metamodel, false,
					wrapper, solution,cp);
		} catch (ATLCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	//Operation10;
	protected <ToModify extends LocatedElement> List<Object> genericAttributeModification(EMFModel atlModel,
			String outputFolder, Class<ToModify> ToModifyClass, String feature, MetaModel metamodel,
			boolean exactToModifyType, ATLModel wrapper, Solution solution, CommonFunctionOperators cp) throws ATLCoreException {
		
		List<Object> ReturnResult = new ArrayList<Object>();
		List<ToModify> modifiable = (List<ToModify>) wrapper.allObjectsOf(ToModifyClass);	
		ReturnResult.add(wrapper);
		ReturnResult.add(atlModel);	
		String comment = null;
		Module module = wrapper.getModule();
		if (module != null) {
			EStructuralFeature f = wrapper.source(module).eClass().getEStructuralFeature("commentsAfter");
			this.comments = (EDataTypeEList<String>) wrapper.source(module).eGet(f);
		}
        // operation 5
		if (exactToModifyType)
			filterSubtypes(modifiable, ToModifyClass);
		int randomInt = -2;
		
		this.mutationapply=false;
		int count = -1;
		ArrayList<Integer> candidaterandomInt = new ArrayList<Integer>();
		while (!this.checkmutationapply ) {

			count = count + 1;
			boolean corecttarget = false;
			List<Integer> Result = null;
			String[] array = null;
			// choose left hand side that upper bound>1
			while (!corecttarget) {
				
				Result = cp.ReturnFirstIndex(randomInt, modifiable.size(), checkmutationapply, solution,null,modifiable);
				randomInt = Result.get(0);
				if(Result.get(2)>0 )
					candidaterandomInt.add(Result.get(2));
				if(candidaterandomInt.size()>=modifiable.size() )
					corecttarget = true;
			    if(randomInt!=-1)
			    {
				 array = modifiable.get(randomInt).getLocation().split(":", 2);
				 corecttarget = true;
				}
			}
			
			if(!candidaterandomInt.contains( randomInt))
				candidaterandomInt.add(randomInt );
			
			 if (solution.getpreviousgeneration() == true && randomInt!=-1) { // for Crossover 
				
				ReturnResult = OperationPreviousGenerationModefyBinding(randomInt, solution, atlModel, metamodel,
						modifiable, wrapper, feature, ReturnResult);
				this.checkmutationapply = true;
				
			}

			else if (modifiable.size() > 0 && randomInt != -1 && solution.getpreviousgeneration() == false) {
				
				
				if (!OperationsThreeStep.statecase.equals("case5") && !OperationsThreeStep.statecase.equals("case2")
						&& !OperationsThreeStep.statecase.equals("case3")) {
					
						ReturnResult=BindingTargetModification(solution, randomInt,checkmutationapply,
								wrapper,feature, modifiable, array, metamodel, atlModel, comment, ReturnResult);
						}

				else {
					//  operation 5: collection , operation 2, operation 10
				
					ReturnResult = IteratorPredefinedCollectionOperation(wrapper,randomInt, feature,array
							  , checkmutationapply,atlModel,metamodel,modifiable,solution,ReturnResult,comment,cp);
					
					this.checkmutationapply =this.mutationapply;
				}

			}
			 if(candidaterandomInt.size()>=modifiable.size())
					this.checkmutationapply = true;
			 
			 if(OperationsThreeStep.statecase.equals("case2") && candidaterandomInt.size()==12)
				 	this.checkmutationapply = true;
		}

		return ReturnResult;
	}

	
	private <ToModify extends LocatedElement> List<Object> BindingTargetModification(Solution solution, int randomInt, boolean checkmutationapply,
			ATLModel wrapper, String feature, List<ToModify> modifiable, String[] array, MetaModel metamodel
			, EMFModel atlModel, String comment, List<Object> ReturnResult) {
		
		int row = 0;
		int column = 0;	
		// find choosen target in LHS of binding
		 ArrayList<Integer> row_column=FindSpecificTarget(solution, row, column, randomInt );
		 row=row_column.get(0);	
		 column=row_column.get(1);
		if (solution.getCoSolutionThreeStep().getOp().originalwrapper.get(row).get(column) == 1) {
			this.checkmutationapply = true;
							} else {
			// operation3: initial population
			EStructuralFeature featureDefinition = wrapper.source(modifiable.get(randomInt)).eClass()
					.getEStructuralFeature(feature);
			if (wrapper.source(modifiable.get(randomInt)).eGet(featureDefinition) != null) {

				EObject object2modify_src = wrapper.source(modifiable.get(randomInt));
				Object oldFeatureValue = object2modify_src.eGet(featureDefinition);
				int location=findtargetlocationourtwodimentionalarray(solution,array);
				int indexrule = -1;
				indexrule = FindIndexRule(array);	
				// find outpattern of the target
				List<OutPatternElement> modifiable2 = (List<OutPatternElement>) wrapper
						.allObjectsOf(OutPatternElement.class);
				EObject oldFeatureValue3 = findoutputforthistarget(wrapper, indexrule, array, modifiable2);		
				// save output for solution
				EClassifier classifier = metamodel
						.getEClassifier(modifiable2.get(location).getType().getName());
				solution.listeobject.set(MyProblemThreeStep.indexoperation - 1, classifier);		
				FindCandidateReplacements(location, oldFeatureValue3);		
				int indexof = -1;
				int randomInt2 = -2;
				indexof = solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(location)
						.indexOf(Integer.parseInt(array[0]));		
				randomInt2  = findcorrectreplacementtarget(solution, location, array, replacements,
						oldFeatureValue, replacementstype, indexof, randomInt2);
				if(this.unitfindtarget) {
					
				UpdateTargetgetBindingParameters(wrapper, modifiable, randomInt, featureDefinition,
						replacements, randomInt2, solution,location, indexof, replacementstype );
				this.comments.add(AddNewCommentsOnTransformation(oldFeatureValue, replacements,randomInt2, modifiable, randomInt ));
				this.checkmutationapply = true;
				ReturnResult=applyChange(randomInt, modifiable, ReturnResult, replacements, randomInt2,
						oldFeatureValue, comment, wrapper, atlModel);
				}

			}

		}

		return ReturnResult;
	}

	private void FindCandidateReplacements(int location, EObject oldFeatureValue3) {
		// TODO Auto-generated method stub
		
		
		if (!NSGAIIThreeStep.listoutpatternmodify.contains(location))
			NSGAIIThreeStep.listoutpatternmodify.add(location);
							
		int indexof2 = -1;
		// choose replacement that be accessed from out pattern
		indexof2 = NSGAIIThreeStep.classnamestringtarget.indexOf(toString(oldFeatureValue3));
		for (int y = NSGAIIThreeStep.classnamestartpointtarget
				.get(indexof2); y < NSGAIIThreeStep.classnamestartpointtarget.get(indexof2)
						+ NSGAIIThreeStep.classnamelengthtarget.get(indexof2); y++) {
			this.replacements.add(
					NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(y).getName());
			this.replacementstype.add(NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(y)
					.getEType().getName());
		}
		
	}

	private <ToModify extends LocatedElement> void UpdateTargetgetBindingParameters(ATLModel wrapper, List<ToModify> modifiable, int randomInt,
			EStructuralFeature featureDefinition, List<Object> replacements, int randomInt2, Solution solution,
			int location, int indexof, List<String> replacementstype) {
		
		wrapper.source(modifiable.get(randomInt)).eSet(featureDefinition,
				replacements.get(randomInt2));
		solution.getCoSolutionThreeStep().getOp().listpropertyname.get(location).set(indexof,
				replacements.get(randomInt2).toString());
		solution.getCoSolutionThreeStep().getOp().listpropertytype.get(location).set(indexof,
				replacementstype.get(randomInt2).toString());
		StoreTwoIndex(randomInt, randomInt2, location);
		
		
	}

	private <ToModify extends LocatedElement> String AddNewCommentsOnTransformation(Object oldFeatureValue, List<Object> replacements, int randomInt2,
			List<ToModify> modifiable, int randomInt) {
		// TODO Auto-generated method stub
		return "\n-- MUTATION \"" + this.getDescription() + "\" from "
		+ oldFeatureValue.toString() + " to " + replacements.get(randomInt2) + " (line "
		+ modifiable.get(randomInt).getLocation() + " of original transformation)\n";
		
	}



	  private ArrayList<Integer> FindSpecificTarget(Solution solution, int row, int column, int randomInt) {
		// TODO Auto-generated method stub
		  int ii = -1;
		  boolean satisfycondition = false;
		  ArrayList<Integer> row_column = new ArrayList<Integer>();
			for (int h = 0; h < solution.getCoSolutionThreeStep().getOp().listpropertyname.size(); h++) {
				int pp = -1;
				for (int h2 = 0; h2 < solution.getCoSolutionThreeStep().getOp().listpropertyname.get(h)
						.size(); h2++) {

					if (solution.getCoSolutionThreeStep().getOp().listpropertyname.get(h).get(h2) != null
							&& satisfycondition == false) {
						pp = pp + 1;
						ii = ii + 1;
						if (ii == randomInt) {
							row_column.add(h); // row
							row_column.add(h2); // column
							satisfycondition = true;
							break;
						}

					}
				}
			}

		return row_column;
	}

	protected <ToModify extends LocatedElement> List<Object> IteratorPredefinedCollectionOperation(ATLModel wrapper, int randomInt, String feature, String[] array, boolean checkmutationapply, EMFModel atlModel, MetaModel metamodel,
			  List<ToModify> modifiable, Solution solution, List<Object> ReturnResult,
			  String comment, CommonFunctionOperators cp ) {
		// TODO Auto-generated method stub
		  EStructuralFeature featureDefinition = wrapper.source(modifiable.get(randomInt)).eClass()
					.getEStructuralFeature(feature);
			int indexrule = -1;
			indexrule = FindIndexRule(array);
			if (modifiable.size() == 1 && !OperationsThreeStep.statecase.equals("case2")
					&& !OperationsThreeStep.statecase.equals("case3"))
				this.checkmutationapply = true;
				boolean check = true;
				if (check == true && indexrule >= 0) {
					EObject object2modify_src = wrapper.source(modifiable.get(randomInt));
					Object oldFeatureValue = object2modify_src.eGet(featureDefinition);	
					List<Object> replacements = this.replacements(atlModel, null, modifiable.get(randomInt),
						oldFeatureValue.toString(), metamodel);
	
					if ( replacements.size()>0
						&& !oldFeatureValue.toString().equals("toString")) {
						int randomInt2 = -2;
						boolean unitfind = false;
						randomInt2=checkReplacementChooseCorrect(OperationsThreeStep.statecase,array,oldFeatureValue,
							replacements,randomInt2,unitfind, atlModel,randomInt,metamodel,modifiable,solution,wrapper,indexrule, cp
							);
						if(randomInt2>=0)
							if(!replacements.get(randomInt2).equals("intersection")) {
						
								if(OperationsThreeStep.statecase.equals("case5")) {
									updatelineattributerefinement(oldFeatureValue.toString(), Integer.parseInt(array[0]), solution);
									if(this.indextype>0)
										solution.RHSattributerefinement.set(this.indextype, replacements.get(randomInt2).toString());
								}
								boolean ch=true;
								if(OperationsThreeStep.statecase.equals("case2")
										&& oldFeatureValue.toString().equals("oclIsKindOf")
										&& replacements.get(randomInt2).equals("oclIsTypeOf"))
									ch=false;
								if(ch)
								{
									this.mutationapply = true;
									wrapper.source(modifiable.get(randomInt)).eSet(featureDefinition,
									replacements.get(randomInt2));
									StoreTwoIndex(randomInt, randomInt2, indexrule);
									this.comments.add(AddNewCommentsOnTransformation(oldFeatureValue, replacements, randomInt2,
									modifiable, randomInt));
									ReturnResult = applyChange(randomInt, modifiable, ReturnResult, replacements, randomInt2,
									oldFeatureValue, comment, wrapper, atlModel);
						}
					
					}

				}

			}
			return ReturnResult ;
		}

	  private void updatelineattributerefinement(String navigation, int line, Solution solution) {
			// TODO Auto-generated method stub
			int id=solution.RHSattributerefinement.indexOf( Integer.toString(line));
			for(int i=id+1;i<solution.RHSattributerefinement.size();i++) {
				if(solution.RHSattributerefinement.get(i).equals(navigation)) {
					this.indextype=i;
					break;
				}
				
			}
		}

	protected <ToModify extends LocatedElement> Integer checkReplacementChooseCorrect(String statecase, String[] array, Object oldFeatureValue, List<Object> replacements, int randomInt2, boolean unitfind, EMFModel atlModel, int randomInt, MetaModel metamodel
			,List<ToModify> modifiable,Solution solution,ATLModel wrapper, int indexrule, CommonFunctionOperators cp) {
		// TODO Auto-generated method stub
			
		if(OperationsThreeStep.statecase.equals("case2")) {
			
			randomInt2 = FindSecondIndex(randomInt2, replacements.size());
			unitfind = true;
		}
		if(OperationsThreeStep.statecase.equals("case5")) {
			 	
			randomInt2=CollectionReplacementSection(array,solution,oldFeatureValue, atlModel,modifiable,randomInt, oldFeatureValue,
					metamodel, replacements, randomInt2, unitfind);
			
		}
		
		if(OperationsThreeStep.statecase.equals("case3")) {
			
			randomInt2=BindingTargetReplacementSelection(randomInt2, replacements, indexrule,wrapper,solution,array,modifiable,
					randomInt,cp,unitfind);
				}
		return randomInt2;
	}

	
	private <ToModify extends LocatedElement> int CollectionReplacementSection(String[] array, Solution solution, Object oldFeatureValue,
			EMFModel atlModel, List<ToModify> modifiable, int randomInt, Object oldFeatureValue2, MetaModel metamodel,
			List<Object> replacements2, int randomInt2, boolean unitfind) {
		// TODO Auto-generated method stub
		
		if(NSGAIIThreeStep.emptycollectionlocation.contains( Integer.parseInt(array[0]) ) ) {
			
			find_specific_collection_RHSBinding(solution,array,oldFeatureValue);
			// Type on attribute ra peida kon: Name, EString,...
			String typenextattr = null;
			for(int u=0;u<NSGAIIThreeStep.listinheritattributesourcemetamodel.size();u++)
			{
			if(NSGAIIThreeStep.listinheritattributesourcemetamodel.get(u).getName().equals(NSGAIIThreeStep.wholelineattributecollection.get(this.indextype+1 ) ))
			{
			    typenextattr=NSGAIIThreeStep.listnavigationtype.get( u);
			    break;
			}
			}
				
			if( typenextattr.equals("EString") || typenextattr.equals("String") || typenextattr.equals("EString") 
					|| !typenextattr.equals("boolean") || !typenextattr.equals("integer") || typenextattr.equals("collection") ||typenextattr.equals("Real"))
			{
		
				 replacements = this.replacements(atlModel,  null,
						modifiable.get(randomInt), oldFeatureValue.toString(), metamodel);
				 while (!unitfind) {
					 randomInt2 = FindSecondIndex(randomInt2, replacements.size()); 
				 if (!oldFeatureValue.toString().equals( replacements.get(randomInt2).toString()))
					unitfind = true;	
				 
				 }
			}
		}
		else {//operation 5:
					
			randomInt2 = FindSecondIndex(randomInt2, replacements.size());	
			unitfind = true;	
			if(replacements.get(randomInt2).toString().equals("excludes"))
				return -1;	
		}	
		return randomInt2;
	}

	private <ToModify extends LocatedElement> int BindingTargetReplacementSelection(int randomInt2, List<Object> replacements2, int indexrule, ATLModel wrapper,
			Solution solution, String[] array, List<ToModify> modifiable, int randomInt,CommonFunctionOperators cp ,boolean unitfind) {
		// TODO Auto-generated method stub
		
		randomInt2 = FindSecondIndex(randomInt2, replacements.size());
		//randomInt2 =0;
		String fromelement=findFromElementInpattern(indexrule,wrapper);
		findFromElementandLastattrIterationRHSRefinement(solution,array,fromelement);
		String[] array2 = null;
		if(randomInt-1>0) // if two iteration this binding 
		array2 = modifiable.get(randomInt-1).getLocation().split(":", 2);
		if(array2 != null) 
		if(this.typeAttribut.size()>0 && (replacements.get(randomInt2).equals("reject") || replacements.get(randomInt2).equals("sortedBy")) 
				 ) {
			String typeAttributeIteration=cp.findAttributeType(this.lastAttributIteration); //p.elems->select : p.elems=Classifier
			int idtypeAttribute=-1;//inheritclass: [[NamedElt, Package, Classifier, DataType, Class, Attribute], [Package], [Classifier, DataType, Class], [DataType], [Class], [Attribute]]
			for(int u=0;u<NSGAIIThreeStep.inheritClass.size();u++) {
				for(int u2=0;u2<NSGAIIThreeStep.inheritClass.get(u).size();u2++) {
				if(NSGAIIThreeStep.inheritClass.get(u).get(0).equals(typeAttributeIteration )) {
					idtypeAttribute=u; // [Classifier, DataType, Class] :2
					break;
				}
				}
			}
			boolean attriAccesible=true; // if there is attribute e.oclIsTypeOf(Class!Class))->reject(e |e.isAbstract	) Reject Type should hace access to attribute
			for(int u=1;u<NSGAIIThreeStep.inheritClass.get(idtypeAttribute).size();u++) {
			if(!NSGAIIThreeStep.inheritClass.get(idtypeAttribute).get(u).equals(solution.RHSattributerefinement.get(this.indextype) )) {	
				for(int d=0;d<this.typeAttribut.size();d++) {
				attriAccesible=cp.attributeAccesibleInpattern(NSGAIIThreeStep.inheritClass.get(idtypeAttribute).get(u), this.typeAttribut.get(d)); 
			if(!attriAccesible)
				break;
				}
			if(!attriAccesible)
					break;
			}
			
			}
			if(!attriAccesible)
			randomInt2 =-2;
		}
		unitfind = true;		

		
		return randomInt2;
	}

	private void find_specific_collection_RHSBinding(Solution solution, String[] array, Object oldFeatureValue) {
		
		int startlocation=solution.RHSattributerefinement.indexOf((array[0]) );
	
		for(int u=startlocation;u<solution.RHSattributerefinement.size() ;u++) {
			
			if(solution.RHSattributerefinement.get(u).equals( (oldFeatureValue.toString()))) {
				this.indextype=u;
				break;
				
			}
		}
	}

	private String findFromElementInpattern(int indexrule, ATLModel wrapper) {
		// TODO Auto-generated method stub
		List<InPatternElement> listinpattern = (List<InPatternElement>) wrapper.allObjectsOf(InPatternElement.class);
		return toString(listinpattern.get(indexrule));
		
	}

	private void findFromElementandLastattrIterationRHSRefinement(Solution solution, String[] array, String fromelement) {
		// TODO Auto-generated method stub
		
		CommonFunctionOperators cf=new CommonFunctionOperators();
		int indexwholelineattributlocation = solution.RHSattributelocation
				.indexOf(Integer.parseInt(array[0]));
		int startlocation = solution.RHSattributelocation.get(indexwholelineattributlocation + 1);
		int endlocation = solution.RHSattributelocation.get(indexwholelineattributlocation + 2);
		for (int u = startlocation; u <= endlocation; u++) {
			// find oldType in RHSattribute
			if (solution.RHSattributerefinement.get(u).equals(fromelement)) {
				this.indexAttributIteration = u;
				break;

			}

		}
		for (int u = this.indexAttributIteration+1; u <= endlocation; u++) {
			// find oldType in RHSattribute
			
			if (NSGAIIThreeStep.totallistattribute.contains(solution.RHSattributerefinement.get(u))) {
				this.lastAttributIteration = solution.RHSattributerefinement.get(u);
			}	
		}
		for(int u=this.indexAttributIteration+2;u<solution.RHSattributerefinement.size();u++) {
			int foo=cf.NextElementofRHSattribute(solution,u);
			if(solution.RHSattributelocation.contains( foo) || solution.RHSattributerefinement.get(u).equals(fromelement)) {
			break;	
			}
			else {
				if(solution.RHSattributerefinement.get(u).equals("oclIsTypeOf")
						|| solution.RHSattributerefinement.get(u).equals("oclIsKindOf")) {
					this.indextype=u+2;
					
				}
				if(solution.RHSattributerefinement.get(u).equals(solution.RHSattributerefinement.get(u+1))
						&& solution.RHSattributerefinement.get(u).length()==1
						&& NSGAIIThreeStep.totallistattribute.contains(solution.RHSattributerefinement.get(u+2)))
						{
					this.typeAttribut.add(solution.RHSattributerefinement.get(u+2));
				}
			}
		}
	}

	private int findtargetlocationourtwodimentionalarray(Solution solution, String[] array) {
		// TODO Auto-generated method stub
		int location = 0;
		for (int h = 0; h < solution.getCoSolutionThreeStep().getOp().listpropertynamelocation
				.size(); h++) {
			if (solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(h)
					.size() > 0)
				if (solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(h)
						.get(0) <= Integer.parseInt(array[0])
						&& Integer.parseInt(array[0]) <= solution.getCoSolutionThreeStep()
								.getOp().listpropertynamelocation
										.get(h)
										.get(solution.getCoSolutionThreeStep()
												.getOp().listpropertynamelocation.get(h).size()
												- 1)) {
					location = h;
					break;
				}
		}

		return location;
	}

	private Integer findcorrectreplacementtarget(Solution solution, int location, String[] array,
			List<Object> replacements, Object oldFeatureValue, List<String> replacementstype, int indexof, int randomInt2) {
		// TODO Auto-generated method stub
		
		ArrayList<Integer> candidaterandomInt2 = new ArrayList<Integer>();
		while (!this.unitfindtarget) {

				randomInt2 = FindSecondInd(randomInt2, replacements.size());
				if(!candidaterandomInt2.contains( randomInt2))
					candidaterandomInt2.add(randomInt2 );
			// choose attribute with same type of right
			if (!oldFeatureValue.equals(replacements.get(randomInt2))
					&& !replacements.get(randomInt2).toString().equals("orderedMessages")) {
				if ((solution.getCoSolutionThreeStep().getOp().listpropertytype.get(location).get(indexof)
						.equals("Boolean") && replacementstype.get(randomInt2).equals("Boolean"))
						|| (solution.getCoSolutionThreeStep().getOp().listpropertytype.get(location).get(indexof)
								.equals("EBoolean") && replacementstype.get(randomInt2).equals("EBoolean"))
						|| (!solution.getCoSolutionThreeStep().getOp().listpropertytype.get(location).get(indexof)
								.equals("EBoolean") && !solution.getCoSolutionThreeStep().getOp().listpropertytype.get(location).get(indexof)
								.equals("Boolean") && !replacementstype.get(randomInt2).equals("EBoolean") 
								&& !replacementstype.get(randomInt2).equals("Boolean") )
						)
						
					this.unitfindtarget = true;
				 break;
				
			}
			if(candidaterandomInt2.size()==replacements.size())
			{
				
				this.unitfindtarget = false;
				break;
				
			}
		}

		return randomInt2;
	}

	private EObject findoutputforthistarget(ATLModel wrapper, int indexrule, String[] array,
			List<OutPatternElement> modifiable2) {
		// TODO Auto-generated method stub
		int indexout = -1;
		for (int i = 0; i < modifiable2.size(); i++) {

			String[] array2 = modifiable2.get(i).getLocation().split(":", 2);
			int indexrule2 = -1;
			indexrule2 = FindIndexRule(array2);
			if (indexrule == indexrule2 && Integer.parseInt(array[0]) > Integer.parseInt(array2[0])) {
				indexout = i;
			}
		}

		// output ra ke peida kard stringesh ra bar migardoone
		EStructuralFeature featureDefinitionout = wrapper.source(modifiable2.get(indexout)).eClass()
				.getEStructuralFeature("type");
		EObject object2modify_src3 = wrapper.source(modifiable2.get(indexout));
		EObject oldFeatureValue3 = (EObject) object2modify_src3.eGet(featureDefinitionout);

		return oldFeatureValue3;
	}

	private <ToModify extends LocatedElement> List<Object> applyChange(int randomInt, List<ToModify> modifiable,
			List<Object> ReturnResult, List<Object> replacements, int randomInt2, Object oldFeatureValue,
			String comment, ATLModel wrapper, EMFModel atlModel) {
		
		comment = AddNewCommentsOnTransformation(oldFeatureValue, replacements, randomInt2,
				modifiable, randomInt);
		NSGAIIThreeStep.writer.println(randomInt);
		NSGAIIThreeStep.writer.println(randomInt2);
		NSGAIIThreeStep.numop = NSGAIIThreeStep.numop + 1;
		ReturnResult.set(0, wrapper);
		ReturnResult.set(1, atlModel);
		ReturnResult.add(comment);

		return ReturnResult;
	}

	

	private <ToModify extends LocatedElement> List<Object> OperationPreviousGenerationModefyBinding(int randomInt,
			Solution solution, EMFModel atlModel, MetaModel metamodel, List<ToModify> modifiable, ATLModel wrapper,
			String feature, List<Object> ReturnResult) {
		
		if (!OperationsThreeStep.statecase.equals("case5") && !OperationsThreeStep.statecase.equals("case2")
				&& !OperationsThreeStep.statecase.equals("case3")) {
			if (solution.inorout.get(MyProblemThreeStep.indexoperation - 1).equals("out")) {

				ReturnResult=BindingTargetOnNewGeneratedTarget(wrapper,randomInt,
						solution, atlModel, ReturnResult);
				
			} else { // 3:Binding target --- for running crossover
				
				ReturnResult=BindingTargetExistingTarget(modifiable, randomInt, wrapper,feature,solution, ReturnResult, atlModel );
				}
		} else { // crossover :operation 5 and operation 2, operation 10: crossover 
			String comment = null;
			EStructuralFeature featureDefinition = wrapper.source(modifiable.get(randomInt)).eClass()
					.getEStructuralFeature(feature);
			EObject object2modify_src = wrapper.source(modifiable.get(randomInt));
			Object oldFeatureValue = object2modify_src.eGet(featureDefinition);
			List<Object> replacements = this.replacements(atlModel, null, modifiable.get(randomInt),
					oldFeatureValue.toString(), metamodel);
			String[] array = modifiable.get(randomInt).getLocation().split(":", 2);
			int randomInt2 = -2;
			randomInt2 = FindSecondIndex(randomInt2, replacements.size());
			if(OperationsThreeStep.statecase.equals("case5")) {
				updatelineattributerefinement(oldFeatureValue.toString(), Integer.parseInt(array[0]), solution);
				solution.RHSattributerefinement.set(this.indextype, replacements.get(randomInt2).toString());
				}
			wrapper.source(modifiable.get(randomInt)).eSet(featureDefinition, replacements.get(randomInt2));
			StoreTwoIndex(randomInt, randomInt2, -2);
			this.comments.add(AddNewCommentsOnTransformation(oldFeatureValue, replacements, randomInt2,
					modifiable, randomInt));
			ReturnResult = applyChange(randomInt, modifiable, ReturnResult, replacements, randomInt2,
					oldFeatureValue, comment, wrapper, atlModel);
		}

		return ReturnResult;
	}

	private <ToModify extends LocatedElement> List<Object> BindingTargetExistingTarget(List<ToModify> modifiable, int randomInt, ATLModel wrapper,
			String feature, Solution solution, List<Object> ReturnResult, EMFModel atlModel) {
		// TODO Auto-generated method stub
		String comment = null;
		ToModify modifiable2 = modifiable.get(randomInt);
		String[] array = modifiable2.getLocation().split(":", 2);
		if (!solution.listlineofcodes.contains(Integer.parseInt(array[0]))) {
			
			EStructuralFeature featureDefinition = wrapper.source(modifiable2).eClass()
					.getEStructuralFeature(feature);
			EObject object2modify_src = wrapper.source(modifiable2);
			Object oldFeatureValue = object2modify_src.eGet(featureDefinition);
			int location = -2;
			location = FindThirdIndex(location);
			EClassifier eclassifier = solution.listeobject.get(MyProblemThreeStep.indexoperation - 1);
			// khati ke taghiir mikone
			int indexof = solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(location)
					.indexOf(Integer.parseInt(array[0]));  
			List<Object> replacements = new ArrayList<Object>();
			List<String> replacementstype = new ArrayList<String>();
			int indexof2 = -1;
			// choose replacement that be accessed from out pattern
			indexof2 = NSGAIIThreeStep.classnamestringtarget.indexOf(eclassifier.getName());
			for (int y = NSGAIIThreeStep.classnamestartpointtarget
					.get(indexof2); y < NSGAIIThreeStep.classnamestartpointtarget.get(indexof2)
							+ NSGAIIThreeStep.classnamelengthtarget.get(indexof2); y++) {
				replacements.add(
						NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(y).getName());
				replacementstype.add(NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(y)
						.getEType().getName());
			}
			
			int randomInt2 = -2;
			randomInt2 = FindSecondIndex(randomInt2, replacements.size());
			if (!oldFeatureValue.toString().equals(replacements.get(randomInt2).toString())) {
				wrapper.source(modifiable2).eSet(featureDefinition, replacements.get(randomInt2));
				solution.getCoSolutionThreeStep().getOp().listpropertyname.get(location).set(indexof,
						replacements.get(randomInt2).toString());
				StoreTwoIndex(randomInt, randomInt2, location);
				comment = AddNewCommentsOnTransformation(oldFeatureValue, replacements, randomInt2,
						modifiable, randomInt);
				this.comments.add(comment);					
				NSGAIIThreeStep.numop = NSGAIIThreeStep.numop + 1;
				ReturnResult.set(0, wrapper);
				ReturnResult.set(1, atlModel);
				ReturnResult.add(comment);

			} else {
				StoreTwoIndex(randomInt, randomInt2, location);
			}

		} else {
			StoreTwoIndex(-2, -2, -2);
			solution.inorout.set(MyProblemThreeStep.indexoperation - 1, "empty");
			solution.newbindings.set(MyProblemThreeStep.indexoperation - 1, null);

		}


		return ReturnResult;
	}

	private <ToModify extends LocatedElement> List<Object> BindingTargetOnNewGeneratedTarget(ATLModel wrapper, int randomInt, Solution solution, EMFModel atlModel, List<Object> ReturnResult) {
		// TODO Auto-generated method stub
		List<ToModify> containers = (List<ToModify>) wrapper.allObjectsOf(OutPatternElement.class);
		EStructuralFeature feature2 = wrapper.source(containers.get(randomInt)).eClass()
				.getEStructuralFeature("bindings");
		List<Binding> realbindings = (List<Binding>) wrapper.source(containers.get(randomInt)).eGet(feature2);
		int index = -1;
		Binding b = solution.newbindings.get(MyProblemThreeStep.indexoperation - 1);
		String classifier2 = solution.modifypropertyname.get(MyProblemThreeStep.indexoperation - 1);
		  for (int i = 0; i < realbindings.size(); i++) {
			if (solution.getCoSolutionThreeStep().getOp().originalwrapper.get(randomInt).get(i) == 1
					&& realbindings.get(i).getValue().getClass().equals(b.getValue().getClass())
					&& realbindings.get(i).getPropertyName().equals(classifier2)) {
						index = i;
			}

		}

		if (index != -1) {
			int randomInt2 = -2;
			randomInt2 = FindSecondIndex(randomInt2, -1);
			Binding binding = realbindings.get(index);
			String oldFeatureValue = binding.getPropertyName();
			binding.setPropertyName(solution.newstring.get(MyProblemThreeStep.indexoperation - 1));
			solution.getCoSolutionThreeStep().getOp().listpropertyname.get(randomInt).set(randomInt2,
					solution.newstring.get(MyProblemThreeStep.indexoperation - 1));
			realbindings.set(index, binding);
			String comment = null;
			comment = "\n-- MUTATION \"" + this.getDescription() + "\" from " + oldFeatureValue + " to "
					+ solution.newstring.get(MyProblemThreeStep.indexoperation - 1) + " (line "
					+ solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(randomInt)
							.get(randomInt2)
					+ " of original transformation)\n";
			this.comments.add(comment);
			NSGAIIThreeStep.numop = NSGAIIThreeStep.numop + 1;
			StoreTwoIndex(randomInt, randomInt2, randomInt);
			ReturnResult.set(0, wrapper);
			ReturnResult.set(1, atlModel);
			ReturnResult.add(comment);

		}

		else {
			StoreTwoIndex(-2, -2, -2);
			solution.inorout.set(MyProblemThreeStep.indexoperation - 1, "empty");
			solution.newbindings.set(MyProblemThreeStep.indexoperation - 1, null);

		}
		return ReturnResult;
	}

	protected int FindIndexRule(String[] array) {
		// TODO Auto-generated method stub
		int indexrule = -1;
		for (int j = 0; j < NSGAIIThreeStep.faultrule.size(); j++) {

			if (Integer.parseInt(array[0]) > NSGAIIThreeStep.faultrule.get(j)
					&& Integer.parseInt(array[0]) < NSGAIIThreeStep.finalrule.get(j))

				indexrule = j;
		}
		return indexrule;

	}

	private int FindThirdIndex(int randomInt2) {
		// TODO Auto-generated method stub
		if (NSGAIIThreeStep.statemutcrossinitial == "mutation") {

			switch (MyProblemThreeStep.indexoperation) {
			case 1:
				randomInt2=RetrieveSecondParameter( randomInt2, MyProblemThreeStep.secondoldoperation1);
				break;
			case 2:
				randomInt2=RetrieveSecondParameter( randomInt2, MyProblemThreeStep.secondoldoperation2);
				break;
			case 3:
				randomInt2=RetrieveSecondParameter( randomInt2, MyProblemThreeStep.secondoldoperation3);
				break;
			case 4:
				randomInt2=RetrieveSecondParameter( randomInt2, MyProblemThreeStep.secondoldoperation4);
				break;
			case 5:
				randomInt2=RetrieveSecondParameter( randomInt2, MyProblemThreeStep.secondoldoperation5);
				break;
			case 6:
				randomInt2=RetrieveSecondParameter( randomInt2, MyProblemThreeStep.secondoldoperation6);
				break;
			case 7:
				randomInt2=RetrieveSecondParameter( randomInt2, MyProblemThreeStep.secondoldoperation7);
				break;
			case 8:
				randomInt2=RetrieveSecondParameter( randomInt2, MyProblemThreeStep.secondoldoperation8);
				break;
			case 9:
				randomInt2=RetrieveSecondParameter( randomInt2, MyProblemThreeStep.secondoldoperation9);
				break;
			
			}
		}
		return randomInt2;
	}

	private int RetrieveSecondParameter(int randomInt2, int secondoldoperation) {
		// TODO Auto-generated method stub
		if (secondoldoperation != -1)
			randomInt2 =  secondoldoperation;
		return randomInt2;
	}

	protected int FindSecondInd(int randomInt2, int size) {
		Random number_generator = new Random();
		randomInt2 = number_generator.nextInt(size);
		return randomInt2;
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
	protected int FindSecondIndex(int randomInt2, int size) {
		// TODO Auto-generated method stub
		if (NSGAIIThreeStep.statemutcrossinitial == "mutation") {

			switch (MyProblemThreeStep.indexoperation) {
			case 1:
				randomInt2 =GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation1, randomInt2,  size);
				break;
			case 2:
				randomInt2 =GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation2, randomInt2,  size);
				break;
			case 3:
				randomInt2 =GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation3, randomInt2,  size);
				break;
			case 4:
				randomInt2 =GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation4, randomInt2,  size);				
				break;
			case 5:
				randomInt2 =GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation5, randomInt2,  size);
				break;
			case 6:
				randomInt2 =GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation6, randomInt2,  size);
				break;
			case 7:
				randomInt2 =GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation7, randomInt2,  size);
				break;
			case 8:
				randomInt2 =GenerateThirdRandomNumber(MyProblemThreeStep.replaceoperation8, randomInt2,  size);
				break;
			}

		} else {
			Random number_generator = new Random();
			randomInt2 = number_generator.nextInt(size);
		}

		return randomInt2;
	}

	
	protected void StoreTwoIndex(int randomInt, int randomInt2, int location) {
		// TODO Auto-generated method stub

		switch (MyProblemThreeStep.indexoperation) {
		case 1:
			MyProblemThreeStep.oldoperation1 = randomInt;
			MyProblemThreeStep.replaceoperation1 = randomInt2;
			MyProblemThreeStep.secondoldoperation1 = location;

			break;
		case 2:
			MyProblemThreeStep.oldoperation2 = randomInt;
			MyProblemThreeStep.replaceoperation2 = randomInt2;
			MyProblemThreeStep.secondoldoperation2 = location;

			break;
		case 3:
			MyProblemThreeStep.oldoperation3 = randomInt;
			MyProblemThreeStep.replaceoperation3 = randomInt2;
			MyProblemThreeStep.secondoldoperation3 = location;

			break;
		case 4:
			MyProblemThreeStep.oldoperation4 = randomInt;
			MyProblemThreeStep.replaceoperation4 = randomInt2;
			MyProblemThreeStep.secondoldoperation4 = location;

			break;
		case 5:
			MyProblemThreeStep.oldoperation5 = randomInt;
			MyProblemThreeStep.replaceoperation5 = randomInt2;
			MyProblemThreeStep.secondoldoperation5 = location;

			break;
		case 6:
			MyProblemThreeStep.oldoperation6 = randomInt;
			MyProblemThreeStep.replaceoperation6 = randomInt2;
			MyProblemThreeStep.secondoldoperation6 = location;

			break;
		case 7:
			MyProblemThreeStep.oldoperation7 = randomInt;
			MyProblemThreeStep.replaceoperation7 = randomInt2;
			MyProblemThreeStep.secondoldoperation7 = location;

			break;
		case 8:
			MyProblemThreeStep.oldoperation8 = randomInt;
			MyProblemThreeStep.replaceoperation8 = randomInt2;
			MyProblemThreeStep.secondoldoperation8 = location;

			break;
		}

	}

}
