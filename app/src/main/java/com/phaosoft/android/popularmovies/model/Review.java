package com.phaosoft.android.popularmovies.model;

/**
 * Review data model module
 */
public class Review {
    private static String reviewer = null;
    private static String review = null;

    public Review() {}

    public Review(String reviewer, String review) {
        this.reviewer = reviewer;
        this.review = review;
    }

    public String getReviewer() { return reviewer; }

    public String getReview() { return review; }

    @Override
    public String toString() { return review + " - " + reviewer; }
}