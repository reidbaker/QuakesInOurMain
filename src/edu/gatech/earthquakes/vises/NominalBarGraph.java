package edu.gatech.earthquakes.vises;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.gatech.earthquakes.components.Importer;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;
import edu.gatech.earthquakes.model.DataRow.Continent;
import processing.core.PApplet;

public class NominalBarGraph extends BarGraph
{
	private HashMap<Object, Integer> bars;
	
	public NominalBarGraph(int x, int y, int w, int h, DataSet displayData, String dataType) {
		super(x, y, w, h, displayData, dataType);
		bars = new HashMap<Object, Integer>();
		createBars();
	}

	@Override
	public void drawComponent(PApplet parent) {
		parent.noFill();
		parent.stroke(Theme.getBaseUIColor());
		parent.rect(x, y, w, h);
		parent.noStroke();
		//parent.stroke(Theme.getBrightUIColor());
		parent.fill(Theme.getDarkUIColor());
		int barW = (w - buffer*2 - 2*numDivisions)/numDivisions;
		int barX = x+buffer;
		
		for(Object key : bars.keySet()){
			parent.rect(barX, y+(h-buffer-bars.get(key)*10), barW, bars.get(key)*10);
			barX += barW+2;
			//parent.fill(255);
			parent.textAlign(PApplet.CENTER);
			parent.text(key.toString(),barX-barW/2, y+h);
		}
	}
	
	public void createBars(){
		HashSet<DataRow> currentData = (HashSet<DataRow>)displayData.getDatum();
		
		for(DataRow row: currentData){
			System.out.println(row.getValue(dataType));
			if(bars.containsKey(row.getValue(dataType)))
				bars.put(row.getValue(dataType), bars.get(row.getValue(dataType))+1);
			else
				bars.put(row.getValue(dataType), 1);
		}
		
		numDivisions = bars.size();
	}
	
	public static void main(String[] args) {
		NominalBarGraph n = new NominalBarGraph(0, 0, 0, 0,Importer.importData(), DataRow.CONTINENT);
		System.out.println(n.numDivisions);
	}

}
