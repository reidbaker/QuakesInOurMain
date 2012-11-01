package edu.gatech.earthquakes.model;

import java.util.Set;

public class DataSet {

	private Set<DataRow> datum;

	public DataSet(Set<DataRow> datum){
        this.datum = datum;
    }

	public Set<DataRow> getDatum() {
		return datum;
	}

	public void setDatum(Set<DataRow> datum) {
		this.datum = datum;
	}



}
