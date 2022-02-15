package cd.udem.fixingatlerror;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import anatlyzer.atl.util.ATLSerializer;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.util.PseudoRandom;
import ruletypesmm.Rule;
import ruletypesmm.Trafo;
import ruletypesmm.RuletypesmmPackage;





public class CoSolutionThreeStep {

	  ArrayList<Integer> operations ;
	  List<List<String>> editedmetamodellist = new ArrayList<List<String>>();
	  private int secondfit;
	  private int Thirdfit;
	  List<String> rowone ;
	  public static int check=0;
	  static int min_solution_size = 1 ;
	  static int max_solution_size = 4 ;
	  OperationsThreeStep op;
	  public OperationsThreeStep getOp() {
		return op;
	}
    public int getsecondfit() {
    	return secondfit;
    }
    public void setsecondfit(int i) {
    	 secondfit=i;
    }
    public int getThirdfit() {
    	return Thirdfit;
    }
    public void setThirdfit(int i) {
    	 Thirdfit=i;
    }
	
	public void setOp(OperationsThreeStep op) {
		this.op = op;
	}

		// these values represents the existing  operations
	    public static int min_operations_interval = 1 ;
	    public  static int max_operations_interval = 11 ;// bod 7
	    public Window w;
	   
	
	 int numberofoperations;
	 int solutionsize;
	 
	 
	 static int objectives_number = 2;// bod
	    ArrayList<Double> objectives ;
	    ArrayList<String> objectives_names ;
	 
	 int nberrors;
	 
	 int coverage;
	 
	 int idealpoint;
	 int effort ;
	 int rank ;
	 double dominance ;
	 double distance ;
	 double crowding_distance;
	 
	 
	 public CoSolutionThreeStep() 
	    {
	    	try {
				this.op=new OperationsThreeStep();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 this.w=new Window();
		 
		 	this.operations = new ArrayList<Integer>();
	    	
	    	this.numberofoperations=0;
	    	this.nberrors=0;
	    	this.solutionsize=0;
	    	this.idealpoint=0;
	    	this.dominance = 0 ;
	        this.distance = 0 ;
	        this.crowding_distance = 0 ;
	        this.objectives = new ArrayList<Double>() ;
	        this.objectives_names = new ArrayList<String>() ;
	 
	    }
	    
	    CoSolutionThreeStep(CoSolutionThreeStep a) throws Exception
	    {
	    	this.op= a.op;
	    	this.operations = new ArrayList<Integer>(a.operations);
	    	
	    	 this.w=new Window();
	    	this.numberofoperations=a.numberofoperations;
	    	this.nberrors=a.nberrors;
	    	this.solutionsize=a.solutionsize;
	    	this.idealpoint=a.idealpoint;
	    	 this.rank = a.rank ;
	         this.dominance = a.dominance ;
	         this.distance = a.distance ; 
	         this.crowding_distance = a.crowding_distance ;
	         this.objectives = new ArrayList<Double>() ;
	         
	         for(int i=0;i<a.objectives.size();i++)
	         {
	             this.objectives.add(a.objectives.get(i)) ;
	         }
	         
	         this.objectives_names = new ArrayList<String>() ;
	         
	         for(int i=0;i<a.objectives_names.size();i++)
	         {
	             this.objectives_names.add(a.objectives_names.get(i)) ;
	         }
	         
	    }
	    
	    
	    void create_solution() throws IOException
	    {
	        // creation of a sequence of operations
	    	//solutionsize = Operations.random(min_solution_size, max_solution_size);
	    	Random r = new Random();
	    	 solutionsize = min_solution_size + r.nextInt(max_solution_size - min_solution_size);

	    	System.out.println("le nb aleatoire de la taille de la solution est : "+solutionsize);
	    
	        for(int i=0;i<solutionsize;i++)
	        {
	        	// set of operations on the ATL Rules.
	            //int numoperation=Operations.random(min_operations_interval, max_operations_interval);
	        	
	        	//int numoperation = min_operations_interval + r.nextInt(max_operations_interval - min_operations_interval);
	        	int numoperation=PseudoRandom.randInt(min_operations_interval,max_operations_interval);
	        	System.out.println("le num aleatoire de l operation est : "+numoperation);
	    	    
	            // operations contains the number of the operation to apply
	        	
	            this.operations.add(numoperation);
	           
	           // this.parameters.add(op.addparameters(numoperation));
	         
	        } 
	
	        
	    }	
	
	    void evaluate_solution() throws Exception
	    {
		 int tab[]=new int[5];
		 int[]temp=new int[2];
		
	        this.objectives = new ArrayList<Double>() ;
	        this.objectives_names = new ArrayList<String>() ;
	    
	
		 for(int i=0;i<this.operations.size();i++)
	        {
	            
	        
		     System.out.println("fin de l'execution de l'operation:  "+this.operations.get(i));
		                
	        } 
	        System.out.println("fin de l'execution de toutes les operations  ");
	    	
	        ATLSerializer.serialize(this.op.analyser.getATLModel(),  "examples/class2rel/trafo/newcl2rel.atl");  
	        this.nberrors=op.analyser.getATLModel().getErrors().getNbErrors();  	
	        System.out.println("errors= "+op.analyser.getATLModel().getErrors().getNbErrors());
	    	this.solutionsize=operations.size();
	
		    System.out.println("*********************Fin execution de la Solution *****************");
		     System.out.println("errors= "+this.nberrors);
		     System.out.println("coverage= "+this.coverage);
		     System.out.println("size of the solution= "+this.solutionsize);
		     System.out.println("**************************************");
   	
		     // au lieu de la methode all_metrics
		     objectives.add((double)this.solutionsize);
		     this.objectives_names.add("solutionsize");
		     objectives.add((double)this.nberrors);
		     this.objectives_names.add("nberrors");
		     objectives.add((double)this.coverage);
		     this.objectives_names.add("coverage");
	    }
	    
	
	    public   int  Coverage(EMFModel atlModel2)
	    {
	    	check=1;
	    	Setting setting=new Setting();
			 
	    	this.w.getSMClick( setting.getnewoutputresult()+NSGAII.indexmodeltransformation+"/finalresult"+Class2Rel.totalnumber+".atl",atlModel2);
	    	check=1;
	    	List<String> secondfoot=  footprintscalcul("");
	   this.editedmetamodellist.clear();
	   NSGAII.argumentlist.clear();
	    editedmetamodellist = new ArrayList<List<String>>();
		 NSGAII.argumentlist=new ArrayList<String>();
	    return (((setting.getmetamodellist().size())-(secondfoot.size())));

	    }
	    
	    void displaysolution()
	    {
		 for(int i=0;i<operations.size();i++)
	        {
	            
	           System.out.print ("*******Operations to execute ***** :  "+this.operations.get(i)+" : ");
	           switch (this.operations.get(i)) {
				case 1:
					System.out.println("CreateInitialState "); break;
				case 2:
					System.out.println("CreateSimpleState ");break;
				case 3:
					System.out.println("CreateFinalState ");break;
				case 4:
					System.out.println("CreateSuccessorSimple ");break;
				case 5:
					System.out.println("CreatePredeccessorSimple ");break;
				case 6:
					System.out.println("CreatePredeccessortoInitial ");break;
				case 7:
					System.out.println("CreateSuccessortoFinal ");break;
				case 8:
					
					System.out.println("CreateSuccessorInitial ");break;
				case 9:
					System.out.println("CreatePredeccessorFinal ");break;
				case 10:
					System.out.println("DeleteInitialState ");break;
				case 11:
					System.out.println("DeleteSimpleState ");break;
				case 12:
					System.out.println("DeleteFinalState ");break;
				case 13:
					System.out.println("RemoveSuccessorSimple ");break;
				case 14:
					System.out.println("RemovePredeccessorSimple ");break;
				case 15:
					System.out.println("RemovePredeccessortoinitial ");break;
				case 16:
					System.out.println("RemoveSuccessortoFinal ");break;
				case 17:
					System.out.println("RemoveSuccessorInitial ");break;
				case 18:
					System.out.println("RemovePredecessorFinal ");break;

				}
	        } 
	    	
	    }
	 
		 
	 public List<String> footprintscalcul (String Typesmmpath)
	 {	
		 Typesmmpath="tempTypesExtracted.xmi";
		 RuletypesmmPackage.eINSTANCE.eClass();
	 
		 Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		    Map<String, Object> m = reg.getExtensionToFactoryMap();
		    m.put("xmi", new XMIResourceFactoryImpl());

		    // Obtain a new resource set
		    ResourceSet resSet = new ResourceSetImpl();
		   
		    // Get the resource
		   Resource resource = resSet.getResource(URI
		      .createURI(Typesmmpath), true);
		     Trafo trafo = (Trafo) resource.getContents().get(0);
		    EList<Rule> r1 =trafo.getRules();
  		    Setting s=new Setting();
		    List<String> atllist=union(r1);
		    List<String> coveragelist=intersection(s.getmetamodellist(),atllist);
		 return coveragelist;
	 }
	 
	 
	  public <String> List<String> union(EList<Rule> r1) {
		
		  List<String> list1;
		    List<String> list2;
		  
		  Set<String> set = new HashSet<String>();

		  for(int i=0;i<r1.size();i++)
		  { 
			  list1= (List<String>) r1.get(i).getAllFootPrints();
			  set.addAll(list1);
	       // set.addAll(list2);
		  }
	        return new ArrayList<String>(set);
	    }

	  
	public List<String> intersection4(List<String>  list1, List<String>  list2, int i, List<String>  list3) {
	        List<String> list = new ArrayList<String>();
            
	        for (int j=0;j<list1.size();j++) {
	        	
	        	if(NSGAII.argumentlist.get(j).equals(String.valueOf(i))) {
	        		if(list2.contains(NSGAII.argumentlist.get(j+1)) && !list3.contains(NSGAII.argumentlist.get(j+1))) {
	        			list.add(NSGAII.argumentlist.get(j+1));
	        		}
	        		
	        	}
	    	    j=j+1;
	            
	        }
	                return list;
	    }

	  public List<String> intersection3(List<String>  list1, List<String>  list2) {
	        List<String> list = new ArrayList<String>();

	        for (int j=0;j<list1.size();j++) {
	        	
	        	int indexfound = list1.get(j).indexOf(".");
	    	    
	    	    if (indexfound > -1) 
	    	    {
	    	    	String extensionRemoved = ( list1.get(j)).split("\\.")[1];
	    	    	boolean check=false;
	    	    	for(int i=0;i<list2.size();i++)
	    	    	{
	    	    		
						int indexfound2 =  list2.get(i).indexOf("."+extensionRemoved);
	    	    		 if (indexfound2>-1)
	    	    		    check=true;  
	    	    		 
	    	    			
	    	    	}
	    	    	if (check==false)
	    	    		 list.add(list1.get(j));
	    	        
	    	    }
	    	    else {
	            if(!list2.contains(list1.get(j))) {
	                list.add(list1.get(j));
	            }
	    	    }
	        }
	        return list;
	    }
	  public <T> List<T> intersection2(List<T> list1, List<T> list2) {
	        List<T> list = new ArrayList<T>();

	        for (T t : list1) {
	        	
	        	int indexfound = (  (String) t).indexOf(".");
	    	    System.out.println(indexfound);
	    	    if (indexfound > -1) 
	    	    {
	    	    	String extensionRemoved = ( (String) t).split("\\.")[1];
	    	    	for(int i=0;i<list2.size();i++)
	    	    	{
	    	    		 @SuppressWarnings("unchecked")
						int indexfound2 = ((List<String>) list2.get(i)).indexOf("."+extensionRemoved);
	    	    		 if (indexfound2>-1)
	    	    			 list.add(t);
	    	    	}
	    	        
	    	    }
	    	    else {
	            if(!list2.contains(t)) {
	                list.add(t);
	            }
	    	    }
	        }

	        return list;
	    }
	  public <T> List<T> intersection5(List<T> list1, List<T> list2) {
	        List<T> list = new ArrayList<T>();

	        for (T t : list1) {
	            if(!list2.contains(t)) {
	                list.add(t);
	            }
	        }

	        return list;
	    }
	    public <T> List<T> intersection(List<T> list1, List<T> list2) {
	        List<T> list = new ArrayList<T>();

	        for (T t : list1) {
	            if(list2.contains(t)) {
	                list.add(t);
	            }
	        }

	        return list;
	    }
	 
	 
	 
	 
	 
	 
	 ///////////////////////////////For NSGA 2
	 
	 void mutation1()
	    {
	        
		 
		 
		 int numoperation=Operations.random(min_operations_interval, max_operations_interval);
         
         int position = Operations.random(0, (this.operations.size()-1));
         System.out.println("la position a changer est "+position+" qui est :"+operations.get(position));

         this.operations.set(position, numoperation);
  	        
	     //   } 
	    }
	 void mutation2()
	    {
	        
		 System.out.println("a modifier");
		 
		 int numoperation=Operations.random(min_operations_interval, max_operations_interval);
      
      int position = Operations.random(0, (this.operations.size()-1));
      System.out.println("la position a changer est "+position+" qui est :"+operations.get(position));

      this.operations.set(position, numoperation);
	        
	     //   } 
	    }
	
	 void print_metrics()
	    {
	        System.out.println("\n Rank :  "+this.rank+" Distance : "+this.distance+"\n");
	        for(int i=0;i<this.objectives.size();i++)
	        {
	            System.out.print(" "+objectives_names.get(i)+" : "+this.objectives.get(i));
	        }
	        System.out.println("\n");
	    }
	    
	    String objectives_names_to_string()
	    {
	        String result = " ";
	        for(int i=0;i<this.objectives_names.size();i++)
	        {
	            result+=(String)objectives_names.get(i)+" ";
	        }
	        return result ;
	    }
	    
	    void print_solution()
	    {
	        System.out.println("\n --- Printing solution operations ---");
	        System.out.println("\n --- Rank : "+this.rank);
	        for(int i=0;i<this.operations.size();i++)
	        {
	        	 }
	    }
	    
	    String objectives_values_to_string()
	    {
	        String result = " ";
	        for(int i=0;i<this.objectives.size();i++)
	        {
	            result+=Double.toString(objectives.get(i));
	            
	            if(i != (this.objectives.size()-1))
	            {
	                result+=" , ";
	            }
	            
	        }
	        return result ;
	    }
	 /////////////FIN FOR NSGA 
	 
	 
}
