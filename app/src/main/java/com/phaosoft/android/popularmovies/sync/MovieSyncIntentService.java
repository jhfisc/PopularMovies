package com.phaosoft.android.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;

public class MovieSyncIntentService extends IntentService {

    public MovieSyncIntentService() {
        super("MovieSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MovieSync.syncMovie(this);
    }
}
