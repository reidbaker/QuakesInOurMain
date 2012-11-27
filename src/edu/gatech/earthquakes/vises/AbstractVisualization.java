package edu.gatech.earthquakes.vises;

import java.awt.Rectangle;

import processing.core.PApplet;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.interfaces.Drawable;
import edu.gatech.earthquakes.interfaces.Resizable;

public abstract class AbstractVisualization implements Drawable, Resizable{

	protected int x, y, w, h;
	
	public AbstractVisualization(int x, int y, int w, int h){
		this.x = x; this.y = y; this.w = w; this.h = h;
	}
	
	public void drawComponent(PApplet parent){
		parent.noFill();
		parent.stroke(Theme.getBaseUIColor());
		parent.strokeCap(PApplet.ROUND);
		parent.rect(x, y, w, h);
	}
	
	@Override
	public void resizeTo(Rectangle bounds) {
		this.x = bounds.x;
		this.y = bounds.y;
		this.w = bounds.width;
		this.h = bounds.height;
	}
}
