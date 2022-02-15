//  Distance.java
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

package jmetal.util;

import jmetal.core.SolutionSet;
import jmetal.metaheuristics.nsgaII.NSGAIIThreeStep;
/**
 * This class implements some utilities for calculating distances
 */
public class DistanceSocialDiversity {

  /**
   * Constructor.
   */
  public DistanceSocialDiversity() {
    //do nothing.
  } // Distance


  /**
   * Returns a matrix with distances between solutions in a
   * <code>SolutionSet</code>.
   * @param solutionSet The <code>SolutionSet</code>.
   * @return a matrix with distances.
   */
    /** Returns the minimum distance from a <code>Solution</code> to a 
   * <code>SolutionSet according to the encodings.variable values</code>.
   * @param solution The <code>Solution</code>.
   * @param solutionSet The <code>SolutionSet</code>.
   * @return The minimum distance between solution and the set.
   * @throws JMException
   */
   /** Returns the distance between two solutions in the search space.
   *  @param solutionI The first <code>Solution</code>.
   *  @param solutionJ The second <code>Solution</code>.
   *  @return the distance between solutions.
   * @throws JMException
   */
   /** Returns the distance between two solutions in objective space.
   *  @param solutionI The first <code>Solution</code>.
   *  @param solutionJ The second <code>Solution</code>.
   *  @return the distance between solutions in objective space.
   */
   /**
   * Return the index of the nearest solution in the solution set to a given solution
   * @param solution
   * @param solutionSet
   * @return  The index of the nearest solution; -1 if the solutionSet is empty
   */
  /** Assigns crowding distances to all solutions in a <code>SolutionSet</code>.
   * @param solutionSet The <code>SolutionSet</code>.
   * @param nObjs Number of objectives.
 * @param writer 
   */
  public void SocialDiversityAssignment(SolutionSet solutionSet, int nObjs) {
    
	int size = solutionSet.size();

    if (size == 0)
      return;

    if (size == 1) {
      solutionSet.get(0).setSocialDiversity(Double.POSITIVE_INFINITY);
      return;
    } // if
    //Use a new SolutionSet to evite alter original solutionSet
    SolutionSet front = new SolutionSet(size);
    for (int i = 0; i < size; i++){
      front.add(solutionSet.get(i));
    }

    for (int i = 0; i < size; i++)
        front.get(i).setSocialDiversity(0.0);
   
    int similarity = 0;
    double index=0.0;
    for (int i = 0; i<size; i++) {
    	
    	  NSGAIIThreeStep.writer.println("indexi");
		  NSGAIIThreeStep.writer.println(front.get(i).getindex());
		  NSGAIIThreeStep.writer.println(front.get(i).getfirstfitness());
		  NSGAIIThreeStep.writer.println(front.get(i).getsecondfitness());
    	similarity = 0;
    	index=0.0;
      for (int j = 0; j < size; j++) {
    	  if(i!=j) {
    		  index=index+1;
    		  
    		  for(int k=0; k<front.get(i).Diffsubsetmodel0.size();k++) {
    			
    			  if(front.get(j).Diffsubsetmodel0.contains(front.get(i).Diffsubsetmodel0.get(k) )) {
    				  similarity++;
    			  }
    			  
    		  }
    		
    		  for(int k=0; k<front.get(i).Diffsubsetmodel1.size();k++) {
    			 
    			  if(front.get(j).Diffsubsetmodel1.contains(front.get(i).Diffsubsetmodel1.get(k) )) {
    				  similarity++;
    			  }
    			  
    		  }
    		
    		  for(int k=0; k<front.get(i).Diffsubsetmodel2.size();k++) {
    			 
    			  if(front.get(j).Diffsubsetmodel2.contains(front.get(i).Diffsubsetmodel2.get(k) )) {
    			
    				  similarity++;
    			  }
    			  
    		  }
    		 
    		  for(int k=0; k<front.get(i).Diffsubsetmodel3.size();k++) {
    
    			  if(front.get(j).Diffsubsetmodel3.contains(front.get(i).Diffsubsetmodel3.get(k) )) {

    				  similarity++;
    			  }
  
    		  }
    	  }
       
      } // for
      NSGAIIThreeStep.writer.println("similarityend"+ similarity);
      front.get(i).setObjective(1, similarity);
      front.get(i).setsecondfitness(similarity );
      } // for  
   } // SocialDiversityAssing            
} // Distance

