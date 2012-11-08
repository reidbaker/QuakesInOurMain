package edu.gatech.earthquakes.vises;

import java.util.HashMap;
import java.util.HashSet;

import processing.core.PApplet;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;

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
		float heightScale = (h-buffer*2.0f)/calcMax();
		
		for(Object key : bars.keySet()){
			parent.rect(barX, y+(h-buffer-bars.get(key)*heightScale), barW, bars.get(key)*heightScale);
			barX += barW+2;
			//parent.fill(255);
			parent.textAlign(PApplet.CENTER);
			parent.text(key.toString(),barX-barW/2, y+h);
		}
	}
	
	private int calcMax(){
		int max = 0;
		for(Object key : bars.keySet()){
			if(max < bars.get(key))
				max = bars.get(key);
		}
		return max;
	}
	
	private void createBars(){
		HashSet<DataRow> currentData = (HashSet<DataRow>)displayData.getDatum();
		
		for(DataRow row: currentData){
			if(row.getValue(dataType)!=null)
				if(bars.containsKey(row.getValue(dataType)))
					bars.put(row.getValue(dataType), bars.get(row.getValue(dataType))+1);
				else
					bars.put(row.getValue(dataType), 1);
		}
		
		numDivisions = bars.size();
	}

}
