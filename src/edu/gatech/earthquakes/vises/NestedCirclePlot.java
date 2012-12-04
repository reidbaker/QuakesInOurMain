package edu.gatech.earthquakes.vises;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import processing.core.PApplet;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.model.DataComparator;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;

public class NestedCirclePlot extends Aggregate implements Filterable {
    // the thing that will be grouped by after location
    private String dataType;
    private DataComparator dataComp;
    private Map<String, Set<TypeCount>> computedValues;
    private double maxVal = 0;
    private int offset = 15;
    private TreeSet<TypeCount> totals;
    private int numTotalQuakes;

    public NestedCirclePlot(int x, int y, int w, int h, DataSet displayData,
            String dataType, int numTotalQuakes) {
        super(x, y, w, h, displayData);
        this.dataType = dataType;
        this.numTotalQuakes = numTotalQuakes;

        DataComparator.CompareCategories category = null;
        switch (dataType) {
            case DataRow.DEPENDENCY:
                category = DataComparator.CompareCategories.DEPENDENCY;
                break;
            case DataRow.TYPE:
                category = DataComparator.CompareCategories.TYPE;
                break;
        }

        dataComp = new DataComparator(
                DataComparator.CompareCategories.CONTINENT, category);
        
        computeNominalData();

    }

    public void drawComponent(PApplet parent) {
        super.drawComponent(parent);

        float drawY = y + h - buffer;
        float maxCircleRadius = getCircleRadius(maxVal);

        boolean right = false;
        parent.noStroke();

        parent.textSize(offset * 3 / 4);
        parent.textAlign(PApplet.CENTER);

        for (String country : computedValues.keySet()) {

            //draw the colored square for the country and write the country name
            parent.fill(DataRow.getColorFor(country));
            if (right) {
                parent.rect(x + w / 2 + offset / 2,
                        drawY - maxCircleRadius * 2, maxCircleRadius * 2,
                        maxCircleRadius * 2);
                parent.fill(0);
                parent.text(country, x + w / 2 + offset / 2 + maxCircleRadius,
                        drawY + offset * 3 / 4);
            } else {
                parent.rect(x + w / 2 - offset / 2 - maxCircleRadius * 2, drawY
                        - maxCircleRadius * 2, maxCircleRadius * 2,
                        maxCircleRadius * 2);
                parent.fill(0);
                parent.text(country, x + w / 2 - offset / 2 - maxCircleRadius,
                        drawY + offset * 3 / 4);
            }
            
            parent.strokeWeight(1);
            //draw the circles for the country
            for (TypeCount t : computedValues.get(country)) {

                parent.fill(Theme.changeSaturation(
                        DataRow.getColorFor(t.getType()), .5f, true));

                parent.stroke(0xff555555);

                float radius = getCircleRadius(t.getCount());
                if (right)
                    parent.ellipse(x + w / 2 + offset / 2 + maxCircleRadius, drawY - radius, radius * 2, radius * 2);
                else
                    parent.ellipse(x + w / 2 - offset / 2 - maxCircleRadius, drawY - radius, radius * 2, radius * 2);
            }

            if (right)
                drawY -= maxCircleRadius * 2 + offset;
            right = !right;
        }
        //draw the outline of the percentage square
        parent.noFill();
        parent.rect(x + w / 2 + offset / 2, drawY - maxCircleRadius * 2, maxCircleRadius * 2, maxCircleRadius * 2);
        
        parent.noStroke();
        //draw the total percentages (upper right corner square)
        for (TypeCount t : totals) {
            int color = Theme.changeSaturation(
                    DataRow.getColorFor(t.getType()), .5f, true);
            parent.fill(color);
            float heightPercent = t.getCount() / (float) numTotalQuakes;
            parent.rect(x + w / 2 + offset / 2, drawY - heightPercent
                    * maxCircleRadius * 2, maxCircleRadius * 2, heightPercent
                    * maxCircleRadius * 2);
            drawY -= heightPercent * maxCircleRadius * 2;
        }

    }
    
    /**
     * Calculates the radius of the circle based on the number of earthquakes that
     * circle represents
     * 
     * @param count
     * @return
     */
    private float getCircleRadius(double count) {
        float maxDiameter = Math.min(
                (float) ((h - buffer * 2 - offset
                        * (Math.ceil(computedValues.size() / 2.0) - 1)) / (Math
                        .ceil(computedValues.size() / 2.0))),
                (w - buffer * 2 - offset * 2) / 2);

        double maxArea = Math.PI * Math.pow(maxDiameter / 2, 2);

        float area = (float) (maxArea * count / maxVal);
        return (float) (Math.sqrt(area / Math.PI));
    }

    private void computeMaxVal() {
        for (Set<TypeCount> countryData : computedValues.values()) {
            for (TypeCount t : countryData) {
                if (t.getCount() > maxVal)
                    maxVal = t.getCount();
            }
        }
    }

    private void calculateTotals() {
        HashMap<String, Integer> counts = new HashMap<String, Integer>();

        for (Set<TypeCount> countryData : computedValues.values()) {
            for (TypeCount t : countryData) {
                System.out.println(t.getType() + " " + t.getCount());
                if (counts.containsKey(t.getType())) {

                    int count = counts.get(t.getType());
                    counts.put(t.getType(), t.getCount() + count);
                } else
                    counts.put(t.getType(), t.getCount());
            }
        }

        totals = new TreeSet<TypeCount>();
        
        for (String type : counts.keySet()) {
            totals.add(new TypeCount(type, counts.get(type)));
            System.out.println("Type :" + counts.get(type));
        }
    }

    private void computeNominalData() {
        // populate the computed values with the continents and the set
        computedValues = new TreeMap<String, Set<TypeCount>>();
        for (DataRow.Continent c : DataRow.Continent.values()) {
            computedValues.put(c.toString(), new TreeSet<TypeCount>());
        }

        ArrayList<DataRow> list = new ArrayList<DataRow>(displayData.getDatum());
        Collections.sort(list, dataComp);

        String type = null;
        String continent = null;
        if (list.size() > 0) {
            type = list.get(0).getValue(dataType).toString();
            continent = list.get(0).getValue(DataRow.CONTINENT).toString();
        }

        int count = 0;

        for (DataRow quake : list) {
            // get continent and type out of the current quake
            String curContinent = quake.getValue(DataRow.CONTINENT).toString();
            String curType = quake.getValue(dataType).toString();

            // if we've come to data from a new continent
            if (!curContinent.equals(continent)) {
                computedValues.get(continent).add(new TypeCount(type, count));
                count = 1;
                type = curType;
                continent = curContinent;
            } else {
                if (curType.equals(type)) {
                    count++;
                } else {
                    computedValues.get(curContinent).add(new TypeCount(type, count));
                    count = 1;
                    type = curType;
                }
            }
        }
        computedValues.get(continent).add(new TypeCount(type, count));

        calculateTotals();
        computeMaxVal();
    }

    @Override
    public void filterBy(DataSet filteredData) {
        this.displayData = filteredData;
        computeNominalData();
    }

    private class TypeCount implements Comparable<TypeCount> {
        private String type;
        private int count;

        public TypeCount(String type, int count) {
            this.type = type;
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public int compareTo(TypeCount t) {
            return t.count - count;
        }

        public String getType() {
            return type;
        }
    }

}
