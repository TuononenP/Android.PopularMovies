package com.petrituononen.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.petrituononen.popularmovies.data.MovieContract;
import com.petrituononen.popularmovies.data.ParcelableMovieDb;
import com.petrituononen.popularmovies.data.VideoListModel;
import com.petrituononen.popularmovies.utilities.NetworkUtils;
import com.petrituononen.popularmovies.utilities.PicassoUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.Reviews;
import info.movito.themoviedbapi.model.Video;

/**
 * Created by Petri Tuononen on 24.1.2017.
 * Show details of the movie.
 */
public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String CLICKED_MOVIE_DB_STATE = "clicked_movie_db_state";
    private ParcelableMovieDb mMovieDb;
    private List<VideoListModel> mVideoListModels;
    private List<Reviews> mReviews;
    private PicassoUtils mPicassoUtils = new PicassoUtils();
    private boolean mIsFavoriteMovie;
    private LinearLayoutManager mReviewsLayoutManager;
    private LinearLayoutManager mVideosLayoutManager;
    private Cursor mCursor;

    public static final String[] MOVIE_DETAIL_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_FAVORITE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_POSTER_BYTES
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_FAVORITE = 2;
    public static final int INDEX_MOVIE_POSTER = 3;
    public static final int INDEX_MOVIE_SYNOPSIS = 4;
    public static final int INDEX_MOVIE_RATING = 5;
    public static final int INDEX_MOVIE_RELEASE_DATE = 6;
    public static final int INDEX_MOVIE_POPULARITY = 7;
    public static final int INDEX_MOVIE_POSTER_BYTES = 8;

    @BindView(R.id.original_title_textview) TextView mOriginalTitleTextView;
    @BindView(R.id.movie_release_year_textview) TextView mReleaseYearTextView;
    @BindView(R.id.movie_rating_textview) TextView mUserRatingTextView;
    @BindView(R.id.plot_synopsis_textview) TextView mPlotSynopsisTextView;
    @BindView(R.id.movie_thumbnail_imageview) ImageView mMovieThumbnailImageView;
    @BindView(R.id.rv_movie_reviews) RecyclerView mReviewsRecyclerView;
    @BindView(R.id.rv_movie_trailers) RecyclerView mVideosRecyclerView;
    @BindView(R.id.tv_videos_title) TextView mVideoTitleTextView;
    @BindView(R.id.tv_review_title) TextView mReviewsTitleTextView;
    @BindView(R.id.tv_videos_not_found) TextView mVideosNotFoundTextView;
    @BindView(R.id.tv_reviews_not_found) TextView mReviewsNotFoundTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent.hasExtra(CLICKED_MOVIE_DB_STATE)) {
            mMovieDb = intent.getParcelableExtra(CLICKED_MOVIE_DB_STATE);
            if (mMovieDb != null) {
                try {
                    String releaseYear = mMovieDb.getReleaseDate().substring(0,4);
                    mReleaseYearTextView.setText(releaseYear);
                    mOriginalTitleTextView.setText(mMovieDb.getOriginalTitle());
                    String rating = String.format("%1$.1f / 10", mMovieDb.getVoteAverage());
                    mUserRatingTextView.setText(rating);
                    mPlotSynopsisTextView.setText(mMovieDb.getOverview());

                    mCursor = getMovieCursor();
                    if (mCursor != null && mCursor.moveToFirst()) {
                        byte[] posterBytes = mCursor.getBlob(INDEX_MOVIE_POSTER_BYTES);
                        if (posterBytes != null) {
                            Bitmap posterBitmap = BitmapFactory.decodeByteArray(posterBytes, 0, posterBytes.length);
                            mMovieThumbnailImageView.setImageBitmap(posterBitmap);
                        }
                        else {
                            String imageUrl = mPicassoUtils.formMoviePosterUrl(mMovieDb, this);
                            mPicassoUtils.loadAlbumArtThumbnail(this, mMovieThumbnailImageView, imageUrl);
                        }
                    }
                    else {
                        String imageUrl = mPicassoUtils.formMoviePosterUrl(mMovieDb, this);
                        mPicassoUtils.loadAlbumArtThumbnail(this, mMovieThumbnailImageView, imageUrl);
                    }

                    showReviews();
                    showVideos();
                }
                catch (Exception ex) {
                    Log.w(TAG, ex.getMessage());
                }
                if (!NetworkUtils.isOnline(getApplicationContext())) {
                    Toast.makeText(DetailActivity.this,
                            R.string.internet_access_required_to_view_reviews_and_trailers,
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void showReviews() {
        List<Reviews> reviews = mMovieDb.getReviews();
        mReviews = reviews;

        mReviewsLayoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView.setLayoutManager(mReviewsLayoutManager);
        mReviewsRecyclerView.setHasFixedSize(true);
        mReviewsRecyclerView.setNestedScrollingEnabled(false);

        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviews);
        mReviewsRecyclerView.setAdapter(reviewsAdapter);
        reviewsAdapter.notifyDataSetChanged();

        if (reviews.size() == 0) {
            mReviewsNotFoundTextView.setVisibility(View.VISIBLE);
        }
        else {
            mReviewsNotFoundTextView.setVisibility(View.GONE);
        }
    }

    private void showVideos() {
        List<Video> videos = mMovieDb.getVideos();
        List<VideoListModel> videoModels = new ArrayList<>();
        for(Video video : videos) {
            if (video.getSite().toLowerCase().equals("youtube") && video.getType().toLowerCase().equals("trailer")) {
                VideoListModel model = new VideoListModel(video.getName(), formYoutubeUrl(video.getKey()));
                videoModels.add(model);
            }
        }
        mVideoListModels = videoModels;

        mVideosLayoutManager = new LinearLayoutManager(this);
        mVideosRecyclerView.setLayoutManager(mVideosLayoutManager);
        mVideosRecyclerView.setHasFixedSize(true);
        mVideosRecyclerView.setNestedScrollingEnabled(false);

        VideoAdapter videoAdapter = new VideoAdapter(this, videoModels);
        mVideosRecyclerView.setAdapter(videoAdapter);
        videoAdapter.notifyDataSetChanged();

        if (videos.size() == 0) {
            mVideosNotFoundTextView.setVisibility(View.VISIBLE);
        }
        else {
            mVideosNotFoundTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.save_as_favorite_movie:
                mIsFavoriteMovie = !mIsFavoriteMovie;
                Cursor cursor = getMovieCursor();
                // Insert cursor if movie does not exist in db
                if (cursor != null && cursor.moveToFirst() == false) {
                    new InsertCursorTask().execute();
                }
                else {
//                    // delete from db if movie is no more user's favorite
//                    if (!isFavorite) {
//                        getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
//                                MovieContract.MovieEntry.getMovieIdSelection(mMovieDb.getId()), null);
//                    }
                    new UpdateCursorTask().execute();
                }
                // update menu item star icon
                if(mIsFavoriteMovie) {
                    item.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_on, null));
                }
                else {
                    item.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_off, null));
                }
                break;
    }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);

        return true;
    }

    private String formYoutubeUrl(String id) {
        String youtubeBaseUrl = "http://www.youtube.com/watch?v=";
        return youtubeBaseUrl + id;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // change star icon on appbar if movie is saved to db and is favorite
        MenuItem saveAsFavoriteMovieMenuItem = menu.findItem(R.id.save_as_favorite_movie);
        if (mCursor != null && mCursor.moveToFirst()) {
            Boolean isFavorite = mCursor.getInt(INDEX_MOVIE_FAVORITE) == 1;
            mIsFavoriteMovie = isFavorite;
            if (isFavorite) {
                saveAsFavoriteMovieMenuItem.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_on, null));
            }
            else {
                saveAsFavoriteMovieMenuItem.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_off, null));
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Find the current movie from db.
     */
    private Cursor getMovieCursor() {
        Cursor cursor = null;
        if (mMovieDb != null) {
            int movieId = mMovieDb.getId();
            // find movie from database
            Uri uri = MovieContract.MovieEntry.buildMovieUriWithId(movieId);
            cursor = getContentResolver().query(uri, MOVIE_DETAIL_PROJECTION, null, null, null);
        }

        return cursor;
    }

    private class UpdateCursorTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                    mMovieDb.GetContentValues(getApplicationContext(), mIsFavoriteMovie),
                    MovieContract.MovieEntry.getMovieIdSelection(mMovieDb.getId()), null);
            return null;
        }
    }

    private class InsertCursorTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                    mMovieDb.GetContentValues(getApplicationContext(), mIsFavoriteMovie));
            return null;
        }
    }
}
