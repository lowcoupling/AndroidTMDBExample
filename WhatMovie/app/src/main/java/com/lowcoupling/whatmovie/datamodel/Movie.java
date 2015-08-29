package com.lowcoupling.whatmovie.datamodel;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Movie {
    private String imdbId;
    private String title;
    private Drawable thumb;
    private String thumbUrl;
    private String overview;
    private String traktId;
    private String ratinvVotes;
    private String rating;



    public  Movie(){
        imdbId="";
    }

    public void initLightWeight(Cursor cursor){
        this.setTitle(cursor.getString(0));
        this.setImdbId(cursor.getString(1));
        this.setTraktId(cursor.getString(2));
    }

    public  Movie(Cursor cursor){
        this.setImdbId(cursor.getString(1));
        this.setTitle(cursor.getString(2));
        this.setOverview(cursor.getString(3));
        this.setRating(cursor.getString(4));
        this.setRatinvVotes(cursor.getString(5));
        this.setThumbUrl(cursor.getString(6));
        byte [] bmapArray =  cursor.getBlob(7);
        if (bmapArray !=null) {
            Bitmap btm = BitmapFactory.decodeByteArray(bmapArray,
                    0, bmapArray.length);
            this.thumb = new BitmapDrawable(btm);
        }
        this.setTraktId(cursor.getString(8));

    }

    public void setImdbId(String _id){this.imdbId=_id;}
    public String getImdbId(){return this.imdbId;}

    public String getTitle(){return title;}
    public void setTitle(String title){this.title=title;}

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }


    public Drawable getThumb() {
        return thumb;
    }

    public void setThumb(Drawable thumb) {
        this.thumb = thumb;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOverview() {
        return overview;
    }

    public void setTraktId(String id) {
        this.traktId = id;
    }

    public String getTraktId() {
        return traktId;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setRatinvVotes(String ratinvVotes) {
        this.ratinvVotes = ratinvVotes;
    }

    public String getRatinvVotes() {
        return ratinvVotes;
    }

    public String getRating() {
        return rating;
    }
}