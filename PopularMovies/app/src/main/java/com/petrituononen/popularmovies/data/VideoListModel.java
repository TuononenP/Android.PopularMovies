package com.petrituononen.popularmovies.data;

/**
 * Created by petri on 22.2.2017.
 */

public class VideoListModel {

    public VideoListModel(String title, String url) {
        this.title = title;
        this.url = url;
    }

    private String title;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
