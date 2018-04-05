package com.udacity.garuolis.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 4;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String sql = "CREATE TABLE " +
                MoviesContract.MovieEntry.TABLE_FAVORITES + "("
                + MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                + MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE,"
                + MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + MoviesContract.MovieEntry.COLUMN_POSTER + " TEXT NOT NULL"
                + ");";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_FAVORITES);
            onCreate(db);
        }
    }
}
