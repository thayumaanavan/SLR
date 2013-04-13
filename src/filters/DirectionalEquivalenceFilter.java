/**
 * 
 */
package filters;

/**
 * @author thayumaanavan
 *
 */
public class DirectionalEquivalenceFilter extends Filter {
	
	private double[] reference;
	public double Sensitivity;
	
	public DirectionalEquivalenceFilter()
    {
        this.reset();
    }

	/* (non-Javadoc)
	 * @see filters.Filter#filterAlgorithm(double[])
	 */
	@Override
	public double[] filterAlgorithm(double[] vector) {
		// TODO Auto-generated method stub
		System.out.println("Direction Equivalence Filter");
		   if (vector[0] < reference[0] - this.Sensitivity ||
		           vector[0] > reference[0] + this.Sensitivity ||
		           vector[1] < reference[1] - this.Sensitivity ||
		           vector[1] > reference[1] + this.Sensitivity ||
		           vector[2] < reference[2] - this.Sensitivity ||
		           vector[2] > reference[2] + this.Sensitivity)
		            {
		                this.reference = vector;
		                System.out.println(vector[0]+","+vector[1]+","+vector[2]);
		                return vector;
		            }
		            else
		            {
		            	System.out.println("Filtered vector");
		            	System.out.println(vector[0]+","+vector[1]+","+vector[2]);
		                return null;
		            }
		
	}

	/* (non-Javadoc)
	 * @see filters.Filter#reset()
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		this.Sensitivity = 0.2;
        this.reference = new double[] { 0.0, 0.0, 0.0 };

	}

}
