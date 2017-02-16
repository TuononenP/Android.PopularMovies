package com.petrituononen.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.petrituononen.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by Petri Tuononen on 12.2.2017.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 3;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =

                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                        MovieEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieEntry.COLUMN_MOVIE_ID       + " INTEGER NOT NULL, "                 +
                        MovieEntry.COLUMN_TITLE + " STRING NOT NULL,"                  +
                        MovieEntry.COLUMN_FAVORITE   + " INTEGER DEFAULT 0, "                    +
                        MovieEntry.COLUMN_POSTER   + " STRING NOT NULL, "                    +
                        MovieEntry.COLUMN_SYNOPSIS   + " STRING NOT NULL, "                    +
                        MovieEntry.COLUMN_RATING   + " REAL NOT NULL, "                    +
                        MovieEntry.COLUMN_RELEASE_DATE + " STRING NOT NULL, "                    +

                        " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
