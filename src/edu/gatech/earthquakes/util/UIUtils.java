package edu.gatech.earthquakes.util;

import java.lang.reflect.Field;

import processing.core.PApplet;
import processing.core.PGraphics;

public final class UIUtils {

    private UIUtils() {
	// do nothing
    }

    public static void roundRect(final int x, final int y, final int w,
	    final int h, final int radius, final PApplet parent) {
	int modifiedRadius = radius;
	final PApplet p = parent;
	if (p == null) {
	    return;
	}

	final int stroke = UIUtils.getStrokeColor(p);

	// Sanitize data
	if (radius > w / 2) {
	    modifiedRadius = w / 2;
	}
	if (radius > h / 2) {
	    modifiedRadius = h / 2;
	}

	// Draw all rectangles first
	parent.noStroke();
	// Center
	p.rect(x + modifiedRadius, y + modifiedRadius, w - 2 * modifiedRadius,
	        h - 2 * modifiedRadius);
	// Top
	p.rect(x + modifiedRadius, y, w - 2 * modifiedRadius, modifiedRadius);
	// Bottom
	p.rect(x + modifiedRadius, y + h - modifiedRadius, w - 2
	        * modifiedRadius, modifiedRadius);
	// Left
	p.rect(x, y + modifiedRadius, modifiedRadius, h - 2 * modifiedRadius);
	// Right
	p.rect(x + w - modifiedRadius, y + modifiedRadius, modifiedRadius, h
	        - 2 * modifiedRadius);

	// Draw all Corners
	parent.stroke(stroke);
	// Top Left
	p.arc(x + modifiedRadius, y + modifiedRadius, 2 * modifiedRadius,
	        2 * modifiedRadius, PApplet.PI, PApplet.PI + PApplet.HALF_PI);
	// Top Right
	p.arc(x + w - modifiedRadius, y + modifiedRadius, 2 * modifiedRadius,
	        2 * modifiedRadius, PApplet.PI + PApplet.HALF_PI,
	        2 * PApplet.PI);
	// Bottom Left
	p.arc(x + modifiedRadius, y + h - modifiedRadius, 2 * modifiedRadius,
	        2 * modifiedRadius, PApplet.HALF_PI, PApplet.PI);
	// Bottom Right
	p.arc(x + w - modifiedRadius, y + h - modifiedRadius,
	        2 * modifiedRadius, 2 * modifiedRadius, 0, PApplet.HALF_PI);

	// Draw all Lines
	// Top
	p.line(x + modifiedRadius, y, x + w - modifiedRadius, y);
	// Bottom
	p.line(x + modifiedRadius, y + h, x + w - modifiedRadius, y + h);
	// Left
	p.line(x, y + modifiedRadius, x, y + h - modifiedRadius);
	// Right
	p.line(x + w, y + modifiedRadius, x + w, y + h - modifiedRadius);
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
