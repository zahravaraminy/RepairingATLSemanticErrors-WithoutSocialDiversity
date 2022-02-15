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
import cd.udem.fixingatlerror.CoSolution;
import jmetal.core.Problem;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.variable.Int;
import jmetal.metaheuristics.nsgaII.NSGAII;

/**
 * Class representing the solution type of solutions composed of Int variables
 */
public class IntSolutionType extends SolutionType {

	public static CoSolution S;
	public static ArrayList<Integer> operations; // = new ArrayList<Rule> ();
	public static int min_operations_size = 1;//
	public static int max_operations_size;// 
	public static int min_operations_interval = 1;
	public static int max_operations_interval = 10;// 

	public static int operations_size;
	int h = 0;

	/**
	 * Constructor
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public IntSolutionType(Problem problem) {
		super(problem);
	} // Constructor

	/**
	 * Creates the variables of the solution
	 */
	public Variable[] createVariables() {

		// System.out.println("v222");
		Variable[] variables = new Variable[problem_.getNumberOfVariables()];
		int lastoperationssize = max_operations_size;

		operations = new ArrayList<Integer>();
		Random number_generator = new Random();
		operations_size = min_operations_size
				+ (int) (Math.random() * ((max_operations_size - min_operations_size) + 1));
		h++;
		int numoperation = -1;
		int var = 0;
		for (int i = 0; i < lastoperationssize; i++)
			operations.add(-3);
		// int temp=-1;
		List<Integer> temp = new ArrayList<Integer>();
		int i = -1;
		
		System.out.println("Forbidden Operations:" + NSGAII.forbiddenoperations);
		while (var < operations_size) // var<2
		{
			boolean tp = false;
			while (tp == false) {
				i = number_generator.nextInt(lastoperationssize);
				if (!temp.contains(i))
					tp = true;
			}
			variables[var] = new Int((int) problem_.getLowerLimit(var), (int) problem_.getUpperLimit(var));
			numoperation = min_operations_interval
					+ number_generator.nextInt(max_operations_interval - min_operations_interval + 1);
			
			if(!NSGAII.forbiddenoperations.contains(numoperation))
			{   
				operations.set(i, numoperation);
				temp.add(i);
				var++;
			}
			
				
		} 
		System.out.println("le num aleatoire de l operation est : " + operations);
		return variables;
	}

	
}
