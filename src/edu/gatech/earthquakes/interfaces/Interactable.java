package edu.gatech.earthquakes.interfaces;

import com.google.common.eventbus.Subscribe;

import edu.gatech.earthquakes.model.Interaction;

public interface Interactable {

	@Subscribe
	public void handleInput(Interaction interaction);
	
}
