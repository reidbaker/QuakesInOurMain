package edu.gatech.earthquakes.vises;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import processing.core.PApplet;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;

public class NominalBarGraph extends BarGraph
{
	private Hashtable<String, Integer> bars;

	public NominalBarGraph(int x, int y, int w, int h, DataSet displayData, String dataType) {
		super(x, y, w, h, displayData, dataType, "Earthquake " + dataType);
		createBars();
	}

	@Override
	public void drawComponent(PApplet parent) {
		super.drawComponent(parent);


		parent.noStroke();

		//parent.fill(Theme.getDarkUIColor());

		//width of the bars - scales based on the number of bars that we have
		int barW = (w - buffer*2 - 2*numDivisions)/numDivisions;
		int barX = x+buffer+4;
		//the scale factor for the height of the bars
		float heightScale = (h-buffer*2.0f)/calcMax();

		parent.textAlign(PApplet.CENTER);
		parent.textSize(barW/4);

		ArrayList<String> sortedKeys = new ArrayList<>(bars.keySet());
		Collections.sort(sortedKeys);
		for(String key : sortedKeys){
			parent.fill(DataRow.getColorFor(key));
			parent.stroke(Theme.rgba(DataRow.getColorFor(key), 100));
			parent.rect(barX, y+(h-buffer-bars.get(key)*heightScale), barW, bars.get(key)*heightScale);
			barX += barW+2;
			parent.fill(Theme.getDarkUIColor());
			parent.textAlign(PApplet.LEFT);
			parent.pushMatrix();
			parent.translate(barX-barW/2, y+h-(buffer) - 5);
			parent.rotate(-1 * PApplet.HALF_PI);
			parent.text(key.toString(),0,0);
			parent.popMatrix();
		}

		drawAxes(parent);
	}


	private void drawAxes(PApplet parent){
		parent.stroke(Theme.getDarkUIColor());
		parent.fill(Theme.getDarkUIColor());

		parent.line(x+buffer, y+h-buffer, x+ w-buffer, y+h-buffer); //bottom
		parent.line(x+buffer, y+buffer, x+ buffer, y+h-buffer); //left

		int numTicks = 10;
		int tickVal = calcMax()/numTicks;
		int tickLabel = 0;

		parent.textSize(8);
		for(int i=0; i<= h-buffer*2; i+= (h-buffer*2)/numTicks){
			parent.line(x+buffer-2, y+h-buffer-i, x+buffer+2, y+h-buffer-i);
			parent.text(tickLabel  + "", x+buffer/2, y+h-buffer-i + 4 );
			tickLabel += tickVal;
		}
	}

	/**
	 * Finds the value of the highest bar
	 *
	 * @return
	 */
	private int calcMax(){
		int max = 0;
		for(Object key : bars.keySet()){
			if(max < bars.get(key))
				max = bars.get(key);
		}
		return max;
	}

	private void createBars(){
		bars = new Hashtable<String, Integer>();

		for(DataRow row: displayData){
			if(row.getValue(dataType)!=null)
				if(bars.containsKey(row.getValue(dataType).toString()))
					bars.put(row.getValue(dataType).toString(), bars.get(row.getValue(dataType).toString()) +1);
				
				else
					bars.put(row.getValue(dataType).toString(), 1);
		}
		numDivisions = bars.size();
	}

	@Override
	public void filterBy(DataSet ds){
		super.filterBy(ds);
		createBars();
	}

}
