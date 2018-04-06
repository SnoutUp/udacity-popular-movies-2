package com.udacity.garuolis.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.udacity.garuolis.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_FAVORITES      = "favorites";

        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_ID      = "movie_id";
        public static final String COLUMN_TITLE         = "title";
        public static final String COLUMN_POSTER        = "poster_path";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_FAVORITES).build();

        public static Uri BuildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
