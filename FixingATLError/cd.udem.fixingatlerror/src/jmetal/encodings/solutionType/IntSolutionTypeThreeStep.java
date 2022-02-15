//  IntSolutionType.java
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

package jmetal.encodings.solutionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import cd.udem.fixingatlerror.CoSolutionThreeStep;
import jmetal.core.Problem;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.metaheuristics.nsgaII.NSGAIIThreeStep;

/**
 * Class representing the solution type of solutions composed of Int variables
 */
public class IntSolutionTypeThreeStep extends SolutionType {
	
	public static CoSolutionThreeStep S;
	public static ArrayList<Integer> operations; 
	public static int min_operations_size = 1;
	public static int max_operations_size=7;
	public static int min_operations_interval = 1;
	public static int max_operations_interval = 11;
	public static int operations_size=7; 
	int h = 0;

	/**
	 * Constructor
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public IntSolutionTypeThreeStep(Problem problem) {
		super(problem);
	} // Constructor

	
	/**
	 * Creates the variables of the solution
	 * @return 
	 */
	public Variable[] createVariables() {

		
		Variable[] variables = new Variable[problem_.getNumberOfVariables()];
		int lastoperationssize = max_operations_size+1;
		operations = new ArrayList<Integer>();
		Random number_generator = new Random();
		operations_size = 1+number_generator.nextInt(max_operations_size-3);
		h++;
		int numoperation = -1;
		int var = 0;
		for (int i = 0; i < lastoperationssize; i++)
			operations.add(-3);
		
		List<Integer> temp = new ArrayList<Integer>();
		int i = -1;
		
		while (var < operations_size) // choose randomly operations_size, not repeated operation
		{
			boolean tp = false;
			while (tp == false) { // not repeated index
				i = number_generator.nextInt(lastoperationssize);
				if (!temp.contains(i))
					tp = true;
			 }
			
			numoperation = min_operations_interval
					+ number_generator.nextInt(max_operations_interval - min_operations_interval + 1);
			
			if(!NSGAIIThreeStep.forbiddenoperations.contains(numoperation) && !operations.contains(numoperation))
			{
				operations.set(i, numoperation);
				temp.add(i);
				var++;
				
			}
			
		}
	
	    
	
		
		return variables;
	}

	
}
