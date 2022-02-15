//  NSGAII.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.metaheuristics.nsgaII;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFReferenceModel;
import org.eclipse.m2m.atl.engine.parser.AtlParser;
import cd.udem.fixingatlerror.CoSolution;
import cd.udem.fixingatlerror.Setting;
import cd.udem.fixingatlerror.Settingpostprocessing;
import jmetal.core.*;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;
import produce.output.xmimodel.LaunchATLHelper;

/** 
 *  Implementation of NSGA-II.
 *  This implementation of NSGA-II makes use of a QualityIndicator object
 *  to obtained the convergence speed of the algorithm. This version is used
 *  in the paper:
 *     A.J. Nebro, J.J. Durillo, C.A. Coello Coello, F. Luna, E. Alba 
 *     "A Study of Convergence Speed in Multi-Objective Metaheuristics." 
 *     To be presented in: PPSN'08. Dortmund. September 2008.
 */

public class NSGAIIpostprocessing extends Algorithm {
  /**
   * Constructor
   * @param problem Problem to solve
   */
  public NSGAIIpostprocessing(Problem problem) {
    super (problem) ;
  } // NSGAIIZA
  public void setString(String str){
      this.statemutcrossinitial = str;
  }
  private String folderMutants;
  public static int counter=0;
  public static int numberoperationargument=1;
  public static int numberinitialerror;
  public static boolean checkcollection=false;
  public static String BestSolutionLocation;
  public static boolean checkfilter=false;
  public static boolean checksequence=false;
  public static boolean checkoperationcall=false;
  public static boolean checkiteration=false;
  public static String inputfaultytransformation;
  public static String combinedfaultytransformations;
  private EMFModel atlModel;
  public static PrintWriter writer;
  public static int fitness2=0;
  public static int fitness3=0;
  public static int indexmodeltransformation=1;
  public static List<Integer> faultrule = new ArrayList<Integer>();
  public static List<Integer> rulefilter = new ArrayList<Integer>();
  public static List<Integer> finalrule = new ArrayList<Integer>();
  public static List<Integer> faultlocation = new ArrayList<Integer>();
  public static List<Integer> errorrule = new ArrayList<Integer>();
  public static List<Integer> listoutpatternmodify = new ArrayList<Integer>();
  public static ArrayList<Integer> startrule = new ArrayList<Integer>();
  public static ArrayList<Integer> finishedrule = new ArrayList<Integer>();
  
  public static String statemutcrossinitial="notmutation";
  public static int numop=0;
  public static int counterdelet=0;
  public static int countfilter=0;
  public static boolean startsituation=false;
  public static int fixedgeneration=-1;
  public static ArrayList<Integer> listfilterapplied = new ArrayList<Integer>();
  public static List<String> argumentlist = new ArrayList<String>();
  public static List<String> oclkindlineattr = new ArrayList<String>();
  public static List<Integer> deletlist = new ArrayList<Integer>();
  public static List<EClassifier> classifierliast = new ArrayList<EClassifier >();
  public static List<List<String>> originalmetamodellist = new ArrayList<List<String>>();
  public static List<List<String>> listattrhelper = new ArrayList<List<String>>();
  public static List<List<Integer>> linehelper = new ArrayList<List<Integer>>();
  public static List<String> alllistnavigationsplitdot = new ArrayList<String>();
  public FileWriter csvWriterpareto;
  /**   
   * Runs the NSGA-II algorithm.
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws JMException 
   */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
	  
    
    Settingpostprocessing s=new  Settingpostprocessing();
    
    QualityIndicator indicators; // QualityIndicator object
    int requiredEvaluations; // Use in the example of use of the
    // indicators object (see below)
       requiredEvaluations = 0;
     Solution newSolution;
     Setting setting=new Setting();
      LaunchATLHelper atlLauncher = new LaunchATLHelper(setting.gettargetmetamodel(), setting.getsourcemetamodel(), "Packageinput.xmi", folderMutants, folderMutants, folderMutants);
      CoSolution.check=0;
      newSolution = new Solution(problem_);
      try {
		problem_.evaluate(newSolution,this.csvWriterpareto,atlLauncher);
	} catch (JMException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     
      problem_.evaluateConstraints(newSolution);
       
   return null;
     
  } // execute
  
  
  public List<EPackage> retPackMM(Resource resourceMM)
{
	ResourceSet resourceSet=resourceMM.getResourceSet();
	List<EPackage> metamodel = new ArrayList<EPackage>();
	for (EObject obj : resourceMM.getContents()) {
		if (obj instanceof EPackage) {
			EPackage.Registry.INSTANCE.put		(((EPackage)obj).getNsURI(), ((EPackage)obj).getEFactoryInstance().getEPackage());
			resourceSet.getPackageRegistry().put(((EPackage)obj).getNsURI(), ((EPackage)obj).getEFactoryInstance().getEPackage());
			metamodel.add((EPackage)obj);
		}
	}
	return metamodel;
}
public Resource retPackResouceMM(String MMPath)
	 {	 	
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		URI fileURI = URI.createFileURI(MMPath);//ecore.getFullPath().toOSString());		
		Resource resource = resourceSet.getResource(fileURI, true);	
		return resource;
	 }
private void deleteDirectory (String directory, boolean recursive) {
		File folder = new File(directory);
		if (folder.exists())
			for (File file : folder.listFiles()) {				
				if (file.isDirectory()) deleteDirectory(file.getPath(), recursive);
				file.delete();
			}
		folder.delete();
	}
	
	/**
	 * It creates a directory.
	 * @param folder name of directory
	 */
	private void createDirectory (String directory) {
		File folder = new File(directory);
		while (!folder.exists()) 
			folder.mkdir();
	}
	private EMFModel loadTransformationModel (String atlTransformationFile) throws ATLCoreException {
		ModelFactory      modelFactory = new EMFModelFactory();
		
		EMFReferenceModel atlMetamodel = (EMFReferenceModel)modelFactory.getBuiltInResource("ATL.ecore");
		AtlParser         atlParser    = new AtlParser();		
		EMFModel          atlModel     = (EMFModel)modelFactory.newModel(atlMetamodel);
		atlParser.inject (atlModel, atlTransformationFile);	
		atlModel.setIsTarget(true);				
		return atlModel;
	}
	private /*static*/ long index = 1;
	private String getValidNameOfFile (String outputFolder) {
		String outputfile = null;
		String aux        = null;
		for (long i=index; outputfile==null; i++) {
			aux = File.separatorChar + "finalresult" + ".atl";
			if (!new File(outputFolder, aux).exists()) { 
				outputfile = outputFolder + aux;
				index = i;
			}
			else index = i;
		}
		return outputfile;
	}
	
	protected boolean save (EMFModel atlModel, String outputFolder) {
		try {
			// save atl file
			System.out.println("outputFolder");
			System.out.println(outputFolder);
			String atl_transformation = this.getValidNameOfFile(outputFolder);
			AtlParser atlParser       = new AtlParser();
			atlParser.extract(atlModel, atl_transformation);
			
			// compile transformation
			String asm_transformation = atl_transformation.replace(".atl", ".asm");
		} 
		catch (ATLCoreException e) {}
		
		return false;
	}
	

  public void Testeroperate () throws ATLCoreException  {
		
		
		String temporalFolder="C:/Users/varaminz/Downloads/Project/workspaceTEST34/anatlyzer.atl.tests.api/trafo2/zahra2";
		String trafo="C:/Users/varaminz/Downloads/Project/workspaceTEST34/anatlyzer.atl.tests.api/trafo/PNML2PetriNet.atl";
		this.folderMutants = temporalFolder + "mutants" + File.separator;
		this.atlModel = this.loadTransformationModel(trafo);
		 this.deleteDirectory(this.folderMutants, true);
		 this.createDirectory(this.folderMutants);
		 this.save(this.atlModel, this.folderMutants);
			
		
		
	}
	
} // NSGA-II
