package edu.gatech.earthquakes.vises;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import processing.core.PApplet;

import edu.gatech.earthquakes.components.Theme;
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
	private double maxVal = 0;
	
	public NestedCirclePlot(int x, int y, int w, int h, DataSet displayData, String dataType) {
		super(x, y, w, h, displayData);
		this.dataType = dataType;
		DataComparator.CompareCategories category = null;
		
		switch(dataType){
		case DataRow.MOMENT_MAGNITUDE:
			category = DataComparator.CompareCategories.MAGNITUDE;
			nominal = false;
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
	
	public void drawComponent(PApplet parent){
		super.drawComponent(parent);
		
		float drawY = y + h - buffer;
		float maxCircleSize = getCircleSize(maxVal);
		int[] colors = Theme.getColorPallette(computedGrid[0].length);
		boolean right = false;
		
		for(double[] values: computedGrid){
			
			int colorIndex = 0;
			float firstSize = getCircleSize(values[0]);
			
			for(double val: values){
				int curColor = colors[colorIndex++];
				
				parent.fill(Theme.rgba(curColor, 100));
				parent.stroke(curColor);
				
				float size = getCircleSize(val);
				if(right)
					parent.ellipse(x+(3*w/4), drawY-(maxCircleSize-firstSize+size)/2, size, size);
				else
					parent.ellipse(x+(w/4), drawY-(maxCircleSize-firstSize+size)/2, size, size);
			}
			if(right)
				drawY -= maxCircleSize;	
			right = !right;
		}
		
	}
	
	/*
	 * All of th scaling is done with the formula of:
	 * 
	 * f(x) = (b-a)(x-min)/(max-min) + a
	 * 
	 * where [min,max] maps to [a,b]
	 */
	private float getCircleSize(double count) {
		float minSize = 0;
		float maxSize = (h-buffer*2) / (computedGrid.length/2);
		
		return (float)((maxSize - minSize) * count /maxVal + minSize);
	}
	
	private void computeMaxVal(){
		for(double[] d: computedGrid)
			if(d[0] > maxVal)
				maxVal = d[0];
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
			Arrays.sort(d);
			for(int i=0; i<d.length/2; i++){
				double temp = d[d.length-1 -i];
				d[d.length-1-i] = d[i];
				d[i] = temp;
			}
			System.out.println(Arrays.toString(d));
		}
		
		computeMaxVal();
	}
	
	@Override
	public void filterBy(DataSet filteredData) {
		
		
	}





}
