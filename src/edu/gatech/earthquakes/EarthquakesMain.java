package edu.gatech.earthquakes;

import processing.core.PApplet;
import edu.gatech.earthquakes.components.Controller;

public class EarthquakesMain extends PApplet{

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "EarthquakesMain" });
	}
	
	public void setup() {
	    smooth();
	    size(1024, 768);
	    new Controller(this);
	}
	
	public void draw() {
		background(0xFFFFFF);
	}
}
