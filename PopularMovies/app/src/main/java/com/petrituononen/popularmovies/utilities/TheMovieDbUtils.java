package com.petrituononen.popularmovies.utilities;

import android.content.Context;

import com.petrituononen.popularmovies.exceptions.NoInternetConnectionException;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbReviews;
import info.movito.themoviedbapi.model.Reviews;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * Created by Petri Tuononen on 20.1.2017.
 */

public class TheMovieDbUtils {
    private ApiKeyUtils ApiKeyUtil = new ApiKeyUtils();
    private static final String LANGUAGE = "en";

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
        return getMovies(context).getTopRatedMovies(LANGUAGE, pageNumber);
    }

    public MovieResultsPage getMostPopular(Context context, int pageNumber)
            throws NoInternetConnectionException {
        return getMovies(context).getPopularMovies(LANGUAGE, pageNumber);
    }

    public List<Video> getVideos(Context context, int movieId)
            throws NoInternetConnectionException {
        return getMovies(context).getVideos(movieId, LANGUAGE);
    }

    public TmdbReviews getReviews(Context context) {
        return new TmdbApi(getApiKey(context)).getReviews();
    }

    public List<Reviews> getReviews(Context context, int movieId, int pageNo) {
        return getReviews(context).getReviews(movieId, LANGUAGE, pageNo).getResults();
    }
}
