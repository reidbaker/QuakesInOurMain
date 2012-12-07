package edu.gatech.earthquakes.vises;

import java.awt.Rectangle;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import processing.core.PApplet;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import edu.gatech.earthquakes.components.Controller;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.interfaces.Drawable;
import edu.gatech.earthquakes.interfaces.Interactable;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;
import edu.gatech.earthquakes.model.Interaction;
import edu.gatech.earthquakes.util.UIUtils;

public class Workspace extends AbstractVisualization implements Interactable {

    private DataSet masterData;
    List<AbstractVisualization> allVises;
    List<AbstractVisualization> openVises;

    private int numHighlighted;

    private static final int BAR_WIDTH = 75, CORNER_RADIUS = 10,
	    BUTTON_PADDING = 10;

    public Workspace(int x, int y, int w, int h, DataSet masterData) {
	super(x, y, w - BAR_WIDTH, h, "Primary Workspace", true);
	this.masterData = masterData;
	allVises = Lists.newArrayList();
	openVises = Lists.newArrayList();
	numHighlighted = -1;
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

	Multi nomiBars = new NominalBarGraph(x, y, w, h, masterData,
	        DataRow.TYPE);
	Controller.registerVisualization(nomiBars);
	allVises.add(nomiBars);

	openVises.addAll(allVises);

    }

    @Override
    public void drawComponent(PApplet parent) {

	// Draw Sidebar Rectangles
	parent.noStroke();
	parent.fill(Theme.rgba(Theme.getBrightUIColor(), 0x33));

	parent.rect(x + w - BAR_WIDTH, y + CORNER_RADIUS, CORNER_RADIUS, h - 2
	        * CORNER_RADIUS);
	parent.rect(x + w - (BAR_WIDTH - CORNER_RADIUS), y, BAR_WIDTH
	        - CORNER_RADIUS, h);

	// Draw Sidebar Corners
	parent.stroke(Theme.getBaseUIColor());
	parent.strokeWeight(2);
	parent.arc(x + w - (BAR_WIDTH - CORNER_RADIUS), y + CORNER_RADIUS,
	        2 * CORNER_RADIUS, 2 * CORNER_RADIUS, PApplet.PI, PApplet.PI
	                + PApplet.HALF_PI);
	parent.arc(x + w - (BAR_WIDTH - CORNER_RADIUS), y + h - CORNER_RADIUS,
	        2 * CORNER_RADIUS, 2 * CORNER_RADIUS, PApplet.HALF_PI,
	        PApplet.PI);

	// Draw Sidebar Lines
	parent.noFill();
	parent.line(x + w, y, x + w - BAR_WIDTH + CORNER_RADIUS, y);

	parent.line(x + w - BAR_WIDTH, y + CORNER_RADIUS, x + w - BAR_WIDTH, y
	        + h - CORNER_RADIUS);

	parent.line(x + w, y + h, x + w - BAR_WIDTH + CORNER_RADIUS, y + h);

	// Draw Icons for each vis
	parent.stroke(Theme.getBaseUIColor());
	int buttonWidth = BAR_WIDTH - BUTTON_PADDING * 2;
	int buttonHeight = h / allVises.size() - 2 * BUTTON_PADDING;
	int i = 0;
	for (AbstractVisualization av : allVises) {
	    if (openVises.contains(av)) {
		if (numHighlighted == i) {
		    parent.fill(Theme.changeBrightness(
			    Theme.rgba(Theme.getBrightUIColor(), 0xaa), 1.1f,
			    true));
		} else {
		    parent.fill(Theme.rgba(Theme.getBrightUIColor(), 0xaa));
		}
	    } else {
		if (numHighlighted == i) {
		    parent.fill(Theme.changeBrightness(Theme.changeSaturation(
			    Theme.getBrightUIColor(), 0.0f, true), 0.75f, true));
		} else {
		    parent.fill(Theme.changeSaturation(
			    Theme.getBrightUIColor(), 0.0f, true));
		}
	    }
	    int bx = x + w - BAR_WIDTH + BUTTON_PADDING;
	    int by = y + BUTTON_PADDING
		    + (i * (BUTTON_PADDING * 2 + buttonHeight));

	    UIUtils.roundRect(bx, by, buttonWidth, buttonHeight, CORNER_RADIUS,
		    parent);
	    parent.fill(Theme.getDarkUIColor());
	    parent.textAlign(PApplet.CENTER, PApplet.CENTER);
	    int size = Math.min(buttonWidth, buttonHeight);
	    parent.textSize(size - 2);
	    parent.text("" + (i + 1), bx + buttonWidth / 2, by + buttonHeight
		    / 2);
	    i++;
	}
    }

    @Override
    public void resizeTo(Rectangle bounds) {
	super.resizeTo(bounds);
	int index = 0;

	// Determine vis aspect ratio
	int width = (bounds.width - BAR_WIDTH - 10) / openVises.size();
	int height = bounds.height;
	int numRows = 1;
	int maxPerRow = openVises.size();

	while (height / (float) width > 2.0f) {
	    numRows++;
	    maxPerRow = openVises.size() / numRows;
	    if (openVises.size() % numRows != 0) {
		maxPerRow++;
	    }
	    width = (bounds.width - BAR_WIDTH - 10) / maxPerRow;
	    height = bounds.height/numRows;
	}

	int indexInRow = 0;
	int rowCount = 0;
	
//	while(index < openVises.size()){
//	    
//	    if(indexInRow == maxPerRow){
//		rowCount++;
//		indexInRow = 0;
//	    }
//	}

	for (AbstractVisualization v : openVises) {
	    v.resizeTo(new Rectangle(bounds.x
		    + (index * (bounds.width - BAR_WIDTH - 10) / openVises
		            .size()), bounds.y, (bounds.width - BAR_WIDTH - 10)
		    / openVises.size(), bounds.height));
	    index++;
	}
    }

    @Subscribe
    public void handleInput(Interaction interaction) {
	PApplet pa = interaction.getParentApplet();

	// Handle highlighting detection
	boolean isOverOne = false;
	for (int i = 0; i < allVises.size(); i++) {
	    if (isOverVisButton(pa.mouseX, pa.mouseY, i)) {
		if (interaction.isFirstPress()) {
		    toggleVis(i);
		}
		numHighlighted = i;
		isOverOne = true;
	    }
	}
	if (!isOverOne) {
	    numHighlighted = -1;
	}
    }

    private boolean isOverVisButton(int px, int py, int visCount) {
	int bw = BAR_WIDTH - BUTTON_PADDING * 2;
	int bh = h / allVises.size() - 2 * BUTTON_PADDING;
	int bx = x + w - BAR_WIDTH + BUTTON_PADDING;
	int by = y + BUTTON_PADDING + (visCount * (BUTTON_PADDING * 2 + bh));

	if (px > bx && px < (bx + bw) && py > by && py < (by + bh)) {
	    return true;
	} else {
	    return false;
	}
    }

    private void toggleVis(int i) {
	AbstractVisualization vis = allVises.get(i);
	if (openVises.contains(vis)) {
	    openVises.remove(vis);
	    if (vis instanceof Drawable)
		Controller.DRAW_BUS.unregister(vis);
	    if (vis instanceof Interactable)
		Controller.INTERACT_BUS.unregister(vis);
	} else {
	    openVises.add(Math.min(i, openVises.size()), vis);
	    if (vis instanceof Drawable)
		Controller.DRAW_BUS.register(vis);
	    if (vis instanceof Interactable)
		Controller.INTERACT_BUS.register(vis);
	}
	int index = 0;
	for (AbstractVisualization v : openVises) {
	    v.resizeTo(new Rectangle(x
		    + (index * (w - BAR_WIDTH - 10) / openVises.size()), y, (w
		    - BAR_WIDTH - 10)
		    / openVises.size(), h));
	    index++;
	}
    }

}
