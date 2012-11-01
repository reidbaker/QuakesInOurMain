package edu.gatech.earthquakes.vises;

import edu.gatech.earthquakes.model.DataSet;

/**
 * A visualization that displays more than one earthquake
 * 
 * @author Elizabeth
 *
 */
public abstract class MultiVisualization extends AbstractVisualization {

	private DataSet displayData;
	
	public MultiVisualization(int x, int y, int w, int h) {
		super(x, y, w, h);
	}
}
