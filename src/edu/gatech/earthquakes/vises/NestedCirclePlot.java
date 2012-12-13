package edu.gatech.earthquakes.vises;

import java.awt.Rectangle;
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

    // for keeping track of how to lay out the country squares
    private int maxCountryWidth;

    public NestedCirclePlot(int x, int y, int w, int h, DataSet displayData,
	    String dataType, int numTotalQuakes) {
	super(x, y, w, h, displayData, "Type by Continent");
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

	float maxCircleRadius = getCircleRadius(maxVal);
	float drawY = y + h - buffer - maxCountryWidth;
	float drawX = x + buffer;

	parent.noStroke();

	parent.textSize(offset * 3 / 4);
	parent.textAlign(PApplet.CENTER);

	for (String country : computedValues.keySet()) {
	    drawCountry(parent, country, drawX, drawY);

	    drawX += maxCountryWidth + offset;

	    if (drawX > x + w - buffer - maxCountryWidth) {
		drawX = x + buffer;
		drawY -= maxCountryWidth + offset;
	    }

	}
	// draw the outline of the percentage square
	parent.noFill();
	parent.noStroke();
	parent.rect(drawX, drawY - maxCircleRadius * 2, maxCircleRadius * 2,
	        maxCircleRadius * 2);
	drawY += maxCountryWidth;
	// draw the total percentages (upper right corner square)
	for (TypeCount t : totals) {
	    int color = Theme.changeSaturation(
		    DataRow.getColorFor(t.getType()), .5f, true);
	    parent.fill(color);
	    float heightPercent = t.getCount() / (float) numTotalQuakes;
	    parent.rect(drawX, drawY - heightPercent * maxCircleRadius * 2,
		    maxCircleRadius * 2, heightPercent * maxCircleRadius * 2);
	    drawY -= heightPercent * maxCircleRadius * 2;
	}

    }

    private void drawCountry(PApplet parent, String country, float xPos,
	    float yPos) {
	float maxCircleRadius = getCircleRadius(maxVal);

	// draw the colored square for the country and write the country name
	parent.noStroke();
	parent.fill(DataRow.getColorFor(country));
	parent.rect(xPos, yPos, maxCircleRadius * 2, maxCircleRadius * 2);
	parent.fill(0);
	parent.text(country, xPos + maxCircleRadius, yPos + maxCircleRadius * 2
	        + offset * 3 / 4);

	parent.strokeWeight(1);
	// draw the circles for the country
	for (TypeCount t : computedValues.get(country)) {

	    parent.fill(Theme.changeSaturation(
		    DataRow.getColorFor(t.getType()), .5f, true));

	    parent.stroke(0xff555555);

	    float radius = getCircleRadius(t.getCount());
	    parent.ellipse(xPos + maxCircleRadius, yPos + maxCircleRadius * 2
		    - radius, radius * 2, radius * 2);
	}
    }

    /**
     * Calculates the radius of the circle based on the number of earthquakes
     * that circle represents
     * 
     * @param count
     * @return
     */
    private float getCircleRadius(double count) {
	double maxArea = Math.PI * Math.pow(maxCountryWidth / 2, 2);

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
		    computedValues.get(curContinent).add(
			    new TypeCount(type, count));
		    count = 1;
		    type = curType;
		}
	    }
	}
	if (continent != null)
	    computedValues.get(continent).add(new TypeCount(type, count));

	calculateTotals();
	computeMaxVal();
    }

    @Override
    public void filterBy(DataSet filteredData) {
	this.displayData = filteredData;
	computeNominalData();
    }

    public void resizeTo(Rectangle bounds) {
	super.resizeTo(bounds);

	// this is an absolutely terrible way to do this, but for the purposes
	// of the demo tomorrow, this is
	// how we are doing it for now.
	maxCountryWidth = Math.max(Math.max(
	        Math.min((w - buffer * 2) / 4 - offset, (h - buffer * 2) / 2
	                - offset),
	        Math.min((h - buffer * 2) / 4 - offset, (w - buffer * 2) / 2
	                - offset)), Math.min((w - buffer * 2) / 3 - offset,
	        (h - buffer * 2) / 3 - offset));

	System.out.println((float) w / h);

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
