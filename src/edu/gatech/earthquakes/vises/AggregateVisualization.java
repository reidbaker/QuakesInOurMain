package edu.gatech.earthquakes.vises;

public abstract class AggregateVisualization extends MultiVisualization{

	//the data variable we are organizing the quakes based off of 
	private String aggregator;
	
	public AggregateVisualization(int x, int y, int w, int h) {
		super(x, y, w, h);
	}
}
