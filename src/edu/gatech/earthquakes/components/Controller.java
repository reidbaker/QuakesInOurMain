package edu.gatech.earthquakes.components;

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
import edu.gatech.earthquakes.vises.NominalBarGraph;

public class Controller {

	private final PApplet parentApplet;

	private final DataSet masterData;

	private final Slider dataslider;
	
	public static final EventBus brushBus = new EventBus("Brushing Bus");
	public static final EventBus drawBus = new EventBus("Drawing Bus");
	public static final EventBus filterBus = new EventBus("Filtering Bus");
	public static final EventBus interactBus = new EventBus("Interacting Bus");

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
		registerVisualization(dataslider);
		
		NominalBarGraph n = new NominalBarGraph(20, 20, 500, 500,Importer.importData(), DataRow.CONTINENT);
		registerVisualization(n);
	}

	public void registerVisualization(AbstractVisualization av) {
		if (av instanceof Brushable)
			brushBus.register(av);
		if (av instanceof Drawable)
			drawBus.register(av);
		if (av instanceof Filterable)
			filterBus.register(av);
		if (av instanceof Interactable)
			interactBus.register(av);
	}

	/**
	 * Called at each loop of the animation thread
	 */
	public void refresh() {
		handleInput();
		redrawAll();
	}

	public void redrawAll() {
		drawBus.post(parentApplet);
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
		interactBus.post(i);
	}

	public static void applyFilter(DataSet ds) {
		filterBus.post(ds);
	}

	public static void applyBrushing(DataSet ds) {
		brushBus.post(ds);
	}
}
