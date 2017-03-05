package com.petrituononen.popularmovies.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.petrituononen.popularmovies.BuildConfig;

/**
 * Created by Petri Tuononen on 12.2.2017.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movie";
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_TOP_RATED = "top_rated";
    public static final String PATH_MOST_POPULAR = "most_popular";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .build();

        public static final Uri FAVORITES
                = CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
        public static final Uri TOP_RATED
                = CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED).build();
        public static final Uri MOST_POPULAR
                = CONTENT_URI.buildUpon().appendPath(PATH_MOST_POPULAR).build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_FAVORITE = "isFavorite";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULARITY = "popularity";

        public static Uri buildMovieUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getSqlSelectForFavorites() {
            return COLUMN_FAVORITE + "=" + 1;
        }

        public static String getSortOrderMostPopular() {
            return COLUMN_POPULARITY + " DESC";
        }

        public static String getSortOrderTopRated() {
            return COLUMN_RATING + " DESC";
        }

        public static String getSqlOrderByPopularityDescending() { return COLUMN_POPULARITY + " DESC";}
    }
}
