package edu.gatech.earthquakes.vises;

import java.awt.Rectangle;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import processing.core.PApplet;

import com.google.common.collect.Lists;

import edu.gatech.earthquakes.components.Controller;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;

public class Workspace extends AbstractVisualization {

	private DataSet masterData;
	List<AbstractVisualization> vises;

	public Workspace(int x, int y, int w, int h, DataSet masterData) {
		super(x, y, w, h, "Primary Workspace", true);
		this.masterData = masterData;
		vises = Lists.newArrayList();
		intantiateVises();
	}

	public void intantiateVises() {
		DataRow mainQuake = null;
		for (DataRow quake : masterData.getDatum())
			try {
				if (quake.getValue(DataRow.DATE).equals(
						new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
								.parse("20010126"))
						&& quake.getValue(DataRow.DEPENDENCY).equals(
								DataRow.Dependency.INDEPENDENT)) {
					mainQuake = quake;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		Individual detail = new DetailedInfo(x, y, w, h, mainQuake);
		Controller.registerVisualization(detail);
		vises.add(detail);

		Aggregate circles = new NestedCirclePlot(x, y, w, h, masterData, DataRow.TYPE);
		Controller.registerVisualization(circles);
		vises.add(circles);
		
		Individual aftershock = new AftershockMap(x, y, w, h, mainQuake,
				masterData);
		Controller.registerVisualization(aftershock);
		vises.add(aftershock);
		
		Multi bars = new DepthPlot(x, y, w, h, masterData);
		Controller.registerVisualization(bars);
		vises.add(bars);
		
	}

	@Override
	public void drawComponent(PApplet parent) {
		parent.noFill();
		parent.stroke(Theme.getBaseUIColor());
		parent.strokeWeight(2);
//		parent.rect(x, y, w, h);
	}

	@Override
	public void resizeTo(Rectangle bounds) {
		super.resizeTo(bounds);
		int index = 0;
		for (AbstractVisualization v : vises) {
			v.resizeTo(new Rectangle(bounds.x
					+ (index * bounds.width / vises.size()), bounds.y,
					bounds.width / vises.size(), bounds.height));
			index++;
		}
	}

}
