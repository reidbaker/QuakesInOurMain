package edu.gatech.earthquakes.model;

import java.util.Comparator;
import java.util.Date;

public class DataComparator implements Comparator<DataRow> {

	// TODO: Taylor add Continental Locaiton, Dependency, Type, Depth
	public enum CompareCategories {
		DATE(false), DATE_REVERSE(true), MAGNITUDE(false), MAGNITUDE_REVERSE(
				true);
		
		private boolean reversed;

		private CompareCategories(boolean reversed) {
			this.reversed = reversed;
		}

		private boolean isReversed() {
			return reversed;
		}
	}

	private CompareCategories[] categories;

	public DataComparator(CompareCategories... compareCategories) {
		this.categories = compareCategories;
	}

	@Override
	public int compare(DataRow arg0, DataRow arg1) {
		return compareLeveled(arg0, arg1, 0);
	}

	public int compareLeveled(DataRow arg0, DataRow arg1, int index) {
		if (index >= categories.length)
			return 0;
		switch (categories[index]) {
		// The enum contains a boolean as for when it's reversed. Otherwise,
		// logic is the same.
		case DATE:
		case DATE_REVERSE:
			Date d0 = (Date) arg0.getVariables().get(DataRow.DATE);
			Date d1 = (Date) arg1.getVariables().get(DataRow.DATE);
			if (d0.compareTo(d1) != 0)
				return (!categories[index].isReversed() ? d0.compareTo(d1) : d1
						.compareTo(d0));
			break;
		case MAGNITUDE:
		case MAGNITUDE_REVERSE:
			Double m0 = (double) arg0.getVariables().get(
					DataRow.MOMENT_MAGNITUDE);
			Double m1 = (double) arg1.getVariables().get(
					DataRow.MOMENT_MAGNITUDE);
			if (m0.compareTo(m1) != 0)
				return (!categories[index].isReversed() ? m0.compareTo(m1) : m1
						.compareTo(m0));
			break;
		}
		return compareLeveled(arg0, arg1, index + 1);
	}
}
