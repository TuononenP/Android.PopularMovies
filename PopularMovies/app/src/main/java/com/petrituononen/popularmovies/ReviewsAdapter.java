package com.petrituononen.popularmovies;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.petrituononen.popularmovies.R;
import com.petrituononen.popularmovies.utilities.HtmlUtilities;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.Reviews;

/**
 * Created by Petri Tuononen on 5.3.2017.
 */

public class ReviewsAdapter extends BaseAdapter {

    private List<Reviews> mReviewsList;

    public ReviewsAdapter(List<Reviews> reviews) {
        mReviewsList = reviews;
    }

    @Override
    public int getCount() {
        return mReviewsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mReviewsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.review_item, parent, false);
        TextView content = (TextView)row.findViewById(R.id.tv_review_content);
        content.setText(mReviewsList.get(position).getContent());
        TextView author = (TextView)row.findViewById(R.id.tv_review_author);
        Spanned authorHtml = HtmlUtilities.fromHtml("<b>"+ mReviewsList.get(position).getAuthor() + "<b/>");
        author.setText(context.getString(R.string.review_by) + " " + authorHtml);

        return (row);
    }
}
