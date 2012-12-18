package edu.gatech.earthquakes.model;

import processing.core.PApplet;

public class Interaction {

    final private boolean firstPress, dragged, released;
    final private PApplet parentApplet;

    public Interaction(final boolean firstPress, final boolean dragged,
            final boolean released, final PApplet parentApplet) {
        super();
        this.firstPress = firstPress;
        this.dragged = dragged;
        this.released = released;
        this.parentApplet = parentApplet;
    }

    public boolean isFirstPress() {
        return firstPress;
    }

    public boolean isDragged() {
        return dragged;
    }

    public boolean isReleased() {
        return released;
    }

    public PApplet getParentApplet() {
        return parentApplet;
    }

}
