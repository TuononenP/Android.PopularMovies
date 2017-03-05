package com.petrituononen.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.petrituononen.popularmovies.data.VideoListModel;

import java.util.List;

/**
 * Created by Petri Tuononen on 22.2.2017.
 */

public class VideoListAdapter extends BaseAdapter {
    Context mContext;
    List<VideoListModel> mRowItems;

    public VideoListAdapter(Context context, List<VideoListModel> items) {
        this.mContext = context;
        this.mRowItems = items;
    }

    private class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvUrl;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.video_list_item, null);
            holder = new ViewHolder();
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_video_title);
            holder.tvUrl = (TextView) convertView.findViewById(R.id.tv_youtube_uri);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_video_image);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        VideoListModel rowItem = (VideoListModel) getItem(position);

        holder.tvTitle.setText(rowItem.getTitle());
        holder.tvUrl.setText(rowItem.getUrl());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(mRowItems.get(position).getUrl())));
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return mRowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mRowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mRowItems.indexOf(getItem(position));
    }
}