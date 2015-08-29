package com.lowcoupling.whatmovie.updaters;


import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.lowcoupling.whatmovie.DataViewHelper;
import com.lowcoupling.whatmovie.ViewHolder;
import com.lowcoupling.whatmovie.contentProvider.WhatMovieContract;
import com.lowcoupling.whatmovie.datamodel.Movie;
import com.lowcoupling.whatmovie.intentServices.MovieThumbIntentService;

import org.json.JSONArray;
import org.json.JSONObject;

public class MovieDetailUpdater extends BroadcastReceiver {
    private String result ="";
    public MovieDetailUpdater() {

    }

    public void update(Context context) {
        JSONObject jsonMovie = null;
        String json = (String) this.result;
        if(json!=null) {
            try {
                JSONArray jArr = new JSONArray((String) this.result);
                if (jArr.length() > 0) {
                    jsonMovie = jArr.getJSONObject(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (jsonMovie != null) {
                try {
                    JSONObject images = jsonMovie.getJSONObject("movie").getJSONObject("images").getJSONObject("poster");
                    String overview = jsonMovie.getJSONObject("movie").getString("overview");
                    JSONObject ids = jsonMovie.getJSONObject("movie").getJSONObject("ids");

                    String thumbUrl = images.getString("thumb");
                    String imdbId = ids.getString("imdb");
                    String traktId = ids.getString("slug");

                    Movie movie = (Movie) DataViewHelper.getInstance().getEntities().get(imdbId);
                    movie.setThumbUrl(thumbUrl);

                    View view = DataViewHelper.getInstance().getDataView().get(imdbId);
                    if (view!=null ) {
                        Intent msgIntent = new Intent(context, MovieThumbIntentService.class);
                        msgIntent.putExtra("imdbId",movie.getImdbId());
                        msgIntent.putExtra("thumbUrl",movie.getThumbUrl());
                        context.startService(msgIntent);
                    }

                    ViewHolder vh = (ViewHolder) view.getTag();
                    if (vh!=null){
                        vh.imageUrl = movie.getThumbUrl();
                        view.setTag(vh);
                    }

                    ContentValues cv = new ContentValues();
                    cv.put(WhatMovieContract.COLUMN_IMDB_ID,imdbId);
                    cv.put(WhatMovieContract.COLUMN_TRAKT_ID,traktId);
                    cv.put(WhatMovieContract.COLUMN_OVERVIEW,overview);
                    cv.put(WhatMovieContract.COLUMN_THUMB_URL,thumbUrl);
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("content");
                    Uri uri = builder.authority(WhatMovieContract.AUTHORITY).
                            appendPath(WhatMovieContract.TABLE_MOVIES).
                            appendPath("1").
                            build();
                    context.getContentResolver().insert(uri,cv);



                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        result = intent.getStringExtra("movieDetail");
        update(context);
        //context.unregisterReceiver(this);
    }
}