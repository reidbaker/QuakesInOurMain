package edu.gatech.earthquakes.components;

import java.awt.Color;

public class Theme {

	private static final int baseUIColor = 0xFF00678B;
//	private static final int baseDataColor = 0xFF4499bb;

	private static final int baseDataColor = getRGB(new float[]{0,1,1});

	/**
	 * The change applied to values for highlight and darkened compliments, as
	 * well as pallette generation.
	 */
	private static final float deltaV = 0.3f;

	private static final int numHues = 7, numBrightSat = 4;

	private static float[] getHSB(int rgbColor) {
		float[] hsb = new float[3];
		Color rgb = new Color(rgbColor);
		Color.RGBtoHSB(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), hsb);
		return hsb;
	}

	private static int getRGB(float[] hsb) {
		Color converted = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
		return converted.getRGB();
	}

	public static int getBaseUIColor() {
		return getRGB(getHSB(baseUIColor));
	}

	public static int getDarkUIColor() {
		float[] hsb = getHSB(baseUIColor);
		hsb[2] = hsb[2] - deltaV;
		if (hsb[2] < 0.0f)
			hsb[2] = 0.0f;
		return getRGB(hsb);
	}

	public static int getBrightUIColor() {
		float[] hsb = getHSB(baseUIColor);
		hsb[2] = hsb[2] + deltaV;
		if (hsb[2] > 1.0f)
			hsb[2] = 1.0f;
		return getRGB(hsb);
	}

	public static int[] getColorPallette(int n) {
		int[] colors = new int[n];
		float[] currHSB = getHSB(baseDataColor);
		float[] initHSB = getHSB(baseDataColor);
		colors[0] = getRGB(currHSB);
		for (int i = 1; i < n; i++) {
			currHSB[0] += 1.0f / numHues;
			if (currHSB[0] > 1.0f)
				currHSB[0] -= 1.0f;
			// Handle that circle around hue is done
			if (i % numHues == 0) {
				currHSB[0] += 1.0f / (numHues * 2);
				if (currHSB[0] > 1.0f)
					currHSB[0] -= 1.0f;
//				currHSB[1] -= deltaV*2/3;
				currHSB[2] -= deltaV*2/3;

				if (i % (numHues * numBrightSat) == 0) {
					currHSB[0] = initHSB[0];
					currHSB[1] = initHSB[1];
					currHSB[2] = initHSB[2];
				}
			}

			// Sanitize data
			if (currHSB[0] < 0.0f)
				currHSB[0] = 0.0f;
			if (currHSB[0] > 1.0f)
				currHSB[0] = 1.0f;
			if (currHSB[1] < 0.0f)
				currHSB[1] = 0.0f;
			if (currHSB[1] > 1.0f)
				currHSB[1] = 1.0f;
			if (currHSB[2] < 0.0f)
				currHSB[2] = 0.0f;
			if (currHSB[2] > 1.0f)
				currHSB[2] = 1.0f;

			// Add color
			colors[i] = getRGB(currHSB);
		}
		return colors;
	}
}
