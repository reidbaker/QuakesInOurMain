package edu.gatech.earthquakes.interfaces;

import com.google.common.eventbus.Subscribe;

import processing.core.PApplet;

public interface Drawable {

    @Subscribe
    public void drawComponent(PApplet parent);

}
