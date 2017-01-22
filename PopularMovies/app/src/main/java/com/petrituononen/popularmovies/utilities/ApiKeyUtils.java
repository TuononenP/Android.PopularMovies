package com.petrituononen.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import com.petrituononen.popularmovies.R;

import java.io.IOException;

/**
 * Created by Petri Tuononen on 20.1.2017.
 */
public class ApiKeyUtils {
    private static final String TAG = ApiKeyUtils.class.getSimpleName();
    private IOUtils mIOUtils = new IOUtils();

    public String getTheMovieDbApiKey(Context context) {
        String apiKey = "";
        try {
            String fileName = context.getString(R.string.themoviedb_api_key_filename);
            String encoding = context.getString(R.string.default_file_encoding);
            apiKey = mIOUtils.readFileFromAssetsFolder(context, fileName, encoding);
        } catch (IOException e) {
            String errorText = context.getString(R.string.themoviedb_api_key_not_found_error);
            Log.w(TAG, errorText);
            e.printStackTrace();
        }
        return apiKey;
    }
}
