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
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Module;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.atlext.OCL.BooleanExp;
import anatlyzer.atlext.OCL.IntegerExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.RealExp;
import anatlyzer.atlext.OCL.StringExp;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.util.ATLSerializer;
//import anatlyzer.evaluation.mutators.ATLModel;
import evaluation.mutator.AbstractMutator;
import deletion.operator.AbstractDeletionMutator;
import modification.invocation.operator.CollectionOperationModificationMutatorThreeStep;
//import anatlyzer.evaluation.tester.Tester;
import cd.udem.fixingatlerror.Class2Rel;
import cd.udem.fixingatlerror.Operations;
import cd.udem.fixingatlerror.Operationspostprocessing;
import cd.udem.fixingatlerror.Settingpostprocessing;
import jmetal.metaheuristics.nsgaII.NSGAIIpostprocessing;
import jmetal.core.Solution;
import jmetal.metaheuristics.nsgaII.NSGAII;
import transML.utils.modeling.EMFUtils;

public abstract class AbstractModificationMutator extends AbstractMutator {

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
			Solution solution) {
		try {
			return genericAttributeModification(atlModel, outputFolder, ToModifyClass, feature, metamodel, false,
					wrapper, solution);
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
			boolean exactToModifyType, ATLModel wrapper, Solution solution) throws ATLCoreException {
				return null;
	}
	
	private <ToModify extends LocatedElement> List<Object> OperationPreviousGenerationModefyBinding(int randomInt,
			Solution solution, EMFModel atlModel, MetaModel metamodel, List<ToModify> modifiable, ATLModel wrapper,
			String feature, EDataTypeEList<String> comments, List<Object> ReturnResult) {
				
		return null;
	}

	protected int FindIndexRule(String[] array) {
		// TODO Auto-generated method stub
		int indexrule = -1;
		for (int j = 0; j < NSGAII.faultrule.size(); j++) {

			if (Integer.parseInt(array[0]) > NSGAII.faultrule.get(j)
					&& Integer.parseInt(array[0]) < NSGAII.finalrule.get(j))

				indexrule = j;
			// NSGAII.errorrule.add(j);
		}
		return indexrule;

	}

	protected List<Integer> ReturnFirstIndex(int randomInt, int size, boolean checkmutationapply, int count,
			Solution solution) {
		
		
			Random number_generator = new Random();
			randomInt = number_generator.nextInt(size);
			checkmutationapply = true;
			solution.setpreviousgeneration(false);

		List<Integer> Result = new ArrayList<Integer>();
		Result.add(randomInt);
		if (checkmutationapply == true)
			Result.add(1);
		else
			Result.add(0);
		return Result;
	}
	

	protected void StoreTwoIndex(int randomInt, int randomInt2, int location) {
		// TODO Auto-generated method stub
		

	}

	

}
