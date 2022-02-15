package cd.udem.fixingatlerror;
import jmetal.metaheuristics.nsgaII.NSGAIIThreeStep;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.MyProblemThreeStep;
import jmetal.problems.ProblemFactory;
import jmetal.qualityIndicator.QualityIndicator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Class2Rel extends BaseTest {
	
	public static String typeoperation = null;
	public static Logger logger_; // Logger object
	public static FileHandler fileHandler_; // FileHandler object
	public static int checkoffspring = 0;
	public static int totalnumber = 1;
	public static int numberdiversity = 1;
		
	public static int atlindex = 1;
	public static Runtime runtime;
	public static boolean HIDE_SYSTEM_ERR = false;
	public static String HIDE_SYSTEM_ERR_FILE = "err.log";

	public static void main(String[] args) throws Exception, IllegalArgumentException {

		if (HIDE_SYSTEM_ERR) {
			System.err.println("System.err redirected to '" + HIDE_SYSTEM_ERR_FILE + "'");
			File file = new File(HIDE_SYSTEM_ERR_FILE);
			FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			System.setErr(ps);
			ps.close();
			System.err.println("System.err - on " + new Date());

		}
		File dir=new File(NSGAIIThreeStep.startingroot+"/varaminz");
		if (!dir.exists())
			  dir.mkdir();
		 
		File dir2=new File(NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step);	 
		Boolean del=deleteDirectory( dir2);
		 if (!dir2.exists())
			  dir2.mkdir();
	   String str="examples";
	   String str2=NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples";	   	 
  	   File dest = new File(str2);     
  	   del=deleteDirectory( dest); 
  	   if(!dest.exists())
	     copyDirectory(str,str2); 
  	
		for (int i = 0; i < 20; i++) {
			numberdiversity=1;
		for (int j = 0; j < 1; j++) {
			
	         NSGAIIThreeStep.fitness2=-100;		
		     MyProblemThreeStep.findfirst=false;		
		     NSGAIIThreeStep.fixedgeneration=-1;
		     NSGAIIThreeStep.fitness3=0;
			 NSGAIIThreeStep.retainedSolution.setindex(-1);
			 NSGAIIThreeStep.indexiteration=50000;
			 NSGAIIThreeStep.foundIndex=-1;
			 NSGAIIThreeStep.numberincorrectatl=0;
			 str2=NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity;		
			  dest = new File(str2); 	
			  del=deleteDirectory( dest);
		     if(  i==0)
			  {
			   new Class2Rel().runthirdstep(args);
			   str2=NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity;
			  dest = new File(str2); 	
			  del=deleteDirectory( dest);
			  
			  }
			checkoffspring = 0;
			totalnumber = 1;
			numberdiversity++;
			System.gc();
		}
		int k=i+1;
		 str="examplesresult3/atl"+k;
		 dest = new File(str);
		 str2=NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/trafo/semanticfaulty/atl"+k;	 
		 copyDirectory(str2,str);	
		atlindex++;
		}
	}

	static boolean  deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
	public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) 
			  throws IOException {
			    Files.walk(Paths.get(sourceDirectoryLocation))
			      .forEach(source -> {
			          Path destination = Paths.get(destinationDirectoryLocation, source.toString()
			            .substring(sourceDirectoryLocation.length()));
			          try {
			              Files.copy(source, destination);
			          } catch (IOException e) {
			              e.printStackTrace();
			          }
			      });
			}
	

	private void runthirdstep(String[] args) throws Exception {
		
		//Getting the runtime reference from system
		 runtime = Runtime.getRuntime();
		Problem problem; // The problem to solve
		Algorithm algorithm; // The algorithm to use
		Operator crossover; // Crossover operator
		Operator mutation; // Mutation operator
		Operator selection; // Selection operator
		HashMap parameters; // Operator parameters
		QualityIndicator indicators; // Object to get quality indicators
		indicators = null;
		if (args.length == 1) {
			Object[] params = { "Real" };
			problem = (new ProblemFactory()).getProblem(args[0], params);
		} 
		else if (args.length == 2) {
			Object[] params = { "Real" };
			problem = (new ProblemFactory()).getProblem(args[0], params);
			indicators = new QualityIndicator(problem, args[1]);
		} 
		else { 
			problem = new MyProblemThreeStep("Int");
			 } 

		algorithm = new NSGAIIThreeStep(problem);
		Setting s = new Setting();
		algorithm.setInputParameter("populationSize", s.getpopulationsize()); 																		// va myproblem barabar bashe
		algorithm.setInputParameter("maxEvaluations", 2000); 
		// Mutation and Crossover for Real codification
		parameters = new HashMap();
		parameters.put("probability", 0.9);
		parameters.put("distributionIndex", 50.0);
		crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);
		parameters = new HashMap();
		parameters.put("probability", 0.2);
		parameters.put("distributionIndex", 30.0);
		mutation = MutationFactory.getMutationOperator("BitFlipMutation" + "", parameters);
		parameters = null;
		selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);
		// Add the operators to the algorithm
		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
		algorithm.addOperator("selection", selection);
		// Add the indicator object to the algorithm
		algorithm.setInputParameter("indicators", indicators);
		// Execute the Algorithm
		long initTime = System.currentTimeMillis();
		SolutionSet population = algorithm.execute();
		long estimatedTime = System.currentTimeMillis() - initTime;
		population.printRulesToFile("Rule");
		population.printObjectivesToFile(s.getpath()+"/resultformodeltransformation1"
		  +"/FUN",estimatedTime,
		population.size()); 
		population.clear();
		System.out.println("end");
		
	}

}
