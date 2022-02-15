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
import anatlyzer.atlext.OCL.SequenceExp;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;
//import anatlyzer.evaluation.mutators.ATLModel;
import anatlyzer.atl.model.ATLModel;
import evaluation.mutator.AbstractMutator;
import deletion.operator.AbstractDeletionMutator;
//import anatlyzer.evaluation.tester.Tester;
import cd.udem.fixingatlerror.Class2Rel;
import cd.udem.fixingatlerror.Operations;
import cd.udem.fixingatlerror.Operationspostprocessing;
import cd.udem.fixingatlerror.Setting;
import cd.udem.fixingatlerror.Settingpostprocessing;
import jmetal.core.Solution;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.nsgaII.NSGAIIpostprocessing;
import transML.exceptions.transException;
import anatlyzer.atl.types.Type;

public abstract class AbstractTypeModificationMutatorpost extends AbstractMutator {

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
	
	private int numberoutpattern=0;
	private boolean repeatargument=false;
	private String m = "C:/Users/varaminz/Downloads/Project/workspaceTEST34/anatlyzer.atl.tests.api/trafo2/zahra2mutants/finalresult"
			+ Class2Rel.totalnumber + ".atl";
	private List<Integer> alloclrule = new ArrayList<Integer>();
	private List<Integer> checkedruleinpattern = new ArrayList<Integer>();
	List<String> listattrforcheck = new ArrayList<String>();
	//private List<Integer> indexallerrorocl = new ArrayList<Integer>();
	private ArrayList<String> listattrnavigation = new ArrayList<String>();
	private ArrayList<String> listreplacement = new ArrayList<String>();
	private List<String> checklistattr = new ArrayList<String>();
	private List<String> listoldclass = new ArrayList<String>();
	private List<String> listsubclasslefthandside = new ArrayList<String>();
	private List<Integer> listsubclassrule = new ArrayList<Integer>();
	private List<Integer> checknotrepeat=new ArrayList<Integer>();
	private List<Integer> listrandomInt=new ArrayList<Integer>();
	private int startcombinedattr=0;
	private int indexrule=-1;
	private EMFModel atlModel3;
	private int start=0;
	private String m2 = "C:/Users/varaminz/Downloads/Project/workspaceTEST34/anatlyzer.atl.tests.api/trafo/PNML2PetriNet2.atl";
	private EMFModel atlModel4;
	private ArrayList<Integer> oclindex;
	private int indexcorrectocl=-1;
	private int numrun=0;
    private boolean findcorrectclass=false;
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
		AtlParser atlParser = new AtlParser();
		EMFModel atlModel = (EMFModel) modelFactory.newModel(atlMetamodel);
		atlParser.inject(atlModel, atlTransformationFile);
		atlModel.setIsTarget(true);

		
		return atlModel;
	}

	protected <ToModify extends LocatedElement> List<Object> genericTypeModification(EMFModel atlModel, String outputFolder,
			Class<ToModify> ToModifyClass, String featureName, MetaModel[] metamodels, boolean exactToModifyType,
			ATLModel wrapper,Solution solution,MetaModel metamodels1, MetaModel metamodels2) {
		List<Object> ReturnResult = new ArrayList<Object>();
		List<ToModify> modifiable = (List<ToModify>) wrapper.allObjectsOf(ToModifyClass);
		
			return null;
	

	}
	

	
	private  <ToModify extends LocatedElement> List<Object> Postprocessingforinpatternmodification(ATLModel wrapper, int randomInt, Solution solution, List<ToModify> modifiable, String featureName
			,MetaModel metamodels1, MetaModel metamodels2,EMFModel atlModel,MetaModel[] metamodels,EDataTypeEList<String> comments
			,List<Object> ReturnResult, int index) {
		// TODO Auto-generated method stub
	return  null;	
	}

	private <ToModify extends LocatedElement> List<Object> Analysiseachbinding(Solution solution, List<ToModify> modifiable
			,ATLModel wrapper,String featureName,MetaModel metamodels1, MetaModel metamodels2,EMFModel atlModel,MetaModel[] metamodels
			, EDataTypeEList<String> comments,List<Object> ReturnResult, int id ) {
		// TODO Auto-generated method stub
	
	return null;	
	}

	private void FindOutPatternIsequalLeftHandSideandHasNoFilter(ATLModel wrapper, String featureName, String typeleftside) {
		// TODO Auto-generated method stub
		
		List<OutPatternElement> outpatternlist = (List<OutPatternElement>) wrapper.allObjectsOf(OutPatternElement.class);
		
		int id = -1;
		for(int i=0;i<outpatternlist.size();i++) {
			
			EStructuralFeature featureDefinition = wrapper.source(outpatternlist.get(i)).eClass()
					.getEStructuralFeature(featureName);
			String[] array = outpatternlist.get(i).getLocation().split(":", 2);
			id=FindRuleforThisLine( array);
			if (featureDefinition != null && featureDefinition.getUpperBound() == 1) {
			
				EObject object2modify_src = wrapper.source(outpatternlist.get(i));
				EObject oldFeatureValue = (EObject) object2modify_src.eGet(featureDefinition);
				
				if(typeleftside.equals(toString(oldFeatureValue))) {
					List<InPattern> containers = (List<InPattern>) wrapper.allObjectsOf(InPattern.class);
					EStructuralFeature feature = wrapper.source(containers.get(id)).eClass()
							.getEStructuralFeature("filter");
					
					if (feature != null) {
						System.out.println("hasnotfilter");
						if (feature.getUpperBound() == 1 && feature.getLowerBound() == 0) {
							EObject link = (EObject) wrapper.source(containers.get(id)).eGet(feature);
							if (link == null) {
								System.out.println(Integer.parseInt(array[0]));
								this.indexrule=id;
							}
							
						}
					}
					
					
				}
			}
		}
		
	}

	private boolean FindOutPatternIsequalLeftHandSidewithsubclassess(ATLModel wrapper, String featureName) {
		// TODO Auto-generated method stub
		
		List<OutPatternElement> outpatternlist = (List<OutPatternElement>) wrapper.allObjectsOf(OutPatternElement.class);
		this.listsubclassrule.clear();
		this.listsubclassrule=new ArrayList<Integer>();
		int id = -1;
		for(int j=0;j<this.listsubclasslefthandside.size();j++) {
		for(int i=0;i<outpatternlist.size();i++) {
			
			EStructuralFeature featureDefinition = wrapper.source(outpatternlist.get(i)).eClass()
					.getEStructuralFeature(featureName);
			String[] array = outpatternlist.get(i).getLocation().split(":", 2);
			id=FindRuleforThisLine( array);
			if (featureDefinition != null && featureDefinition.getUpperBound() == 1 && !this.checkedruleinpattern.contains(id)) {
			
				EObject object2modify_src = wrapper.source(outpatternlist.get(i));
				EObject oldFeatureValue = (EObject) object2modify_src.eGet(featureDefinition);
				
				if(this.listsubclasslefthandside.get(j).equals(toString(oldFeatureValue))) {
					
					this.listsubclassrule.add(id);
					this.checkedruleinpattern.add(id);
					
					
				}
			}
		}
		
	}
		if(this.listsubclasslefthandside.size()==this.listsubclassrule.size())
			return true;
		else return false;
}
	private Integer FindRuleforThisLine(String[] array) {
		int idrule=-1;
		for (int j = 0; j < NSGAIIpostprocessing.faultrule.size(); j++) {
			if (Integer.parseInt(array[0]) > NSGAIIpostprocessing.faultrule.get(j)
					&& Integer.parseInt(array[0]) < NSGAIIpostprocessing.finalrule.get(j))

				idrule = j;

		}
		return idrule;
	}
	private String IfRightHandSideIsUsingCondition(ATLModel wrapper, int h, int h2) {
		// TODO Auto-generated method stub
				return null;
	}

	private String ExtractTypeRightHandSideIfThereisnotHelper(int h, int h2, String specificinput) {
		// TODO Auto-generated method stub
				return null;
	}

	private <ToModify extends LocatedElement> List<Object> ModifyInpatternElement(EMFModel atlModel, EObject oldFeatureValue, MetaModel[] metamodels, String helpertype, EStructuralFeature featureDefinition, EObject object2modify_src
			, List<ToModify> modifiable,EDataTypeEList<String> comments,ATLModel wrapper,List<Object> ReturnResult, int id, String outpatternfound,String featureName, String specificoutput ) {
		return ReturnResult;	
	}

	private boolean IFOutPatternHasSubClass(String outpatternfound, int i) {
		// TODO Auto-generated method stub
		
		Settingpostprocessing s=new Settingpostprocessing();
		String MMRootPath2=null;
		if(i==0)
		MMRootPath2     = s.gettargetmetamodel();
		else
		MMRootPath2     = s.getsourcemetamodel();	
		 MetaModel metatarget=null;
		 try {
			metatarget=new MetaModel(MMRootPath2);
		} catch (transException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 this.listsubclasslefthandside.clear();
		 this.listsubclasslefthandside=new ArrayList<String>();
		 for (EClassifier classifier : metatarget.getEClassifiers()) {
			 
				if (classifier instanceof EClass) {
					
					EClass child = ((EClass) classifier);
		
					if(child.getName().equals(outpatternfound)) {
						
						for (EClassifier classifier2 : metatarget.getEClassifiers()) {
			 
							if (classifier2 instanceof EClass) {
					
								EClass child2 = ((EClass) classifier2);
								
								if(child.isSuperTypeOf(child2) && !child2.equals(child)) {
									
									this.listsubclasslefthandside.add( child2.getName());
									System.out.println("inherit");
									System.out.println(child2);
									//return true;
						 
								}
							}
						}
					}
				}
		 }
		 if(this.listsubclasslefthandside.size()>0 )
			 return true;
		 else
		return false;
	}

	private EObject FindOutpatternIsequaltoLeftHandSideType(ATLModel wrapper, String typeleftside, String featureName) {
		// TODO Auto-generated method stub
		List<OutPatternElement> outpatternlist = (List<OutPatternElement>) wrapper.allObjectsOf(OutPatternElement.class);
		EObject oldFeatureValue2=null;
		for(int i=0;i<outpatternlist.size();i++) {
			
			EStructuralFeature featureDefinition = wrapper.source(outpatternlist.get(i)).eClass()
					.getEStructuralFeature(featureName);
			String[] array = outpatternlist.get(i).getLocation().split(":", 2);
			
			if (featureDefinition != null && featureDefinition.getUpperBound() == 1) {
			
				EObject object2modify_src = wrapper.source(outpatternlist.get(i));
				EObject oldFeatureValue = (EObject) object2modify_src.eGet(featureDefinition);
				
				
					System.out.println("eqq");
					System.out.println(toString(oldFeatureValue));
				
				if(toString(oldFeatureValue).equals( typeleftside) && !typeleftside.equals("String")) {
					System.out.println("2eqq");
					this.numberoutpattern=this.numberoutpattern+1;
					for (int j = 0; j < NSGAIIpostprocessing.faultrule.size(); j++) {
						if (Integer.parseInt(array[0]) > NSGAIIpostprocessing.faultrule.get(j)
								&& Integer.parseInt(array[0]) < NSGAIIpostprocessing.finalrule.get(j))

							this.indexrule = j;

					}
					oldFeatureValue2=oldFeatureValue;
				//	return (toString(oldFeatureValue2));
			        
				}
				
		
			}
			
		}
		return oldFeatureValue2;
	}

	private String IfRightHandSideISHelperReturnHelperType(ATLModel wrapper, int h, int h2) {
		// TODO Auto-generated method stub
		return null;
		
		
	}

	private String ReturnHelpetType(ATLModel wrapper, List<Helper> helper, int h, int h2, int i, int j) {
		// TODO Auto-generated method stub
				return null;
        	
	}

	private String ReturnTypeLeftHandSideBinding(String specificoutput, String leftsidebinding) {
		// TODO Auto-generated method stub
				return null;
	}

	private <ToModify extends LocatedElement> String ReturnSpecificInput(ATLModel wrapper, String featureName, int h
			,List<ToModify> modifiable) {
		// TODO Auto-generated method stub
		int indexrule = -1;
		List<OutPatternElement> variables = (List<OutPatternElement>)wrapper.allObjectsOf(OutPatternElement.class);
		String[] array = variables.get(h).getLocation().split(":", 2);
		for (int j = 0; j < NSGAIIpostprocessing.faultrule.size(); j++) {
			if (Integer.parseInt(array[0]) > NSGAIIpostprocessing.faultrule.get(j)
					&& Integer.parseInt(array[0]) < NSGAIIpostprocessing.finalrule.get(j))

				indexrule = j;

		}
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

	private int checknumberoclkindofinrules() {
		// TODO Auto-generated method stub
				return  0;
	}

	private <ToModify extends LocatedElement> List<Object> Postprocessingforoclwithoutiteration(ATLModel wrapper, List<EObject> value2, int randomInt,
			EStructuralFeature featureDefinition, boolean checkmutationapply, EMFModel atlModel, MetaModel[] metamodels, int count,
			Solution solution, String[] array, EDataTypeEList<String> comments, String comment, int indexrule
			,  List<ToModify>  modifiable, List<Object> ReturnResult) {
		// TODO Auto-generated method stub
		
			return ReturnResult;	
	}

	private <ToModify extends LocatedElement> List<Object> Postprocessingforoclwithiteration(ATLModel wrapper, List<EObject> value2, int randomInt,
			EStructuralFeature featureDefinition, boolean checkmutationapply, EMFModel atlModel, MetaModel[] metamodels, int count,
			Solution solution, String[] array, EDataTypeEList<String> comments, String comment, int indexrule
			,  List<ToModify>  modifiable, List<Object> ReturnResult, int idrule) {
		// TODO Auto-generated method stub
			
	return ReturnResult;	
	}
	private void findallattriflinebeforeoclandtwolinebeforeiteration( String[] array) {
			
		}
		

// in baraye samte chape 
	private void findallattributespecificoclliteration( String[] array) {
			
	}
	private void findallattributespecificoclkindofwithiteration(String[] array, EObject oldFeatureValue2) {
			
	}

	private void findallattributespecificoclkindof(String[] array, EObject oldFeatureValue2) {
				
		
	}

	private <ToModify extends LocatedElement> List<Object> CheckInheritanceinsideoclandclass(String typenavigation, EObject oldFeatureValue2, boolean 
			findreplace, boolean
	 perivoustype,boolean 
	 checkmutationapply, List<EObject> value2, EDataTypeEList<String> comments, List<ToModify> modifiable, String comment, List<EObject> replacements, int randomInt4, int randomInt, int randomInt3, int indexrule
	 , ATLModel wrapper, EMFModel atlModel, List<Object> ReturnResult, String [] array) {
		// TODO Auto-generated method stub
		
		Settingpostprocessing s=new Settingpostprocessing();
		String MMRootPath2     = s.gettargetmetamodel();
		 MetaModel metatarget=null;
		
		 try {
				metatarget=new MetaModel(MMRootPath2);
			} catch (transException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			 EClass childo=null;
			 EClass child2o=null;
			 EClass child3o=null;
		
			// find inside ocl in list of classifier
			 for (EClassifier classifier : metatarget.getEClassifiers()) {
				 
				 if (classifier instanceof EClass) {
					 
					 EClass child = ((EClass) classifier);
					 
					 if(child.getName().equals(typenavigation )) {
						 childo=child;
						 
					 }
				 }
			 }
			 
				boolean correctattr=false;
				boolean correctattrnew=false;
				boolean attrerror=false;
				 for (EClassifier classifier2 : metatarget.getEClassifiers()) {
						if (classifier2 instanceof EClass) {
							 
							EClass child2 = ((EClass) classifier2);
							EClass child3 = ((EClass) classifier2);
							 if(child2.getName().equals(toString(oldFeatureValue2)) ) {
								 child2o=child2;
								 //agar in rule joz rule hast ke attr ocl ke error dare tosh hast 
							//	 attrerror=true;		 
							 }
							 	
							 if(child3.getName().equals(toString(replacements.get(randomInt4))) ) {
								 child3o=child3;
									//	 correctattrnew=true;// System.out.println("oooiiiiiiiiiiii");
								 
							 }
						
						}
						
					}
			
				 boolean checkattr=false;
				 boolean checkattr2=false;
				 checkattr=Chechaccessattributepreiviousargument(oldFeatureValue2,checkattr);
				 checkattr2=Chechaccessattributepreiviousargument(replacements.get(randomInt4),checkattr2);
				 				if(this.listattrforcheck.size()==0)
					       checkattr=true;
				 					
				if(childo!=null && child2o!=null)
				 if((childo.isSuperTypeOf(child2o) && checkattr==true) 
							) {
						 
						 findreplace=true;
						 perivoustype=true;
						 checkmutationapply = true;
						 this.findcorrectclass=true;
						 this.listrandomInt.add( randomInt);
						 this.checknotrepeat.add(this.indexcorrectocl);
						 this.listoldclass.add(toString(oldFeatureValue2) );
						 this.alloclrule.add(Integer.parseInt(array[0]));
						 System.out.println("\n-- MUTATION \"" + this.getDescription() + "\" from "
									+ toString(modifiable.get(randomInt)) + ":" + toString(oldFeatureValue2)
									+ " to " + toString(modifiable.get(randomInt)) + ":"
									+ toString(replacements.get(randomInt4)) + " (line "
									+ modifiable.get(randomInt).getLocation()
									+ " of original transformation)\n");
					
						// correctattrnew=true;
						 System.out.println("notchange");
						 System.out.println("y1");
						 
					 }
				if(childo!=null && child3o!=null)
					 if(
							( childo.isSuperTypeOf(child3o) && perivoustype==false )
							&& !toString(replacements.get(randomInt4)).equals( typenavigation ) && checkattr2==true ) {
						 this.listrandomInt.add( randomInt);
						 this.checknotrepeat.add(this.indexcorrectocl);
						 this.alloclrule.add(Integer.parseInt(array[0]));
						 this.listoldclass.add(toString(replacements.get(randomInt4)) );
						 System.out.println("y2");
					//	 perivoustype=false;
						 findreplace=true;
						 checkmutationapply = true;
						 this.findcorrectclass=true;
						 correctattrnew=true;
						 
					 }
				//toString(replacements.get(randomInt4)).equals( typenavigation )
				if(childo==null || child2o==null || child3o==null
						|| this.repeatargument==true)
				{
					System.out.println("y3");
					this.listoldclass.add(toString(oldFeatureValue2) );
					this.listrandomInt.add( randomInt);
					this.checknotrepeat.add(this.indexcorrectocl);
					this.alloclrule.add(Integer.parseInt(array[0]));
					
					
				}
				//	if (correctattr==false && correctattrnew==false)
				//		findreplace=false;
					System.out.println("randomint");
					System.out.println(checkattr);
					System.out.println(checkattr2);
					System.out.println(this.listrandomInt);
					System.out.println(this.checknotrepeat);
					System.out.println(this.alloclrule);
					System.out.println(this.listoldclass);
					  ReturnResult=Applynewoperationandaddcomments(perivoustype, indexrule, replacements, randomInt4,
								randomInt, randomInt3,oldFeatureValue2,value2,comments, comment,modifiable
								, wrapper,  atlModel, ReturnResult, checkmutationapply,correctattrnew);
					
			 
			 
		return 	ReturnResult; 
		
	}

	private boolean Chechaccessattributepreiviousargument(EObject oldFeatureValue2, boolean checkattr) {
		// TODO Auto-generated method stub
			return true;

	}

	private <ToModify extends LocatedElement> List<Object> Applynewoperationandaddcomments(boolean perivoustype, int indexrule, List<EObject> replacements, int randomInt4, int randomInt, int randomInt3, EObject oldFeatureValue2,
			List<EObject> value2, EDataTypeEList<String> comments, String comment, List<ToModify> modifiable
			, ATLModel wrapper, EMFModel atlModel, List<Object> ReturnResult, boolean checkmutationapply, boolean correctattrnew) {
			return 	null;
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
		//	System.out.println("2222222222222");
			def = ((ForEachOutPatternElement)def).getIterator();
			if (def.eContainer() instanceof OutPatternElement) {
				OutPatternElement element = (OutPatternElement)def.eContainer();
				if (element.getType() instanceof OclModelElement)
					c = outputMM.getEClassifier(((OclModelElement)element.getType()).getName());
			}
		}
		// case 3 -> iterator
		else if (def instanceof Iterator) {
		//	System.out.println("3333333333333");
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
				//System.out.println("5555555555");
				c = inputMM.getEClassifier(((VariableDeclaration)def).getType().getName());
			}
			else if (((VariableDeclaration)def).getType() instanceof CollectionType) {
				//System.out.println("6666666666");
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
	private Boolean ReturnIsThereCollection(List<Integer> collectionlocation, List<Integer> collectionlocation2) {
		// TODO Auto-generated method stub
		List<Integer> collectionrule = new ArrayList<Integer>();
		Boolean IsThereCollection = false;
		if (Class2Rel.typeoperation.equals("collectionelement")) {
			for (int i = 0; i < collectionlocation.size(); i++)
				for (int j = 0; j < NSGAII.faultrule.size(); j++) {
					if (collectionlocation.get(i) > NSGAII.faultrule.get(j)
							&& collectionlocation.get(i) < NSGAII.finalrule.get(j))
						collectionrule.add(j);

				}
			for (int i = 0; i < collectionrule.size(); i++)
				if (NSGAII.errorrule.contains(collectionrule.get(i)))
					IsThereCollection = true;
		} else if (Class2Rel.typeoperation.equals("argu")) {
			for (int i = 0; i < collectionlocation2.size(); i++)
				for (int j = 0; j < NSGAII.faultrule.size(); j++) {
					if (collectionlocation2.get(i) > NSGAII.faultrule.get(j)
							&& collectionlocation2.get(i) < NSGAII.finalrule.get(j))
						collectionrule.add(j);

				}
			for (int i = 0; i < collectionrule.size(); i++)
				if (NSGAII.errorrule.contains(collectionrule.get(i)))
					IsThereCollection = true;
		}

		return IsThereCollection;

	}

	private Object[] FindFaultyLocation() {
		// TODO Auto-generated method stub
		String stringSearch = "rule";
		String stringSearch2 = "}";
		String stringSearch3 = "Sequence";
		String stringSearch4 = ".oclIsKindOf";
		List<Integer> collectionlocation = new ArrayList<Integer>();
		List<Integer> collectionlocation2 = new ArrayList<Integer>();
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new FileReader(
					"C:/Users/varaminz/Downloads/Project/workspaceTEST34/anatlyzer.atl.tests.api/trafo2/zahra2mutants/finalresult"
							+ Class2Rel.totalnumber + ".atl"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NSGAII.faultrule.clear();
		NSGAII.finalrule.clear();
		int linecount = 0;
		int lastline = 0;
		String line;

		try {
			while ((line = bf.readLine()) != null) {
				linecount++;
				int indexfound = line.indexOf(stringSearch);
				int indexfound2 = line.indexOf(stringSearch2);
				int indexfound3 = line.indexOf(stringSearch3);
				int indexfound4 = line.indexOf(stringSearch4);
				if (indexfound > -1) {
				
					NSGAII.faultrule.add(linecount);

				}
				if (indexfound2 > -1) {
					lastline = linecount;

				}
				if (indexfound3 > -1) {
					collectionlocation.add(linecount);

				}
				if (indexfound4 > -1) {
					collectionlocation2.add(linecount);
		

				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 1; i < NSGAII.faultrule.size(); i++)
			NSGAII.finalrule.add(NSGAII.faultrule.get(i) - 2);
		NSGAII.finalrule.add(lastline);
		try {
			bf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Object[] { collectionlocation, collectionlocation2 };

	}

	private void StoreTwoIndex(int randomInt, int randomInt2) {
		// TODO Auto-generated method stub
	}

	private int FindSecondIndex(int randomInt2, int size) {
		return randomInt2;

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
		// TODO Auto-generated method stub
		return 0;

	}

	private int calculaterandomInt2(List<EObject> value) {
		// TODO Auto-generated method stub
		int randomInt = -2;
		return randomInt;

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

		 return replacements;
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
	// private EClass getCompatible (EClass c, List<EClass> candidates) {
	private List<EClass> getCompatible(EClass c, List<EClass> candidates) {
		// EClass compatible = null;
		List<EClass> compatible = new ArrayList<EClass>();
		// for (int i=0; i<candidates.size() && compatible==null; i++) {
		for (int i = 0; i < candidates.size(); i++) {
			EClass c2 = candidates.get(i);
			if (isCompatibleWith(c2, c))
				compatible.add(c2);
		}
		return compatible;
	}

	// private EClass getIncompatible (EClass c, List<EClass> candidates) {
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
