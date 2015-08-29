package com.lowcoupling.whatmovie.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.lowcoupling.whatmovie.contentProvider.WhatMovieContract;

public class MovieDetailLoader extends AsyncTaskLoader {

    private Context context;
    private String imdbid;

    public MovieDetailLoader(Context context,String imdbid) {
        super(context);
        this.context = context;
        this.imdbid = imdbid;
    }

    @Override
    public Object loadInBackground() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content");
        Uri uri = builder.authority(WhatMovieContract.AUTHORITY).
                appendPath(WhatMovieContract.TABLE_MOVIES).
                appendPath("1").
                build();
        String [] params = {this.imdbid};
        //Log.d("Movie Loader","Retrieving movie "+this.imdbid);
        Cursor mc = context.getContentResolver().query(uri,null," imdbId = ?",params,null);
        return mc;

    }

    public String getImdbId() {
        return this.imdbid;
    }
}
