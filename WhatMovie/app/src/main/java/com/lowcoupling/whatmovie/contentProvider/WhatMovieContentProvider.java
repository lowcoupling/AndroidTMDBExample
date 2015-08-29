package com.lowcoupling.whatmovie.contentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.lowcoupling.whatmovie.contentProvider.database.MovieDBHelper;


public class WhatMovieContentProvider extends ContentProvider {

    private static final int ALL_MOVIES = 1;
    private static final int MOVIE = 2;
    private UriMatcher uriMatcher;
    private MovieDBHelper dbHelper = null;

    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(WhatMovieContract.AUTHORITY, WhatMovieContract.TABLE_MOVIES, ALL_MOVIES);
        uriMatcher.addURI(WhatMovieContract.AUTHORITY, "movies/#", MOVIE);
        dbHelper = new MovieDBHelper(getContext());
        return false;


    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String s2) {
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case ALL_MOVIES:
                cursor = dbHelper.getAllMoviesLightweightCursor();
                getContext().getContentResolver().notifyChange(uri,null);
                break;
            case MOVIE:
                cursor = dbHelper.getMovies(projection,selection,selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                dbHelper.updateMovie(contentValues);
                getContext().getContentResolver().notifyChange(uri,null);
                break;

        }
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        switch (uriMatcher.match(uri)) {
            case ALL_MOVIES:
                break;
            case MOVIE:
                break;


        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        switch (uriMatcher.match(uri)) {
            case ALL_MOVIES:
                break;
            case MOVIE:
                break;


        }
        return 0;
    }
}
