package edu.gatech.earthquakes;

import processing.core.PApplet;

public class EarthquakesMain extends PApplet{

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "EarthquakesMain" });
	}
	
	public void setup() {
	    smooth();
	    size(1024, 768);
	}
	
	public void draw() {
		background(0xFFFFFF);
	}
}
