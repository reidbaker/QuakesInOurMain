package edu.gatech.earthquakes.vises;

import java.util.*;

import processing.core.PApplet;

import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;

public class AftershockMap extends Multi {

	private DataRow mainQuake;
	private int sizeOfDegree;
	private double[] latRange;
	private double[] lonRange;
	
	public AftershockMap(int x, int y, int w, int h, DataSet displayData) {
		super(x, y, w, h, displayData);
		// TODO Auto-generated constructor stub
		for(DataRow quake : displayData.getDatum()){
			System.out.println(quake.getValue(DataRow.DEPENDENCY));
			if(quake.getValue(DataRow.DEPENDENCY).equals(DataRow.Dependency.INDEPENDENT))
				mainQuake = quake;
		}
		calculateSizeOfDegree();
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
		for(int i=0; i< c.length; i++){
			double x = (w*(c[i][0]-latRange[0]))/(latRange[1]-latRange[0]);
			double y = (h*(c[i][1]-lonRange[0]))/(lonRange[1]-lonRange[0]);
			double minSize = 5;
			double maxSize = 30;
			
			//for magnitude, min and max are assumed to be 3 and 7 based on moment magnitude numbers
			double size = (maxSize-minSize)*(m[i]-3)/4 + minSize;
			
			parent.ellipse((float)x, (float)y, (float)size, (float)size);
			//System.out.println("Lat: " + c[i][0] + " X: " + x + ", Lon: " + c[i][1]);
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
	
	private void calculateSizeOfDegree(){
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
			sizeOfDegree = w/(((int)dif) + 1);
			
			latRange = new double[]{lat[0]-buffer, lat[lat.length-1]+buffer};
			lonRange = new double[]{lon[0]-buffer, lon[0]+dif+buffer};
		}
		else{
			dif = lon[lon.length-1]-lon[0];
			sizeOfDegree = h/(((int)dif) + 1);
			
			lonRange = new double[]{lon[0]-buffer, lon[lon.length-1]+buffer};
			latRange = new double[]{lat[0]-buffer, lat[0]+dif+buffer};
		}
		
		System.out.println(dif);
		System.out.println(Arrays.toString(lonRange));
		System.out.println(Arrays.toString(latRange));
		
	}
}
