package edu.gatech.earthquakes.components;

import java.awt.Color;
import java.util.Arrays;

public class Theme {
    public static final int HIGHLIGHTED_COLOR = 0xFFC9D94E;
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
    private static final int[] pallette = new int[] {
            // continents
            0xff03a688, 0xffc9d94e, 0xfff23535, 0xfff2762e, 0xfff2ac29,
            0xff58838c, 0xff79c7d9,

            // dependency
            0xffbf996b, 0xfff28972, 0xfff2bc79,

            // types
            0xff0071bc, 0xffbc6f00,  0xff79637e, 0xff9bf2ea,

            0xffce1a53,

            // depth
            0xff1d8f49,

            0xff497358, 0xffd62bd9, 0xffd96aa3, 0xffffe800, };

    public static float[] getHSB(int rgbColor) {
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
        if (n < 0)
            throw new IllegalArgumentException("Can't have negative index.");
        int index = n % pallette.length;
        return pallette[index];
    }

    public static int rgba(int rgb, int a) {
        return rgb & ((a << 24) | 0xFFFFFF);
    }

    public static int changeSaturation(int rgb, float percentage, boolean relative) {
        float[] hsb = getHSB(rgb);
        if(relative)
            hsb[1] *= percentage;
        else
            hsb[1] = percentage;
        if (hsb[1] < 0.0f)
            hsb[1] = 0.0f;
        return getRGB(hsb);
    }

    public static int changeBrightness(int rgb, float percentage, boolean relative) {
        float[] hsb = getHSB(rgb);
        if(relative)
            hsb[2] *= percentage;
        else
            hsb[2] = percentage;
        if (hsb[2] < 0.0f)
            hsb[2] = 0.0f;
        return getRGB(hsb);
    }

}
