package edu.gatech.earthquakes.components;

import java.util.List;

import processing.core.PApplet;
import edu.gatech.earthquakes.interfaces.Brushable;
import edu.gatech.earthquakes.interfaces.Drawable;
import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.interfaces.Interactable;
import edu.gatech.earthquakes.vises.AbstractVisualization;

public class Controller {

	private final PApplet parentApplet;

	private List<Brushable> brushableVises;
	private List<Drawable> drawableVises;
	private List<Filterable> filterableVises;
	private List<Interactable> interactableVises;

	public Controller(PApplet parent) {
		this.parentApplet = parent;
	}

	public void registerVisualization(AbstractVisualization av){
		if(av instanceof Brushable)
			brushableVises.add(av);
		if(av instanceof Drawable)
			drawableVises.add(av);
		if(av instanceof Filterable)
			filterableVises.add(av);
		if(av instanceof Interactable)
			interactableVises.add(av);
	}
}
