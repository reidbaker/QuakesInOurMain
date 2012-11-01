package edu.gatech.earthquakes.components;

import processing.core.PApplet;
import edu.gatech.earthquakes.interfaces.Interactable;
import edu.gatech.earthquakes.vises.AbstractVisualization;

public class Slider extends AbstractVisualization implements Interactable{
	float left, right;
	int goalLeft, goalRight;
	int snappedLeft, snappedRight;
	int rangeMin, rangeMax;

	int drawInterval;

	int[] values;
	
	boolean moveLeft, moveRight, moveAll;

	public static final int OUTSIDE = 0, INSIDE = 1, LEFTHANDLE = 2,
			RIGHTHANDLE = 3;

	public Slider(int x, int y, int w, int h, int[] values) {
		super(x, y, w, h);
		this.left = x;
		this.right = w + x;
		goalLeft = (int) (left + 0.5f);
		goalRight = (int) (right + 0.5f);
		snappedLeft = goalLeft;
		snappedRight = goalRight;
		drawInterval = 1;
		this.values = values;
		rangeMin = values[0];
		rangeMax = values[values.length - 1];
		
		moveLeft = moveRight = moveAll = false;
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
		} else if (goalLeft > goalRight - w / values.length) {
			goalLeft = goalRight - w / values.length;
		}
	}

	public void dragRH(int nx, int px) {
		goalRight += nx - px;
		if (goalRight > x + w) {
			goalRight -= (goalRight - (x + w));
		} else if (goalLeft > goalRight - w / values.length) {
			goalRight = goalLeft + w / values.length;
		}
	}

	public void snapGoals() {
		int leftX = goalLeft - x;
		float ratioL = leftX / (float) w;
		int index = (int) (ratioL * values.length + 0.5);
		snappedLeft = x + w * index / values.length;
		if (index == 0)
			snappedLeft = x;
		rangeMin = values[index];

		int rightX = goalRight - x;
		float ratioR = rightX / (float) w;
		index = (int) (ratioR * values.length + 0.5);
		if (index == values.length)
			snappedRight = x + w;
		snappedRight = x + w * index / values.length;
		if (index != 0)
			rangeMax = values[index - 1];
	}

	public int getLeftBound() {
		int leftX = (int) (left + 0.5) - x;
		float ratioL = leftX / (float) w;
		int index = (int) (ratioL * values.length + 0.5);
		return values[index];
	}

	public int getRightBound() {
		int rightX = (int) (right + 0.5) - x;
		float ratioR = rightX / (float) w;
		int index = (int) (ratioR * values.length + 0.5);
		return values[index - 1];
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
		for (int i = 0; i < values.length; i++) {
			int xpos = x + (i) * w / (values.length) + w / (2 * values.length);
			if (values[i] % drawInterval == 0 || i == 0
					|| i == values.length - 1) {
				p.textAlign(PApplet.CENTER);
				p.text(values[i], xpos, y + h + 24);
			}

			// Draw ruler ticks
			if (values[i] % 100 == 0) {
				p.line(xpos, y + h, xpos, y + h - 15);
			} else if (values[i] % 10 == 0) {
				p.line(xpos, y + h, xpos, y + h - 10);
			} else {
				p.line(xpos, y + h, xpos, y + h - 5);
			}
		}

		// Draw mini graph

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
	public void handleInput(boolean pressed, boolean dragged, boolean released,
			PApplet parent) {
		if (pressed) {
			int location = whereIs(parent.mouseX, parent.mouseY);
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
		} else if (dragged) {
			if(moveLeft){
				dragLH(parent.mouseX, parent.pmouseX);
				snapGoals();
			}
			if(moveRight){
				dragRH(parent.mouseX, parent.pmouseX);
				snapGoals();
			}
			if(moveAll){
				dragAll(parent.mouseX, parent.pmouseX);
				snapGoals();
			}
		} else if(released){
			updateGoals();
			moveLeft = moveRight = moveAll = false;
		}
	}

}
