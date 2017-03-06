package com.petrituononen.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by Petri Tuononen on 12.2.2017.
 */
public class MovieProvider extends ContentProvider {

    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;
    public static final int CODE_FAVORITE_MOVIES = 102;
    public static final int CODE_TOP_RATED_MOVIES = 103;
    public static final int CODE_MOST_POPULAR_MOVIES = 104;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mDbHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIES, CODE_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/#", CODE_MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/" + MovieContract.PATH_FAVORITES, CODE_FAVORITE_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/" + MovieContract.PATH_TOP_RATED, CODE_TOP_RATED_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/" + MovieContract.PATH_MOST_POPULAR, CODE_MOST_POPULAR_MOVIES);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (id == -1) {
                            throw new SQLException("Failed to insert row into " + uri);
                        }
                        else {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOVIE_WITH_ID:
                String movieId = uri.getLastPathSegment();
                String[] selectMovieWithId = new String[]{movieId};
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectMovieWithId,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_TOP_RATED_MOVIES:
                // TODO: Finish
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOST_POPULAR_MOVIES:
                // TODO: Finish
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FAVORITE_MOVIES:
                String sqlSelectFavorites = MovieContract.MovieEntry.getSqlSelectForFavorites();
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        sqlSelectFavorites,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
            case CODE_MOVIE_WITH_ID:
                Uri newUri = null;
                db.beginTransaction();
                try {
                        long id = db.insertOrThrow(MovieContract.MovieEntry.TABLE_NAME, null, values);
                        if (id == -1) {
                            String msg = "Failed to insert row into " + uri;
                            Log.w("MovieProvider", msg);
                            throw new SQLException(msg);
                        }
                        newUri = MovieContract.MovieEntry.buildMovieUriWithId(id);
                        getContext().getContentResolver().notifyChange(newUri, null);
                        db.setTransactionSuccessful();

                } finally {
                    db.endTransaction();
                }
                return newUri;
            default:
                return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int numRowsDeleted;

        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
            case CODE_MOVIE_WITH_ID:
                numRowsDeleted = mDbHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                if (numRowsDeleted == -1) {
                    String msg = "Failed to delete row " + uri;
                    Log.w("MovieProvider", msg);
                    throw new SQLException(msg);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int numRowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
            case CODE_MOVIE_WITH_ID:
                numRowsUpdated = mDbHelper.getWritableDatabase().update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                if (numRowsUpdated == -1) {
                    String msg = "Failed to update row " + uri;
                    Log.w("MovieProvider", msg);
                    throw new SQLException(msg);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsUpdated;
    }

    @Override
    public void shutdown() {
        mDbHelper.close();
        super.shutdown();
    }
}
