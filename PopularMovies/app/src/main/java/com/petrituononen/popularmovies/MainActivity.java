package com.petrituononen.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.petrituononen.popularmovies.utilities.BasicUtils;
import com.petrituononen.popularmovies.utilities.NetworkUtils;
import com.petrituononen.popularmovies.utilities.TheMovieDbUtils;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class MainActivity extends AppCompatActivity {

//    private static final int NUM_LIST_ITEMS = 100;
    private MovieAdapter mAdapter;
    private RecyclerView mMoviesList;
    private NetworkUtils mNetworkUtils = new NetworkUtils();
    private TextView mNoInternetAccessTextView;
    private TheMovieDbUtils mMovieUtils = new TheMovieDbUtils();
    private static final String TOP_RATED = "top-rated";
    private static final String MOST_POPULAR = "most-popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // show text view if there is no internet connectivity
        mNoInternetAccessTextView = (TextView)findViewById(R.id.tv_no_internet_access);
        if(mNetworkUtils.isOnline(this) == false) {
            mNoInternetAccessTextView.setVisibility(View.VISIBLE);
        }

        mMoviesList = (RecyclerView) findViewById(R.id.rv_movie_posters);

        int columns = BasicUtils.calculateNoOfColumns(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, columns);
        mMoviesList.setLayoutManager(layoutManager);
        mMoviesList.setHasFixedSize(true);

        mAdapter = new MovieAdapter(new ArrayList<MovieDb>());
        mMoviesList.setAdapter(mAdapter);

        new MovieTask(this, mAdapter, mMoviesList).execute(MOST_POPULAR);
    }

    private void showTopRatedMovies() {
        new MovieTask(this, mAdapter, mMoviesList).execute(TOP_RATED);
    }

    private void showMostPopularMovies() {
        new MovieTask(this, mAdapter, mMoviesList).execute(MOST_POPULAR);
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
        }
        else if (id == R.id.app_bar_list_top_rated) {
            showTopRatedMovies();
        }
        return super.onOptionsItemSelected(item);
    }

    public class MovieTask extends AsyncTask<String, Void, List<MovieDb>> {

        private Context mContext;
        private MovieAdapter mMovieAdapter;
        private RecyclerView mRecyclerView;

        public MovieTask (Context context, MovieAdapter adapter, RecyclerView recyclerView) {
            mContext = context;
            mRecyclerView = recyclerView;
            mMovieAdapter = adapter;
        }

        @Override
        protected List<MovieDb> doInBackground(String... params) {
            String param = params[0];
            MovieResultsPage resultPage;
            if (param == TOP_RATED) {
                resultPage = mMovieUtils.getTopRated(mContext, 0);
                return resultPage.getResults();
            }
            else if (param == MOST_POPULAR) {
                resultPage = mMovieUtils.getMostPopular(mContext, 0);
                return resultPage.getResults();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieDb> movieDbs) {
            mMovieAdapter = new MovieAdapter(movieDbs);
            mRecyclerView.setAdapter(mMovieAdapter);
            mMovieAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
