package edu.gatech.earthquakes.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DataSet implements Iterable<DataRow>{

	private Set<DataRow> datum;

	public DataSet(Set<DataRow> datum){
        this.datum = datum;
    }
	
	public DataSet(DataRow row){
		Set<DataRow> singleRow = new HashSet<>();
		singleRow.add(row);
		this.datum = singleRow;
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
