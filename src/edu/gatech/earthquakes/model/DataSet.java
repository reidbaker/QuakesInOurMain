package edu.gatech.earthquakes.model;

import java.util.Iterator;
import java.util.Set;

public class DataSet implements Iterable<DataRow>{

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

	@Override
	public Iterator<DataRow> iterator() {
		return datum.iterator();
	}



}
