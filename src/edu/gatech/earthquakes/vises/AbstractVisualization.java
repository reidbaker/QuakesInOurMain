package edu.gatech.earthquakes.vises;

import processing.core.PApplet;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.interfaces.Drawable;

public abstract class AbstractVisualization implements Drawable{

	protected int x, y, w, h;
	
	public AbstractVisualization(int x, int y, int w, int h){
		this.x = x; this.y = y; this.w = w; this.h = h;
	}
	
	public void drawComponent(PApplet parent){
		parent.noFill();
		parent.stroke(Theme.getBaseUIColor());
		parent.strokeCap(PApplet.ROUND);
		parent.rect(x, y, w, h);
		//parent.
	}
}
