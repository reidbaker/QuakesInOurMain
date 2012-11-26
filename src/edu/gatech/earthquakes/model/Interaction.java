package edu.gatech.earthquakes.model;

import processing.core.PApplet;

public class Interaction {

	private boolean firstPress, dragged, released;
	private PApplet parentApplet;
	
	public Interaction(boolean firstPress, boolean dragged, boolean released,
			PApplet parentApplet) {
		super();
		this.firstPress = firstPress;
		this.dragged = dragged;
		this.released = released;
		this.parentApplet = parentApplet;
	}

	public boolean isFirstPress() {
		return firstPress;
	}

	public boolean isDragged() {
		return dragged;
	}

	public boolean isReleased() {
		return released;
	}

	public PApplet getParentApplet() {
		return parentApplet;
	}
	
	
	
}
