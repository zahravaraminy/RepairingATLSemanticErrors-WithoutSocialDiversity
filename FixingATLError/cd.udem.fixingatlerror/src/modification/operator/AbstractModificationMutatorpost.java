package modification.operator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFReferenceModel;
import org.eclipse.m2m.atl.engine.parser.AtlParser;

import witness.generator.MetaModel;
import anatlyzer.atlext.ATL.ATLFactory;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.InPatternElement;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.atlext.OCL.Attribute;
import anatlyzer.atlext.OCL.BooleanExp;
import anatlyzer.atlext.OCL.CollectionType;
import anatlyzer.atlext.OCL.IntegerExp;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.Operation;
import anatlyzer.atlext.OCL.RealExp;
import anatlyzer.atlext.OCL.StringExp;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.util.ATLSerializer;
//import anatlyzer.evaluation.mutators.ATLModel;
import evaluation.mutator.AbstractMutator;
import deletion.operator.AbstractDeletionMutator;
//import anatlyzer.evaluation.tester.Tester;
import cd.udem.fixingatlerror.Class2Rel;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import cd.udem.fixingatlerror.Operations;
import cd.udem.fixingatlerror.Operationspostprocessing;
import cd.udem.fixingatlerror.Settingpostprocessing;
import jmetal.metaheuristics.nsgaII.NSGAIIpostprocessing;
import jmetal.core.Solution;
import jmetal.metaheuristics.nsgaII.NSGAII;
import transML.utils.modeling.EMFUtils;

public abstract class AbstractModificationMutatorpost extends AbstractMutator {

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
	private String originalATLFile = "C:/Users/varaminz/Downloads/Project/workspaceTEST34/anatlyzer.atl.tests.api/trafo2/zahra2mutants/finalresult"
			+ Class2Rel.totalnumber + ".atl";
	private EMFModel atlModel3;
	private int indexrulespecificinput=-1;
	protected abstract List<Object> replacements(EMFModel atlModel, EClass oldFeatureValue, EObject object2modify,
			String currentAttributeValue, MetaModel metamodel);

	/**
	 * Generic modification. It allows subtypes of the class to modify.
	 * 
	 * @param atlModel
	 * @param outputFolder
	 * @param ToModifyClass
	 *            class of objects to modify (example Binding)
	 * @param featureName
	 *            feature to modify (example propertyName)
	 * @param metamodel
	 *            metamodel containing the candidate types for the modification
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

	/**
	 * Generic modification. The parameter 'exactContainerType' allows configuring
	 * whether the type of the class to modify must be exactly the one received, or
	 * if the subtypes should be also considered.
	 * 
	 * @param atlModel
	 * @param outputFolder
	 * @param ToModifyClass
	 *            class of objects to modify (example Binding)
	 * @param featureName
	 *            feature to modify (example propertyName)
	 * @param metamodel
	 *            metamodel containing the candidate types for the modification
	 * @param exactToModifyType
	 *            false to consider also subtypes of the class ToModify, true to
	 *            discard subtypes of the class ToModify
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

	public void FindErrorRule() {
		String stringSearch = "rule";
		String stringSearch2 = "}";

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
				if (indexfound > -1) {
					// System.out.println("Word is at position " + indexfound + " on line " +
					// linecount);
					NSGAII.writer.println("Word is at position " + indexfound + " on line " + linecount);
					NSGAII.faultrule.add(linecount);

				}
				if (indexfound2 > -1) {
					lastline = linecount;

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

	}

	protected <ToModify extends LocatedElement> List<Object> genericAttributeModification(EMFModel atlModel,
			String outputFolder, Class<ToModify> ToModifyClass, String feature, MetaModel metamodel,
			boolean exactToModifyType, ATLModel wrapper, Solution solution,CommonFunctionOperators cp) throws ATLCoreException {
				return null;
	}
	
	
	private <ToModify extends LocatedElement> List<Object> RemoveRedundancytarget(Solution solution, ATLModel wrapper, MetaModel metamodel,
			EMFModel atlModel, EDataTypeEList<String> comments, List<Object> ReturnResult, List<ToModify> modifiable,
			String featureb) {
		
			return null;
	}
	
	private <ToModify extends LocatedElement> List<Object> CheckIfThereIsStringTypeINallattrlist(List<String> allattrleft, ArrayList<Integer> locationredundancy
			,ATLModel wrapper, int h, int h2,int h3, String specificinput
			,Solution solution,  MetaModel metamodel , EMFModel atlModel ,
			EDataTypeEList<String> comments ,List<Object> ReturnResult , List<ToModify> modifiable,
			 String featureb ) {
			return null;
		
	}

	
	private <ToModify extends LocatedElement> List<Object> IfRightHandSideBindingIsNotStringType(String typhelper, String usingcondition, String typhelper2,
			String usingcondition2, String extractedtypemeta, String extractedtypemeta2, Solution solution,
			ATLModel wrapper, MetaModel metamodel, EMFModel atlModel, EDataTypeEList<String> comments,
			List<Object> ReturnResult, List<ToModify> modifiable, String featureb,
			ArrayList<Integer> locationredundancy, List<String> allattrleft, int h, int h2, int h3) {
				return null;
		
		
	}

	private <ToModify extends LocatedElement> List<Object> IfRightHandSideBindingIsStringType(String typhelper, String usingcondition, String typhelper2,
			String usingcondition2, String extractedtypemeta, String extractedtypemeta2, Solution solution,
			ATLModel wrapper, MetaModel metamodel, EMFModel atlModel, EDataTypeEList<String> comments,
			List<Object> ReturnResult, List<ToModify> modifiable, String featureb, ArrayList<Integer> locationredundancy,
			List<String> allattrleft, int h, int h2, int h3) {
		// TODO Auto-generated method stub
		if(typhelper!=null) {
			if(typhelper.equals("StringType")) {
				int randomInt=ChooseRandomStringType(allattrleft,"string",h,h2);	
				ReturnResult=ModifyInpatternElement( solution,  wrapper
							,  metamodel,  atlModel, comments,
							 ReturnResult,  modifiable,  featureb, locationredundancy.get(0),  allattrleft.get( randomInt * 2), h, h2);
				
			}
			
		}
		if(usingcondition!=null) {
			if(usingcondition.equals("EString") || usingcondition.equals("String")) {
				int randomInt=ChooseRandomStringType(allattrleft,"string",h,h2);	
				ReturnResult=ModifyInpatternElement( solution,  wrapper
							,  metamodel,  atlModel, comments,
							 ReturnResult,  modifiable,  featureb, locationredundancy.get(0),  allattrleft.get( randomInt * 2), h, h2);
				
			}
			
		}
		
		if(extractedtypemeta!=null) {
			if(extractedtypemeta.equals("EString") || extractedtypemeta.equals("String")) {
				int randomInt=ChooseRandomStringType(allattrleft,"string",h,h2);	
				ReturnResult=ModifyInpatternElement( solution,  wrapper
							,  metamodel,  atlModel, comments,
							 ReturnResult,  modifiable,  featureb, locationredundancy.get(0),  allattrleft.get( randomInt * 2), h, h2);
				
			}
			
		}
		
		if(typhelper2!=null) {
			if(typhelper2.equals("StringType")) {
				int randomInt=ChooseRandomStringType(allattrleft,"string",h,h3);	
				ReturnResult=ModifyInpatternElement( solution,  wrapper
							,  metamodel,  atlModel, comments,
							 ReturnResult,  modifiable,  featureb, locationredundancy.get(1),  allattrleft.get( randomInt * 2), h, h3);
				
			}
		}
		if(usingcondition2!=null) {
			if(usingcondition2.equals("EString") || usingcondition2.equals("String")) {
				int randomInt=ChooseRandomStringType(allattrleft,"string",h,h3);	
				ReturnResult=ModifyInpatternElement( solution,  wrapper
							,  metamodel,  atlModel, comments,
							 ReturnResult,  modifiable,  featureb, locationredundancy.get(1),  allattrleft.get( randomInt * 2), h, h3);
				
		
			}
		}
		if(extractedtypemeta2!=null) {
			if(extractedtypemeta2.equals("EString") || extractedtypemeta2.equals("String")) {
				int randomInt=ChooseRandomStringType(allattrleft,"string",h,h3);	
				ReturnResult=ModifyInpatternElement( solution,  wrapper
							,  metamodel,  atlModel, comments,
							 ReturnResult,  modifiable,  featureb, locationredundancy.get(1),  allattrleft.get( randomInt * 2), h, h3);
				
				
			}
			
		}
		
		
		return ReturnResult;
		

		
	}

	private List<Integer> ChooseRandomnotStringattrType(List<String> allattrleft, String string, int row, int column) {
	
				return null;
	}
		private int ChooseRandomStringType(List<String> allattrleft, String string, int row, int column) {
		// TODO Auto-generated method stub
			
		int index=-1;
		boolean checkifchoosedstringtype=false;
		while(checkifchoosedstringtype==false) {
			Random number_generator = new Random();
			int randomInt = number_generator.nextInt(allattrleft.size()/2);
			if(string.equals("string")) {
			if(allattrleft.get((randomInt * 2)+1).equals("String") || allattrleft.get((randomInt * 2)+1).equals("EString")) {
				 index=randomInt;
				 checkifchoosedstringtype=true;
				
			}
			}
			else {
				if(!allattrleft.get((randomInt * 2)+1).equals("String") && !allattrleft.get((randomInt * 2)+1).equals("EString")
						&& !allattrleft.get((randomInt * 2)+1).equals("Boolean")) {
					 index=randomInt;
					 checkifchoosedstringtype=true;
					
				}
				
			}
			
		}
		return index;
	}

	private String IfRightHandSideIsUsingCondition(ATLModel wrapper, int h, int h2) {
		// TODO Auto-generated method stub
				return null;
	}

	private List<String> ExtractwholeAttrLeftSide(String specificoutput,ATLModel wrapper, int h, int h2, Solution solution) {
		// TODO Auto-generated method stub
				return null;
	}

	private <ToModify extends LocatedElement> List<Object> Analysiseachbinding(Solution solution, ATLModel wrapper
			, MetaModel metamodel, EMFModel atlModel, EDataTypeEList<String> comments,
			List<Object> ReturnResult, List<ToModify> modifiableb, String featureb) {
		// TODO Auto-generated method stub
				return ReturnResult;
	}

	private <ToModify extends LocatedElement> List<Object> ModifyInpatternElement(Solution solution, ATLModel wrapper
	, MetaModel metamodel, EMFModel atlModel, EDataTypeEList<String> comments,
	List<Object> ReturnResult, List<ToModify> modifiable, String feature, int line, String correcttarget, int row, int column){
		
		List<Object> replacements = this.replacements(atlModel,  null,
				null, null, metamodel);
		int index=-1;
		for(int j=0;j<replacements.size();j++) {
			System.out.println((replacements.get(j)).toString());
			if((replacements.get(j)).toString().equals(correcttarget) ) {
				index=j;
				
			}
			
		}
		String comment =null;
		for(int k=0;k<modifiable.size();k++) {
			
			EObject object2modify_src = wrapper.source(modifiable.get(k));
			EStructuralFeature featureDefinition = wrapper.source(modifiable.get(k)).eClass()
					.getEStructuralFeature(feature);
			
			Object oldFeatureValue = object2modify_src.eGet(featureDefinition);
			String[] array = modifiable.get(k).getLocation().split(":", 2);
			if(Integer.parseInt(array[0])==line) {
				
				wrapper.source(modifiable.get(k)).eSet(featureDefinition,
						replacements.get(index));
				solution.getCoSolutionpostprocessing().getOp().listpropertyname.get(row).set(column,
						replacements.get(index).toString());
				
				comments.add("\n-- MUTATION \"" + this.getDescription() + "\" from "
						+ oldFeatureValue.toString() + " to " + replacements.get(index)
						+ " (line " + modifiable.get(k).getLocation()
						+ " of original transformation)\n");
				comment = "\n-- MUTATION \"" + this.getDescription() + "\" from "
						+ oldFeatureValue.toString() + " to " + replacements.get(index)
						+ " (line " + modifiable.get(k).getLocation()
						+ " of original transformation)\n";
				System.out.println("\n-- MUTATION \"" + this.getDescription() + "\" from "
						+ oldFeatureValue.toString() + " to " + replacements.get(index)
						+ " (line " + modifiable.get(k).getLocation()
						+ " of original transformation)\n");
				
				ReturnResult.set(0, wrapper);
				ReturnResult.set(1, atlModel);
				ReturnResult.add(comment);
				
			}
			
			
		}
		System.out.println("bib2");
		System.out.println(ReturnResult.get(0).toString());
		
		return ReturnResult;
		
	}
	private String Findhelpernavigationamongattribute(String specificoutput, List<String> helpernavigation) {
		// TODO Auto-generated method stub
	  return null;	
	}

	private String ExtractTypeRightHandSideIfThereisnotHelper(int h, int h2, String specificinput) {
		// TODO Auto-generated method stub
		
		
		return null;
	}

	
	private List<String> ReturnHelpetType(ATLModel wrapper, List<Helper> helper, int h, int h2, int i, int j) {
		// TODO Auto-generated method stub
		
		List<String> helpernavigation = new ArrayList<String>();
		String[] arrayhelp = helper.get(i).getLocation().split(":", 2);
		
		int nextlocation=-1;
		if(i+1!=helper.size()) {
			String[] array2 = helper.get(i+1).getLocation().split(":", 2);
				 nextlocation=Integer.parseInt(array2[0]);
		}
		else {
			nextlocation=NSGAIIpostprocessing.faultrule.get(0);
		}
		String helper_return  = helper.get(i).getDefinition().getFeature() instanceof Operation? 
                toString(((Operation)helper.get(i).getDefinition().getFeature()).getReturnType()):
                toString(((Attribute)helper.get(i).getDefinition().getFeature()).getType());
                List<VariableExp> variables = (List<VariableExp>)wrapper.allObjectsOf(VariableExp.class);
                for(int k=0;k<variables.size();k++) {
            		EObject navigationExpression = variables.get(k).eContainer();
            		if (navigationExpression instanceof NavigationOrAttributeCallExp) {
            			EStructuralFeature featureDefinition2 = wrapper.source(navigationExpression).eClass().getEStructuralFeature("name");
            			Object object2modify_src2 = wrapper.source(navigationExpression).eGet(featureDefinition2);
            			String[] arraynavi = variables.get(k).getLocation().split(":", 2);
            			if(Integer.parseInt(arraynavi[0])> Integer.parseInt(arrayhelp[0]) &&
            					Integer.parseInt(arraynavi[0])<nextlocation) {
            				helpernavigation.add( object2modify_src2.toString());
            				System.out.println("navihelp");
            				System.out.println(object2modify_src2.toString());
            			}
            		}
                }
                System.out.println("typehelper");    
        System.out.println(helper_return); 
        
        if(helper_return.equals("StringType")) {
        	
        	return helpernavigation;
        }
        else
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

	private boolean checkloopcondition(List<Integer> linepostprocessing, List<Integer> linepostprocessingcheck) {
		// TODO Auto-generated method stub
		
		for(int i=0;i<linepostprocessing.size();i++) {
			if(!linepostprocessingcheck.contains( linepostprocessing.get(i))) {
				
				return false;
			}
			
		}
		return true;
	}

	private List<Integer> Searchinglinehelpers(int helpernam) {
		// TODO Auto-generated method stub
		for(int i=0;i<NSGAIIpostprocessing.linehelper.size();i++) {
			if(NSGAIIpostprocessing.linehelper.get(i).contains(helpernam )) {
				
				return NSGAIIpostprocessing.linehelper.get(i);
			}
			
			
			
		}
		
		return null;
	}

	private List<String> Searchingallhelpers(String helpernam) {
		// TODO Auto-generated method stub
		for(int i=0;i<NSGAIIpostprocessing.listattrhelper.size();i++) {
			if(NSGAIIpostprocessing.listattrhelper.get(i).get(0).contains(helpernam )) {
				
				return NSGAIIpostprocessing.listattrhelper.get(i);
			}
			
			
			
		}
		return null;
		
	}

	private <ToModify extends LocatedElement> List<Object> OperationPreviousGenerationModefyBinding(int randomInt,
			Solution solution, EMFModel atlModel, MetaModel metamodel, List<ToModify> modifiable, ATLModel wrapper,
			String feature, EDataTypeEList<String> comments, List<Object> ReturnResult) {
				return null;
	}

	protected int FindIndexRule2(int array) {
		// TODO Auto-generated method stub
		int indexrule = -1;
		for (int j = 0; j < NSGAIIpostprocessing.faultrule.size(); j++) {

			if (array > NSGAIIpostprocessing.faultrule.get(j)
					&& array < NSGAIIpostprocessing.finalrule.get(j))

				indexrule = j;
			// NSGAII.errorrule.add(j);
		}
		return indexrule;

	}

	protected int FindIndexRule(String[] array) {
		// TODO Auto-generated method stub
		int indexrule = -1;
		for (int j = 0; j < NSGAIIpostprocessing.faultrule.size(); j++) {

			if (Integer.parseInt(array[0]) > NSGAIIpostprocessing.faultrule.get(j)
					&& Integer.parseInt(array[0]) < NSGAIIpostprocessing.finalrule.get(j))

				indexrule = j;
			// NSGAII.errorrule.add(j);
		}
		return indexrule;

	}

	private void SaveAsXMI(ATLModel wrapper) {
		// TODO Auto-generated method stub
		ResourceSet outputMetamodelResourceSet = new ResourceSetImpl();

		String str = "C:/Users/varaminz/Downloads/Project/workspaceTEST34/anatlyzer.atl.tests.api/trafo2/zahra2cover";
		Resource outputMetamodelResource = outputMetamodelResourceSet.createResource(
				URI.createFileURI(new File(str + "/fixmodel" + Class2Rel.totalnumber + ".xmi").getAbsolutePath()));
		outputMetamodelResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*",
				new XMLResourceFactoryImpl());
		outputMetamodelResource.getContents().add(wrapper.getRoot());
		try {
			outputMetamodelResource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected List<Integer> ReturnFirstIndex(int randomInt, int size, boolean checkmutationapply, int count,
			Solution solution) {
				return null;
	}

	

}
