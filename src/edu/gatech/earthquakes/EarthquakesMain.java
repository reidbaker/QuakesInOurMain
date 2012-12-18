package edu.gatech.earthquakes;

import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.JFrame;

import processing.core.PApplet;
import processing.core.PFont;
import edu.gatech.earthquakes.components.Controller;
import edu.gatech.earthquakes.components.Theme;

public class EarthquakesMain extends PApplet {

    private static final long serialVersionUID = 1L;
    private Controller cont;

    public static void main(String[] args) {
        PApplet.main(new String[] { "edu.gatech.earthquakes.EarthquakesMain" });
    }

    public void setup() {
        smooth();

        PFont font = null;
        String fileName = ".." + File.separator + "data" + File.separator
                + "fonts" + File.separator + "Quicksand-Regular.ttf";
        createInput(fileName);
        font = createFont(fileName, 24);

        textFont(font);
        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        JFrame sample = new JFrame();
        sample.pack();
        Insets insets = sample.getInsets();
        int wwidth = (bounds.width - insets.left - insets.right);
        int wheight = (bounds.height - insets.top - insets.bottom);
        sample.dispose();
        size(wwidth, wheight - 48); // Offset applet bottom and top
        cont = new Controller(this);
    }

    public void draw() {
        background(Theme.getBackgroundColor());
        cont.refresh();
    }
}
