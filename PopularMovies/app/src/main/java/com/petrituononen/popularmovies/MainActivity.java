package com.petrituononen.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.petrituononen.popularmovies.data.MovieContract;
import com.petrituononen.popularmovies.data.ParcelableMovieDb;
import com.petrituononen.popularmovies.exceptions.ApiKeyNotFoundException;
import com.petrituononen.popularmovies.exceptions.NoInternetConnectionException;
import com.petrituononen.popularmovies.utilities.BasicUtils;
import com.petrituononen.popularmovies.utilities.NetworkUtils;
import com.petrituononen.popularmovies.utilities.TheMovieDbUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * Created by Petri Tuononen on 20.1.2017.
 * Main activity lists movies by selected sort order.
 */
public class MainActivity
        extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<ArrayList<ParcelableMovieDb>> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String MOVIE_LIST_SAVE_STATE = "saved-movie-list";
    private static final String LIST_INSTANCE_STATE = "list-instance-state";
    private static final String CLICKED_MOVIE_DB_STATE = "clicked_movie_db_state";
    private static final String LAST_SORT_ORDER_STATE = "last-sort-order-state";
    private static final String TOP_RATED = "top-rated";
    private static final String MOST_POPULAR = "most-popular";
    private static final String FAVORITES = "favorites";
    private static final String SORT_ORDER = "sort-order";
    private static final String MOVIES_FROM_DB = "movies-from-db";
    private static final int MOVIE_POSTER_LOADER = 77;

    private String mLastSortOrderState;

    private static int mImageWidth;
    private static int mImageHeight;

    private boolean mMoviesFromDb;

    private MovieAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private Parcelable mListState;

    private final TheMovieDbUtils mMovieUtils = new TheMovieDbUtils();
    private static ArrayList<ParcelableMovieDb> mMovies = new ArrayList<>();

    @BindView(R.id.rv_movie_posters) RecyclerView mMoviesList;
    @BindView(R.id.tv_no_internet_access) TextView mNoInternetAccessTextView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_api_key_not_found) TextView mApiKeyNotFoundTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // show text view if there is no internet connectivity
        if (!NetworkUtils.isOnline(this)) {
            mNoInternetAccessTextView.setVisibility(View.VISIBLE);
        }
        else {
            mNoInternetAccessTextView.setVisibility(View.INVISIBLE);
        }

        // calculate responsive column count and image width and height
        // to accommodate varying display sizes
        int columnCount = BasicUtils.calculateNoOfColumns(this);
        setImageWidthAndHeight(this, columnCount);

        mLayoutManager = new GridLayoutManager(this, columnCount);
        mMoviesList.setLayoutManager(mLayoutManager);
        mMoviesList.setHasFixedSize(true);

        if (savedInstanceState != null) {
            // restore movie objects
            if (savedInstanceState.containsKey(MOVIE_LIST_SAVE_STATE)) {
                ArrayList<ParcelableMovieDb> moviesParcelable
                        = savedInstanceState.getParcelableArrayList(MOVIE_LIST_SAVE_STATE);
                if (moviesParcelable != null && moviesParcelable.size() > 0) {
                    mAdapter = new MovieAdapter(moviesParcelable, mImageWidth, mImageHeight, this);
                    mMoviesList.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }
            // restore list state
            if (savedInstanceState.containsKey(LIST_INSTANCE_STATE)) {
                mListState = savedInstanceState.getParcelable(LIST_INSTANCE_STATE);
                mMoviesList.getLayoutManager().onRestoreInstanceState(mListState);
            }
            // restore sort order
            if (savedInstanceState.containsKey(LAST_SORT_ORDER_STATE)) {
                mLastSortOrderState = savedInstanceState.getString(LAST_SORT_ORDER_STATE);
            }

            if (savedInstanceState.containsKey(MOVIES_FROM_DB)) {
                mMoviesFromDb = savedInstanceState.getBoolean(MOVIES_FROM_DB);
            }
        }

        if (mMoviesList.getAdapter() == null) {
            // these two lines avoid No adapter attached; skipping layout error
            mAdapter = new MovieAdapter(new ArrayList<ParcelableMovieDb>(),
                    mImageWidth, mImageHeight, this);
            mMoviesList.setAdapter(mAdapter);

            showMostPopularMovies();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putParcelableArrayList(MOVIE_LIST_SAVE_STATE, mMovies);
        mListState = mMoviesList.getLayoutManager().onSaveInstanceState();
        saveInstanceState.putParcelable(LIST_INSTANCE_STATE, mListState);
        saveInstanceState.putString(LAST_SORT_ORDER_STATE, mLastSortOrderState);
        saveInstanceState.putBoolean(MOVIES_FROM_DB, mMoviesFromDb);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMovies = savedInstanceState.getParcelableArrayList(MOVIE_LIST_SAVE_STATE);
        mListState = savedInstanceState.getParcelable(LIST_INSTANCE_STATE);
        mLastSortOrderState = savedInstanceState.getString(LAST_SORT_ORDER_STATE);
        mMoviesFromDb = savedInstanceState.getBoolean(MOVIES_FROM_DB);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
        mListState = null;
    }

    /**
     * Calculate resized image width and height to stretch movie posters to fit
     * columns and display height perfectly but maintaining aspect ratio
     * aim is 2x2 in portrait mode and 3x1 in landscape mode for mobile phone.
     * @param context
     * @param columnCount
     */
    private void setImageWidthAndHeight(Context context, int columnCount) {
        int displayWidth = BasicUtils.getDisplayWidthInPx(context);
        double scale = 1.5;
        mImageWidth = (int) Math.ceil(displayWidth / columnCount);
        mImageHeight = (int) Math.ceil(scale * mImageWidth);
    }

    private void showErrorMessage() {
        Toast.makeText(getBaseContext(), R.string.could_not_fetch_movies, Toast.LENGTH_LONG).show();
    }

    private void showErrorMessage(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showTopRatedMovies() {
        startMoviePosterLoader(TOP_RATED);
    }

    private void showMostPopularMovies() {
        startMoviePosterLoader(MOST_POPULAR);
    }

    private void showFavoriteMovies() {
        startMoviePosterLoader(FAVORITES);
    }

    private void startMoviePosterLoader(String sortOrder) {
        Bundle bundle = new Bundle();
        bundle.putString(SORT_ORDER, sortOrder);
        startMoviePosterLoader(bundle);
    }

    private void startMoviePosterLoader(Bundle bundle) {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<ParcelableMovieDb>> moviePosterLoader
                = loaderManager.getLoader(MOVIE_POSTER_LOADER);
        if (moviePosterLoader == null) {
            loaderManager.initLoader(MOVIE_POSTER_LOADER, bundle, this);
        }
        else {
            loaderManager.restartLoader(MOVIE_POSTER_LOADER, bundle, this);
        }
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
        } else if (id == R.id.app_bar_list_favorites) {
            showFavoriteMovies();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        detailIntent.putExtra(CLICKED_MOVIE_DB_STATE, (Parcelable) mMovies.get(clickedItemIndex));
        startActivity(detailIntent);
    }

    @Override
    public Loader<ArrayList<ParcelableMovieDb>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<ParcelableMovieDb>>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                mLoadingIndicator.setVisibility(View.VISIBLE);
                // load from cache if cache is not empty and sort order is same
                if (mMovies != null && mMovies.size() > 0
                        && mLastSortOrderState.equals(args.getString(SORT_ORDER))) {
                    deliverResult(mMovies);
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                }
                else {
                    forceLoad();
                }
            }

            @Override
            public ArrayList<ParcelableMovieDb> loadInBackground() {
                String sortOrder = args.getString(SORT_ORDER);
                if (sortOrder == null || TextUtils.isEmpty(sortOrder)) {
                    return null;
                }
                mLastSortOrderState = sortOrder;
                List<MovieDb> movieList = new ArrayList<>();
                ArrayList<ParcelableMovieDb> movies = new ArrayList<>();
                Cursor cursor = null;
                switch (sortOrder) {
                    case TOP_RATED:
                        try {
                            MovieResultsPage resultPage =
                                    mMovieUtils.getTopRated(this.getContext(), 0);
                            movieList = resultPage.getResults();
                        } catch (NoInternetConnectionException e) {
                            logNoInternetConnectionException(e);
                        } catch (ApiKeyNotFoundException e) {
                            e.printStackTrace();
                            mApiKeyNotFoundTextView.setVisibility(View.VISIBLE);
                        }
                        // get from database
//                        cursor = getContentResolver().query(
//                                MovieContract.MovieEntry.TOP_RATED,
//                                null,
//                                null,
//                                null,
//                                null);
                        break;
                    case MOST_POPULAR:
                        try {
                            MovieResultsPage resultPage =
                                    mMovieUtils.getMostPopular(this.getContext(), 0);
                            movieList = resultPage.getResults();
                        } catch (NoInternetConnectionException e) {
                            logNoInternetConnectionException(e);
                        } catch (ApiKeyNotFoundException e) {
                            e.printStackTrace();
                            mApiKeyNotFoundTextView.setVisibility(View.VISIBLE);
                        }
                        // get from database
//                        cursor = getContentResolver().query(
//                                MovieContract.MovieEntry.MOST_POPULAR,
//                                null,
//                                null,
//                                null,
//                                null);
                        break;
                    case FAVORITES:
                        cursor = getContentResolver().query(
                                MovieContract.MovieEntry.FAVORITES,
                                null,
                                null,
                                null,
                                MovieContract.MovieEntry.COLUMN_TITLE + " ASC");
                        break;
                }

                // convert to parcelable movieDb from Cursor
                if (cursor != null && cursor.moveToFirst()){
                    mMoviesFromDb = true;
                    while(!cursor.isAfterLast()){
                        movies.add(new ParcelableMovieDb(getBaseContext(), cursor));
                        cursor.moveToNext();
                    }
                }
                else {
                    mMoviesFromDb = false;
                }

                if (cursor != null) {
                    cursor.close();
                }

                // convert to parcelable movieDb from API call results
                if (movieList != null && movieList.size() > 0) {
                    for (MovieDb movie : movieList) {
                        movies.add(new ParcelableMovieDb(getBaseContext(), movie));
                    }
                }
                return movies;
            }

            @Override
            public void deliverResult(ArrayList<ParcelableMovieDb> data) {
                mMovies = data;
                super.deliverResult(data);
            }

            private void logNoInternetConnectionException(NoInternetConnectionException e) {
                String errorText = this.getContext().getString(R.string.no_internet_warning);
                Log.w(TAG, errorText);
                e.printStackTrace();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ParcelableMovieDb>> loader,
                               ArrayList<ParcelableMovieDb> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mNoInternetAccessTextView.setVisibility(View.INVISIBLE);

        // check internet connectivity and display warning if needed
        if (!mMoviesFromDb) {
            if (!NetworkUtils.isOnline(loader.getContext())) {
                mNoInternetAccessTextView.setVisibility(View.VISIBLE);
            }
        }

        // show error message if movies could not be fetched
        if (data == null || data.size() == 0) {
            switch (mLastSortOrderState) {
                case MOST_POPULAR:
                    showErrorMessage(getString(R.string.could_not_fetch_popular_movies));
                    break;
                case TOP_RATED:
                    showErrorMessage(getString(R.string.could_not_fetch_top_rated_movies));
                    break;
                case FAVORITES:
                    showErrorMessage(getString(R.string.could_not_fetch_favorite_movies));
                    break;
            }
        }

        mMovies = data;
        mAdapter = new MovieAdapter(data, mImageWidth, mImageHeight, this);
        mMoviesList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ParcelableMovieDb>> loader) {

    }
}
