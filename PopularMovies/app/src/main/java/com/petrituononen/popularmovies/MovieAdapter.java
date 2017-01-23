package com.petrituononen.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.petrituononen.popularmovies.utilities.PicassoUtils;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by Petri Tuononen on 22.1.2017.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final String TAG = MovieAdapter.class.getSimpleName();
    private PicassoUtils mPicassoUtils = new PicassoUtils();
    private int mNumberOfItems;
    private List<MovieDb> mMovies;
    private Context mContext;
    private static int mImageWidth;
    private static int mImageHeight;
    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MovieAdapter(List<MovieDb> movies, int imageWidth, int imageHeight,
                        ListItemClickListener listener) {
        if (movies != null) {
            mMovies = movies;
            mNumberOfItems = movies.size();
        }
        else {
            mMovies = new ArrayList<MovieDb>();
        }
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
        mOnClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        mContext = context;
        int layoutIdForListItem = R.layout.movie_poster_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mNumberOfItems;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        MovieDb movie = mMovies.get(position);
        holder.bind(movie, mContext);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mMoviePosterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mMoviePosterImageView = (ImageView)itemView.findViewById(R.id.iv_movie_poster_item);
            itemView.setOnClickListener(this);
        }

        /**
         * This method will take a MovieDb as input and
         * displays the appropriate movie poster within a list item.
         * @param movie
         * @param context
         */
        void bind(MovieDb movie, Context context) {
            String moviePosterUrl = formMoviePosterUrl(movie, context);
            mPicassoUtils.loadAlbumArtThumbnail(context, mMoviePosterImageView,
                    moviePosterUrl, mImageWidth, mImageHeight);
        }

        String formMoviePosterUrl(MovieDb movie, Context context) {
            String moviePosterPath = movie.getPosterPath();
            String movieBasePath = context.getString(R.string.themoviedb_api_movie_poster_basepath);
            String moviePosterSize = context.getString(R.string.themoviedb_api_movie_poster_size);
            String moviePosterUrl = movieBasePath + moviePosterSize + moviePosterPath;
            return moviePosterUrl;
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }
}
