package edu.gatech.earthquakes.model;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

import edu.gatech.earthquakes.components.Controller;

public class DeadEventCanary {

    private static DeadEventCanary instance;

    static {
	instance = new DeadEventCanary();
    }

    public static DeadEventCanary getInstance() {
	return instance;
    }

    @Subscribe
    public void respondToDeadEvent(final DeadEvent de) {
	String busName = "";
	if (de.getSource().equals(Controller.BRUSH_BUS)) {
	    busName = "brushing";
	} else if (de.getSource().equals(Controller.DRAW_BUS)) {
	    busName = "drawing";
	} else if (de.getSource().equals(Controller.FILTER_BUS)) {
	    busName = "filering";
	} else if (de.getSource().equals(Controller.INTERACT_BUS)) {
	    busName = "interaction";
	}

	System.err.println("Dead Event " + de.getEvent() + " Dispatched on "
	        + busName + " bus.");
    }
}
