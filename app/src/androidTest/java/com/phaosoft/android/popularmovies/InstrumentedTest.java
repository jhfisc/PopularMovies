package com.phaosoft.android.popularmovies;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.phaosoft.android.popularmovies.utils.ImageUtils;
import com.phaosoft.android.popularmovies.utils.NetworkUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.phaosoft.android.popularmovies", appContext.getPackageName());
    }

    @Test
    public void testScaleImage() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        // scaled image --> image
        int width = 10;
        int height = 20;
        Drawable testDrawable = ImageUtils.scaleImage(appContext, R.drawable.image_unavailable,
                width, height);
        assertNotNull(testDrawable);
        assertEquals(width, testDrawable.getIntrinsicWidth());
        assertEquals(height, testDrawable.getIntrinsicHeight());

        // bad width/height --> null
        int bad_size = -100;
        testDrawable = ImageUtils.scaleImage(appContext, R.drawable.powered_by_408x161,
                bad_size, bad_size);
        assertNull(testDrawable);

        // resource not found --> null
        testDrawable = ImageUtils.scaleImage(appContext, 10, width, height);
        assertNull(testDrawable);

        // will not scale --> image
        testDrawable = ImageUtils.scaleImage(appContext, R.mipmap.ic_launcher, width, height);
        assertNotNull(testDrawable);
        assertNotEquals(width, testDrawable.getIntrinsicWidth());
        assertNotEquals(height, testDrawable.getIntrinsicHeight());
    }

    @Test
    public void testBuildQueryString() {
        int page = 1;
        String query = NetworkUtils.buildQueryString("", page);
        assertTrue(query.contains("popular"));
        assertTrue(query.contains("page=" + page));

        page = 4;
        query = NetworkUtils.buildQueryString("Popular", page);
        assertTrue(query.contains("popular"));
        assertTrue(query.contains("page=" + page));

        page = 2;
        query = NetworkUtils.buildQueryString("Highest Rated", page);
        assertTrue(query.contains("top_rated"));
        assertTrue(query.contains("page=" + page));

        page = -1;
        query = NetworkUtils.buildQueryString("Highest Rated", page);
        assertTrue(query.contains("page=" + 1));
    }
}
