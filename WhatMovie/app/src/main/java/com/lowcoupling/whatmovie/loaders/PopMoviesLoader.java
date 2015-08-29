package com.lowcoupling.whatmovie.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.lowcoupling.whatmovie.contentProvider.WhatMovieContract;

public class PopMoviesLoader extends AsyncTaskLoader {
    private Context context;
    private String imdbid;
    public PopMoviesLoader(Context context) {
        super(context);
        this.imdbid="ALL";
        this.context = context;
    }


    @Override
    public Object loadInBackground() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content");
        Uri uri = builder.authority(WhatMovieContract.AUTHORITY).
                appendPath(WhatMovieContract.TABLE_MOVIES).
                build();
        Cursor mc = context.getContentResolver().query(uri,null,null,null,null);
        return mc;

    }

    public String getImdbId() {
        return imdbid;
    }
}
