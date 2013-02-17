/**
 * 
 */
package filters;

/**
 * @author thayumaanavan
 *
 */


public abstract class Filter {
	
	public double[] filter(double[] vector)
    {
        if (vector == null)
        {
            return null;
        }
        else
        {
            return filterAlgorithm(vector);
        }
    }
	
	 abstract public double[] filterAlgorithm(double[] vector);

     abstract public void reset();

}
