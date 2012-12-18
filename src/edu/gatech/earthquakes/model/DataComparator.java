package edu.gatech.earthquakes.model;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class DataComparator implements Comparator<DataRow> {

    private static final DataComparator baseCompare = new DataComparator(
            DataComparator.CompareCategories.DATE,
            DataComparator.CompareCategories.MAGNITUDE,
            DataComparator.CompareCategories.DEPTH);

    public static final HashMap<String, CompareCategories> categoryMap;
    static {
        categoryMap = new HashMap<>();
        categoryMap.put(DataRow.CONTINENT, CompareCategories.CONTINENT);
        categoryMap.put(DataRow.DEPENDENCY, CompareCategories.DEPENDENCY);
        categoryMap.put(DataRow.TYPE, CompareCategories.TYPE);
    }

    public enum CompareCategories {
        DATE(false), DATE_REVERSE(true), MAGNITUDE(false), MAGNITUDE_REVERSE(
                true), CONTINENT(false), DEPENDENCY(false), TYPE(false), DEPTH(
                false), DEPTH_REVERSED(true);

        private boolean reversed;

        private CompareCategories(boolean reversed) {
            this.reversed = reversed;
        }

        private boolean isReversed() {
            return reversed;
        }
    }

    public static DataComparator getDefaultComparator() {
        return baseCompare;
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
                    return (!categories[index].isReversed() ? d0.compareTo(d1)
                            : d1.compareTo(d0));
                break;
            case MAGNITUDE:
            case MAGNITUDE_REVERSE:
                Double m0 = (double) arg0.getVariables().get(
                        DataRow.MOMENT_MAGNITUDE);
                Double m1 = (double) arg1.getVariables().get(
                        DataRow.MOMENT_MAGNITUDE);
                if (m0.compareTo(m1) != 0)
                    return (!categories[index].isReversed() ? m0.compareTo(m1)
                            : m1.compareTo(m0));
                break;
            case CONTINENT:
                DataRow.Continent c0 = (DataRow.Continent) arg0.getVariables()
                        .get(DataRow.CONTINENT);
                DataRow.Continent c1 = (DataRow.Continent) arg1.getVariables()
                        .get(DataRow.CONTINENT);

                if (c0.compareTo(c1) != 0)
                    return c0.compareTo(c1);
                break;
            case DEPENDENCY:
                DataRow.Dependency dependency0 = (DataRow.Dependency) arg0
                        .getVariables().get(DataRow.DEPENDENCY);
                DataRow.Dependency dependency1 = (DataRow.Dependency) arg1
                        .getVariables().get(DataRow.DEPENDENCY);
                if (dependency0.compareTo(dependency1) != 0)
                    return dependency0.compareTo(dependency1);
                break;
            case DEPTH:
            case DEPTH_REVERSED:
                Object dObj0 = arg0.getVariables().get(DataRow.DEPTH);
                Object dObj1 = arg1.getVariables().get(DataRow.DEPTH);
                if (dObj0 == null) {
                    if (dObj1 == null) {
                        break;
                    } else {
                        return -1;
                    }
                } else if (dObj1 == null) {
                    return 1;
                }
                Double depth0 = (double) dObj0;
                Double depth1 = (double) dObj1;
                if (depth0.compareTo(depth1) != 0)
                    return (!categories[index].isReversed() ? depth0
                            .compareTo(depth1) : depth1.compareTo(depth0));
                break;
            case TYPE:
                DataRow.Type t0 = (DataRow.Type) arg0.getVariables().get(
                        DataRow.TYPE);
                DataRow.Type t1 = (DataRow.Type) arg1.getVariables().get(
                        DataRow.TYPE);
                if (t0.compareTo(t1) != 0)
                    return t0.compareTo(t1);
                break;
        }
        return compareLeveled(arg0, arg1, index + 1);
    }
}
