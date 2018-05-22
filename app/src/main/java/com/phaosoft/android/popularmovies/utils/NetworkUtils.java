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

package com.phaosoft.android.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.phaosoft.android.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Network utilities module
 */
public class NetworkUtils {
    private static final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String PARAM_KEY = "api_key";
    private static final String PARAM_PAGE = "page";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String API_KEY = BuildConfig.API_KEY;

    private final static String TRAILERS_URL = "https://www.youtube.com/watch";
    private final static String PARAM_TRAILER = "v";

    private final static String TRAILER_KEY = "%TRAILER_KEY%";
    private final static String TRAILER_IMAGE = "https://img.youtube.com/vi/" +  TRAILER_KEY + "/default.jpg";

    /**
     * check if the network is available.
     *
     * @param context for the current view
     * @return true|false depending if the network is up or not.
     */
    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (manager != null) {
                NetworkInfo network = manager.getActiveNetworkInfo();
                return network.isAvailable() && network.isConnected();
            }
        } catch (NullPointerException e) {
            // ignore exception and fall through to returning false
        }
        return false;
    }

    /**
     * Gets the response from an HTTP URL request.
     *
     * @param url a URL to open
     * @return the HTTP request response
     * @throws IOException when the connection fails
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (UnknownHostException e) {
            // ignore unknown host exception, most likely a device configuration issue
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Builds a query string based upon the user selection and desired page number.
     *
     * @param sort sort title [Highest Rated, Most Popular]
     * @param page desired page to retrieve
     * @return url String
     */
    public static String buildQueryString(String sort, int page) {
        String sorter = POPULAR;
        if (sort.equals("Highest Rated")) {
            sorter = TOP_RATED;
        }

        // ensure that the page number is equal or greater then 1
        int queryPage = Math.max(1, page);

        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL + sorter).buildUpon()
                .appendQueryParameter(PARAM_KEY, API_KEY)
                .appendQueryParameter(PARAM_PAGE, Integer.toString(queryPage))
                .build();

        Log.d("buildQueryString", "Uri: " + builtUri.toString());

        return builtUri.toString();
    }

    /**
     * Builds a trailer video query string based upon the movie ID.
     *
     * @param movieID movieDB ID
     * @return url String
     */
    public static String buildVideoString(String movieID) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL + movieID + "/videos").buildUpon()
                .appendQueryParameter(PARAM_KEY, API_KEY)
                .build();

        Log.d("buildVideoString", "Uri: " + builtUri.toString());

        return builtUri.toString();
    }

    /**
     * Builds a reviewer query string based upon the movie ID.
     *
     * @param movieID movieDB ID
     * @return url String
     */
    public static String buildReviewString(String movieID) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL + movieID + "reviews").buildUpon()
                .appendQueryParameter(PARAM_KEY, API_KEY)
                .build();

        Log.d("buildVideoString", "Uri: " + builtUri.toString());

        return builtUri.toString();
    }

    public static String buildTrailerString(String trailerKey) {
        Uri builtUri = Uri.parse(TRAILERS_URL).buildUpon()
                .appendQueryParameter(PARAM_TRAILER, trailerKey)
                .appendQueryParameter(PARAM_KEY, API_KEY)
                .build();

        Log.d("buildVideoString", "Uri: " + builtUri.toString());

        return builtUri.toString();
    }

    public static String buildTrailerThumbnailString(String trailerKey) {
        String trailerThumbnail = TRAILER_IMAGE.replace(TRAILER_KEY, trailerKey);
        Log.d("buildTrailerThumbnailString", "Uri: " + trailerThumbnail);

        return trailerThumbnail;
    }
}
