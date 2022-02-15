/*******************************************************************************
 * Copyright (c) 2010, 2011 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package ruletypeextraction.files;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.m2m.atl.common.ATLExecutionException;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.IExtractor;
import org.eclipse.m2m.atl.core.IInjector;
import org.eclipse.m2m.atl.core.IModel;
import org.eclipse.m2m.atl.core.IReferenceModel;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFExtractor;
import org.eclipse.m2m.atl.core.emf.EMFInjector;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.launch.ILauncher;
import org.eclipse.m2m.atl.engine.emfvm.launch.EMFVMLauncher;

/**
 * Entry point of the 'ExtractRuleTypes' transformation module.
 */
public class ExtractRuleTypes {

	/**
	 * The property file. Stores module list, the metamodel and library locations.
	 * @generated
	 */
	private Properties properties;
	
	/**
	 * The IN model.
	 * @generated
	 */
	protected IModel inModel;	
	
	/**
	 * The IN_Src model.
	 * @generated
	 */
	protected IModel in_srcModel;

	/**
	 * The IN_Trg model.
	 * @generated
	 */
	protected IModel in_trgModel;

	/**
	 * The OUT model.
	 * @generated
	 */
	protected IModel outModel;	
		
	/**
	 * The main method.
	 * 
	 * @param args
	 *            are the arguments
	 * @throws IOException 
	 * @generated
	 */
	
	public static void main(String[] args) throws IOException{
		try {
			if (args.length < 4) {
				System.out.println("Arguments not valid : {IN_model_path, IN_Src_model_path, IN_Trg_model_path, OUT_model_path}.");
			} else {
				ExtractRuleTypes runner = new ExtractRuleTypes();
				runner.loadModels(args[0], args[1], args[2]);
				runner.doExtractRuleTypes(new NullProgressMonitor());
				runner.saveModels(args[3]);
			}
		} catch (ATLCoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ATLExecutionException e) {
			e.printStackTrace();
		}
	}

	public static void main2 (String[] args) throws IOException{
		try {
			if (args.length < 4) {
				System.out.println("Arguments not valid : {IN_model_path, IN_Src_model_path, IN_Trg_model_path, OUT_model_path}.");
			} else {
				ExtractRuleTypes runner = new ExtractRuleTypes();
				runner.loadModels(args[0], args[1], args[2]);
				runner.doExtractRuleTypes(new NullProgressMonitor());
				runner.saveModels(args[3]);
			}
		} catch (ATLCoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ATLExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public void extractRuleTypes(String transfoInjected, String srcMM, String trgMM, String outFile) {
		try {
			ExtractRuleTypes runner = new ExtractRuleTypes();
			runner.loadModels(transfoInjected, srcMM, trgMM);
			runner.doExtractRuleTypes(new NullProgressMonitor());
			runner.saveModels(outFile);
		} catch (ATLCoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ATLExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor.
	 *
	 * @generated
	 */
	public ExtractRuleTypes() throws IOException {
		properties = new Properties();
		properties.load(getFileURL("ExtractRuleTypes.properties").openStream());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	}
	
	/**
	 * Load the input and input/output models, initialize output models.
	 * 
	 * @param inModelPath
	 *            the IN model path
	 * @param insrcModelPath
	 *            the INSrc model path
	 * @param intrgModelPath
	 *            the INTrg model path
	 * @throws ATLCoreException
	 *             if a problem occurs while loading models
	 *
	 * @generated
	 */
	public void loadModels(String inModelPath, String in_srcModelPath, String in_trgModelPath) throws ATLCoreException {
		ModelFactory factory = new EMFModelFactory();
		IInjector injector = new EMFInjector();
	 	IReferenceModel mm_outMetamodel = factory.newReferenceModel();
		injector.inject(mm_outMetamodel, getMetamodelUri("MM_Out"));
	 	IReferenceModel mm_atlMetamodel = factory.newReferenceModel();
		injector.inject(mm_atlMetamodel, getMetamodelUri("MM_ATL"));
	 	IReferenceModel mm_trgMetamodel = factory.newReferenceModel();
		injector.inject(mm_trgMetamodel, getMetamodelUri("MM_Trg"));
	 	IReferenceModel mm_srcMetamodel = factory.newReferenceModel();
		injector.inject(mm_srcMetamodel, getMetamodelUri("MM_Src"));
		this.inModel = factory.newModel(mm_atlMetamodel);
		injector.inject(inModel, inModelPath);
		this.in_srcModel = factory.newModel(mm_srcMetamodel);
		injector.inject(in_srcModel, in_srcModelPath);
		this.in_trgModel = factory.newModel(mm_trgMetamodel);
		injector.inject(in_trgModel, in_trgModelPath);
		this.outModel = factory.newModel(mm_outMetamodel);
	}
	public void loadModels2(EMFModel inModelPath, String in_srcModelPath, String in_trgModelPath) throws ATLCoreException {
		ModelFactory factory = new EMFModelFactory();
		IInjector injector = new EMFInjector();
	 	IReferenceModel mm_outMetamodel = factory.newReferenceModel();
		injector.inject(mm_outMetamodel, getMetamodelUri("MM_Out"));
	 	IReferenceModel mm_atlMetamodel = factory.newReferenceModel();
		injector.inject(mm_atlMetamodel, getMetamodelUri("MM_ATL"));
	 	IReferenceModel mm_trgMetamodel = factory.newReferenceModel();
		injector.inject(mm_trgMetamodel, getMetamodelUri("MM_Trg"));
	 	IReferenceModel mm_srcMetamodel = factory.newReferenceModel();
		injector.inject(mm_srcMetamodel, getMetamodelUri("MM_Src"));
	//	this.inModel = factory.newModel(mm_atlMetamodel);
//		injector.inject(inModel, inModelPath);
		this.inModel = (IModel)inModelPath ;
		this.in_srcModel = factory.newModel(mm_srcMetamodel);
		injector.inject(in_srcModel, in_srcModelPath);
		this.in_trgModel = factory.newModel(mm_trgMetamodel);
		injector.inject(in_trgModel, in_trgModelPath);
		this.outModel = factory.newModel(mm_outMetamodel);
	}


	/**
	 * Save the output and input/output models.
	 * 
	 * @param outModelPath
	 *            the OUT model path
	 * @throws ATLCoreException
	 *             if a problem occurs while saving models
	 *
	 * @generated
	 */
	public void saveModels(String outModelPath) throws ATLCoreException {
		IExtractor extractor = new EMFExtractor();
		extractor.extract(outModel, outModelPath);
	}
	
	public IModel getoutModel() throws ATLCoreException {
			return this.outModel;}

	/**
	 * Transform the models.
	 * 
	 * @param monitor
	 *            the progress monitor
	 * @throws ATLCoreException
	 *             if an error occurs during models handling
	 * @throws IOException
	 *             if a module cannot be read
	 * @throws ATLExecutionException
	 *             if an error occurs during the execution
	 *
	 * @generated
	 */
	public Object doExtractRuleTypes(IProgressMonitor monitor) throws ATLCoreException, IOException, ATLExecutionException {
		ILauncher launcher = new EMFVMLauncher();
		Map<String, Object> launcherOptions = getOptions();
		launcher.initialize(launcherOptions);
		launcher.addInModel(inModel, "IN", "MM_ATL");
		launcher.addInModel(in_srcModel, "IN_Src", "MM_Src");
		launcher.addInModel(in_trgModel, "IN_Trg", "MM_Trg");
		launcher.addOutModel(outModel, "OUT", "MM_Out");
		return launcher.launch("run", monitor, launcherOptions, (Object[]) getModulesList());
	}
	
	/**
	 * Returns an Array of the module input streams, parameterized by the
	 * property file.
	 * 
	 * @return an Array of the module input streams
	 * @throws IOException
	 *             if a module cannot be read
	 *
	 * @generated
	 */
	protected InputStream[] getModulesList() throws IOException {
		InputStream[] modules = null;
		String modulesList = properties.getProperty("ExtractRuleTypes.modules");
		if (modulesList != null) {
			String[] moduleNames = modulesList.split(",");
			modules = new InputStream[moduleNames.length];
			for (int i = 0; i < moduleNames.length; i++) {
				String asmModulePath = new Path(moduleNames[i].trim()).removeFileExtension().addFileExtension("asm").toString();
				modules[i] = getFileURL(asmModulePath).openStream();
			}
		}
		return modules;
	}
	
	/**
	 * Returns the URI of the given metamodel, parameterized from the property file.
	 * 
	 * @param metamodelName
	 *            the metamodel name
	 * @return the metamodel URI
	 *
	 * @generated
	 */
	protected String getMetamodelUri(String metamodelName) {
		return properties.getProperty("ExtractRuleTypes.metamodels." + metamodelName);
	}
	
	/**
	 * Returns the file name of the given library, parameterized from the property file.
	 * 
	 * @param libraryName
	 *            the library name
	 * @return the library file name
	 *
	 * @generated
	 */
	protected InputStream getLibraryAsStream(String libraryName) throws IOException {
		return getFileURL(properties.getProperty("ExtractRuleTypes.libraries." + libraryName)).openStream();
	}
	
	/**
	 * Returns the options map, parameterized from the property file.
	 * 
	 * @return the options map
	 *
	 * @generated
	 */
	protected Map<String, Object> getOptions() {
		Map<String, Object> options = new HashMap<String, Object>();
		for (Entry<Object, Object> entry : properties.entrySet()) {
			if (entry.getKey().toString().startsWith("ExtractRuleTypes.options.")) {
				options.put(entry.getKey().toString().replaceFirst("ExtractRuleTypes.options.", ""), 
				entry.getValue().toString());
			}
		}
		return options;
	}
	
	/**
	 * Finds the file in the plug-in. Returns the file URL.
	 * 
	 * @param fileName
	 *            the file name
	 * @return the file URL
	 * @throws IOException
	 *             if the file doesn't exist
	 * 
	 * @generated
	 */
	protected static URL getFileURL(String fileName) throws IOException {
		final URL fileURL;
		if (isEclipseRunning()) {
			URL resourceURL = ExtractRuleTypes.class.getResource(fileName);
			if (resourceURL != null) {
				fileURL = FileLocator.toFileURL(resourceURL);
			} else {
				fileURL = null;
			}
		} else {
			fileURL = ExtractRuleTypes.class.getResource(fileName);
		}
		if (fileURL == null) {
			throw new IOException("'" + fileName + "' not found");
		} else {
			return fileURL;
		}
	}

	/**
	 * Tests if eclipse is running.
	 * 
	 * @return <code>true</code> if eclipse is running
	 *
	 * @generated
	 */
	public static boolean isEclipseRunning() {
		try {
			return Platform.isRunning();
		} catch (Throwable exception) {
			// Assume that we aren't running.
		}
		return false;
	}
}
