package edu.gatech.earthquakes.vises;

import edu.gatech.earthquakes.model.DataSet;

// name subject to change
/*
 * The idea is that this is a set of circles that can show two different types of groupings.
 * Right now, its being done for location and then by quake type
 */
public class NestedCirclePlot extends Aggregate {
	
	//the thing that will be grouped by after location
	private String dataType;
	//private HashSet<String, >
	public NestedCirclePlot(int x, int y, int w, int h, DataSet displayData, String dataType) {
		super(x, y, w, h, displayData);
		this.dataType = dataType;
		// TODO Auto-generated constructor stub
	}
	
	

}
