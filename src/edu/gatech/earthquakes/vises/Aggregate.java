package edu.gatech.earthquakes.vises;

import edu.gatech.earthquakes.model.DataSet;

public abstract class Aggregate extends Multi{

	//the data variable we are organizing the quakes based off of
	@SuppressWarnings("unused")
	private String aggregator;

	public Aggregate(int x, int y, int w, int h, DataSet displayData) {
		this(x, y, w, h, displayData, "Aggregate - FIX ME");
	}

	public Aggregate(int x, int y, int w, int h, DataSet displayData, String title) {
		super(x, y, w, h, displayData, title);
	}
}
