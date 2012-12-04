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

public class Workspace extends AbstractVisualization {

    private DataSet masterData;
    List<AbstractVisualization> vises;

    private static final int BAR_WIDTH = 75, CORNER_RADIUS = 25;

    public Workspace(int x, int y, int w, int h, DataSet masterData) {
	super(x, y, w - BAR_WIDTH, h, "Primary Workspace", true);
	this.masterData = masterData;
	vises = Lists.newArrayList();
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
	vises.add(detail);

	Aggregate circles = new NestedCirclePlot(x, y, w, h, masterData,
	        DataRow.TYPE);
	Controller.registerVisualization(circles);
	vises.add(circles);

	Individual aftershock = new AftershockMap(x, y, w, h, mainQuake,
	        masterData);
	Controller.registerVisualization(aftershock);
	vises.add(aftershock);

	Multi bars = new DepthPlot(x, y, w, h, masterData);
	Controller.registerVisualization(bars);
	vises.add(bars);

    }

    @Override
    public void drawComponent(PApplet parent) {

	parent.noStroke();
	parent.fill(Theme.rgba(Theme.getBrightUIColor(), 0x33));
	
	parent.rect(parent.getWidth() - BAR_WIDTH, y + CORNER_RADIUS, CORNER_RADIUS, h-2*CORNER_RADIUS);
	parent.rect(parent.getWidth() - (BAR_WIDTH - CORNER_RADIUS), y, BAR_WIDTH-CORNER_RADIUS, h);

	parent.stroke(Theme.getBaseUIColor());
	parent.strokeWeight(2);
	parent.arc(parent.getWidth() - (BAR_WIDTH - CORNER_RADIUS), y
	        + CORNER_RADIUS, 2 * CORNER_RADIUS, 2 * CORNER_RADIUS,
	        PApplet.PI, PApplet.PI + PApplet.HALF_PI);
	parent.arc(parent.getWidth() - (BAR_WIDTH - CORNER_RADIUS), y+h
	        - CORNER_RADIUS, 2 * CORNER_RADIUS, 2 * CORNER_RADIUS,
	        PApplet.HALF_PI, PApplet.PI);

	parent.noFill();
	parent.line(parent.getWidth(), y, parent.getWidth() - BAR_WIDTH
	        + CORNER_RADIUS, y);

	parent.line(parent.getWidth() - BAR_WIDTH, y + CORNER_RADIUS,
	        parent.getWidth() - BAR_WIDTH, y + h - CORNER_RADIUS);

	parent.line(parent.getWidth(), y + h, parent.getWidth() - BAR_WIDTH
	        + CORNER_RADIUS, y + h);
    }

    @Override
    public void resizeTo(Rectangle bounds) {
	bounds.width -= BAR_WIDTH;
	super.resizeTo(bounds);
	int index = 0;
	for (AbstractVisualization v : vises) {
	    v.resizeTo(new Rectangle(bounds.x
		    + (index * bounds.width / vises.size()), bounds.y,
		    bounds.width / vises.size(), bounds.height));
	    index++;
	}
    }

}
