package com.petrituononen.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

/**
 * Created by Petri Tuononen on 20.1.2017.
 */
public class BasicUtils {

    /**
     * Calculates number of columns for GridLayoutManager.
     * @param context
     * @return
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
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
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        return dpHeight;
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
