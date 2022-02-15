package deletion.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import cd.udem.fixingatlerror.OperationsThreeStep;
import jmetal.core.Solution;
import jmetal.metaheuristics.nsgaII.NSGAIIThreeStep;
import jmetal.problems.MyProblemThreeStep;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import anatlyzer.atl.analyser.Analyser;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atl.model.ATLModel;
import evaluation.mutator.AbstractMutator;
import witness.generator.MetaModel;

public abstract class AbstractDeletionMutator extends AbstractMutator {

	static int i = 1;
	int indexoriginal = -1;
	Analyser analyser;
	EDataTypeEList<String> comments = null;
	boolean checkmutationapply = false;
		/**
	 * Generic deletion. It allows subtypes of both the container class and the
	 * class to delete.
	 * 
	 * @param atlModel
	 * @param outputFolder
	 * @param ContainerClass
	 *            container class of the class of objects to delete (example
	 *            OutPattern)
	 * @param ToDeleteClass
	 *            class of objects to delete (example Binding)
	 * @param relation
	 *            containment relation (example bindings)
	 * @return
	 */
	protected <Container extends LocatedElement, ToDelete/* extends LocatedElement */> List<Object> genericDeletion(
			EMFModel atlModel, String outputFolder, Class<Container> ContainerClass, Class<ToDelete> ToDeleteClass,
			String relation, ATLModel wrapper, Solution solution,MetaModel outputMM,CommonFunctionOperators cp) {
		return this.genericDeletion(atlModel, outputFolder, ContainerClass, ToDeleteClass, relation, false, wrapper,
				solution, outputMM,cp);

	}

	
	/**
	 * Generic deletion. It allows subtypes of the class to delete. The parameter
	 * 'exactContainerType' allows configuring whether the type of the container
	 * must be exactly the one received, or if the subtypes should be also
	 * considered.
	 * 
	 * @param atlModel
	 * @param outputFolder
	 * @param ContainerClass
	 *            container class of the class of objects to delete (example
	 *            OutPattern)
	 * @param ToDeleteClass
	 *            class of objects to delete (example Binding)
	 * @param relation
	 *            containment relation (example bindings)
	 * @param exactContainerType
	 *            false to consider also subtypes of the ContainerClass, true to
	 *            discard subtypes of the ContainerClass
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

	private EClass getCompatible(EClass c, List<EClass> candidates) {
		EClass compatible = null;
		for (int i = 0; i < candidates.size() && compatible == null; i++) {
			EClass c2 = candidates.get(i);
			if (isCompatibleWith(c2, c))
				compatible = c2;
		}
		return compatible;
	}

	protected EClass getCompatibleSubclass(EClass c, MetaModel mm) {
		List<EClass> subclasses = subclasses(c, mm);
		return getCompatible(c, subclasses);
	}

	protected EClass getCompatibleSuperclass(EClass c) {
		List<EClass> superclasses = c.getEAllSuperTypes();
		return c.getEStructuralFeatures().size() > 0 ? getCompatible(c, superclasses) : null;
	}

	protected EClass getCompatibleUnrelatedClass(EClass c, MetaModel mm) {
		List<EClass> unrelatedClasses = unrelatedClasses(c, mm);
		return getCompatible(c, unrelatedClasses);
	}

	protected EClass getIncompatibleSubclass(EClass c, MetaModel mm) {
		List<EClass> subclasses = subclasses(c, mm);
		return getIncompatible(c, subclasses);
	}

	private EClass getIncompatible(EClass c, List<EClass> candidates) {
		EClass incompatible = null;
		for (int i = 0; i < candidates.size() && incompatible == null; i++) {
			EClass c2 = candidates.get(i);
			if (!isCompatibleWith(c2, c))
				incompatible = c2;
		}
		return incompatible;
	}

	protected EClass getIncompatibleUnrelatedClass(EClass c, MetaModel mm) {
		List<EClass> unrelatedClasses = unrelatedClasses(c, mm);
		return getIncompatible(c, unrelatedClasses);
	}

	protected <Container extends LocatedElement, ToDelete/* extends LocatedElement */> List<Object> genericDeletion(
			EMFModel atlModel, String outputFolder, Class<Container> ContainerClass, Class<ToDelete> ToDeleteClass,
			String relation, boolean exactContainerType, ATLModel wrapper, Solution solution,MetaModel outputMM,CommonFunctionOperators cp) {
				
				List<Object> ReturnResult = new ArrayList<Object>();
				List<Container> containers = (List<Container>) wrapper.allObjectsOf(ContainerClass);
				ReturnResult.add(wrapper);
				ReturnResult.add(atlModel);
				Module module = wrapper.getModule();
				String comment = null;
				if (module != null) {
					EStructuralFeature feature = wrapper.source(module).eClass().getEStructuralFeature("commentsAfter");
					comments = (EDataTypeEList<String>) wrapper.source(module).eGet(feature);
				}

				if (exactContainerType)
					filterSubtypes(containers, ContainerClass);
				int randomInt = -2;
				ArrayList<Integer> filterrule = new ArrayList<Integer>(); 
				while (!checkmutationapply) {
					
					List<Integer> Result = cp.ReturnFirstIndex(randomInt, containers.size(), checkmutationapply, solution,null,null);
					randomInt = Result.get(0);
					if (solution.getpreviousgeneration()) {
						ReturnResult = OperationPreviousGenerationDeletion(randomInt, solution, atlModel, containers, wrapper,
						relation, comments, ReturnResult, outputMM, cp);
						checkmutationapply = true;
					} else if (!solution.getpreviousgeneration()) {
						// choose inpattern
						String[] array = containers.get(randomInt).getLocation().split(":", 2);
						int indexrule = cp.FindRule(array);	
						filterrule.add(indexrule);
				
						if (containers.size() > 0 ) {

							EStructuralFeature feature = wrapper.source(containers.get(randomInt)).eClass()
							.getEStructuralFeature(relation);

							if (feature != null) {
						
								if (feature.getUpperBound() == 1 && feature.getLowerBound() == 0) {
							
									ReturnResult=DeleteFilter(wrapper,containers, randomInt, feature, ReturnResult,
									filterrule,comment, atlModel, solution, ToDeleteClass, indexrule );
														
								}
								// CASE 2: multivalued feature

								else { 
									// link: total bindins in inpattern
									ReturnResult=DeleteBinding(wrapper,containers,randomInt, solution, ReturnResult, feature, array 
									,comment, atlModel, cp, Result);
							
								}
						}
				 }
			}

		}

		return ReturnResult;
	}

	
	private <Container extends LocatedElement, ToDelete >List<Object> DeleteBinding(ATLModel wrapper, List<Container> containers, int randomInt, Solution solution,
			List<Object> ReturnResult, EStructuralFeature feature, String[] array, String comment, EMFModel atlModel,
			CommonFunctionOperators cp, List<Integer> Result) {
		// TODO Auto-generated method stub
		List<EObject> link = (List<EObject>) wrapper.source(containers.get(randomInt))
				.eGet(feature);

		if (feature.getLowerBound() == link.size())
			checkmutationapply = true;
		if (feature.getLowerBound() < link.size()) {

			int size = link.size();
			int randomInt2 = -2;//size:list of binding
			ArrayList<Integer> tempsecondint = receivesecondrandomInt(randomInt2, size, solution,
					randomInt); 
			randomInt2 = tempsecondint.get(1);
			if (randomInt2 == -1) {
				checkmutationapply = true;
			}
			// mutation: remove object
			if (randomInt2 != -1) { 
				
				// find selected binding in attribute list
				// row: listpropertyname
				int row = FindSelectedInpatternPropertyName(array,solution); // array:line inpattern, listpropertylocation:binding1[,,]bindin2[]
				int lastindex=FindSelectedBindingPropertyName(solution,row,randomInt2);
				LocatedElement object = null;
			  // delete new created binding
				if(this.indexoriginal < randomInt2) 
				{   if(row>=0)
					if(solution.getCoSolutionThreeStep().getOp().listpropertyname.get(row).get(lastindex)!=null)
					{
						List<Binding> realbindings=cp.extractAllBindingOutpattern(wrapper,containers,randomInt); 
						solution.newbindings.set(MyProblemThreeStep.indexoperation-1, realbindings.get(randomInt2));
						solution.modifypropertyname.set(MyProblemThreeStep.indexoperation - 1,
								solution.getCoSolutionThreeStep().getOp().listpropertyname.get(row).get(lastindex));
						realbindings.remove(randomInt2);
						comments.add(AddNewCommentExtraBinding( solution,  containers,  randomInt,  row,  lastindex));
						tempsecondint.set(0, lastindex);
						tempsecondint.set(1, lastindex);
						updaterandomInt(randomInt, tempsecondint, solution);
						solution.inorout.set(MyProblemThreeStep.indexoperation - 1, "out");
						comment = AddNewCommentExtraBinding(solution, containers, randomInt, row, lastindex);
						NSGAIIThreeStep.numop = NSGAIIThreeStep.numop + 1;
						checkmutationapply = true;
						solution.getCoSolutionThreeStep().getOp().listpropertyname.get(row).set(lastindex,
								null);
						ReturnResult.set(0, wrapper);
						ReturnResult.set(1, atlModel);
						ReturnResult.add(comment);

				}	
				}
				else { // Operations 11: deletion of filter
					boolean listlineofcode = false;
					EObject eobject = link.get(randomInt2); // specific binding
					object = (LocatedElement) wrapper.target(eobject);
					String[] array2 = object.getLocation().split(":", 2);//location binding
					if (!solution.listlineofcodes.contains(Integer.parseInt(array2[0])))
						listlineofcode = true;
					if (Result.get(1) == 1
							&& solution.listlineofcodes.contains(Integer.parseInt(array2[0])))
						checkmutationapply = false;

					if (listlineofcode) {
						
						int location = findRowListProperty(array2,solution);
						int indexof = findRelatedColumnListProperty(array2,solution,location);
						link.remove(randomInt2);
						NSGAIIThreeStep.deletlist.add(randomInt);
						tempsecondint.add(0, randomInt2);
						tempsecondint.add(1, randomInt2);
						updaterandomInt(randomInt, tempsecondint, solution);
						comment =AddNewComment(object, containers, randomInt);
						comments.add(comment);
						solution.listlineofcodes.add(Integer.parseInt(array2[0]));
						NSGAIIThreeStep.numop = NSGAIIThreeStep.numop + 1;
						checkmutationapply = true;
						solution.getCoSolutionThreeStep().getOp().listpropertyname.get(row).set(lastindex,
								null);
						ReturnResult.set(0, wrapper);
						ReturnResult.set(1, atlModel);
						ReturnResult.add(comment);

						}
				}
			}

		}

		return ReturnResult;
	}


	private <Container extends LocatedElement, ToDelete >  List<Object> DeleteFilter(ATLModel wrapper, List<Container> containers, int randomInt,
			EStructuralFeature feature, List<Object> ReturnResult,ArrayList<Integer> filterrule, String comment, EMFModel atlModel, Solution solution
			, Class<ToDelete> ToDeleteClass, int indexrule) {
	
			EObject link = (EObject) wrapper.source(containers.get(randomInt)).eGet(feature);
			boolean filterrulecheck=false;
			for(int p=0;p<NSGAIIThreeStep.errorrule.size();p++)
				if(!filterrule.contains(NSGAIIThreeStep.errorrule.get(p)) &&  !NSGAIIThreeStep.listfilterapplied.contains(NSGAIIThreeStep.errorrule.get(p)))
					filterrulecheck=true;
 
			if(!filterrulecheck)
				checkmutationapply = true;
			if (link != null) {
				LocatedElement object = (LocatedElement) wrapper.target(link);
				boolean listlineofcode = false;  
		        	String[] array2 = object.getLocation().split(":", 2);
		        	if (!solution.listfilterdeletion.contains(Integer.parseInt(array2[0])))
		        		listlineofcode = true;     
		        	if (ToDeleteClass.isAssignableFrom(object.getClass()) && listlineofcode == true) {
		        		wrapper.source(containers.get(randomInt)).eSet(feature, null);
		        		updatefirstIndex(randomInt);
		        		comments.add(AddNewComment(object, containers, randomInt));
		        		comment = AddNewComment(object, containers, randomInt);
		        		solution.listfilterdeletion.add(Integer.parseInt(array2[0]));
		        		NSGAIIThreeStep.listfilterapplied.add(indexrule);
		        		NSGAIIThreeStep.numop = NSGAIIThreeStep.numop + 1;
		        		checkmutationapply = true;
		        		ReturnResult.set(0, wrapper);
		        		ReturnResult.set(1, atlModel);
		        		ReturnResult.add(comment);
				}

		}

		return ReturnResult;
	}

	
	private  <Container extends LocatedElement, ToDelete/* extends LocatedElement */> String AddNewCommentExistingBinding(Solution solution, List<Container> containers, int randomInt, int index) {
		
		return 
				"\n-- MUTATION \"" + this.getDescription() + "\" "
				+ solution.getCoSolutionThreeStep().getOp().listpropertyname.get(randomInt)
						.get(index)
				+ " in " + toString(containers.get(randomInt)) + " (line "
				+ solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(randomInt)
						.get(index)
				+ " of original transformation)\n";
	}
	private  <Container extends LocatedElement, ToDelete/* extends LocatedElement */> String AddNewCommentExtraBinding(Solution solution, List<Container> containers, int randomInt, int row, int lastindex) {
		
		return "\n-- outdelet1MUTATION \"" + this.getDescription() + "\" "
				+ solution.getCoSolutionThreeStep().getOp().listpropertyname.get(row)
				.get(lastindex)
		+ " in " + toString(containers.get(randomInt)) + " (line "
		+ solution.getCoSolutionThreeStep().getOp().listpropertynamelocation
				.get(row).get(lastindex)
		+ " of original transformation)\n";
	}
	
	
	private <Container extends LocatedElement, ToDelete> String AddNewComment(LocatedElement object, List<Container> containers, int randomInt) {
		// TODO Auto-generated method stub
	 return	"\n-- MUTATION \"" + this.getDescription() + "\" "
		+ toString(object) + " in " + toString(containers.get(randomInt))
		+ " (line " + object.getLocation() + " of original transformation)\n";
	}

	private int findRelatedColumnListProperty(String[] array2, Solution solution, int location) {
		// TODO Auto-generated method stub
		for(int p=0;p<solution.getCoSolutionThreeStep().getOp().listpropertynamelocation
				.get(location).size();p++)
			if(solution.getCoSolutionThreeStep().getOp().listpropertynamelocation
				.get(location).get(p).equals(  Integer.parseInt(array2[0]) ))
				return p;
		return 0;
	}

	private int findRowListProperty(String[] array2, Solution solution) {
		// TODO Auto-generated method stub
		for (int h = 0; h < solution.getCoSolutionThreeStep()
				.getOp().listpropertynamelocation.size(); h++) {
			if(solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(h).size()>0)
			{
			if (solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(h)
					.get(0) <= Integer.parseInt(array2[0])
					&& Integer.parseInt(array2[0]) <= solution.getCoSolutionThreeStep()
							.getOp().listpropertynamelocation
									.get(h)
									.get(solution.getCoSolutionThreeStep()
											.getOp().listpropertynamelocation.get(h)
													.size()
											- 1)) {
				return h;
			
			}
		}
	}

		return 0;
	}

	private int FindSelectedBindingPropertyName(Solution solution, int row, int randomInt2) {
		// TODO Auto-generated method stub
		
		int newindex = -1;
		this.indexoriginal=-1;
		boolean b = false; // find selected binding in listpropertyname
		int lastindex = -1; //originalwrapper(row) [,,,,] , LHS of binding
		if(row>=0)
		for (int h = 0; h < solution.getCoSolutionThreeStep().getOp().originalwrapper.get(row)
				.size(); h++) { //!null: exist binding 
			if (solution.getCoSolutionThreeStep().getOp().listpropertyname.get(row).get(h) != null
					&& solution.getCoSolutionThreeStep().getOp().originalwrapper.get(row)
							.get(h) == 0) {
				this.indexoriginal = this.indexoriginal + 1;
				newindex = this.indexoriginal;
				// choose LHS = binding choosed in ATL
				if (this.indexoriginal == randomInt2) { // find index binding
					lastindex = h;
					
				}
		}
			// if selected binding is new binding not original ATL
			else if (solution.getCoSolutionThreeStep().getOp().originalwrapper.get(row).get(h) == 1
					&& b == false && solution.getCoSolutionThreeStep().getOp().listpropertyname
							.get(row).get(h) != null) {
				newindex = newindex + 1;
				if (newindex == randomInt2) {
					lastindex = h;
					b = true;
					break;
				}
			}

		}

		return lastindex;
	}

	private int FindSelectedInpatternPropertyName(String[] array, Solution solution) {
		for (int h = 0; h < solution.getCoSolutionThreeStep().getOp().listpropertynamelocation
				.size(); h++) {
			if(solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(h).size()>0)
			{
			if (solution.getCoSolutionThreeStep().getOp().listpropertynamelocation.get(h)
					.get(0) == Integer.parseInt(array[0]) + 1) {
				     return h;
				
			}
			}
		}

		return -1;
	}

	private <Container extends LocatedElement, ToDelete> List<Object> OperationPreviousGenerationDeletion(
			int randomInt, Solution solution, EMFModel atlModel, List<Container> containers, ATLModel wrapper,
			String relation, EDataTypeEList<String> comments, List<Object> ReturnResult,MetaModel outputMM , CommonFunctionOperators cp) {
	
		if (solution.inorout.get(MyProblemThreeStep.indexoperation-1).equals("out")) {
			
			ReturnResult=DeleteBindingCrossoverNewBinding(wrapper,containers,randomInt, solution, ReturnResult, 
					atlModel, cp);
			
			}
		else {
			if(OperationsThreeStep.statecase.equals("case2")) {
				
				ReturnResult=PredefindDeletion(containers,randomInt,wrapper,relation, solution, ReturnResult, atlModel, cp );
			}
			
		else {
			String comment = null;
			// TODO Auto-generated method stub
			Container modifiable2 = containers.get(randomInt);
			EStructuralFeature feature = wrapper.source(modifiable2).eClass().getEStructuralFeature(relation);
			List<EObject> link = (List<EObject>) wrapper.source(modifiable2).eGet(feature);
			int randomInt2 = -2;
			ArrayList<Integer> tempsecondint = receivesecondrandomInt(randomInt2, -1, solution, randomInt);
			randomInt2 = tempsecondint.get(1);
			int newsecondindex2 = -1;
			if (randomInt2 == -2) {
				updaterandomInt(randomInt, tempsecondint, solution);
				}
			else if(randomInt2>=0 ){
				if(solution.getCoSolutionThreeStep().getOp().listpropertyname.get(randomInt).get(randomInt2)!=null)
				{
				EObject eobject = link.get(randomInt2);
				LocatedElement object = (LocatedElement) wrapper.target(eobject);
				String[] array2 = object.getLocation().split(":", 2);
				if (!solution.listlineofcodes.contains(Integer.parseInt(array2[0]))) {
					link.remove(randomInt2);
					comment = AddNewComment(object, containers, randomInt);
					comments.add(comment);
					updaterandomInt(randomInt, tempsecondint, solution);
					solution.listlineofcodes.add(Integer.parseInt(array2[0]));
					NSGAIIThreeStep.numop = NSGAIIThreeStep.numop + 1;
					solution.getCoSolutionThreeStep().getOp().listpropertyname.get(randomInt).set(randomInt2,
							null);
					ReturnResult.set(0, wrapper);
					ReturnResult.set(1, atlModel);
					ReturnResult.add(comment);
					ClearMandatoryDeletionAttribute();
				} else {
					updaterandomInt(randomInt, tempsecondint, solution);
					solution.inorout.set(MyProblemThreeStep.indexoperation-1, "empty");
					solution.newbindings.set(MyProblemThreeStep.indexoperation-1, null);
			
				}
			}
		}
	}
	}
		return ReturnResult;
	}

	
	private <Container extends LocatedElement, ToDelete> List<Object> PredefindDeletion(List<Container> containers, int randomInt, ATLModel wrapper,
			String relation, Solution solution, List<Object> ReturnResult, EMFModel atlModel, CommonFunctionOperators cp) {
		// TODO Auto-generated method stub
		
		String comment = null;	
		Container modifiable2 = containers.get(randomInt);
		EStructuralFeature feature = wrapper.source(modifiable2).eClass().getEStructuralFeature(relation);	
		EObject link = (EObject) wrapper.source(containers.get(randomInt)).eGet(feature);
		if (link != null) {
			LocatedElement object = (LocatedElement) wrapper.target(link);
			  boolean listlineofcode = false;  
		        String[] array2 = object.getLocation().split(":", 2);
		        
		        if (!solution.listfilterdeletion.contains(Integer.parseInt(array2[0]))
		        	)
		         listlineofcode = true;
		        
			if (listlineofcode) {
				wrapper.source(containers.get(randomInt)).eSet(feature, null);
				updatefirstIndex(randomInt);
				comment = AddNewComment(object, containers, randomInt);
				comments.add(comment);
				int indexrule =cp.FindRule(array2);
				solution.listfilterdeletion.add(Integer.parseInt(array2[0]));
				NSGAIIThreeStep.listfilterapplied.add(indexrule);
				NSGAIIThreeStep.numop = NSGAIIThreeStep.numop + 1;
				ReturnResult.set(0, wrapper);
			    ReturnResult.set(1, atlModel);
			    ReturnResult.add(comment);
				
			}

		}
		return ReturnResult;

	}


	private <Container extends LocatedElement, ToDelete> List<Object> DeleteBindingCrossoverNewBinding(ATLModel wrapper, List<Container> containers, int randomInt,
			Solution solution, List<Object> ReturnResult, EMFModel atlModel, CommonFunctionOperators cp) {
		// TODO Auto-generated method stub
		
		EStructuralFeature feature2 = wrapper.source(containers.get(randomInt))
				.eClass().getEStructuralFeature("bindings");
		List<Binding> realbindings = (List<Binding>) wrapper
				.source(containers.get(randomInt)).eGet(feature2);
		Binding b=solution.newbindings.get(MyProblemThreeStep.indexoperation-1);
		String classifier2 = solution.modifypropertyname.get(MyProblemThreeStep.indexoperation - 1);
		int index=-1;
		index=-1;
		for(int i=0;i<realbindings.size();i++) {
			if( solution.getCoSolutionThreeStep().getOp().originalwrapper.get(randomInt).get(i) == 1
					&& realbindings.get(i).getPropertyName().equals(classifier2)) {
			index=i;
	
			}
		}
		
		if(index!=-1) {
			ArrayList<Integer> tempsecondint = new ArrayList<Integer>();	
			realbindings.remove(index);
			String comment = null;
			comment = AddNewCommentExistingBinding( solution, containers, randomInt, index);
			comments.add(comment);
			tempsecondint.add(0, index);
			tempsecondint.add(1, index);
			updaterandomInt(randomInt, tempsecondint, solution);
			NSGAIIThreeStep.numop = NSGAIIThreeStep.numop + 1;
			solution.getCoSolutionThreeStep().getOp().listpropertyname.get(randomInt).set(index,
				null);
			ReturnResult.set(0, wrapper);
			ReturnResult.set(1, atlModel);
			ReturnResult.add(comment);
			ClearMandatoryDeletionAttribute();
		
		}else { // not exit that binding
			ArrayList<Integer> tempsecondint2 = new ArrayList<Integer>();
			tempsecondint2.add(-2);
			tempsecondint2.add(-2);
			updaterandomInt(-2, tempsecondint2, solution);
			solution.inorout.set(MyProblemThreeStep.indexoperation-1, "empty");
			solution.newbindings.set(MyProblemThreeStep.indexoperation-1, null);
			
		}
	
		return ReturnResult;
	}


	private void ClearMandatoryDeletionAttribute(){
		
		NSGAIIThreeStep.mandatorydeletionattribute.clear();
		NSGAIIThreeStep.mandatorydeletionlocation.clear();
	}
	
	private void updatefirstIndex(int randomInt) {
		// TODO Auto-generated method stub
				switch (MyProblemThreeStep.indexoperation) {
			case 1:
				MyProblemThreeStep.oldoperation1 = randomInt;

				break;
			case 2:
				MyProblemThreeStep.oldoperation2 = randomInt;

				break;
			case 3:
				MyProblemThreeStep.oldoperation3 = randomInt;

				break;
			case 4:
				MyProblemThreeStep.oldoperation4 = randomInt;

				break;
			case 5:
				MyProblemThreeStep.oldoperation5 = randomInt;

				break;
			case 6:
				MyProblemThreeStep.oldoperation6 = randomInt;

				break;
			case 7:
				MyProblemThreeStep.oldoperation7 = randomInt;

				break;
			case 8:
				MyProblemThreeStep.oldoperation8 = randomInt;

				break;
			case 9:
				MyProblemThreeStep.oldoperation9 = randomInt;

				break;
			case 10:
				MyProblemThreeStep.oldoperation10 = randomInt;

				break;
			case 11:
				MyProblemThreeStep.oldoperation11 = randomInt;

				break;
			case 12:
				MyProblemThreeStep.oldoperation12 = randomInt;

				break;
			case 13:
				MyProblemThreeStep.oldoperation13 = randomInt;

				break;
			case 14:
				MyProblemThreeStep.oldoperation14 = randomInt;

				break;
			case 15:
				MyProblemThreeStep.oldoperation15 = randomInt;

				break;
			case 16:
				MyProblemThreeStep.oldoperation16 = randomInt;

				break;
			case 17:
				MyProblemThreeStep.oldoperation17 = randomInt;

				break;
			case 18:
				MyProblemThreeStep.oldoperation18 = randomInt;

				break;
			case 19:
				MyProblemThreeStep.oldoperation19 = randomInt;

				break;
			case 20:
				MyProblemThreeStep.oldoperation20 = randomInt;

				break;
			case 21:
				MyProblemThreeStep.oldoperation21 = randomInt;

				break;
			case 22:
				MyProblemThreeStep.oldoperation22 = randomInt;

				break;

			}
	}

	private ArrayList<Integer> realsecondindex(int replace, Solution solution, int randomInt, int randomInt2) {
		ArrayList<Integer> realrunsecondIndex = new ArrayList<Integer>();
		realrunsecondIndex.add(replace);
	
		int temp = replace;
		for (int h = 0; h < solution.TwoIndexDeletion.size(); h++) {
			if (solution.TwoIndexDeletion.get(h) == randomInt) {
				if (solution.TwoIndexDeletion.get(h + 1) <= temp) {
					randomInt2 = (int) (temp) - 1;
					temp = randomInt2;
				}
				if (solution.TwoIndexDeletion.get(h + 1) == replace) {
					randomInt2 = -1;
				}
			}
			h = h + 1;
			if (randomInt2 == -1) {
				break;
			}

		}
		if (randomInt2 == -1) {
			realrunsecondIndex.clear();
			realrunsecondIndex = new ArrayList<Integer>();
			realrunsecondIndex.add(-1);
			realrunsecondIndex.add(-1);
		}

		else if (randomInt2 == -2)
			realrunsecondIndex.add(replace);
		else
			realrunsecondIndex.add(randomInt2);
		return realrunsecondIndex;

	}

	private ArrayList<Integer> receivesecondrandomInt(int randomInt2, int size, Solution solution, int randomInt) {
		// TODO Auto-generated method stub
		ArrayList<Integer> realrunsecondIndex = new ArrayList<Integer>();
		if (NSGAIIThreeStep.statemutcrossinitial.equals("mutation")) {

			switch (MyProblemThreeStep.indexoperation) {
				case 1:
						realrunsecondIndex=RealNumberSecondParameter(solution,MyProblemThreeStep.replaceoperation1, 
							randomInt2, size, randomInt, 0);
					break;
				case 2:
						realrunsecondIndex=RealNumberSecondParameter(solution,MyProblemThreeStep.replaceoperation2, 
						randomInt2, size, randomInt, 1);
					break;
				case 3:
						realrunsecondIndex=RealNumberSecondParameter(solution,MyProblemThreeStep.replaceoperation3, 
						randomInt2, size, randomInt, 2);
					break;
				case 4:
					realrunsecondIndex=RealNumberSecondParameter(solution,MyProblemThreeStep.replaceoperation4, 
							randomInt2, size, randomInt, 3);
					break;
				case 5:
					realrunsecondIndex=RealNumberSecondParameter(solution,MyProblemThreeStep.replaceoperation5, 
							randomInt2, size, randomInt, 4);
					break;

				case 6:
					realrunsecondIndex=RealNumberSecondParameter(solution,MyProblemThreeStep.replaceoperation6, 
							randomInt2, size, randomInt, 5);
					break;

				case 7:
					realrunsecondIndex=RealNumberSecondParameter(solution,MyProblemThreeStep.replaceoperation7, 
							randomInt2, size, randomInt, 6);
					break;
				case 8:
					realrunsecondIndex=RealNumberSecondParameter(solution,MyProblemThreeStep.replaceoperation8, 
							randomInt2, size, randomInt, 7);
					break;

				}

		} else {
			Random number_generator = new Random();
			if (size > 1)
				randomInt2 = number_generator.nextInt(size);
			else
				randomInt2 = 0;
			realrunsecondIndex.add(randomInt2);
			realrunsecondIndex.add(randomInt2);

		}
		if(NSGAIIThreeStep.mandatorydeletionattribute.size()>0) {
			
			realrunsecondIndex.set(0,NSGAIIThreeStep.mandatorydeletionlocation.get(0)); // which binding choose to delete
			realrunsecondIndex.set(1,NSGAIIThreeStep.mandatorydeletionlocation.get(0));
			}
		

		return realrunsecondIndex;

	}
    private ArrayList<Integer> RealNumberSecondParameter(Solution solution, int replaceoperation, int randomInt2, int size, int randomInt, int j) {
		// TODO Auto-generated method stub
    	
    	ArrayList<Integer> realrunsecondIndex = new ArrayList<Integer>();
    	if (solution.inorout.get(j).equals("empty")) {
			if (replaceoperation!= -1) {

				realrunsecondIndex = realsecondindex((int) (replaceoperation), solution, randomInt,
						randomInt2);
			} else {
				randomInt2 =  ChooseRandomInt( size, randomInt2, solution, randomInt);
				realrunsecondIndex.add(randomInt2);
				realrunsecondIndex.add(randomInt2);

			}
		}else {
			
			realrunsecondIndex.add(replaceoperation);
			realrunsecondIndex.add(replaceoperation);
		}

		return realrunsecondIndex;
	}


	private int ChooseRandomInt(int size,int randomInt2,Solution solution,int randomInt) {
    	
		Random number_generator = new Random();
		if (size > 1)
			randomInt2 = number_generator.nextInt(size);
		else
			randomInt2 = 0;
		
	return randomInt2;
    }
	private int findrealsecondindex(int randomInt, int randomInt2, Solution solution) {
		for (int h = 0; h < solution.TwoIndexDeletion.size(); h++) {
			if (solution.TwoIndexDeletion.get(h) == randomInt)
				if (solution.TwoIndexDeletion.get(h + 1) <= randomInt2)
					randomInt2 = randomInt2 + 1;
			h = h + 1;

		}
		solution.TwoIndexDeletion.add(randomInt);
		solution.TwoIndexDeletion.add(randomInt2);
		return randomInt2;

	}

	private void updaterandomInt(int randomInt, ArrayList<Integer> tempsecondint, Solution solution) {
		// TODO Auto-generated method stub
		int newsecondindex = 0;
		switch (MyProblemThreeStep.indexoperation) {

		case 1:
			  UpdateTwoIndexDeletion(solution,0,tempsecondint,randomInt, newsecondindex );
			break;
		case 2:
			UpdateTwoIndexDeletion(solution,1,tempsecondint,randomInt, newsecondindex );			
			break;
		case 3:
			UpdateTwoIndexDeletion(solution,2,tempsecondint,randomInt, newsecondindex );			
			break;
		case 4:
			UpdateTwoIndexDeletion(solution,3,tempsecondint,randomInt, newsecondindex );			
			break;
		case 5:
			UpdateTwoIndexDeletion(solution,4,tempsecondint,randomInt, newsecondindex );			
			break;
		case 6:
			UpdateTwoIndexDeletion(solution,5,tempsecondint,randomInt, newsecondindex );			
			break;
		case 7:
			UpdateTwoIndexDeletion(solution,6,tempsecondint,randomInt, newsecondindex );			
			break;
		case 8:
			UpdateTwoIndexDeletion(solution,7,tempsecondint,randomInt, newsecondindex );			
			break;
			}

	}

	private void UpdateTwoIndexDeletion(Solution solution, int j, ArrayList<Integer> tempsecondint, int randomInt,
			int newsecondindex) {
		
		if (solution.inorout.get(j).equals("empty")) {
			if (tempsecondint.get(0) != -1) {
				if (tempsecondint.get(0) == tempsecondint.get(1))
					newsecondindex = findrealsecondindex(randomInt, tempsecondint.get(1), solution);
				else {
					newsecondindex = tempsecondint.get(0);
					solution.TwoIndexDeletion.add(randomInt);
					solution.TwoIndexDeletion.add(newsecondindex);
				}
			} else {
				randomInt = -2;
				newsecondindex = -2;
			}
		} else {
			newsecondindex = tempsecondint.get(0);
			solution.TwoIndexDeletion.add(randomInt);
			solution.TwoIndexDeletion.add(newsecondindex);

		}
		MyProblemThreeStep.oldoperation1 = randomInt;
		MyProblemThreeStep.replaceoperation1 = newsecondindex;
	
	}

	public Resource retPackResouceMM(String MMPath) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new EcoreResourceFactoryImpl());
		URI fileURI = URI.createFileURI(MMPath);// ecore.getFullPath().toOSString());
		Resource resource = resourceSet.getResource(fileURI, true);
		return resource;
	}

	
	
}
