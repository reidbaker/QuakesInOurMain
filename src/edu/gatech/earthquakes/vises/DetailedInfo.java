package edu.gatech.earthquakes.vises;

import java.awt.Rectangle;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Locale.Category;

import processing.core.PApplet;

import com.google.common.eventbus.Subscribe;

import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.interfaces.Brushable;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;
import edu.gatech.earthquakes.web.CustomSearch;

public class DetailedInfo extends Individual implements Brushable {
    // Display Strings
    // private static final String NUMBER_OF_RESULTS = "Number of Results";
    private static final String TITLE = "Top result title";

    // Calculated Data
    private volatile int numResults;
    private volatile String title;

    // To keep from spamming custom search
    private volatile boolean searching;

    // Formatting
    private int xPadding;
    private int yPadding;
    private int textSize;

    public DetailedInfo(int x, int y, int w, int h, DataRow displayData) {
	super(x, y, w, h, displayData, "Detailed Information");
	setFormatting(w, h);
	recalculateNumResults();
	searching = false;
    }

    private void setFormatting(int width, int height) {
	xPadding = width / 50;
	yPadding = width / 50;
	textSize = Math.min(width / 20, height / 20);
    }

    private void recalculateNumResults() {
	if (!searching) {
	    new Thread(new Runnable() {

		@Override
		public void run() {
		    try {
			numResults = CustomSearch.getTotalCount(CustomSearch
			        .getInstance().getQuery(getWebQuery()));
			title = CustomSearch.getTitles(0, CustomSearch
			        .getInstance().getQuery(getWebQuery()));
			searching = false;
		    } catch (UnknownHostException uhe) {
			numResults = -1;
			title = "";
			System.err.println("Unknown Host: " + uhe.getMessage());
		    } catch (NoSuchAlgorithmException | IOException e) {
			numResults = -1;
			title = "";
			e.printStackTrace();
		    } catch (Exception e) {
			numResults = -1;
			title = "";
		    } finally {
			searching = false;
		    }
		}
	    }).run(); // Should be threaded in the end, but having I/O issues
		      // right now.
	}
    }

    public void drawComponent(PApplet parent) {
	super.drawComponent(parent);
	PApplet p = parent;
	p.stroke(Theme.getBaseUIColor());
	p.strokeWeight(2);

	p.textAlign(PApplet.LEFT);
	p.textSize(textSize);
	String displayOutput = getDisplayString();
	// magical 2's are to keep text from drawing outside the box
	p.text(displayOutput, x + xPadding, y + yPadding, x + w - 2 * xPadding,
	        y + h - 2 * yPadding - textSize);
    }

    private String getDisplayString() {
	StringBuilder sb = new StringBuilder();

	sb.append(DataRow.MOMENT_MAGNITUDE);
	sb.append(": ");
	sb.append(displayData.getValue(DataRow.MOMENT_MAGNITUDE));
	sb.append('\n');

	sb.append(DataRow.LATTITUDE);
	sb.append(": ");
	sb.append(displayData.getValue(DataRow.LATTITUDE));
	sb.append('\n');

	sb.append(DataRow.LONGITUDE);
	sb.append(": ");
	sb.append(displayData.getValue(DataRow.LONGITUDE));
	sb.append('\n');

	sb.append(DataRow.CONTINENT);
	sb.append(": ");
	sb.append(displayData.getValue(DataRow.CONTINENT));
	sb.append('\n');

	sb.append(DataRow.DEPTH);
	sb.append(": ");
	sb.append(displayData.getValue(DataRow.DEPTH));
	sb.append("\n");

	sb.append(DataRow.DATE);
	sb.append(": ");

	Calendar cal = Calendar.getInstance();
	Locale loc = Locale.getDefault(Category.DISPLAY);
	cal.setTime((Date) displayData.getValue(DataRow.DATE));
	String displayMonth = cal.getDisplayName(Calendar.MONTH,
	        Calendar.SHORT, loc);
	sb.append(displayMonth);
	sb.append(" ");
	sb.append(cal.get(Calendar.DAY_OF_MONTH));
	sb.append(", ");
	sb.append(cal.get(Calendar.YEAR));
	sb.append("\n");

	if (numResults >= 0) {
	    sb.append("Google Search Results for Earthquake: ");
	    sb.append(numResults);
	    sb.append('\n');
	}
	if (title.length() > 0) {
	    sb.append(TITLE);
	    sb.append(": ");
	    sb.append(title);
	    sb.append('\n');
	}

	return sb.toString();
    }

    private String getWebQuery() {
	StringBuilder sb = new StringBuilder();
	Date d = (Date) displayData.getValue(DataRow.DATE);
	Calendar cal = Calendar.getInstance();
	cal.setTime(d);
	sb.append(new SimpleDateFormat("MMM").format(d));
	sb.append(" ");
	sb.append(cal.get(Calendar.DAY_OF_MONTH));
	sb.append(" ");
	sb.append(cal.get(Calendar.YEAR));
	sb.append(" ");
	sb.append(displayData.getValue(DataRow.CONTINENT));
	sb.append(" ");
	sb.append("Earthquake");
	return sb.toString();
    }

    @Override
    @Subscribe
    public void brushData(DataSet ds) {
	if (!ds.getDatum().isEmpty() && ds.getDatum().size() == 1) {
	    // do my things
	    displayData = ds.getDatum().iterator().next();
	    recalculateNumResults();
	}
    }

    @Override
    public void resizeTo(Rectangle bounds) {
	super.resizeTo(bounds);
	setFormatting(bounds.width, bounds.height);
    }

}
