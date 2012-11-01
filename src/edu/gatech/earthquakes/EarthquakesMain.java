package edu.gatech.earthquakes;

import processing.core.PApplet;
import edu.gatech.earthquakes.components.Controller;
import edu.gatech.earthquakes.components.Theme;

public class EarthquakesMain extends PApplet{

	private static final long serialVersionUID = 1L;
	private Controller cont;

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "EarthquakesMain" });
	}

	public void setup() {
	    smooth();
	    size(1024, 768);
	    cont = new Controller(this);
	}

	public void draw() {
		background(Theme.getBackgroundColor());
		cont.refresh();
	}
}
