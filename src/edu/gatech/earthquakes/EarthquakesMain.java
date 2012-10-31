package edu.gatech.earthquakes;

import processing.core.PApplet;
import edu.gatech.earthquakes.components.Controller;
import edu.gatech.earthquakes.components.Theme;

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
		background(0xFFFFFFFF);
		fill(Theme.getBaseUIColor());
		rect(0,0,50,50);
		fill(Theme.getDarkUIColor());
		rect(50,0,50,50);
		fill(Theme.getBrightUIColor());
		rect(100,0,50,50);
		
		int[] colors = Theme.getColorPallette(54);
		
		int x = 0, y = 100;
		for(int i = 0; i < colors.length; i++){
			fill(colors[i]);
			rect(x,y,50,50);
			x += 50;
			if(x > getWidth()){
				x = 0;
				y += 50;
			}
		}
	}
}
