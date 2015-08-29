package com.lowcoupling.whatmovie.updaters;


import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lowcoupling.whatmovie.DataViewHelper;
import com.lowcoupling.whatmovie.ViewHolder;
import com.lowcoupling.whatmovie.contentProvider.WhatMovieContract;
import com.lowcoupling.whatmovie.datamodel.Movie;

import java.io.ByteArrayOutputStream;

public class MovieThumbUpdater  extends BroadcastReceiver {
    private Movie movie;
    private Context context;

    public MovieThumbUpdater(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String imdbid = intent.getStringExtra("imdbId");
        this.context = context;
        this.movie = (Movie)DataViewHelper.getInstance().getEntities().get(imdbid);
        update();
    }

    public void update() {
        if(movie==null)return;
        View lastView =  DataViewHelper.getInstance().getDataView().get(movie.getImdbId());
        ViewHolder vh = (ViewHolder) lastView.getTag();
        if (vh.imdbId.equals(movie.getImdbId())){
            ImageView imageView = vh.imageView;
            imageView.setImageDrawable(movie.getThumb());
        }

        ContentValues cv = new ContentValues();
        Drawable thumbnail = movie.getThumb();
        //if (thumbnail==null){
        //    thumbnail = this.context.getApplicationContext().getResources().getDrawable(R.drawable.ic_launcher);
        //}
        if(thumbnail ==null )Log.e("THUMBNAIL","IS NULL");
        if ((thumbnail!=null)) {
            movie.setThumb(thumbnail);
            Bitmap bitmap = ((BitmapDrawable) movie.getThumb()).getBitmap();
            if ((bitmap != null)) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                cv.put(WhatMovieContract.COLUMN_IMDB_ID, movie.getImdbId());
                cv.put(WhatMovieContract.COLUMN_THUMB, stream.toByteArray());
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("content");
                Uri uri = builder.authority(WhatMovieContract.AUTHORITY).
                        appendPath(WhatMovieContract.TABLE_MOVIES).
                        appendPath("1").
                        build();
                context.getContentResolver().insert(uri, cv);
            }
        }

        movie.setThumb(null);
    }
}
