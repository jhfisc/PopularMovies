package com.phaosoft.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.phaosoft.android.popularmovies.model.Movie;
import com.phaosoft.android.popularmovies.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private static final int MOVIE_LOADER = 22;

    @BindView(R.id.movie_poster) ImageView moviePoster;
    @BindView(R.id.release_date) TextView releaseDate;
    @BindView(R.id.vote_average) TextView voteAverage;
    @BindView(R.id.description_tv) TextView movieDescription;

    public static final String MOVIE_POSITION = "movie_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);  // bind the UI objects

        String json = "{\n" +
                "      \"vote_count\": 1973,\n" +
                "      \"id\": 299536,\n" +
                "      \"video\": false,\n" +
                "      \"vote_average\": 8.7,\n" +
                "      \"title\": \"Avengers: Infinity War\",\n" +
                "      \"popularity\": 608.376017,\n" +
                "      \"poster_path\": \"/7WsyChQLEftFiDOVTGkv3hFpyyt.jpg\",\n" +
                "      \"original_language\": \"en\",\n" +
                "      \"original_title\": \"Avengers: Infinity War\",\n" +
                "      \"genre_ids\": [\n" +
                "        12,\n" +
                "        878,\n" +
                "        14,\n" +
                "        28\n" +
                "      ],\n" +
                "      \"backdrop_path\": \"/bOGkgRGdhrBYJSLpXaxhXVstddV.jpg\",\n" +
                "      \"adult\": false,\n" +
                "      \"overview\": \"As the Avengers and their allies have continued to protect the world from threats too large for any one hero to handle, a new danger has emerged from the cosmic shadows: Thanos. A despot of intergalactic infamy, his goal is to collect all six Infinity Stones, artifacts of unimaginable power, and use them to inflict his twisted will on all of reality. Everything the Avengers have fought for has led up to this moment - the fate of Earth and existence itself has never been more uncertain.\",\n" +
                "      \"release_date\": \"2018-04-27\"\n" +
                "    }";

        Movie movie = JsonUtils.parseJsonMovie(json);

        setTitle(movie.getTitle());
        Log.i("Movie", movie.getPosterUrl());
        Picasso.with(this)
                .load(movie.getPosterUrl())
                .resize(400, 600)
                .into(moviePoster);
        releaseDate.setText(movie.getDate());
        voteAverage.setText(String.valueOf(movie.getVote()));
        movieDescription.setText(movie.getSynopsis());
    }

}
