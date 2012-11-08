package edu.gatech.earthquakes.vises;

import edu.gatech.earthquakes.model.DataRow;

public abstract class Individual extends AbstractVisualization {
	
	// the current quake that is displayed by this particular individual vis
	@SuppressWarnings("unused")
	private DataRow displayData;
	
	public Individual(int x, int y, int w, int h) {
		super(x, y, w, h);
	}
}
