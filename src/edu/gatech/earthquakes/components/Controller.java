package edu.gatech.earthquakes.components;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import processing.core.PApplet;

import com.google.common.eventbus.EventBus;

import edu.gatech.earthquakes.interfaces.Brushable;
import edu.gatech.earthquakes.interfaces.Drawable;
import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.interfaces.Interactable;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;
import edu.gatech.earthquakes.model.Interaction;
import edu.gatech.earthquakes.vises.AbstractVisualization;
import edu.gatech.earthquakes.vises.AftershockMap;
import edu.gatech.earthquakes.vises.DetailedInfo;
import edu.gatech.earthquakes.vises.Individual;
import edu.gatech.earthquakes.vises.NominalBarGraph;

public class Controller {

	private final PApplet parentApplet;

	private final DataSet masterData;

	private final Slider dataslider;
	
	public static final EventBus BRUSH_BUS = new EventBus("Brushing Bus");
	public static final EventBus DRAW_BUS = new EventBus("Drawing Bus");
	public static final EventBus FILTER_BUS = new EventBus("Filtering Bus");
	public static final EventBus INTERACT_BUS = new EventBus("Interacting Bus");

	public Controller(PApplet parent) {
		this.parentApplet = parent;
		this.masterData = Importer.importData();

		int[] test = new int[2012-495];
		for(int i = 0; i < test.length; i++){
			test[i] = i+495;
		}
		
		Workspace workspace = new Workspace(10, 10, parent.getWidth() - 20, parent.getHeight() - 120);
		registerVisualization(workspace);
		
		dataslider = new Slider(50, 768 - 100, 924, 50, masterData);
		dataslider.setDrawInterval(250);
		registerVisualization(dataslider);
		
		NominalBarGraph n = new NominalBarGraph(20, 20, 500, 500,Importer.importData(), DataRow.CONTINENT);
		registerVisualization(n);
		
		//Elizabeth's testing things
		DataRow mainQuake = null;
//		boolean found = false;
		for(DataRow quake: masterData.getDatum())
			try {
				if(quake.getValue(DataRow.DATE).equals(new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse("20010126")) && quake.getValue(DataRow.DEPENDENCY).equals(DataRow.Dependency.INDEPENDENT)){
					mainQuake = quake;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		Individual m = new DetailedInfo(525, 20, 450, 500, mainQuake);
		registerVisualization(m);
	}

	public void registerVisualization(AbstractVisualization av) {
		if (av instanceof Brushable)
			BRUSH_BUS.register(av);
		if (av instanceof Drawable)
			DRAW_BUS.register(av);
		if (av instanceof Filterable)
			FILTER_BUS.register(av);
		if (av instanceof Interactable)
			INTERACT_BUS.register(av);
	}

	/**
	 * Called at each loop of the animation thread
	 */
	public void refresh() {
		handleInput();
		redrawAll();
	}

	public void redrawAll() {
		DRAW_BUS.post(parentApplet);
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

		Interaction i = new Interaction(firstPress, drag, released, parentApplet);
		INTERACT_BUS.post(i);
	}

	public static void applyFilter(DataSet ds) {
		FILTER_BUS.post(ds);
	}

	public static void applyBrushing(DataSet ds) {
		BRUSH_BUS.post(ds);
	}
}


