/**
 * 
 */
package logic;

/**
 * @author thayumaanavan
 *
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import classifier.DTW;



import datastructure.LabelledTimeSeriesClassificationData;
import datastructure.Matrix;

import filters.*;

public class ProcessingUnit {
	
	protected List<Filter> dataFilters ;
	LabelledTimeSeriesClassificationData current;
	public List<LabelledTimeSeriesClassificationData> trainsequence;
	Boolean analyzing,learning;
	Matrix  trainingSample;
	DTW recognition;

   
	/**
	 * 
	 */
	public ProcessingUnit(boolean autofilter) 
	{
		this.learning = false;
        this.analyzing = false;
        this.trainsequence = new ArrayList<LabelledTimeSeriesClassificationData>();
		dataFilters=new ArrayList<Filter>();
		trainingSample=new Matrix();
		recognition=new DTW();
		
		// TODO Auto-generated constructor stub
		if (autofilter)
        {
            this.addFilter(new IdleStateFilter());
            this.addFilter(new MotionDetectFilter());
            this.addFilter(new DirectionalEquivalenceFilter());
        }
		current=new LabelledTimeSeriesClassificationData(9, null, null);
		//current.setNumDim(9);
		
	}
	
	 public void addFilter(Filter filter)
     {
         this.dataFilters.add(filter);
     }
	
	 public void addData(Vector<Double> vector)
     {
		 
        /* for(Filter i:this.dataFilters)
         {
             vector = i.filter(vector);
         }*/
		 if (this.learning || this.analyzing)
		 {
			 //System.out.println(vector.get(2));
			 
			 trainingSample.push_back(vector);
			// System.out.println("row="+trainingSample.getNumRows());
			// System.out.println("col="+trainingSample.getNumCols());
			 
		 }
			 
		 
		 
     }
	 
	 public void startTraining()
	 {
		 if (!this.analyzing && !this.learning)
         {
             this.learning = true;
             System.out.println("Started...");
         }
	 }
	 
	 public void stopTraining()
	 {
		 if (this.learning)
         {
			 
             /*if (this.current.getCountOfData() > 0)
             {
            	 
                // LabelledTimeSeriesClassificationData gesture = new LabelledTimeSeriesClassificationData(this.current);
                 //this.trainsequence.add(gesture);
                 //this.current = new LabelledTimeSeriesClassificationData();
             }*/

             this.learning = false;
             System.out.println("stopped...");
         }
	 }
	 
	 public void finishTraining(int n)
	 {
		 if (!this.analyzing && !this.learning)
         {
			 current.addSample(n,trainingSample);
			// System.out.println("row="+trainingSample.getNumRows());
			 //System.out.println("col="+trainingSample.getNumCols());
			 this.learning=false;
			 for(int i=0;i<trainingSample.getNumRows();i++)
				 for(int j=0;j<trainingSample.getNumCols();j++)
					 System.out.print(trainingSample.dataptr[i][j]);
			 trainingSample=new Matrix();
			 System.out.println("sample added...");
             /*if (!this.trainsequence.isEmpty())
             {
            	 this.learning = true;
            	 
            	 
            	 this.trainsequence = new ArrayList<LabelledTimeSeriesClassificationData>();
                 this.learning = false;
             }*/
         }
	 }

	/**
	 * @param next
	 * @throws IOException 
	 */
	public void Save(String next) throws IOException 
	{
		// TODO Auto-generated method stub
		current.saveDatasetToFile(next);
		
	}
	
	public void train()
	{
		Boolean res=recognition.train(current);
		if(!res)
			System.out.println("training failed");
	}
	
	public void startRecognition()
	{
		if(!this.analyzing && !this.learning)
		{
			this.analyzing=true;
		}
	}
	
	public void stopRecognition()
	{
		if(this.analyzing)
		{
			Boolean predict=recognition.predict(this.trainingSample);
			if(!predict)
				System.out.println("prediction failed");
			
			this.analyzing=false;
			printText(recognition.getPredictedClassLabel());
			
		}
	}

	/**
	 * @param gesture
	 */
	private void printText(int id) {
		// TODO Auto-generated method stub
		switch(id)
		{
		case 0:
			System.out.println("gesture 1");
			break;
		case 1:
			System.out.println("gesture 2");
			break;
		case 2:
			System.out.println("gesture 3");
			
		}
	}
}
