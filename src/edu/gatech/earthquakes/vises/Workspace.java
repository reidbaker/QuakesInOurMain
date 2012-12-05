package edu.gatech.earthquakes.vises;

import java.awt.Rectangle;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import processing.core.PApplet;

import com.google.common.collect.Lists;

import edu.gatech.earthquakes.components.Controller;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;
import edu.gatech.earthquakes.util.UIUtils;

public class Workspace extends AbstractVisualization {

    private DataSet masterData;
    List<AbstractVisualization> allVises;
    List<AbstractVisualization> openVises;

    private static final int BAR_WIDTH = 75, CORNER_RADIUS = 10,
	    BUTTON_PADDING = 10;

    public Workspace(int x, int y, int w, int h, DataSet masterData) {
	super(x, y, w - BAR_WIDTH, h, "Primary Workspace", true);
	this.masterData = masterData;
	allVises = Lists.newArrayList();
	openVises = Lists.newArrayList();
	intantiateVises();
    }

    public void intantiateVises() {
	DataRow mainQuake = null;
	for (DataRow quake : masterData.getDatum())
	    try {
		if (quake.getValue(DataRow.DATE).equals(
		        new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
		                .parse("20010126"))
		        && quake.getValue(DataRow.DEPENDENCY).equals(
		                DataRow.Dependency.INDEPENDENT)) {
		    mainQuake = quake;
		}
	    } catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	Individual detail = new DetailedInfo(x, y, w, h, mainQuake);
	Controller.registerVisualization(detail);
	allVises.add(detail);

	Aggregate circles = new NestedCirclePlot(x, y, w, h, masterData,
	        DataRow.TYPE, masterData.getDatum().size());
	Controller.registerVisualization(circles);
	allVises.add(circles);

	Individual aftershock = new AftershockMap(x, y, w, h, mainQuake,
	        masterData);
	Controller.registerVisualization(aftershock);
	allVises.add(aftershock);

	Multi bars = new DepthPlot(x, y, w, h, masterData);
	Controller.registerVisualization(bars);
	allVises.add(bars);

    }

    @Override
    public void drawComponent(PApplet parent) {

	// Draw Sidebar Rectangles
	parent.noStroke();
	parent.fill(Theme.rgba(Theme.getBrightUIColor(), 0x33));

	parent.rect(parent.getWidth() - BAR_WIDTH, y + CORNER_RADIUS,
	        CORNER_RADIUS, h - 2 * CORNER_RADIUS);
	parent.rect(parent.getWidth() - (BAR_WIDTH - CORNER_RADIUS), y,
	        BAR_WIDTH - CORNER_RADIUS, h);

	// Draw Sidebar Corners
	parent.stroke(Theme.getBaseUIColor());
	parent.strokeWeight(2);
	parent.arc(parent.getWidth() - (BAR_WIDTH - CORNER_RADIUS), y
	        + CORNER_RADIUS, 2 * CORNER_RADIUS, 2 * CORNER_RADIUS,
	        PApplet.PI, PApplet.PI + PApplet.HALF_PI);
	parent.arc(parent.getWidth() - (BAR_WIDTH - CORNER_RADIUS), y + h
	        - CORNER_RADIUS, 2 * CORNER_RADIUS, 2 * CORNER_RADIUS,
	        PApplet.HALF_PI, PApplet.PI);

	// Draw Sidebar Lines
	parent.noFill();
	parent.line(parent.getWidth(), y, parent.getWidth() - BAR_WIDTH
	        + CORNER_RADIUS, y);

	parent.line(parent.getWidth() - BAR_WIDTH, y + CORNER_RADIUS,
	        parent.getWidth() - BAR_WIDTH, y + h - CORNER_RADIUS);

	parent.line(parent.getWidth(), y + h, parent.getWidth() - BAR_WIDTH
	        + CORNER_RADIUS, y + h);

	// Draw Icons for each vis
	parent.stroke(Theme.getBaseUIColor());
	int buttonWidth = BAR_WIDTH - BUTTON_PADDING * 2;
	int buttonHeight = h / allVises.size() - 2 * BUTTON_PADDING;
	int i = 0;
	for (AbstractVisualization av : allVises) {
	    if (openVises.contains(av)) {
		parent.fill(Theme.rgba(Theme.getBrightUIColor(), 0x33));
	    } else {
		parent.fill(Theme.changeSaturation(Theme.getBrightUIColor(),
		        0.2f, true));
	    }
	    int bx = parent.getWidth() - BAR_WIDTH + BUTTON_PADDING;
	    int by = 
		    y + BUTTON_PADDING
	            + (i * (BUTTON_PADDING * 2 + buttonHeight));
	    
	    UIUtils.roundRect(bx,by,
		    buttonWidth, buttonHeight, CORNER_RADIUS, parent);
	    parent.fill(Theme.getDarkUIColor());
	    parent.textAlign(PApplet.CENTER, PApplet.CENTER);
	    int size = Math.min(buttonWidth, buttonHeight);
	    parent.textSize(size - 2);
	    parent.text("" + (i+1),bx + buttonWidth/2, by + buttonHeight/2);
	    i++;
	}
    }

    @Override
    public void resizeTo(Rectangle bounds) {
	bounds.width -= BAR_WIDTH;
	super.resizeTo(bounds);
	int index = 0;
	for (AbstractVisualization v : allVises) {
	    v.resizeTo(new Rectangle(bounds.x
		    + (index * bounds.width / allVises.size()), bounds.y,
		    bounds.width / allVises.size(), bounds.height));
	    index++;
	}
    }

}
