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
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.phaosoft.android.popularmovies.model.Movie;
import com.phaosoft.android.popularmovies.utils.ImageUtils;
import com.phaosoft.android.popularmovies.utils.JsonUtils;
import com.phaosoft.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.apache.commons.collections4.ListUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.ListPopupWindow.WRAP_CONTENT;

/**
 * Popular Movies MainActivity module
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<String> {

    @BindView(R.id.sort_spinner) Spinner sortSpinner;
    @BindView(R.id.scroll_view) ScrollView scrollView;
    @BindView(R.id.grid_layout) GridLayout pictureGrid;
    @BindView(R.id.movie_db_image) ImageView mMovieDBImage;
    @BindView(R.id.network_available) TextView mNetworkAvailability;

    private static final int MINIMUM_IMAGE_WIDTH = 540;

    private static final int MOVIE_SEARCH_LOADER = 22;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    private static int currentSort = -1;
    public static List<Movie> movies = null;

    private static String queryString = null;

    private static int yPosition = -1;
    private static int page = 1;
    private static boolean dealingWithBottom = true;
    private static boolean spinnerBeingSet = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);  // bind the UI objects

        if (! NetworkUtils.isNetworkAvailable(this)) {
            mNetworkAvailability.setVisibility(View.VISIBLE);
        }

        // configure the spinner
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this, R.array.sorted_array,
                R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_menu_layout);
        int white = ContextCompat.getColor(this, android.R.color.white);
        sortSpinner.getBackground().setColorFilter(white, PorterDuff.Mode.SRC_ATOP);

        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(this);
        sortSpinner.setDropDownWidth(WRAP_CONTENT);

        if (currentSort == -1) {
            currentSort = sortSpinner.getSelectedItemPosition();
        } else {
            spinnerBeingSet = true;
            sortSpinner.setSelection(currentSort);
        }

        // make sure that the MovieDB image is selectable
        mMovieDBImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.themoviedb.org/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        // set observers on the scrollView
        ViewTreeObserver observer = scrollView.getViewTreeObserver();

        /*
         * watch the scrolling so that we know where we are in the view for when we return and
         * so that we know when we have scrolled to the bottom of the view.
         */
        observer.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                // save the current Y location of the scrollview each time the view is moved
                yPosition = scrollView.getScrollY();

                // figure out if the user has scrolled to the bottom of the movies
                int bottom = yPosition + scrollView.getHeight();
                if (! dealingWithBottom && scrollView.getChildAt(0).getBottom() <= bottom) {
                    // ensure that only a single query is occurring at a time for reaching the bottom
                    dealingWithBottom = true;
                    // get the next page of movies
                    page++;
                    // query the Movie DB
                    queryMovieDB();
                }
            }
        });

        /*
         * watch the layout visibility changes so that we can scroll to the correct position when
         * the layout has changed.
         */
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // layout is done now scroll to the appropriate location
                        if (yPosition != -1) {
                            scrollView.scrollTo(0, yPosition);
                        } else {
                            scrollView.scrollTo(0, 0);
                        }
                        dealingWithBottom = false;
                    }
                }
        );

        queryMovieDB();
    }

    private void queryMovieDB() {
        // build the query string
        queryString = NetworkUtils.buildQueryString(String.valueOf(
                sortSpinner.getItemAtPosition(currentSort)), page);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, queryString);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieSearchLoader = loaderManager.getLoader(MOVIE_SEARCH_LOADER);
        if (movieSearchLoader == null) {
            loaderManager.initLoader(MOVIE_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_SEARCH_LOADER, queryBundle, this);
        }
    }

    @Override protected void onResume() {
        super.onResume();

        // ensure that the sort spinner matches the movies being displayed
        if (currentSort != -1) {
            sortSpinner.setSelection(currentSort);
        } else {
            currentSort = sortSpinner.getSelectedItemPosition();
            queryString = NetworkUtils.buildQueryString(String.valueOf(
                    sortSpinner.getItemAtPosition(currentSort)), page);
        }
    }

    private void launchDetailActivity(int position) {
        if (movies == null) {
            Log.d("Main", "no movies to select");
            return ;
        }
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.MOVIE_POSITION, position);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // if the spinner is being set from onCreate do nothing
        if (spinnerBeingSet) {
            spinnerBeingSet = false;
            return ;
        }

        // get the loader
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieSearchLoader = loaderManager.getLoader(MOVIE_SEARCH_LOADER);

        // ensure that the network is up
        if (! NetworkUtils.isNetworkAvailable(this)) {
            // show the network unavailable text view
            mNetworkAvailability.setVisibility(View.VISIBLE);
            if (movieSearchLoader != null) {
                /*
                 * since the network is down cancel the loader otherwise a timeout exception
                 * will be thrown
                 */
                movieSearchLoader.cancelLoad();
            }
            return ;
        } else {
            // hide the network unavailable text view
            mNetworkAvailability.setVisibility(View.INVISIBLE);
        }

        // ensure that the first page is retrieved
        page = 1;
        movies = null;
        yPosition = -1;
        currentSort = position;
        queryMovieDB();

        // done with the spinner being changed and reaching the bottom of the page
        spinnerBeingSet = false;
        dealingWithBottom = false;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String movieJson;

            @Override
            protected void onStartLoading() {

                /* If no arguments were passed, we don't have a query to perform. Simply return. */
                if (args == null) {
                    return;
                }

                /*
                 * When we initially begin loading in the background, we want to display the
                 * loading indicator to the user
                 */

                if (movieJson != null) {
                    deliverResult(movieJson);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {

                /* Extract the search query from the args using our constant */
                String searchQueryUrlString = null;
                if (args != null) {
                    searchQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);
                }

                /* If the user didn't enter anything, there's nothing to search for */
                if (searchQueryUrlString == null || TextUtils.isEmpty(searchQueryUrlString)) {
                    return null;
                }

                /* Parse the URL from the passed in String and perform the search */
                try {
                    URL movieUrl = new URL(searchQueryUrlString);
                    return NetworkUtils.getResponseFromHttpUrl(movieUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String data) {
                movieJson = data;
                super.deliverResult(data);
            }
        };
    }

    private boolean moviesSame(List<Movie> current) {
        if (movies == null || current == null) {
            return movies == null && current == null;
        }

        boolean equal = movies.size() == current.size();
        for (int i = 0; i < movies.size() && equal; i++) {
            equal = movies.get(i).equals(current.get(i));
        }
        return equal;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (null == data) {
            return ;
        } else {
            Log.d("Raw Movie Data", data);
            List<Movie> tmpMovies = JsonUtils.parseJsonMovie(data);
            if (movies == null && tmpMovies != null) {
                movies = new ArrayList<>(tmpMovies);
                yPosition = -1;
            } else if (tmpMovies != null && ! moviesSame(tmpMovies)) {
                movies = ListUtils.union(movies, tmpMovies);
            }
        }

        Log.d("Main:Load", (movies == null)?"no movies":movies.toString());

        // calculate the image size based upon the grid size
        int width = pictureGrid.getWidth();
        int total = movies.size();
        int column = 2;
        while ((width / column) > MINIMUM_IMAGE_WIDTH) {
            column++;
        }

        int image_width = width / column;
        // a movie poster's height is typically 1 1/2 times the width
        int image_height = (image_width * 3) / 2;

        pictureGrid.removeAllViews();

        int row = total / column;
        pictureGrid.setColumnCount(column);
        pictureGrid.setRowCount(row + 1);

        // image_unavailable is too big for the size of the thumbnails, therefore, resize it
        Drawable unavailable = ImageUtils.scaleImage(this,
                R.drawable.image_unavailable, image_width, image_height);

        // populate the gridlayout with the movie posters
        for (int i = 0; i < total; i++) {

            Movie movie = movies.get(i);
            // create a dynamic image view to hold the image
            ImageView dImageView = new ImageView(this);
            // set the size of the image
            DrawerLayout.LayoutParams dImageParams =
                    new DrawerLayout.LayoutParams(image_width, image_height);
            dImageView.setLayoutParams(dImageParams);
            // make it easy to tell which image was selected
            dImageView.setTag(i);
            // load the image
            Picasso.with(this)
                    .load(movie.getPosterUrl())
                    .resize(image_width, image_height)
                    .centerCrop()
                    .placeholder(unavailable)
                    .into(dImageView);

            /*
             * setup a callback for when the image is selected so that we can start the
             * detail activity.
             */
            dImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int tag = (int)view.getTag();
                    launchDetailActivity(tag);
                }
            });

            // place the dynamic image into the gridlayout
            dImageView.setLayoutParams(new GridLayout.LayoutParams());

            GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.Spec colSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
                    rowSpan, colSpan);
            pictureGrid.addView(dImageView, gridParam);

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) { }
}