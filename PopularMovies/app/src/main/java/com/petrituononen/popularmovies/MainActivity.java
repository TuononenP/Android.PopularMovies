package com.petrituononen.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.petrituononen.popularmovies.data.ParcelableMovieDb;
import com.petrituononen.popularmovies.exceptions.NoInternetConnectionException;
import com.petrituononen.popularmovies.utilities.BasicUtils;
import com.petrituononen.popularmovies.utilities.NetworkUtils;
import com.petrituononen.popularmovies.utilities.TheMovieDbUtils;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private static final String MOVIE_LIST_SAVE_STATE = "saved-movie-list";
    private static final String CLICKED_MOVIE_DB_STATE = "clicked_movie_db_state";
    private MovieAdapter mAdapter;
    private RecyclerView mMoviesList;
    private TextView mNoInternetAccessTextView;
    private GridLayoutManager mLayoutManager;
    private static ArrayList<ParcelableMovieDb> mMovies = new ArrayList<>();
    private TheMovieDbUtils mMovieUtils = new TheMovieDbUtils();
    private static final String TOP_RATED = "top-rated";
    private static final String MOST_POPULAR = "most-popular";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static int mImageWidth;
    private static int mImageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // show text view if there is no internet connectivity
        mNoInternetAccessTextView = (TextView) findViewById(R.id.tv_no_internet_access);
        if (NetworkUtils.isOnline(this) == false) {
            mNoInternetAccessTextView.setVisibility(View.VISIBLE);
        }

        // calculate responsive column count and image width and height
        // to accommodate varying display sizes
        int columnCount = BasicUtils.calculateNoOfColumns(this);
        setImageWidthAndHeight(this, columnCount);

        mMoviesList = (RecyclerView) findViewById(R.id.rv_movie_posters);
        mLayoutManager = new GridLayoutManager(this, columnCount);
        mMoviesList.setLayoutManager(mLayoutManager);
        mMoviesList.setHasFixedSize(true);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MOVIE_LIST_SAVE_STATE)) {
                ArrayList<ParcelableMovieDb> moviesParcelable = savedInstanceState.getParcelableArrayList(MOVIE_LIST_SAVE_STATE);
                if (moviesParcelable != null && moviesParcelable.size() > 0) {
                    mAdapter = new MovieAdapter(moviesParcelable, mImageWidth, mImageHeight, this);
                    mMoviesList.setAdapter(mAdapter);
                }
            }
        }

        if (mMoviesList.getAdapter() == null) {
            showMostPopularMovies();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putParcelableArrayList(MOVIE_LIST_SAVE_STATE, mMovies);
    }

    /**
     * Calculate resized image width and height to stretch movie posters to fit
     * columns and display height perfectly but maintaining aspect ratio
     * aim is 2x2 in portrait mode and 3x1 in landscape mode for mobile phone.
     * @param context
     * @param columnCount
     */
    public void setImageWidthAndHeight(Context context, int columnCount) {
        int displayWidth = BasicUtils.getDisplayWidthInPx(context);
        double scale = 1.5;
        mImageWidth = (int) Math.ceil(displayWidth / columnCount);
        mImageHeight = (int) Math.ceil(scale * mImageWidth);
    }

    private void showTopRatedMovies() {
        new MovieTask(this, mAdapter, mMoviesList, this).execute(TOP_RATED);
    }

    private void showMostPopularMovies() {
        new MovieTask(this, mAdapter, mMoviesList, this).execute(MOST_POPULAR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.app_bar_list_most_popular) {
            showMostPopularMovies();
        } else if (id == R.id.app_bar_list_top_rated) {
            showTopRatedMovies();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        detailIntent.putExtra(CLICKED_MOVIE_DB_STATE, (Parcelable) mMovies.get(clickedItemIndex));
        startActivity(detailIntent);
    }

    public class MovieTask extends AsyncTask<String, Void, ArrayList<ParcelableMovieDb>> {

        private Context mContext;
        private MovieAdapter mMovieAdapter;
        private RecyclerView mRecyclerView;
        private MovieAdapter.ListItemClickListener mListener;

        public MovieTask(Context context, MovieAdapter adapter, RecyclerView recyclerView,
                         MovieAdapter.ListItemClickListener listener) {
            mContext = context;
            mRecyclerView = recyclerView;
            mMovieAdapter = adapter;
            mListener = listener;
        }

        @Override
        protected ArrayList<ParcelableMovieDb> doInBackground(String... params) {
            String param = params[0];
            List<MovieDb> movieList = new ArrayList<>();
            ArrayList<ParcelableMovieDb> movies = new ArrayList<>();
            if (param != null && TextUtils.isEmpty(param) == false) {
                switch (param) {
                    case TOP_RATED:
                        try {
                            MovieResultsPage resultPage = mMovieUtils.getTopRated(mContext, 0);
                            movieList = resultPage.getResults();
                        } catch (NoInternetConnectionException e) {
                            String errorText = mContext.getString(R.string.no_internet_warning);
                            Log.w(TAG, errorText);
                            e.printStackTrace();
                        }
                        break;
                    case MOST_POPULAR:
                        try {
                            MovieResultsPage resultPage = mMovieUtils.getMostPopular(mContext, 0);
                            movieList = resultPage.getResults();
                        } catch (NoInternetConnectionException e) {
                            String errorText = mContext.getString(R.string.no_internet_warning);
                            Log.w(TAG, errorText);
                            e.printStackTrace();
                        }
                        break;
                }
                // convert to parcelable
                if (movieList != null && movieList.size() > 0) {
                    for (MovieDb movie : movieList) {
                        movies.add(new ParcelableMovieDb(movie));
                    }
                }
            }
            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<ParcelableMovieDb> movieDbs) {
            // check internet connectivity and display warning if needed
            if (NetworkUtils.isOnline(mContext)) {
                mNoInternetAccessTextView.setVisibility(View.INVISIBLE);
            }
            else {
                mNoInternetAccessTextView.setVisibility(View.VISIBLE);
            }
            mMovies = movieDbs;
            mMovieAdapter = new MovieAdapter(movieDbs, mImageWidth, mImageHeight, mListener);
            mRecyclerView.setAdapter(mMovieAdapter);
            mMovieAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
