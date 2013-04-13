/**
 * 
 */
package datastructure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Vector;
import util.ClassTracker;


/**
 * @author thayumaanavan
 *
 */
public class LabelledTimeSeriesClassificationData {

	String datasetName;
	String infoText;
	int numDim;
	int totNumSamp;
	Vector <LabelledTimeSeriesClassificationSample> data;
	Vector< ClassTracker > classTracker;	
	Boolean trackingClass;
	
	//Constructor
	public LabelledTimeSeriesClassificationData(int nd,String name,String info)
	{
		this.numDim  =nd;
		this.infoText =info;
		this.datasetName=name;
		this.totNumSamp =0;
		this.data=new Vector<LabelledTimeSeriesClassificationSample>();
		this.classTracker=new Vector<ClassTracker>();
		this.trackingClass=false;
	}
	
	/**
	 * @param current
	 */
	public LabelledTimeSeriesClassificationData(
			LabelledTimeSeriesClassificationData rhs) {
		// TODO Auto-generated constructor stub
		this.data=new Vector<LabelledTimeSeriesClassificationSample>();
		this.data=rhs.data;
		this.datasetName = rhs.datasetName;
	    this.infoText = rhs.infoText;
	    this.numDim = rhs.numDim;
		this.classTracker = rhs.classTracker;

	}

	public void setDataSetName(String name)
	{
		this.datasetName =name;
	}
	
	public void setNumDim(int nd)
	{
		numDim=nd;
	}
	
	public void setInfoText(String info)
	{
		infoText=info;
	}
	
	public void addSample(int n,Matrix m)
	{
		LabelledTimeSeriesClassificationSample s=new LabelledTimeSeriesClassificationSample(n,m);
		//System.out.println("row="+s.getLength());
		data.addElement(s);
		
		totNumSamp++;
		if( classTracker.size() == 0 )
		{
			ClassTracker tracker=new ClassTracker(n,1);
			classTracker.add(tracker);
		}	
		else
		{
			Boolean labelFound = false;
			for(int i=0; i<classTracker.size(); i++)
			{
				if( n == classTracker.get(i).getClassLabel())
				{
					classTracker.get(i).counter++;
					labelFound = true;
					break;
				}
			}
			if( !labelFound )
			{
				ClassTracker tracker=new ClassTracker(n,1);
				classTracker.add(tracker);
			}
		}
	}
	
	String getDataSetName()
	{
		return this.datasetName;
	}
	
	public int getNumDim()
	{
		return numDim;
	}
	
	public Vector<LabelledTimeSeriesClassificationSample>getDataVector()
	{
		return data;
	}
	
	String getInfoText()
	{
		return infoText;
	}
	
	public void saveDatasetToFile(String fname) throws IOException
	{
		//FileWriter fw=new FileWriter(fname);
		//BufferedWriter bf=new BufferedWriter(fw);
		String content="LabelledTimeSeriesClassificationData\nDatasetName:"
				+datasetName+"\nInfoText:"+infoText+"\nNumDim:"
				+numDim+"\nTotalNumTrainingExamples:"+totNumSamp+"\nNumberOfClasses:";
		PrintWriter pw=new PrintWriter(fname);
		pw.println("LabelledTimeSeriesClassificationData-Training dataset");
		pw.println("datasetName:"+datasetName);
		pw.println("InfoText:"+infoText);
		pw.println("NumDim:"+numDim);
		pw.println("TotalNumTrainingExamples:"+totNumSamp);
		pw.println("NumberOfClasses:"+classTracker.size());
		pw.println("ClassIDsAndCounters: ");
		
		for(int i=0;i<classTracker.size();i++)
		{
			pw.println(classTracker.get(i).getClassLabel()+"\t"+classTracker.get(i).counter);
		}
		
		pw.println("LabelledTimeSeriesClassificationData:");
		for(int x=0;x<totNumSamp;x++)
		{
			pw.println();
			pw.println("ClassID:"+data.get(x).getClassLabel());
			pw.println("TimeSerieslength:"+data.get(x).getLength());
			pw.println("TimeSeriesData:");
			
			for(int i=0;i<data.get(x).getLength();i++)
			{
				for(int j=0;j<numDim;j++)
				{
					pw.print(data.get(x).getData().dataptr[i][j]);
					if(j<numDim-1)
						pw.print("\t");
				}
				pw.println();
			}			
		}
		
		
		//bf.write(content);
		//bf.close();
		pw.close();
		
	}
	
	public void loadDatasetToFile(String fname) throws IOException
	{
		
	}

	/**
	 * @return
	 */
	public int getCountOfData() {
		// TODO Auto-generated method stub
		return this.data.size();
		
	}

	/**
	 * @return
	 */
	public int getNumSamples() {
		// TODO Auto-generated method stub
		return this.totNumSamp;
	}

	/**
	 * @return
	 */
	public int getNumClasses() {
		// TODO Auto-generated method stub
		return classTracker.size();
	}

	/**
	 * @return
	 */
	public Vector<ClassTracker> getClassTracker() {
		// TODO Auto-generated method stub
		return this.classTracker;
	}
	
	public LabelledTimeSeriesClassificationData getClassData(int classLabel){
	    LabelledTimeSeriesClassificationData classData=new LabelledTimeSeriesClassificationData(numDim,null,null);
	    for(int x=0; x<totNumSamp; x++){
	        if( data.get(x).getClassLabel() == classLabel ){
	            classData.addSample( classLabel, data.get(x).getData() );
	        }
	    }
	    return classData;
	}

}
