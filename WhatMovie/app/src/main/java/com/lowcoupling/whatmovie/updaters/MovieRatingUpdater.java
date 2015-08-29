package com.lowcoupling.whatmovie.updaters;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.lowcoupling.whatmovie.DataViewHelper;
import com.lowcoupling.whatmovie.contentProvider.WhatMovieContract;
import com.lowcoupling.whatmovie.datamodel.Movie;

import org.json.JSONObject;

public class MovieRatingUpdater extends BroadcastReceiver{
    private String result;
    private String imdbId;
    private Context context;
    public MovieRatingUpdater(){}

    public void update() {
        if(result!=null) {
            String res = (String) this.result;
            Movie movie = (Movie) DataViewHelper.getInstance().getEntities().get(this.imdbId);
            View view = DataViewHelper.getInstance().getDataView().get(movie.getImdbId());

            try {
                JSONObject jr = new JSONObject(res);
                String rating = jr.getString("rating");
                String votes = jr.getString("votes");

                ContentValues cv = new ContentValues();
                cv.put(WhatMovieContract.COLUMN_IMDB_ID, imdbId);
                cv.put(WhatMovieContract.COLUMN_RATING, rating);
                cv.put(WhatMovieContract.COLUMN_RATING_VOTES, votes);
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("content");
                Uri uri = builder.authority(WhatMovieContract.AUTHORITY).
                        appendPath(WhatMovieContract.TABLE_MOVIES).
                        appendPath("1").
                        build();
                context.getContentResolver().insert(uri, cv);


            } catch (Exception e) {
                e.printStackTrace();
            }
            //rating.setText()
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        result = intent.getStringExtra("movieRating");
        this.context = context;
        this.imdbId = intent.getStringExtra("imdbId");
        update();

        //context.unregisterReceiver(this);
    }
}
