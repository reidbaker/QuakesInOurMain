package edu.gatech.earthquakes.vises;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.model.DataComparator;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;

// name subject to change
/*
 * The idea is that this is a set of circles that can show two different types of groupings.
 * Right now, its being done for location and then by quake type
 */

public class NestedCirclePlot extends Aggregate implements Filterable {
	//the thing that will be grouped by after location
	private String dataType;
	private DataComparator dataComp;
	private double[][] computedGrid;
	private boolean nominal;
	//private HashSet<String, >
	public NestedCirclePlot(int x, int y, int w, int h, DataSet displayData, String dataType) {
		super(x, y, w, h, displayData);
		this.dataType = dataType;
		DataComparator.CompareCategories category = null;
		
		int numCircles = 0;
		switch(dataType){
		case DataRow.MOMENT_MAGNITUDE:
			category = DataComparator.CompareCategories.MAGNITUDE;
			nominal = false;
			numCircles = 7;
			break;
		case DataRow.DEPENDENCY:
			category = DataComparator.CompareCategories.DEPENDENCY;
			nominal = true;
			break;
		case DataRow.TYPE:
			category = DataComparator.CompareCategories.TYPE;
			nominal = true;
			break;
		}
		dataComp = new DataComparator(DataComparator.CompareCategories.CONTINENT, category);
		
		
		computeData();
	}
	
	private void computeData(){
		ArrayList<DataRow> list = new ArrayList<DataRow>(displayData.getDatum());
		Collections.sort(list, dataComp);
		
		if(nominal){
			int numBuckets = 0;
			if(dataType.equals(DataRow.DEPENDENCY))
				numBuckets = 3;
			else
				numBuckets = 5;
			
			computedGrid = new double[DataRow.Continent.values().length][numBuckets];
			
			int contIndex = 0;
			String contName = list.get(0).getValue(DataRow.CONTINENT).toString();
			int bucketIndex = 0;
			String bucketName = list.get(0).getValue(dataType).toString();
			//System.out.println("BucketName: " +bucketName);
			
			for(int i=0; i<list.size(); i++){
				//current continent we're at
				String curContName = list.get(i).getValue(DataRow.CONTINENT).toString();
				//current "bucket" we're at
				String curBucketName = list.get(i).getValue(dataType).toString();
				//check if we're on the same continent
				if(contName.equals(curContName)){
					//if we're not on the same bucket, increment the bucket index
					if(!bucketName.equals(curBucketName)){
						bucketName = curBucketName;
						bucketIndex++;
						System.out.println(bucketIndex);
					}
				}
				//if we're not on the same continent, increment the continent index and reset the bucket info
				else{
					contIndex++;
					bucketIndex = 0;
					bucketName = curBucketName;
					contName = curContName;
				}
				
				//increment the count
				computedGrid[contIndex][bucketIndex]++;
			}
		}
		for(double[] d: computedGrid){
			System.out.println(Arrays.toString(d));
		}
		
		
	}
	
	@Override
	public void filterBy(DataSet filteredData) {
		
		
	}





}
