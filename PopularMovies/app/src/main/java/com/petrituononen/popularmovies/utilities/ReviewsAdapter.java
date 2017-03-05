package com.petrituononen.popularmovies.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.petrituononen.popularmovies.R;

/**
 * Created by Pepe on 5.3.2017.
 */

public class ReviewsAdapter extends BaseAdapter {

    private String[] mReviews;

    public ReviewsAdapter(String[] reviews) {
        mReviews = reviews;
    }

    @Override
    public int getCount() {
        return mReviews.length;
    }

    @Override
    public Object getItem(int position) {
        return mReviews[position];
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
        TextView title = (TextView)row.findViewById(R.id.tv_review_item_title);
        title.setText(mReviews[position]);

        return (row);
    }
}
