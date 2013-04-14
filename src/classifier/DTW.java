/**
 * 
 */
package classifier;

import java.util.Vector;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Math;

import util.CircularBuffer;
import datastructure.LabelledTimeSeriesClassificationData;
import datastructure.Matrix;

/**
 * @author thayumaanavan
 *
 */

class IndexDist
{
	int x,y;
	double dist;
	public IndexDist()
	{
		x=y=0;
		dist=0;
	}
}

class DTWTemplate
{
	int classLabel,avgTempLength;
	Matrix timeSeries;
	double trainingMu,trainingSigma,threshold;
	
	
	
	public DTWTemplate(){
        classLabel = 0;
		trainingMu = 0.0;
		trainingSigma = 0.0;
		threshold=0.0;
		avgTempLength=0;
	}
}
public class DTW {

	private static final double NAN = Double.POSITIVE_INFINITY;
	private static final double DEFAULT_NULL_LIKELIHOOD_VALUE = 0;
	Vector< DTWTemplate > templatesBuffer;
	int numTemplates,avgTempLength;
	int numClasses;
    int predictedClassLabel;
    double bestDistance;
    Vector< Double > classLikelihoods;
    Vector< Double > classDistances;
    Vector< Integer > classLabels;
    Boolean trained;
    CircularBuffer<Vector<Double>>continuousInputDataBuffer;
    int numFeatures;
    double radius;
    double maxLikelihood;
	/**
	 * 
	 */
	public DTW() {
		// TODO Auto-generated constructor stub
		numTemplates=0;
		avgTempLength=0;
		radius=0.2;
		templatesBuffer=new Vector<DTWTemplate>();
		classLikelihoods=new Vector<Double>();
		classDistances=new Vector<Double>();
		classLabels=new Vector<Integer>();
		continuousInputDataBuffer=new CircularBuffer<Vector<Double>>();
	}
	
	public int getNumTemplates()
	{
		return numTemplates;
	}
	
	public int getPredictedClassLabel()
	{
		if( trained ) return predictedClassLabel; 
	    return 0; 
	}

	
	
	public Boolean train(LabelledTimeSeriesClassificationData trainingData)
	{
	    _train(trainingData);
	    
		return trained;
	}

	/**
	 * @param trainingData
	 */
	private Boolean _train(LabelledTimeSeriesClassificationData labelledTrainingData) {
		// TODO Auto-generated method stub
		int bestIndex=0,worstIndex=0;
		
		//templatesBuffer.clear();
		//classLabels.clear();
		trained = false;
	    continuousInputDataBuffer.clear();
		
		if( labelledTrainingData.getNumSamples() == 0 )
		{
			System.out.println("No samples in training data");
			return false;
		}
		
		numClasses = labelledTrainingData.getNumClasses();
		numTemplates = labelledTrainingData.getNumClasses();
	    numFeatures = labelledTrainingData.getNumDim();
		templatesBuffer.setSize(numTemplates);
	    classLabels.setSize(numClasses);
		avgTempLength = 0;
		
		LabelledTimeSeriesClassificationData trainingData=new LabelledTimeSeriesClassificationData( labelledTrainingData );

		
		for(int k=0;k<numTemplates;k++)
		{
			int classLabel=trainingData.getClassTracker().get(k).getClassLabel();
			LabelledTimeSeriesClassificationData classData=trainingData.getClassData(classLabel);
			int numExamples=classData.getNumSamples();
			bestIndex=worstIndex=0;
			templatesBuffer.set(k, new DTWTemplate());
			templatesBuffer.get(k).classLabel=classLabel;
			classLabels.set(k,classLabel);
			
			if(numExamples<1)
			{
				System.out.println("Number of example:1");
				return false;
			}
			if(numExamples==1)
			{
				bestIndex=worstIndex=0;
				templatesBuffer.get(k).threshold=0.0;
				
			}
			else
			{
				if( !_train_NDDTW(classData,templatesBuffer.get(k),bestIndex) )
				{
					return false;
				}
			}
			
			templatesBuffer.get(k).timeSeries=classData.getDataVector().get(bestIndex).getData();
			
			avgTempLength+=templatesBuffer.get(k).avgTempLength;
			
		}
		
		trained=true;
		avgTempLength=(int)avgTempLength/numTemplates;
		continuousInputDataBuffer.clear();
		
		predictedClassLabel = 0;
		
		return trained;
	}

	/**
	 * @param classData
	 * @param dtwTemplate
	 * @param bestIndex
	 * @return
	 */
	private boolean _train_NDDTW(
			LabelledTimeSeriesClassificationData trainingData,
			DTWTemplate dtwTemplate, int bestIndex) {
		// TODO Auto-generated method stub
		
		int numExamples=trainingData.getNumSamples();
		Vector<Double> results=new Vector<Double>(numExamples);
		Matrix distanceResults=new Matrix(numExamples,numExamples);
		dtwTemplate.avgTempLength=0;
		for(int m=0;m<numExamples;m++)
		{
			Matrix templateA,templateB;
			dtwTemplate.avgTempLength+=trainingData.getDataVector().get(m).getLength();
			
			templateA=trainingData.getDataVector().get(m).getData();
			for(int n=0;n<numExamples;n++)
			{
				if(m!=n)
				{
					templateB=trainingData.getDataVector().get(n).getData();
					
					double dist=0.0;
					dist=computeDistance(templateA,templateB);
					
					distanceResults.dataptr[m][n]=dist;
					results.add(m, dist+results.get(m));
				}
			}
		}
		
		for(int m=0;m<numExamples;m++)
			results.add(m, results.get(m)/(numExamples-1));
		
		bestIndex=0;
		double bestAverage=results.get(0);
		for(int m=1;m<numExamples;m++)
		{
			if(results.get(m)<bestAverage)
			{
				bestAverage =results.get(m);
				bestIndex=m;
			}
			
		}
		
		if (numExamples>2)
		{
			
			dtwTemplate.trainingMu=results.get(bestIndex);
			dtwTemplate.trainingSigma=0.0;
			
			for(int n=0;n<numExamples;n++)
			{
				if(n!=bestIndex)
				{
					dtwTemplate.trainingSigma +=Math.pow(distanceResults.dataptr[bestIndex][n]-results.get(bestIndex),2);
					
				}
			}
			dtwTemplate.trainingSigma=Math.sqrt(dtwTemplate.trainingSigma/(numExamples-2));
			
			dtwTemplate.threshold=dtwTemplate.trainingMu+(dtwTemplate.trainingSigma/**nullRejectionCoeff*/);
			
		}
		else
		{
			dtwTemplate.trainingMu=dtwTemplate.trainingSigma=0.0;
			
		}
		
		dtwTemplate.avgTempLength=(int) (dtwTemplate.avgTempLength/(double)numExamples);
		
		return true;
	}
	
	public Boolean predict(Matrix inputTimeSeries)
	{
		if(!trained)
		{
			System.out.println("not trained");
			return false;
		}
		Boolean debug=false;
		
		if(classLikelihoods.size()!=numTemplates)
			classLikelihoods.setSize(numTemplates);
		if(classDistances.size()!=numTemplates)
			classDistances.setSize(numTemplates);
		
		predictedClassLabel=0;
		maxLikelihood=DEFAULT_NULL_LIKELIHOOD_VALUE;
		for(int k=0;k<classLikelihoods.size();k++)
		{
			classLikelihoods.set(k, 0.0);
			classDistances.set(k, DEFAULT_NULL_LIKELIHOOD_VALUE);	
		}
		
		if(numFeatures!=inputTimeSeries.getNumCols())
		{
			return false;
		}
		
		double sum=0;
		for(int k=0;k<numTemplates;k++)
		{
			classDistances.set(k, computeDistance(templatesBuffer.get(k).timeSeries,inputTimeSeries));
			classLikelihoods.set(k,Math.exp(1.0-(classDistances.get(k))));
			sum +=classLikelihoods.get(k);
			
		}
		
		if(debug)
		{}
			
		Boolean sumIsZero=false;
		if(sum==0) sumIsZero=true;
		
		int closestTemplateIndex=0;
		bestDistance=classDistances.get(0);
		for(int k=1;k<numTemplates;k++)
		{
			if(classDistances.get(k)<bestDistance)
			{
				bestDistance=classDistances.get(k);
				closestTemplateIndex=k;
			}
		}
		
		maxLikelihood = 0;
		int maxLikelihoodIndex=0;
		if(!sumIsZero)
		{
			classLikelihoods.set(0,classLikelihoods.get(0)/sum);
			maxLikelihood=classLikelihoods.get(0);
			for(int k=1;k<numTemplates;k++)
			{
				classLikelihoods.set(k,classLikelihoods.get(k)/sum);
				if(classLikelihoods.get(k)>maxLikelihood)
				{
					maxLikelihood=classLikelihoods.get(k);
					maxLikelihoodIndex=k;
				}
			}
		}
		
		if(debug)
		{
			
		}
		
		/*if(useNullRejection)
		{
			
		}*/
		
		predictedClassLabel=templatesBuffer.get(closestTemplateIndex).classLabel;
		return true;
	}
	
	Boolean predict(Vector<Double> inputVector)
	{
		if(!trained)
			return false;
		
		predictedClassLabel=0;
		maxLikelihood=DEFAULT_NULL_LIKELIHOOD_VALUE;
		for(int c=0;c<classLikelihoods.size();c++)
			classLikelihoods.set(c, DEFAULT_NULL_LIKELIHOOD_VALUE);
		
		if(numFeatures!=inputVector.size())
			return false;
		
		continuousInputDataBuffer.push_back(inputVector);
		
		if(continuousInputDataBuffer.getNumValuesInBuffer() <avgTempLength)
			return false;
		
		Matrix predictionTimeSeries =new Matrix(avgTempLength,numFeatures);
		for(int i=0;i<predictionTimeSeries.getNumRows();i++)
			for(int j=0;j<predictionTimeSeries.getNumCols();j++)
				predictionTimeSeries.dataptr[i][j]=continuousInputDataBuffer.getBuffer(i).get(j);
		
		return predict(predictionTimeSeries);
	}
	
	
	double computeDistance(Matrix timeSeriesA ,Matrix timeSeriesB)
	{
		double[][] distMatrix=null;
		Vector<IndexDist> warpPath = new Vector<IndexDist>();
		IndexDist tempW = new IndexDist();
		int M=timeSeriesA.getNumRows();
		int N=timeSeriesB.getNumRows();
		int C=timeSeriesA.getNumCols();
		int i,j,k,index=0;
		double totalDist,v,normFactor=0.0;
		
		radius=Math.ceil(Math.min(M,N)/2.0);
		distMatrix=new double[M][];
		for(i=0;i<M;i++)
			distMatrix[i]=new double[N];
		//Euclidean distance
		for(i=0;i<M;i++)
		{
			for(j=0;j<N;j++)
			{
				distMatrix[i][j]=0.0;
				for(k=0;k<C;k++)
				{
					distMatrix[i][j]+=Math.sqrt(timeSeriesA.dataptr[i][k]-timeSeriesB.dataptr[j][k]);
				}
				distMatrix[i][j]=Math.sqrt(distMatrix[i][j]);
			}
		}
		
		double distance=Math.sqrt(d(M-1,N-1,distMatrix,M,N));
		
		if(Double.isInfinite(distance))
		{
			return Double.POSITIVE_INFINITY;
		}
		
		for(i=0;i<M;i++)
			for(j=0;j<N;j++)
				distMatrix[i][j]=Math.abs(distMatrix[i][j]);
		
		i=M-1;
		j=N-1;
		tempW.x = i;
		tempW.y = j;
	    tempW.dist = distMatrix[tempW.x][tempW.y];
		totalDist = distMatrix[tempW.x][tempW.y];
	    warpPath.addElement(tempW);
		

	    normFactor = 1;
		while( i != 0 && j != 0 ) {
			if(i==0) j--;
			else if(j==0) i--;
			else{
	            //Find the minimum cell to move to
				v = 99e+99;
				index = 0;
				if( distMatrix[i-1][j] < v ){ v = distMatrix[i-1][j]; index = 1; }
				if( distMatrix[i][j-1] < v ){ v = distMatrix[i][j-1]; index = 2; }
				if( distMatrix[i-1][j-1] < v ){ v = distMatrix[i-1][j-1]; index = 3; }
				switch(index){
					case(1):
						i--;
						break;
					case(2):
						j--;
						break;
					case(3):
						i--;
						j--;
						break;
					default:
	                    return Double.POSITIVE_INFINITY;
						
				}
			}
			normFactor++;
			tempW.x = i;
			tempW.y = j;
	        tempW.dist = distMatrix[tempW.x][tempW.y];
			totalDist += distMatrix[tempW.x][tempW.y];
			warpPath.addElement(tempW);
		}

		for(i=0; i<M; i++){
			
			distMatrix[i] = null;
		}
		
		distMatrix=null;
		
		return totalDist/normFactor;
	}

	
	
	/**
	 * @param m
	 * @param n
	 * @param distMatrix
	 * @param M
	 * @param N
	 * @return
	 */
	private double d(int m, int n, double[][] distMatrix, int M, int N) {
		// TODO Auto-generated method stub
		double dist=0;
		
	/*	if(m>0)
		{
			if(Math.abs((double)(n-(N/(M/m))))>radius)
			{
				if(n-(N/(M/m))>0)
				{
					for(int i=0;i<m;i++)
						for(int j=n;j<N;j++)
							distMatrix[i][j]=NAN;
					
				}
				else
				{
					for(int i=0;i<m;i++)
						for(int j=n;j<N;j++)
							distMatrix[i][j]=NAN;
				}
			}*/
		
		if( distMatrix[m][n] < 0 || Double.isNaN( distMatrix[m][n] ) ){
	        dist =Math.abs( distMatrix[m][n] );
	        return dist;
	    }

		if( m == 0 && n == 0 )
		{
	        dist = distMatrix[0][0];
	        distMatrix[0][0] = -distMatrix[0][0];
	        return dist;
	    }

		if( m == 0 )
		{
	        double contribDist = d(m,n-1,distMatrix,M,N);

	        dist = distMatrix[m][n] + contribDist;

	        distMatrix[m][n] = -dist;
	        return dist;
	    }
		else
		{
			if ( n == 0) 
			{
	            double contribDist = d(m-1,n,distMatrix,M,N);

	            dist = distMatrix[m][n] + contribDist;

	            distMatrix[m][n] = -dist;
	            return dist;
			}
	        else
	        {
	        	double contribDist1 = d(m-1,n-1,distMatrix,M,N);
	        	double contribDist2 = d(m-1,n,distMatrix,M,N);
            	double contribDist3 = d(m,n-1,distMatrix,M,N);
            	double minValue = 99e+99;
            	int index = 0;
            	if( contribDist1 < minValue ){ minValue = contribDist1; index = 1; }
				if( contribDist2 < minValue ){ minValue = contribDist2; index = 2; }
				if( contribDist3 < minValue ){ minValue = contribDist3; index = 3; }

				switch ( index )
				{
					case 1:
						dist = distMatrix[m][n] + minValue;
						break;
					case 2:
						dist = distMatrix[m][n] + minValue;
						break;
					case 3:
						dist = distMatrix[m][n] + minValue;
						break;

					default:
						break;
				}
				distMatrix[m][n] = -dist; 
				return dist;
	        }
		}

		
		//return dist;
	}

	
	
	public Boolean saveModelToFile(String name) throws FileNotFoundException
	{
		PrintWriter pw=new PrintWriter(name);
		
		if(!trained)
		{
			System.out.println("Model not yet trained");
			return false;
		}
		
		pw.println("DTW Model File");
		pw.println("NumberOfDimensions:"+numFeatures);
		pw.println("NumberOfClasses:"+numClasses);
		pw.println("NumberOfTemplates:"+numTemplates);
		//pw.println("DistanceMethod:Euclidea");
		pw.println("OverallAverageTemplateLength:"+avgTempLength);
		
		for(int i=0;i<numTemplates;i++)
		{
			pw.println("Template:"+i+1);
			pw.println("ClassLabel:"+templatesBuffer.get(i).classLabel);
			pw.println("TimeSeriesLength:"+templatesBuffer.get(i).timeSeries.getNumRows());
			pw.println("TemplateThreshold:"+templatesBuffer.get(i).threshold);
			pw.println("TrainingMu:"+templatesBuffer.get(i).trainingMu);
			pw.println("TrainingSigma:"+templatesBuffer.get(i).trainingSigma);
			pw.println("AverageTemplateLength:"+templatesBuffer.get(i).avgTempLength);
			pw.println("TimeSeries");
			
			for(int k=0;k<templatesBuffer.get(i).timeSeries.getNumRows();k++)
			{
				for(int j=0;j<templatesBuffer.get(i).timeSeries.getNumCols();j++)
					pw.print(templatesBuffer.get(i).timeSeries.dataptr[k][j]+"\t");
				pw.println();
			
			}
			pw.println();
		}
		return true;
	}
	
	public Boolean loadModelToFile(String name)
	{
		return true;
	}

}