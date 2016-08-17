package com.pccw.nowplayer.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;

/**
 * Created by Kriz on 23/6/15.
 */
public class ColorUtils {

    public static int parseColor(String colorString, int defaultColor) {
        try {
            if (colorString == null || colorString.length() == 0) return defaultColor;
            return Color.parseColor(colorString);
        } catch (Exception ignored) {
            return defaultColor;
        }
    }

    public static String toARGBHexString(int color) {
        return String.format("#%08X", (0xFFFFFFFF & color));
    }

    public static String toRGBHexString(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static ColorStateList getColorStatusList(int clickedColor, int normalColor) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{android.R.attr.state_pressed},
                new int[]{}
        };

        int[] colors = new int[]{
                clickedColor,
                clickedColor,
                normalColor
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        return colorStateList;
    }
}
