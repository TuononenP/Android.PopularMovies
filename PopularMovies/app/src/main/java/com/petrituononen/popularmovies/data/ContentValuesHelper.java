package com.petrituononen.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;

import com.petrituononen.popularmovies.utilities.PicassoUtils;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by Petri Tuononen on 12.2.2017.
 */
public final class ContentValuesHelper {
    public static ContentValues GetContentValues(Context context, ParcelableMovieDb movieDb,
                                                 boolean isFavorite) {
        if (movieDb == null) {
            return null;
        }
        ContentValues values = new ContentValues();

        PicassoUtils picassoUtils = new PicassoUtils();
        String posterPath = picassoUtils.formMoviePosterUrl(movieDb, context);

        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieDb.getId());
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movieDb.getOriginalTitle());
        values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, isFavorite);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER, posterPath);
        values.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movieDb.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_RATING, movieDb.getVoteAverage());
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieDb.getReleaseDate());

        return values;
    }
}
