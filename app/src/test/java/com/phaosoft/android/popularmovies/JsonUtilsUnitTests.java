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

import android.util.Log;

import com.phaosoft.android.popularmovies.model.Movie;
import com.phaosoft.android.popularmovies.model.Trailer;
import com.phaosoft.android.popularmovies.utils.JsonUtils;
import com.phaosoft.android.popularmovies.utils.NetworkUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * JsonUnitls class Unit Tests
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class JsonUtilsUnitTests {
    private static final String TWO_MOVIES =
            "{\"page\":1,\"total_results\":19894,\"total_pages\":995,\"results\":" +
                    "[{\"vote_count\":3745,\"id\":\"299536\",\"video\":false," +
                    "\"vote_average\":8.5,\"title\":\"Avengers: Infinity War\"," +
                    "\"popularity\":577.421593," +
                    "\"poster_path\":\"\\/7WsyChQLEftFiDOVTGkv3hFpyyt.jpg\"," +
                    "\"original_language\":\"en\",\"original_title\":\"Avengers: Infinity War\"," +
                    "\"genre_ids\":[12,878,14,28]," +
                    "\"backdrop_path\":\"\\/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg\",\"adult\":false," +
                    "\"overview\":\"As the Avengers and their allies have continued to protect " +
                    "the world from threats too large for any one hero to handle, a new danger " +
                    "has emerged from the cosmic shadows: Thanos. A despot of intergalactic " +
                    "infamy, his goal is to collect all six Infinity Stones, artifacts of " +
                    "unimaginable power, and use them to inflict his twisted will on all of " +
                    "reality. Everything the Avengers have fought for has led up to this moment " +
                    "- the fate of Earth and existence itself has never been more uncertain.\"," +
                    "\"release_date\":\"2018-04-25\"}," +
                    "{\"vote_count\":8,\"id\":\"510819\",\"video\":false,\"vote_average\":2.9," +
                    "\"title\":\"Dirty Dead Con Men\",\"popularity\":544.737128," +
                    "\"poster_path\":\"\\/r70GGoZ5PqqokDDRnVfTN7PPDtJ.jpg\"," +
                    "\"original_language\":\"en\",\"original_title\":\"Dirty Dead Con Men\"," +
                    "\"genre_ids\":[28,80,18]," +
                    "\"backdrop_path\":\"\\/75RJi3yVZnZtVj4Kn1bYGzkhiEx.jpg\",\"adult\":false," +
                    "\"overview\":\"A cool and dangerous neo-noir crime film that revolves " +
                    "around the disturbed lives of two unlikely partners: Mickey Rady, a " +
                    "rogue undercover cop and Kook Packard, a smooth and charismatic con man. " +
                    "Together they rip off those operating outside of the law...for their own " +
                    "personal gain. But things go awry when one heist suck them deep into a " +
                    "city-wide conspiracy...\",\"release_date\":\"2018-03-30\"}]}";

    private static final String A_MOVIE = "{\"id\":\"299536\",\"vote_average\":8.5,\"title\":" +
            "\"Avengers: Infinity War\",\"popularity\":575.963952," +
            "\"poster_path\":\"\\/7WsyChQLEftFiDOVTGkv3hFpyyt.jpg\"," +
            "\"original_language\":\"en\",\"original_title\":\"Avengers: Infinity War\"," +
            "\"genre_ids\":[12,878,14,28],\"backdrop_path\":" +
            "\"\\/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg\",\"adult\":false,\"overview\":" +
            "\"fate of Earth\",\"release_date\":\"2018-04-25\"}";

    private static final String NULL_MOVIE = "{}";
    private static final String ONLY_RESULTS = "{\"results\": 1}";
    private static final String ONLY_TITLE = "{\"title\": 1}";

    @Test
    public void jsonUtilsMovies() {
        List<Movie> movies = JsonUtils.parseJsonMovie(TWO_MOVIES);

        assertNotNull(movies);
        assertEquals(2, movies.size());
    }

    @Test
    public void jsonUtilsMovie() {
        List<Movie> movie = JsonUtils.parseJsonMovie(A_MOVIE);

        assertNotNull(movie);
        assertEquals(1, movie.size());

    }

    @Test
    public void jsonUtilsNullMovie() {
        List<Movie> movie = JsonUtils.parseJsonMovie(NULL_MOVIE);
        assertNull(movie);

        movie = JsonUtils.parseJsonMovie(ONLY_RESULTS);
        assertNull(movie);

        movie = JsonUtils.parseJsonMovie(ONLY_TITLE);
        assertNull(movie);
    }
}
