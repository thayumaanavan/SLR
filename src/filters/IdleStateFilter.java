package filters;
/**
 * @author thayumaanavan
 *
 */

public class IdleStateFilter extends Filter {
	
	
	//Value below than this will be neglected.
	public double Sensitivity;
	
	
	//Constructor
	public IdleStateFilter(){
		this.Sensitivity=.5;
		
	}
	//removes data if it's in idle state
	@Override
	public double[] filterAlgorithm(double[] vector) {
		// TODO Auto-generated method stub
		//System.out.println("idlestate filter");
		double absvalue = Math.sqrt((vector[0] * vector[0]) +
                (vector[1] * vector[1]) + (vector[2] * vector[2]));

        
        if (absvalue > this.Sensitivity)
        {
        	System.out.println("idlestate filter");
        	System.out.println(vector[0]+","+vector[1]+","+vector[2]);
            return vector;
        }
        else
        {
        	System.out.println("Idlestate filter-Filtered vector");
        	System.out.println(vector[0]+","+vector[1]+","+vector[2]);
            return null;
        }
		
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
