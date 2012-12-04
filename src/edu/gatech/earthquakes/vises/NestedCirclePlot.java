package edu.gatech.earthquakes.vises;

import java.util.ArrayList;
import java.util.Arrays;
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
    private boolean nominal = false;
    private TreeSet<TypeCount> totals;

    public NestedCirclePlot(int x, int y, int w, int h, DataSet displayData,
            String dataType) {
        super(x, y, w, h, displayData);
        this.dataType = dataType;

        DataComparator.CompareCategories category = null;
        switch (dataType) {
            case DataRow.MOMENT_MAGNITUDE:
                category = DataComparator.CompareCategories.MAGNITUDE;
                break;
            case DataRow.DEPENDENCY:
                category = DataComparator.CompareCategories.DEPENDENCY;
                nominal = true;
                break;
            case DataRow.TYPE:
                category = DataComparator.CompareCategories.TYPE;
                nominal = true;
                break;
        }

        dataComp = new DataComparator(
                DataComparator.CompareCategories.CONTINENT, category);

        if (nominal)
            computeNominalData();
        else
            computeNumericData();
    }

    public void drawComponent(PApplet parent) {
        super.drawComponent(parent);

        float drawY = y + h - buffer;
        float maxCircleRadius = getCircleRadius(maxVal);

        boolean right = false;
        parent.noStroke();

        parent.textSize(offset*3/4);
        parent.textAlign(PApplet.CENTER);
        
        for (String country : computedValues.keySet()) {

            parent.fill(DataRow.getColorFor(country));
          
            if (right) {
                parent.rect(x + w / 2 + offset / 2,
                        drawY - maxCircleRadius * 2, maxCircleRadius * 2,
                        maxCircleRadius * 2);
                parent.fill(0);
                parent.text(country, x + w / 2 + offset / 2 + maxCircleRadius, drawY + offset*3/4);
            } else{
                parent.rect(x + w / 2 - offset / 2 - maxCircleRadius * 2, drawY
                        - maxCircleRadius * 2, maxCircleRadius * 2,
                        maxCircleRadius * 2);
                parent.fill(0);
                parent.text(country, x + w / 2 - offset / 2 - maxCircleRadius, drawY + offset*3/4);
            }
            float firstRadius = 0;

            for (TypeCount t : computedValues.get(country)) {

                if (firstRadius == 0)
                    firstRadius = getCircleRadius(t.getCount());

                parent.fill(Theme.changeSaturation(DataRow.getColorFor(t.getType()),.5f,true));
               // parent.fill(DataRow.getColorFor(t.getType()));
               parent.strokeWeight(1);
                parent.stroke(0xff555555);

                float radius = getCircleRadius(t.getCount());
                if (right) {
                    parent.ellipse(x + w / 2 + offset / 2 + maxCircleRadius,
                            drawY - radius, radius * 2, radius * 2);
                } else

                    parent.ellipse(x + w / 2 - offset / 2 - maxCircleRadius,
                            drawY - radius, radius * 2, radius * 2);
            }

            if (right)
                drawY -= maxCircleRadius * 2 + offset;

            right = !right;
        }
        
        parent.stroke(0);

        for (TypeCount t : totals) {
            int color =  Theme.changeSaturation(DataRow.getColorFor(t.getType()),.5f,true);
            parent.fill(Theme.rgba(color,180));
            float heightPercent = 0;//getCircleRadius(t.getCount())/getCircleRadius(count);

            parent.rect(x + w / 2 + offset / 2, drawY - heightPercent*maxCircleRadius*2, maxCircleRadius*2,heightPercent*maxCircleRadius*2);
            drawY -= heightPercent*maxCircleRadius*2;
        }

    }

    /*
     * All of th scaling is done with the formula of:
     * 
     * f(x) = (b-a)(x-min)/(max-min) + a
     * 
     * where [min,max] maps to [a,b]
     */
    // [min,max] = [0,maxVal] -> [a,b] = [0,maxArea]
    private float getCircleRadius(double count) {
        float maxDiameter = Math.min( (float) ((h - buffer * 2 - offset
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
                } 
                else
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
        //populate the computed values with the continents and the set 
        computedValues = new TreeMap<String,Set<TypeCount>>();
        for(DataRow.Continent c: DataRow.Continent.values()){
            computedValues.put(c.toString(), new TreeSet<TypeCount>());
        }
        
        ArrayList<DataRow> list = new ArrayList<DataRow>(displayData.getDatum());
        Collections.sort(list, dataComp);
        
        String type = null;
        String continent = null;
        if(list.size() >0){
            type = list.get(0).getValue(dataType).toString();
            continent = list.get(0).getValue(DataRow.CONTINENT).toString(); 
        }
        
        int count = 0;
        System.out.println("Num Quakes: " + list.size());
        int numQuakesCounted = 0;
        for (DataRow quake : list) {
            // get continent and type out of the current quake
            String curContinent = quake.getValue(DataRow.CONTINENT).toString();
            String curType = quake.getValue(dataType).toString();
            
            //if we've come to data from a new continent
            if(!curContinent.equals(continent)){
                computedValues.get(continent).add(new TypeCount(type, count));
                count = 1;
                type = curType;
                continent = curContinent;
            }
            else{
                if (curType.equals(type)) {
                    count++;
                } 
                else {
                    computedValues.get(curContinent).add(new TypeCount(type, count));
                    count = 1;
                    type = curType;
                    numQuakesCounted += count;
                }
            }
        }
        computedValues.get(continent).add(new TypeCount(type, count));
        numQuakesCounted += count;
        
        System.out.println("NumQuakesCounted: " + numQuakesCounted);
        calculateTotals();
        computeMaxVal();
    }

    private void computeNumericData() {
        computedValues = new TreeMap<String, Set<TypeCount>>();
        // totals = new TreeMap<String, Integer>();
        /*
         * ArrayList<DataRow> list = new
         * ArrayList<DataRow>(displayData.getDatum()); Collections.sort(list,
         * dataComp);
         * 
         * for (DataRow quake : list) { // if we already have data from this
         * continent String continent =
         * quake.getValue(DataRow.CONTINENT).toString(); String bucket =
         * quake.getValue(dataType).toString();
         * 
         * if (computedValues.containsKey(continent)) { if
         * (computedValues.get(continent).containsKey(bucket)) { int value =
         * computedValues.get(continent).get(bucket);
         * computedValues.get(continent).put(bucket, ++value); } else{
         * computedValues.get(continent).put(bucket, 1); } } // if we don't
         * already have this continent else { computedValues.put(continent, new
         * HashMap<String, Integer>());
         * computedValues.get(continent).put(bucket, 1); } } calculateTotals();
         * computeMaxVal();
         */
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
            return t.count-count;
        }

        public String getType() {
            return type;
        }
    }

}
