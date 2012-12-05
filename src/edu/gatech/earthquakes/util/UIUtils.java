package edu.gatech.earthquakes.util;

import java.lang.reflect.Field;

import processing.core.PApplet;
import processing.core.PGraphics;

public class UIUtils {

    public static void roundRect(int x, int y, int w, int h, int r,
	    PApplet parent) {
	
	PApplet p = parent;
	if(p == null){
	    return;
	}
	
	int stroke = UIUtils.getStrokeColor(p);

	// Sanitize data
	if (r > w / 2)
	    r = w / 2;
	if (r > h / 2)
	    r = h / 2;

	// Draw all rectangles first
	parent.noStroke();
	// Center
	p.rect(x + r, y + r, w - 2 * r, h - 2 * r);
	// Top
	p.rect(x + r, y, w - 2 * r, r);
	// Bottom
	p.rect(x + r, y + h - r, w - 2 * r, r);
	// Left
	p.rect(x, y + r, r, h - 2 * r);
	// Right
	p.rect(x + w - r, y + r, r, h - 2 * r);

	// Draw all Corners
	parent.stroke(stroke);
	//Top Left
	p.arc(x+r, y+r, 2*r, 2*r, PApplet.PI, PApplet.PI + PApplet.HALF_PI);
	//Top Right
	p.arc(x+w-r, y+r, 2*r, 2*r, PApplet.PI + PApplet.HALF_PI, 2*PApplet.PI);
	//Bottom Left
	p.arc(x+r, y+h-r, 2*r, 2*r, PApplet.HALF_PI, PApplet.PI);
	//Bottom Right
	p.arc(x+w-r, y+h-r, 2*r, 2*r, 0, PApplet.HALF_PI);
	
	//Draw all Lines
	//Top
	p.line(x+r, y, x+w-r, y);
	//Bottom
	p.line(x+r, y+h, x+w-r, y+h);
	//Left
	p.line(x, y+r, x, y+h-r);
	//Right
	p.line(x+w, y+r, x+w, y+h-r);
    }

    public static int getFillColor(PApplet papplet) {
	try {
	    Field graphicsField = PApplet.class.getDeclaredField("g");
	    PGraphics graphics = (PGraphics) graphicsField.get(papplet);

	    return graphics.fillColor;
	} catch (NoSuchFieldException nsfe) {
	    nsfe.printStackTrace();
	    return 0;
	} catch (IllegalAccessException iae) {
	    iae.printStackTrace();
	    return 0;
	}
    }

    public static int getStrokeColor(PApplet papplet) {
	try {
	    Field graphicsField = PApplet.class.getDeclaredField("g");
	    PGraphics graphics = (PGraphics) graphicsField.get(papplet);

	    return graphics.strokeColor;
	} catch (NoSuchFieldException nsfe) {
	    nsfe.printStackTrace();
	    return 0;
	} catch (IllegalAccessException iae) {
	    iae.printStackTrace();
	    return 0;
	}
    }

}
