package edu.gatech.earthquakes.vises;

import java.awt.Rectangle;
import java.util.ArrayList;
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
    private int numberMissing;
    private int textSize;

    private long minTime, maxTime;

    public DepthPlot(int x, int y, int w, int h, DataSet displayData) {
	super(x, y, w, h, displayData, "Depth vs Time");
	calculateRanges();
	calculateDrawingValues();
	textSize = Math.min(w / 20, h / 20);
    }

    public void drawComponent(PApplet parent) {
	super.drawComponent(parent);
	parent.strokeWeight(1);
	drawAxes(parent);
	for (int i = 0; i < drawingCoordinates.length; i++) {
	    if (drawingCoordinates[i][1] != y) {
		int color = DataRow.getColorFor(DataRow.DEPTH);

		float loc = (drawingCoordinates[i][1] - y - buffer) / h;
		color = Theme.changeSaturation(color, 1 - loc, false);

		if (i == highlightedIndex) {
		    parent.fill(Theme.rgba(Theme.HIGHLIGHTED_COLOR, 150));
		    parent.stroke(Theme.HIGHLIGHTED_COLOR);
		} else {
		    float brightness = PApplet.map(loc, 0f, 1f, 0f, 0.5f);
		    color = Theme.changeBrightness(color, 0.75f - brightness,
			    false);
		    parent.fill(Theme.rgba(color, 150));
		    parent.stroke(color);
		}
		parent.ellipse(drawingCoordinates[i][0],
		        drawingCoordinates[i][1], quakeRadii[i] * 2,
		        quakeRadii[i] * 2);
	    }
	}

	parent.textAlign(PApplet.LEFT);
	parent.textSize(textSize);
	parent.fill(Theme.getDarkUIColor());
	String displayOutput = "# missing depth: " + numberMissing;
	parent.text(displayOutput, x + buffer, y + h - 2 * buffer, x + w - 2
	        * buffer, y + h);

    }

    private float getCircleRadius(double mag) {
	float minDiameter = w / 35;
	float maxDiameter = w / 15;
	double maxArea = Math.PI * Math.pow(maxDiameter / 2, 2);
	double minArea = Math.PI * Math.pow(minDiameter / 2, 2);

	float area = (float) ((maxArea - minArea) * (mag - magRange[0])
	        / (magRange[1] - magRange[0]) + minArea);
	return (float) (Math.sqrt(area / Math.PI));
    }

    private void calculateDrawingValues() {
	numberMissing = 0;

	drawingCoordinates = new float[displayData.getDatum().size()][2];
	quakeRadii = new float[displayData.getDatum().size()];

	minTime = Long.MAX_VALUE;
	maxTime = Long.MIN_VALUE;
	for (DataRow d : displayData) {
	    Date date = (Date) d.getValue(DataRow.DATE);
	    long time = date.getTime();
	    if (time < minTime) {
		minTime = time;
	    }
	    if (time > maxTime) {
		maxTime = time;
	    }
	}

	int index = 0;
	for (DataRow d : displayData) {
	    // calculate the x coordinate
	    Date date = (Date) d.getValue(DataRow.DATE);
	    long time = date.getTime();
	    drawingCoordinates[index][0] = PApplet.map(time, minTime, maxTime,
		    x + buffer, x + w - buffer);

	    // calculate the y coordinate
	    if (d.getValue(DataRow.DEPTH) != null)
		drawingCoordinates[index][1] = y
		        + calculateY((double) d.getValue(DataRow.DEPTH));
	    else {
		drawingCoordinates[index][1] = y;
		numberMissing++;
	    }

	    if (d.getValue(DataRow.MOMENT_MAGNITUDE) != null)
		quakeRadii[index] = getCircleRadius((double) d
		        .getValue(DataRow.MOMENT_MAGNITUDE));
	    else {
		quakeRadii[index] = (float) magRange[0];
	    }

	    index++;

	}
    }

    private float calculateY(double depth) {
	return (float) ((h - buffer * 2) * (depth - depthRange[0])
	        / (depthRange[1] - depthRange[0]) + buffer);
    }

    private void calculateRanges() {
	timeRange = new Date[2];
	depthRange = new double[2];
	magRange = new double[2];

	for (DataRow d : displayData) {
	    // get the data from the current quake
	    Date curDate = (Date) d.getValue(DataRow.DATE);
	    double curDepth = depthRange[0];
	    double curMag = magRange[0];

	    if (d.getValue(DataRow.DEPTH) != null) {
		curDepth = (double) d.getValue(DataRow.DEPTH);
	    }
	    if (d.getValue(DataRow.MOMENT_MAGNITUDE) != null) {
		curMag = (double) d.getValue(DataRow.MOMENT_MAGNITUDE);
	    }
	    // if this is the first thing we've hit, set everything to the
	    // current quake
	    if (timeRange[0] == null) {
		timeRange[0] = curDate;
		timeRange[1] = curDate;
		depthRange[0] = curDepth;
		depthRange[1] = curDepth;
		magRange[0] = curMag;
		magRange[1] = curMag;
	    }

	    // check the time ranges
	    if (curDate.before(timeRange[0]))
		timeRange[0] = curDate;
	    else if (curDate.after(timeRange[1]))
		timeRange[1] = curDate;

	    // check the depth ranges
	    if (curDepth < depthRange[0])
		depthRange[0] = curDepth;
	    else if (curDepth > depthRange[1])
		depthRange[1] = curDepth;

	    // check the magnitude ranges
	    if (curMag < magRange[0])
		magRange[0] = curMag;
	    else if (curMag > magRange[1])
		magRange[1] = curMag;
	}
    }

    private void drawAxes(PApplet parent) {
       
	//the size in pixels of a unit of depth 
	float depthPixelOffset = (h-buffer*2)/(float)depthRange[1];
	
	
	parent.stroke(0xaa);
	parent.fill(0);
	parent.textSize(depthPixelOffset*2.5f);
	parent.textAlign(PApplet.CENTER);
	
	for(int i= 0;  i< depthRange[1]; i+= 5){
	    parent.line(x + buffer - 2, y + buffer + i*depthPixelOffset, x + w - buffer, y + buffer + i*depthPixelOffset);
            parent.pushMatrix();
            parent.translate(x + 2*buffer / 3, y + buffer + i*depthPixelOffset);
            parent.rotate(-PApplet.PI / 2);
            parent.text(i+ "", 0, 0);
            parent.popMatrix();
	}
    }

    @Override
    public void filterBy(DataSet filteredData) {
	this.displayData = filteredData;
	calculateDrawingValues();

    }

    public void resizeTo(Rectangle bounds) {
	super.resizeTo(bounds);;
	calculateDrawingValues();
    }

    @Override
    public void handleInput(Interaction interaction) {

	int mX = interaction.getParentApplet().mouseX;
	int mY = interaction.getParentApplet().mouseY;
	// check if mouse in within the vis
	if (mX > x && mX < x + w && mY > y && mY < y + h) {

	    float[] depths = getDepths();
	    float[] mag = getMagnitudes();
	    boolean found = false;
	    int lastDist = Integer.MAX_VALUE;
	    int distanceToMouse = Integer.MAX_VALUE;

	    for (int i = 0; i < depths.length; i++) {
		distanceToMouse = (int) Math.round(Math.sqrt((Math.abs(mX
		        - drawingCoordinates[i][0]) + Math.abs(mY
		        - drawingCoordinates[i][1]))));
		// if the mouse is within radius of the current earthquake
		if (lastDist > distanceToMouse
		        && Math.abs(mY - drawingCoordinates[i][1]) < getCircleRadius(mag[i])
		        && Math.abs(mX - drawingCoordinates[i][0]) < getCircleRadius(mag[i])) {
		    highlightedIndex = i;
		    ArrayList<DataRow> rowList = new ArrayList<>(
			    displayData.getDatum());
		    Controller.applyBrushing(new DataSet(rowList.get(i)));
		    found = true;
		    lastDist = distanceToMouse;
		}

		if (!found) {
		    highlightedIndex = -1;
		    Controller.applyBrushing(new DataSet(
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
