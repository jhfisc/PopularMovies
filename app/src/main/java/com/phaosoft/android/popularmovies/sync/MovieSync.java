package com.phaosoft.android.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MovieSync {

    /**
     * Performs the network request for updated weather, parses the JSON from that request, and
     * inserts the new weather information into our ContentProvider. Will notify the user that new
     * weather has been loaded if the user hasn't been notified of the weather within the last day
     * AND they haven't disabled notifications in the preferences screen.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncMovie(Context context) {

        try {
            String str = "https://api.themoviedb.org/3/discover/movie?api_key=7d7dc1d96a37db918fc2d52df9ecffad&language=en-US&region=US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&primary_release_date.lte=2018-05-03";
            URL url = new URL(str);

            /* Use the URL to retrieve the JSON */
            String stringResponse = getResponseFromHttpUrl(url);

//            ContentValues[] weatherValues = OpenWeatherJsonUtils
//                    .getWeatherContentValuesFromJson(context, jsonWeatherResponse);

//            if (weatherValues != null && weatherValues.length != 0) {
//
//            }
            Log.i("URL", stringResponse);
        } catch (IOException e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}
