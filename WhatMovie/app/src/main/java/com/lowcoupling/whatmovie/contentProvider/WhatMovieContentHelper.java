package com.lowcoupling.whatmovie.contentProvider;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.lowcoupling.whatmovie.datamodel.Movie;

public class WhatMovieContentHelper {

    public static Movie getMovie(Context context,String imdbId){
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(WhatMovieContract.AUTHORITY).
                appendPath(WhatMovieContract.TABLE_MOVIES).appendPath("1").build();

        Cursor cursor = context.getContentResolver().query(uri,null,
                "imdbId = ?", // c. selections
                new String[] {imdbId},
                null);
        Movie result = null;
        if (cursor != null  && cursor.moveToFirst() ) {
            result = new Movie();
            result.setImdbId(imdbId);
            result.setTitle(cursor.getString(2));
            result.setOverview(cursor.getString(3));
            result.setRating(cursor.getString(4));
            result.setRatinvVotes(cursor.getString(5));
            //Log.d("GREAT ", result.getTitle());
       }
        return result;
    }
}
