// package fr.eseo.atl;
package produce.output.xmimodel;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.EmftvmFactory;
import org.eclipse.m2m.atl.emftvm.ExecEnv;
import org.eclipse.m2m.atl.emftvm.Metamodel;
import org.eclipse.m2m.atl.emftvm.Model;
import org.eclipse.m2m.atl.emftvm.compiler.AtlToEmftvmCompiler;
import org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceImpl;
import org.eclipse.m2m.atl.emftvm.util.ClassModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.DefaultModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.ModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.VMException;

import cd.udem.fixingatlerror.Class2Rel;

public class LaunchATLHelper3 {

	public static void main(String[] args) throws IOException {
		Resource[] ret = null;
		
		String path="Tmp/varaminz/step3new/experiment11"+"/transformation1"+"/";
		LaunchATLHelper3 l = new LaunchATLHelper3(path+"HSM.ecore",path+"FSM.ecore",
				path+"HSM_input0.xmi",path+"HSM_input1.xmi",path+"HSM_input2.xmi",path+"HSM_input3.xmi");
		try {
			ret = l.applyTransformationmodels(
					 path+"targetdockbook.xmi", path+"targetdockbook1.xmi", path+"targetdockbook2.xmi", path+"targetdockbook3.xmi", path, "HSM2FSM");
	//		ret.save(Collections.EMPTY_MAP);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 for(int i=0;i<ret.length;i++) {
			 if(ret[i]!=null) {
			 ret[i].save(Collections.EMPTY_MAP);
				// remove resource from its ResourceSet, memory leak otherwise
			//	ret[i].getResourceSet().getResources().remove(ret[i]);
			 }
		 } 
	}
	
	ResourceSet rs = null;
	Map<String, Metamodel> metamodels = new HashMap<String, Metamodel>();
	Model sourceModel;
	Model sourceModel2;
	Model sourceModel3;
	Model sourceModel4;
	AtlToEmftvmCompiler compiler = new AtlToEmftvmCompiler();

	final int CLEANUP_THRESHOLD = 32;

	public LaunchATLHelper3(String sourceMetamodelPath, String targetMetamodelPath, String sourceModelPath,
			String sourceModelPath2, String sourceModelPath3, String sourceModelPath4) {

		rs = new ResourceSetImpl();
		// use to load ecore and XMI files
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
			"ecore",
			new EcoreResourceFactoryImpl()
		);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				"xmi",
				new XMIResourceFactoryImpl()
			);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				"emftvm",
				new EMFTVMResourceFactoryImpl()
			);
		
		// load source & target metamodels
		loadMetamodel(sourceMetamodelPath);
		loadMetamodel(targetMetamodelPath);
		
		// load the source model from file
		Resource sourceResource = loadModel(sourceModelPath, rs);
		sourceModel = EmftvmFactory.eINSTANCE.createModel();
		sourceModel.setResource(sourceResource);
		
		Resource sourceResource2 = loadModel(sourceModelPath2, rs);
		sourceModel2 = EmftvmFactory.eINSTANCE.createModel();
		sourceModel2.setResource(sourceResource2);
		if(sourceModelPath3!=null) {
		Resource sourceResource3 = loadModel(sourceModelPath3, rs);
		sourceModel3 = EmftvmFactory.eINSTANCE.createModel();
		sourceModel3.setResource(sourceResource3);
		}
		Resource sourceResource4 = loadModel(sourceModelPath4, rs);
		sourceModel4 = EmftvmFactory.eINSTANCE.createModel();
		sourceModel4.setResource(sourceResource4);
	}

	

	public LaunchATLHelper3() {
		// TODO Auto-generated constructor stub
	}



	public void cleanup() {
		// when compiling ATL modules, EMFTVM stores the resulting model in the ResourceSet which grows indefinitely
		// quick and dirty way of cleaning up those unnecessary models from ResourceSet
		rs.getResources().removeIf((it) -> {return it instanceof EMFTVMResourceImpl;});
	}
	public Resource[] applyTransformationmodels(String targetModelPath,String targetModelPath2,String targetModelPath3,String targetModelPath4 ,String ATLPath, String moduleName) throws Exception {

		
		Resource[] targetlists = new Resource[4];
		
		// create an execution environment 
		ExecEnv execEnv = EmftvmFactory.eINSTANCE.createExecEnv(); // ExecEnv is specific to a transformation, thus it cannot be reused
		
		// register metamodels in ExecEnv
		metamodels.forEach((name, mm) -> {
			execEnv.registerMetaModel(name, mm);
		});

		compileATLModule(ATLPath + moduleName);
		ModuleResolver mr = new DefaultModuleResolver(ATLPath, rs);
		execEnv.loadModule(mr, moduleName);
		// register source model as the IN model in ATL transformation
		//if(model_index.contains(2)) {
				
				execEnv.registerInputModel("IN", sourceModel);
				// create a new resource for target model
				Resource targetResource = createModel(targetModelPath, rs);
				Model targetModel = EmftvmFactory.eINSTANCE.createModel();
				targetModel.setResource(targetResource);
				// register target model as the OUT model in ATL transformation
				execEnv.registerOutputModel("OUT", targetModel);
				try {
					execEnv.run(null);			  // this goes Exception
					
				}
				catch ( VMException  e) {
					
					System.err.println("Cannot apply transformation " + moduleName);
					//
					System.err.println(e.getMessage());
					
				}
				catch ( NullPointerException  e) {
					System.err.println("null pointer exception " + moduleName);
					//
					System.err.println(e.getMessage());
			//		throw e; 
				}
				catch ( IllegalArgumentException  e) {   // this new exception is for restore execEnv
					cleanup();
					execEnv.clearModels();
					System.err.println(e.getMessage());
					System.err.println("illegal exception " + moduleName);
					throw e;
				}
				
				targetlists[0]=targetResource;
		//}
				
	//	if(model_index.contains(3)) {	
				execEnv.registerInputModel("IN", sourceModel2);
				// create a new resource for target model
				Resource targetResource2 = createModel(targetModelPath2, rs);
				Model targetModel2 = EmftvmFactory.eINSTANCE.createModel();
				targetModel2.setResource(targetResource2);
				// register target model as the OUT model in ATL transformation
				execEnv.registerOutputModel("OUT", targetModel2);
				try {
					execEnv.run(null);			
				}
				catch (VMException e) {
					System.err.println("Cannot apply transformation " + moduleName);
					System.err.println(e.getMessage());
				}
				catch ( IllegalArgumentException  e) {   // this new exception is for restore execEnv
					cleanup();
					execEnv.clearModels();
					System.err.println("illegal exception " + moduleName);
					throw e;
				}
				targetlists[1]=targetResource2;
		//}
		
				execEnv.registerInputModel("IN", sourceModel3);
				// create a new resource for target model
				Resource targetResource3 = createModel(targetModelPath3, rs);
				Model targetModel3 = EmftvmFactory.eINSTANCE.createModel();
				targetModel3.setResource(targetResource3);
				// register target model as the OUT model in ATL transformation
				execEnv.registerOutputModel("OUT", targetModel3);
				try {
					execEnv.run(null);			
				}
				catch (VMException e) {
					System.err.println("Cannot apply transformation " + moduleName);
					System.err.println(e.getMessage());
				}
				catch ( IllegalArgumentException  e) {   // this new exception is for restore execEnv
					cleanup();
					execEnv.clearModels();
					System.err.println("illegal exception " + moduleName);
					throw e;
				}
				targetlists[2]=targetResource3;
		
	//	if(model_index.contains(1)) {
				execEnv.registerInputModel("IN", sourceModel4);
				// create a new resource for target model
				Resource targetResource4 = createModel(targetModelPath4, rs);
				Model targetModel4 = EmftvmFactory.eINSTANCE.createModel();
				targetModel4.setResource(targetResource4);
				// register target model as the OUT model in ATL transformation
				execEnv.registerOutputModel("OUT", targetModel4);
				try {
					execEnv.run(null);			
				}
				catch (VMException e) {
					System.err.println("Cannot apply transformation " + moduleName);
					System.err.println(e.getMessage());
				}
				catch ( IllegalArgumentException  e) {   // this new exception is for restore execEnv
					cleanup();
					execEnv.clearModels();
					System.err.println("illegal exception " + moduleName);
					throw e;
				}
				targetlists[3]=targetResource4;
	//	}
				// create a new ClassModuleResolver
				// this is used resolve ATL modules
				if (rs.getResources().size() > CLEANUP_THRESHOLD) {
					cleanup();
				}
				
			return targetlists;
	}


	
	public Resource[] applyTransformation(String targetModelPath,String targetModelPath2,String targetModelPath3,String targetModelPath4 ,String ATLPath, String moduleName) throws Exception {
		
		Resource[] targetlists = new Resource[4];
		ExecEnv execEnv = EmftvmFactory.eINSTANCE.createExecEnv(); // ExecEnv is specific to a transformation, thus it cannot be reused
		
		// register metamodels in ExecEnv
		metamodels.forEach((name, mm) -> {
			execEnv.registerMetaModel(name, mm);
		});

		// register source model as the IN model in ATL transformation
				execEnv.registerInputModel("IN", sourceModel);
				// create a new resource for target model
				Resource targetResource = createModel(targetModelPath, rs);
				Model targetModel = EmftvmFactory.eINSTANCE.createModel();
				targetModel.setResource(targetResource);
				// register target model as the OUT model in ATL transformation
				execEnv.registerOutputModel("OUT", targetModel);
				
				//Print used memory
				System.out.println("Used Memory before compile:"
					+ (Class2Rel.runtime.totalMemory() - Class2Rel.runtime.freeMemory()) / (1024*1024));

				compileATLModule(ATLPath + moduleName);
				//Print used memory
				System.out.println("Used Memory after compile atl:"
					+ (Class2Rel.runtime.totalMemory() - Class2Rel.runtime.freeMemory()) / (1024*1024));
				
				ModuleResolver mr = new DefaultModuleResolver(ATLPath, rs);
				execEnv.loadModule(mr, moduleName);
		// create a new resource for target model
				try {
					execEnv.run(null);			
				}
				catch (VMException e) {
					System.err.println("Cannot apply transformation " + moduleName);
					System.err.println(e.getMessage());
				}
				
				targetlists[0]=targetResource;
				
				execEnv.registerInputModel("IN", sourceModel2);
				// create a new resource for target model
				Resource targetResource2 = createModel(targetModelPath2, rs);
				Model targetModel2 = EmftvmFactory.eINSTANCE.createModel();
				targetModel2.setResource(targetResource2);
				// register target model as the OUT model in ATL transformation
				execEnv.registerOutputModel("OUT", targetModel2);
				try {
					execEnv.run(null);			
				}
				catch (VMException e) {
					System.err.println("Cannot apply transformation " + moduleName);
					System.err.println(e.getMessage());
				}
				targetlists[1]=targetResource2;
				
				execEnv.registerInputModel("IN", sourceModel3);
				// create a new resource for target model
				Resource targetResource3 = createModel(targetModelPath3, rs);
				Model targetModel3 = EmftvmFactory.eINSTANCE.createModel();
				targetModel3.setResource(targetResource3);
				// register target model as the OUT model in ATL transformation
				execEnv.registerOutputModel("OUT", targetModel3);
				try {
					execEnv.run(null);			
				}
				catch (VMException e) {
					System.err.println("Cannot apply transformation " + moduleName);
					System.err.println(e.getMessage());
				}
				targetlists[2]=targetResource3;
				
				execEnv.registerInputModel("IN", sourceModel4);
				// create a new resource for target model
				Resource targetResource4 = createModel(targetModelPath4, rs);
				Model targetModel4 = EmftvmFactory.eINSTANCE.createModel();
				targetModel4.setResource(targetResource4);
				// register target model as the OUT model in ATL transformation
				execEnv.registerOutputModel("OUT", targetModel4);
				try {
					execEnv.run(null);			
				}
				catch (VMException e) {
					System.err.println("Cannot apply transformation " + moduleName);
					System.err.println(e.getMessage());
				}
				targetlists[3]=targetResource4;
				// create a new ClassModuleResolver
				// this is used resolve ATL modules
				if (rs.getResources().size() > CLEANUP_THRESHOLD) {
					cleanup();
				}
				
			return targetlists;
	}
	
	
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
		//compiler = null;
		//Print used memory
			//	System.out.println("Used Memory after compile in compile:"
				//	+ (Class2Rel.runtime.totalMemory() - Class2Rel.runtime.freeMemory()) / (1024*1024));

		
	}
	private  void loadMetamodel(String path) { 
		//loadMetamodel(String path, ResourceSet rs, ExecEnv env)
		Resource resource = rs.getResource(URI.createFileURI(path), true);
		
		for (EObject e : resource.getContents()) {
			if (e instanceof EPackage) {
				final EPackage ep = (EPackage)e;
				// register the metamodel in package registry to later read the model
				rs.getPackageRegistry().put(
					ep.getNsURI(),
					ep.getEFactoryInstance().getEPackage() // isn't this the same as just e ?
				);
				// create the ATL metamodel
				Metamodel mm = EmftvmFactory.eINSTANCE.createMetamodel();
				mm.setResource(ep.eResource());
				
				metamodels.put(ep.getName(), mm);
				// register the ATL metamodel in the exec env
				//env.registerMetaModel(ep.getName(), mm);
			}
		}
	}

	private  Resource loadModel(String path, ResourceSet rs) {
		return rs.getResource(URI.createFileURI(path), true);
	}

	private  Resource createModel(String path, ResourceSet rs) {
		return rs.createResource(URI.createFileURI(path));
	}
		
}
