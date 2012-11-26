package edu.gatech.earthquakes.interfaces;

import com.google.common.eventbus.Subscribe;

import edu.gatech.earthquakes.model.DataSet;

public interface Filterable {

	@Subscribe
	public void filterBy(DataSet filteredData);
	
}
