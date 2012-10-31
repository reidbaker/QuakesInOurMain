package edu.gatech.earthquakes.interfaces;

import edu.gatech.earthquakes.model.DataSet;

public interface Filterable {

	public void filterBy(DataSet filteredData);
	
}
