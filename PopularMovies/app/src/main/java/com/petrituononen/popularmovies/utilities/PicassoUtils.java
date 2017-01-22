package com.petrituononen.popularmovies.utilities;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Petri Tuononen on 20.1.2017.
 */
public class PicassoUtils {
    public void loadAlbumArtThumbnail(Context context, ImageView imageView, String imageUrl) {
        Picasso.with(context).load(imageUrl)
                .fit()
//                .placeholder(R.drawable.movie_poster_placeholder)
//                .error(R.drawable.movie_poster_placeholder_error)
                .into(imageView);
    }
}
