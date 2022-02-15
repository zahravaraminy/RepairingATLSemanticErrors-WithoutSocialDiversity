package jmetal.problems.AdaptiveInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import jmetal.encodings.solutionType.IntSolutionType;

public class CoSolution {
	//public static ArrayList<Rule> rules = new ArrayList<Rule> ();
	
	public static ArrayList<Integer> ind1 = new ArrayList<Integer>();
	public static ArrayList<Integer> ind2 = new ArrayList<Integer>();
	public static ArrayList<Integer> ind3 = new ArrayList<Integer>();
	 public static ArrayList <String> perfect_rule = new  ArrayList <String>();
	public static int NumberOfVaraibles ;
	int min_rules_size = 5;
	int max_rules_size = 10; 
	public Rule create_rule(Input input)
	{int source_index;
	int source_index0;
	int source_index1;
	int source_index2;
	int target_index;
	
	Random number_generator = new Random();
		source_index = number_generator.nextInt(Input.Context().length);
		ind1.add(source_index);
		source_index0 = number_generator.nextInt(Input.ValuesOfContext().length);
		source_index1 = number_generator.nextInt(Input.Metrics().length);
		ind2.add(source_index1);
		source_index2 = number_generator.nextInt(Input.Operator().length);
		target_index = number_generator.nextInt(Input.Problem().length);
		ind3.add(target_index);
		// System.out.println(source_index1);
		Rule temp = new Rule();
		temp.src = Input.Context()[source_index];
		temp.src0 = Input.ValuesOfContext()[source_index0];
		temp.src2 = Input.Metrics()[source_index1];
		temp.src3 = Input.Operator()[source_index2];
		temp.src4 = Input.Val_Metric();
		temp.trg = Input.Problem()[target_index];
		// print_metamodel(temp.src,temp.src0,temp.src2,temp.trg);

		temp.print_rule();
		//rules.add(temp);
		
		//System.out.println(temp.rule_text);
		return temp;
		
	}
	public Rule create_rule2(Input input)
	{int source_index;
	int source_index0;
	int source_index1;
	int source_index2;
	int target_index;
	Random number_generator = new Random();
		source_index = number_generator.nextInt(Input.Context().length);
		ind1.add(source_index);
		source_index0 = number_generator.nextInt(Input.ValuesOfContext().length);
		source_index1 = number_generator.nextInt(Input.Metrics().length);
		ind2.add(source_index1);
		source_index2 = number_generator.nextInt(Input.Operator().length);
		target_index = number_generator.nextInt(Input.Problem().length);
		ind3.add(target_index);
		// System.out.println(source_index1);
		Rule temp = new Rule();
		temp.src = Input.Context()[source_index];
		temp.src0 = Input.ValuesOfContext()[source_index0];
		temp.src2 = Input.Metrics()[source_index1];
		temp.src3 = Input.Operator()[source_index2];
		temp.src4 = Input.Val_Metric();
		temp.trg = Input.Problem()[target_index];
		// print_metamodel(temp.src,temp.src0,temp.src2,temp.trg);

		temp.print_rule();
		//rules.add(temp);
		
		System.out.println(temp.rule_text);
		return temp;
		
	}
	
	
	public void create_solution(Input r, int value) {
		int rules_size;
		int source_index;
		int source_index0;
		int source_index1;
		int source_index2;
		int target_index;
		Random number_generator = new Random();
		
		for (int i = 0; i < value; i++) {
			source_index = number_generator.nextInt(Input.Context().length);
			ind1.add(source_index);
			source_index0 = number_generator.nextInt(Input.ValuesOfContext().length);
			source_index1 = number_generator.nextInt(Input.Metrics().length);
			ind2.add(source_index1);
			source_index2 = number_generator.nextInt(Input.Operator().length);
			target_index = number_generator.nextInt(Input.Problem().length);
			ind3.add(target_index);
			// System.out.println(source_index1);
			Rule temp = new Rule();
			temp.src = Input.Context()[source_index];
			temp.src0 = Input.ValuesOfContext()[source_index0];
			temp.src2 = Input.Metrics()[source_index1];
			temp.src3 = Input.Operator()[source_index2];
			temp.src4 = Input.Val_Metric();
			temp.trg = Input.Problem()[target_index];
			// print_metamodel(temp.src,temp.src0,temp.src2,temp.trg);

			temp.print_rule();
			//rules.add(temp);
			System.out.println(temp.rule_text);
		}
	
	}
	public void Mymutation(int value )
	 {    
		

		 if (value >= IntSolutionType.operations_size) {
			 System.out.println("ce fait !!!!!");
			 value = IntSolutionType.operations_size-1;}
		  Random number_generator = new Random();
	      int nb = 0;
	      nb = (int) (Math.random() * 2 ); 
	     // System.out.println("nombre à choisir : "+nb);
	      System.out.println("mutation point: "+value);
	      System.out.println("size of rules : "+IntSolutionType.operations.size());
	      if (nb==0)
	      {
	      Rule temp = new Rule();
	      int source_index1 = number_generator.nextInt(Input.ValuesOfContext().length);
	      int source_index3 = number_generator.nextInt(Input.Operator().length);
	      temp.src =  Input.Context()[ind1.get(value)];
	      temp.src0 = Input.ValuesOfContext()[source_index1];
	      temp.src2 = Input.Metrics()[ind2.get(value)];
	      temp.src3 = Input.Operator()[source_index3];
	      temp.src4 = Input.Val_Metric();
	      temp.trg =  Input.Problem()[ind3.get(value)];
	      temp.print_rule();
	      }
	     } 
	public Rule [] MyCrossover (ArrayList<Rule> parent1, ArrayList<Rule> parent2 , int min_rules_size )
	{
			Rule Tab [] = new  Rule [2] ;	
			Rule temp_rule = new Rule();
			ArrayList<Rule> Array_temp = new ArrayList<Rule>();
			ArrayList<Rule> Array_temp2 = new ArrayList<Rule>();
			//temp_rule = parents.get(i).rules.get(first_rule_index);
			//temp_rule = parents.get(i).rules.get(1);
			for (int j = 0; j < min_rules_size-1 ; j++){ 
				temp_rule = parent1.get(j);
				Array_temp.add(temp_rule);
				}
			for (int j = 0; j < min_rules_size-1 ; j++){ 
				temp_rule = parent2.get(j);
				Array_temp2.add(temp_rule);
				}
			for(int h= 0; h<min_rules_size-1;h++)
			{parent1.set(h, Array_temp2.get(h));
			//System.out.println("parent 1 : "+parent1.get(h).rule_text);
			} 
			
			for(int h= 0; h<min_rules_size-1;h++)
			{parent2.set(h, Array_temp.get(h));
			//System.out.println("parent 2 : "+parent2.get(h).rule_text);
			}
			return null;
	}
	public double fitness_1()
	{   
		double fitt = 0.0 ;
	    fitt = IntSolutionType.operations.size();     //(double)((int)rules.size())/((int)100);
		System.out.println("fit1"+IntSolutionType.operations.size());
		return fitt ;
		
	}
	public double fitness_2()
	{
		double fit2 = 0.0;
		double []  Nbr_Occ_Trace =new double[16];
		double []  Nbr_Occ_Solution = new double[16]; 
	
		                                           //density unity regularity sequence  grouping  simplicity
		String [][]  Interface_Metric = {{"InterfaceA","0.23","0.55","0.78","0.52","0.47","0.98"},
		                                 {"InterfaceB","0.41","0.76","0.54","0.45","0.19","0.72"},
		                                 {"InterfaceC","0.65","0.82","0.50","0.51","0.72","0.44"},
		                                 {"InterfaceD","0.56","0.18","0.42","0.48","0.54","0.50"},
		                                 {"InterfaceE","0.18","0.56","0.87","0.71","0.23","0.90"},
		                                 {"InterfaceF","0.85","0.46","0.27","0.21","0.67","0.28"},
		                                 {"InterfaceG","0.93","0.64","0.35","0.67","0.38","0.14"},
		                                 {"InterfaceH","0.71","0.15","0.20","0.36","0.23","0.14"},
		                                 {"InterfaceI","0.63","0.70","0.89","0.04","0.21","0.26"},
		                                 {"InterfaceJ","0.48","0.15","0.64","0.38","0.84","0.90"},
		                                 {"InterfaceK","0.03","0.54","0.72","0.49","0.32","0.55"},
		                                 {"InterfaceL","0.32","0.29","0.01","0.37","0.04","0.79"},
		                                 {"InterfaceM","0.12","0.45","0.24","0.50","0.70","0.44"},
		                                 {"InterfaceN","0.30","0.15","0.52","0.58","0.33","0.54"},
		                                 {"InterfaceO","0.54","0.24","0.67","0.20","0.85","0.90"}
		                                 };       //new double[7][];
		 int k=1;
		 int t=1;
		 String nomFichier = "D:/perfect_rules.txt";
		 String val01 = null  ;
	//	 Workbook workbook = null;
		return t;
			
	}
	public static void main(String[] args) throws FileNotFoundException,
	IOException {
		Input r = new Input ();
		int  min_rules_size = 5;
		ArrayList <Rule> parent1 = new ArrayList <Rule>();
		ArrayList <Rule> parent2 = new ArrayList <Rule>();
	    
		CoSolution S = new  CoSolution ();
		for(int i = 0 ; i< 5 ;i++)
		{parent1.add(S.create_rule2(r));}
		for(int i = 0 ; i< 5 ;i++)
		{System.out.println("parent 1 : "+parent1.get(i).rule_text);}
		
		for(int i = 0 ; i< 5 ;i++)
		{parent2.add(S.create_rule2(r));}
		for(int i = 0 ; i< 5 ;i++)
		{System.out.println("parent 2 : "+parent2.get(i).rule_text);}
		S.MyCrossover(parent1, parent2, min_rules_size);
		
	}
}
