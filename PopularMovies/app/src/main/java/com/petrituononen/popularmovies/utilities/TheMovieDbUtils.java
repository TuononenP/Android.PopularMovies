package com.petrituononen.popularmovies.utilities;

import android.content.Context;

import com.petrituononen.popularmovies.exceptions.NoInternetConnectionException;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * Created by Petri Tuononen on 20.1.2017.
 */

public class TheMovieDbUtils {
    private ApiKeyUtils ApiKeyUtil = new ApiKeyUtils();

    private String getApiKey(Context context) {
        String apiKey = "";
        apiKey = ApiKeyUtil.getTheMovieDbApiKey(context);
        return apiKey;
    }

    public TmdbMovies getMovies(Context context) throws NoInternetConnectionException {
        if (!NetworkUtils.isOnline(context)) {
            throw new NoInternetConnectionException();
        }
        return new TmdbApi(getApiKey(context)).getMovies();
    }

    public MovieResultsPage getTopRated(Context context, int pageNumber)
            throws NoInternetConnectionException {
        return getMovies(context).getTopRatedMovies("en", pageNumber);
    }

    public MovieResultsPage getMostPopular(Context context, int pageNumber)
            throws NoInternetConnectionException {
        return getMovies(context).getPopularMovies("en", pageNumber);
    }
}
