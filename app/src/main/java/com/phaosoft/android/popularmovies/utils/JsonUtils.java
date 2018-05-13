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

import android.util.Log;

import com.phaosoft.android.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    final static String BASE_URL = "https://image.tmdb.org/t/p/w500";

    public static List<Movie> parseJsonMovie(String json) {
        JSONObject jobj = null;

        try {
            jobj = new JSONObject(json);
            Movie current;
            List<Movie> movies = new ArrayList<>();

            if (jobj.has("results")) {
                JSONArray results = jobj.getJSONArray("results");
                int len = results.length();
                for (int i = 0; i < len; i++) {
                    JSONObject result = (JSONObject)results.get(i);
                    movies.add(new Movie(result.getString("title"),
                            result.getString("release_date"),
                            BASE_URL + result.getString("poster_path"),
                            result.getDouble("vote_average"),
                            result.getString("overview")));
                }

                return movies;
            } else {
                movies.add(new Movie(jobj.getString("title"),
                        jobj.getString("release_date"),
                        BASE_URL + jobj.getString("poster_path"),
                        jobj.getDouble("vote_average"),
                        jobj.getString("overview")));

                return movies;
            }
        } catch (JSONException e) {
            Log.e("JsonUtils", e.getMessage());
            return null;
        }
    }

}