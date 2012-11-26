package edu.gatech.earthquakes.interfaces;

import com.google.common.eventbus.Subscribe;

import edu.gatech.earthquakes.model.DataSet;


public interface Brushable {

	@Subscribe
	public void brushData(DataSet ds);
	
}
