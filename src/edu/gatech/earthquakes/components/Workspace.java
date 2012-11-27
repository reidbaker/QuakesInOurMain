package edu.gatech.earthquakes.components;

import java.awt.Rectangle;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import processing.core.PApplet;

import com.google.common.collect.Lists;

import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;
import edu.gatech.earthquakes.vises.AbstractVisualization;
import edu.gatech.earthquakes.vises.AftershockMap;
import edu.gatech.earthquakes.vises.DetailedInfo;
import edu.gatech.earthquakes.vises.Individual;

public class Workspace extends AbstractVisualization {

	private DataSet masterData;
	List<AbstractVisualization> vises;

	public Workspace(int x, int y, int w, int h, DataSet masterData) {
		super(x, y, w, h);
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

		Individual detail = new DetailedInfo(20, 20, 500, 500, mainQuake);
		Controller.registerVisualization(detail);
		vises.add(detail);

		Individual aftershock = new AftershockMap(525, 20, 450, 500, mainQuake,
				masterData);
		Controller.registerVisualization(aftershock);
		vises.add(aftershock);
	}

	@Override
	public void drawComponent(PApplet parent) {
		parent.noFill();
		parent.stroke(Theme.getBaseUIColor());
		parent.strokeWeight(2);
		parent.rect(x, y, w, h);
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
