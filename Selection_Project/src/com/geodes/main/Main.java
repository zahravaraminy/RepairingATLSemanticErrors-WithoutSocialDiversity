package com.geodes.main;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {	
	
	static int LIMITE = 1 ; // the number of instances
	static double probabilty = 1; // if you want a random value just make it equal = -1
	final static int SCOPE = 10; // the max value of multiplicity * 
	
	
	public static void main(String[] args) {	
				
		try {
			listFilesForFolder(new File("output-incorrect-produced-model-oneoperation"));
		}catch(Exception e) {e.printStackTrace();}
			
	
	}	
	
	
	public static void write(Principal p,File path) {
		 try {
		      FileWriter myWriter = new FileWriter(path);
		      myWriter.write(new String(p.txt));
		      myWriter.close();
		    } catch (IOException e) {e.printStackTrace();}		
	}
	
	
	
	public static void listFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory() && fileEntry.getName().contains("transformation")) {
	        	String s = null ;
	        	for (final File x : fileEntry.listFiles()) {
	        		if(x.getName().contains("target")) {
	        			s = x.getAbsolutePath();
	        			break;
	        		}
	        	}if(s==null) continue;
new Principal(fileEntry.getAbsolutePath()+"/Class2Relational.atl","models/Class.xsd","input-model-CL2RL/Packageinput.xmi","models/Relational.xsd","correct-output-model-CL2RL/correctoutputmodel.xmi",s,"Schema","name","Package");	

	        } else listFilesForFolder(fileEntry);
	        
	    }
	}

}

