package edu.gatech.earthquakes.vises;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Date;

import processing.core.PApplet;

import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;

public class DepthPlot extends Multi implements Filterable {
	private float[] quakeRadii;
	private float[][] drawingCoordinates;
	
	private Date[] timeRange;
	private double[] depthRange;
	private double[] magRange;
	
	public DepthPlot(int x, int y, int w, int h, DataSet displayData) {
		super(x, y, w, h, displayData);
		calculateRanges();
		calculateDrawingValues();
	}
	
	public void drawComponent(PApplet parent){
		super.drawComponent(parent);
		parent.strokeWeight(1);	
		for(int i=0; i< drawingCoordinates.length; i++){
			if(drawingCoordinates[i][1] != y){
				int color = DataRow.getColorFor(DataRow.DEPTH);
				float loc = (drawingCoordinates[i][1] - y - buffer) / h;
				color = Theme.changeSaturation(color, 1-loc);
				float brightness = PApplet.map(loc, 0f, 1f, 0f, 0.5f);
				color = Theme.changeBrightness(color, 0.75f-brightness);
				parent.fill(Theme.rgba(color,200));
				parent.stroke(color);
				parent.ellipse(drawingCoordinates[i][0], drawingCoordinates[i][1], quakeRadii[i]*2,quakeRadii[i]*2);
			}
		}
	}
	
	
	private float getCircleRadius(double mag) {
		float minDiameter = w/35;
		float maxDiameter = w/15;
		double maxArea = Math.PI*Math.pow(maxDiameter/2, 2);
		double minArea = Math.PI*Math.pow(minDiameter/2, 2);
		
		float area = (float) ((maxArea - minArea) * (mag - magRange[0]) / (magRange[1]-magRange[0]) + minArea);
		return (float)(Math.sqrt(area/Math.PI));
	}
	
	private void calculateDrawingValues(){
		float xoffset = (w-2*buffer)/(float)displayData.getDatum().size();
		
		drawingCoordinates = new float[displayData.getDatum().size()][2];
		quakeRadii = new float[displayData.getDatum().size()];
		int index = 0;
		for(DataRow d: displayData){
			//calculate the x coordinate
			drawingCoordinates[index][0] = x+buffer+xoffset*index;
			
			//calculate the y coordinate
			if(d.getValue(DataRow.DEPTH) != null)
				drawingCoordinates[index][1] = y+calculateY((double)d.getValue(DataRow.DEPTH));
			else{
				drawingCoordinates[index][1] = y;
			}
			//System.out.println(drawingCoordinates[index][1]);
		
			
			
			if(d.getValue(DataRow.MOMENT_MAGNITUDE) != null)
				quakeRadii[index] = getCircleRadius((double)d.getValue(DataRow.MOMENT_MAGNITUDE));
			else{
				quakeRadii[index] = (float) magRange[0];
			}
			
			index++;
			
		}
	}
	
	private float calculateY(double depth){
		return (float) ((h-buffer*2)*(depth-depthRange[0])/(depthRange[1]-depthRange[0]) + buffer);
	}
	
	private void calculateRanges(){
		timeRange = new Date[2];
		depthRange = new double[2];
		magRange = new double[2];
		
		for(DataRow d : displayData){
			//get the data from the current quake
			Date curDate = (Date) d.getValue(DataRow.DATE);
			double curDepth = depthRange[0];
			double curMag = magRange[0];
			
			if(d.getValue(DataRow.DEPTH)!= null)
				curDepth = (double) d.getValue(DataRow.DEPTH);
			if(d.getValue(DataRow.MOMENT_MAGNITUDE) != null)
				curMag = (double)d.getValue(DataRow.MOMENT_MAGNITUDE);
			//System.out.println(curMag);
			//if this is the first thing we've hit, set everything to the current quake
			if(timeRange[0] == null){
				timeRange[0] = curDate;
				timeRange[1] = curDate;
				depthRange[0] = curDepth;
				depthRange[1] = curDepth;
				magRange[0] = curMag;
				magRange[1] = curMag;
			}
			
			//check the time ranges
			if(curDate.before(timeRange[0]))
				timeRange[0] = curDate;
			else if(curDate.after(timeRange[1]))
				timeRange[1] = curDate;
			
			//check the depth ranges
			if(curDepth < depthRange[0])
				depthRange[0] = curDepth;
			else if(curDepth > depthRange[1])
				depthRange[1] = curDepth;
			
			//check the magnitude ranges
			if(curMag < magRange[0])
				magRange[0] = curMag;
			else if(curMag > magRange[1])
				magRange[1] = curMag;
		}
	}
	

	@Override
	public void filterBy(DataSet filteredData) {
		this.displayData = filteredData;
		calculateRanges();
		calculateDrawingValues();
		
	}
	
	public void resizeTo(Rectangle bounds) {
		super.resizeTo(bounds);
		
		calculateRanges();
		calculateDrawingValues();
	}

}
