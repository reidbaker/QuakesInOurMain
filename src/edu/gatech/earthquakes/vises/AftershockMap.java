package edu.gatech.earthquakes.vises;

import java.text.DecimalFormat;
import java.util.*;

import processing.core.PApplet;

import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;

public class AftershockMap extends Multi {

	private double[] latRange;
	private double[] lonRange;
	private int buffer = 20;
	private DecimalFormat df;
	
	public AftershockMap(int x, int y, int w, int h, DataSet displayData) {
		super(x, y, w, h, displayData);
		
		calculateRanges();
		df = new DecimalFormat("0.00");
	}
	
	public static DataSet findAftershocks(DataRow mainQuake, DataSet allQuakes){
		Date date = (Date)mainQuake.getValue(DataRow.DATE);
		Set<DataRow> dep = new HashSet<DataRow>();
		
		for(DataRow quake : allQuakes.getDatum()){
			if(quake.getValue(DataRow.DEPENDENCY).equals(DataRow.Dependency.DEPENDENT) && quake.getValue(DataRow.MAIN_DATE).equals(date))
				dep.add(quake);
		}
		
		dep.add(mainQuake);

		return new DataSet(dep);
	}
	
	public void drawComponent(PApplet parent){
		super.drawComponent(parent);
		
		double[][] c = getCoordinates();
		double[] m = getMagnitudes();
		
		/*All of th scaling is done with the formula of:
		 * 
		 * f(x) = (b-a)(x-min)/(max-min) + a
		 * 
		 * where [min,max] maps to [a,b]
		 */
		
		double minSize = 5;
		double maxSize = 30;
		
		for(int i=0; i< c.length; i++){
			double qx = ((w-buffer*2)*(c[i][0]-latRange[0]))/(latRange[1]-latRange[0]);
			double qy = ((h-buffer*2)*(c[i][1]-lonRange[0]))/(lonRange[1]-lonRange[0]);
			
			//for magnitude, min and max are assumed to be 3 and 7 based on moment magnitude numbers
			double size = (maxSize-minSize)*(m[i]-3)/4 + minSize;
			
			parent.fill(Theme.getColorPallette(1)[0]-0xAA000000);
			parent.stroke(Theme.getColorPallette(1)[0]-0x66000000);
			parent.ellipse(x +(float)qx+buffer, y + h-(float)qy-buffer, (float)size, (float)size);
			//System.out.println("Lat: " + c[i][0] + " X: " + x + ", Lon: " + c[i][1]);
		}
		
		drawAxes(parent);
		
		
	}
	
	private void drawAxes(PApplet parent){
		parent.stroke(Theme.getDarkUIColor());
		parent.fill(Theme.getDarkUIColor());
		parent.textSize(8);
		parent.line(x+buffer, y+buffer, x+ w-buffer, y+buffer); //top
		parent.line(x+buffer, y+h-buffer, x+ w-buffer, y+h-buffer); //bottom
		parent.line(x+buffer, y+buffer, x+ buffer, y+h-buffer); //left
		parent.line(x+w-buffer, y+buffer, x+ w-buffer, y+h-buffer); //right
		
		parent.text(df.format(latRange[0]), x+buffer, y+h);
		parent.text(df.format(latRange[1]), x+w-buffer, y+h);
		parent.text(df.format(lonRange[0]), x+buffer, y+h-buffer);
		parent.text(df.format(lonRange[1]), x+buffer, y+buffer);
		
		double lat = 0; 
		
		for(int i=0; i<(w-buffer*2); i+=50){
			lat = (((latRange[1]-latRange[0])*i)/(w-buffer*2)) + latRange[0];
			parent.line(x+buffer+i, y+h-buffer-2, x+buffer+i, y+h-buffer+2);
			parent.text(df.format(lat),x+buffer+i , y+h);
		}
		
		double lon = 0;
		for(int i=0; i<(h-buffer*2); i+=50){
			lon = (((lonRange[1]-lonRange[0])*i)/(h-buffer*2)) + lonRange[0];
			parent.line(x+buffer-2, y+h-buffer-i, x+buffer+2, y+h-buffer-i);
			parent.text(df.format(lon),x+buffer/2 , y+h-buffer-i);
		}
	}
	
	private double[][] getCoordinates(){
		double[][] coords = new double[displayData.getDatum().size()][2];
		int i = 0;
		for(DataRow quake: displayData.getDatum()){
			coords[i][0] = (Double)quake.getValue(DataRow.LATTITUDE);
			coords[i++][1] = (Double)quake.getValue(DataRow.LONGITUDE);
		}
		
		return coords;
	}
	
	private double[] getMagnitudes(){
		
		double[] mag = new double[displayData.getDatum().size()];
		int i = 0;
		for(DataRow quake: displayData.getDatum()){
			mag[i++] = (Double)quake.getValue(DataRow.MOMENT_MAGNITUDE);
		}
		
		return mag;
	}
	
	private void calculateRanges(){
		double[][] coords = getCoordinates();
		
		double[] lat = new double[coords.length];
		double[] lon = new double[coords.length];
		
		for(int i = 0; i< coords.length; i++){
			lat[i] = coords[i][0];
			lon[i] = coords[i][1];
		}
		
		Arrays.sort(lat);
		Arrays.sort(lon);
		
		double dif = 0;
		double buffer = .1;
		if(lat[lat.length-1]-lat[0] > lon[lon.length-1]-lon[0] ){
			dif = lat[lat.length-1]-lat[0];
			
			latRange = new double[]{lat[0]-buffer, lat[lat.length-1]+buffer};
			lonRange = new double[]{lon[0]-buffer, lon[0]+dif+buffer};
		}
		else{
			dif = lon[lon.length-1]-lon[0];
			
			lonRange = new double[]{lon[0]-buffer, lon[lon.length-1]+buffer};
			latRange = new double[]{lat[0]-buffer, lat[0]+dif+buffer};
		}
		
		System.out.println(dif);
		System.out.println(Arrays.toString(lonRange));
		System.out.println(Arrays.toString(latRange));
		
	}
}
