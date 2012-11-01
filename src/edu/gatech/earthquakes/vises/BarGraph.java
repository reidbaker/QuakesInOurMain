package edu.gatech.earthquakes.vises;

import processing.core.PApplet;

public class BarGraph extends Aggregate {
	private String xDataName;
	private String yDataName;
	
	
	public BarGraph(int x, int y, int w, int h, String xDataName, String yDataName) {
		super(x, y, w, h);
		
		this.xDataName = xDataName;
		this.yDataName = yDataName;
	}

	@Override
	public void drawComponent(PApplet parent) {
		// TODO Auto-generated method stub

	}

}
