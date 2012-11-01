package edu.gatech.earthquakes.components;

import java.util.List;

import com.google.common.collect.Lists;

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
	
	private static Controller controllerInstance;

	private final Slider dataslider;

	public Controller(PApplet parent) {
		this.parentApplet = parent;
//		this.MasterData = Importer.importData();
		this.MasterData = new DataSet(null);
		
		controllerInstance = this;
		
		brushableVises = Lists.newArrayList();
		drawableVises = Lists.newArrayList();
		filterableVises = Lists.newArrayList();
		interactableVises = Lists.newArrayList();
		
		dataslider = new Slider(50, 768-100-50, 924, 100, new int[]{1,2,3,4,5});
		registerVisualization(dataslider);
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
	
	public void redrawAll(){
		for(Drawable d : drawableVises){
			d.drawComponent(parentApplet);
		}
	}
	
	private boolean alreadyPressed;
	
	public void handleInput(){
		boolean firstPress = false;
		boolean drag = false;
		boolean released = false;
		if(parentApplet.mousePressed){
			if(!alreadyPressed){
				firstPress = true;
			} else {
				drag = true;
			}
			alreadyPressed = true;
		}
		else{
			if(alreadyPressed == true){
				released = true;
			}
			alreadyPressed = false;
		}
		
		for(Interactable i : interactableVises){
			i.handleInput(firstPress, drag, released, parentApplet);
		}
	}
	
	public static void applyFilter(DataSet ds){
		for(Filterable f : controllerInstance.getFilterableVises()){
			f.filterBy(ds);
		}
	}
	
	/**
	 * Called at each loop of the animation thread
	 */
	public void refresh(){
		handleInput();
		redrawAll();
	}
	
	public void releasedMouse(){
		
	}
	
	public List<Brushable> getBrushableVises() {
		return brushableVises;
	}

	public List<Filterable> getFilterableVises() {
		return filterableVises;
	}
}
