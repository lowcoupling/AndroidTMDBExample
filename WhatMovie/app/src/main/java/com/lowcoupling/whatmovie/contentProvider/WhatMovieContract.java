package com.lowcoupling.whatmovie.contentProvider;

public class WhatMovieContract {
    public static final String AUTHORITY            = "com.lowcoupling.whatmovie.provider";
    public static final String CONTENT_URI          = "content://"+AUTHORITY;
    public static final String TABLE_MOVIES         = "movies";
    public static final String COLUMN_ID            = "_id";
    public static final String COLUMN_IMDB_ID       = "imdbId";
    public static final String COLUMN_TITLE         = "title";
    public static final String COLUMN_OVERVIEW      = "overview";
    public static final String COLUMN_RATING        = "rating";
    public static final String COLUMN_RATING_VOTES  = "rating_votes";
    public static final String COLUMN_THUMB_URL    = "thumb_url";
    public static final String COLUMN_THUMB        = "thumb";
    public static final String COLUMN_TRAKT_ID     = "traktId";
    public static final String DATABASE_NAME       = "whatmovie.db";
    public static final int DATABASE_VERSION       = 1;
    public static final String[] MOVIE_COLUMNS = {WhatMovieContract.COLUMN_ID,
            WhatMovieContract.COLUMN_IMDB_ID, WhatMovieContract.COLUMN_TITLE,
            WhatMovieContract.COLUMN_OVERVIEW, WhatMovieContract.COLUMN_RATING,
            WhatMovieContract.COLUMN_RATING_VOTES, WhatMovieContract.COLUMN_THUMB_URL,
            WhatMovieContract.COLUMN_THUMB, WhatMovieContract.COLUMN_TRAKT_ID};


}
