package com.petrituononen.popularmovies.utilities;

import android.content.Context;
import android.widget.ImageView;

import com.petrituononen.popularmovies.R;
import com.petrituononen.popularmovies.data.ParcelableMovieDb;
import com.squareup.picasso.Picasso;

/**
 * Created by Petri Tuononen on 20.1.2017.
 */
public class PicassoUtils {
    public void loadAlbumArtThumbnail(Context context, ImageView imageView, String imageUrl,
                                      int imageWidth, int imageHeight) {
        Picasso.with(context).load(imageUrl)
                .error(context.getDrawable(R.drawable.movie_poster_error))
                .resize(imageWidth, imageHeight)
                .centerCrop()
                .into(imageView);
    }

    public void loadAlbumArtThumbnail(Context context, ImageView imageView, String imageUrl) {
        Picasso.with(context).load(imageUrl)
                .fit()
                .placeholder(context.getDrawable(R.drawable.movie_poster_placeholder))
                .error(context.getDrawable(R.drawable.movie_poster_error))
                .into(imageView);
    }

    public String formMoviePosterUrl(ParcelableMovieDb movie, Context context) {
        String moviePosterPath = movie.getPosterPath();
        String movieBasePath = context.getString(R.string.themoviedb_api_movie_poster_basepath);
        String moviePosterSize = context.getString(R.string.themoviedb_api_movie_poster_size);
        return movieBasePath + moviePosterSize + moviePosterPath;
    }
}
