package edu.gatech.earthquakes.components;

import java.util.List;

import processing.core.PApplet;

import com.google.common.collect.Lists;

import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.vises.AbstractVisualization;
import edu.gatech.earthquakes.vises.NominalBarGraph;

public class Workspace extends AbstractVisualization {

	List<AbstractVisualization> vises;

	public Workspace(int x, int y, int w, int h) {
		super(x, y, w, h);
		vises = Lists.newArrayList();
	}

	@Override
	public void drawComponent(PApplet parent) {
		parent.noFill();
		parent.stroke(Theme.getBaseUIColor());
		parent.strokeWeight(2);
		parent.rect(x, y, w, h);
	}

}
