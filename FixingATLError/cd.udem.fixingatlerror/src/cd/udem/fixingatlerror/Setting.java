package cd.udem.fixingatlerror;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jmetal.metaheuristics.nsgaII.NSGAIIThreeStep;

public class Setting {
private int populationsize=100;//100
private int maxevaluation=50000;//20
String	FileInputAtl;
//change bedam
private int NumberMutants=3;
private int NumberEvaluation=1;
public int frequence=5;

/* PNML 2 PetriNet */
//private String secondecorename="PetriNet";
//private String firstecorename="PNML";
//private String targetmetamodel ="examples/class2rel/metamodels/PNML.ecore";
//private String sourcemetamodel= "examples/class2rel/metamodels/PetriNet.ecore";



/* Class 2 RElational */
/*private String secondecorename="Relational";
private String firstecorename="Class";
private String sourcemetamodel  =  "Tmp/varaminz/step3new/examples/class2rel/metamodels/Relational.ecore";
private String targetmetamodel   = "Tmp/varaminz/step3new/examples/class2rel/metamodels/Class.ecore";*/

/* Dockbook 2 BibTex */
/*private String secondecorename="DocBook";
private String firstecorename="BibTeX";
private String sourcemetamodel  =  NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/metamodels/DocBook.ecore";
private String targetmetamodel   = NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/metamodels/BibTeX.ecore";
*/
private String secondecorename="ER";
private String firstecorename="SimpleUml";
private String sourcemetamodel  =  NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/metamodels/ER.ecore";
private String targetmetamodel   = NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/metamodels/SimpleUml.ecore";


/*private String secondecorename="bpmn";
private String firstecorename="UML2_3_0_0";
private String sourcemetamodel  =  "Tmp/varaminz/step3new/examples/class2rel/metamodels/bpmn.ecore";
private String targetmetamodel   = "Tmp/varaminz/step3new/examples/class2rel/metamodels/UML2_3_0_0.ecore";*/


private String trafo   =null;
private String firstinput   =null;
private String secondinput   =null;
private String thirdinput   =null;
private String fourthinput   =null;
List<String> metamodellist = new ArrayList<String>();
private String inputfaultytransformation=NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/class2rel/trafo/semanticfaulty/atl"+Class2Rel.atlindex+"/2.atl";
int counterinput=1;
private String path = "outputfolder";
private String newoutputresult=NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step;
private String outputresult=NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/outputfolder";
private String outputfolder=NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/zahra2";
public static ArrayList<String> ruleName = new ArrayList<String>();
public static ArrayList<String> extendsruleName = new ArrayList<String>();
public static ArrayList<ArrayList<String>> subcalss_supperclasstarget = new ArrayList<ArrayList<String>>();

public  List<String> getruleName(){
	return this.ruleName;
}
public void setruleName(String str) {
	this.ruleName.add(str);
}
public  List<String> getextendsruleName(){
	return this.extendsruleName;
}
public void setextendsruleName(String str) {
	this.extendsruleName.add(str);
}
public ArrayList<ArrayList<String>> getsubcalss_supperclasstarget() {
	return subcalss_supperclasstarget;
}
public Setting(){
		File inputfile = new File(NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/config.txt");
		FileReader fr = null;
		try {
			fr = new FileReader(inputfile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   //reads the file  
		BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream  
		StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters  
		String line;
		try {
			newoutputresult=br.readLine();
			outputresult=br.readLine();
			this.FileInputAtl=br.readLine();
			sourcemetamodel  =  br.readLine();
			targetmetamodel   = br.readLine();
			secondecorename=br.readLine();
			firstecorename=br.readLine();
			trafo=br.readLine();		
			thirdinput=br.readLine();
			fourthinput=br.readLine();
			firstinput=br.readLine();
			secondinput=br.readLine();			
			while((line=br.readLine()) != null){
				metamodellist.add(line);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		this.FileInputAtl=NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/trafo2/zahra2mutants/finalresult";
	}

	public String getFileInputAtl() {
		return this.FileInputAtl;
	}
	public String getinputfaultytransformation() {
		return this.inputfaultytransformation;
	}
	public String getsourcemetamodel () {
		return this.sourcemetamodel;
	}
	public String gettrafo () {
		return this.trafo;
	}
	public String getfirstinput () {
		return this.firstinput;
	}
	public String getsecondinput () {
		return this.secondinput;
	}
	public String getthirdinput () {
		return this.thirdinput;
	}
	public String getfourthinput () {
		return this.fourthinput;
	}
	
	public String gettargetmetamodel() {
		return this.targetmetamodel;
	}
	public String getoutputfolder() {
		return this.outputfolder;
	}
	public String getsecondecorename() {
		return this.secondecorename;
	}
	public String getoutputresult() {
		return this.outputresult;
	}
	public String getnewoutputresult() {
		return this.newoutputresult;
	}
	public String getfirstecorename() {
		return this.firstecorename;
	}
	public String getpath() {
		return this.path;
	}
	public int getcounterinput() {
		return this.counterinput;
	}
	public int getpopulationsize() {
		return this.populationsize;
	}
	public  List<String> getmetamodellist (){
		return this.metamodellist;
	}
	public int getmaxevaluation() {
		return this.maxevaluation;
	}
	
	public int getNumberMutants() {
		return this.NumberMutants;
	}
	public int setmaxevaluation(int i) {
		
	     return this.maxevaluation=i;
	
	        }
	public int getNumberEvaluation() {
		return this.NumberEvaluation;
	}
}
