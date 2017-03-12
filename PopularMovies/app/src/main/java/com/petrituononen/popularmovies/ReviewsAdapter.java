package com.petrituononen.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.petrituononen.popularmovies.utilities.HtmlUtilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.movito.themoviedbapi.model.Reviews;

/**
 * Created by Petri Tuononen on 12.3.2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private Context mContext;
    List<Reviews> mReviews;
    private int mNumberOfItems;

    public ReviewsAdapter(List<Reviews> reviews) {
        if (reviews != null) {
            mReviews = reviews;
            mNumberOfItems = reviews.size();
        }
        else {
            mReviews = new ArrayList<>();
        }
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        mContext = context;
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewsAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Reviews review = mReviews.get(position);
        holder.bind(review, mContext);
    }

    @Override
    public int getItemCount() {
        return mNumberOfItems;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_review_content) TextView mReviewContentTextView;
        @BindView(R.id.tv_review_author) TextView mReviewAuthorTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Reviews review, Context context) {
            mReviewContentTextView.setText(review.getContent());
            Spanned authorHtml = HtmlUtilities.fromHtml("<b>"+ review.getAuthor() + "<b/>");
            mReviewAuthorTextView.setText(context.getString(R.string.review_by) + " " + authorHtml);
        }
    }
}
