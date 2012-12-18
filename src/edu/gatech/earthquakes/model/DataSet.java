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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((datum == null) ? 0 : datum.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataSet other = (DataSet) obj;
        if (datum == null) {
            if (other.datum != null)
                return false;
        } else if (!datum.equals(other.datum))
            return false;
        return true;
    }

    public void setDatum(TreeSet<DataRow> datum) {
        this.datum = datum;
    }

    @Override
    public Iterator<DataRow> iterator() {
        return datum.iterator();
    }

}
