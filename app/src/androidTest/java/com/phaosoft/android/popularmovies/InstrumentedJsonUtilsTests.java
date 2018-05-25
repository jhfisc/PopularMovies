package com.phaosoft.android.popularmovies;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.phaosoft.android.popularmovies.model.Trailer;
import com.phaosoft.android.popularmovies.utils.ImageUtils;
import com.phaosoft.android.popularmovies.utils.JsonUtils;
import com.phaosoft.android.popularmovies.utils.NetworkUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedJsonUtilsTests {

    private static final String TWO_TRAILERS =
            "{\"id\":\"299536\",\"results\":" +
                    "[{\"id\":\"5a200baa925141033608f5f0\",\"iso_639_1\":\"en\"," +
                    "\"iso_3166_1\":\"US\",\"key\":\"6ZfuNTqbHE8\"," +
                    "\"name\":\"Official Trailer\",\"site\":\"YouTube\",\"size\":1080," +
                    "\"type\":\"Trailer\"}," +
                    "{\"id\":\"5a200bcc925141032408d21b\",\"iso_639_1\":\"en\"," +
                    "\"iso_3166_1\":\"US\",\"key\":\"sAOzrChqmd0\"," +
                    "\"name\":\"Action...Avengers: Infinity War\",\"site\":\"YouTube\"," +
                    "\"size\":720,\"type\":\"Clip\"}]}";
    private static final String A_TRAILER =
            "{\"id\":\"5a200baa925141033608f5f0\",\"iso_639_1\":\"en\"," +
            "\"iso_3166_1\":\"US\",\"key\":\"6ZfuNTqbHE8\"," +
            "\"name\":\"Official Trailer\",\"site\":\"YouTube\",\"size\":1080," +
            "\"type\":\"Trailer\"},";

    private static final String NULL_TRAILER = "{}";
    private static final String ONLY_TRAILER_RESULTS = "{\"results\": 1}";
    private static final String ONLY_TRAILER_TITLE = "{\"title\": 1}";

    @Test
    public void jsonUtilsTrailers() {
        List<Trailer> trailers = JsonUtils.parseJsonTrailers(TWO_TRAILERS);

        assertNotNull(trailers);
        assertEquals(2, trailers.size());
    }

    @Test
    public void jsonUtilsTrailer() {
        List<Trailer> trailers = JsonUtils.parseJsonTrailers(A_TRAILER);

        assertNotNull(trailers);
        assertEquals(1, trailers.size());

    }

    @Test
    public void jsonUtilsNullTrailer() {
        List<Trailer> trailer = JsonUtils.parseJsonTrailers(NULL_TRAILER);
        assertNull(trailer);

        trailer = JsonUtils.parseJsonTrailers(ONLY_TRAILER_RESULTS);
        assertNull(trailer);

        trailer = JsonUtils.parseJsonTrailers(ONLY_TRAILER_TITLE);
        assertNull(trailer);

    }
}
