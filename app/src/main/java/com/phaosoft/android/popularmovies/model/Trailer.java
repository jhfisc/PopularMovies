package com.phaosoft.android.popularmovies.model;

/**
 * Trailers data model module
 */

public class Trailer {
    private String key = null;
    private String name = null;
    private String videoUrl = null;
    private String pictureUrl = null;

    public Trailer() {}
    public Trailer(String key, String name, String videoUrl, String pictureUrl) {
        this.key = key;
        this.name = name;
        this.videoUrl = videoUrl;
        this.pictureUrl = pictureUrl;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    @Override
    public String toString() {
        return this.key + ":" + this.name + ":" + pictureUrl;
    }
}
