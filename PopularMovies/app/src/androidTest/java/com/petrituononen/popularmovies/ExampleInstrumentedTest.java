package com.petrituononen.popularmovies;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.petrituononen.popularmovies.utilities.ApiKeyUtils;
import com.petrituononen.popularmovies.utilities.NetworkUtils;
import com.petrituononen.popularmovies.utilities.TheMovieDbUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private TheMovieDbUtils mMovieUtils = new TheMovieDbUtils();
    private Context mContext = InstrumentationRegistry.getTargetContext();
    private NetworkUtils mNetworkUtils = new NetworkUtils();

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.petrituononen.popularmovies", appContext.getPackageName());
    }

    @Test
    public void api_key_is_found() throws Exception {
        ApiKeyUtils apiKeyUtils = new ApiKeyUtils();
        String apiKey = apiKeyUtils.GetTheMovieDbApiKey(mContext);
        assertNotEquals("", apiKey);
    }

    @Test
    public void can_get_most_popular_movies() throws Exception {
        String response = mMovieUtils.GetMostPopularMovies(mContext);
        assertNotEquals("", response);
    }

    @Test
    public void can_get_top_rated_movies() throws Exception {
        String response = mMovieUtils.GetTopRatedMovies(mContext);
        assertNotEquals("", response);
    }

    @Test
    public void is_online() throws Exception {
        Boolean isOnline = mNetworkUtils.isOnline(mContext);
        assertTrue(isOnline);
    }
}
