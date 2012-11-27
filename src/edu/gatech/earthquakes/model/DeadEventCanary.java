package edu.gatech.earthquakes.model;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

public class DeadEventCanary {

	private static DeadEventCanary instance;
	
	static{
		instance = new DeadEventCanary();
	}
	
	public static DeadEventCanary getInstance(){
		return instance;
	}
	
	@Subscribe
	public void respondToDeadEvent(DeadEvent de){
		System.err.println("Dead Event Dispatched on bus " + de.getSource());
	}
}
