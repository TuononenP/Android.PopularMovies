package com.petrituononen.popularmovies.utilities;

import android.content.Context;

import com.petrituononen.popularmovies.exceptions.ApiKeyNotFoundException;
import com.petrituononen.popularmovies.exceptions.NoInternetConnectionException;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbReviews;
import info.movito.themoviedbapi.model.Reviews;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.reviews;

/**
 * Created by Petri Tuononen on 20.1.2017.
 */

public class TheMovieDbUtils {
    private ApiKeyUtils ApiKeyUtil = new ApiKeyUtils();
    private static final String LANGUAGE = "en";

    private String getApiKey(Context context) throws ApiKeyNotFoundException {
        String apiKey = null;
        apiKey = ApiKeyUtil.getTheMovieDbApiKey(context);
        return apiKey;
    }

    public TmdbApi getApi(Context context)
            throws NoInternetConnectionException, ApiKeyNotFoundException {
        if (!NetworkUtils.isOnline(context)) {
            throw new NoInternetConnectionException();
        }
        TmdbApi movieApi = null;
        movieApi = new TmdbApi(getApiKey(context));
        return movieApi;
    }

    public TmdbMovies getMovies(Context context)
            throws NoInternetConnectionException, ApiKeyNotFoundException {
        TmdbMovies movies = null;
        TmdbApi movieApi = getApi(context);
        if (movieApi != null) {
            movies = movieApi.getMovies();
        }
        return movies;
    }

    public MovieResultsPage getTopRated(Context context, int pageNumber)
            throws NoInternetConnectionException, ApiKeyNotFoundException {
        TmdbMovies movies = getMovies(context);
        if (movies != null) {
            return movies.getTopRatedMovies(LANGUAGE, pageNumber);
        }
        return new MovieResultsPage();
    }

    public MovieResultsPage getMostPopular(Context context, int pageNumber)
            throws NoInternetConnectionException, ApiKeyNotFoundException {
        TmdbMovies movies = getMovies(context);
        if (movies != null) {
            return movies.getPopularMovies(LANGUAGE, pageNumber);
        }
        return new MovieResultsPage();
    }

    public List<Video> getVideos(Context context, int movieId)
            throws NoInternetConnectionException, ApiKeyNotFoundException {
        List<Video> videos = new ArrayList<>();
        TmdbMovies movies = getMovies(context);
        if (movies != null) {
            videos = movies.getVideos(movieId, LANGUAGE);
        }
        return videos;
    }

    public TmdbReviews getTmdbReviews(Context context)
            throws NoInternetConnectionException, ApiKeyNotFoundException {
        TmdbReviews tmdbReviews = null;
        TmdbApi api = getApi(context);
        if (api != null) {
            tmdbReviews = api.getReviews();
        }
        return tmdbReviews;
    }

    public List<Reviews> getReviews(Context context, int movieId, int pageNo)
            throws NoInternetConnectionException, ApiKeyNotFoundException {
        List<Reviews> reviews = new ArrayList<>();
        TmdbReviews tmdbReviews = getTmdbReviews(context);
        if (tmdbReviews != null) {
            reviews = tmdbReviews.getReviews(movieId, LANGUAGE, pageNo).getResults();
        }
        return reviews;
    }
}
