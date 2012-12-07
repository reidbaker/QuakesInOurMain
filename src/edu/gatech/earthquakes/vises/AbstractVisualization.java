package edu.gatech.earthquakes.vises;

import java.awt.Rectangle;

import processing.core.PApplet;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.interfaces.Drawable;
import edu.gatech.earthquakes.interfaces.Resizable;

public abstract class AbstractVisualization implements Drawable, Resizable {

    private Rectangle fb; // FrameBounds
    protected int x, y, w, h;
    protected int buffer = 20;
    private String title;
    private boolean undecorated;

    private final int FRAME_TOP = 30, FRAME_BOTTOM = 2, FRAME_LEFT = 2,
	    FRAME_RIGHT = 2, CORNER_RADIUS = 25, BASE_INSET = 2;

    public AbstractVisualization(int x, int y, int w, int h) {
	this(x, y, w, h, "Abstract Vis - FIX ME");
    }

    public AbstractVisualization(int x, int y, int w, int h, String title) {
	this(x, y, w, h, title, false);
    }

    public AbstractVisualization(int x, int y, int w, int h, String title,
	    boolean undecorated) {
	fb = new Rectangle(x, y, w, h);
	this.title = title;
	this.undecorated = undecorated;
	recalculateInsets();
    }

    public void drawComponent(PApplet parent) {
	if (!undecorated) {
	    parent.noStroke();
	    parent.fill(Theme.rgba(Theme.getBrightUIColor(), 0x66));
	    parent.rect(fb.x + BASE_INSET + CORNER_RADIUS, fb.y + BASE_INSET,
		    fb.width - 2 * BASE_INSET - CORNER_RADIUS, CORNER_RADIUS);
	    parent.rect(fb.x + BASE_INSET, fb.y + BASE_INSET + CORNER_RADIUS,
		    fb.width - 2 * BASE_INSET, FRAME_TOP - CORNER_RADIUS);

	    parent.stroke(Theme.getBaseUIColor());
	    parent.strokeWeight(2);
	    parent.arc(fb.x + BASE_INSET + CORNER_RADIUS, fb.y + BASE_INSET
		    + CORNER_RADIUS, 2 * CORNER_RADIUS, 2 * CORNER_RADIUS,
		    PApplet.PI, PApplet.PI + PApplet.HALF_PI);

	    parent.noFill();
	    parent.strokeCap(PApplet.ROUND);
	    parent.strokeJoin(PApplet.ROUND);
	    // border around left, bottom, right;
	    parent.beginShape();
	    parent.vertex(fb.x + BASE_INSET, fb.y + BASE_INSET + CORNER_RADIUS);
	    parent.vertex(fb.x + BASE_INSET, fb.y + fb.height - BASE_INSET);
	    parent.vertex(fb.x + fb.width - BASE_INSET, fb.y + fb.height
		    - BASE_INSET);
	    parent.vertex(fb.x + fb.width - BASE_INSET, fb.y + BASE_INSET);
	    parent.vertex(fb.x + BASE_INSET + CORNER_RADIUS, fb.y + BASE_INSET);
	    parent.endShape();
	    parent.line(fb.x + BASE_INSET, fb.y + FRAME_TOP, fb.x + fb.width
		    - 2 * BASE_INSET, fb.y + FRAME_TOP);

	    parent.fill(Theme.getDarkUIColor());
	    parent.textSize(FRAME_TOP - 8);
	    parent.textAlign(PApplet.LEFT);
	    parent.text(title, fb.x + BASE_INSET + CORNER_RADIUS, fb.y,
		    fb.width - 2 * BASE_INSET - CORNER_RADIUS, FRAME_TOP);
	    ;
	}
    }

    @Override
    public void resizeTo(Rectangle bounds) {
	fb = (Rectangle) bounds.clone();
	recalculateInsets();
    }

    public void setUndecorated(boolean undecorated) {
	this.undecorated = undecorated;
    }

    private void recalculateInsets() {
	if (undecorated) {
	    this.x = fb.x;
	    this.y = fb.y;
	    this.w = fb.width;
	    this.h = fb.height;
	} else {
	    this.x = fb.x + FRAME_LEFT;
	    this.y = fb.y + FRAME_TOP;
	    this.w = fb.width - (FRAME_LEFT + FRAME_RIGHT);
	    this.h = fb.height - (FRAME_TOP + FRAME_BOTTOM);
	}
    }

    public String getTitle() {
	return title;
    }
}
