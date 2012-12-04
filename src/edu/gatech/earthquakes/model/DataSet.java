package edu.gatech.earthquakes.model;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class DataSet implements Iterable<DataRow> {

    private TreeSet<DataRow> datum;

    public DataSet(Set<DataRow> datum) {
	if (datum instanceof TreeSet<?>) {
	    this.datum = (TreeSet<DataRow>) datum;
	} else {
	    this.datum = new TreeSet<>();
	    this.datum.addAll(datum);
	}
    }

    public DataSet(DataRow row) {
	TreeSet<DataRow> singleRow = new TreeSet<>();
	singleRow.add(row);
	this.datum = singleRow;
    }

    public TreeSet<DataRow> getDatum() {
	return datum;
    }

    public void setDatum(TreeSet<DataRow> datum) {
	this.datum = datum;
    }

    @Override
    public Iterator<DataRow> iterator() {
	return datum.iterator();
    }

}
