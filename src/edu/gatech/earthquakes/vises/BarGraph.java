package edu.gatech.earthquakes.vises;

import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.model.DataSet;

public abstract class BarGraph extends Aggregate implements Filterable{
	protected String dataType;
	protected int buffer = 20;
	protected int numDivisions;
	

	public BarGraph(int x, int y, int w, int h, DataSet displayData, String dataType) {
		this(x, y, w, h, displayData, dataType, "Bargraph - FIX ME");
	}
	
	public BarGraph(int x, int y, int w, int h, DataSet displayData, String dataType, String title) {
		super(x, y, w, h, displayData, title);
		
		this.dataType = dataType;
	}

	@Override
	public void filterBy(DataSet filteredData) {
		displayData = filteredData;	
	}

}
