package edu.gatech.earthquakes.components;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;

import processing.core.PApplet;
import edu.gatech.earthquakes.interfaces.Brushable;
import edu.gatech.earthquakes.interfaces.Drawable;
import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.interfaces.Interactable;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;
import edu.gatech.earthquakes.vises.AbstractVisualization;
import edu.gatech.earthquakes.vises.AftershockMap;
import edu.gatech.earthquakes.vises.NominalBarGraph;
import edu.gatech.earthquakes.components.Importer;

public class Controller {

	private final PApplet parentApplet;

	private List<Brushable> brushableVises;
	private List<Drawable> drawableVises;
	private List<Filterable> filterableVises;
	private List<Interactable> interactableVises;

	private final DataSet masterData;

	private static Controller controllerInstance;

	private final Slider dataslider;

	public Controller(PApplet parent) {
		this.parentApplet = parent;
		this.masterData = Importer.importData();
		controllerInstance = this;

		brushableVises = Lists.newArrayList();
		drawableVises = Lists.newArrayList();
		filterableVises = Lists.newArrayList();
		interactableVises = Lists.newArrayList();

		int[] test = new int[2012-495];
		for(int i = 0; i < test.length; i++){
			test[i] = i+495;
		}
		
		Workspace workspace = new Workspace(10, 10, parent.getWidth() - 20, parent.getHeight() - 120);
		registerVisualization(workspace);
		
		dataslider = new Slider(50, 768 - 100, 924, 50, masterData);
		dataslider.setDrawInterval(250);
		registerVisualization(dataslider);
		
		//NominalBarGraph n = new NominalBarGraph(20, 20, 500, 500,Importer.importData(), DataRow.CONTINENT);
		//registerVisualization(n);
		
		//Elizabeth's testing things
		DataRow mainQuake = null;
		boolean found = false;
		for(DataRow quake: masterData.getDatum())
			try {
				if(quake.getValue(DataRow.DATE).equals(new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse("20010126")) && quake.getValue(DataRow.DEPENDENCY).equals(DataRow.Dependency.INDEPENDENT)){
					mainQuake = quake;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		AftershockMap m = new AftershockMap(20, 20, 500, 500, AftershockMap.findAftershocks(mainQuake, masterData));
		registerVisualization(m);
		//System.out.println((AftershockMap.findAftershocks(mainQuake, masterData)));
	}

	public void registerVisualization(AbstractVisualization av) {
		if (av instanceof Brushable)
			brushableVises.add((Brushable) av);
		if (av instanceof Drawable)
			drawableVises.add((Drawable) av);
		if (av instanceof Filterable)
			filterableVises.add((Filterable) av);
		if (av instanceof Interactable)
			interactableVises.add((Interactable) av);
	}

	/**
	 * Called at each loop of the animation thread
	 */
	public void refresh() {
		handleInput();
		redrawAll();
	}

	public void redrawAll() {
		for (Drawable d : drawableVises) {
			d.drawComponent(parentApplet);
		}
	}

	private boolean alreadyPressed;

	public void handleInput() {
		boolean firstPress = false;
		boolean drag = false;
		boolean released = false;
		if (parentApplet.mousePressed) {
			if (!alreadyPressed) {
				firstPress = true;
			} else {
				drag = true;
			}
			alreadyPressed = true;
		} else {
			if (alreadyPressed == true) {
				released = true;
			}
			alreadyPressed = false;
		}

		for (Interactable i : interactableVises) {
			i.handleInput(firstPress, drag, released, parentApplet);
		}
	}

	public static void applyFilter(DataSet ds) {
		for (Filterable f : controllerInstance.getFilterableVises()) {
			f.filterBy(ds);
		}
	}

	public static void applyBrushing(DataSet ds) {
		for (Brushable b : controllerInstance.getBrushableVises()) {
			b.brushData(ds);
		}
	}

	public List<Brushable> getBrushableVises() {
		return brushableVises;
	}

	public List<Filterable> getFilterableVises() {
		return filterableVises;
	}
}


