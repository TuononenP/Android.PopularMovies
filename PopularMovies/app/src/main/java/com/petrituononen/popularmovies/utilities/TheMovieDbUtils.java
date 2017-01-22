package com.petrituononen.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.petrituononen.popularmovies.R;
import com.petrituononen.popularmovies.exceptions.NoInternetConnectionException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * Created by Petri Tuononen on 20.1.2017.
 */

public class TheMovieDbUtils {
    private static final String TAG = ApiKeyUtils.class.getSimpleName();
    private static final String API_KEY_PARAM = "api_key";
    private ApiKeyUtils ApiKeyUtil = new ApiKeyUtils();
    private NetworkUtils NetworkUtil = new NetworkUtils();

    public String getMostPopularMovies(Context context) {
        String mostPopular = getMostPopularPath(context);
        return getMoviesList(context, mostPopular);
    }

    public String getTopRatedMovies(Context context) {
        String topRated = getTopRatedPath(context);
        return getMoviesList(context, topRated);
    }

    /**
     *
     * @param context
     * @param sortOrder For the full list of sort paths see https://developers.themoviedb.org/3/movies
     * @return
     */
    private String getMoviesList(Context context, String sortOrder) {
        String response = "";
        String basePath = getBasePath(context);
        String apiKey = getApiKey(context);
        Uri uri = Uri
                .parse(basePath)
                .buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();
        try {
            URL url = new URL(uri.toString());
            response = NetworkUtils.getResponseFromHttpUrl(url, context);
        } catch (MalformedURLException e) {
            String errorText = context.getString(R.string.themoviedb_url_malformed_error);
            Log.w(TAG, errorText);
            e.printStackTrace();
        } catch (IOException e) {
            String errorText = context.getString(R.string.themoviedb_api_call_error);
            Log.w(TAG, errorText);
            e.printStackTrace();
        } catch (NoInternetConnectionException e) {
            String errorText = context.getString(R.string.no_internet_warning);
            Log.w(TAG, errorText);
            e.printStackTrace();
        }
        return response;
    }

    private String getApiKey(Context context) {
        String apiKey = "";
        apiKey = ApiKeyUtil.getTheMovieDbApiKey(context);
        return apiKey;
    }

    private String getBasePath(Context context) {
        return context.getString(R.string.themoviedb_api_basepath);
    }

    private String getMostPopularPath(Context context) {
        return context.getString(R.string.themoviedb_api_most_popular);
    }

    private String getTopRatedPath(Context context) {
        return context.getString(R.string.themoviedb_api_top_rated);
    }

    public TmdbMovies getMovies(Context context) {
        return new TmdbApi(getApiKey(context)).getMovies();
    }

    public MovieResultsPage getTopRated(Context context, int pageNumber) {
        return getMovies(context).getTopRatedMovies("en", pageNumber);
    }

    public MovieResultsPage getMostPopular(Context context, int pageNumber) {
        return getMovies(context).getPopularMovies("en", pageNumber);
    }
}
