package anatlyzer.examples.api;


/**
 *
 * @author MWM
 */
public class ReferencePoint 
{
    public double objectives[];
    
    ReferencePoint(int objectives_number)
    {
        this.objectives = new double[objectives_number];
    }
    
    public ReferencePoint(ReferencePoint ref)
    {
        this.objectives = new double[ref.objectives.length];
        for(int i=0;i<this.objectives.length;i++)
        {
            this.objectives[i] = ref.objectives[i];
        }
    }
    public ReferencePoint(double table[])
    {
        this.objectives = new double[table.length];
        for(int i=0;i<this.objectives.length;i++)
        {
            this.objectives[i] = table[i];
        }
    }
    
    public String reference_point_to_string()
    {
        String result = " ";
        for(int i=0;i<this.objectives.length;i++)
        {
            result+= Double.toString(objectives[i])+" ";
        }
        return result ;
    }
}
