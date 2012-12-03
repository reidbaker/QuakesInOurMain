package edu.gatech.earthquakes.vises;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import processing.core.PApplet;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.model.DataComparator;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;

// name subject to change
/*
 * The idea is that this is a set of circles that can show two different types of groupings.
 * Right now, its being done for location and then by quake type
 */

public class NestedCirclePlot extends Aggregate implements Filterable {
	// the thing that will be grouped by after location
	private String dataType;
	private DataComparator dataComp;
	private HashMap<String, HashMap<String, Integer>> computedValues;
	private double maxVal = 0;
	private int offset = 25;
	private boolean nominal = false;
	private HashMap<String, Integer> totals;

	public NestedCirclePlot(int x, int y, int w, int h, DataSet displayData, String dataType) {
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
		
		dataComp = new DataComparator(DataComparator.CompareCategories.CONTINENT, category);
		
		if(nominal)
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
		for(String bucket: totals.keySet()){
			parent.fill(Theme.rgba(DataRow.getColorFor(bucket),150));
			float percent= (float)totals.get(bucket)/displayData.getDatum().size();
			parent.rect(x+buffer, drawY-percent*(h-buffer*2), 20, percent*(h-buffer*2));
			drawY -= percent*(h-buffer*2);
		}
		drawY = y + h - buffer;

		for (String country : computedValues.keySet()) {

			parent.fill(Theme.rgba(DataRow.getColorFor(country), 100));
			parent.stroke(Theme.rgba(DataRow.getColorFor(country), 150));
			if (right){
				parent.fill(Theme.rgba(DataRow.getColorFor(country), 100));
				parent.stroke(Theme.rgba(DataRow.getColorFor(country), 150));
				parent.rect(x + w / 2 + offset / 2, drawY - maxCircleRadius * 2, maxCircleRadius * 2, maxCircleRadius * 2);
			}
			else
				parent.rect(x + w / 2 - offset / 2 - maxCircleRadius * 2, drawY - maxCircleRadius * 2, maxCircleRadius * 2, maxCircleRadius * 2);

			float firstRadius = 0;
			
			for (String bucket : computedValues.get(country).keySet()) {

				if (firstRadius == 0)
					firstRadius = getCircleRadius(computedValues.get(country) .get(bucket));

				parent.fill(Theme.rgba(DataRow.getColorFor(bucket), 100));
				parent.stroke(Theme.rgba(DataRow.getColorFor(bucket), 250));

				float radius = getCircleRadius(computedValues.get(country).get(bucket));
				if (right) {
					parent.ellipse(x + w / 2 + offset / 2 + maxCircleRadius,
							drawY-radius,
							radius * 2, radius * 2);
				} else

					parent.ellipse(x + w / 2 - offset / 2 - maxCircleRadius,
							drawY -radius,
							radius * 2, radius * 2);
			}

			if (right)
				drawY -= maxCircleRadius * 2 + offset;
			
			right = !right;
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
		for (HashMap<String, Integer> countryTable : computedValues.values()) {
			for (Integer i : countryTable.values()) {
				if (i > maxVal)
					maxVal = i;
			}
		}
	}
	
	private void calculateTotals(){
		for (HashMap<String, Integer> countryTable : computedValues.values()) {
			for (String bucket : countryTable.keySet()) {
				if (totals.containsKey(bucket)){
					totals.put(bucket, totals.get(bucket) + countryTable.get(bucket));
				}
				else{
					totals.put(bucket, countryTable.get(bucket));
				}
			}
		}
	}
	
	
	private void computeNominalData() {
		computedValues = new HashMap<String, HashMap<String, Integer>>();
		totals = new HashMap<String, Integer>();
		
		ArrayList<DataRow> list = new ArrayList<DataRow>(displayData.getDatum());
		Collections.sort(list, dataComp);

		for (DataRow quake : list) {
			// if we already have data from this continent
			String continent = quake.getValue(DataRow.CONTINENT).toString();
			String bucket = quake.getValue(dataType).toString();

			if (computedValues.containsKey(continent)) {
				if (computedValues.get(continent).containsKey(bucket)) {
					int value = computedValues.get(continent).get(bucket);
					computedValues.get(continent).put(bucket, ++value);
				}
				else{
					computedValues.get(continent).put(bucket, 1);
				}
			}
			// if we don't already have this continent
			else {
				computedValues.put(continent, new HashMap<String, Integer>());
				computedValues.get(continent).put(bucket, 1);
			}
		}
		calculateTotals();
		computeMaxVal();
	}
	
	private void computeNumericData() {
		computedValues = new HashMap<String, HashMap<String, Integer>>();
		totals = new HashMap<String, Integer>();
		
		ArrayList<DataRow> list = new ArrayList<DataRow>(displayData.getDatum());
		Collections.sort(list, dataComp);

		for (DataRow quake : list) {
			// if we already have data from this continent
			String continent = quake.getValue(DataRow.CONTINENT).toString();
			String bucket = quake.getValue(dataType).toString();

			if (computedValues.containsKey(continent)) {
				if (computedValues.get(continent).containsKey(bucket)) {
					int value = computedValues.get(continent).get(bucket);
					computedValues.get(continent).put(bucket, ++value);
				}
				else{
					computedValues.get(continent).put(bucket, 1);
				}
			}
			// if we don't already have this continent
			else {
				computedValues.put(continent, new HashMap<String, Integer>());
				computedValues.get(continent).put(bucket, 1);
			}
		}
		calculateTotals();
		computeMaxVal();
	}

	@Override
	public void filterBy(DataSet filteredData) {
		this.displayData = filteredData;
		computeNominalData();
	}

}
