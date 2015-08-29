package com.lowcoupling.whatmovie.contentProvider.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lowcoupling.whatmovie.contentProvider.WhatMovieContract;
import com.lowcoupling.whatmovie.datamodel.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME       = "whatmovie.db";
    private static final int DATABASE_VERSION       = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + WhatMovieContract.TABLE_MOVIES + "(" + WhatMovieContract.COLUMN_ID
            + " integer primary key autoincrement, " + WhatMovieContract.COLUMN_IMDB_ID
            + " text, " + WhatMovieContract.COLUMN_TITLE
            + " text , "+ WhatMovieContract.COLUMN_OVERVIEW
            + " text, "+ WhatMovieContract.COLUMN_RATING
            + " text , "+ WhatMovieContract.COLUMN_RATING_VOTES
            + " text,"+ WhatMovieContract.COLUMN_THUMB_URL
            + " text,"+ WhatMovieContract.COLUMN_THUMB
            + " blob,"+ WhatMovieContract.COLUMN_TRAKT_ID
            + " text)";

    public MovieDBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    public int updateMovie(ContentValues values) {
        String imdbId = (String) values.get(WhatMovieContract.COLUMN_IMDB_ID);
        int i = 0;
        if (imdbId!=null) {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor =
                    db.query(WhatMovieContract.TABLE_MOVIES, // a. table
                            WhatMovieContract.MOVIE_COLUMNS, // b. column names
                            " imdbId = ?", // c. selections
                            new String[]{imdbId}, // d. selections args
                            null, // e. group by
                            null, // f. having
                            null, // g. order by
                            null); // h. limit
            if (cursor != null && cursor.moveToFirst()) {
                i = db.update(WhatMovieContract.TABLE_MOVIES,
                        values,
                        WhatMovieContract.COLUMN_IMDB_ID + " = ?",
                        new String[]{imdbId});

            } else {
                db.insert(WhatMovieContract.TABLE_MOVIES, // table
                        null, //nullColumnHack
                        values);
                i = 1;
            }
            //db.close();
        }
        return 1;
    }
    public void updateMovie(Movie movie){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WhatMovieContract.COLUMN_TITLE, movie.getTitle());
        values.put(WhatMovieContract.COLUMN_OVERVIEW, movie.getOverview());
        values.put(WhatMovieContract.COLUMN_RATING, movie.getRating());
        values.put(WhatMovieContract.COLUMN_RATING_VOTES, movie.getRatinvVotes());
        Cursor cursor =
                db.query(WhatMovieContract.TABLE_MOVIES, // a. table
                        WhatMovieContract.MOVIE_COLUMNS, // b. column names
                        "imdbId = ?", // c. selections
                        new String[] { movie.getImdbId() }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        if (cursor != null){
            //this is an update!
            // 3. updating row
            int i = db.update(WhatMovieContract.TABLE_MOVIES, //table
                    values, // column/value
                    WhatMovieContract.COLUMN_IMDB_ID+" = ?", // selections
                    new String[] { movie.getImdbId() });

        }else {
            //new MOVIE!
            db.insert(WhatMovieContract.TABLE_MOVIES, // table
                    null, //nullColumnHack
                    values);
        }
        cursor.close();
        db.close();
    }

    public Cursor getMovies(String[] projection, String selection, String[] selectionArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =
                db.query(WhatMovieContract.TABLE_MOVIES, // a. table
                        WhatMovieContract.MOVIE_COLUMNS, // b. column names
                        selection,
                        selectionArgs,
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
       // Log.d("YEAH ",cursor.getString(6));
        return cursor;
    }

    public Cursor getMovieCursor(String imdbId){
        Log.d("GetMovieCursor","retrieving movie"+imdbId);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =
                db.query(WhatMovieContract.TABLE_MOVIES, // a. table
                        WhatMovieContract.MOVIE_COLUMNS, // b. column names
                        " imdbId = ?", // c. selections
                        new String[] { imdbId}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        return cursor;
    }
    public Movie getMovie(String imdbId){
        SQLiteDatabase db = this.getWritableDatabase();
        Movie result = null;
        Cursor cursor =
                db.query(WhatMovieContract.TABLE_MOVIES, // a. table
                        WhatMovieContract.MOVIE_COLUMNS, // b. column names
                        " imdbId = ?", // c. selections
                        new String[] { imdbId}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        if (cursor != null) {
            result = new Movie();
            result.setImdbId(imdbId);
            result.setTitle(cursor.getString(2));
            result.setOverview(cursor.getString(3));
            result.setRating(cursor.getString(4));
            result.setRatinvVotes(cursor.getString(5));
            /*
            {COLUMN_ID, COLUMN_IMDB_ID, COLUMN_TITLE,
                                                   COLUMN_OVERVIEW, COLUMN_RATING,
                                                   COLUMN_RATING_VOTES};
             */
        }
        return result;
    }

    public Cursor getAllMoviesLightweightCursor(){
        String query = "SELECT  title,imdbId, traktId FROM " + WhatMovieContract.TABLE_MOVIES+ " LIMIT 50";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor getAllMoviesCursor(){
        String query = "SELECT  * FROM " + WhatMovieContract.TABLE_MOVIES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<Movie>();
        String query = "SELECT  * FROM " + WhatMovieContract.TABLE_MOVIES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Movie movie = null;
        if (cursor.moveToFirst()) {
            do {
                movie = new Movie();
                movie.setImdbId(cursor.getString(1));
                movie.setTitle(cursor.getString(2));
                movie.setOverview(cursor.getString(3));
                movie.setRating(cursor.getString(4));
                movie.setRatinvVotes(cursor.getString(5));
                movies.add(movie);
            } while (cursor.moveToNext());
        }

        return movies;
    }
}
