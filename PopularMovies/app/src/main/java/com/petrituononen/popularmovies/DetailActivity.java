package com.petrituononen.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.petrituononen.popularmovies.data.ParcelableMovieDb;
import com.petrituononen.popularmovies.utilities.PicassoUtils;

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

    @BindView(R.id.original_title_textview) TextView mOriginalTitleTextView;
    @BindView(R.id.movie_release_year_textview) TextView mReleaseYearTextView;
    @BindView(R.id.movie_rating_textview) TextView mUserRatingTextView;
    @BindView(R.id.plot_synopsis_textview) TextView mPlotSynopsisTextView;
    @BindView(R.id.movie_thumbnail_imageview) ImageView mMovieThumbnailImageView;

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

                    List<Reviews> reviews = mMovieDb.getReviews();
                    List<Video> videos = mMovieDb.getVideos();
                    // TODO: show reviews and videos

                    // TODO: Set star icon to on state if movie is favorite
                }
                catch (Exception ex) {
                    Log.w(TAG, ex.getMessage());
                }
            }
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
}
