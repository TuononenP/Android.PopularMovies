package com.petrituononen.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.petrituononen.popularmovies.exceptions.NoInternetConnectionException;
import com.petrituononen.popularmovies.utilities.BasicUtils;
import com.petrituononen.popularmovies.utilities.NetworkUtils;
import com.petrituononen.popularmovies.utilities.TheMovieDbUtils;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    //    private static final int NUM_LIST_ITEMS = 100;
    private MovieAdapter mAdapter;
    private RecyclerView mMoviesList;
    private NetworkUtils mNetworkUtils = new NetworkUtils();
    private TextView mNoInternetAccessTextView;
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
        if (mNetworkUtils.isOnline(this) == false) {
            mNoInternetAccessTextView.setVisibility(View.VISIBLE);
        }

        mMoviesList = (RecyclerView) findViewById(R.id.rv_movie_posters);

        int columnCount = BasicUtils.calculateNoOfColumns(this);
        setImageWidthAndHeight(this, columnCount);
        GridLayoutManager layoutManager = new GridLayoutManager(this, columnCount);
        mMoviesList.setLayoutManager(layoutManager);
        mMoviesList.setHasFixedSize(true);

        mAdapter = new MovieAdapter(new ArrayList<MovieDb>(), mImageWidth, mImageHeight, this);
        mMoviesList.setAdapter(mAdapter);

        new MovieTask(this, mAdapter, mMoviesList, this).execute(MOST_POPULAR);
    }

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
        startActivity(detailIntent);
    }

    public class MovieTask extends AsyncTask<String, Void, List<MovieDb>> {

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
        protected List<MovieDb> doInBackground(String... params) {
            String param = params[0];
            MovieResultsPage resultPage;
            switch (param) {
                case TOP_RATED:
                    try {
                        resultPage = mMovieUtils.getTopRated(mContext, 0);
                        return resultPage.getResults();
                    } catch (NoInternetConnectionException e) {
                        String errorText = mContext.getString(R.string.no_internet_warning);
                        Log.w(TAG, errorText);
                        e.printStackTrace();
                    }
                    break;
                case MOST_POPULAR:
                    try {
                        resultPage = mMovieUtils.getMostPopular(mContext, 0);
                        return resultPage.getResults();
                    } catch (NoInternetConnectionException e) {
                        String errorText = mContext.getString(R.string.no_internet_warning);
                        Log.w(TAG, errorText);
                        e.printStackTrace();
                    }
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieDb> movieDbs) {
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
