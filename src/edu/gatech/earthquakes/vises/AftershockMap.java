package edu.gatech.earthquakes.vises;

import java.text.DecimalFormat;
import java.util.*;

import processing.core.PApplet;

import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.interfaces.Brushable;
import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.interfaces.Interactable;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;
import edu.gatech.earthquakes.model.Interaction;

public class AftershockMap extends Individual implements Interactable,
		Filterable {

	private double[] latRange;
	private double[] lonRange;
	private int buffer = 20;
	private DecimalFormat df;
	private double[] highlightedPos;
	private DataSet aftershocks;

	public AftershockMap(int x, int y, int w, int h, DataRow displayData) {
		super(x, y, w, h, displayData);
		df = new DecimalFormat("0.00");
	}

	public void drawComponent(PApplet parent) {
		super.drawComponent(parent);

		double[][] coords = getCoordinates();
		double[] m = getMagnitudes();

		for (int i = 0; i < coords.length; i++) {

			// for magnitude, min and max are assumed to be 3 and 7 based on
			// moment magnitude numbers
			double[] c = getDrawingCoord(coords[i][0], coords[i][1]);

			if (highlightedPos != null && coords[i][0] == highlightedPos[0]
					&& coords[i][1] == highlightedPos[1]) {
				parent.fill(Theme.getColorPallette(2)[1] - 0x99000000);
				parent.stroke(Theme.getColorPallette(2)[1] - 0x33000000);
				// parent.text(c[i][0] + "," + c[i][1], x +(float)qx+buffer,
				// y + h-(float)qy-buffer);
			} else {
				parent.fill(Theme.getColorPallette(1)[0] - 0xAA000000);
				parent.stroke(Theme.getColorPallette(1)[0] - 0x66000000);
			}

			parent.ellipse((float) c[0], (float) c[1],
					(float) getCircleSize(m[i]), (float) getCircleSize(m[i]));
			// parent.text(c[i][0] + "," + c[i][1], x +(float)qx+buffer, y +
			// h-(float)qy-buffer);
			// System.out.println("Lat: " + c[i][0] + " X: " + x + ", Lon: "
			// + c[i][1]);
		}

		drawAxes(parent);
	}

	private void drawAxes(PApplet parent) {
		parent.stroke(Theme.getDarkUIColor());
		parent.noFill();

		/*
		 * if we want to only have some of the lines parent.line(x+buffer,
		 * y+buffer, x+ w-buffer, y+buffer); //top parent.line(x+buffer,
		 * y+h-buffer, x+ w-buffer, y+h-buffer); //bottom parent.line(x+buffer,
		 * y+buffer, x+ buffer, y+h-buffer); //left parent.line(x+w-buffer,
		 * y+buffer, x+ w-buffer, y+h-buffer); //right
		 */

		parent.rect(x + buffer, y + buffer, w - buffer * 2, h - buffer * 2);

		parent.fill(Theme.getDarkUIColor());
		parent.textSize(8);

		// label the edges
		parent.text(df.format(latRange[0]), x + buffer, y + h);
		parent.text(df.format(latRange[1]), x + w - buffer, y + h);
		parent.text(df.format(lonRange[0]), x + buffer, y + h - buffer);
		parent.text(df.format(lonRange[1]), x + buffer, y + buffer);

		// make and label the latitude tick marks
		double lat = 0;
		for (int i = 0; i < (w - buffer * 2); i += 50) {
			lat = (((latRange[1] - latRange[0]) * i) / (w - buffer * 2))
					+ latRange[0];
			parent.line(x + buffer + i, y + h - buffer - 2, x + buffer + i, y
					+ h - buffer + 2);
			parent.text(df.format(lat), x + buffer + i, y + h - buffer / 4);
		}

		// make and label the longitude tick marks
		double lon = 0;
		for (int i = 0; i < (h - buffer * 2); i += 50) {
			lon = (((lonRange[1] - lonRange[0]) * i) / (h - buffer * 2))
					+ lonRange[0];
			parent.line(x + buffer - 2, y + h - buffer - i, x + buffer + 2, y
					+ h - buffer - i);
			parent.text(df.format(lon), x + buffer / 2, y + h - buffer - i);
		}
	}

	private double[][] getCoordinates() {
		double[][] coords = new double[aftershocks.getDatum().size()][2];

		int i = 0;
		for (DataRow quake : aftershocks) {
			coords[i][0] = (Double) quake.getValue(DataRow.LATTITUDE);
			coords[i++][1] = (Double) quake.getValue(DataRow.LONGITUDE);
		}

		return coords;
	}

	private double[] getMagnitudes() {

		double[] mag = new double[aftershocks.getDatum().size()];
		int i = 0;
		for (DataRow quake : aftershocks) {
			mag[i++] = (Double) quake.getValue(DataRow.MOMENT_MAGNITUDE);
		}

		return mag;
	}

	private void calculateRanges() {
		double[][] coords = getCoordinates();

		double[] lat = new double[coords.length];
		double[] lon = new double[coords.length];

		for (int i = 0; i < coords.length; i++) {
			lat[i] = coords[i][0];
			lon[i] = coords[i][1];
		}

		Arrays.sort(lat);
		Arrays.sort(lon);

		double dif = 0;
		double buffer = .1;

		if (aftershocks.getDatum().size() > 2) {
			if (lat[lat.length - 1] - lat[0] > lon[lon.length - 1] - lon[0]) {
				dif = lat[lat.length - 1] - lat[0];

				latRange = new double[] { lat[0] - buffer,
						lat[lat.length - 1] + buffer };
				lonRange = new double[] { lon[0] - buffer,
						lon[0] + dif + buffer };
			} else {
				dif = lon[lon.length - 1] - lon[0];

				lonRange = new double[] { lon[0] - buffer,
						lon[lon.length - 1] + buffer };
				latRange = new double[] { lat[0] - buffer,
						lat[0] + dif + buffer };
			}
		}
		else {
			double mainLat = (Double)displayData.getValue(DataRow.LATTITUDE);
			double mainLon = (Double)displayData.getValue(DataRow.LONGITUDE);
			
			latRange = new double[] { mainLat-1-buffer, mainLat+1+buffer };
			lonRange = new double[] { mainLon-1-buffer, mainLon+1+buffer };
		}

		// System.out.println(dif);
		// System.out.println(Arrays.toString(lonRange));
		// System.out.println(Arrays.toString(latRange));

	}

	@Override
	public void handleInput(Interaction interaction) {

		double[][] coords = getCoordinates();
		double[] mag = getMagnitudes();
		double qx = 0;
		double qy = 0;
		boolean found = false;

		for (int i = 0; i < coords.length && !found; i++) {
			double[] c = getDrawingCoord(coords[i][0], coords[i][1]);

			if (Math.abs(interaction.getParentApplet().mouseX - c[0]) < getCircleSize(mag[i]) / 2
					&& Math.abs(interaction.getParentApplet().mouseY - c[1]) < getCircleSize(mag[i]) / 2) {
				highlightedPos = new double[] { coords[i][0], coords[i][1] };
				found = true;
			}

			if (!found)
				highlightedPos = null;

		}

	}

	/*
	 * All of th scaling is done with the formula of:
	 * 
	 * f(x) = (b-a)(x-min)/(max-min) + a
	 * 
	 * where [min,max] maps to [a,b]
	 */
	private double getCircleSize(double mag) {
		double minSize = 5;
		double maxSize = 30;

		return (maxSize - minSize) * (mag - 3) / 4 + minSize;
	}

	private double[] getDrawingCoord(double lat, double lon) {
		double qx = ((w - buffer * 2) * (lat - latRange[0]))
				/ (latRange[1] - latRange[0]);
		double qy = ((h - buffer * 2) * (lon - lonRange[0]))
				/ (lonRange[1] - lonRange[0]);

		return new double[] { x + qx + buffer, y + h - qy - buffer };
	}

	@Override
	public void filterBy(DataSet filteredData) {
		Date date = (Date) displayData.getValue(DataRow.DATE);

		Set<DataRow> shocks = new HashSet<DataRow>();

		for (DataRow quake : filteredData) {
			if (quake.getValue(DataRow.DEPENDENCY).equals(
					DataRow.Dependency.DEPENDENT)
					&& quake.getValue(DataRow.MAIN_DATE).equals(date))
				shocks.add(quake);
			if (quake.equals(displayData))
				shocks.add(displayData);
		}

		aftershocks = new DataSet(shocks);
		calculateRanges();
	}
}
