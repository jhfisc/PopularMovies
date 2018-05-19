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

package com.phaosoft.android.popularmovies;

import com.phaosoft.android.popularmovies.model.Movie;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Movie class Unit Tests
 */

public class MovieUnitTests {
    private String testTitle = "Movie Title";
    private String testDate = "12-10-2014";
    private String testPoster = "/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg";
    private String testSynopsis = "This is a synopsis";
    private double testVote = 7.6;

    @Test
    public void testGetters() {
        Movie movie = new Movie(testTitle, testDate, testPoster, testVote, testSynopsis);

        assertTrue(movie.getTitle().equals(testTitle));
        assertTrue(movie.getDate().equals(testDate));
        assertTrue(movie.getPosterUrl().equals(testPoster));
        assertTrue(movie.getSynopsis().equals(testSynopsis));
        assertTrue(Math.abs(movie.getVote()) == Math.abs(testVote));
    }

    @Test
    public void testEquals() {
        Movie movie = new Movie(testTitle, testDate, testPoster, testVote, testSynopsis);

        Movie same = new Movie(testTitle, testDate, testPoster, testVote, testSynopsis);
        Movie notSame = new Movie("A", "B", "C", 5.5, "D");

        assertTrue(movie.equals(same));
        assertFalse(movie.equals(notSame));
    }

    @Test
    public void testToString() {
        Movie movie = new Movie(testTitle, testDate, testPoster, testVote, testSynopsis);

        assertTrue(movie.toString().equals(testTitle + ":" + testDate + ":" + testVote));
    }
}
