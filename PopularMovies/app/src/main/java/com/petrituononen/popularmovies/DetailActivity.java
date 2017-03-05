package com.petrituononen.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.petrituononen.popularmovies.data.MovieContract;
import com.petrituononen.popularmovies.data.ParcelableMovieDb;
import com.petrituononen.popularmovies.data.VideoListModel;
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
    private PicassoUtils mPicassoUtils = new PicassoUtils();

    public static final String[] MOVIE_DETAIL_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_FAVORITE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_FAVORITE = 2;
    public static final int INDEX_MOVIE_POSTER = 3;
    public static final int INDEX_MOVIE_SYNOPSIS = 4;
    public static final int INDEX_MOVIE_RATING = 5;
    public static final int INDEX_MOVIE_RELEASE_DATE = 6;

    @BindView(R.id.original_title_textview) TextView mOriginalTitleTextView;
    @BindView(R.id.movie_release_year_textview) TextView mReleaseYearTextView;
    @BindView(R.id.movie_rating_textview) TextView mUserRatingTextView;
    @BindView(R.id.plot_synopsis_textview) TextView mPlotSynopsisTextView;
    @BindView(R.id.movie_thumbnail_imageview) ImageView mMovieThumbnailImageView;
    @BindView(R.id.lw_reviews) ListView mReviewsListView;
    @BindView(R.id.lw_videos) ListView mVideosListView;
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

                    String imageUrl = mPicassoUtils.formMoviePosterUrl(mMovieDb, this);
                    mPicassoUtils.loadAlbumArtThumbnail(this, mMovieThumbnailImageView, imageUrl);

                    showReviews();

                    showVideos();

                    // TODO: Set star icon to on state if movie is favorite
//                    int movieId = mMovieDb.getId();
//                    // find movie from database
//                    Uri uri = MovieContract.MovieEntry.buildMovieUriWithId(movieId);
//                    Cursor cursor = getContentResolver().query(uri, MOVIE_DETAIL_PROJECTION, null, null, null);
//                    if (cursor != null && cursor.moveToFirst()) {
//                        if (cursor.getInt(INDEX_MOVIE_ID) == movieId) {
//                            String title = cursor.getString(INDEX_MOVIE_TITLE);
//                            Boolean isFavorite = cursor.getInt(INDEX_MOVIE_FAVORITE) == 1;
//                            String poster = cursor.getString(INDEX_MOVIE_POSTER);
//                            String synopsis = cursor.getString(INDEX_MOVIE_SYNOPSIS);
//                            float ratingFloat = cursor.getFloat(INDEX_MOVIE_RATING);
//                            String releaseDate = cursor.getString(INDEX_MOVIE_RELEASE_DATE);
//                        }
//                    }
//                    else {
//                        // add if does not exists
//                        getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, mMovieDb.GetContentValues(getBaseContext(), true));
//                    }
//                    getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, mMovieDb.GetContentValues(getBaseContext(), true));
                }
                catch (Exception ex) {
                    Log.w(TAG, ex.getMessage());
                }
            }
        }
    }

    private void showReviews() {
        List<Reviews> reviews = mMovieDb.getReviews();

        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviews);
        mReviewsListView.setAdapter(reviewsAdapter);

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

        VideoListAdapter videosAdapter = new VideoListAdapter(this, videoModels);
        mVideosListView.setAdapter(videosAdapter);

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
                finish();
                return true;
            case R.id.save_as_favorite_movie:
                //TODO: Implement saving favorite movie

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
}
