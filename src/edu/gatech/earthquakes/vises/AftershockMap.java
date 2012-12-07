package edu.gatech.earthquakes.vises;

import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import processing.core.PApplet;
import edu.gatech.earthquakes.components.Controller;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.interfaces.Interactable;
import edu.gatech.earthquakes.model.DataComparator;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;
import edu.gatech.earthquakes.model.Interaction;

public class AftershockMap extends Individual implements Interactable,
        Filterable {

    private double[] latRange;
    private double[] lonRange;
    private double[] magRange;

    private double[] highlightedPos;
    private DataSet aftershocks;
    private static int mainColor = Theme.getPalletteColor(16);
    private static int shockColor = Theme.getPalletteColor(17);

    public AftershockMap(int x, int y, int w, int h, DataRow displayData,
	    DataSet filterData) {
	super(x, y, w, h, displayData);
	filterBy(filterData);
    }

    public void drawComponent(PApplet parent) {
	super.drawComponent(parent);
	parent.textAlign(PApplet.CENTER);

	double[][] coords = getCoordinates();
	double[] m = getMagnitudes();
	double[] mainCoords = getDrawingCoord((double)displayData.getValue(DataRow.LONGITUDE), (double)displayData.getValue(DataRow.LATTITUDE));
	//System.out.println(mainCoords[0]);
	for (int i = 0; i < coords.length; i++) {

	    // for magnitude, min and max are assumed to be 3 and 7 based on
	    // moment magnitude numbers
	    double[] c = getDrawingCoord(coords[i][0], coords[i][1]);
	    System.out.println(c[0]);

	    if (highlightedPos != null && coords[i][0] == highlightedPos[0]
		    && coords[i][1] == highlightedPos[1]) {
		parent.fill(Theme.rgba(Theme.HIGHLIGHTED_COLOR, 0x55));
		parent.stroke(Theme.rgba(Theme.HIGHLIGHTED_COLOR, 0xbb));
	    } 
	    else if(c[0] == mainCoords[0] && c[1] == mainCoords[1] ){
	       
	        parent.fill(Theme.rgba(mainColor, 0x88));
                parent.stroke(Theme.rgba(mainColor, 0xdd));
	    }
	    else {
		parent.fill(Theme.rgba(shockColor, 0x55));
		parent.stroke(Theme.rgba(shockColor, 0xbb));
	    }

	    parent.ellipse((float) c[0], (float) c[1],
		    (float) getCircleRadius(m[i]) * 2,
		    (float) getCircleRadius(m[i]) * 2);

	}

	drawAxes(parent);
    }

    private void drawAxes(PApplet parent) {
	parent.stroke(Theme.getDarkUIColor());
	parent.noFill();

	parent.rect(x + buffer, y + buffer, w - buffer * 2, h - buffer * 2);

	parent.fill(Theme.getDarkUIColor());
	parent.textSize(w / 60);

	// make and label the latitude tick marks
	double lon = 0;
	for (int i = 0; i < (w - buffer * 2); i += 50) {
	    lon = (((lonRange[1] - lonRange[0]) * i) / (w - buffer * 2))
		    + lonRange[0];
	    parent.line(x + buffer + i, y + h - buffer - 2, x + buffer + i, y
		    + h - buffer + 2);
	    parent.text(formatDegrees(lon), x + buffer + i, y + h - buffer / 4);
	}

	// make and label the longitude tick marks
	double lat = 0;
	for (int i = 0; i < (h - buffer * 2); i += 50) {
	    lat = (((latRange[1] - latRange[0]) * i) / (h - buffer * 2))
		    + latRange[0];
	    parent.line(x + buffer - 2, y + h - buffer - i, x + buffer + 2, y
		    + h - buffer - i);

	    parent.pushMatrix();
	    parent.translate(x + 2 * buffer / 3, y + h - buffer - i);
	    parent.rotate(-PApplet.PI / 2);
	    parent.text(formatDegrees(lat), 0, 0);
	    parent.popMatrix();
	}
    }

    private String formatDegrees(double lat) {
	String formatted = "";
	int degree = (int) lat;
	double minutes = (lat - degree) * 60;
	formatted += degree + "\u00B0 " + (int) minutes + "'";
	return formatted;
    }

    private double[][] getCoordinates() {
	double[][] coords = new double[aftershocks.getDatum().size()][2];

	int i = 0;
	for (DataRow quake : aftershocks) {
	    coords[i][0] = (Double) quake.getValue(DataRow.LONGITUDE);
	    coords[i++][1] = (Double) quake.getValue(DataRow.LATTITUDE);
	}

	return coords;
    }

    private double[] getMagnitudes() {

	double[] mag = new double[aftershocks.getDatum().size()];
	int i = 0;
	for (DataRow quake : aftershocks) {
	    mag[i++] = (Double) quake.getValue(DataRow.MOMENT_MAGNITUDE);
	}

	return mag;
    }

    private void calculateRanges() {
	double[][] coords = getCoordinates();

	double[] lon = new double[coords.length];
	double[] lat = new double[coords.length];

	for (int i = 0; i < coords.length; i++) {
	    lon[i] = coords[i][0];
	    lat[i] = coords[i][1];
	}
	
	Arrays.sort(lon);
	Arrays.sort(lat);
	

	double dif = 0;
	double buffer = .1;

	if (aftershocks.getDatum().size() > 2) {
	    if (lat[lat.length - 1] - lat[0] > lon[lon.length - 1] - lon[0]) {
		dif = lat[lat.length - 1] - lat[0];

		latRange = new double[] { lat[0] - buffer,
		        lat[lat.length - 1] + buffer };
		lonRange = new double[] { lon[0] - buffer,
		        lon[0] + dif + buffer };
	    } else {
		dif = lon[lon.length - 1] - lon[0];

		lonRange = new double[] { lon[0] - buffer,
		        lon[lon.length - 1] + buffer };
		latRange = new double[] { lat[0] - buffer,
		        lat[0] + dif + buffer };
	    }
	} else {
	    double mainLat = (Double) displayData.getValue(DataRow.LATTITUDE);
	    double mainLon = (Double) displayData.getValue(DataRow.LONGITUDE);

	    latRange = new double[] { mainLat - 1 - buffer,
		    mainLat + 1 + buffer };
	    lonRange = new double[] { mainLon - 1 - buffer,
		    mainLon + 1 + buffer };
	}

	double[] mags = getMagnitudes();
	Arrays.sort(mags);
	magRange = new double[] { mags[0] - .5, mags[mags.length - 1] - .5 };
    }

    @Override
    public void handleInput(Interaction interaction) {

	int mx = interaction.getParentApplet().mouseX;
	int my = interaction.getParentApplet().mouseY;

	if (mx > x && mx < x + w && my > y && my < y + h) {

	    double[][] coords = getCoordinates();
	    double[] mag = getMagnitudes();
	    boolean found = false;

	    for (int i = 0; i < coords.length && !found; i++) {
		double[] c = getDrawingCoord(coords[i][0], coords[i][1]);

		if (Math.abs(interaction.getParentApplet().mouseX - c[0]) < getCircleRadius(mag[i])
		        && Math.abs(interaction.getParentApplet().mouseY - c[1]) < getCircleRadius(mag[i])) {
		    highlightedPos = new double[] { coords[i][0], coords[i][1] };
		    ArrayList<DataRow> rowList = new ArrayList<>(
			    aftershocks.getDatum());
		    Controller.BRUSH_BUS.post(new DataSet(rowList.get(i)));
		    found = true;
		}

		if (!found) {
		    highlightedPos = null;
		    Controller.BRUSH_BUS.post(new DataSet(
			    new HashSet<DataRow>()));
		}

	    }
	}
    }

    /*
     * All of th scaling is done with the formula of:
     *
     * f(x) = (b-a)(x-min)/(max-min) + a
     *
     * where [min,max] maps to [a,b]
     */

    private float getCircleRadius(double mag) {
	float minDiameter = w / 12;
	float maxDiameter = w / 10;
	double maxArea = Math.PI * Math.pow(maxDiameter / 2, 2);
	double minArea = Math.PI * Math.pow(minDiameter / 2, 2);

	float area = (float) ((maxArea - minArea) * (mag - magRange[0])
	        / (magRange[1] - magRange[0]) + minArea);
	return (float) (Math.sqrt(area / Math.PI));
    }

    private double[] getDrawingCoord(double lon, double lat) {
	double qx = ((w - buffer * 2) * (lon - lonRange[0]))
	        / (lonRange[1] - lonRange[0]);
	double qy = ((h - buffer * 2) * (lat - latRange[0]))
	        / (latRange[1] - latRange[0]);

	return new double[] { x + qx + buffer, y + h - qy - buffer };
    }
    
    public void resizeTo(Rectangle bounds) {
       super.resizeTo(bounds);
       if (aftershocks.getDatum().size() > 0) {
           calculateRanges();
       }
    }
    

    @Override
    public void filterBy(DataSet filteredData) {
	Date date = (Date) displayData.getValue(DataRow.DATE);
	System.out.println(date);
	Set<DataRow> shocks = new HashSet<DataRow>();

	for (DataRow quake : filteredData) {
	    if (quake.getValue(DataRow.DEPENDENCY).equals(
		    DataRow.Dependency.DEPENDENT)
		    && quake.getValue(DataRow.MAIN_DATE).equals(date))
		shocks.add(quake);
	    if (quake.getValue(DataRow.LATTITUDE).equals(displayData.getValue(DataRow.LATTITUDE))
	            && quake.getValue(DataRow.LONGITUDE).equals(displayData.getValue(DataRow.LONGITUDE))){
		shocks.add(displayData);
		System.out.println("added");
	    }
	}
	aftershocks = new DataSet(shocks);
	if (aftershocks.getDatum().size() > 0) {
	    calculateRanges();
	}
    }
}
