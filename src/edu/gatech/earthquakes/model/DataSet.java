package edu.gatech.earthquakes.model;

import java.util.Collection;

public class DataSet {

	private Collection<DataRow> datum;
	
	public Collection<DataRow> getDatum() {
		return datum;
	}

	public void setDatum(Collection<DataRow> datum) {
		this.datum = datum;
	}

	public DataSet(Collection<DataRow> datum){
		this.datum = datum;
	}
	
}
