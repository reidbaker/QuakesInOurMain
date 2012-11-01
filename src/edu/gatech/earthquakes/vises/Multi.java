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

	public Multi(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	protected final void applyFilterGlobally() {
		Controller.applyFilter(displayData);
	}
}
