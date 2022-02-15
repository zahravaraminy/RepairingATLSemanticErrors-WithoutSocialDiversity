package cd.udem.fixingatlerror;


import java.io.*;
import java.util.Map;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.atl.common.ATLExecutionException;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import exceptions.*;
import ruletypeextraction.files.*;
import ruletypesmm.RuletypesmmPackage;

public class Window  {

	
	String ATLTransName;
	Resource resource;
	public Window() {
	}


	/*Button that executes the whole thing. First, the ATL file is injected into a model.
	 *Then, types from the model are extracted. Finally, the Similarity Matrix is calculated */
	public void getSMClick(String pathATLTransformation, EMFModel atlModel2) {
		try{
		
			Setting s=new Setting();
			String pathSrcEcoreMMFile=s.gettargetmetamodel();
			String pathTrgEcoreMMFile= s.getsourcemetamodel();
			String pathOUTFile="/outputfootprints";
			boolean ok = checkFields(pathATLTransformation, pathSrcEcoreMMFile,
					pathTrgEcoreMMFile, pathOUTFile);

			if (ok){
				try {
					ExtractRuleTypes runner = new ExtractRuleTypes();
					//runner.loadModels("tempModel.xmi", pathSrcEcoreMMFile,pathTrgEcoreMMFile);
					runner.loadModels2(atlModel2, pathSrcEcoreMMFile,pathTrgEcoreMMFile);
					runner.doExtractRuleTypes(new NullProgressMonitor());
					runner.saveModels("tempTypesExtracted.xmi");
					RuletypesmmPackage.eINSTANCE.eClass(); 
					Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
					Map<String, Object> m = reg.getExtensionToFactoryMap();
					m.put("xmi", new XMIResourceFactoryImpl());
					 // Obtain a new resource set
					 ResourceSet resSet = new ResourceSetImpl();
					 // Get the resource
					 this.resource = resSet.getResource(URI
					   .createURI("tempwaelTypesExtracted.xmi"), true);
					
									} catch (ATLCoreException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ATLExecutionException e) {
				}	
			}
			else{
			}
		} catch (ParametersException e) {
		}
		

	}
	
	private boolean checkFields(String pathSrcMMEcoreFile, String pathTrgMMEcoreFile, String pathATLTransformation,
			String csvPath)	throws ParametersException {

		try {
			
			boolean ok = true;
			
			if (!new File(pathATLTransformation).exists()) {
				//messageLabel
				//		.setText("  ERROR. File for the ATL transformation not found.");
				ok = false;
			} else if (!new File(pathSrcMMEcoreFile).exists() && !pathSrcMMEcoreFile.startsWith("http://")) {
			//	messageLabel
				//		.setText("  ERROR. File for source metamodel not found.");
				ok = false;
			} else if (!new File(pathTrgMMEcoreFile).exists() && !pathTrgMMEcoreFile.startsWith("http://")) {
			//	messageLabel
				//		.setText("  ERROR. File for the target metamodel not found.");
				ok = false;
			} else if (!new File(csvPath).exists() && !csvPath.contains(" ")) {
				File file = new File(csvPath);
				file.mkdir();
			}
			
			return ok;
			
		} catch (NumberFormatException e) {
			throw new ParametersException(
					"ERROR. Insert a natural number for the number model generated with the ASSL file.");
		}
	}
}
