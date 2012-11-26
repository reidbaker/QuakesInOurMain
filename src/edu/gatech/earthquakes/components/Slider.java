package edu.gatech.earthquakes.components;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import processing.core.PApplet;

import com.google.common.collect.Sets;

import edu.gatech.earthquakes.interfaces.Interactable;
import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;
import edu.gatech.earthquakes.model.Interaction;
import edu.gatech.earthquakes.vises.AbstractVisualization;

public class Slider extends AbstractVisualization implements Interactable{
	float left, right;
	int goalLeft, goalRight;
	int snappedLeft, snappedRight;
	int rangeMin, rangeMax;

	int drawInterval;

	DataSet data;
	int[] years;
	int[] fullYears;
	
	boolean moveLeft, moveRight, moveAll;

	public static final int OUTSIDE = 0, INSIDE = 1, LEFTHANDLE = 2,
			RIGHTHANDLE = 3;

	public Slider(int x, int y, int w, int h, DataSet data) {
		super(x, y, w, h);
		this.left = x;
		this.right = w + x;
		goalLeft = (int) (left + 0.5f);
		goalRight = (int) (right + 0.5f);
		snappedLeft = goalLeft;
		snappedRight = goalRight;
		drawInterval = 100;
		this.data = data;
		
		grabDates();

		rangeMin = years[0];
		rangeMax = years[years.length - 1];
		fullYears = new int[rangeMax - rangeMin];
		for(int i = 0; i < fullYears.length; i++){
			fullYears[i] = i + years[0];
		}

		moveLeft = moveRight = moveAll = false;
	}

	public void grabDates() {
		Set<Date> dates = Sets.newTreeSet();
		for (DataRow dr : data) {
			dates.add((Date) dr.getVariables().get(DataRow.DATE));
		}
		Date[] dateArray = dates.toArray(new Date[] {});
		years = new int[dateArray.length];
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < years.length; i++) {
			cal.setTime(dateArray[i]);
			years[i] = cal.get(Calendar.YEAR);
		}
	}

	public void changeWidthTo(int newWidth) {
		// Updates the slider width to a new value;
		float ratioR = (right - x) / (float) w, ratioL = (left - x) / (float) w;
		float ratioGR = (goalRight - x) / (float) w, ratioGL = (goalLeft - x)
				/ (float) w;
		w = newWidth;
		right = x + (int) (ratioR * w + 0.5);
		left = x + (int) (ratioL * w + 0.5);
		goalRight = x + (int) (ratioGR * w + 0.5);
		goalLeft = x + (int) (ratioGL * w + 0.5);
		snapGoals();
	}

	public void setDrawInterval(int drawInterval) {
		this.drawInterval = drawInterval;
	}

	public int whereIs(int x, int y) {
		int ret = OUTSIDE;
		if (x >= left && x <= right && y > this.y && y < this.y + h) {
			ret = INSIDE;
		} else if (x > left - 10 && x < left && y > this.y && y < this.y + h) {
			ret = LEFTHANDLE;
		} else if (x > right && x < right + 10 && y > this.y && y < this.y + h) {
			ret = RIGHTHANDLE;
		}
		return ret;
	}

	public void dragAll(int nx, int px) {
		goalLeft += nx - px;
		goalRight += nx - px;
		if (goalLeft < x) {
			goalRight += x - goalLeft;
			goalLeft += x - goalLeft;
		}
		if (goalRight > x + w) {
			goalLeft -= (goalRight - (x + w));
			goalRight -= (goalRight - (x + w));
		}
	}

	public void dragLH(int nx, int px) {
		goalLeft += nx - px;
		if (goalLeft < x) {
			goalLeft += x - goalLeft;
		} else if (goalLeft > goalRight - w / fullYears.length) {
			goalLeft = goalRight - w / fullYears.length;
		}
	}

	public void dragRH(int nx, int px) {
		goalRight += nx - px;
		if (goalRight > x + w) {
			goalRight -= (goalRight - (x + w));
		} else if (goalLeft > goalRight - w / fullYears.length) {
			goalRight = goalLeft + w / fullYears.length;
		}
	}

	public void snapGoals() {
		int leftX = goalLeft - x;
		float ratioL = leftX / (float) w;
		int index = (int) (ratioL * fullYears.length + 0.5);
		snappedLeft = x + w * index / fullYears.length;
		if (index == 0)
			snappedLeft = x;
		rangeMin = fullYears[index];

		int rightX = goalRight - x;
		float ratioR = rightX / (float) w;
		index = (int) (ratioR * fullYears.length + 0.5);
		if (index == fullYears.length)
			snappedRight = x + w;
		snappedRight = x + w * index / fullYears.length;
		if (index != 0)
			rangeMax = fullYears[index - 1];
	}

	public int getLeftBound() {
		int leftX = (int) (left + 0.5) - x;
		float ratioL = leftX / (float) w;
		int index = (int) (ratioL * fullYears.length + 0.5);
		return fullYears[index];
	}

	public int getRightBound() {
		int rightX = (int) (right + 0.5) - x;
		float ratioR = rightX / (float) w;
		int index = (int) (ratioR * fullYears.length + 0.5);
		return fullYears[index - 1];
	}

	public void updateGoals() {
		goalLeft = snappedLeft;
		goalRight = snappedRight;
	}

	public void updateAnim(int slowness) {
		if (Math.abs(snappedLeft - left) > 0) {
			left += (snappedLeft - left) / slowness;
			if (Math.abs(snappedLeft - left) == 1) {
				left = snappedLeft;
			}
		}
		if (Math.abs(snappedRight - right) > 0) {
			right += (snappedRight - right) / slowness;
			if (Math.abs(snappedRight - right) == 1) {
				right = snappedRight;
			}
		}
	}

	@Override
	public void drawComponent(PApplet parent) {
		PApplet p = parent;
		p.stroke(Theme.getBaseUIColor());
		p.strokeWeight(2);
		p.noFill();
		p.strokeJoin(PApplet.ROUND);
		p.beginShape();
		p.vertex(x, y + h);
		p.vertex(x, y);
		p.vertex(x + w, y);
		p.vertex(x + w, y + h);
		p.endShape();

		// Draw underlying data
		p.fill(Theme.getDarkUIColor());
		p.strokeWeight(2);
		p.stroke(Theme.getBaseUIColor());
		p.line(x, y + h, x + w, y + h);
		for (int i = 0; i < fullYears.length; i++) {
			int xpos = x + (i) * w / (fullYears.length) + w / (2 * fullYears.length);
			if (fullYears[i] % drawInterval == 0 || i == 0
					|| i == fullYears.length - 1) {
				p.textAlign(PApplet.CENTER);
				p.text(fullYears[i], xpos, y + h + 24);
			}

			// Draw ruler ticks
			if (fullYears[i] % 100 == 0) {
				p.line(xpos, y + h, xpos, y + h - 15);
			} else if (fullYears[i] % 10 == 0) {
				p.line(xpos, y + h, xpos, y + h - 10);
			} else {
//				p.line(xpos, y + h, xpos, y + h - 5);
			}
		}

		// Draw mini graph
		for(DataRow dr : data){
			
		}

		// Draw main bar
		p.fill(0, 0, 0, 0);
		for (int i = 0; i < h; i++) {
			p.stroke(rgba(Theme.getBaseUIColor(), i * 127 / h));
			p.line(left, y + i, right, y + i);
		}
		p.stroke(Theme.getBaseUIColor());
		p.rect(left, y, right - left, h);

		// Draw left handle
		p.stroke(0, 0, 0, 0);
		if(whereIs(p.mouseX, p.mouseY) == LEFTHANDLE){
			p.fill(rgba(Theme.getBrightUIColor(), 127));
		} else {
			p.fill(rgba(Theme.getBaseUIColor(), 127));
		}
		p.arc(left, y + 10, 20, 20, PApplet.PI, 3 * PApplet.PI / 2);
		p.arc(left, y + h - 10, 20, 20, PApplet.PI / 2, PApplet.PI);
		p.rect(left + 0.5f - 10, y + 10, 10, h - 20);

		p.fill(Theme.getDarkUIColor());
		p.ellipse(left - 5, y + (h / 2) - 5, 4, 4);
		p.ellipse(left - 5, y + (h / 2), 4, 4);
		p.ellipse(left - 5, y + (h / 2) + 5, 4, 4);

		// Draw right handle
		p.stroke(0, 0, 0, 0);
		if(whereIs(p.mouseX, p.mouseY) == RIGHTHANDLE){
			p.fill(rgba(Theme.getBrightUIColor(), 127));
		} else {
			p.fill(rgba(Theme.getBaseUIColor(), 127));
		}
		p.arc(right, y + 10, 20, 20, 3 * PApplet.PI / 2, 2 * PApplet.PI);
		p.arc(right, y + h - 10, 20, 20, 0, PApplet.PI / 2);
		p.rect(right + 0.5f, y + 10, 10, h - 20);

		p.fill(Theme.getDarkUIColor());
		p.ellipse(right + 5, y + (h / 2) - 5, 4, 4);
		p.ellipse(right + 5, y + (h / 2), 4, 4);
		p.ellipse(right + 5, y + (h / 2) + 5, 4, 4);
		
		updateAnim(4);
	}

	private int rgba(int rgb, int a){
		return rgb & ((a << 24) | 0xFFFFFF);
	}

	@Override
	public void handleInput(Interaction interaction) {
		if (interaction.isFirstPress()) {
			int location = whereIs(interaction.getParentApplet().mouseX, interaction.getParentApplet().mouseY);
			switch (location) {
			case LEFTHANDLE:
				moveLeft = true;
				break;
			case RIGHTHANDLE:
				moveRight = true;
				break;
			case INSIDE:
				moveAll = true;
				break;
			}
		} else if (interaction.isDragged()) {
			if(moveLeft){
				dragLH(interaction.getParentApplet().mouseX, interaction.getParentApplet().pmouseX);
				snapGoals();
			}
			if(moveRight){
				dragRH(interaction.getParentApplet().mouseX, interaction.getParentApplet().pmouseX);
				snapGoals();
			}
			if(moveAll){
				dragAll(interaction.getParentApplet().mouseX, interaction.getParentApplet().pmouseX);
				snapGoals();
			}
		} else if(interaction.isReleased()){
			updateGoals();
			moveLeft = moveRight = moveAll = false;
		}
	}

}
