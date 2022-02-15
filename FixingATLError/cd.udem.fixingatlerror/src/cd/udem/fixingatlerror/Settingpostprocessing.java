package cd.udem.fixingatlerror;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Settingpostprocessing {

String	FileInputAtl;
private String secondecorename;
private String firstecorename;
private String targetmetamodel;
private String sourcemetamodel;
List<String> metamodellist = new ArrayList<String>();
int counterinput=1;

public Settingpostprocessing(){
		File inputfile = new File("examples/configpostprocessing.txt");
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
		
			sourcemetamodel  =  br.readLine();
			targetmetamodel   = br.readLine();
			secondecorename=br.readLine();
			firstecorename=br.readLine();
			
		
			while((line=br.readLine()) != null){
				metamodellist.add(line);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

	public String getFileInputAtl() {
		return this.FileInputAtl;
	}
	
	public String getsourcemetamodel () {
		return this.sourcemetamodel;
	}
	public String gettargetmetamodel() {
		return this.targetmetamodel;
	}
	
	public String getsecondecorename() {
		return this.secondecorename;
	}
	
	public String getfirstecorename() {
		return this.firstecorename;
	}
	
	public int getcounterinput() {
		return this.counterinput;
	}
	
	public  List<String> getmetamodellist (){
		return this.metamodellist;
	}

	
}
