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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFReferenceModel;
import org.eclipse.m2m.atl.engine.parser.AtlParser;
import cd.udem.fixingatlerror.BaseTest;
import cd.udem.fixingatlerror.Class2Rel;
import cd.udem.fixingatlerror.CoSolutionThreeStep;
import cd.udem.fixingatlerror.Setting;
import jmetal.core.*;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Distance;
import jmetal.util.DistanceSocialDiversity;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.comparators.CrowdingComparator;
import jmetal.util.comparators.DiversityComparator;
import produce.output.xmimodel.LaunchATLHelper;
import transML.exceptions.transException;
import witness.generator.MetaModel;

/** 
 *  Implementation of NSGA-II.
 *  This implementation of NSGA-II makes use of a QualityIndicator object
 *  to obtained the convergence speed of the algorithm. This version is used
 *  in the paper:
 *     A.J. Nebro, J.J. Durillo, C.A. Coello Coello, F. Luna, E. Alba 
 *     "A Study of Convergence Speed in Multi-Objective Metaheuristics." 
 *     To be presented in: PPSN'08. Dortmund. September 2008.
 */

public class NSGAIIThreeStep extends Algorithm {
  /**
   * Constructor
   * @param problem Problem to solve
   */
  public NSGAIIThreeStep(Problem problem) {
    super (problem) ;
  } // NSGAIIZA
  public void setString(String str){
      this.statemutcrossinitial = str;
  }
  
  public static ArrayList<ArrayList<String>> inheritClass = new ArrayList<ArrayList<String>>();
  public static ArrayList<ArrayList<String>> subcalss_supperclass = new ArrayList<ArrayList<String>>();
  public static ArrayList<String> inelementletter = new ArrayList<String>();
  public static ArrayList<Integer> preconditionline = new ArrayList<Integer>();
  public static ArrayList<Double> probability_rule = new ArrayList<Double>();
  public static ArrayList<Double> probability_In_Pattern = new ArrayList<Double>();
  public static ArrayList<String> mandatorycreationoperation = new ArrayList<String>();
  public static ArrayList<Integer> mandatorycreationlocation = new ArrayList<Integer>();
  public static ArrayList<Integer> model_index = new ArrayList<Integer>();
  public static ArrayList<String> mandatorydeletionattribute = new ArrayList<String>();
  public static ArrayList<Integer> mandatorydeletionlocation = new ArrayList<Integer>();
  public static ArrayList<String> mandatoryoutpatternoperation = new ArrayList<String>();
  public static ArrayList<Integer> mandatoryoutpatternlocation = new ArrayList<Integer>();
  public static ArrayList<String> totallistattribute = new ArrayList<String>();
  public static ArrayList<Integer> ocliskineoflocation = new ArrayList<Integer>();
  public static ArrayList<String> listnavigationtype = new ArrayList<String>();	
  public static ArrayList<String> oclletter = new ArrayList<String>();
  public static ArrayList<String> Diffsubset0 = new ArrayList<String>();	
  public static ArrayList<String> Diffsubset1 = new ArrayList<String>();
  public static ArrayList<String> Diffsubset2 = new ArrayList<String>();
  public static ArrayList<String> Diffsubset3 = new ArrayList<String>();
  public static double diversityMaxn;
  public static int temprule=-1;
  public static int foundIndex=-1;
  public static int indexiteration=50000;
  public static int numberincorrectatl=0;
  public static Solution retainedSolution=new Solution();
  public static ArrayList<Integer> locationfilter = new ArrayList<Integer>();
  public static ArrayList<String> wholelineattributerefinement = new ArrayList<String>();
  public static ArrayList<String> wholelineattributecollection = new ArrayList<String>();
  public static ArrayList<String> wholelineattributerefinementindex = new ArrayList<String>();
  public static ArrayList<Integer> wholeattributargumentlocation = new ArrayList<Integer>();
  public static ArrayList<ArrayList<String>> Twodimensionattributelist = new ArrayList<ArrayList<String>>();
  public static ArrayList<ArrayList<ArrayList<String>>> ClassNameAttributType = new ArrayList<ArrayList<ArrayList<String>>>();
  public static ArrayList<ArrayList<ArrayList<String>>> ClassNameAttributTypeTarget = new ArrayList<ArrayList<ArrayList<String>>>();
  public static ArrayList<Integer> locationfrom = new ArrayList<Integer>();	
  public static ArrayList<Integer> parameterlocation = new ArrayList<Integer>();
  public static ArrayList<Integer> iterationcall = new ArrayList<Integer>();
  public static ArrayList<Integer> qumecall = new ArrayList<Integer>();
  public static ArrayList<Integer> emptycollectionlocation = new ArrayList<Integer>();
  public static ArrayList<Integer> preconditionlocation = new ArrayList<Integer>();
  public static ArrayList<String> inpatternstringlocation = new ArrayList<String>();
  public static ArrayList<Integer> inpatternhasfilter = new ArrayList<Integer>();
  public static ArrayList<Integer> lazyrulelocation = new ArrayList<Integer>();
  public static List<List<String>> listattrhelper = new ArrayList<List<String>>();
  public static ArrayList<String> Helpername = new ArrayList<String>();
  public static ArrayList<String> classnamestring = new ArrayList<String>();
  public static ArrayList<String> abstractclassname = new ArrayList<String>();
  public static ArrayList<String> filterasstring = new ArrayList<String>();
  public static ArrayList<String> abstractsourceclassname = new ArrayList<String>();
  public static ArrayList<String> classnamestringtarget = new ArrayList<String>();
  public static ArrayList<Integer> classnamestartpointtarget = new ArrayList<Integer>();
  public static ArrayList<Integer> classnamelengthtarget = new ArrayList<Integer>();
  public static List<EStructuralFeature> listinheritattributesourcemetamodeltarget = new ArrayList<EStructuralFeature>();
  public static ArrayList<String> listnavigationtypeinheritattrtarget = new ArrayList<String>();
  public static ArrayList<Integer> classnamestartpoint = new ArrayList<Integer>();
  public static ArrayList<Integer> classnamelength = new ArrayList<Integer>();
  public static List<EStructuralFeature> listsourcemetamodel = new ArrayList<EStructuralFeature>();
  public static List<EStructuralFeature> listinheritattributesourcemetamodel = new ArrayList<EStructuralFeature>();
  public static List<Integer> listmandatoryfeaturelocation = new ArrayList<Integer>();
  public static boolean postprocessing=false;
  public static List<Integer> forbiddenoperations;
  public static int counter=0;
  public static int numberinitialerror;
  public static boolean checkcollection=false;
  public static boolean checkfilter=false;
  public static boolean checksequence=false;
  public static boolean checkoperationcall=false;
  public static boolean checkiteration=false;
  public static String inputfaultytransformation;
  public static String combinedfaultytransformations;
  public static PrintWriter writer;
  public static boolean array_status=false;
  public static int fitness2=0;
  public static int fitness3=0;
  public static int indexmodeltransformation=1;
  public static List<Integer> faultrule = new ArrayList<Integer>();
  public static List<Integer> finalrule = new ArrayList<Integer>();
  public static List<Integer> faultlocation = new ArrayList<Integer>();
  public static List<Integer> errorrule = new ArrayList<Integer>();
  public static List<Integer> listoutpatternmodify = new ArrayList<Integer>();
  public static String statemutcrossinitial="notmutation";
  public static String step="step5ostende4"; // must be equal with Config.txt
  public static String startingroot="Tmp"; 
  public static int numop=0;
  public static int counterdelet=0;
  public static int countfilter=0;
  public static boolean startsituation=false;
  public static int fixedgeneration=-1;
  public static ArrayList<Integer> listfilterapplied = new ArrayList<Integer>();
  public static List<String> argumentlist = new ArrayList<String>();
  public static List<Integer> deletlist = new ArrayList<Integer>();
  public static List<EClassifier> classifierliast = new ArrayList<EClassifier >();
  public FileWriter csvWriterpareto;
  private String totalstrrule=null;
  private boolean closeparantez=false;
  private ArrayList<String> wholelineattribute = new ArrayList<String>();
  private int indexlength=-1;
  private boolean checkingto=false;
  private boolean arrivefilter=false;
  private int indexfrom=-1;
  private boolean reachend=false;
  private ArrayList<Integer> endbinding = new ArrayList<Integer>();
  private ArrayList<ArrayList<String>> rightsideattr = new ArrayList<ArrayList<String>>();
  private boolean fleshcondition=false;
  private boolean untilto=false;
  private boolean chechfrom=false;
  private boolean precondition2=false;
  private boolean arrivingfilter=false;
  private ArrayList<Integer> inpattern = new ArrayList<Integer>();
  private boolean precondition=false;
  private boolean insidehelper=false;
  private int idrule=-1;
  private int collefthandside=0;
  private int lastline=0;
  private ArrayList<Integer> listassequence = new ArrayList<Integer>();
  private ArrayList<Integer> listlazy = new ArrayList<Integer>();
  private int indexhelper=0;
  private int linecount = 0;
  private String line; 
  private boolean startrule=false;
  private int linerepeated=0;
  
  /**   
   * Runs the NSGA-II algorithm.
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws JMException 
   */
  public SolutionSet execute() throws JMException, ClassNotFoundException, Exception,NullPointerException  {
	  
    int populationSize;
    int maxEvaluations;
    int evaluations;
    Setting s=new  Setting();
    String  soldir1=NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/trafo/semanticfaulty/atl"+Class2Rel.atlindex+"/diversity"+Class2Rel.numberdiversity; 
    deleteDirectory(soldir1,true);
    File dir11=new File(soldir1);
    	if (!dir11.exists())
    	   dir11.mkdir();   
    QualityIndicator indicators; // QualityIndicator object
    int requiredEvaluations; // Use in the example of use of the
    // indicators object (see below)
    try {
		writer = new PrintWriter(soldir1+"/file-operatin.txt", "UTF-8");
		try {
			this.csvWriterpareto= new FileWriter(soldir1+"/paretosize.csv");
				} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    SolutionSet population;
    SolutionSet offspringPopulation;
    SolutionSet union;

    Operator mutationOperator;
    Operator crossoverOperator;
    Operator selectionOperator;

    Distance distance = new Distance();
   // DistanceSocialDiversity distance = new DistanceSocialDiversity(); //step 4
    //Read the parameters
    populationSize = ((Integer) getInputParameter("populationSize")).intValue();
    maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
    indicators = (QualityIndicator) getInputParameter("indicators");

    //Initialize the variables
    population = new SolutionSet(populationSize);
    evaluations = 0;
    requiredEvaluations = 0;
    //Read the operators
    mutationOperator = operators_.get("mutation");
    crossoverOperator = operators_.get("crossover");
    selectionOperator = operators_.get("selection");
    // Create the initial solutionSet
    Solution newSolution;
    calculatelistsourcemetamodel();
	calculatelisttargetmetamodel();
 	ArrayList<Integer> list = new ArrayList<Integer>();
    CoSolutionThreeStep cocou=new CoSolutionThreeStep();
    InitializeinputParameters();
    ArrayList<ArrayList<String>> listproperty=cocou.getOp().listpropertyname;
    ArrayList<ArrayList<Integer>> listpropertylocation=cocou.getOp().listpropertynamelocation;
    FindFaultyRule(listproperty,listpropertylocation); //hazf kon
    probability_each_rule(s,list);
    probability_In_pattern_Modification();
    deleteDirectory (s.getpath()+"/resultformodeltransformation"+NSGAIIThreeStep.indexmodeltransformation+"/solution", false);
    CoSolutionThreeStep.check=0;
    Setting setting=new Setting();
    LaunchATLHelper atlLauncher = new LaunchATLHelper(setting.gettargetmetamodel(), setting.getsourcemetamodel(), s.getthirdinput()+".xmi",s.getfourthinput()+".xmi",s.getfirstinput()+".xmi",s.getsecondinput()+".xmi");
    model_index.clear();	
    model_index = new ArrayList<Integer>();
//	model_index=compare2();
    model_index.add(0);
    model_index.add(1);
    model_index.add(2);
    model_index.add(3);
    int i=0;
    while(i<s.getpopulationsize()) {
      NSGAIIThreeStep.counter=NSGAIIThreeStep.counter+1;
      newSolution = new Solution(problem_);
     try {
      problem_.evaluate(newSolution,this.csvWriterpareto,atlLauncher);
      problem_.evaluateConstraints(newSolution);   
      i++;
      evaluations++;
      population.add(newSolution);
     }catch(Exception exception){
    	 NSGAIIThreeStep.counter=NSGAIIThreeStep.counter-1;
    	 System.out.println("IllegalArgumentException11");
     }  
    } 
     boolean correctSolution = false;
     maxEvaluations=s.getmaxevaluation();
     while (evaluations < maxEvaluations) {
      // Create the offSpring solutionSet      
    	 offspringPopulation = new SolutionSet(populationSize);
    	 Solution[] parents = new Solution[2];
    	 int id_pop_size=-1;
    	 while ( id_pop_size < (populationSize)) {
    	      if (evaluations < maxEvaluations) {
    	    	  //obtain parents
    	    	  parents[0] = (Solution) selectionOperator.execute(population);
    	    	  parents[1] = (Solution) selectionOperator.execute(population);
    	    	  writer.println("choosefrompopulation-firstparent");
    	    	  writer.println(parents[0].getoperations());
    	    	  writer.println(parents[0].getindex());
    	    	  writer.flush();
    	    	  writer.println("choosefrompopulation-secondparent");
    	    	  writer.println(parents[1].getoperations());
    	    	  writer.println(parents[1].getindex());
    	    	  writer.flush();   
    	    	  Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
    	    	  writer.println("aftercrossover");   
    	    	  writer.println(offSpring[0].getoperations());
    	    	  writer.println(offSpring[1].getoperations());
    	    	  writer.flush();
    	    	  mutationOperator.execute(offSpring[0]);
    	    	  writer.println("aftermutation");
    	    	  mutationOperator.execute(offSpring[1]);
    	    	  NSGAIIThreeStep.statemutcrossinitial="mutation";
    	    	  Class2Rel.checkoffspring=1;
          
    	    	  try {
    	    		  problem_.evaluate(offSpring[0],this.csvWriterpareto, atlLauncher);
    	    		  problem_.evaluateConstraints(offSpring[0]);
    	    		  NSGAIIThreeStep.counter=NSGAIIThreeStep.counter+1;
    	    		  offspringPopulation.add(offSpring[0]);
    	    		  id_pop_size++;
    	    		  evaluations++;
    	    	  }
    	    	  catch (IllegalArgumentException exception) {
    	    		  System.out.println("IllegalArgumentException2");
    	    	  }
    	    	if( id_pop_size==populationSize-1)
    	    		break;
    	    		writer.println("offSpring[0].getoperations()");
    	    		writer.println(offSpring[0].getindex());
    	    		Class2Rel.checkoffspring=1;
    	    		try {
    	    			problem_.evaluate(offSpring[1],this.csvWriterpareto, atlLauncher);
    	    			problem_.evaluateConstraints(offSpring[1]);
    	    			NSGAIIThreeStep.counter=NSGAIIThreeStep.counter+1;
    	    			offspringPopulation.add(offSpring[1]);
    	    			id_pop_size++;
    	    			evaluations++;
    	    		}
    	    		catch (IllegalArgumentException exception) {
    	    			System.out.println("IllegalArgumentException2");
    	    		}
    	    		writer.println("offSpring[1].getoperations()");
    	    		writer.println(offSpring[1].getindex());
    	    		writer.flush();
    	    		if( id_pop_size==populationSize-1)
    	    		break;
             } // if                            
      } // for
      // Create the solutionSet union of solutionSet and offSpring
    	 union = ((SolutionSet) population).union(offspringPopulation);
    	 for (int indexSol = 0; indexSol < union.size(); indexSol++) {
    	  
    		 if (union.get(indexSol).getfirstfitness() == 0.0) {
    		  correctSolution = true;
    		  retainedSolution = union.get(indexSol);
    	  }
    	 }
      
    	 if (correctSolution) {
    		 indexiteration=evaluations;
    		 evaluations = maxEvaluations;
    	  } 

    	// distance2.SocialDiversityAssignment(union, problem_.getNumberOfObjectives()); 
    	 // Ranking the union
    	 Ranking ranking = new Ranking(union,this.csvWriterpareto);
    	 int remain = populationSize;
    	 int index = 0;
    	 SolutionSet front = null;
    	 population.clear();
    	 // Obtain the next front
    	 front = ranking.getSubfront(index);
    	 writer.println("ranking");
    	 writer.println(front.size());
    	 for (int k = 0; k < front.size(); k++) {
    		 writer.println(front.get(k));  
    		 writer.println(front.get(k).getindex());  
    		 writer.println(front.get(k).getsecondfitness());
    		 
    		 writer.flush();
    	  if(front.get(k).getfirstfitness()==0.0)
    	  {
    		  checkiteration=true;
    	      writer.println("Solution Found");
    	  }
      }

      while ((remain > 0) && (remain >= front.size())) {
        //Assign crowding distance to individuals
         distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
    	//distance.SocialDiversityAssignment(front, problem_.getNumberOfObjectives());
    	 
    	  for (int k = 0; k < front.size(); k++) {
          population.add(front.get(k));
        } // for

        //Decrement remain
        remain = remain - front.size();
        //Obtain the next front
        index++;
        if (remain > 0) {
          front = ranking.getSubfront(index);
        } // if        
      } // while

      // Remain is less than front(index).size, insert only the best one
      if (remain > 0) {  // front contains individuals to insert                        
           distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
    	  //distance.SocialDiversityAssignment(front, problem_.getNumberOfObjectives());
    	  front.sort(new CrowdingComparator());
         for (int k = 0; k < remain; k++) {
          population.add(front.get(k));
        } // for

        if(!population.contains(retainedSolution) && correctSolution==true) {
            
           writer.println("Solution found");
           writer.println(retainedSolution.getindex());
           foundIndex=retainedSolution.getindex();
           writer.println(retainedSolution.getfirstfitness());
           writer.println(population.size());
           population.add(retainedSolution);
           writer.println(population.size());
           writer.flush();

   }

        remain = 0;
  } // if                               

      // This piece of code shows how to use the indicator object into the code
      // of NSGA-II. In particular, it finds the number of evaluations required
      // by the algorithm to obtain a Pareto front with a hypervolume higher
      // than the hypervolume of the true Pareto front.
      if ((indicators != null) &&
          (requiredEvaluations == 0)) {
        double HV = indicators.getHypervolume(population);
        if (HV >= (0.98 * indicators.getTrueParetoFrontHypervolume())) {
          requiredEvaluations = evaluations;
        } // if
      } // if
//      System.out.println("after crossover");
      writer.flush();
      
    } // while
    
   
    // Return as output parameter the required evaluations
    setOutputParameter("evaluations", requiredEvaluations);
    // Return the first non-dominated front
    Ranking ranking = new Ranking(population,this.csvWriterpareto);
    ranking.getSubfront(0).printFeasibleFUN("FUN_NSGAII") ;
    writer.println("subfront");
    writer.println(ranking.getSubfront(0).size());
    writer.close();
    try {
		this.csvWriterpareto.flush();
		 this.csvWriterpareto.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    writer.println("condition");
    writer.println(correctSolution);	
    writer.println(ranking.getSubfront(0).contains(retainedSolution));
    if(!ranking.getSubfront(0).contains(retainedSolution) && correctSolution==true) {
    	
    	writer.println("ppp22222222222222222");
    	writer.println(retainedSolution.getindex());
    	writer.println(retainedSolution.getfirstfitness());
    	ranking.getSubfront(0).add(retainedSolution);
    	
    	}
    
    writer.flush();
    return ranking.getSubfront(0);
  } // execute
  
  
  public ArrayList<Integer> compare2() {
		
	    ArrayList<Integer> modelindex = new ArrayList<Integer>();
		// register globally the Ecore Resource Factory to the ".ecore" extension
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		ResourceSet rs = new ResourceSetImpl();
		final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
		rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
		Setting s=new Setting();
		URI modelURI = URI.createFileURI(s.gettargetmetamodel());
		Resource r = rs.getResource(modelURI, true);
		EObject eObject = r.getContents().get(0);
		if (eObject instanceof EPackage) {
			EPackage p = (EPackage)eObject;
			rs.getPackageRegistry().put(p.getNsURI(), p);
		}
			
		// Register the factory
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());	
		int differenceCount=CompareTwoModels(
				NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/trafo/inputatl"+"/target3"+".xmi",
				NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/trafo/inputatl/correctoutput3.xmi");
		modelindex.add(3);
		differenceCount=CompareTwoModels(
				NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/trafo/inputatl"+"/target2"+".xmi",
				NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/trafo/inputatl/correctoutput2.xmi");
		modelindex.add(2);
	
		differenceCount=CompareTwoModels(
				NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/trafo/inputatl"+"/target1"+".xmi",
				NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/trafo/inputatl/correctoutput1.xmi");
		modelindex.add(1);
		
		differenceCount=CompareTwoModels(
				NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/trafo/inputatl"+"/target0"+".xmi",
				NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/trafo/inputatl/correctoutput0.xmi");
		modelindex.add(0);
		
		return modelindex;
		
	}
	
private int CompareTwoModels(String producedmodel, String expectedmodel) {
			// TODO Auto-generated method stub
			// Load the two input models
			ResourceSet resourceSet1 = new ResourceSetImpl();
			ResourceSet resourceSet2 = new ResourceSetImpl();
			URI uri1 = URI.createFileURI(producedmodel);
			URI uri2 = URI.createFileURI(expectedmodel);
			resourceSet1.getResource(uri1, true);
			resourceSet2.getResource(uri2, true);	    
			// Configure EMF Compare
			EMFCompare comparator = EMFCompare.builder().build();
			// Compare the two models
			IComparisonScope scope = new DefaultComparisonScope(resourceSet1, resourceSet2, null);
			Comparison comparison = comparator.compare(scope);
			EList<Diff> differences = comparison.getDifferences();
			
			
	return differences.size();
}
private void probability_In_pattern_Modification() {
	// TODO Auto-generated method stub
	  
	  probability_In_Pattern.add(probability_rule.get(0) );
	  for(int i=1;i<probability_rule.size()-1;i++) {
		  probability_In_Pattern.add( probability_In_Pattern.get(i-1) +probability_rule.get(i)); 
		  
	  }
	  probability_In_Pattern.add((double) 1); 
	  
	
}
private void probability_each_rule(Setting s, ArrayList<Integer>  list) {
	// TODO Auto-generated method stub
	  int probability_sum=list.size() * s.frequence+ (NSGAIIThreeStep.faultrule.size()-list.size());
		System.out.println(s.frequence);
		for(int i=0;i<NSGAIIThreeStep.faultrule.size();i++) {
			if( list.contains(i)) {
				probability_rule.add( (double) ((double) s.frequence/(double) probability_sum));
				
			}
			else
				probability_rule.add((double) ((double) 1/(double) probability_sum));
				
		}
	
}
private void InitializeinputParameters() {
	// TODO Auto-generated method stub
	  NSGAIIThreeStep.listattrhelper.clear();
	  NSGAIIThreeStep.listattrhelper = new ArrayList<List<String>>();
	 
	}
  
   
private ArrayList<Integer> buildForbiddenOperation() {
		ArrayList<Integer> forbiddenoperations = new ArrayList<Integer>();
		
		if (!NSGAIIThreeStep.checkcollection )
			forbiddenoperations.add(7);
		
		if (!NSGAIIThreeStep.checkfilter )
			forbiddenoperations.add(5);
		
		if (!NSGAIIThreeStep.checksequence )
			forbiddenoperations.add(4);
		
		if (!NSGAIIThreeStep.checkiteration )
			forbiddenoperations.add(10);
		
		if (!NSGAIIThreeStep.checkoperationcall )
			forbiddenoperations.add(2);
		
		return forbiddenoperations;
		
	}
  
  private void FindFaultyRule(ArrayList<ArrayList<String>> listproperty, ArrayList<ArrayList<Integer>> listpropertylocation) {
	  	Setting s = null;
	  	if(!NSGAIIThreeStep.postprocessing)
		  s = new Setting();
		if(!NSGAIIThreeStep.startsituation)
		{
		String stringSearch = "rule";
		String stringSearch14 = "lazy";
		String stringSearch3 = "Sequence(";
		String stringSearch9 = "Sequence {}";
		String stringSearch8 = "asSequence";
		List<String> stringSearch4    = Arrays.asList ( new String[]{ ".oclIsKindOf",".oclAsType",".oclIsTypeOf"} ); // delete .startsWith
		String stringSearch5 = "from";
		String stringSearch6 = "to";
		String stringSearch7 = "using";
		String stringSearch10 = "->";
		String stringSearch19 = "<-";
		String stringSearch11 = "thisModule";
		String stringSearch13 = "'";
		String stringSearch15 = "!";
		String stringSearch18 = "|";
		String stringSearch16 = "def:";
		String stringSearch17 = ".";
		
		
		List<String> iterationlist    = Arrays.asList ( new String[]{"select", "collect", "reject", "forAll", "isUnique", "exists"} );
		List<String> temporarylist    = Arrays.asList ( new String[]{"isEmpty", "notEmpty", "includes", "excludes", "includesAll", "excludesAll","size", "sum", "count",
				"indexOf","union", "intersection","first", "last", "asBag", "asSequence", "asSet", "flatten", "append", "prepend", "including", "excluding"} );
		List<String> collectionempty    = Arrays.asList ( new String[]{"isEmpty()", "notEmpty()", "includes()", "excludes()", "includesAll()", "excludesAll()","size()", "sum()", "count()",
				"indexOf()","union()", "intersection()","first()", "last()", "asBag()", "asSequence()", "asSet()", "flatten()", "append()", "prepend()", "including()", "excluding()"} );
		List<String> operationcall    = Arrays.asList ( new String[]{"oclIsKindOf", "oclIsTypeOf", "startsWith", "endsWith", "concat", "trim", "max","min", "exp", "log", "floor","size", "toInteger"
				 , "toReal", "indexOf","lastIndexOf", "abs"} );
		
		BufferedReader bf = null;
		if(!NSGAIIThreeStep.postprocessing) {
		try {
			bf = new BufferedReader(new FileReader(s.getinputfaultytransformation()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		else {
			
			try {
				bf = new BufferedReader(new FileReader(NSGAIIThreeStep.inputfaultytransformation));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
       
        for(int i=0;i<10;i++) // 10= number of rule
        	NSGAIIThreeStep.filterasstring.add("empty");
        try {
			while (( line = bf.readLine()) != null)
			{
			    linecount++;
			    int indexfound = line.indexOf(stringSearch);
			    int indexfound3 = line.indexOf(stringSearch3);
				int colon = line.indexOf(stringSearch5);
				int indexfound5 = line.indexOf(stringSearch6);
				int indexfound7 = line.indexOf(stringSearch7);
				int indexfound8 = line.indexOf(stringSearch8);
				int indexfound9 = line.indexOf(stringSearch9);
				int indexfound10 = line.indexOf(stringSearch10);
				int indexfound11 = line.indexOf(stringSearch11);
				int indexfound13 = line.indexOf(stringSearch13);
				int indexfound14 = line.indexOf(stringSearch14);
				int indexfound15 = line.indexOf(stringSearch15);
				int indexfound16 = line.indexOf(stringSearch16);
				int indexfound17 = line.indexOf(stringSearch17);
				int indexfound18 = line.indexOf(stringSearch18);
				int indexfound19 = line.indexOf(stringSearch19);
				
				StartTransformationRule(
						indexfound19, indexfound10, listpropertylocation, s, indexfound19, indexfound10);
				PartToTransformation(indexfound10);
				FindOCLLetter(temporarylist,operationcall,
						collectionempty, indexfound18, iterationlist);
				PartFromTransformation( colon);
				FindInpattern(indexfound, indexfound15, indexfound7, indexfound5 , s );
				EndRule(listproperty, indexfound3, indexfound8, indexfound9, indexfound11,
						indexfound13, indexfound14, stringSearch4);
				HelperName(indexfound17, indexfound16);
							}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        InitializeList(bf);
       		 
	}
    System.out.println(NSGAIIThreeStep.wholelineattributerefinement);		
}
  private void InitializeList(BufferedReader bf) {
	// TODO Auto-generated method stub
	
	  if(!NSGAIIThreeStep.postprocessing)
      {
      		for(int i=1;i<NSGAIIThreeStep.faultrule.size();i++)
      			NSGAIIThreeStep.finalrule.add(NSGAIIThreeStep.faultrule.get(i)-2);
      			NSGAIIThreeStep.finalrule.add(lastline);
      			for(int u=0;u< wholelineattribute.size();u=u+1)
      			{
      				String[] parts2 = wholelineattribute.get(u).split("<-|\\+|\\.|\\'|\\;|\\->|\\;|\\ |\\->|\\s+|\\,|\\(|\\)|\\!|\\s+|\\}|reject|select|to|if|then|else|false|union|collect|flatten|endif| not "
      						);
      				String[] parts3 = wholelineattribute.get(u).split("<-|\\+|\\.|\\'|\\;|\\->|\\;|\\ |\\->|\\s+|\\,|\\(|\\)|\\!|\\s+|\\}|to|if|then|else|false|endif| not "
      						);
      
      				for(int u2=0;u2<parts3.length;u2++) {
      	
      					if(!parts3[u2].isEmpty() && !parts3[u2].equals("|") &&  !parts3[u2].equals("not"))
      					{
      						NSGAIIThreeStep.wholelineattributecollection.add( parts3[u2]);
      	
      					}
      				}
		
      				for(int u2=0;u2<parts2.length;u2++) {
      	
      					if(!parts2[u2].isEmpty() && !parts2[u2].equals("|") &&  !parts2[u2].equals("not"))
      					{
      						NSGAIIThreeStep.wholelineattributerefinement.add( parts2[u2]);
      	
      					}
      				}
      			} 
      		try {
      			bf.close();
      		} catch (IOException e) {
      			// TODO Auto-generated catch block
      			e.printStackTrace();
      		}
      		Collections.sort(NSGAIIThreeStep.faultlocation);
      		for(int i=0;i<NSGAIIThreeStep.faultlocation.size();i++)
      			for(int j=0;j<NSGAIIThreeStep.faultrule.size();j++)
      			{
      				if(NSGAIIThreeStep.faultlocation.get(i)>=NSGAIIThreeStep.faultrule.get(j) && NSGAIIThreeStep.faultlocation.get(i)<=NSGAIIThreeStep.finalrule.get(j))
      					NSGAIIThreeStep.errorrule.add(j);
      			}
		 
      		createclassifier(); 
      		for(int i=0;i<NSGAIIThreeStep.inpatternstringlocation.size();i++ )
      			if( (NSGAIIThreeStep.locationfilter.get(i)-NSGAIIThreeStep.locationfrom.get(i))>2)
					NSGAIIThreeStep.inpatternhasfilter.add(1);
      			else
      				NSGAIIThreeStep.inpatternhasfilter.add(0); 
		 
      		NSGAIIThreeStep.startsituation=true;
		
      		for (int i = 0; i < listlazy.size(); i++)
      			for (int j = 0; j < NSGAIIThreeStep.faultrule.size(); j++) {
      				if (listlazy.get(i) >= NSGAIIThreeStep.faultrule.get(j)
							&& listlazy.get(i) <= NSGAIIThreeStep.finalrule.get(j))
      					NSGAIIThreeStep.lazyrulelocation.add(j);

      			}
				 for (int i = 0; i < NSGAIIThreeStep.ocliskineoflocation.size(); i++) {
			 
					 for (int j = 0; j < NSGAIIThreeStep.faultrule.size(); j++) {
						 if (NSGAIIThreeStep.ocliskineoflocation.get(i) > NSGAIIThreeStep.faultrule.get(j)
							&& NSGAIIThreeStep.ocliskineoflocation.get(i) < NSGAIIThreeStep.finalrule.get(j) && !NSGAIIThreeStep.locationfilter.contains( NSGAIIThreeStep.ocliskineoflocation.get(i)))
						 {
							 NSGAIIThreeStep.checkcollection = true;
							 break;
						 }
					 }
				 }

				 NSGAIIThreeStep.forbiddenoperations = buildForbiddenOperation();
		}

}
private void HelperName(int indexfound17, int indexfound16) {
	// TODO Auto-generated method stub
	
	  if (indexfound16 > -1) {
			
			NSGAIIThreeStep.listattrhelper.add(new ArrayList<String>());
			String[] parts = line.split("def:");
			String[] subparts =parts[1].split(":");
			NSGAIIThreeStep.listattrhelper.get(indexhelper).add( subparts[0]);
			NSGAIIThreeStep.Helpername.add( subparts[0].trim());
			indexhelper=indexhelper+1;
			insidehelper=true;

		}
		
		if(indexfound17>-1 && insidehelper==true) 
		{
			 String[] parts = line.split("\\+|\\.|\\'|\\;|\\->|\\;|\\ |\\)|\\)->|\\s+");
			 for(int j=0;j<parts.length;j++)
			   if(!parts[j].equals(" "))	
				 {
					NSGAIIThreeStep.listattrhelper.get(indexhelper-1).add(parts[j] );	
					
				 }
		}
}
private void EndRule(ArrayList<ArrayList<String>> listproperty, int indexfound3, int indexfound8, int indexfound9, int indexfound11, int indexfound13, int indexfound14, List<String> stringSearch4) {
	// TODO Auto-generated method stub
	  
	  if(collefthandside<listproperty.size())
		    if(listproperty.get(collefthandside).size()==rightsideattr.size() 
		    		) {
		 
	        	rightsideattr = new ArrayList<ArrayList<String>>();
				collefthandside=collefthandside+1;
      	
	        }
	    if (line.contains("}")) 
	    {
	    		lastline=linecount;
	    		untilto=false;
	       
	      }

	    if (indexfound3 > -1 && startrule==true) {
	    	NSGAIIThreeStep.checksequence = true;
		}
	    if (indexfound8 > -1) {
			listassequence.add(linecount);

		}
	    if (indexfound9 > -1) {
			listassequence.add(linecount);

		}
	    if (line.indexOf(stringSearch4.get(0)) > -1  ||  line.indexOf(stringSearch4.get(1)) > -1 ||  line.indexOf(stringSearch4.get(2)) > -1) {
			NSGAIIThreeStep.ocliskineoflocation.add(linecount);
				}
		if (indexfound11 > -1) {
			NSGAIIThreeStep.parameterlocation.add(linecount);
		
		 }
		if (indexfound13 > -1) {
			NSGAIIThreeStep.qumecall.add(linecount);
			

		 }
		if (indexfound14 > -1) {
			listlazy.add(linecount);
		
		 }

}
private void FindInpattern(int indexfound, int indexfound15, int indexfound7, int indexfound5, Setting s) {
	// TODO Auto-generated method stub
	
	  if(indexfound15>-1 && precondition2==true && linecount-indexfrom==1 && startrule==true) {
			String[] parts = line.split("!");
			NSGAIIThreeStep.inpatternstringlocation.add(parts[1]);
			inpattern.add( linecount);	
		}
		 if(indexfound7>-1) {
			  precondition=true;
		 }
		
		 if(indexfound5 >-1) { // "to"
			precondition2=false;
			if(precondition==true && (line.trim().length()==2 || line.trim().length()==3)) {
				 NSGAIIThreeStep.locationfilter.add(NSGAIIThreeStep.locationfrom.get( NSGAIIThreeStep.locationfrom.size()-1) );	
			 }
			
			 if(precondition==false && (line.trim().length()==2 || line.trim().length()==3)) {
				 NSGAIIThreeStep.locationfilter.add( linecount);
				 untilto=false;
				 checkingto=true;
				 arrivingfilter=false;
				
			 }
			 
			 if(linecount-indexfrom>2 && precondition==true && line.length()==2)
				 NSGAIIThreeStep.preconditionlocation.add( linecount-2);
			
			}
	    if (indexfound > -1) 
	     {
	    	  String[] parts = line.split("\\s+");
	    	  AddRuleNametoArrayList(line,parts, s);
	    	  totalstrrule="";
	    	  startrule=true;
	    	  insidehelper=false;
	    	  precondition=false;
	          NSGAIIThreeStep.faultrule.add(linecount);
	          idrule=idrule+1;
	        
	    }
	    
}
private void PartFromTransformation(int colon) {
	// TODO Auto-generated method stub
	  
	  if(chechfrom==true && startrule==true ) {
			
			String[] part=line.split(":");
			if(!part[0].isEmpty())
			NSGAIIThreeStep.inelementletter.add(part[0].trim());
			chechfrom=false;	
		}
		// "from"
		if(colon>-1 && !line.contains("<-") && startrule==true) {
			
			 indexfrom=linecount;
			 precondition2=true;
			 checkingto=false;
			 chechfrom=true;
			 if(linecount>5)
			 NSGAIIThreeStep.locationfrom.add(linecount);
			 
		}
		// line haye toye filter o migire
		if(arrivingfilter==true && checkingto==false) {
			
			totalstrrule=totalstrrule+line;	
		}
		if(line.endsWith("(") && checkingto==false) {
			
			arrivingfilter=true;
		}
		
	
}
private void FindOCLLetter(List<String> temporarylist, List<String> operationcall,
		List<String> collectionempty, int indexfound18, List<String> iterationlist) {
	
	  if(indexfound18 > -1) {
			// when it has ->, should find ocl letter (line ke ocl dare va khode ocl letter, ) ->select(e|  oclletter:32,e
			String[] parts = line.split("->");
			if(parts.length>1) {
			String[] parts2 = parts[1].split("\\(");
			String[] parts3 = parts2[1].split("\\||\\s+");
			NSGAIIThreeStep.oclletter.add( Integer.toString(linecount));
			NSGAIIThreeStep.oclletter.add( parts3[0]);
			}
		}
		
		for(int i=0;i<temporarylist.size();i++) {
			if(line.indexOf(temporarylist.get(i))>-1 &&  fleshcondition==true) 
			{
				NSGAIIThreeStep.checkfilter = true;
					
			}	
		}
		fleshcondition=false;
		for(int i=0;i<operationcall.size();i++) {
			if(line.indexOf(operationcall.get(i))>-1 && startrule==true) 
			{
				NSGAIIThreeStep.checkoperationcall = true;	
			}
			
		}
		for(int i=0;i<collectionempty.size();i++) {
			if(line.indexOf(collectionempty.get(i))>-1 ) 
			{
				NSGAIIThreeStep.emptycollectionlocation.add( linecount);
					
			}
			
		}
		
		for(int i=0;i<iterationlist.size();i++) {
			if(line.indexOf(iterationlist.get(i))>-1 && startrule==true) 
			{
				//tables<-p.elems->select(e|  : 1) ->select... hazf kon 2)faghat elems begir
				String[] parts = line.split("->");
				String[] parts2 = parts[0].toString().split("\\.");
				if(parts2.length>1)
				NSGAIIThreeStep.iterationcall.add( linecount);
				NSGAIIThreeStep.checkiteration = true;
					
			}
			
		}
		
		
	}
private void PartToTransformation(int indexfound10) {
	// TODO Auto-generated method stub
	
	  if(line.contains(","))
		untilto=false;
		
	 if(untilto) {
			
			String str=wholelineattribute.get(wholelineattribute.size()-1 )+line;
			wholelineattribute.set(wholelineattribute.size()-1,Integer.toString(linecount) );
			UpdatePreviousstartandendlocation(linerepeated);
			indexlength=UpdateAttributeArgumentLocation(indexlength,linecount, line);
			wholelineattribute.add( str);
			
		}
		//"->" aval
			if (indexfound10 > -1 && startrule==true && reachend==false && untilto==false  ) { // && checkusing==false
			
				wholelineattribute.add(Integer.toString(linecount) );
				String[] parts3 = line.split("<-|->"); //|=
				wholelineattribute.add(parts3[1]);
				indexlength=UpdateAttributeArgumentLocation(indexlength,linecount, parts3[1]);
				if(line.contains("(") && !line.contains(")") &&  startrule==true) {
					 untilto=true;
					 linerepeated=linecount;
				 }
	
				fleshcondition=true;
		}
	
}
private void StartTransformationRule(int linecount, int linerepeated, ArrayList<ArrayList<Integer>> listpropertylocation, Setting s, int indexfound19, int indexfound10) {
	// TODO Auto-generated method stub
	  if(closeparantez==true && line.contains(")")) {
			
			 String str=wholelineattribute.get(wholelineattribute.size()-1 )+line;
			 wholelineattribute.set(wholelineattribute.size()-1,Integer.toString(linecount) );
			 UpdatePreviousstartandendlocation(linerepeated);
			 totalstrrule=totalstrrule+line;
			 indexlength=UpdateAttributeArgumentLocation(indexlength,linecount, line);
			 wholelineattribute.add(str);
			 closeparantez=false;
			
		}	
	  if( ( ( line.endsWith(" )") ||  line.endsWith(")")) && line.length()==1 )   && checkingto==false) 
		{
			arrivefilter=false;
		}
		
		if( arrivefilter) {
			
			wholelineattribute.add(Integer.toString(linecount) );
			wholelineattribute.add( line);
			indexlength=UpdateAttributeArgumentLocation(indexlength,linecount, line);
			NSGAIIThreeStep.filterasstring.set(idrule, line);
			if(!wholelineattribute.get(wholelineattribute.size()-1).contains(")") && wholelineattribute.get(wholelineattribute.size()-1).contains("("))
			{
				closeparantez=true;	
			}
						
		}	
		if(line.endsWith("(") && ( linecount-indexfrom )==2  && checkingto==false)
		{
			arrivefilter=true;
		}
		
		if(reachend) {
			
			String str=wholelineattribute.get(wholelineattribute.size()-1 )+line;
			wholelineattribute.set(wholelineattribute.size()-1,Integer.toString(linecount) );
			UpdatePreviousstartandendlocation(linerepeated);
			indexlength=UpdateAttributeArgumentLocation(indexlength,linecount, line);
			totalstrrule=totalstrrule+line;
			wholelineattribute.add( str);
			if(line.contains("}")) {
				
				reachend=false;
			}
					
		}
		
		if(line.startsWith("}")) {
			
			String[] parts2 = totalstrrule.split("<-|\\+|\\.|\\'|\\;|\\->|\\;|\\ |\\->|\\s+|\\,|\\(|\\)|\\!|\\s+|\\}|reject|select|union|collect|flatten|if|then|else|false|endif| not | or"
					);
			ArrayList<String> attrrule = new ArrayList<String>();
	        for(int u2=0;u2<parts2.length;u2++) {
	        	
	        	if(!parts2[u2].isEmpty() && !parts2[u2].equals("|") &&  !parts2[u2].equals("not"))
	        	{
	        		 attrrule.add( parts2[u2]);
	        	}
	       	}
			
	        NSGAIIThreeStep.Twodimensionattributelist.add( attrrule);
	   	 }
		
		if (indexfound19 > -1 && indexfound10 <= -1) {
			
			String[] parts = line.split("<-|\\->");
			String[] parts3 = line.split("<-");
			String[] parts2 = parts[1].split("<-|\\+|\\.|\\'|\\;|\\->|\\;|\\ |\\->|\\s+|\\,|\\(|\\)"
					+ "|\\!");
			rightsideattr.add(new ArrayList<String>());
			totalstrrule=totalstrrule+parts3[1];
			wholelineattribute.add(Integer.toString(linecount) );
			wholelineattribute.add(parts3[1]);
			indexlength=UpdateAttributeArgumentLocation(indexlength,linecount, parts3[1]);
			for(int y=0;y<listpropertylocation.size();y++) {
					int indexline=listpropertylocation.get(y).indexOf( linecount);
					if(indexline>=0) {	
					 endbinding=listpropertylocation.get(y);
					}
				}	
			if(endbinding.size()==1) {
					
					reachend=true;	
					linerepeated=linecount;	
				}
				
			 for(int j=0;j<parts2.length;j++)
			 {
				 if(!parts2[j].isEmpty() && !line.equals(" ") && !parts2[j].equals(s.getfirstecorename()) 
						 )	
					   {
						rightsideattr.get( rightsideattr.size()-1).add( parts2[j].trim());
						
					  }
			      }  
			 }
	   }
private void AddRuleNametoArrayList(String line,String[] parts, Setting s)
  {
	  boolean findextends=false;
	for(int j=0;j<parts.length;j++)
	{
		if( parts[j].equals("rule"))
			  s.setruleName((parts[j+1]));
		if( parts[j].equals("extends"))
		{
			  s.setextendsruleName((parts[j+1]));
			  findextends=true;
		}
		
	}
	if(findextends==false) {
		s.setextendsruleName("empty");
	}
  }
  private void UpdatePreviousstartandendlocation(int linerepeated) {
	// TODO Auto-generated method stub
	  
	  boolean findstartline=false;
		for(int u=0;u<NSGAIIThreeStep.wholeattributargumentlocation.size();u=u+3) {
			if(NSGAIIThreeStep.wholeattributargumentlocation.get(u)==linerepeated) {
			findstartline=true;	
			}
			if(findstartline) {
				NSGAIIThreeStep.wholeattributargumentlocation.set(u+1,NSGAIIThreeStep.wholeattributargumentlocation.get(u+1 )+1);
				NSGAIIThreeStep.wholeattributargumentlocation.set(u+2,NSGAIIThreeStep.wholeattributargumentlocation.get(u+2 )+1);
			}
			
		}
	
}
private int UpdateAttributeArgumentLocation(int indexlength, int linecount, String line) {
	// TODO Auto-generated method stub
	  NSGAIIThreeStep.wholeattributargumentlocation.add((linecount) );	
	  String[] parts2 = line.split("<-|\\+|\\.|\\'|\\;|\\->|\\;|\\ |\\->|\\s+|\\,|\\(|\\)|\\!|\\s+|\\}|reject|select|collect|union|flatten|to|if|then|else|false|endif| not "
				);
      int idreallength=0;
      for(int u2=0;u2<parts2.length;u2++) {
      		
      	if(!parts2[u2].isEmpty() && !parts2[u2].equals("|") &&  !parts2[u2].equals("not"))
      	{
      		idreallength=idreallength+1;
      	
      	}

      }
      NSGAIIThreeStep.wholeattributargumentlocation.add( indexlength+2 );
      NSGAIIThreeStep.wholeattributargumentlocation.add( idreallength+(NSGAIIThreeStep.wholeattributargumentlocation.get( NSGAIIThreeStep.wholeattributargumentlocation.size()-1))-1 );
      indexlength=(NSGAIIThreeStep.wholeattributargumentlocation.get( NSGAIIThreeStep.wholeattributargumentlocation.size()-1));
		
	return indexlength;
}

  private void calculatelisttargetmetamodel() {
		
	  	Setting s=new Setting();
		String MMRootPath2     = s.getsourcemetamodel();
		 MetaModel metatarget=null;
		 try {
			metatarget=new MetaModel(MMRootPath2);
		} catch (transException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 int yy=0;
		 int start=0;
		 int lenght=0;
		 ArrayList<String> tempsubclass_suerclass;
		 boolean firsttime=true;
		 List<EStructuralFeature> mainclass4 = new ArrayList<EStructuralFeature>();
		 NSGAIIThreeStep.ClassNameAttributTypeTarget = new ArrayList<ArrayList<ArrayList<String>>>();
		 for (EClassifier classifier : metatarget.getEClassifiers()) {
			 tempsubclass_suerclass = new ArrayList<String>();
				if (classifier instanceof EClass) {
					
					EClass child = ((EClass) classifier);
					tempsubclass_suerclass.add(child.getName());
					NSGAIIThreeStep.classnamestringtarget.add(child.getName());
					NSGAIIThreeStep.classnamestartpointtarget.add(start);
					System.out.println(child.getName());
					if(child.isAbstract())
						abstractclassname.add(child.getName());
					ArrayList<ArrayList<String>>  clasnameattrtype= new ArrayList<ArrayList<String>>();
								clasnameattrtype.add(new ArrayList<String>());
								clasnameattrtype.add(new ArrayList<String>());
								
					for (int  y=0;y<classifier.eContents().size();y++)
					{
						if (classifier.eContents().get(y) instanceof EAttribute
								||classifier.eContents().get(y) instanceof EReference ) {
							
							mainclass4.add( (EStructuralFeature) classifier.eContents().get(y));
							NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.add( (EStructuralFeature) classifier.eContents().get(y)); 
							NSGAIIThreeStep.listnavigationtypeinheritattrtarget.add(((EStructuralFeature) classifier.eContents().get(y)).getEType().getName());
							clasnameattrtype.get(clasnameattrtype.size()-2).add(((EStructuralFeature) classifier.eContents().get(y)).getName());
							clasnameattrtype.get(clasnameattrtype.size()-1).add(((EStructuralFeature) classifier.eContents().get(y)).getEType().getName());
							yy=yy+1;
							lenght=lenght+1;
							
						}
						
					}
				
					 for (int i=0 ;i< child.getEAllSuperTypes().size();i++) {
						
						 EClass classifier2=child.getEAllSuperTypes().get(i);
						
						 if(classifier2.getName()!=null) {
							 tempsubclass_suerclass.add(classifier2.getName());
						 for(int j=0;j< classifier2.eContents().size();j++) {
							 if (classifier2.eContents().get(j) instanceof EAttribute
										||classifier2.eContents().get(j) instanceof EReference ) {
								 
								 NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.add( (EStructuralFeature) classifier2.eContents().get(j));  
								 NSGAIIThreeStep.listnavigationtypeinheritattrtarget.add(((EStructuralFeature) classifier2.eContents().get(j)).getEType().getName());
								 clasnameattrtype.get(clasnameattrtype.size()-2).add(((EStructuralFeature) classifier2.eContents().get(j)).getName());
								 clasnameattrtype.get(clasnameattrtype.size()-1).add(((EStructuralFeature) classifier2.eContents().get(j)).getEType().getName());
								 lenght=lenght+1;
								 
							 }
							 
						 }
					   } 
				    }
					
					 for (EClassifier classifier2 : metatarget.getEClassifiers()) {
						 if (classifier2 instanceof EClass) {
							 EClass child2 = ((EClass) classifier2);
							 if(child.isSuperTypeOf(child2) && !child2.equals(child)) {
								 
								 tempsubclass_suerclass.add(child2.getName());			 
							 }
							 
						 }
						 
					 }
					 firsttime=FindAllSuperClassesOfClass(tempsubclass_suerclass, firsttime, s.subcalss_supperclasstarget);
					 NSGAIIThreeStep.ClassNameAttributTypeTarget.add( clasnameattrtype);
					 NSGAIIThreeStep.classnamelengthtarget.add(lenght);
					 start=start+lenght;
					 lenght=0;	
				}
		 	}
	}

private void calculatelistsourcemetamodel() {
	// TODO Auto-generated method stub
	
	Setting s=new Setting();
	String MMRootPath2     = s.gettargetmetamodel();
	 MetaModel metatarget=null;
	 try {
		metatarget=new MetaModel(MMRootPath2);
	} catch (transException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 int yy=0;
	 int start=0;
	 int lenght=0;
	 NSGAIIThreeStep.inheritClass = new ArrayList<ArrayList<String>>();
	 ArrayList<String> tempsubclass_suerclass;
	 boolean firsttime=true;
	 NSGAIIThreeStep.ClassNameAttributType = new ArrayList<ArrayList<ArrayList<String>>>();
	 for (EClassifier classifier : metatarget.getEClassifiers()) {
		 tempsubclass_suerclass = new ArrayList<String>();
			if (classifier instanceof EClass) {
				
				EClass child = ((EClass) classifier);
				NSGAIIThreeStep.classnamestring.add(child.getName());
				tempsubclass_suerclass.add(child.getName());
				ArrayList<ArrayList<String>>  clasnameattrtype= new ArrayList<ArrayList<String>>();
				NSGAIIThreeStep.classnamestartpoint.add(start);
				if(child.isAbstract())
					abstractsourceclassname.add(child.getName());
				clasnameattrtype.add(new ArrayList<String>());
				clasnameattrtype.add(new ArrayList<String>());
				for (int  y=0;y<classifier.eContents().size();y++)
				{		
					if (classifier.eContents().get(y) instanceof EAttribute
							||classifier.eContents().get(y) instanceof EReference ) {
						
						NSGAIIThreeStep.listsourcemetamodel.add( (EStructuralFeature) classifier.eContents().get(y));
						NSGAIIThreeStep.totallistattribute.add((NSGAIIThreeStep.listsourcemetamodel.get( NSGAIIThreeStep.listsourcemetamodel.size()-1).getName()));
						NSGAIIThreeStep.listinheritattributesourcemetamodel.add( (EStructuralFeature) classifier.eContents().get(y)); 
						NSGAIIThreeStep.listnavigationtype.add(((EStructuralFeature) classifier.eContents().get(y)).getEType().getName());
						clasnameattrtype.get(clasnameattrtype.size()-2).add(((EStructuralFeature) classifier.eContents().get(y)).getName());
						clasnameattrtype.get(clasnameattrtype.size()-1).add(((EStructuralFeature) classifier.eContents().get(y)).getEType().getName());
						yy=yy+1;
						lenght=lenght+1;
						
						
					}
					
				}
				ArrayList<String> secondColumninheritclass = new ArrayList<String>();
				secondColumninheritclass.add( child.getName());
				 for (int i=0 ;i< child.getEAllSuperTypes().size();i++) {
					
					 EClass classifier2=child.getEAllSuperTypes().get(i);
					 if(classifier2.getName()!=null) {
						 tempsubclass_suerclass.add(classifier2.getName());
					 for(int j=0;j< classifier2.eContents().size();j++) {
						 if (classifier2.eContents().get(j) instanceof EAttribute
									||classifier2.eContents().get(j) instanceof EReference ) {
							 
							 //for source metamodel
							 NSGAIIThreeStep.listinheritattributesourcemetamodel.add( (EStructuralFeature) classifier2.eContents().get(j));  
							 clasnameattrtype.get(clasnameattrtype.size()-2).add(((EStructuralFeature) classifier2.eContents().get(j)).getName());
							 clasnameattrtype.get(clasnameattrtype.size()-1).add(((EStructuralFeature) classifier2.eContents().get(j)).getEType().getName());	
							 lenght=lenght+1;
							 
						 }
						 
					}
					 
			    } 
				 }
				 
				 for (EClassifier classifier2 : metatarget.getEClassifiers()) {
					 if (classifier2 instanceof EClass) {
						 EClass child2 = ((EClass) classifier2);
						 if(child.isSuperTypeOf(child2) && !child2.equals(child)) {
							 
							 secondColumninheritclass.add(child2.getName());
							 tempsubclass_suerclass.add(child2.getName());
							 
							}
						}
					}
				 
				 NSGAIIThreeStep.inheritClass.add( secondColumninheritclass);
				 firsttime=FindAllSuperClassesOfClass(tempsubclass_suerclass, firsttime, NSGAIIThreeStep.subcalss_supperclass);		 
				 NSGAIIThreeStep.ClassNameAttributType.add( clasnameattrtype);
				 NSGAIIThreeStep.classnamelength.add(lenght);
				 start=start+lenght;
				 lenght=0;
			}
		}
	 }


private boolean FindAllSuperClassesOfClass(ArrayList<String> tempsubclass_suerclass, boolean firsttime, ArrayList<ArrayList<String>> subcalss_supperclass2) {
	// TODO Auto-generated method stub
	
	 for(int i=1; i<tempsubclass_suerclass.size();i++) {
		 
		 ArrayList<String> tempsubclass = new ArrayList<String>();
		 tempsubclass.add(tempsubclass_suerclass.get(i));
		 tempsubclass.add(tempsubclass_suerclass.get(0));
		 
		 if(firsttime) {
			 subcalss_supperclass2.add( tempsubclass);
			 if(i==tempsubclass_suerclass.size()-1)
				 firsttime=false; 
		 }
		 else {
			 boolean available=false;
		 for(int j=0; j< subcalss_supperclass2.size();j++ ) {
			
		 if(subcalss_supperclass2.get(j).get(0).equals(tempsubclass.get(0) ) )
		 {
			 available=true;
			 int id= subcalss_supperclass2.get(j).size()-1;
			 subcalss_supperclass2.get(j).add(id,tempsubclass.get(1));
		 }
		
		 }
		 if(!available) {
			 subcalss_supperclass2.add( tempsubclass);
		 }
		 
		 }
		 
	 }
	 	
	return firsttime;
}

private void createclassifier() {
	// TODO Auto-generated method stub
	Setting s=new Setting();
	final String MMRootPath2     = s.getsourcemetamodel();
	
	List<EPackage>   metamodels2= retPackMM(retPackResouceMM(MMRootPath2));
	MetaModel metamodel = null;
	 for (EPackage p: metamodels2) {
		 metamodel = new MetaModel(p); 
	 }

	 for (EClassifier classifier : metamodel.getEClassifiers()) {
		 if (classifier instanceof EClass) {
				NSGAIIThreeStep.classifierliast.add(classifier);
		 }
		 
	 }
	 	
}
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
			String atl_transformation = this.getValidNameOfFile(outputFolder);
			AtlParser atlParser       = new AtlParser();
			atlParser.extract(atlModel, atl_transformation);	
			// compile transformation
			String asm_transformation = atl_transformation.replace(".atl", ".asm");
		} 
		catch (ATLCoreException e) {}
		return false;
	}
	
	
} // NSGA-II
