/*
   Copyright 2018 John Fischer

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package com.phaosoft.android.popularmovies.model;

/**
 * Movie data model module
 */

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

    public String toString() {
        return this.title + ":" + this.date + ":" + this.vote;
    }

    /**
     * Compare this movie with another movie to see if they are the same.
     *
     * @param comp the movie to compare
     * @return true if the movies are identical, otherwise false.
     */
    public boolean equals(Movie comp) {
        return this.title.equals(comp.getTitle()) &&
                this.date.equals(comp.getDate()) &&
                this.posterUrl.equals(comp.getPosterUrl()) &&
                this.synopsis.equals(comp.getSynopsis()) &&
                Math.abs(this.vote) == Math.abs(comp.vote);
    }
}
