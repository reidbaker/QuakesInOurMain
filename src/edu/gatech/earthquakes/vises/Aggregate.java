package edu.gatech.earthquakes.vises;

public abstract class Aggregate extends Multi{

	//the data variable we are organizing the quakes based off of 
	private String aggregator;
	
	public Aggregate(int x, int y, int w, int h) {
		super(x, y, w, h);
	}
}
