package com.phaosoft.android.popularmovies.model;

public class Movie {
    private String title;
    private String date;
    private String posterUrl;
    private String synopsis;
    private Double vote;

    public Movie() { }
    public Movie(String title, String date, String posterUrl, Double vote, String synopsis) {
        this.title = title;
        this.date = date;
        this.posterUrl = posterUrl;
        this.vote = vote;
        this.synopsis = synopsis;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public Double getVote() {
        return vote;
    }
}
