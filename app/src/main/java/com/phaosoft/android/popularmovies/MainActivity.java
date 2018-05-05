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
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.phaosoft.android.popularmovies.model.Movie;
import com.phaosoft.android.popularmovies.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.ListPopupWindow.WRAP_CONTENT;

/**
 * Popular Movies MainActivity module
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<String>{

    @BindView(R.id.sort_spinner) Spinner sortSpinner;
    @BindView(R.id.grid_layout) GridLayout pictureGrid;
    @BindView(R.id.movie_db_image) ImageView mMovieDBImage;
    @BindView(R.id.network_available) TextView mNetworkAvailibility;

    private static final int MOVIE_SEARCH_LOADER = 22;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    private static final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String PARAM_KEY = "api_key";
    private static final String PARAM_LANGUAGE = "language";
    private static final String PARAM_SORT = "sort_by";
    private static final String PARAM_ADULT = "include_adult";
    private static final String PARAM_VIDEO = "include_video";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_RELEASE = "primary_release_date.lte";
    private static final String PARAM_REGION = "region";

    private static final String API_KEY = "7d7dc1d96a37db918fc2d52df9ecffad";
    private static final String SORT_POPULARITY = "popularity.desc";
    private static final String SORT_VOTE = "vote_average.desc";
    private static final String SORT_RELEASE_DATE = "release_date.desc";

    private static int currentSort = -1;
    public static List<Movie> movies = null;

    private static String queryString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);  // bind the UI objects

        if (! isNetworkAvailable()) {
            mNetworkAvailibility.setVisibility(View.VISIBLE);
        }

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
            sortSpinner.setSelection(currentSort);
        }

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

        queryString = buildQueryString(currentSort);
        Log.i("queryString", queryString);

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
        if (currentSort != -1) {
            sortSpinner.setSelection(currentSort);
        } else {
            currentSort = sortSpinner.getSelectedItemPosition();
            queryString = buildQueryString(currentSort);
            Log.i("queryString", queryString);
        }
    }

    private void launchDetailActivity(int position) {
        if (movies == null) {
            return ;
        }
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.MOVIE_POSITION, position);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieSearchLoader = loaderManager.getLoader(MOVIE_SEARCH_LOADER);

        if (! isNetworkAvailable()) {
            mNetworkAvailibility.setVisibility(View.VISIBLE);
            if (movieSearchLoader != null) {
                movieSearchLoader.cancelLoad();
            }
            return ;
        } else {
            mNetworkAvailibility.setVisibility(View.INVISIBLE);
        }

        currentSort = position;
        queryString = buildQueryString(currentSort);
        Log.i("Query String", queryString);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, queryString);

        if (movieSearchLoader == null) {
            loaderManager.initLoader(MOVIE_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_SEARCH_LOADER, queryBundle, this);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
        Log.i("onCreateLoader", "-----> called");
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
                    Log.i("onStartLoading", movieJson);
                    deliverResult(movieJson);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {

                /* Extract the search query from the args using our constant */
                String searchQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);

                /* If the user didn't enter anything, there's nothing to search for */
                if (searchQueryUrlString == null || TextUtils.isEmpty(searchQueryUrlString)) {
                    return null;
                }

                /* Parse the URL from the passed in String and perform the search */
                try {
                    URL githubUrl = new URL(searchQueryUrlString);
                    String githubSearchResults = getResponseFromHttpUrl(githubUrl);
                    return githubSearchResults;
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
        } finally {
            urlConnection.disconnect();
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Log.i("onLoadFinished", "-----> called");
        if (null == data) {
            Log.i("onLoadFinished", "-----> data is null");
        } else {
            Log.i("onLoadFinished", "-----> " + data);
            movies = JsonUtils.parseJsonMovie(data);
            if (movies != null && movies.size() > 0) {
                // TODO - populate grid layout
                Log.i("onLoadFinished", movies.get(0).toString());
            }
        }

        pictureGrid.removeAllViews();

        int width = pictureGrid.getWidth();
        int total = movies.size();
        int column = 4;
        int row = total / column;
        pictureGrid.setColumnCount(column);
        pictureGrid.setRowCount(row + 1);
        int image_width = width / column;
        int image_height = (image_width * 3) / 2;

        Drawable unavailable = ResourcesCompat.getDrawable(getResources(),
                R.drawable.image_unavailable, null);
        Bitmap bitmap = ((BitmapDrawable) unavailable).getBitmap();
        unavailable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,
                image_width, image_height, true));

        for (int i = 0, c = 0, r = 0; i < total; i++, c++) {
            if (c == column) {
                c = 0;
                r++;
            }

            Movie movie = movies.get(i);
            ImageView dImageView = new ImageView(this);
            DrawerLayout.LayoutParams dImageParams =
                    new DrawerLayout.LayoutParams(width/column,
                            ((width/column)* 3)/2);
            dImageView.setLayoutParams(dImageParams);
            dImageView.setTag(i);
            Picasso.with(this)
                    .load(movie.getPosterUrl())
                    .resize(image_width, image_height)
                    .centerCrop()
                    .placeholder(unavailable)
                    .into(dImageView);

            dImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int tag = (int)view.getTag();
                    launchDetailActivity(tag);
                }
            });

            dImageView.setLayoutParams(new GridLayout.LayoutParams());

             GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
             GridLayout.Spec colspan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
                    rowSpan, colspan);
            pictureGrid.addView(dImageView, gridParam);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        Log.i("onLoaderReset", "-----> called");
    }

    public String buildQueryString(int position) {
        String itemString = String.valueOf(sortSpinner.getItemAtPosition(position));
        String sorter;
        switch (itemString) {
            case "Highest Rated":
                sorter = SORT_VOTE;
                break;
            case "Most Popular":
                sorter = SORT_POPULARITY;
                break;
            case "Release Date":
                sorter = SORT_RELEASE_DATE;
                break;
            default:
                sorter = SORT_POPULARITY;
                break;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = new Date();
        String today = formatter.format(date);

        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_KEY, API_KEY)
                .appendQueryParameter(PARAM_SORT, sorter)
                .appendQueryParameter(PARAM_REGION, "US")
                .appendQueryParameter(PARAM_LANGUAGE, "en-US")
                .appendQueryParameter(PARAM_ADULT, "false")
                .appendQueryParameter(PARAM_VIDEO, "false")
                .appendQueryParameter(PARAM_PAGE, "1")
                .appendQueryParameter(PARAM_RELEASE, today)
                .build();

        return builtUri.toString();
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo info = manager.getActiveNetworkInfo();
            return info.isAvailable() && info.isConnected();
        } catch (NullPointerException e) {
            return false;
        }
    }
}
