package produce.output.xmimodel;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.EmftvmFactory;
import org.eclipse.m2m.atl.emftvm.ExecEnv;
import org.eclipse.m2m.atl.emftvm.Metamodel;
import org.eclipse.m2m.atl.emftvm.Model;
import org.eclipse.m2m.atl.emftvm.compiler.AtlToEmftvmCompiler;
import org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.util.DefaultModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.ModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.TimingData;

public class ATLLauncher2 {
	
	AtlToEmftvmCompiler compiler = new AtlToEmftvmCompiler();
	public final static String path="C:/Users/varaminz/Downloads/Project/WorkspaceFixingATLError/FixingATLError/cd.udem.fixingatlerror/Tmp/varaminz/step3new/examples/class2rel/metamodels/";
	// Some constants for quick initialization and testing.
	public final static String IN_METAMODEL = path+"Class.ecore"; //"./Metamodeles/Composed.ecore";
	public final static String IN_METAMODEL_NAME = "Class"; //"Composed";
	
	
	public final static String IN_METAMODEL_2 = path+"Relational.ecore"; //"./Metamodeles/Composed.ecore";
	public final static String IN_METAMODEL_NAME_2 = "Relational"; //"Composed";
	
	
	public final static String OUT_METAMODEL = path+"Relational.ecore";
	public final static String OUT_METAMODEL_NAME = "Relational";
	
	
	public final static String IN_MODEL =  path+"Class_input0.xmi"; // "./Modeles/composed.xmi";
	public final static String IN_MODEL_2 =  path+"dockout0.xmi";  //
	public final static String OUT_MODEL = path+"dockout0.xmi";	  //
	
	public final static String TRANSFORMATION_DIR =path;
	public final static String TRANSFORMATION_MODULE= "Class2Relational"; //"Composed2Simple";
	
	// The input and output metamodel nsURIs are resolved using lazy registration of metamodels, see below.
	private String inputMetamodelNsURI;
	private String inputMetamodelNsURI2;
	private String outputMetamodelNsURI;
	
	//Main transformation launch method
	public void launch(String inMetamodelPath, String inModelPath, String inMetamodelPath2, String inModelPath2, String outMetamodelPath,
			String outModelPath, String transformationDir, String transformationModule){
		
		/* 
		 * Creates the execution environment where the transformation is going to be executed,
		 * you could use an execution pool if you want to run multiple transformations in parallel,
		 * but for the purpose of the example let's keep it simple.
		 */
		ExecEnv env = EmftvmFactory.eINSTANCE.createExecEnv();
		ResourceSet rs = new ResourceSetImpl();

		/*
		 * Load meta-models in the resource set we just created, the idea here is to make the meta-models
		 * available in the context of the execution environment, the ResourceSet is later passed to the
		 * ModuleResolver that is the actual class that will run the transformation.
		 * Notice that we use the nsUris to locate the metamodels in the package registry, we initialize them 
		 * from Ecore files that we registered lazily as shown below in e.g. registerInputMetamodel(...) 
		 */
		Metamodel inMetamodel = EmftvmFactory.eINSTANCE.createMetamodel();
		//inMetamodel.setResource(CodePackage.eINSTANCE.eResource());
		inMetamodel.setResource(rs.getResource(URI.createURI(inputMetamodelNsURI), true));
		env.registerMetaModel(IN_METAMODEL_NAME, inMetamodel);
		
		Metamodel outMetamodel = EmftvmFactory.eINSTANCE.createMetamodel();
		outMetamodel.setResource(rs.getResource(URI.createURI(outputMetamodelNsURI), true));
		env.registerMetaModel(OUT_METAMODEL_NAME, outMetamodel);
		
		/*
		 * Create and register resource factories to read/parse .xmi and .emftvm files,
		 * we need an .xmi parser because our in/output models are .xmi and our transformations are
		 * compiled using the ATL-EMFTV compiler that generates .emftvm files
		 */
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("emftvm", new EMFTVMResourceFactoryImpl());
		
		Model outModel = EmftvmFactory.eINSTANCE.createModel();
		outModel.setResource(rs.createResource(URI.createURI(outModelPath)));
		env.registerOutputModel("OUT", outModel);
		
		// Load models
		Model inModel = EmftvmFactory.eINSTANCE.createModel();
		Resource sourceResource = loadModel(inModelPath, rs);
		//inModel.setResource(rs.getResource(URI.createURI(inModelPath, true), true));
		inModel.setResource(sourceResource);
		env.registerInputModel("IN", inModel);
		
		
		
		/*
		 *  Load and run the transformation module
		 *  Point at the directory your transformations are stored, the ModuleResolver will 
		 *  look for the .emftvm file corresponding to the module you want to load and run
		 */
		try {
			compileATLModule(transformationDir + TRANSFORMATION_MODULE);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ModuleResolver mr = new DefaultModuleResolver(transformationDir, rs);
		TimingData td = new TimingData();
		env.loadModule(mr, TRANSFORMATION_MODULE);
		td.finishLoading();
		env.run(td);
		td.finish();
			
		// Save models
		try {
			outModel.getResource().save(Collections.emptyMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * I seriously hate relying on the eclipse facilities, and if you're not building an eclipse plugin
	 * you can't rely on eclipse's registry (let's say you're building a stand-alone tool that needs to run ATL
	 * transformation, you need to 'manually' register your metamodels) 
	 * This method does two things, it initializes an Ecore parser and then programmatically looks for
	 * the package definition on it, obtains the NsUri and registers it.
	 */
	private  void compileATLModule(String path) throws FileNotFoundException {
		//AtlToEmftvmCompiler compiler = new AtlToEmftvmCompiler();
		InputStream fin = new FileInputStream(path + ".atl");
		//Print used memory
				//System.out.println("Used Memory before compile in compile:"
				//	+ (Class2Rel.runtime.totalMemory() - Class2Rel.runtime.freeMemory()) / (1024*1024));
		compiler.compile(fin, path + ".emftvm");
		try {
			fin.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	private  Resource loadModel(String path, ResourceSet rs) {
		return rs.getResource(URI.createFileURI(path), true);
	}
	private String lazyMetamodelRegistration(String metamodelPath){
		
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
   	
	    ResourceSet rs = new ResourceSetImpl();
	    // Enables extended meta-data, weird we have to do this but well...
	    final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(EPackage.Registry.INSTANCE);
	    rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
	
	    Resource r = rs.getResource(URI.createFileURI(metamodelPath), true);
	    EObject eObject = r.getContents().get(0);
	    // A meta-model might have multiple packages we assume the main package is the first one listed
	    if (eObject instanceof EPackage) {
	        EPackage p = (EPackage)eObject;
	        System.out.println(p.getNsURI());
	        EPackage.Registry.INSTANCE.put(p.getNsURI(), p);
	        return p.getNsURI();
	    }
	    return null;
	}
	
	/*
	 * As shown above we need the inputMetamodelNsURI and the outputMetamodelNsURI to create the context of
	 * the transformation, so we simply use the return value of lazyMetamodelRegistration to store them.
	 * -- Notice that the lazyMetamodelRegistration(..) implementation may return null in case it doesn't 
	 * find a package in the given metamodel, so watch out for malformed metamodels.
	 * 
	 */
	public void registerInputMetamodel(String inputMetamodelPath, String inputMetamodelPath2){	
		inputMetamodelNsURI = lazyMetamodelRegistration(inputMetamodelPath);
	//	inputMetamodelNsURI2 = lazyMetamodelRegistration(inputMetamodelPath2);
	}

	public void registerOutputMetamodel(String outputMetamodelPath){
		outputMetamodelNsURI = lazyMetamodelRegistration(outputMetamodelPath);
	}
	
	/*
	 *  A test main method, I'm using constants so I can quickly change the case study by simply
	 *  modifying the header of the class.
	 */	
	public static void main(String[] args){
		ATLLauncher2 l = new ATLLauncher2();
		l.registerOutputMetamodel(OUT_METAMODEL);
		l.registerInputMetamodel(IN_METAMODEL , IN_METAMODEL_2);
		
		l.launch(IN_METAMODEL, IN_MODEL, IN_METAMODEL_2, IN_MODEL_2 ,OUT_METAMODEL, OUT_MODEL, TRANSFORMATION_DIR, TRANSFORMATION_MODULE);
	}
}
