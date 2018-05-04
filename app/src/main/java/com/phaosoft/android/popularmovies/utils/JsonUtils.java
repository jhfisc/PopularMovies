package com.phaosoft.android.popularmovies.utils;

import android.util.Log;

import com.phaosoft.android.popularmovies.model.Movie;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
    final static String BASE_URL = "https://image.tmdb.org/t/p/w500";

    public static Movie parseJsonMovie(String json) {
        JSONObject jobj = null;

        try {
            jobj = new JSONObject(json);
            return new Movie(jobj.getString("title"), jobj.getString("release_date"),
                    BASE_URL + jobj.getString("poster_path"),
                    jobj.getDouble("vote_average"), jobj.getString("overview"));
        } catch (JSONException e) {
            Log.e("JsonUtils", e.getMessage());
            return null;
        }
    }
}