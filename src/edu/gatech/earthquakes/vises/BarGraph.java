package edu.gatech.earthquakes.vises;

import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;
import processing.core.PApplet;

public abstract class BarGraph extends Aggregate implements Filterable{
	protected String dataType;
	protected int buffer = 10;
	protected int numDivisions;
	

	public BarGraph(int x, int y, int w, int h, String dataType) {
		super(x, y, w, h);
		
		this.dataType = dataType;
	}

	@Override
	public void filterBy(DataSet filteredData) {
		displayData = filteredData;	
	}

}
