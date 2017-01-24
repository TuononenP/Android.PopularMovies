package com.petrituononen.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.petrituononen.popularmovies.data.ParcelableMovieDb;
import com.petrituononen.popularmovies.utilities.PicassoUtils;

/**
 * Created by Petri Tuononen on 24.1.2017.
 * Show details of the movie.
 */
public class DetailActivity extends AppCompatActivity {

    private static final String CLICKED_MOVIE_DB_STATE = "clicked_movie_db_state";
    private ParcelableMovieDb mMovieDb;

    private static final String TAG = DetailActivity.class.getSimpleName();
    private TextView mOriginalTitleTextView;
    private TextView mReleaseYearTextView;
    private TextView mUserRatingTextView;
    private TextView mPlotSynopsisTextView;
    private ImageView mMovieThumbnailImageView;
    private PicassoUtils mPicassoUtils = new PicassoUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mOriginalTitleTextView = (TextView) findViewById(R.id.original_title_textview);
        mReleaseYearTextView = (TextView) findViewById(R.id.movie_release_year_textview);
        mUserRatingTextView = (TextView) findViewById(R.id.movie_rating_textview);
        mPlotSynopsisTextView = (TextView) findViewById(R.id.plot_synopsis_textview);
        mMovieThumbnailImageView = (ImageView) findViewById(R.id.movie_thumbnail_imageview);

        Intent intent = getIntent();
        if (intent.hasExtra(CLICKED_MOVIE_DB_STATE)) {
            mMovieDb = intent.getParcelableExtra(CLICKED_MOVIE_DB_STATE);
            if (mMovieDb != null) {
                try {
                    String releaseYear = mMovieDb.getReleaseDate().substring(0,4);
                    mReleaseYearTextView.setText(releaseYear);
                    mOriginalTitleTextView.setText(mMovieDb.getOriginalTitle());
                    mUserRatingTextView.setText(String.valueOf(mMovieDb.getVoteAverage()) + "/10");
                    mPlotSynopsisTextView.setText(mMovieDb.getOverview());

                    String imageUrl = mPicassoUtils.formMoviePosterUrl(mMovieDb, this);
                    mPicassoUtils.loadAlbumArtThumbnail(this, mMovieThumbnailImageView, imageUrl);
                }
                catch (Exception ex) {
                    Log.w(TAG, ex.getMessage());
                }
            }
        }
    }
}
