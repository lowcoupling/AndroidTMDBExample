package com.lowcoupling.whatmovie.updaters;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ArrayAdapter;
import com.lowcoupling.whatmovie.DataViewHelper;
import com.lowcoupling.whatmovie.contentProvider.WhatMovieContract;
import com.lowcoupling.whatmovie.datamodel.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is the broadcast receiver triggered on the reception of a JSON array of movie ids
 * (PopMoviesIntentService)
 * The receiver adds each movie to the list of the main gridview adapter
 * it also adds movie data in the Local SQLite DB through the related content provider
 * It also triggers the grid view update through the invocation of the notify data set changed
 * method
 */
public class PopMoviesUpdater  extends BroadcastReceiver {

    private final ArrayAdapter adapter;
    private final Fragment fragment;
    private String result="";
    public PopMoviesUpdater(ArrayAdapter adapter, Fragment fragment){
        this.adapter=adapter;
        this.fragment = fragment;
    }
    public void update() {
        String json = (String) this.result;
        if (json != null) {
            try {

                JSONArray jArr = new JSONArray(json);
                for (int count = 0; count < jArr.length(); count++) {
                    JSONObject obj = jArr.getJSONObject(count);
                    JSONObject ids = obj.getJSONObject("ids");
                    String imdbId = (String) ids.get("imdb");
                    String traktId = (String) ids.get("slug");
                    Movie tmpMovie = new Movie();
                    tmpMovie.setImdbId(imdbId);
                    tmpMovie.setTraktId(traktId);
                    tmpMovie.setTitle((String) obj.get("title"));
                    if (adapter != null & fragment != null) {
                        //movie data are added to the adapter list (GridView)
                        adapter.add(tmpMovie);
                        //but they are also stored/updated in the Local DB
                        ContentValues cv = new ContentValues();
                        cv.put(WhatMovieContract.COLUMN_IMDB_ID, imdbId);
                        cv.put(WhatMovieContract.COLUMN_TRAKT_ID, traktId);
                        cv.put(WhatMovieContract.COLUMN_TITLE, tmpMovie.getTitle());
                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("content");
                        Uri uri = builder.authority(WhatMovieContract.AUTHORITY).
                                appendPath(WhatMovieContract.TABLE_MOVIES).
                                appendPath("1").
                                build();
                        fragment.getActivity().getContentResolver().insert(uri, cv);

                        DataViewHelper.getInstance().getEntities().put(imdbId, tmpMovie);
                        // adapter.notifyDataSetChanged();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        result = intent.getStringExtra("movies");
        update();

    }
}
