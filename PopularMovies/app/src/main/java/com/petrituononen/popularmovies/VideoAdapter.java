package com.petrituononen.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.petrituononen.popularmovies.data.VideoListModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Petri Tuononen on 12.3.2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    List<VideoListModel> mVideos;
    Context mContext;
    final private VideoAdapter.ListItemClickListener mOnClickListener;

    public VideoAdapter(Context context, List<VideoListModel> videos, VideoAdapter.ListItemClickListener listener) {
        if (videos != null) {
            mVideos = videos;
        }
        else {
            mVideos = new ArrayList<>();
        }
        mOnClickListener = listener;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        mContext = context;
        int layoutIdForListItem = R.layout.video_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new VideoAdapter.VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        VideoListModel video = mVideos.get(position);
        holder.bind(video);
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_video_title) TextView mVideoTitle;
        @BindView(R.id.tv_youtube_uri) TextView mYoutubeUriTextView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(VideoListModel video) {
            mVideoTitle.setText(video.getTitle());
            mYoutubeUriTextView.setText(video.getUrl());
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }
}
