package cd.udem.fixingatlerror;

import java.util.ArrayList;

import anatlyzer.examples.api.ReferencePoint;
import anatlyzer.examples.api.Sigma;




/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MWM
 */
public class Execution 
{
    //--- User parameters ---
    // these values represents the chosen reference point and sigma value
    
    static double aspiration_values[] = {0.5,0.5};
    static ReferencePoint ref = new ReferencePoint(aspiration_values) ;
    static Sigma s = new Sigma(1);
    
    static int generations = 10 ;
    
    static int population_size = 5 ;
    
    Execution()
    {
        
    }
    
    public static void main(String[] args) throws Exception 
    {
 
    	Population p = new Population(population_size,s,ref,generations); 
       ArrayList<CoSolution> parents = new ArrayList<CoSolution>();
        
        p.create_poplulation();
        
        for(int i=0; i<generations; i++)
        {
            p.update_sigma_value(i, generations);
            parents = p.random_selection();
            p.generate_next_popluation(parents,i);
            //p.print_popluation_metrics(i);
        }
        
        
    }
}