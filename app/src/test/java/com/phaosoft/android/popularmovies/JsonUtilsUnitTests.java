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
import com.phaosoft.android.popularmovies.utils.JsonUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
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
    private static final String TWO_MOVIES = "{\"page\":1,\"total_results\":19833," +
            "\"total_pages\":992,\"results\":[{\"vote_count\":3652,\"id\":299536,\"video\":" +
            "false,\"vote_average\":8.5,\"title\":\"Avengers: Infinity War\",\"popularity\":" +
            "575.963952,\"poster_path\":\"\\/7WsyChQLEftFiDOVTGkv3hFpyyt.jpg\"," +
            "\"original_language\":\"en\",\"original_title\":\"Avengers: Infinity War\"," +
            "\"genre_ids\":[12,878,14,28],\"backdrop_path\":" +
            "\"\\/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg\",\"adult\":false," +
            "\"overview\":\"more uncertain.\",\"release_date\":\"2018-04-25\"},{\"vote_count\":" +
            "3652,\"id\":299536,\"video\":false,\"vote_average\":8.5,\"title\":" +
            "\"Avengers: Infinity War\",\"popularity\":575.963952,\"poster_path\":" +
            "\"\\/7WsyChQLEftFiDOVTGkv3hFpyyt.jpg\",\"original_language\":\"en\"," +
            "\"original_title\":\"Avengers: Infinity War\",\"genre_ids\":[12,878,14,28]," +
            "\"backdrop_path\":\"\\/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg\",\"adult\":false," +
            "\"overview\":\"existence itself\",\"release_date\":\"2018-04-25\"}]}";

    private static final String A_MOVIE = "{\"vote_average\":8.5,\"title\":" +
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
        PowerMockito.mockStatic(Log.class);

        List<Movie> movie = JsonUtils.parseJsonMovie(A_MOVIE);

        assertNotNull(movie);
        assertEquals(1, movie.size());

    }

    @Test
    public void jsonUtilsNullMovie() {
        List<Movie> movie = JsonUtils.parseJsonMovie(NULL_MOVIE);

        assertEquals(null, movie);

        movie = JsonUtils.parseJsonMovie(ONLY_RESULTS);
        assertEquals(null, movie);

        movie = JsonUtils.parseJsonMovie(ONLY_TITLE);
        assertEquals(null, movie);
    }
}
