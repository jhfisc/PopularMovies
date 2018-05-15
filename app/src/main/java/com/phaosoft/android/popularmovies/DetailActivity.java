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

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.phaosoft.android.popularmovies.model.Movie;
import com.phaosoft.android.popularmovies.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.movie_poster) ImageView moviePoster;
    @BindView(R.id.release_date) TextView releaseDate;
    @BindView(R.id.vote_average) TextView voteAverage;
    @BindView(R.id.description_tv) TextView movieDescription;

    public static final String MOVIE_POSITION = "movie_position";
    private static final int DEFAULT_POSITION = -1;

    // default size for image
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);  // bind the UI objects

        Intent intent = getIntent();
        if (intent == null) {
            Log.d("Detail", "no intent for details");
            finish();
        }

        // Assume 0 movie index and adjuest according to what is store in the intent
        int position = 0;
        try {
            if (intent != null) {
                position = intent.getIntExtra(MOVIE_POSITION, DEFAULT_POSITION);
            }
        } catch (NullPointerException e) {
            Log.d("Detail", e.getMessage());
            finish();
        }

        List<Movie> movies = MainActivity.movies;
        if (movies == null) {
            Log.d("Detail", "no movies for details");
            finish();
            return;
        }

        // update layout with details
        Movie movieDetail = movies.get(position);

        setTitle(movieDetail.getTitle());

        // get and scale the blank image
        Drawable unavailable = ImageUtils.scaleImage(this,
                R.drawable.image_unavailable, WIDTH, HEIGHT);
        // get the movie image and use the blank image while loading
        Picasso.with(this)
                .load(movieDetail.getPosterUrl())
                .placeholder(unavailable)
                .resize(WIDTH, HEIGHT)
                .into(moviePoster);

        releaseDate.setText(movieDetail.getDate());

        voteAverage.setText(String.valueOf(movieDetail.getVote()));

        movieDescription.setText(movieDetail.getSynopsis());
    }

}