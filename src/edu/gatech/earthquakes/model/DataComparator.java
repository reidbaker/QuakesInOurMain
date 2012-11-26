package edu.gatech.earthquakes.model;

import java.util.Comparator;
import java.util.Date;


public class DataComparator implements Comparator<DataRow>{
	
	public enum CompareCategories {
		DATE,
		MAGNITUDE,
	}
	
	private boolean reversed;
	private CompareCategories type;
	
	
	public DataComparator(CompareCategories compareType, boolean reversed){
		this.type = compareType;
		this.reversed = reversed;
	}


	@Override
	public int compare(DataRow arg0, DataRow arg1) {
		switch(type){
		case DATE:
			Date d0 = (Date)arg0.getVariables().get(DataRow.DATE);
			Date d1 = (Date)arg1.getVariables().get(DataRow.DATE);
			return (!reversed?d0.compareTo(d1):d1.compareTo(d0));
		case MAGNITUDE:
			Double m0 = (double)arg0.getVariables().get(DataRow.MOMENT_MAGNITUDE);
			Double m1 = (double)arg1.getVariables().get(DataRow.MOMENT_MAGNITUDE);
			return (!reversed?m0.compareTo(m1):m1.compareTo(m0));
		default:
			return 0;
		}
	}
}
