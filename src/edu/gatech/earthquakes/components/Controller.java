package edu.gatech.earthquakes.components;

import java.util.List;

import processing.core.PApplet;
import edu.gatech.earthquakes.interfaces.Brushable;
import edu.gatech.earthquakes.interfaces.Drawable;
import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.interfaces.Interactable;
import edu.gatech.earthquakes.model.DataSet;
import edu.gatech.earthquakes.vises.AbstractVisualization;
import edu.gatech.earthquakes.components.Importer;

public class Controller {

	private final PApplet parentApplet;

	private List<Brushable> brushableVises;
	private List<Drawable> drawableVises;
	private List<Filterable> filterableVises;
	private List<Interactable> interactableVises;
	private final DataSet MasterData;


	public Controller(PApplet parent) {
		this.parentApplet = parent;
		this.MasterData = Importer.importData();
	}

	public void registerVisualization(AbstractVisualization av){
		if(av instanceof Brushable)
			brushableVises.add((Brushable)av);
		if(av instanceof Drawable)
			drawableVises.add((Drawable)av);
		if(av instanceof Filterable)
			filterableVises.add((Filterable)av);
		if(av instanceof Interactable)
			interactableVises.add((Interactable)av);
	}
}
