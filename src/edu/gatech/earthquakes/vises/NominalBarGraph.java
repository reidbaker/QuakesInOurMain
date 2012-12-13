package edu.gatech.earthquakes.vises;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Set;

import processing.core.PApplet;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;

public class NominalBarGraph extends BarGraph {
    private Hashtable<String, Integer> bars;
    private volatile String longestTitle = "";

    public NominalBarGraph(int x, int y, int w, int h, DataSet displayData,
	    String dataType) {
	super(x, y, w, h, displayData, dataType, "Earthquake " + dataType);
	createBars();
    }

    @Override
    public void drawComponent(PApplet parent) {
	super.drawComponent(parent);

	parent.noStroke();

	// parent.fill(Theme.getDarkUIColor());

	// width of the bars - scales based on the number of bars that we have
	int barW;
	if (numDivisions > 0) {
	    barW = (w - buffer * 2 - 2 * numDivisions) / numDivisions;
	} else {
	    barW = w - buffer * 2;
	}
	int barX = x + buffer + 4;
	// the scale factor for the height of the bars
	float heightScale = (h - buffer * 2.0f) / calcMax();

	parent.textAlign(PApplet.CENTER);
	int textSize = barW / 4;
	parent.textSize(textSize);

	while (parent.textWidth(longestTitle) > (h - buffer - 10)) {
	    textSize--;
	    parent.textSize(textSize);
	}

	ArrayList<String> sortedKeys = new ArrayList<>(bars.keySet());
	Collections.sort(sortedKeys);
	for (String key : sortedKeys) {
	    parent.fill(DataRow.getColorFor(key));
	    parent.stroke(Theme.rgba(DataRow.getColorFor(key), 100));
	    parent.rect(barX, y + (h - buffer - bars.get(key) * heightScale),
		    barW, bars.get(key) * heightScale);
	    barX += barW + 2;
	    parent.fill(Theme.getDarkUIColor());
	    parent.textAlign(PApplet.LEFT);
	    parent.pushMatrix();
	    parent.translate(barX - barW / 2, y + h - (buffer) - 5);
	    parent.rotate(-1 * PApplet.HALF_PI);
	    parent.text(key.toString(), 0, 0);
	    parent.popMatrix();
	}

	drawAxes(parent);
    }

    private void drawAxes(PApplet parent) {
	parent.stroke(Theme.getDarkUIColor());
	parent.fill(Theme.getDarkUIColor());

	parent.line(x + buffer, y + h - buffer, x + w - buffer, y + h - buffer); // bottom
	parent.line(x + buffer, y + buffer, x + buffer, y + h - buffer); // left

	int numTicks = 10;
	int tickVal = calcMax() / numTicks;
	int tickLabel = 0;

	parent.textSize(8);
	parent.textAlign(PApplet.CENTER);
	for (int i = 0; i <= h - buffer * 2; i += (h - buffer * 2) / numTicks) {
	    parent.pushMatrix();
	    parent.line(x + buffer - 2, y + h - buffer - i, x + buffer + 2, y
		    + h - buffer - i);
	    parent.translate(x + buffer / 2, y + h - buffer - i);
	    parent.rotate(-PApplet.PI / 2);
	    parent.text(tickLabel + "", 0, 0);
	    tickLabel += tickVal;
	    parent.popMatrix();
	}
    }

    /**
     * Finds the value of the highest bar
     * 
     * @return
     */
    private int calcMax() {
	int max = 0;
	for (Object key : bars.keySet()) {
	    if (max < bars.get(key))
		max = bars.get(key);
	}
	return max;
    }

    private void createBars() {
	bars = new Hashtable<String, Integer>();

	for (DataRow row : displayData) {
	    if (row.getValue(dataType) != null) {
		if (bars.containsKey(row.getValue(dataType).toString()))
		    bars.put(row.getValue(dataType).toString(),
			    bars.get(row.getValue(dataType).toString()) + 1);

		else
		    bars.put(row.getValue(dataType).toString(), 1);
	    }
	}
	// to stop divide by zero
	numDivisions = Math.max(bars.size(), 1);

	Set<String> keySet = bars.keySet();
	int maxSize = 0;
	for (String key : keySet) {
	    if (key.length() > maxSize) {
		maxSize = key.length();
		longestTitle = key;
	    }
	}
    }

    @Override
    public void filterBy(DataSet ds) {
	super.filterBy(ds);
	createBars();
    }

}
