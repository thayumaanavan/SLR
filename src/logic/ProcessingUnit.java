/**
 * 
 */
package logic;

/**
 * @author thayumaanavan
 *
 */

import java.util.ArrayList;
import java.util.List;

import filters.*;

public class ProcessingUnit {
	
	protected List<Filter> dataFilters ;
	
	

	/**
	 * 
	 */
	public ProcessingUnit(boolean autofilter) {
		dataFilters=new ArrayList<Filter>();
		// TODO Auto-generated constructor stub
		if (autofilter)
        {
            this.AddFilter(new IdleStateFilter());
            this.AddFilter(new MotionDetectFilter());
            this.AddFilter(new DirectionalEquivalenceFilter());
        }
		
		
	}
	
	 public void AddFilter(Filter filter)
     {
         this.dataFilters.add(filter);
     }
	
	 public void AddData(double[] vector)
     {
		 
         for(Filter i:this.dataFilters)
         {
             vector = i.filter(vector);
         }
     }
}
