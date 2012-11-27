package edu.gatech.earthquakes.vises;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import processing.core.PApplet;

import com.google.common.collect.Maps;

import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.model.DataComparator;
import edu.gatech.earthquakes.model.DataComparator.CompareCategories;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataRow.Continent;
import edu.gatech.earthquakes.model.DataSet;

public class SummaryStream extends Aggregate implements Filterable{
	
	private String dataType;
	
	private HashMap<String, float[]> percentages;
	
	public SummaryStream(int x, int y, int w, int h, DataSet displayData, String dataType){
		this(x,y,w,h,displayData);
	}
	
	public SummaryStream(int x, int y, int w, int h, DataSet displayData) {
		super(x, y, w, h, displayData);
		regenerateMap();
	}
	
	@Override
	public void drawComponent(PApplet parent) {
		super.drawComponent(parent);
		drawAxes(parent);
	}
	
	private void drawAxes(PApplet parent){
		// NO
	}
	
	private void regenerateMap(){
		percentages = Maps.newHashMap();
		int yearMin = 0, yearMax = 0;
		DataComparator dc = new DataComparator(CompareCategories.DATE, DataComparator.categoryMap.get(displayData));
		TreeSet<DataRow> rowTree = new TreeSet<>(dc);
		rowTree.addAll(displayData.getDatum());
		DataRow[] rows = rowTree.toArray(new DataRow[]{});
		
		HashMap<String, Integer[]> counts = new HashMap<>();
		Calendar cal = Calendar.getInstance();
		Date d = (Date)rows[0].getValue(DataRow.DATE);
		cal.setTime(d);
		yearMin = cal.get(Calendar.YEAR);
		d = (Date)rows[rows.length-1].getValue(DataRow.DATE);
		yearMax = cal.get(Calendar.YEAR);
		
		//Hard Coding Continent for now...  will factor out later.
		for(Continent c : DataRow.Continent.values()){
			
		}
	}
	
	@Override
	public void filterBy(DataSet ds){
		this.displayData = ds;
		regenerateMap();
	}
	
}
