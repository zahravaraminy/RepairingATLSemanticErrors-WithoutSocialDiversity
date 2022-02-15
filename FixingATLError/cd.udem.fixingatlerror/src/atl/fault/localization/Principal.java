package atl.fault.localization;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import org.xml.sax.SAXException;
//import com.geodes.main.diff_match_patch.Diff;
//import com.geodes.main.diff_match_patch.Operation;

import atl.fault.localization.diff_match_patch.Diff;
import atl.fault.localization.diff_match_patch.Operation;



//*********************************************************************************************
public class Principal {		
	private String source_xmi,path_ecore, path_out1, path_out2, root,atl_path,root_source_model,path_xsd1;			
	Vector<Vector> result,out1_rows, out2_rows;
	//double probability = Main.probabilty;
	StringBuilder txt;	
	private Vector<Vector> allRows; 
	private File fichier;
	String MM; // meta modele id
	private String MetaData;//head
	private Vector<Element> elems_list;
	private Vector<ComplexType> complextype_list;
	Random rd;		
	StringBuilder builder_out1,builder_out2,xsd1,xsd,builder_atl,builder_input;	
	String identity;	
	Vector<Vector> rules_in_atl;
	StringBuilder LOGS;
	private Vector rules;
	public Vector<Vector> evaluation ; // in each vector elem we have : rule selected + why it s selected
	
	// path_atl_error + path_src_xsd + path_src_xmi + path_dst_xsd + path_out1 + path_out2 + dst_root_name + identity + src_root_name
	
	public Vector getRules() {
		return rules;
	}


	public void setRules(Vector rules) {
		this.rules = rules;
	}

	
	public void printMutations(String incorrect_atl, String correct_atl) {		
		StringBuilder list_mutation = new StringBuilder();		
		Vector f2 = new Vector();
		StringBuilder str = new StringBuilder();
		File correct = new File(correct_atl); 
		Scanner lire = null;
		try {
			lire = new Scanner(correct);		
		while (lire.hasNextLine()) {
	        String data = lire.nextLine();
	        f2.add(data);	        	
		}		
		} catch (FileNotFoundException e) {	
			e.printStackTrace();}finally {
				lire.close();
		}
		correct = new File(incorrect_atl); 		
		try {
			lire = new Scanner(correct);	
			
		while (lire.hasNextLine()) {
	        String data = lire.nextLine();
	        str.append(data+"\n");        	
		}		
		} catch (FileNotFoundException e) {	
			e.printStackTrace();}finally {
				lire.close();
		}		
		int max_index_gauche = str.length();		
		int i =0;
		while(i<max_index_gauche) {
			int j = str.indexOf("-- MUTATION",i);
			if(j <0) break;		
			int index = Integer.parseInt(str.substring(str.indexOf("(line ",j)+5,str.indexOf(":",str.indexOf("(line ",j))).replace(" ",""));
			if(getRule(index,f2)!=(-1)) list_mutation.append(getRule(index,f2)+"\n");
			i = j + 6 ;
		}
		File f = new File(this.path_out2);
		String b = new String(f.getAbsolutePath().replace(f.getName(),"Real_Src_of_errors.txt"));
		File RealRules = new File(b);
		write(list_mutation,RealRules);			
	}
	
	
	
public int getRule(int i, Vector lines) {		
		String line = (String) lines.get(i);
		boolean notIn = false;
		mark:
		while(!line.contains("rule")) {
			 i -= 1;
			 if(i <0) {
				 notIn = true;
				 break mark;
			 }
			 line = (String) lines.get(i);			 
		}
		if(notIn) return -1;
		else {
			String s = (String) lines.get(i);
			s = s.substring(s.indexOf("rule")+4, s.indexOf("{")).replace(" ","");		
			for(int k=0; k<this.rules_in_atl.size();k++) {
				if(((String)this.rules_in_atl.get(k).get(0)).equalsIgnoreCase(s)) {
					return k;
				}
			}			
			return -1;		
		}
	}
//******************************************************************************************************************************************
	// the principal constructor
	/***
	 * 
	 * @param atl : The path of incorrect ATL file
	 * @param source_xsd : The path of input xsd file
	 * @param source_xmi : The path of input xmi file
	 * @param path_xsd : The path of Output xsd file
	 * @param out1 : output xmi file from correct atl file 
	 * @param out2 : output xmi file from incorrect atl file 
	 * @param root : the root name of input meta-model()
	 * @param identity : use name
	 * @param root_in_source_model : the root name of output meta-model(in xsd file)
	 */
	public Principal (String atl,String source_xsd, String source_xmi ,String path_xsd,String out1, String out2, String root, String identity, String root_in_source_model) {	
		rules = new Vector();		
		this.source_xmi = source_xmi;
		builder_input = new StringBuilder();
		this.path_xsd1 = source_xsd;
		this.parse_the_source(this.path_xsd1);		
		LOGS = new StringBuilder();
		this.root_source_model = root_in_source_model;
		this.atl_path = atl;
		this.identity = identity;
		xsd = new StringBuilder();
		this.path_out1 = out1;
		this.path_out2 = out2;
		this.root = root;
		result = new Vector();		
		txt = new StringBuilder();
		rd = new Random();
		elems_list = new Vector<Element>();
		complextype_list = new Vector<ComplexType>();
		evaluation = new Vector<Vector>();
		allRows = new Vector<Vector>();
		fichier = new File(path_xsd); // to model references ..... xsd of the ecore model
		Scanner lire = null;
		try {
			lire = new Scanner(fichier);		
		while (lire.hasNextLine()) {
	        String data = lire.nextLine();
	        xsd.append(data+"\n");
	        String[] parseLine = data.split(" ");
	        Vector tmp = new Vector();
	        for(String elem : parseLine) 
	        	if(!elem.isEmpty()) tmp.add(elem);	        
	        allRows.add((Vector) tmp.clone());		
		}		
		} catch (FileNotFoundException e) {	
			e.printStackTrace();}finally {
				lire.close();
			}
		elems_list = getElements(allRows); // la liste des élements du diagrammes
		complextype_list = getComplexTypes(allRows); // les détailles de chaque élements		
		for(Element e : elems_list) 
            e.setType(e.getType().replace("/>",""));		
		for(ComplexType e : complextype_list) {
			e.setName(e.getName().replace(">",""));
			for(Element ee : e.getSequence()) 
				ee.setType(ee.getType().replace("/>",""));	
			for(Attribute x : e.getAttributs())
				x.setType(x.getType().replace("/>",""));
		}			
		LOGS.append("___________________ ATL CORRECTION __________________________________________________\n\n");
		LOGS.append("ATL file :"+this.atl_path+"\nThe instance generated by the correct transformation: "+this.path_out1+"\n");
		LOGS.append("The instance generated by "+new File(this.atl_path).getName()+": "+this.path_out2+"\n");
		LOGS.append("______________________________________________________________________________________\n");		
		builder_out1 = new StringBuilder();
    	fichier = new File(this.path_out1); 		
		try {
			 lire = new Scanner(fichier);		
		while (lire.hasNextLine()) 
			builder_out1.append(lire.nextLine()+" \n");					
		} catch (FileNotFoundException e) {	e.printStackTrace();}finally {
			lire.close();
		}		
		builder_out2 = new StringBuilder();
    	fichier = new File(this.path_out2); 		
		try {
			 lire = new Scanner(fichier);		
		while (lire.hasNextLine()) 
			builder_out2.append(lire.nextLine()+" \n");					
		} catch (FileNotFoundException e) {	e.printStackTrace();}finally {
			lire.close();
		} 		
		Vector<StringBuilder> ny = new Vector<StringBuilder>();
		ny.add(this.builder_input);
		ny.add(this.builder_out1);
		ny.add(this.builder_out2);		
		this.cleaning(ny);	
		builder_atl = new StringBuilder();
    	fichier = new File(this.atl_path); 		
		try {
			 lire = new Scanner(fichier);		
		while (lire.hasNextLine()) {
			String line = lire.nextLine()+" \n";
			String h = new String(line);
			if(h.indexOf("--")<0) builder_atl.append(h);		
		}						
		} catch (FileNotFoundException e) {	e.printStackTrace();}finally {
			lire.close();
		} 		
		rules_in_atl = this.getRulesOfatl();		
		LOGS.append("\n****************************************************************************\n****************************************************************************\n****************************************************************************\n");
		LOGS.append("***********************\\ Extraction of Rules //****************************");		
		Vector<Vector> comparaison= new Vector<Vector>();
		boolean t ;		
		int k = isNormalized();		
		// 1 the file does not start by MM:Root and not start by XMI
		// 2 file without body and not start by XMI
		// 3 more than one root and not start by XMI
		// 4 Concepts outside the Root and not start by XMI
		// 5 The file is good and not start by XMI
		// 6 start by XMI		
		if(k==1) LOGS.append("NOTE : this xmi file does not start by "+"<"+this.getMMname()+":"+this.root+" and it does not start by XMI tag!\n\n");
		if(k==2) LOGS.append("This file has the header only without body!\n\n");
		if(k==3) LOGS.append("ERROR in the structure of your model! There is more than 1 root tag!\n\n");
		if(k==4) LOGS.append("Concepts outside the root!\n\n");
    	if(k==5) {
			LOGS.append(" This is a good xmi file \n\n");
			comparaison = this.compare(builder_out1, builder_out2);
			this.evaluation.clear();
			rules = getRules(comparaison); // the file is good
			rules = this.delete_duplication_rules(rules);		
		}		
		if(k==6) {
			LOGS.append("\n This file starts by XMI tag! so we have to normalize it...");			 
			File f = new File(this.path_out2);
			String b = new String(f.getAbsolutePath().replace(f.getName(),"normalized_"+f.getName()));
			File fich_n = new File(b); 
			try {		
					if(!fich_n.exists()) {
						if(fich_n.createNewFile()) {
							StringBuilder copy = new StringBuilder();
					    	fichier = new File(this.path_out2); 		
							try {
								 lire = new Scanner(fichier);		
							while (lire.hasNextLine()) 
								copy.append(lire.nextLine()+" \n");					
							} catch (FileNotFoundException e) {	e.printStackTrace();}finally {
								lire.close();
							} 			
							String y = new String(copy);
							String x = new String(copy);
							x = x.replace("xmi:XMI",this.getMMname()+":"+this.root);					
							StringBuilder body = new StringBuilder();
							int a = new String("</"+this.getMMname()+":"+this.root+">").length()+1;
							int bb = new String("<"+this.getMMname()+":"+this.root+">").length();
							body.append(copy.substring(copy.indexOf("<"+this.getMMname()+":"+this.root+">")+bb,copy.indexOf("</"+this.getMMname()+":"+this.root+">")+a));
							StringBuilder header_ = new StringBuilder(x.substring(0,x.indexOf(">",x.indexOf(">")+1)+1));										
							header_.append(body);							
							write(header_,fich_n);						
							LOGS.append(" The new generated Xmi file : "+fich_n.getAbsolutePath()+" \n");
							y = y.replace(y.substring(
									y.indexOf("<"+this.getMMname()+":"+this.root+">"),
									y.indexOf("</"+this.getMMname()+":"+this.root+">")+this.getMMname().length()+4+this.root.length()+1
									                 )
							        ,"");						
							y = y.replace(y.substring(0,y.indexOf(">",y.indexOf("?>")+2)+1),"");
							y = y.replace("</xmi:XMI>","");								
							int uu = y.indexOf("<"+this.getMMname()+":");						
							while(uu>=0) {
								String word = y.substring(uu+2+this.getMMname().length(),y.indexOf(" ",uu));							
								Vector tmp = new Vector();
								tmp = this.getIndexOfRule(word);
								if(!tmp.isEmpty()) for(int r=0; r<tmp.size();r++) rules.add((int)tmp.get(r));							
								uu = y.indexOf("<"+this.getMMname()+":",uu+2+this.getMMname().length()+1);								
							}						
							comparaison = this.compare(builder_out1, header_);
							this.evaluation.clear();
							Vector rules_list2 = getRules(comparaison); // vecteur des integers						
							for(int p=0; p<rules_list2.size();p++) 
								rules.add(rules_list2.get(p));						
							
							// add the rule Root_to_Root 		
							
							
							Vector t_t = this.getIndex(this.root_source_model,this.root);
							if(!t_t.isEmpty())
							for(int p=0; p<t_t.size();p++) {
								rules.add(t_t.get(p));							
								Vector h = new Vector();
								h.add((int)t_t.get(p));
								h.add("- Root to Root case ::: Left root ="+this.root_source_model+";  Right root="+this.root+";");
							    this.evaluation.add((Vector) h.clone());
							}
											
							}
					}else {
						StringBuilder copy = new StringBuilder();
				    	fichier = new File(this.path_out2); 		
						try {
							 lire = new Scanner(fichier);		
						while (lire.hasNextLine()) 
							copy.append(lire.nextLine()+" \n");					
						} catch (FileNotFoundException e) {	e.printStackTrace();}finally {
							lire.close();
						} 			
						String y = new String(copy);
						String x = new String(copy);
						x = x.replace("xmi:XMI",this.getMMname()+":"+this.root);					
						StringBuilder body = new StringBuilder();
						int a = new String("</"+this.getMMname()+":"+this.root+">").length()+1;
						int bb = new String("<"+this.getMMname()+":"+this.root+">").length();
						body.append(copy.substring(copy.indexOf("<"+this.getMMname()+":"+this.root+">")+bb,copy.indexOf("</"+this.getMMname()+":"+this.root+">")+a));
						StringBuilder header_ = new StringBuilder(x.substring(0,x.indexOf(">",x.indexOf(">")+1)+1));										
						header_.append(body);							
						write(header_,fich_n);						
						LOGS.append(" The new generated Xmi file : "+fich_n.getAbsolutePath()+" \n");
						y = y.replace(y.substring(
								y.indexOf("<"+this.getMMname()+":"+this.root+">"),
								y.indexOf("</"+this.getMMname()+":"+this.root+">")+this.getMMname().length()+4+this.root.length()+1
								                 )
						        ,"");						
						y = y.replace(y.substring(0,y.indexOf(">",y.indexOf("?>")+2)+1),"");
						y = y.replace("</xmi:XMI>","");								
						int uu = y.indexOf("<"+this.getMMname()+":");						
						while(uu>=0) {
							String word = y.substring(uu+2+this.getMMname().length(),y.indexOf(" ",uu));							
							Vector tmp = new Vector();
							tmp = this.getIndexOfRule(word);
							if(!tmp.isEmpty()) for(int r=0; r<tmp.size();r++) rules.add((int)tmp.get(r));							
							uu = y.indexOf("<"+this.getMMname()+":",uu+2+this.getMMname().length()+1);								
						}						
						comparaison = this.compare(builder_out1, header_);
						this.evaluation.clear();
						Vector rules_list2 = getRules(comparaison); 					
						for(int p=0; p<rules_list2.size();p++) 
							rules.add(rules_list2.get(p));	
						Vector t_t = this.getIndex(this.root_source_model,this.root);
						if(!t_t.isEmpty())
						for(int p=0; p<t_t.size();p++) {
							rules.add(t_t.get(p));							
							Vector h = new Vector();
							h.add((int)t_t.get(p));
							h.add("- Root to Root case ::: Left root ="+this.root_source_model+";  Right root="+this.root+";");
						    this.evaluation.add((Vector) h.clone());
						}
						
					}
					rules.add(0);
					rules = this.delete_duplication_rules(rules);
					
					}catch (IOException e1) {e1.printStackTrace();
				}		 
		}		
		if(k==5 ||  k==6) {
			this.log_Results(comparaison);
			LOGS.append("\n\n");
			this.LOGS.append("\n___________________________________________________________________________");
			this.LOGS.append("\n_________________________\\ Selection of Rules //_________________________");
			this.LOGS.append("\n___________________________________________________________________________");
			LOGS.append("\nList of Rules : \n");
			File f = new File(this.path_out2);
			String b = new String(f.getAbsolutePath().replace(f.getName(),"SelectedRules.txt"));
			File list__r = new File(b);
			try {
				if(list__r.createNewFile()) {
					StringBuilder t_ = new StringBuilder();
					for(int i=0; i <rules.size();i++) {
						LOGS.append("rule: "+this.rules_in_atl.get((int)rules.get(i)).get(0)+" -- index="+rules.get(i)+"\n");
						t_.append(rules.get(i)+" \n");
					}					
					write(t_,list__r);
				}else {
					StringBuilder t_ = new StringBuilder();
					for(int i=0; i <rules.size();i++) {
						LOGS.append("rule: "+this.rules_in_atl.get((int)rules.get(i)).get(0)+" -- index="+rules.get(i)+"\n");
						t_.append(rules.get(i)+" \n");
					}					
					write(t_,list__r);
				}
			} catch (IOException e1) {
				
				LOGS.append("*** ERROR in the creation of the output file!");
			}			
			f = new File(this.path_out2);
			b = new String(f.getAbsolutePath().replace(f.getName(),"Logs.txt"));	
			File loog = new File(b);
			try {
				if(loog.createNewFile()) 					
					write(LOGS,loog);
				else write(LOGS,loog);
			} catch (IOException e1) {				
				LOGS.append("*** ERROR in the creation of logs file!");
			}
			f = new File(this.path_out2);
			b = new String(f.getAbsolutePath().replace(f.getName(),"Report.txt"));	
			File report = new File(b);
			try {
				if(report.createNewFile()) {
					StringBuilder s = new StringBuilder();
					for(int i=0; i<this.rules_in_atl.size();i++) {						
						s.append("- The rule "+this.getRuleName(i)+" (Index = "+i+"):");
						if(this.isSelected(i, rules)) {
							s.append("\n");
							for(Vector elem : this.evaluation) 
								if(((int)elem.get(0))==i) 
									s.append(elem.get(1)+"\n");
						}else s.append(" NOT SELECTED.\n");					
						s.append("\n\n___________________________________________________________________\n\n");
					}					
					write(s,report);
				}else {
					StringBuilder s = new StringBuilder();
					for(int i=0; i<this.rules_in_atl.size();i++) {						
						s.append("- The rule "+this.getRuleName(i)+" (Index = "+i+"):");
						if(this.isSelected(i, rules)) {
							s.append("\n");
							for(Vector elem : this.evaluation) 
								if(((int)elem.get(0))==i) 
									s.append(elem.get(1)+"\n");
						}else s.append(" NOT SELECTED.\n");					
						s.append("\n\n___________________________________________________________________\n\n");
					}					
					write(s,report);
				}
			} catch (IOException e1) {				
				LOGS.append("*** ERROR in the creation of Report.txt file!");
			}	
		}		
	}	

	//********************************FOR STATISTICS AND EVALUATION ***************************************************************************
	// fichier_xsd, Correct_instance, Error_instance, The_root_name, Identity
	/***
	 * 
	 * @param atl : The path of incorrect ATL file
	 * @param atl_correct : The path of correct ATL file
	 * @param source_xsd : The path of input xsd file
	 * @param source_xmi : The path of input xmi file
	 * @param path_xsd : The path of Output xsd file
	 * @param out1 : output xmi file from correct atl file 
	 * @param out2 : output xmi file from incorrect atl file 
	 * @param root : the root name of input meta-model()
	 * @param identity : use name
	 * @param root_in_source_model : the root name of output meta-model(in xsd file)
	 */
	public Principal (String atl,String atl_correct,String source_xsd, String source_xmi ,String path_xsd,String out1, String out2, String root, String identity, String root_in_source_model) {	
		this(atl,source_xsd,source_xmi,path_xsd,out1,out2,root,identity,root_in_source_model);
		printMutations(atl, atl_correct);
		
	
		
	}		
//**************************************************************************************************************************************************************************
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean isSelected(int index,Vector rules ) {		
		for(int i=0;i<rules.size();i++) {
			if((int)rules.get(i)==index) return true;
		}
		return false;
	}
	
	// traitement des espaces dans les fichiers xmi
	public void cleaning(Vector<StringBuilder> vect) {		
		for(StringBuilder x : vect) {
			int max_index_gauche = x.length();		
			int i =0;
			while(i<max_index_gauche) {
				int j = x.indexOf("=\"",i);
				if(j <0) break;			
				x = x.replace(x.indexOf("=\"",i)+2,x.indexOf("\"",x.indexOf("=\"",i)+2),x.substring(x.indexOf("=\"",i)+2,x.indexOf("\"",x.indexOf("=\"",i)+2)).replace(" ",""));
				i = x.indexOf("\"",x.indexOf("=\"",i)+2)+1;			
			}
		}	
	}
	
	//***************************************************************************************************	

	//***************************************************************************************************	
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
	        	System.out.println(fileEntry.getAbsolutePath());
new Principal(fileEntry.getAbsolutePath()+"/Class2Relational.atl","transformation/correcttransformation.atl","models/Class.xsd","input-model-CL2RL/Packageinput.xmi","models/Relational.xsd","correct-output-model-CL2RL/correctoutputmodel.xmi",s,"Schema","name","Package");	

	        } else listFilesForFolder(fileEntry);
	        
	    }
	}
	//***************************************************************************************************	
	//***************************************************************************************************	

	Vector<Vector> allRows2;
	Vector<ComplexType> complextype_list2;
	Vector<Element> elems_list2;
	public void parse_the_source(String xsd) {
		elems_list2 = new Vector<Element>();
		complextype_list2 = new Vector<ComplexType>();		
		 allRows2 = new Vector<Vector>();
		File fichier = new File(xsd); 
		Scanner lire = null;
		xsd1 = new StringBuilder();
		try {
			lire = new Scanner(fichier);		
		while (lire.hasNextLine()) {
	        String data = lire.nextLine();
	        xsd1.append(data+"\n");
	        String[] parseLine = data.split(" ");
	        Vector tmp = new Vector();
	        for(String elem : parseLine) 
	        	if(!elem.isEmpty()) tmp.add(elem);	        
	        allRows2.add((Vector) tmp.clone());		
		}		
		} catch (FileNotFoundException e) {	
			e.printStackTrace();}finally {lire.close();}
		elems_list2 = getElements(allRows2); // la liste des élements du diagrammes
		complextype_list2 = getComplexTypes(allRows2); // les détailles de chaque élements		
		for(Element e : elems_list2) 
            e.setType(e.getType().replace("/>",""));		
		for(ComplexType e : complextype_list2) {
			e.setName(e.getName().replace(">",""));
			for(Element ee : e.getSequence()) 
				ee.setType(ee.getType().replace("/>",""));	
			for(Attribute x : e.getAttributs())
				x.setType(x.getType().replace("/>",""));		
		}		
		builder_input = new StringBuilder();
    	fichier = new File(this.source_xmi); 		
		try {
			 lire = new Scanner(fichier);		
		while (lire.hasNextLine()) {
			String line = lire.nextLine()+" \n";
			String h = new String(line);
			if(h.indexOf("--")<0) builder_input.append(h);		
		}						
		} catch (FileNotFoundException e) {	e.printStackTrace();}finally {lire.close();} 	
	
	}
	
	
	//***************************************************************************************************	

	// 1 the file does not start by MM:Root and not starts by XMI
	// 2 file without body and not starts by XMI
	// 3 more than one root  and not starts by XMI
	// 4 Concepts outside the Root and not starts by XMI
	// 5 The file is good and not starts by XMI
	// 6 starts by XMI
	StringBuilder elementsOutsideTheroot;
public int isNormalized() {	
	elementsOutsideTheroot = new StringBuilder();
		if (builder_out2.indexOf("<xmi:XMI ")<0) { // 			
			if(builder_out2.indexOf("<"+this.getMMname()+":"+this.root)<0) {				
				return 1;
			}
			else 				
				if(builder_out2.indexOf("</"+this.getMMname()+":"+this.root+">")<0) {										
					return 2;
				}
				else 					
					if(this.getParts(builder_out2,this.getMMname()+":"+this.root).size()!=1) {						
						return 3;
					}						
					else {						
						String p = this.getParts(builder_out2,this.getMMname()+":"+this.root).get(0); // un seul bloc de root
							for(ComplexType elem : this.complextype_list) {								
							if(elem.getName().replace("\"","").equalsIgnoreCase(this.root)) {
								for(String b : elem.getBalises()) {	
									for(String h : this.getParts(new StringBuilder(p),b.replace("\"",""))) {
									p=p.replace(h,"");
									}									
								}								
							}else continue;							
						}				
						if(	(p.indexOf("</"+this.getMMname()+":"+this.root+">")  > p.indexOf("/>",p.indexOf("<")) && p.indexOf("/>",p.indexOf("<")) > 0)
								|| 
								p.indexOf("</"+this.getMMname()+":"+this.root+">") > p.indexOf("</",p.indexOf("<"))
						  ) { // so there is some concepts in the outside of the root
							//System.out.println("there is some concepts outside the root");
							
							this.elementsOutsideTheroot.append(p);
							
							return 4;
						}else { 
							//System.out.println("THE FILE IS GOOD");
							return 5;
						}					
					}
			}else return 6;		
}
	
	
//***************************************************************************************************	
	
public static void write(StringBuilder v,File path) {
	 try {
	      FileWriter myWriter = new FileWriter(path);
	      myWriter.write(new String(v));
	      myWriter.close();
	    } catch (IOException e) {e.printStackTrace();}		
}
	
	
//***************************************************************************************************	

	public Vector getRules(Vector<Vector> comparaison) {
		Vector ii = new Vector();
		diff_match_patch dmp = new diff_match_patch();		
		for(Vector elem : comparaison) {			
			String MM1 = ((String) ((Vector)elem.get(0)).get(4)).replace(" ","");
			MM1 = MM1.replace("\"","");
			MM1 = MM1.substring(MM1.indexOf(":")+1);
			String MM2 = null;
			String MM_Original = ((String) ((Vector)elem.get(0)).get(5)).replace(" ","");						
			if(MM_Original.indexOf("__Abstract__")>0) MM_Original = MM_Original.substring(MM_Original.indexOf("t__")+3);
			if(MM_Original.indexOf(":")>0) MM_Original = MM_Original.substring(MM_Original.indexOf(":")+1);
			MM_Original=MM_Original.replace("\"","").replace(" ","");
			if(((Vector)elem.get(1))!=null) { //if mc2 is not null
				MM2 = ((String) ((Vector)elem.get(1)).get(4)).replace(" ","");	
				MM2 = MM2.replace("\"","");
				MM2= MM2.substring(MM2.indexOf(":")+1);
				if(!MM1.equalsIgnoreCase(MM2)) { // mm1 diff mm2 
					//Vector tmp1 = this.getIndexOfRule(MM1);
					//if(!tmp1.isEmpty())
					//for(int b=0;b<tmp1.size();b++) ii.add((int)tmp1.get(b));
					Vector tmp1 = this.getIndex(MM_Original, MM2);
					if(!tmp1.isEmpty())
					for(int b=0;b<tmp1.size();b++) {
						ii.add((int)tmp1.get(b));
						Vector h = new Vector();
						h.add((int)tmp1.get(b));
						h.add("- MM1 != MM2 ::: MM1("+MM1+") is diffrent of MM2("+MM2+") of the element :"+((String) ((Vector)elem.get(0)).get(0))+" - this rule is from "+MM_Original+" to "+MM2);
					    this.evaluation.add((Vector) h.clone());
					}
					
					
					Vector tmp2 =this.getIndexTag(MM_Original,(String) ((Vector)elem.get(1)).get(1));
					for(int b=0;b<tmp2.size();b++) {
						ii.add(tmp2.get(b));
						Vector h = new Vector();
						h.add((int)tmp2.get(b));
						h.add("- MM2 is EMPTY ::: MM1="+MM1+"; The element="+((String) ((Vector)elem.get(0)).get(0))+" - this rule is from "+MM_Original+" to the TAG "+(String) ((Vector)elem.get(1)).get(1));
					    this.evaluation.add((Vector) h.clone());
					}					
					
					// j'ai ajouté cette partie *****************************
					Vector tmp4 = this.getIndex(MM_Original, MM1);
					if(!tmp4.isEmpty())
					for(int b=0;b<tmp4.size();b++) {
						ii.add((int)tmp4.get(b));
						Vector h = new Vector();
						h.add((int)tmp4.get(b));
						h.add("- MM1 != MM2 ::: MM1("+MM1+") is diffrent of MM2("+MM2+") of the element :"+((String) ((Vector)elem.get(0)).get(0))+" - this rule is from "+MM_Original+" to "+MM1);
					    this.evaluation.add((Vector) h.clone());
					}
					
					Vector tmp3 =this.getIndexTag(MM_Original,(String) ((Vector)elem.get(0)).get(1));
					for(int b=0;b<tmp3.size();b++) {
						ii.add(tmp3.get(b));
						Vector h = new Vector();
						h.add((int)tmp3.get(b));
						h.add("- MM2 is EMPTY ::: MM1="+MM1+"; The element="+((String) ((Vector)elem.get(0)).get(0))+" - this rule is from "+MM_Original+" to the TAG "+(String) ((Vector)elem.get(0)).get(1));
					    this.evaluation.add((Vector) h.clone());
					}
					
				}
				else { // mm1 = mm2
					LinkedList<Diff> diff = dmp.diff_main((String)((Vector)elem.get(0)).get(2),(String)((Vector)elem.get(1)).get(2));
					boolean e = false;
					for(int n=0;n<diff.size();n++) {
						if(diff.get(n).operation!=Operation.EQUAL) {
							e = true;
							break;
						}
					 }
					if(e) {
						
						Vector tmp2 =this.getIndexTag(MM_Original,(String) ((Vector)elem.get(0)).get(1));
						for(int b=0;b<tmp2.size();b++) {
							ii.add(tmp2.get(b));
							Vector h = new Vector();
							h.add((int)tmp2.get(b));
							h.add("- MM2 is EMPTY ::: MM1="+MM1+"; The element="+((String) ((Vector)elem.get(0)).get(0))+" - this rule is from "+MM_Original+" to the TAG "+(String) ((Vector)elem.get(0)).get(1));
						    this.evaluation.add((Vector) h.clone());
						}
						
						
						
						Vector tmp1 = this.getIndex(MM_Original,MM1);
						if(!tmp1.isEmpty())
						for(int b=0;b<tmp1.size();b++) {
							ii.add((int)tmp1.get(b));	
							Vector h = new Vector();
							h.add((int)tmp1.get(b));
							h.add("- MM1 = MM2 ::: MM1("+MM1+") is equal to MM2("+MM2+") of the element :"+((String) ((Vector)elem.get(0)).get(0))+" - this rule is from "+MM_Original+" to "+MM1);
						    this.evaluation.add((Vector) h.clone());
						}
					}
				}					
			}			
			else {				
				Vector tmp1 =this.getIndexTag(MM_Original,(String) ((Vector)elem.get(0)).get(1));
				for(int b=0;b<tmp1.size();b++) {
					ii.add(tmp1.get(b));
					Vector h = new Vector();
					h.add((int)tmp1.get(b));
					h.add("- MM2 is EMPTY ::: MM1="+MM1+"; The element="+((String) ((Vector)elem.get(0)).get(0))+" - this rule is from "+MM_Original+" to the TAG "+(String) ((Vector)elem.get(0)).get(1));
				    this.evaluation.add((Vector) h.clone());
				}
				Vector tmp2 =this.getIndex(MM_Original,MM1);
				for(int b=0;b<tmp2.size();b++) {
					ii.add(tmp2.get(b));	
					Vector h = new Vector();
					h.add((int)tmp2.get(b));
					h.add("- MM2 is EMPTY ::: MM1="+MM1+"; The element="+((String) ((Vector)elem.get(0)).get(0))+" - this rule is from "+MM_Original+" to "+MM1);
				    this.evaluation.add((Vector) h.clone());
				}
			}	
		}		
	return (Vector) ii.clone();	
	}	
//*******************************************************************************************************************	
public Vector DeepSearching(String tag,String Original) {
	//System.out.println("DEEP SEARCH ");
	//System.out.println("le meta concept original ="+Original+" le tag dans destination="+tag);
	
		Vector vecteur = new Vector();		
		String[] v = new String(builder_atl).split("rule");
		Vector r_r = new Vector();
		for(String elem : v) {
			if(elem.indexOf("to")>0) {
				r_r.add(elem);
			}
		}		
		for(int i=0; i<r_r.size();i++) {			
				String tmpo = new String((String) r_r.get(i));
				tmpo = tmpo.replace(" ","");
				if(tmpo.indexOf(tag+"<-")>=0) 
					if(((String)this.rules_in_atl.get(i).get(2)).equalsIgnoreCase(Original)) {
						//System.out.println("la regle "+((String)this.rules_in_atl.get(i).get(0)));
						vecteur.add(i);	
					}
							
									
		}		
		return vecteur;		
	}
//***************************************************************************************************	


public String getRuleName(int i ) {	
	return (String) this.rules_in_atl.get(i).get(0);	
}




	// return a vector of vector (in each vector we have : name of the rule + the destination MM + the source MM)
	public Vector<Vector> getRulesOfatl() {			
		Vector<Vector> r = new Vector<Vector>();
		String[] v = new String(this.builder_atl).split("rule");
		Vector r_r = new Vector();
		for(String elem : v) {
			if(elem.indexOf("to")>0) {
				r_r.add(elem);
			}
		}		
		for(int i=0;i<r_r.size();i++) {
			StringBuilder structure = new StringBuilder((String)r_r.get(i));			
			Vector vecteur = new Vector();			
			String rule_name = structure.substring(0,structure.indexOf("{"));			
			String dst_mm = structure.substring(structure.indexOf("!",structure.indexOf("to"))+1,structure.indexOf("(",structure.indexOf("!",structure.indexOf("to"))+1));
			int i1 = structure.indexOf("(",structure.indexOf("!",structure.indexOf("from"))+1);
			int i2 = structure.indexOf("to",structure.indexOf("!",structure.indexOf("from"))+1);
			if(i1 > i2) i1 = i2 ; 
			String src_mm =  structure.substring(structure.indexOf("!",structure.indexOf("from"))+1,i1);
			vecteur.add(rule_name.replace(" ","").replace("\n","").replace("\r",""));
			vecteur.add(dst_mm.replace(" ","").replace("\n","").replace("\r",""));
			vecteur.add(src_mm.replace(" ","").replace("\n","").replace("\r",""));
			r.add((Vector) vecteur.clone());
		}
		return r;  		
	}	
//***************************************************************************************************	
	public Vector delete_duplication_rules(Vector v) {		
		Vector r = new Vector();		
		for(int i=0; i<v.size(); i++) {
			if(v.get(i) != null) {
				int valeur = (int) v.get(i);
				for(int j=0; j<v.size();j++) {
					if(j==i) continue;
					if(v.get(j) != null) {
						int bb = (int) v.get(j);
						if(valeur==bb) v.set(j,null);
					}									
				}
			}					
		}		
		for(int i=0; i<v.size(); i++) 
			if(v.get(i) != null && (((int)v.get(i)) != -1) && (((int)v.get(i)) <this.rules_in_atl.size())) r.add(v.get(i));
		return r;
		}
//***************************************************************************************************	
	public Vector<Vector> compare(StringBuilder out1, StringBuilder out2){		
		int max_index_gauche = out1.length();		
		int i =0;
		Vector<Vector> comparaison = new Vector<Vector>(); 
		while(i<max_index_gauche) {
			Vector<Vector> tmp = new Vector<Vector>();
			int x = out1.indexOf(" "+this.identity+"=\"",i);
			if(x <0) break;
			String variable_x = out1.substring(x+6, out1.indexOf("\"",x+7)+1);
			tmp.add(this.getInfo(variable_x.replace("\"",""), out1, this.xsd));
			int j = out2.indexOf(this.identity+"="+variable_x);
			if(j < 0) tmp.add(null);
			else 
				tmp.add(this.getInfo(variable_x.replace("\"",""), out2,this.xsd));			
			comparaison.add((Vector) tmp.clone());
			i = x + 6;
		}
		
		return comparaison;
	}
//***************************************************************************************************	
	public Vector<Vector> get_left_MetaConcept_list(StringBuilder input_builder){ // input = src_xmi
		int max_index_gauche = input_builder.length();		
		int i =0;
		int j =0;
		Vector<Vector> comparaison = new Vector<Vector>(); 
		while(i<max_index_gauche) {
			j = input_builder.indexOf(" "+this.identity+"=\"",i);
			if(j <0) break;
			String variable_x = input_builder.substring(j+this.identity.length()+3, input_builder.indexOf("\"",j+this.identity.length()+3)+1).replace("\"","").replace(" ","");
			comparaison.add((Vector) this.getInfo2(variable_x, input_builder, this.xsd1).clone());			
			i = j + this.identity.length()+2;
		}		
		return comparaison;		
	}
//***************************************************************************************************	
    // x: est le variable;  ----- output : name,tag,block,index,metamodel
    public Vector<String> getInfo(String x,StringBuilder b, StringBuilder xsd_file){
    	Vector<String> o = new Vector<String>();    
    		o.add(x);
    		int i = b.indexOf(this.identity+"=\""+x+"\"");  
       		int start = b.length()-b.reverse().indexOf("<",b.length()-1-i)-1;
       		b.reverse();
       		int fin = b.indexOf(" ",start+1);
       		String tag = b.substring(start+1,fin);
       		o.add(tag);       		
       	 	if(b.indexOf("</"+tag,start)<0)            	
                o.add(b.substring(b.indexOf("<"+tag,start),b.indexOf("/>",b.indexOf("<"+tag,start))+2));  	
        	else 
        		o.add(b.substring(b.indexOf("<"+tag,start),b.indexOf("</"+tag,start)+tag.length()+3));
        	o.add(start+"");        	
        	int yy = xsd_file.indexOf(" "+this.identity+"=\""+tag+"\" ");
        	int cc = xsd_file.length()-xsd_file.reverse().indexOf("<",xsd_file.length()-1-yy)-1;
        	xsd_file.reverse();
        	o.add(xsd_file.substring(xsd_file.indexOf("type=",cc),xsd_file.indexOf("\"",xsd_file.indexOf("type=",cc)+6)+1));    	
        	//xsd_file.reverse();
        	Vector<Vector> concepts_input_xsd = get_left_MetaConcept_list(this.builder_input);
        	boolean isfinded = false;
        	 for(Vector el : concepts_input_xsd) {				 
				 String dd = ((String)el.get(0)).replace(" " ,"").replace("\"", "");
				 String MM_in_input = ((String)el.get(4)).replace(" ","").replace("\"","");
				 MM_in_input = MM_in_input.substring(MM_in_input.indexOf(":")+1);							 
					 if(dd.equalsIgnoreCase(x)) {	
						 isfinded = true;
					     o.add(MM_in_input);
					     break;
					 }
			 }        	
        	if(!isfinded) o.add("- Generated by ATL -"); 
        	
       
        	return o;
    }	

    
  //***************************************************************************************************	
    // input : x: est le variable;b:src_xmi; xsd_file: src_xsd  ----- output : name,tag,block,index,metamodel
    public Vector<String> getInfo2(String x,StringBuilder b, StringBuilder xsd_file){
    	Vector<String> o = new Vector<String>();
       		o.add(x);
    		int i = b.indexOf(this.identity+"=\""+x+"\"");  
       		int start = b.length()-b.reverse().indexOf("<",b.length()-1-i)-1;
       		b.reverse();
       		int fin = b.indexOf(" ",start+1);
       		String tag = b.substring(start+1,fin);
       		o.add(tag);       		
       	 	if(b.indexOf("</"+tag,start)<0)            	
                o.add(b.substring(b.indexOf("<"+tag,start),b.indexOf("/>",b.indexOf("<"+tag,start))+2));  	
        	else 
        		o.add(b.substring(b.indexOf("<"+tag,start),b.indexOf("</"+tag,start)+tag.length()+3));
        	o.add(start+"");        	
        	int yy = xsd_file.indexOf(" "+this.identity+"=\""+tag+"\" ");
        	int cc = xsd_file.length()-xsd_file.reverse().indexOf("<",xsd_file.length()-1-yy)-1;
        	xsd_file.reverse();
        	
        	String MetaConcept_name = xsd_file.substring(xsd_file.indexOf("type=",cc)+5,xsd_file.indexOf("\"",xsd_file.indexOf("type=",cc)+6)+1).replace("\"","").replace(" ","");
        	
        	
        	boolean ty = false;
        	for(ComplexType ee : this.complextype_list2) {
        		if(ee.getName().replace("\"","").replace(" ","").equalsIgnoreCase(MetaConcept_name.substring(MetaConcept_name.indexOf(":")+1))) {
        			if(ee.isAbstract_elem()) {
        				MetaConcept_name = MetaConcept_name + "__Abstract__";
        				ty=true;
        				break;
        			}
        		}        		
        	}
        	
        	if(ty) {
        		String g = (String) o.get(2);
        		MetaConcept_name = MetaConcept_name + g.substring(g.indexOf("\"",g.indexOf("type="))+1,g.indexOf("\"",g.indexOf("\"",g.indexOf("type="))+1)+1);        		
        	}
        	o.add(MetaConcept_name);    	
        	//xsd_file.reverse();     
        	return o;
    }
 //***************************************************************************************************	
	public void printAtlRules() {		
		for(Vector elem : this.rules_in_atl) {			
			System.out.println("Rule name:"+elem.get(0)+" ---- MM of Source:"+elem.get(2)+" ---- MM of Destination:"+elem.get(1));
		}		
	}	
//***************************************************************************************************	


//***************************************************************************************************	
	
	// for destination MM
	public Vector getIndexOfRule(String MM_name) {
		Vector list_i = new Vector();
		for(int i=0;i<this.rules_in_atl.size();i++) {
			if(((String)this.rules_in_atl.get(i).get(1)).equalsIgnoreCase(MM_name.replace(" ",""))) {				
				list_i.add(i);
			}			
		}
		return list_i;	
	}
	
//***************************************************************************************************	

	
	public Vector getIndex(String src_mc, String dst_mc) {

		Vector list_i = new Vector();
		for(int i=0;i<this.rules_in_atl.size();i++) {			
			String elem1 = ((String) this.rules_in_atl.get(i).get(1)).replace(" ","").replace("\n","").replace("\r","");
			String elem2 = ((String) this.rules_in_atl.get(i).get(2)).replace(" ","").replace("\n","").replace("\r","");
			if(elem1.equalsIgnoreCase(dst_mc) && elem2.equalsIgnoreCase(src_mc))				
				list_i.add(i);			
		}		
		return list_i;		
		
	}
	
	public Vector getIndexTag(String src_mc, String tag_in_to_part) {
		
		Vector vecteur = new Vector();		
		String[] v = new String(builder_atl).split("rule");
		Vector r_r = new Vector();
		for(String elem : v) {
			if(elem.indexOf("to")>0) {
				r_r.add(elem);
			}
		}		
		for(int i=0; i<r_r.size();i++) {			
				String tmpo = new String((String) r_r.get(i));
				tmpo = tmpo.replace(" ","");
				if(tmpo.indexOf(tag_in_to_part+"<-")>=0) 
					if(((String)this.rules_in_atl.get(i).get(2)).equalsIgnoreCase(src_mc)) {
						//System.out.println("la regle "+((String)this.rules_in_atl.get(i).get(0)));
						vecteur.add(i);	
					}
							
									
		}		
		return vecteur;	
		
		
	}
	
	
	
	
	
	// for input MM
		public Vector getIndexOfRule_in(String MM_name) {
			Vector list_i = new Vector();
			for(int i=0;i<this.rules_in_atl.size();i++) {			
				String ee = ((String)this.rules_in_atl.get(i).get(2));
				if(ee.equalsIgnoreCase(MM_name)) {
					list_i.add(i);
				}			
			}
			return list_i;	
		}

	// il va retourner out2 apres la normalisation et rules dans un vecteur   ............out2 and rules        
	public Vector normalization(StringBuilder s) {
		Vector r = new Vector(); 		
		return null;		
	}

//***********************************************************************************************************
	public void log_Results(Vector<Vector> comparaison) {		
		diff_match_patch dmp = new diff_match_patch();
		for(Vector elem : comparaison) {
			this.LOGS.append("\n___________________________________________________________________________");
			this.LOGS.append("\n____________________________\\ Comparaison //______________________________");
			this.LOGS.append("\n___________________________________________________________________________");

			this.LOGS.append("\n\nThe name of the element :"+((Vector)elem.get(0)).get(0));
			if(((Vector)elem.get(1))==null) this.LOGS.append("\n* This element does not exist in out2");
			this.LOGS.append("\nThe Tag in out1 is "+((Vector)elem.get(0)).get(1));
			if(((Vector)elem.get(1))!=null) this.LOGS.append("\nThe Tag in out2 is "+((Vector)elem.get(1)).get(1));
			
			this.LOGS.append("\nThe Meta-Concept of this variable in the INPUT instance (before using atl file) is :"+((Vector)elem.get(0)).get(5));
			this.LOGS.append("\nThe Meta-Concept of this variable in out1 is :"+((Vector)elem.get(0)).get(4));
			if(((Vector)elem.get(1))!=null) this.LOGS.append("\nThe Meta-Concept of this variable in out2 is :"+((Vector)elem.get(1)).get(4));			
			this.LOGS.append("\nThe Structure of this variable in out1 is : "+((Vector)elem.get(0)).get(2));
			if(((Vector)elem.get(1))!=null) this.LOGS.append("\nThe Structure of this variable in out2 is : "+((Vector)elem.get(1)).get(2));		
			if(((Vector)elem.get(1))!=null) {
			this.LOGS.append("\n*** ");
				LinkedList<Diff> diff = dmp.diff_main((String)((Vector)elem.get(0)).get(2),(String)((Vector)elem.get(1)).get(2));
			for(int n=0;n<diff.size();n++) {
				if(diff.get(n).operation!=Operation.EQUAL) {
					this.LOGS.append("--->");
					this.LOGS.append("\n"+diff.get(n));
				}
			}			
			}	
		}		
	}
//***********************************************************************************************************
	public String getMMname() {		
		String s = this.elems_list.get(0).getType().replace("\"","");
		return (String) s.subSequence(0,s.indexOf(":"));		
	}	
//***********************************************************************************************************
	// donner moi les elements qui correspond à un concept X dans le meta-model...mot = tag
   public Vector<String> getParts(StringBuilder xmi, String mot){
   	 Vector<String> o = new Vector<String>();
   	int max = xmi.length();
   	int i = 0 ;     	
   	while(i < max) {        	
       	if(xmi.indexOf("<"+mot,i)<0) break; 
       	if(xmi.indexOf("</"+mot,i)<0) { // le bloc se termine par />
       		o.add(xmi.substring(
               		xmi.indexOf("<"+mot,i),
               		xmi.indexOf("/>",xmi.indexOf("<"+mot,i))+2
               		));       		
               i =  xmi.indexOf("/>",xmi.indexOf("<"+mot,i))+2; 
       	}
       	else {
       		// le bloc se termine par </mot ****************************************added added new ///////////////
       		o.add(xmi.substring(
               		xmi.indexOf("<"+mot,i),
               	    xmi.indexOf("</"+mot,i)+mot.length()+3
               		));
       		i = xmi.indexOf("</"+mot,i)+mot.length()+3;  
       	}
                	
   	}
   	return o ;	
   }
 //***********************************************************************************************************

	public Vector<String> filterOutSideElements(StringBuilder bloc) {
		StringBuilder s = new StringBuilder();
		int i  = bloc.indexOf("<",bloc.indexOf("<"));
		int j = bloc.indexOf("</"+this.getMMname()+":"+this.root+">");
		Vector<String> elements = new Vector<String>();
		while (i < j) {
			StringBuilder elem = new StringBuilder();
			while(!(bloc.substring(i,i+2).equalsIgnoreCase("/>") || bloc.substring(i,i+1).equalsIgnoreCase("</"))) {
				elem.append(bloc.substring(i, i+1)); 
				i++;
			}
			if(bloc.substring(i,i+2).equalsIgnoreCase("/>")) {
				elem.append(bloc.substring(i, i+2)); 
			}else {
				elem.append(bloc.substring(i,bloc.indexOf(">",i)+1)); 
			}				
			elements.add(new String(elem));		
		}		
		return elements;		
	}
//***********************************************************************************************************

	
//***************************************************************************************************	
	
//***************************************************************************************************	
	public int getIndex(Element SubElemOFComplexType) {		
		int index=0;
		for(int i=0;i<elems_list.size();i++) {
			if (elems_list.get(i).getType().equalsIgnoreCase(SubElemOFComplexType.getType())) {
				index = i;
				break;
			}
		}
		return index;
	}
//***************************************************************************************************	
	
	public boolean isExist(String e, Vector<String> vect) {
		for(String elem : vect)
			if(elem.equalsIgnoreCase(e)) return true;
		return false;		
		
	}
	
//***************************************************************************************************	
	

//***************************************************************************************************	
	
//***************************************************************************************************	
public String generateRandomValues(Attribute a) {	
	if(a.getType().contains("EString")) return a.getName().replace("\"","")+"= \""+"RandomValue"+this.rand.nextInt(10000)+"\"";
	if(a.getType().contains("EInt")) return a.getName().replace("\"","")+"= \""+this.rand.nextInt(10000)+"\"";
	if(a.getType().contains("EDouble")) return a.getName().replace("\"","")+"= \""+this.rand.nextDouble()+"\"";
	else {
		return a.getName().replace("\"","")+"="+a.getType();
	}
}
//***********************************************************************************************
Random rand = new Random();
public String findValue(String x,Vector<String> l) {
	for(String e:l) {		
		if(e.contains(x+"=")) {
			int indx = e.indexOf("=");
			return e.substring(indx+1);
		}
	}
	return "";
}
//***********************************************************************************************
public String findValueMM(String x,Vector<String> l) {
	for(String e:l) {		
		if(e.contains(x+":")) {
			int indx = e.indexOf(":");
			return e.substring(indx+1);
		}
	}
	return "";
}
//***********************************************************************************************
public Vector<Element> getElements(Vector<Vector> all){
	 Vector<Element> t = new Vector<Element>();
	for(int i=0;i<all.size();i++) {
		if("<xsd:element".equalsIgnoreCase((String) all.get(i).get(0))) {
			Element e = new Element(findValue("name",all.get(i)),findValue("type",all.get(i)));
			String bool = findValue("ecore:ignore",all.get(i));
			if (bool.equalsIgnoreCase("true")) e.setIgnore(true); else e.setIgnore(false);
			t.add(e);
		}
		if("<xsd:complexType".equalsIgnoreCase((String) all.get(i).get(0))) break;
	}
	return t;
}	

//***********************************************************************************************

public Boolean stringToBoolean(String s) {
	if (s.equalsIgnoreCase("\"true\"")) return true;
	else return false;
}

//*************************je dois ajouter l heritage ici**********************************************************************
public Vector<ComplexType> getComplexTypes(Vector<Vector> all){
	 Vector<ComplexType> t = new Vector<ComplexType>();
	for(int i=0;i<all.size();i++) {
		if("<xsd:element".equalsIgnoreCase((String) all.get(i).get(0))) 
			continue;
		ComplexType tt = new ComplexType();
		if("<xsd:complexType".equalsIgnoreCase((String) all.get(i).get(0))) {
			
			tt.setAbstract_elem(stringToBoolean(findValue("abstract",all.get(i)))); 
			tt.setName(findValue("name", all.get(i)));
			
			if("<xsd:complexContent>".equalsIgnoreCase((String) all.get(i+1).get(0))) {
				tt.isChild = true;
				tt.nameOfParent = findValue("base",all.get(i+1));			
			int j = i;
			while(!("</xsd:complexContent>".equalsIgnoreCase((String) all.get(j).get(0)))) {
				if("<xsd:element".equalsIgnoreCase((String) all.get(j).get(0))) {
					Element e = new Element(findValue("name",all.get(j)),
							findValue("type",all.get(j)),
							findValue("maxOccurs",all.get(j)),
							findValue("minOccurs",all.get(j)));
					e.setResolveProxies(this.stringToBoolean(findValue("ecore:resolveProxies",all.get(j))));
					if(!this.isExist(e.getName(),tt.balises)) tt.balises.add(e.getName());
					tt.getSequence().add(e);
				}
				if("<xsd:attribute".equalsIgnoreCase((String) all.get(j).get(0))) {
					Attribute e = new Attribute(findValue("name",all.get(j)),findValue("type",all.get(j)));
					e.setReference(findValue("ecore:reference",all.get(j)));
					e.setUse(findValue("use",all.get(j)));
					if (!this.findValue("<xsd:simpleType>", all.get(j+1)).equalsIgnoreCase("")) {
					   e.setIsComplexType(true);
					   e.setItemType(this.findValue("itemType", all.get(j+2)));
					}
					tt.getAttributs().add(e);
				}				
				j++;
			}
			}else {
				int j = i;
				while(!("</xsd:complexType>".equalsIgnoreCase((String) all.get(j).get(0)))) {
					if("<xsd:element".equalsIgnoreCase((String) all.get(j).get(0))) {
						Element e = new Element(findValue("name",all.get(j)),
								findValue("type",all.get(j)),
								findValue("maxOccurs",all.get(j)),
								findValue("minOccurs",all.get(j)));
						e.setResolveProxies(this.stringToBoolean(findValue("ecore:resolveProxies",all.get(j))));
						if(!this.isExist(e.getName(),tt.balises)) tt.balises.add(e.getName());
						tt.getSequence().add(e);
					}
					if("<xsd:attribute".equalsIgnoreCase((String) all.get(j).get(0))) {
						Attribute e = new Attribute(findValue("name",all.get(j)),findValue("type",all.get(j)));
						e.setReference(findValue("ecore:reference",all.get(j)));
						e.setUse(findValue("use",all.get(j)));
						if (!this.findValue("<xsd:simpleType>", all.get(j+1)).equalsIgnoreCase("")) {
						   e.setIsComplexType(true);
						   e.setItemType(this.findValue("itemType", all.get(j+2)));
						}
						tt.getAttributs().add(e);
					}					
					j++;
				}
			}
			t.add(tt);		
		}		
	}
	return t;	
}


//***********************************************************************************************************

public String getRandomString() {	
	 byte[] array = new byte[rd.nextInt(10)];new Random().nextBytes(array);
	 return new String(array, Charset.forName("UTF-8"));
}

//***********************************************************************************************************


public void doMetaData(Vector<Vector> all) {
	StringBuilder s = new StringBuilder();
	StringBuilder d = new StringBuilder(findValueMM("xmlns",all.get(1)));	
	String MM = d.substring(0,d.indexOf("="));		
	s.append(getString(all.get(0),0,all.get(0).size()-1)+" ?>");  
	s.append("\n"+"<"+MM+":"+this.root+" xmi:version=\"2.0\""+"\n xmlns:xmi=\"http://www.omg.org/XMI\" \n "
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+"\n xmlns:"+d);
	this.MetaData = new String(s);
	this.MM = new String(MM);	
}
//***********************************************************************************************************

public String getString(Vector<String> vect,int i,int j) {	
	StringBuilder s = new StringBuilder();
	for(int k=i;k<j;k++)
		if(k<j) s.append(vect.get(k)+" ");	
	return new String(s);	
}
//***********************************************************************************************************

public String searchValue(String id, Vector<Vector> vect) {	
	for(int i=0;i<vect.size();i++) {
		String s = findValue(id,vect.get(i));
		if (!s.isEmpty()) return s; 
	}
	return "";
}
//***********************************************************************************************************

public String getMetaData() {
	return MetaData;
}
//***********************************************************************************************************

public void setMetaData(String metaData) {
	MetaData = metaData;
}
//***********************************************************************************************************

public Vector<Vector> getAllRows() {
		return allRows;
}
//***********************************************************************************************************


public void setAllRows(Vector<Vector> allRows) {
		this.allRows = allRows;
}
//***********************************************************************************************************


public File getFichier() {
	return fichier;
}
//***********************************************************************************************************


public void setFichier(File fichier) {
		this.fichier = fichier;
}
//***********************************************************************************************************


	public Vector<Element> getElems_list() {
		return elems_list;
	}


	public void setElems_list(Vector<Element> elems_list) {
		this.elems_list = elems_list;
	}


	public Vector<ComplexType> getComplextype_list() {
		return complextype_list;
	}


	public void setComplextype_list(Vector<ComplexType> complextype_list) {
		this.complextype_list = complextype_list;
	}






	public String getPath_ecore() {
		return path_ecore;
	}






	public void setPath_ecore(String path_ecore) {
		this.path_ecore = path_ecore;
	}






	public String getPath_out1() {
		return path_out1;
	}






	public void setPath_out1(String path_out1) {
		this.path_out1 = path_out1;
	}






	public String getPath_out2() {
		return path_out2;
	}






	public void setPath_out2(String path_out2) {
		this.path_out2 = path_out2;
	}






	public String getRoot() {
		return root;
	}






	public void setRoot(String root) {
		this.root = root;
	}

}
//*********************************************************************************************
class ComplexType {
	
	private Vector<Element> sequence;
	
	private Vector<Attribute> attributs;

	
	public Vector<String> balises;
	
	boolean isChild = false;
	
	private String name;
	
	String nameOfParent;
	
	private boolean abstract_elem = false;
	
	public ComplexType() {
		sequence = new Vector<Element>();
		attributs = new Vector<Attribute>();
		balises = new Vector<String>();
	}
	
	
	
	
	
	
	

	public String getName() {
		return name;
	}








	public void setName(String name) {
		this.name = name;
	}








	public Vector<String> getBalises() {
		return balises;
	}


public String getBalises_string() {
	
	
	StringBuilder b = new StringBuilder();
	
	for(String elem : this.balises) b.append(elem+";");
	
	return new String(b);
	
	
}





	public void setBalises(Vector<String> balises) {
		this.balises = balises;
	}








	public boolean isAbstract_elem() {
		return abstract_elem;
	}








	public void setAbstract_elem(boolean abstract_elem) {
		this.abstract_elem = abstract_elem;
	}








	public Vector<Element> getSequence() {
		return sequence;
	}

	public void setSequence(Vector<Element> sequence) {
		this.sequence = sequence;
	}

	public Vector<Attribute> getAttributs() {
		return attributs;
	}

	public void setAttributs(Vector<Attribute> attributs) {
		this.attributs = attributs;
	} 
	
	
	
	
	
	
	
	
}






//*********************************************************************************************
class Element {
	// le nom de MM ecore
	private String EcoreName;
	private String name;
	private String type;
	private String maxOccurs="unbounded", minOccurs="0";
	private Boolean ignore,resolveProxies;

	
	
	public Element(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}
	public Element( String name, String type, String maxOccurs, String minOccurs) {
		
		this.name = name;
		this.type = type;
		this.maxOccurs = maxOccurs;
		this.minOccurs = minOccurs;
	}
	public String getEcoreName() {
		return EcoreName;
	}
	public void setEcoreName(String ecoreName) {
		EcoreName = ecoreName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMaxOccurs() {
		return maxOccurs;
	}
	public void setMaxOccurs(String maxOccurs) {
		this.maxOccurs = maxOccurs;
	}
	public String getMinOccurs() {
		return minOccurs;
	}
	public void setMinOccurs(String minOccurs) {
		this.minOccurs = minOccurs;
	}
	public Boolean getIgnore() {
		return ignore;
	}
	public void setIgnore(Boolean ignore) {
		this.ignore = ignore;
	}
	public Boolean getResolveProxies() {
		return resolveProxies;
	}
	public void setResolveProxies(Boolean resolveProxies) {
		this.resolveProxies = resolveProxies;
	}
		
}
//***********************************************************************************************************

class Attribute {
	
	private String reference,use,itemType;
	
	private Boolean isComplexType; // si on a xsd:list of SimpleType, alors c'est true
	
	
	public Attribute(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	private String name, type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public Boolean getIsComplexType() {
		return isComplexType;
	}

	public void setIsComplexType(Boolean isComplexType) {
		this.isComplexType = isComplexType;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
}