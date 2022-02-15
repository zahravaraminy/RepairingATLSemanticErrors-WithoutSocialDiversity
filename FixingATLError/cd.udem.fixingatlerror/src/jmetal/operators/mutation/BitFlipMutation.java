//  BitFlipMutation.java
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

package jmetal.operators.mutation;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.encodings.solutionType.IntSolutionTypeThreeStep;
import jmetal.encodings.variable.Binary;
import jmetal.metaheuristics.nsgaII.NSGAIIThreeStep;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.eclipse.emf.ecore.EClassifier;
import anatlyzer.atlext.ATL.Binding;
import cd.udem.fixingatlerror.CoSolution;
import cd.udem.fixingatlerror.CoSolutionThreeStep;

/**
 * This class implements a bit flip mutation operator.
 * NOTE: the operator is applied to binary or integer solutions, considering the
 * whole solution as a single encodings.variable.
 */
public class BitFlipMutation extends Mutation {
  /**
   * Valid solution types to apply this operator 
   */
  private static final List VALID_TYPES = Arrays.asList(BinarySolutionType.class,
      BinaryRealSolutionType.class,
      IntSolutionType.class,IntSolutionTypeThreeStep.class) ;

  private Double mutationProbability_ = null ;
  
	/**
	 * Constructor
	 * Creates a new instance of the Bit Flip mutation operator
	 */
	public BitFlipMutation(HashMap<String, Object> parameters) {
		super(parameters) ;
  	if (parameters.get("probability") != null)
  		mutationProbability_ = (Double) parameters.get("probability") ;  		
	} // BitFlipMutation

	/**
	 * Perform the mutation operation
	 * @param probability Mutation probability
	 * @param solution The solution to mutate
	 * @throws JMException
	 */
	public void doMutation(double probability, Solution solution) throws JMException {
		try {
			if ((solution.getType().getClass() == BinarySolutionType.class) ||
					(solution.getType().getClass() == BinaryRealSolutionType.class)) {
				for (int i = 0; i < solution.getDecisionVariables().length; i++) {
					for (int j = 0; j < ((Binary) solution.getDecisionVariables()[i]).getNumberOfBits(); j++) {
						if (PseudoRandom.randDouble() < probability) {
							((Binary) solution.getDecisionVariables()[i]).bits_.flip(j);
						}
					}
				}

				for (int i = 0; i < solution.getDecisionVariables().length; i++) {
					((Binary) solution.getDecisionVariables()[i]).decode();
				}
			} // if
			else { // Integer representation
				ArrayList<Integer> numbers1 = new ArrayList<Integer>();
				ArrayList<Integer> indexmutation = new ArrayList<Integer>();
				ArrayList<Integer> operationmutation = new ArrayList<Integer>();
				ArrayList<Integer> operationoriginal = new ArrayList<Integer>();
			for (int i = 0; i <solution.getoperations().size() ; i++  )	
		{
				
				
				if (PseudoRandom.randDouble() < 0.15) //.getDecisionVariables().length; i++)
		
	{
		boolean checksecondmutant=false;
			if (checksecondmutant==false) 
					{  
						numbers1.clear();
						for(int jj=0;jj<i;jj++)
							numbers1.add(solution.operation().get(jj));
						for(int jj=i+1;jj<solution.operation().size();jj++)
							numbers1.add(solution.operation().get(jj));
						
						int  value =  PseudoRandom.randInt( 0,  (int)solution.operation().size()/2);
						
							 if (value >= IntSolutionType.operations_size) {
								 //System.out.println("ce fait !!!!!");
								 value = IntSolutionType.operations_size-1;}
						      int nb = 0;
						      nb = (int) (Math.random() * 2 ); 
						      if(value >= solution.operation().size()) { nb = 0 ; } 
						      nb=0;// khodam add kardam
						      boolean check=false;
						      if (nb==0)
						      {
						    	  int numoperation = -10; 
						    	  boolean chh=false;
						    	  while (chh==false) {
						    	  Random randomGenerator = new Random();
						    	  numoperation = CoSolutionThreeStep.min_operations_interval + randomGenerator.nextInt(CoSolutionThreeStep.max_operations_interval - CoSolutionThreeStep.min_operations_interval+1);
						    	 if(!NSGAIIThreeStep.forbiddenoperations.contains(numoperation))
						    	    chh=true;	 
						    	  }
						    	  solution.getlistmutation().add(solution.operation().get(i));
						    	   solution.getlistmutation().add(numoperation);
						    	   
						    	   switch(i+1) {
				          		   case 1:
				          			 solution.setoldrandomIntoperation1(-1);
				          			solution.setreplacerandomIntoperation1(-1);
				          			 solution.setsecondoldrandomIntoperation1(-1); 
				              		   break;
				          		   case 2:
				          			 solution.setoldrandomIntoperation2(-1);
				          			solution.setreplacerandomIntoperation2(-1);
				          			solution.setsecondoldrandomIntoperation2(-1);
				              		   break;
				          		   case 3:
				          			 solution.setoldrandomIntoperation3(-1);
				          			solution.setreplacerandomIntoperation3(-1);
				          			solution.setsecondoldrandomIntoperation3(-1);
				              		   break;
				          		   case 4:
				          			 solution.setoldrandomIntoperation4(-1);
				          			solution.setreplacerandomIntoperation4(-1);
				          			solution.setsecondoldrandomIntoperation4(-1);
				              		   break;
				          		   case 5:
				          			 solution.setoldrandomIntoperation5(-1);
				          			solution.setreplacerandomIntoperation5(-1);
				          			solution.setsecondoldrandomIntoperation5(-1);
				              		   break; 
				          		 case 6:
				          			 solution.setoldrandomIntoperation6(-1);
				          			solution.setreplacerandomIntoperation6(-1);
				          			solution.setsecondoldrandomIntoperation6(-1);
				              		   break; 
				          		 case 7:
				          			 solution.setoldrandomIntoperation7(-1);
				          			solution.setreplacerandomIntoperation7(-1);
				          			solution.setsecondoldrandomIntoperation7(-1);
				              		   break; 
				          		case 8:
				          			 solution.setoldrandomIntoperation8(-1);
				          			solution.setreplacerandomIntoperation8(-1);
				          			solution.setsecondoldrandomIntoperation8(-1);
				              		   break; 
				          		case 9:
				          			 solution.setoldrandomIntoperation9(-1);
				          			solution.setreplacerandomIntoperation9(-1);
				          			solution.setsecondoldrandomIntoperation9(-1);
				              		   break; 
				          		
				          		   }
						    	  solution.setoperation(i, numoperation);
						    	  indexmutation.add(i);
						    	  operationmutation.add( numoperation);
						    	  solution.modifypropertyname.set(i, "khali");
						    	  solution.newstring.set(i, "khali");
						    	  solution.inorout.set(i, "empty");
						    	  Binding b=null;
						    	  solution.newbindings.set(i, b);
						    	  EClassifier obj=null;
						    	  solution.listeobject.set(i, obj);
						      
						      
						      }
						    
					   	  } //IntSolutionType.rules
	}
				else {
					
					operationoriginal.add( solution.operation().get(i));
					
					
				}
				
			}
		
	    	int indexof=-1;
			for (int i = 0; i <solution.getoperations().size() ; i++  )	
			{
				
				if(indexmutation.contains(i)) {
					 indexof=indexof+1;
					 if( !operationoriginal.contains( operationmutation.get(indexof))) {
						  NSGAIIThreeStep.writer.println("adade mutation");
						  NSGAIIThreeStep.writer.flush();
				    	  NSGAIIThreeStep.writer.println(operationmutation.get(indexof));
				    	  NSGAIIThreeStep.writer.flush();
				    	   operationoriginal.add(operationmutation.get(indexof));
					 }
					 else {
						  boolean chh=false;
						  int numoperation2 =-1;
				    	  while (chh==false  ) {
				    	  Random randomGenerator = new Random();
				    	  numoperation2 = CoSolution.min_operations_interval + randomGenerator.nextInt(CoSolution.max_operations_interval - CoSolution.min_operations_interval+1);
				    	 chh=checksituationchh(numoperation2,chh);
				    	 if(operationoriginal.contains(numoperation2))
				    	   chh=false;
				    	  }
				    	  NSGAIIThreeStep.writer.println("adade mutation");
				    	  NSGAIIThreeStep.writer.flush();
				    	  NSGAIIThreeStep.writer.println(numoperation2);
				    	  NSGAIIThreeStep.writer.flush();
				    	  solution.setoperation(i, numoperation2);
						  operationmutation.set(indexof, numoperation2);
						  operationoriginal.add( numoperation2);
						 
						 
					 }
					 
					 
				
				}		
			}
			if(indexmutation.size()==0) {
				NSGAIIThreeStep.writer.println("size sefr");
				solution=domutation( operationoriginal, solution) ;
				
				
			}
				
			
			} // else
		} catch (ClassCastException e1) {
			Configuration.logger_.severe("BitFlipMutation.doMutation: " +
					"ClassCastException error" + e1.getMessage());
			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".doMutation()");
		}
	} // doMutation

	
	public Solution domutation(ArrayList<Integer> operationoriginal, Solution solution) {
	  boolean chh=false;
	  int numoperation=-10;
	  Random number_generator = new Random();
	  int i = number_generator.nextInt(solution.operation().size());
   	  while (chh==false ) {
   	  Random randomGenerator = new Random();
   	  numoperation = CoSolution.min_operations_interval + randomGenerator.nextInt(CoSolution.max_operations_interval - CoSolution.min_operations_interval+1);
   	if(!NSGAIIThreeStep.forbiddenoperations.contains(numoperation))
	    chh=true;
	  }
   	  NSGAIIThreeStep.writer.println("adade mutation");
   	  NSGAIIThreeStep.writer.flush();
   	  NSGAIIThreeStep.writer.println(numoperation);
   	  NSGAIIThreeStep.writer.flush();
	  solution.getlistmutation().add(numoperation);
	  
		
     
     	 solution.getlistmutation().add(solution.operation().get(i));
	  switch(i+1) {
	   case 1:
		 solution.setoldrandomIntoperation1(-1);
		solution.setreplacerandomIntoperation1(-1);
		 solution.setsecondoldrandomIntoperation1(-1); 
		   break;
	   case 2:
		 solution.setoldrandomIntoperation2(-1);
		solution.setreplacerandomIntoperation2(-1);
		solution.setsecondoldrandomIntoperation2(-1);
		   break;
	   case 3:
		 solution.setoldrandomIntoperation3(-1);
		solution.setreplacerandomIntoperation3(-1);
		solution.setsecondoldrandomIntoperation3(-1);
		   break;
	   case 4:
		 solution.setoldrandomIntoperation4(-1);
		solution.setreplacerandomIntoperation4(-1);
		solution.setsecondoldrandomIntoperation4(-1);
		   break;
	   case 5:
		 solution.setoldrandomIntoperation5(-1);
		solution.setreplacerandomIntoperation5(-1);
		solution.setsecondoldrandomIntoperation5(-1);
		   break; 
	 case 6:
		 solution.setoldrandomIntoperation6(-1);
		solution.setreplacerandomIntoperation6(-1);
		solution.setsecondoldrandomIntoperation6(-1);
		   break; 
	 case 7:
		 solution.setoldrandomIntoperation7(-1);
		solution.setreplacerandomIntoperation7(-1);
		solution.setsecondoldrandomIntoperation7(-1);
		   break; 
	case 8:
		 solution.setoldrandomIntoperation8(-1);
		solution.setreplacerandomIntoperation8(-1);
		solution.setsecondoldrandomIntoperation8(-1);
		   break; 
	case 9:
		 solution.setoldrandomIntoperation9(-1);
		solution.setreplacerandomIntoperation9(-1);
		solution.setsecondoldrandomIntoperation9(-1);
		   break; 
	   }
	  solution.setoperation(i, numoperation);
	  solution.modifypropertyname.set(i, "khali");
	  solution.newstring.set(i, "khali");
	  solution.inorout.set(i, "empty");
	  Binding b=null;
	  solution.newbindings.set(i, b);
	  EClassifier obj=null;
	  solution.listeobject.set(i, obj);

     return solution;
		
		
		
	}
	private boolean checksituationchh(int numoperation, boolean chh) {
		// TODO Auto-generated method stub
		 if(NSGAIIThreeStep.checkcollection) {
	 		   
	 		   if(!NSGAIIThreeStep.checkfilter) {
		    	     {
		    	    	 if(!NSGAIIThreeStep.checksequence)
		    	    	 {
		    	    		 if(NSGAIIThreeStep.checkoperationcall) {
		    	    			 
		    	    			 if(NSGAIIThreeStep.checkiteration) {
		    	    		 		if( (numoperation)!=4 && (numoperation)!=5) 
		    	    		 		{
		    	    		 			chh=true;
		    	    		 		}
		    	    		 		}
		    	    			 else {
		    	    				 if( (numoperation)!=4 && (numoperation)!=5 && (numoperation)!=10) 
			    	    		 		{
		    	    					 chh=true;
			    	    		 		}
		    	    				 
		    	    				 
		    	    			 }
		    	    			 
		    	    		 	
		    	    		 }
		    	    		 else {
		    	    			 if(NSGAIIThreeStep.checkiteration) {
		    	    			 if( (numoperation)!=4 && (numoperation)!=5 && (numoperation)!=2) 
			    	    		 	{
		    	    				 chh=true;	
			    	    		 	}
		    	    			 }
		    	    			 else {
		    	    				 if( (numoperation)!=4 && (numoperation)!=5 && (numoperation)!=2 && (numoperation)!=10) 
				    	    		 	{
		    	    					 chh=true;
				    	    		 	}
		    	    				 
		    	    			 }
		    	    		 }
		    	    	 }
		    	    	else {
		    	    		if(NSGAIIThreeStep.checkoperationcall) {
		    	    		if(NSGAIIThreeStep.checkiteration) {	
		    	    		if( (numoperation)!=5) 
		    	    		{
		    	    			chh=true;
		    	    		}
		    	    		}
		    	    		else {	
			    	    		if( (numoperation)!=5 && (numoperation)!=10) 
			    	    		{
			    	    			chh=true;
			    	    		}
			    	    		}
		    	    		
		    	    		}
		    	    		else {
		    	    			if(NSGAIIThreeStep.checkiteration) {	
		    	    			if( (numoperation)!=5 && (numoperation)!=2) 
			    	    		{
		    	    				chh=true;
			    	    		}
		    	    			}
		    	    			else {
		    	    				if( (numoperation)!=5 && (numoperation)!=2 && (numoperation)!=10) 
				    	    		{
		    	    					chh=true;
				    	    		}
		    	    				
		    	    				
		    	    			}
		    	    			
		    	    		}
		    	    	   }
		    	    		
		    	     }
		    	    	 }
		    	    	 else {
		    	    		 if(!NSGAIIThreeStep.checksequence) {
		    	    			 if(NSGAIIThreeStep.checkoperationcall) {
		    	    				 if(NSGAIIThreeStep.checkiteration) {	
		    	    			 if( (numoperation)!=4 )
		    	    			 {
		    	    				 chh=true;
		    	    			 }
		    	    			}
		    	    				 else {
		    	    					 if( (numoperation)!=4 && (numoperation)!=10)
				    	    			 {
		    	    						 chh=true;
				    	    			 }
		    	    					 
		    	    				 }
		    	    			 }
		    	    			 else {
		    	    				 if(NSGAIIThreeStep.checkiteration) {	
		    	    				 if( (numoperation)!=2 && (numoperation)!=4)
			    	    			 {
		    	    					 chh=true;
			    	    			 }
		    	    				 }
		    	    				 else {	
			    	    				 if( (numoperation)!=2 && (numoperation)!=4 && (numoperation)!=10)
				    	    			 {
			    	    					 chh=true;	
				    	    			 }
			    	    				 }
		    	    			 }
		    	    		 }
		    	    		 else {
		    	    			 if(NSGAIIThreeStep.checkoperationcall) {
		    	    				 if(NSGAIIThreeStep.checkiteration) {	 
		    	    					 chh=true;
		    	    				 }
		    	    				 else {	 
		    	    					 if( (numoperation)!=10) {
		    	    						 chh=true;
				    	    				 }
		    	    				 }
		    	    		 }
		    	    		 else {
		    	    			 if(NSGAIIThreeStep.checkiteration) {	 
		    	    			 if((numoperation)!=2) {
		    	    				 chh=true;
		    	    			 }
		    	    			 }
		    	    			 else {
		    	    				 if((numoperation)!=10 && (numoperation)!=2) {
		    	    					 chh=true;
				    	    			 }
		    	    				 
		    	    			 }
		    	    			 
		    	    			 
		    	    		 }
		    	    		 }   
		    	    		  }
	 	  }
	 	   else if(!NSGAIIThreeStep.checkcollection) {
	 		   if(!NSGAIIThreeStep.checkfilter) {
	 			   if(!NSGAIIThreeStep.checksequence) {
	 				  if(NSGAIIThreeStep.checkoperationcall) {
	 					 if(NSGAIIThreeStep.checkiteration) {	 
	 				   	if((numoperation)!=5 && (numoperation)!=4  && (numoperation)!=7) 
	 				   	{
	 				   	chh=true;
	 				   	}
	 					 }
	 					 else {
	 						if((numoperation)!=5 && (numoperation)!=4  && (numoperation)!=7 && (numoperation)!=10) 
		 				   	{
	 							chh=true;
		 				   	}
	 						 
	 					 }
	 				  }
	 				  else {
	 					 if(NSGAIIThreeStep.checkiteration) {	 
	 					 if((numoperation)!=5 && (numoperation)!=4  && (numoperation)!=7  && (numoperation)!=2) 
		 				   	{
	 						chh=true;
		 				   	}
	 					 }
	 					 else {
	 						if((numoperation)!=5 && (numoperation)!=4  && (numoperation)!=7  && (numoperation)!=2 && (numoperation)!=10) 
		 				   	{
	 							chh=true;
		 				   	}
	 						 
	 					 }
	 					  
	 				  }
	 			   }
	 			   else {
	 				  if(NSGAIIThreeStep.checkoperationcall) {
	 					 if(NSGAIIThreeStep.checkiteration) {	 
	 				  if((numoperation)!=5 && (numoperation)!=7) 
	 				  {
	 					 chh=true;
	 				  }
	 					 }
	 					 else {
	 						if((numoperation)!=5 && (numoperation)!=7  && (numoperation)!=10) 
			 				  {
	 							chh=true;
			 				  }
	 						 
	 						 
	 					 }
	 				  }
	 				  else {
	 					 if(NSGAIIThreeStep.checkiteration) {	 
	 					 if((numoperation)!=5 && (numoperation)!=7 && (numoperation)!=2) 
		 				  {
	 						chh=true;
		 				  }
	 					 }
	 					 else {
	 						if((numoperation)!=5 && (numoperation)!=7 && (numoperation)!=2 && (numoperation)!=10) 
			 				  {
	 							chh=true;
			 				  }
	 						 
	 					 }
	 					  
	 				  }
	 			   }
					}
					else {
						if(!NSGAIIThreeStep.checksequence) {
						if(NSGAIIThreeStep.checkoperationcall) {
							if(NSGAIIThreeStep.checkiteration) {	 
						if( (numoperation)!=4  && (numoperation)!=7) 
						{
							
							chh=true;
						}
							}
							else {
								if( (numoperation)!=4  && (numoperation)!=7 && (numoperation)!=10) 
								{
									
									chh=true;
								}
								
							}
							}
						else {
							if(NSGAIIThreeStep.checkiteration) {	
							if( (numoperation)!=4  && (numoperation)!=7 && (numoperation)!=2) 
							{
								
								chh=true;
							}
							}
							else {
								if( (numoperation)!=4  && (numoperation)!=7 && (numoperation)!=2 && (numoperation)!=10) 
								{
									
									chh=true;
								}
								
							}
							
						}
						}
						else {
							if(NSGAIIThreeStep.checkoperationcall) {
								if(NSGAIIThreeStep.checkiteration) {
							if((numoperation)!=7) 
							{
								chh=true;	
							}
								}
								else {
									if((numoperation)!=7 && (numoperation)!=10) 
									{
										chh=true;
									}
										}
							}
							else {
								if(NSGAIIThreeStep.checkiteration) {
								if((numoperation)!=2 && (numoperation)!=7) 
								{
									chh=true;
								}
								}
								else {
									if((numoperation)!=2 && (numoperation)!=7  && (numoperation)!=10) 
									{
										
			    	    	    	     chh=true;
			    	    	    	     
									}
									}
								
								
								
							}
						}
						}
	 		   
	 	   } 
				return chh;
	}

	private Solution replacetwodeletoperation(Solution solution, List<Integer> listdelet, int i) {
		// TODO Auto-generated method stub
		
		for(int pp=0;pp< listdelet.size();pp++)
		{	
			 switch(i+1) {
    		   case 1:
    			   
    			   solution.setoldrandomIntoperation1(replacetwodelet(solution, listdelet.get(pp) ));
       			solution.setreplacerandomIntoperation1(replacetwodelet2(solution, listdelet.get(pp) ));
       			solution.setsecondoldrandomIntoperation1(replacetwodelet3(solution, listdelet.get(pp) ));
        		   break;
    		   case 2:
    			 solution.setoldrandomIntoperation2(replacetwodelet(solution, listdelet.get(pp) ));
    			solution.setreplacerandomIntoperation2(replacetwodelet2(solution, listdelet.get(pp) ));
    			solution.setsecondoldrandomIntoperation2(replacetwodelet3(solution, listdelet.get(pp) ));
        		   break;
    		   case 3:
    			 solution.setoldrandomIntoperation3(replacetwodelet(solution, listdelet.get(pp) ));
    			solution.setreplacerandomIntoperation3(replacetwodelet2(solution, listdelet.get(pp) ));
    			solution.setsecondoldrandomIntoperation3(replacetwodelet3(solution, listdelet.get(pp) ));
        		   break;
    		   case 4:
    			 solution.setoldrandomIntoperation4(replacetwodelet(solution, listdelet.get(pp) ));
    			solution.setreplacerandomIntoperation4(replacetwodelet2(solution, listdelet.get(pp) ));
    			solution.setsecondoldrandomIntoperation4(replacetwodelet3(solution, listdelet.get(pp) ));
        		   break;
    		   case 5:
    			 solution.setoldrandomIntoperation5(replacetwodelet(solution, listdelet.get(pp) ));
    			solution.setreplacerandomIntoperation5(replacetwodelet2(solution, listdelet.get(pp) ));
    			solution.setsecondoldrandomIntoperation5(replacetwodelet3(solution, listdelet.get(pp) ));
        		   break; 
    		 case 6:
    			 solution.setoldrandomIntoperation6(replacetwodelet(solution, listdelet.get(pp) ));
    			solution.setreplacerandomIntoperation6(replacetwodelet2(solution, listdelet.get(pp) ));
    			solution.setsecondoldrandomIntoperation6(replacetwodelet3(solution, listdelet.get(pp) ));
        		   break; 
    		 case 7:
    			 solution.setoldrandomIntoperation7(replacetwodelet(solution, listdelet.get(pp) ));
    			solution.setreplacerandomIntoperation7(replacetwodelet2(solution, listdelet.get(pp) ));
    			solution.setsecondoldrandomIntoperation7(replacetwodelet3(solution, listdelet.get(pp) ));
        		   break; 
    		case 8:
    			 solution.setoldrandomIntoperation8(replacetwodelet(solution, listdelet.get(pp) ));
    			solution.setreplacerandomIntoperation8(replacetwodelet2(solution, listdelet.get(pp) ));
    			solution.setsecondoldrandomIntoperation8(replacetwodelet3(solution, listdelet.get(pp) ));
        		   break; 
    		case 9:
    			 solution.setoldrandomIntoperation9(replacetwodelet(solution, listdelet.get(pp) ));
    			solution.setreplacerandomIntoperation9(replacetwodelet2(solution, listdelet.get(pp) ));
    			solution.setsecondoldrandomIntoperation9(replacetwodelet3(solution, listdelet.get(pp) ));
        		   break; 
    		   }
			 i=listdelet.get(pp);
			 
		
		}
		
		return solution;
		
	}

	private int replacetwodelet(Solution solution, int p) {
		// TODO Auto-generated method stub
		
			
			 switch(p+1) {
    		   case 1:
    			   
    			   	   return solution.getoldrandomIntoperation1();
    			
    		   case 2:
    			   		return solution.getoldrandomIntoperation2();
    		   case 3:
    				   return solution.getoldrandomIntoperation3();
    		   case 4:
    			   		return solution.getoldrandomIntoperation4();
    		   case 5:
    			   		return solution.getoldrandomIntoperation5();
    		   case 6:
    			   		return solution.getoldrandomIntoperation6();
    		   case 7:
    			   		return solution.getoldrandomIntoperation7();
    		   case 8:
    			   		return solution.getoldrandomIntoperation8();
    		   case 9:
    			   		return solution.getoldrandomIntoperation9();
    		   }
			return p;
	    	
		
	}
	private int replacetwodelet2(Solution solution, int p) {
		// TODO Auto-generated method stub
	
			 switch(p+1) {
			 	case 1:
    			      			
    			   	return		solution.getreplacerandomIntoperation1();
    			
       		   case 2:
    			   return		solution.getreplacerandomIntoperation2();
    		   case 3:
    			   return		solution.getreplacerandomIntoperation3();
    		   case 4:
    			   return		solution.getreplacerandomIntoperation4();
    		   case 5:
    			   return		solution.getreplacerandomIntoperation5();
    		   case 6:
    			   return		solution.getreplacerandomIntoperation6();
    		   case 7:
    			   return		solution.getreplacerandomIntoperation7();
    		   	case 8:
    		   		return		solution.getreplacerandomIntoperation8();
    		   	case 9:
    		   	    return		solution.getreplacerandomIntoperation9();
    		   }
			return p;
	    	
		
	}
	private int replacetwodelet3(Solution solution, int p) {
		// TODO Auto-generated method stub
		
			
			 switch(p+1) {
    		   case 1:
    			return   solution.getsecondoldrandomIntoperation1();  
    		   case 2:
    			return   solution.getsecondoldrandomIntoperation2();  
    		   case 3: 		
    			 return	  solution.getsecondoldrandomIntoperation3();   
    		   case 4:
    			 return   solution.getsecondoldrandomIntoperation4();
    		   case 5:
    			 return	  solution.getsecondoldrandomIntoperation5();
    		   case 6:
    			 return	  solution.getsecondoldrandomIntoperation6();
    		   case 7:
    			 return	  solution.getsecondoldrandomIntoperation7();
    		   case 8:
    			 return	  solution.getsecondoldrandomIntoperation8();
    		   case 9:
    			 return	  solution.getsecondoldrandomIntoperation9();
    		   }
			return p;
	    	
		
	}
	
	
	/**
	 * Executes the operation
	 * @param object An object containing a solution to mutate
	 * @return An object containing the mutated solution
	 * @throws JMException 
	 */
	public Object execute(Object object) throws JMException {
		Solution solution = (Solution) object;

		if (!VALID_TYPES.contains(solution.getType().getClass())) {
			Configuration.logger_.severe("BitFlipMutation.execute: the solution " +
					"is not of the right type. The type should be 'Binary', " +
					"'BinaryReal' or 'Int', but " + solution.getType() + " is obtained");

			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		} // if 

		doMutation(mutationProbability_, solution);
		return solution;
	} // execute
} // BitFlipMutation
