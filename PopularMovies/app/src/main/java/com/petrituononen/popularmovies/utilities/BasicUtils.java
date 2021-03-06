package com.petrituononen.popularmovies.utilities;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Petri Tuononen on 20.1.2017.
 */
public class BasicUtils {

    /**
     * Calculates number of columns for GridLayoutManager.
     * ie. how many movies posters can fit on display.
     * @param context
     * @return
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }

    /**
     * Get display width in dp.
     * @param context
     * @return
     */
    public static float getDisplayWidthInDp(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return dpWidth;
    }

    /**
     * Get display width in pixels.
     * @param context
     * @return
     */
    public static int getDisplayWidthInPx(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * Get display height in dp.
     * @param context
     * @return
     */
    public static float getDisplayHeightInDp(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels / displayMetrics.density;
    }

    /**
     * Get display height in pixels.
     * @param context
     * @return
     */
    public static int getDisplayHeightInPx(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
}
