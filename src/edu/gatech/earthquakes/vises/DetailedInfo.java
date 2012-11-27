package edu.gatech.earthquakes.vises;

import java.awt.Rectangle;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.common.eventbus.Subscribe;

import processing.core.PApplet;
import edu.gatech.earthquakes.components.Theme;
import edu.gatech.earthquakes.interfaces.Brushable;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;
import edu.gatech.earthquakes.web.CustomSearch;

public class DetailedInfo extends Individual implements Brushable {
    //Display Strings
    private static final String NUMBER_OF_RESULTS = "Number of Results";

    //Calculated Data
    private int numRestults;

    //Formatting
    private int xPadding;
    private int yPadding;
    private int textSize;

    public DetailedInfo(int x, int y, int w, int h, DataRow displayData) {
        super(x, y, w, h, displayData);
        setFormatting(w);
        try {
            numRestults = recalculateNumResults();
        } catch (NoSuchAlgorithmException | IOException e) {
            numRestults = 0;
            e.printStackTrace();
        }
    }

    private void setFormatting(int w) {
        xPadding = w/50;
        yPadding = w/50;
        textSize = w / 15;
    }

    private int recalculateNumResults() throws NoSuchAlgorithmException,
            MalformedURLException, IOException {
        return CustomSearch.getTotalCount(CustomSearch.getInstance().getQuery(getWebQuery()));
    }

    public void drawComponent(PApplet parent){
        super.drawComponent(parent);
        PApplet p = parent;
        p.stroke(Theme.getBaseUIColor());
        p.strokeWeight(2);

        p.textAlign(PApplet.LEFT);
        p.textSize(textSize);
        String displayOutput = getDisplayString();
        p.text(displayOutput, x + xPadding, y + yPadding, x + w, y + h);
    }

    private String getDisplayString() {
        StringBuilder sb = new StringBuilder();

        sb.append(NUMBER_OF_RESULTS);
        sb.append(": ");
        sb.append(numRestults);
        sb.append('\n');

        sb.append(DataRow.MOMENT_MAGNITUDE);
        sb.append(": ");
        sb.append(displayData.getValue(displayData.MOMENT_MAGNITUDE));
        sb.append('\n');

        sb.append(DataRow.LATTITUDE);
        sb.append(": ");
        sb.append(displayData.getValue(displayData.LATTITUDE));
        sb.append('\n');

        sb.append(DataRow.LONGITUDE);
        sb.append(": ");
        sb.append(displayData.getValue(displayData.LONGITUDE));
        sb.append('\n');

        sb.append(DataRow.CONTINENT);
        sb.append(": ");
        sb.append(displayData.getValue(displayData.CONTINENT));
        sb.append('\n');

        sb.append(DataRow.DEPTH);
        sb.append(": ");
        sb.append(displayData.getValue(displayData.DEPTH));
        sb.append('\n');

        return sb.toString();
    }

    private String getWebQuery(){
        StringBuilder sb = new StringBuilder();
        Date d = (Date)displayData.getValue(displayData.DATE);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        sb.append(new SimpleDateFormat("MMM").format(d));
        sb.append(" ");
        sb.append(cal.get(Calendar.DAY_OF_MONTH));
        sb.append(" ");
        sb.append(cal.get(Calendar.YEAR));
        sb.append(" ");
        sb.append(displayData.getValue(displayData.CONTINENT));
        sb.append(" ");
        sb.append("Earthquake");
        return sb.toString();
    }

    @Override
    @Subscribe
    public void brushData(DataSet ds) {
        if(!ds.getDatum().isEmpty() && ds.getDatum().size() == 1){
            //do my things
            displayData = ds.getDatum().iterator().next();
            try {
                numRestults = recalculateNumResults();
            } catch (NoSuchAlgorithmException | IOException e) {
                numRestults = 0;
                e.printStackTrace();
            }
        }
    }

    @Override
    public void resizeTo(Rectangle bounds) {
        super.resizeTo(bounds);
        setFormatting(bounds.width);
    }

}
