package com.petrituononen.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ImageView;

import com.petrituononen.popularmovies.utilities.ApiKeyUtils;
import com.petrituononen.popularmovies.utilities.NetworkUtils;
import com.petrituononen.popularmovies.utilities.PicassoUtils;
import com.petrituononen.popularmovies.utilities.TheMovieDbUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private static final String TAG = ApiKeyUtils.class.getSimpleName();
    private TheMovieDbUtils mMovieUtils = new TheMovieDbUtils();
    private Context mContext = InstrumentationRegistry.getTargetContext();
    private NetworkUtils mNetworkUtils = new NetworkUtils();
    private PicassoUtils mPicassoUtils = new PicassoUtils();

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.petrituononen.popularmovies", appContext.getPackageName());
    }

    @Test
    public void api_key_is_found() throws Exception {
        ApiKeyUtils apiKeyUtils = new ApiKeyUtils();
        String apiKey = apiKeyUtils.getTheMovieDbApiKey(mContext);
        assertNotEquals("", apiKey);
    }

    @Test
    public void can_get_most_popular_movies() throws Exception {
//        String response = mMovieUtils.getMostPopularMovies(mContext);
//        assertNotEquals("", response);
        MovieResultsPage results = mMovieUtils.getmostPopular(mContext);
//        Log.d(TAG, "Amount of popular movies: " + results.getResults().size());
        assertTrue(results.getResults().size() > 0);
    }

    @Test
    public void can_get_top_rated_movies() throws Exception {
//        String response = mMovieUtils.getTopRatedMovies(mContext);
//        assertNotEquals("", response);
        MovieResultsPage results = mMovieUtils.getTopRated(mContext);
        assertTrue(results.getResults().size() > 0);
    }

//    @Test
//    public void can_load_movie_images() throws Exception {
//        MovieResultsPage results = mMovieUtils.getmostPopular(mContext);
//        List<MovieDb> movies = results.getResults();
//        final AtomicBoolean loaded = new AtomicBoolean();
//        if (movies.size() > 0) {
//            final String moviePosterPath = movies.get(0).getPosterPath();
//            final ImageView view = new ImageView(mContext);
//            String movieBasePath = mContext.getString(R.string.themobiedb_api_movie_poster_basepath);
//            String imageUrl = Uri.parse(movieBasePath).buildUpon().appendPath(moviePosterPath).build().toString();
////            mPicassoUtils.loadAlbumArtThumbnail(mContext, view, imageUrl);
//            Picasso.with(mContext).load(imageUrl).into(view, new Callback.EmptyCallback() { // throws exception
//                @Override public void onSuccess() {
//                    loaded.set(true);
//                }
//            });
//        }
//        assertTrue(loaded.get());
//    }

    @Test
    public void is_online() throws Exception {
        Boolean isOnline = mNetworkUtils.isOnline(mContext);
        assertTrue(isOnline);
    }
}
