package edu.gatech.earthquakes.vises;

import edu.gatech.earthquakes.model.DataRow;

public abstract class Individual extends AbstractVisualization {

    // the current quake that is displayed by this particular individual vis
    protected DataRow displayData;

    public Individual(int x, int y, int w, int h, DataRow displayData) {
	this(x, y, w, h, displayData, "Individual - FIX ME");
    }

    public Individual(int x, int y, int w, int h, DataRow displayData,
	    String title) {
	super(x, y, w, h, title);
	this.displayData = displayData;
    }
}
