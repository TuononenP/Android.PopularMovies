package com.petrituononen.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.petrituononen.popularmovies.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Petri Tuononen on 20.1.2017.
 */

public class TheMovieDbUtils {
    private static final String TAG = ApiKeyUtils.class.getSimpleName();
    private static final String API_KEY_PARAM = "api_key";
    private ApiKeyUtils ApiKeyUtil = new ApiKeyUtils();
    private NetworkUtils NetworkUtil = new NetworkUtils();

    public String GetMostPopularMovies(Context context) {
        String mostPopular = GetMostPopularPath(context);
        return GetMoviesList(context, mostPopular);
    }

    public String GetTopRatedMovies(Context context) {
        String topRated = GetTopRatedPath(context);
        return GetMoviesList(context, topRated);
    }


    /**
     *
     * @param context
     * @param sortOrder For the full list of sort paths see https://developers.themoviedb.org/3/movies
     * @return
     */
    private String GetMoviesList(Context context, String sortOrder) {
        String response = "";
        String basePath = GetBasePath(context);
        String apiKey = GetApiKey(context);
        Uri uri = Uri
                .parse(basePath)
                .buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();
        try {
            URL url = new URL(uri.toString());
            response = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (MalformedURLException e) {
            String errorText = context.getString(R.string.themoviedb_url_malformed_error);
            Log.e(TAG, errorText);
            e.printStackTrace();
        } catch (IOException e) {
            String errorText = context.getString(R.string.themoviedb_api_call_error);
            Log.e(TAG, errorText);
            e.printStackTrace();
        }
        return response;
    }

    private String GetApiKey(Context context) {
        String apiKey = "";
        apiKey = ApiKeyUtil.GetTheMovieDbApiKey(context);
        return apiKey;
    }

    private String GetBasePath(Context context) {
        return context.getString(R.string.themoviedb_api_basepath);
    }

    private String GetMostPopularPath(Context context) {
        return context.getString(R.string.themoviedb_api_most_popular);
    }

    private String GetTopRatedPath(Context context) {
        return context.getString(R.string.themoviedb_api_top_rated);
    }
}
