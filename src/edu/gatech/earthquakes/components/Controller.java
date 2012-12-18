package edu.gatech.earthquakes.components;

import java.awt.Rectangle;

import processing.core.PApplet;

import com.google.common.eventbus.EventBus;

import edu.gatech.earthquakes.interfaces.Brushable;
import edu.gatech.earthquakes.interfaces.Drawable;
import edu.gatech.earthquakes.interfaces.Filterable;
import edu.gatech.earthquakes.interfaces.Interactable;
import edu.gatech.earthquakes.model.DataSet;
import edu.gatech.earthquakes.model.DeadEventCanary;
import edu.gatech.earthquakes.model.Interaction;
import edu.gatech.earthquakes.vises.AbstractVisualization;
import edu.gatech.earthquakes.vises.Slider;
import edu.gatech.earthquakes.vises.Workspace;

public class Controller {

    private final PApplet parentApplet;

    private final Slider dataslider;

    private final Workspace workspace;

    private int lastWidth, lastHeight;

    private static final EventBus BRUSH_BUS = new EventBus("Brushing Bus");
    private static final EventBus DRAW_BUS = new EventBus("Drawing Bus");
    private static final EventBus FILTER_BUS = new EventBus("Filtering Bus");
    private static final EventBus INTERACT_BUS = new EventBus("Interacting Bus");

    public Controller(final PApplet parent) {
        this.parentApplet = parent;
        final DataSet masterData = Importer.importData();
        lastWidth = lastHeight = 0;

        setUpCanary();

        int[] test = new int[2012 - 495];
        for (int i = 0; i < test.length; i++) {
            test[i] = i + 495;
        }

        workspace = new Workspace(10, 10, parent.getWidth() - 20,
                parent.getHeight() - 120, masterData);
        registerVisualization(workspace);

        dataslider = new Slider(50, 768 - 100, 924, 50, masterData);
        dataslider.setUndecorated(true);
        dataslider.setDrawInterval(250);
        registerVisualization(dataslider);
    }

    private void setUpCanary() {
        final DeadEventCanary dec = DeadEventCanary.getInstance();
        BRUSH_BUS.register(dec);
        DRAW_BUS.register(dec);
        FILTER_BUS.register(dec);
        INTERACT_BUS.register(dec);
    }

    public static void registerVisualization(final AbstractVisualization av) {
        if (av instanceof Brushable) {
            BRUSH_BUS.register(av);
        }
        if (av instanceof Drawable) {
            DRAW_BUS.register(av);
        }
        if (av instanceof Filterable) {
            FILTER_BUS.register(av);
        }
        if (av instanceof Interactable) {
            INTERACT_BUS.register(av);
        }
    }

    /**
     * Called at each loop of the animation thread
     */
    public void refresh() {
        handleInput();
        redrawAll();
    }

    public void redrawAll() {
        if (parentApplet.width != lastWidth
                || parentApplet.height != lastHeight) {
            lastWidth = parentApplet.width;
            lastHeight = parentApplet.height;
            windowResized(lastWidth, lastHeight);
        }
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

        Interaction interact = new Interaction(firstPress, drag, released,
                parentApplet);
        INTERACT_BUS.post(interact);
    }

    private static DataSet lastFilterSet = null;

    public static void applyFilter(final DataSet curDataSet) {
        if (!curDataSet.equals(lastFilterSet)) {
            FILTER_BUS.post(curDataSet);
            lastFilterSet = curDataSet;
        }
    }

    private static DataSet lastBrushSet = null;

    public static void applyBrushing(final DataSet curDataSet) {
        if (!curDataSet.equals(lastBrushSet)) {
            BRUSH_BUS.post(curDataSet);
            lastBrushSet = curDataSet;
        }
    }

    public void windowResized(int width, int height) {
        dataslider.resizeTo(new Rectangle(50, height - 100, width - 100, 50));
        workspace.resizeTo(new Rectangle(10, 10, width - 10, height - 120));
    }

    public static void unregisterInputAndDraw(AbstractVisualization vis) {
        if (vis instanceof Drawable)
            Controller.DRAW_BUS.unregister(vis);
        if (vis instanceof Interactable)
            Controller.INTERACT_BUS.unregister(vis);
    }

    public static void reregisterInputAndDraw(AbstractVisualization vis) {
        if (vis instanceof Drawable)
            Controller.DRAW_BUS.register(vis);
        if (vis instanceof Interactable)
            Controller.INTERACT_BUS.register(vis);
    }
}
