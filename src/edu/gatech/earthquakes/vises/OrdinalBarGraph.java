package edu.gatech.earthquakes.vises;

import processing.core.PApplet;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;

public class OrdinalBarGraph extends BarGraph{
	
	private Double min, max;
	
	public OrdinalBarGraph(int x, int y, int w, int h, DataSet displayData, String dataType) {
		super(x, y, w, h,displayData,dataType);
		calculateDataRange();
	}

	@Override
	public void drawComponent(PApplet parent) {
		
	}
	
	/**
	 * Calculates the minimum and maximum range of our data type
	 * 
	 */
	public void calculateDataRange(){
		
		for(DataRow row: displayData){
			if(row.getValue(dataType) != null)
				if(min == null || (Double)row.getValue(dataType) < min)
					min = (Double)row.getValue(dataType);
				if(max == null || (Double)row.getValue(dataType) > max)
					max = (Double)row.getValue(dataType);
		}
		
	}
	
	public void calcNumDivisions(){
		
	}
	
	public void drawAxes(){
		//draw the axes
	}

}
