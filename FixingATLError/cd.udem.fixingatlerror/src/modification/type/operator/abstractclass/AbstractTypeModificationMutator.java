package modification.type.operator.abstractclass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

// REMARKS
// => any subtype of a class is compatible with the class
// => no class has incompatible supertypes
// => the information of the static analysis could be used to select an incompatible
//    class of interest, e.g. a class that does not define a feature used by the rule

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFReferenceModel;
import org.eclipse.m2m.atl.engine.parser.AtlParser;
import anatlyzer.atl.util.ATLUtils;

import witness.generator.MetaModel;
import anatlyzer.atlext.ATL.ATLFactory;
import anatlyzer.atlext.ATL.ATLPackage;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.ContextHelper;
import anatlyzer.atlext.ATL.ForEachOutPatternElement;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.InPattern;
import anatlyzer.atlext.ATL.InPatternElement;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.OutPattern;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atlext.ATL.StaticHelper;
import anatlyzer.atlext.OCL.Attribute;
import anatlyzer.atlext.OCL.CollectionType;
import anatlyzer.atlext.OCL.IteratorExp;
import anatlyzer.atlext.OCL.LoopExp;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OCLPackage;
import anatlyzer.atlext.OCL.OclContextDefinition;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.OclFeatureDefinition;
import anatlyzer.atlext.OCL.OclModelElement;
import anatlyzer.atlext.OCL.OclType;
import anatlyzer.atlext.OCL.Operation;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.atlext.OCL.Parameter;
import anatlyzer.atlext.OCL.PropertyCallExp;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;
//import anatlyzer.evaluation.mutators.ATLModel;
import anatlyzer.atl.model.ATLModel;
import evaluation.mutator.AbstractMutator;
import deletion.operator.AbstractDeletionMutator;
//import anatlyzer.evaluation.tester.Tester;
import cd.udem.fixingatlerror.Class2Rel;
import cd.udem.fixingatlerror.Operations;
import cd.udem.fixingatlerror.Setting;
import jmetal.core.Solution;
import jmetal.metaheuristics.nsgaII.NSGAII;
import transML.exceptions.transException;
import anatlyzer.atl.types.Type;

public abstract class AbstractTypeModificationMutator extends AbstractMutator {

	/**
	 * Generic type modification. It allows subtypes of the container class.
	 * 
	 * @param atlModel
	 * @param outputFolder
	 * @param ToModifyClass
	 *            container class of the class of objects to modify (example
	 *            Parameter)
	 * @param featureName
	 *            feature to modify (example type)
	 * @param metamodel
	 *            metamodel containing the candidate types for the modification
	 */
	private String m = "finalresult"
			+ Class2Rel.totalnumber + ".atl";
	private EMFModel atlModel3;
	private String m2 = "/PNML2PetriNet2.atl";
	private EMFModel atlModel4;

	protected <ToModify extends LocatedElement> List<Object> genericTypeModification(EMFModel atlModel, String outputFolder,
			Class<ToModify> ToModifyClass, String featureName, MetaModel[] metamodels, ATLModel wrapper,Solution solution,MetaModel metamodels1, MetaModel metamodels2) {
		return this.genericTypeModification(atlModel, outputFolder, ToModifyClass, featureName, metamodels, false,
				wrapper,solution,metamodels1,metamodels2);

	}

	/**
	 * Generic type modification (OclModelElement, CollectionType or PrimitiveType).
	 * 
	 * @param atlModel
	 * @param outputFolder
	 * @param ToModifyClass
	 *            container class of the class of objects to modify (example
	 *            Parameter)
	 * @param featureName
	 *            feature to modify (example type)
	 * @param metamodel
	 *            metamodel containing the candidate types for the modification
	 * @param exactToModifyType
	 */
	private EMFModel loadTransformationModel(String atlTransformationFile) throws ATLCoreException {
		ModelFactory modelFactory = new EMFModelFactory();

		EMFReferenceModel atlMetamodel = (EMFReferenceModel) modelFactory.getBuiltInResource("ATL.ecore");

		// EMFReferenceModel atlMetamodel =
		// (EMFReferenceModel)modelFactory.getBuiltInResource("C:\\Users\\wael\\OneDrive\\workspaceATLoperations\\mutants\\anatlyzer.evaluation.mutants\\ATL.ecore");
		AtlParser atlParser = new AtlParser();
		EMFModel atlModel = (EMFModel) modelFactory.newModel(atlMetamodel);
		atlParser.inject(atlModel, atlTransformationFile);
		atlModel.setIsTarget(true);

		// // Should we want to serialize the model.
		// String injectedFile = "file:/" + atlTransformationFile + ".xmi";
		// IExtractor extractor = new EMFExtractor();
		// extractor.extract(atlModel, injectedFile);

		return atlModel;
	}

	protected <ToModify extends LocatedElement> List<Object> genericTypeModification(EMFModel atlModel, String outputFolder,
			Class<ToModify> ToModifyClass, String featureName, MetaModel[] metamodels, boolean exactToModifyType,
			ATLModel wrapper,Solution solution,MetaModel metamodels1, MetaModel metamodels2) {
		List<Object> ReturnResult = new ArrayList<Object>();
		List<ToModify> modifiable = (List<ToModify>) wrapper.allObjectsOf(ToModifyClass);
			
		return null;
		

	}
	

	private String getType(EObject container, VariableExp containee, MetaModel inputMM,
			MetaModel outputMM) {
		// TODO Auto-generated method stub
		
		EClassifier         c   = null;
		VariableDeclaration def = containee.getReferredVariable();
		
		// obtain type (classifier) of variable expression ..............................
		// case 1 -> in pattern element
		if(def!=null) {
		if (def instanceof InPatternElement) {
		
			c = inputMM.getEClassifier(def.getType().getName());
		//	System.out.println(c);
		}
		// case 2 -> for each out pattern element
		else if (def instanceof ForEachOutPatternElement) {

			def = ((ForEachOutPatternElement)def).getIterator();
			if (def.eContainer() instanceof OutPatternElement) {
				OutPatternElement element = (OutPatternElement)def.eContainer();
				if (element.getType() instanceof OclModelElement)
					c = outputMM.getEClassifier(((OclModelElement)element.getType()).getName());
			}
		}
		// case 3 -> iterator
		else if (def instanceof Iterator) {
		
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
		//	System.out.println(c);
		}
		// case 4 -> variable declaration
		else {
			if (toString(def).equals("self")) {
				//System.out.println("4444444444");
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
				
				c = inputMM.getEClassifier(((VariableDeclaration)def).getType().getName());
			}
			else if (((VariableDeclaration)def).getType() instanceof CollectionType) {
				
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

	private <ToModify extends LocatedElement> List<Object> OperationPreviousGeneration(int randomInt,Solution solution,EMFModel atlModel, MetaModel[] metamodels, List<ToModify> modifiable,ATLModel wrapper,String featureName
			,EDataTypeEList<String> comments,List<Object> ReturnResult,MetaModel metamodel1,MetaModel metamodel2) {
		// TODO Auto-generated method stub
		
		ToModify modifiable2=modifiable.get(randomInt);
		String[] array = modifiable2.getLocation().split(":", 2);
		if( Operations.statecase.equals("case7" )) {
		  if(!solution.listlineofcodes.contains(Integer.parseInt(array[0])) )
		  {
			  ReturnResult=ApplyFromPreviousCase7( randomInt, solution, atlModel,  metamodels,  modifiable2, wrapper, featureName
					, comments, ReturnResult);	
		  }
		  else {
			  setvariable(-2, -2, -2);
		  }
		}  
		else {
			 
				  ReturnResult=ApplyFromPreviousOtherCase( randomInt, solution, atlModel,  metamodels,  modifiable2, wrapper, featureName
						, comments, ReturnResult,metamodel1,metamodel2,array);	
			
			 
			}  
		  return ReturnResult;	
}
	private <ToModify extends LocatedElement> List<Object> ApplyFromPreviousOtherCase(int randomInt, Solution solution, EMFModel atlModel,
			MetaModel[] metamodels, ToModify modifiable2, ATLModel wrapper, String featureName,
			EDataTypeEList<String> comments, List<Object> ReturnResult, MetaModel metamodel1,MetaModel metamodel2,String[] array) {
	
				return null;
	}

	private <ToModify extends LocatedElement> List<Object> ApplyFromPreviousCase7(int randomInt,Solution solution,EMFModel atlModel, MetaModel[] metamodels, ToModify modifiable2,ATLModel wrapper,String featureName
			,EDataTypeEList<String> comments,List<Object> ReturnResult) {
	 return null;		
	}

		private int[] something() {
		int number1 = 1;
		int number2 = 2;
		return new int[] { number1, number2 };
	}

	private List<Integer> ReturnFirstIndex(int randomInt, int size, boolean checkmutationapply, int count, Solution solution) {
		// TODO Auto-generated method stub
				return null;
	}

	private void setvariable(int randomInt, int randomInt2, int randomInt3) {
		// TODO Auto-generated method stub
	}

	private int calculaterandomInt(List<EObject> replacements) {
		
		return 0;
	}

	private int calculaterandomInt2(List<EObject> value) {
		return 0;

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
	 * Given a class, it returns a compatible superclass (i.e. with the same
	 * features).
	 * 
	 * @param c
	 * @return a compatible superclass, null if there is none
	 */
	// protected EClass getCompatibleSuperclass (EClass c) {
	protected List<EClass> getCompatibleSuperclass(EClass c) {
		List<EClass> superclasses = c.getEAllSuperTypes();
		// System.out.println("super");
		// System.out.println(superclasses);
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
	// protected EClass getCompatibleSubclass (EClass c, MetaModel mm) {
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
	// protected EClass getCompatibleUnrelatedClass (EClass c, MetaModel mm) {
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
	// protected EClass getIncompatibleSubclass (EClass c, MetaModel mm) {
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
	// protected EClass getIncompatibleUnrelatedClass (EClass c, MetaModel mm) {
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
		// EClass compatible = null;
		List<EClass> compatible = new ArrayList<EClass>();
		for (int i = 0; i < candidates.size(); i++) {
			EClass c2 = candidates.get(i);
			if (isCompatibleWith(c2, c))
				compatible.add(c2);
		}
		return compatible;
	}

	
	private List<EClass> getIncompatible(EClass c, List<EClass> candidates) {
		// EClass incompatible = null;
		List<EClass> incompatible = new ArrayList<EClass>();
		;
		for (int i = 0; i < candidates.size(); i++) {
			EClass c2 = candidates.get(i);
			if (!isCompatibleWith(c2, c))
				incompatible.add(c2);
			// incompatible = c2;
		}
		return incompatible;
	}

	/**
	 * It checks whether a class c1 is compatible with (i.e. it can substitute
	 * safely) another class c2. c1 is compatible with c2 if c1 defines at least all
	 * features that c2 defines (it can define more).
	 * 
	 * @param c1
	 *            class
	 * @param c2
	 *            class
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
