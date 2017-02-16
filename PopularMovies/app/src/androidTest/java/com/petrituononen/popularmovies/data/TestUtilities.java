package com.petrituononen.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Petri Tuononen on 14.2.2017.
 */
public class TestUtilities {

    static final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    static ContentValues createTestMovieContentValues() {

        ContentValues testMovieValues = new ContentValues();

        testMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 10);
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Test Movie");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, "http://imageurl");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, "movie plot");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_RATING, 5.7);
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2017.01.01");

        return testMovieValues;
    }

    static ContentValues[] createBulkInsertTestWeatherValues() {

        ContentValues[] bulkTestWeatherValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues values = new ContentValues();

            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 10 + i);
            values.put(MovieContract.MovieEntry.COLUMN_TITLE, "Test Movie");
            values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
            values.put(MovieContract.MovieEntry.COLUMN_POSTER, "http://imageurl");
            values.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, "movie plot");
            values.put(MovieContract.MovieEntry.COLUMN_RATING, 5.7);
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2017.01.01");

            bulkTestWeatherValues[i] = values;
        }

        return bulkTestWeatherValues;
    }

    static void validateThenCloseCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertNotNull(
                "This cursor is null. Did you make sure to register your ContentProvider in the manifest?",
                valueCursor);

        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);

            /* Test to see if the column is contained within the cursor */
            String columnNotFoundError = "Column '" + columnName + "' not found. " + error;
            assertFalse(columnNotFoundError, index == -1);

            /* Test to see if the expected value equals the actual value (from the Cursor) */
            String expectedValue = entry.getValue().toString();
            String actualValue = valueCursor.getString(index);

            String valuesDontMatchError = "Actual value '" + actualValue
                    + "' did not match the expected value '" + expectedValue + "'. "
                    + error;

            assertEquals(valuesDontMatchError,
                    expectedValue,
                    actualValue);
        }
    }
}
