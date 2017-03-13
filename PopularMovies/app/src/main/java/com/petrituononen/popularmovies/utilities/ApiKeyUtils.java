package com.petrituononen.popularmovies.utilities;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.petrituononen.popularmovies.R;
import com.petrituononen.popularmovies.exceptions.ApiKeyNotFoundException;

import java.io.IOException;

/**
 * Created by Petri Tuononen on 20.1.2017.
 */
public class ApiKeyUtils {
    private static final String TAG = ApiKeyUtils.class.getSimpleName();
    private IOUtilities mIOUtils = new IOUtilities();

    public String getTheMovieDbApiKey(Context context) throws ApiKeyNotFoundException {
        String apiKey = "";
        String fileName = context.getString(R.string.themoviedb_api_key_filename);
        String encoding = context.getString(R.string.default_file_encoding);
        try {
            apiKey = mIOUtils.readFileFromAssetsFolder(context, fileName, encoding);
        } catch (IOException e) {
            String errorText = context.getString(R.string.themoviedb_api_key_not_found_warning);
            Log.w(TAG, errorText);
            e.printStackTrace();
            throw new ApiKeyNotFoundException(errorText);
        }

        if (TextUtils.isEmpty(apiKey)) {
            String apiKeyEmptyErrorText = context.getString(R.string.themoviedb_api_key_empty_warning);
            Log.w(TAG, apiKeyEmptyErrorText);
            throw new ApiKeyNotFoundException(apiKeyEmptyErrorText);
        }
        return apiKey;
    }
}
