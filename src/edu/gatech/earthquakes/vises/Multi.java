package edu.gatech.earthquakes.vises;

import edu.gatech.earthquakes.components.Controller;
import edu.gatech.earthquakes.model.DataSet;

/**
 * A visualization that displays more than one earthquake
 * 
 * @author Elizabeth
 * 
 */
public abstract class Multi extends AbstractVisualization {

    protected DataSet displayData;

    public Multi(int x, int y, int w, int h, DataSet displayData) {
	super(x, y, w, h, "Multi - FIX ME");

	this.displayData = displayData;

    }

    public Multi(int x, int y, int w, int h, DataSet displayData, String title) {
	super(x, y, w, h, title);
	this.displayData = displayData;
    }

    protected final void applyFilterGlobally() {
	Controller.applyFilter(displayData);
    }
}
