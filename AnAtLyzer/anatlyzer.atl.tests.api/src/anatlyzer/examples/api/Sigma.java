package anatlyzer.examples.api;


/**
 *
 * @author MWM
 */
public class Sigma 
{
    public double user_value;
    public double current_value;
    
    public Sigma()
    {
        user_value = 0 ;
        current_value = 1 ;
    }
    public Sigma(double user_value)
    {
        this.user_value = user_value ;
        current_value = 1 ;
    }
    public Sigma(Sigma s)
    {
        this.user_value = s.user_value ;
        this.current_value = s.current_value ;
    }
    
    public void update_sigma(int current_generation, int max_generations)
    {
        current_value = 1 - current_generation*((1-user_value)/max_generations);
    }
    
}
