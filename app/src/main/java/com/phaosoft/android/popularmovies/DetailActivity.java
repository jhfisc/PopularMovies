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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phaosoft.android.popularmovies.model.Movie;
import com.phaosoft.android.popularmovies.model.Review;
import com.phaosoft.android.popularmovies.model.Trailer;
import com.phaosoft.android.popularmovies.utils.ImageUtils;
import com.phaosoft.android.popularmovies.utils.JsonUtils;
import com.phaosoft.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Movie detail module
 */

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.constraint) ConstraintLayout constraintLayout;
    @BindView(R.id.movie_poster) ImageView moviePoster;
    @BindView(R.id.release_date) TextView releaseDate;
    @BindView(R.id.vote_average) TextView voteAverage;
    @BindView(R.id.description_tv) TextView movieDescription;
    @BindView(R.id.favoriteStar) ImageButton favoriteStar;
    @BindView(R.id.trailers) GridLayout trailersGrid;
    @BindView(R.id.trailers_label) TextView trailersLabel;
    @BindView(R.id.reviews_label) TextView reviewsLabel;

    public static final String MOVIE_POSITION = "movie_position";
    private static final int DEFAULT_POSITION = -1;

    // default size for image
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    private static final int MINIMUM_IMAGE_WIDTH = 540;

    private static boolean favorite = false;

    private static final int MOVIE_VIDEO_LOADER = 33;
    private static final String SEARCH_TRAILERS_URL_EXTRA = "trailers";

    private static List<Trailer> trailers = null;

    private static final int MOVIE_REVIEW_LOADER = 44;
    private static final String SEARCH_REVIEW_URL_EXTRA = "reviews";

    private static List<Review> reviewers = null;

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

        // Assume 0 movie index and adjust according to what is stored in the intent
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

        // update layout with the details
        Movie movieDetail = movies.get(position);

        setTitle(movieDetail.getTitle());

        // image_unavailable is too big for the size of the thumbnail, therefore, resize it
        Drawable unavailable = ImageUtils.scaleImage(this,
                R.drawable.image_unavailable, WIDTH, HEIGHT);

        // get the movie image and use the image_unavailable image while loading
        Picasso.with(this)
                .load(movieDetail.getPosterUrl())
                .placeholder(unavailable)
                .resize(WIDTH, HEIGHT)
                .into(moviePoster);

        releaseDate.setText(movieDetail.getDate());

        String vote = String.valueOf(movieDetail.getVote()) +
                getResources().getString(R.string.out_of);
        voteAverage.setText(vote);

        movieDescription.setText(movieDetail.getSynopsis());

        setFavorite();
        favoriteStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favorite = ! favorite;
                setFavorite();
            }
        });

        trailersMovieDB(movieDetail);

        createTrailersLoader();
        createReviewersLoader();
    }

    private LoaderManager.LoaderCallbacks<String> createTrailersLoader() {
        final Context context = getApplicationContext();

        return new LoaderManager.LoaderCallbacks<String>() {
            @NonNull
            @Override
            public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
                final Bundle arguments = args;

                return new AsyncTaskLoader<String>(context) {

                    String trailersJson;

                    @Override
                    protected void onStartLoading() {

                        /* 
                         * If no arguments were passed, we don't have a query to perform. 
                         * Simply return.
                         */
                        if (arguments == null) {
                            return;
                        }

                        /*
                         * When we initially begin loading in the background, we want to display
                         * the loading indicator to the user
                         */

                        if (trailersJson != null) {
                            deliverResult(trailersJson);
                        } else {
                            forceLoad();
                        }
                    }

                    @Override
                    public String loadInBackground() {

                        /* Extract the search query from the args using our constant */
                        String trailersUrlString = null;
                        if (arguments != null) {
                            trailersUrlString = arguments.getString(SEARCH_TRAILERS_URL_EXTRA);
                        }

                        /* If the user didn't enter anything, there's nothing to search for */
                        if (trailersUrlString == null || TextUtils.isEmpty(trailersUrlString)) {
                            return null;
                        }

                        /* Parse the URL from the passed in String and perform the search */
                        try {
                            URL movieUrl = new URL(trailersUrlString);
                            return NetworkUtils.getResponseFromHttpUrl(movieUrl);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public void deliverResult(String data) {
                        trailersJson = data;
                        super.deliverResult(data);
                    }
                };
            }

            @Override
            public void onLoadFinished(@NonNull Loader<String> loader, String data) {
                trailers = JsonUtils.parseJsonTrailers(data);
                if (trailers == null || trailers.isEmpty()) {
                    Log.d("Details onLoadFinished", "trailers null (" + data + ")");
                    trailersLabel.setVisibility(View.INVISIBLE);
                    return;
                }
                trailersLabel.setVisibility(View.VISIBLE);

                Log.d("Details onLoadFinished", trailers.toString());

                int width = trailersGrid.getWidth();
                int total = trailers.size();
                int column = 2;
                while ((width / column) > MINIMUM_IMAGE_WIDTH) {
                    column++;
                }

                int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        (float)5.0, getResources().getDisplayMetrics());
                int imageWidth = Math.max(0, (width - (4 * padding)) / column);
                // a movie poster's height is typically 1 1/2 times the width
                final int imageHeight = Math.max(0, (imageWidth * 2) / 3);

                trailersGrid.removeAllViews();

                int row = total / column;
                trailersGrid.setColumnCount(column);
                trailersGrid.setRowCount(row + 1);

                // image_unavailable is too big for the size of the thumbnails, therefore, resize it
                Drawable unavailable = ImageUtils.scaleImage(context,
                        R.drawable.image_unavailable, imageWidth, imageHeight);
                BitmapDrawable playBitmapDrawable = (BitmapDrawable)ImageUtils.scaleImage(context,
                        R.drawable.play_button_transparent_darkened,
                        imageHeight / 2, imageHeight / 2);
                Bitmap tmpBitmap = null;
                if (playBitmapDrawable != null) {
                    tmpBitmap = playBitmapDrawable.getBitmap();
                }
                final Bitmap playBitmap = tmpBitmap;
                int playSize = imageHeight / 2;
                int centerX = imageWidth / 2;
                int centerY = imageHeight / 2;
                final int playX = centerX - (playSize / 2);
                final int playY = centerY - (playSize / 2);

                // populate the gridlayout with the movie posters
                for (int i = 0; i < total; i++) {
                    final Trailer trailer = trailers.get(i);
                    // create a dynamic image view to hold the image
                    final ImageView dImageView = new ImageView(context);
                    // set the size of the image
                    DrawerLayout.LayoutParams dImageParams =
                            new DrawerLayout.LayoutParams(imageWidth, imageHeight);
                    dImageView.setLayoutParams(dImageParams);
                    dImageView.setPadding(padding, padding, padding, padding);
                    // make it easy to tell which image was selected
                    dImageView.setTag(i);
                    // load the image
                    Picasso.with(context)
                            .load(trailer.getPictureUrl())
                            .resize(imageWidth, imageHeight)
                            .placeholder(unavailable)
                            .into(dImageView,
                                    new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            BitmapDrawable drawable =
                                                    (BitmapDrawable) dImageView.getDrawable();
                                            Bitmap bitmap = drawable.getBitmap();
                                            Canvas canvas = new Canvas(bitmap);
                                            if (playBitmap != null) {
                                                canvas.drawBitmap(playBitmap,
                                                        playX, playY, null);
                                            }
                                            dImageView.setImageBitmap(bitmap);
                                        }

                                        @Override
                                        public void onError() {
                                        }
                                    });

                    dImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int tag = (int)view.getTag();
                            Uri uri = Uri.parse(trailers.get(tag).getVideoUrl());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }
                    });


                    // place the dynamic image into the gridlayout
                    dImageView.setLayoutParams(new GridLayout.LayoutParams());

                    GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
                    GridLayout.Spec colSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
                    GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
                            rowSpan, colSpan);
                    trailersGrid.addView(dImageView, gridParam);
                }
            }

            @Override
            public void onLoaderReset(@NonNull Loader<String> loader) {

            }
        };
    }

    private LoaderManager.LoaderCallbacks<String> createReviewersLoader() {
        final Context context = getApplicationContext();

        return new LoaderManager.LoaderCallbacks<String>() {
            @NonNull
            @Override
            public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
                final Bundle reviewerArguments = args;

                return new AsyncTaskLoader<String>(context) {

                    String reviewersJson;

                    @Override
                    protected void onStartLoading() {

                        /* 
                         * If no arguments were passed, we don't have a query to perform.
                         * Simply return. 
                         */
                        if (reviewerArguments == null) {
                            return;
                        }

                        /*
                         * When we initially begin loading in the background, we want to display 
                         * the loading indicator to the user
                         */

                        if (reviewersJson != null) {
                            deliverResult(reviewersJson);
                        } else {
                            forceLoad();
                        }
                    }

                    @Override
                    public String loadInBackground() {

                        /* Extract the search query from the args using our constant */
                        String reviewersUrlString = null;
                        if (reviewerArguments != null) {
                            reviewersUrlString = 
                                    reviewerArguments.getString(SEARCH_REVIEW_URL_EXTRA);
                        }

                        /* If the user didn't enter anything, there's nothing to search for */
                        if (reviewersUrlString == null || TextUtils.isEmpty(reviewersUrlString)) {
                            return null;
                        }

                        /* Parse the URL from the passed in String and perform the search */
                        try {
                            URL reviewersUrl = new URL(reviewersUrlString);
                            return NetworkUtils.getResponseFromHttpUrl(reviewersUrl);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public void deliverResult(String data) {
                        reviewersJson = data;
                        super.deliverResult(data);
                    }
                };
            }

            @Override
            public void onLoadFinished(@NonNull Loader<String> loader, String data) {
                reviewers = JsonUtils.parseJsonReviewers(data);
                if (reviewers == null || reviewers.isEmpty()) {
                    Log.d("Details onLoadFinished", "reviewers null (" + data + ")");
                    reviewsLabel.setVisibility(View.INVISIBLE);
                    return;
                }
                reviewsLabel.setVisibility(View.VISIBLE);

                Log.d("Details onLoadFinished", reviewers.toString());

                int total = reviewers.size();

                int width = constraintLayout.getWidth();

                LinearLayout verticalLayout = new LinearLayout(context);
                verticalLayout.setId(View.generateViewId());

                LinearLayout.LayoutParams verticalParams =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT, 0);
                verticalLayout.setLayoutParams(verticalParams);
                verticalLayout.setOrientation(LinearLayout.VERTICAL);

                // populate the reviews
                for (int i = 0; i < total; i++) {
                    final Review review = reviewers.get(i);

                    final TextView reviewTextView = new TextView(context);
                    reviewTextView.setId(View.generateViewId());
                    reviewTextView.setText(review.getReview());
                    if ((i % 2) == 0) {
                        reviewTextView.setBackgroundResource(R.color.lightGrey);
                    }
                    reviewTextView.setPadding(20, 50, 20, 25);
                    verticalLayout.addView(reviewTextView);

                    final TextView reviewerTextView = new TextView(context);
                    reviewerTextView.setId(View.generateViewId());
                    reviewerTextView.setText(review.getReviewer());
                    reviewerTextView.setTypeface(null, Typeface.BOLD);
                    if ((i % 2) == 0) {
                        reviewerTextView.setBackgroundResource(R.color.lightGrey);
                    }
                    reviewerTextView.setPadding(Math.max(25, width / 2), 25, 0, 50);
                    verticalLayout.addView(reviewerTextView);
                }

                constraintLayout.addView(verticalLayout);

                int reviewsLabelId = reviewsLabel.getId();
                int verticalID = verticalLayout.getId();
                int parentID = constraintLayout.getId();

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(verticalID, ConstraintSet.TOP,
                        reviewsLabelId, ConstraintSet.BOTTOM);
                constraintSet.connect(verticalID, ConstraintSet.LEFT,
                        parentID, ConstraintSet.LEFT);

                constraintSet.applyTo(constraintLayout);

            }

            @Override
            public void onLoaderReset(@NonNull Loader<String> loader) {

            }
        };
    }

    private void setFavorite() {
        int id = (favorite)?android.R.drawable.star_big_on:android.R.drawable.star_big_off;
        favoriteStar.setImageResource(id);
    }

    private void trailersMovieDB(Movie movie) {
        // build the trailersString
        String trailersString = NetworkUtils.buildVideoString(movie.getID());

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_TRAILERS_URL_EXTRA, trailersString);

        LoaderManager.LoaderCallbacks<String> trailersCallback = createTrailersLoader();
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieSearchLoader = loaderManager.getLoader(MOVIE_VIDEO_LOADER);
        if (movieSearchLoader == null) {
            loaderManager.initLoader(MOVIE_VIDEO_LOADER, queryBundle, trailersCallback);
        } else {
            loaderManager.restartLoader(MOVIE_VIDEO_LOADER, queryBundle, trailersCallback);
        }

        // build the reviewsString
        String reviewersString = NetworkUtils.buildReviewString(movie.getID());

        Bundle trailersQueryBundle = new Bundle();
        trailersQueryBundle.putString(SEARCH_REVIEW_URL_EXTRA, reviewersString);

        LoaderManager.LoaderCallbacks<String> reviewersCallback = createReviewersLoader();
        LoaderManager reviewersLoaderManager = getSupportLoaderManager();
        Loader<String> reviewersSearchLoader = reviewersLoaderManager.getLoader(MOVIE_REVIEW_LOADER);
        if (reviewersSearchLoader == null) {
            reviewersLoaderManager.initLoader(MOVIE_REVIEW_LOADER, trailersQueryBundle, reviewersCallback);
        } else {
            reviewersLoaderManager.restartLoader(MOVIE_REVIEW_LOADER, trailersQueryBundle, reviewersCallback);
        }
    }
}