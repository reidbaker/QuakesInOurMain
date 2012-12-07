package edu.gatech.earthquakes.vises;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import processing.core.PApplet;

import edu.gatech.earthquakes.components.Controller;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.interfaces.Interactable;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;
import edu.gatech.earthquakes.model.Interaction;

public class DepthPlot extends Multi implements Filterable, Interactable {
	private float[] quakeRadii;
	private float[][] drawingCoordinates;
	
	private Date[] timeRange;
	private double[] depthRange;
	private double[] magRange;
	
	private int highlightedIndex;

	public DepthPlot(int x, int y, int w, int h, DataSet displayData) {
		super(x, y, w, h, displayData, "Depth vs Time");
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
				color = Theme.changeSaturation(color, 1-loc,false);

				if(i == highlightedIndex){
	                parent.fill(Theme.rgba(Theme.HIGHLIGHTED_COLOR, 150));
	                parent.stroke(Theme.rgba(Theme.HIGHLIGHTED_COLOR, 230));
				} else {
				    float brightness = PApplet.map(loc, 0f, 1f, 0f, 0.5f);
				    color = Theme.changeBrightness(color, 0.75f-brightness,false);
	                parent.fill(Theme.rgba(color,200));
	                parent.stroke(color);
				}
				parent.ellipse(drawingCoordinates[i][0], drawingCoordinates[i][1], quakeRadii[i]*2,quakeRadii[i]*2);
			}
		}
		
		drawAxes(parent);
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
		//System.out.println(depthRange[1]);
	}
	
	private void drawAxes(PApplet parent){
	    int verticalOffset = h/20;
	    //int depthOffset = depth[1
	   // System.out.println(verticalOffset);
	    for(int i=0; i< depthRange[1]; i+= verticalOffset ){
	        parent.line(x+buffer-2, y+buffer+i, x+buffer+2, y+buffer+i);
	        parent.text(i+"", x+buffer/2, y+buffer+i);
	    }
	}

	@Override
	public void filterBy(DataSet filteredData) {
		this.displayData = filteredData;
		//calculateRanges();
		calculateDrawingValues();
		
	}
	
	public void resizeTo(Rectangle bounds) {
		super.resizeTo(bounds);
		
		//calculateRanges();
		calculateDrawingValues();
	}

	@Override
    public void handleInput(Interaction interaction) {

        int mX = interaction.getParentApplet().mouseX;
        int mY = interaction.getParentApplet().mouseY;
        //check if mouse in within the vis
        if (mX > x && mX < x + w && mY > y && mY < y + h) {

            float[] depths = getDepths();
            float[] mag = getMagnitudes();
            boolean found = false;

            for (int i = 0; i < depths.length && !found; i++) {
                //if the mouse is within radius of the current earthquake
                if(Math.abs(mX - drawingCoordinates[i][0]) < getCircleRadius(mag[i])
                        && Math.abs(mY - drawingCoordinates[i][1]) < getCircleRadius(mag[i])) {
                    highlightedIndex = i;
                    ArrayList<DataRow> rowList = new ArrayList<>(displayData.getDatum());
                    Controller.BRUSH_BUS.post(new DataSet(rowList.get(i)));
                    found = true;
                }

                if (!found) {
                    highlightedIndex = -1;
                    Controller.BRUSH_BUS.post(new DataSet(
                            new HashSet<DataRow>()));
                }

            }
        }
    }

    private float[] getDepths() {
        if (displayData != null) {
            float[] depths = new float[displayData.getDatum().size()];
            int i = 0;
            for (DataRow d : displayData) {
                if (d.getValue(DataRow.DEPTH) != null) {
                    depths[i] = ((Double) d.getValue(DataRow.DEPTH))
                            .floatValue();
                } else {
                    depths[i] = 0.0f;
                }
                i++;
            }
            return depths;
        } else {
            System.err.println("all the things are broken");
            return null;
        }
    }

    private float[] getMagnitudes() {

        float[] mag = new float[displayData.getDatum().size()];
        int i = 0;
        for (DataRow quake : displayData) {
            mag[i] = ((Double) quake.getValue(DataRow.MOMENT_MAGNITUDE))
                    .floatValue();
            i++;
        }
        return mag;
    }

}
