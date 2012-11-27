package edu.gatech.earthquakes.components;

import java.awt.Color;
import java.util.Arrays;

public class Theme {

	private static final int baseUIColor = 0xFF00678B;
	private static final int backgroundColor = 0xFFEEEEEE;

	/**
	 * The change applied to values for highlight and darkened compliments, as
	 * well as pallette generation.
	 */
	private static final float deltaV = 0.3f;
	
	/**
	 * Color pallette for drawing all the data.
	 */
	private static final int[] pallette = new int[]{
		0xFFF23535,
		0xFFF2762E,
		0xFFF2AC29,
		0xFFC9D94E,
		0xFF03A688,
		 
		0xFF79C7D9,
		0xFF58838C,
		0xFFBF996B,
		0xFFF2BC79,
		0xFFF28972,
		
		0xFF9BF2EA,
		0xFF497358,
		0xFFCE1A53,
		0xFF79637E,
		0xFF0071BC,
		
		0xFFBC6F00,
		0xFF1D8F49,
		0xFFD62BD9,
		0xFFFFE800,
		0xFFD96AA3,
	};
	
	static{
		Arrays.sort(pallette, 0 , 5);
		Arrays.sort(pallette, 5, 10);
		Arrays.sort(pallette, 10, 15);
		Arrays.sort(pallette, 15, 20);
	}

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
	
	public static int getBackgroundColor() {
		return backgroundColor;
	}

	public static int getBaseUIColor() {
		return baseUIColor;
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

	public synchronized static int getPalletteColor(int n) {
		if(n < 0)
			throw new IllegalArgumentException("Can't have negative index.");
		int index = n % pallette.length;
		return pallette[index];
	}
	
	public static int rgba(int rgb, int a) {
		return rgb & ((a << 24) | 0xFFFFFF);
	}
}
