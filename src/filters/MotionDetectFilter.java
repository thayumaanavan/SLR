/**
 * 
 */
package filters;

/**
 * @author thayumaanavan
 *
 */
public class MotionDetectFilter extends Filter {
	
	private Boolean nowinmotion;
    private long motionstartstamp;
    public int MotionChangeTime ;
    
    public MotionDetectFilter()
    {
        this.reset();
    }


	/* (non-Javadoc)
	 * @see filters.Filter#filterAlgorithm(double[])
	 */
	@Override
	public double[] filterAlgorithm(double[] vector) {
		// TODO Auto-generated method stub
		System.out.println("Motion Detect Filter");
		if (vector != null)
        {
            this.motionstartstamp = System.currentTimeMillis();;
            if (!this.nowinmotion)
            {
                this.nowinmotion = true;
                this.motionstartstamp = System.currentTimeMillis();;
            }
        }
		System.out.println(vector[0]+","+vector[1]+","+vector[2]);
		return vector;
	}
	
	@Override
	 public  double[] filter(double[] vector)
     {

         if (this.nowinmotion &&
             (System.currentTimeMillis() - this.motionstartstamp) >= this.MotionChangeTime)
         {
             this.nowinmotion = false;
         }

         return filterAlgorithm(vector);
     }

	/* (non-Javadoc)
	 * @see filters.Filter#reset()
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		this.motionstartstamp = System.currentTimeMillis();
        this.nowinmotion = false;
        this.MotionChangeTime = 190;

	}

}
