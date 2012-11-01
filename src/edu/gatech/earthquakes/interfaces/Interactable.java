package edu.gatech.earthquakes.interfaces;

import processing.core.PApplet;

public interface Interactable {

	public void handleInput(boolean pressed, boolean dragged, boolean released, PApplet parent);
	
}
