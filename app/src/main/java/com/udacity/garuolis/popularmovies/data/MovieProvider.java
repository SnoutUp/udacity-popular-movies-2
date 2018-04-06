package com.udacity.garuolis.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class MovieProvider extends ContentProvider {
    private static final String TAG = MovieProvider.class.getSimpleName();

    public static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper dbHelper;

    private static final int MOVIE            = 100;
    private static final int MOVIE_WITH_ID    = 101;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.MovieEntry.TABLE_FAVORITES, MOVIE);
        matcher.addURI(authority, MoviesContract.MovieEntry.TABLE_FAVORITES + "/#", MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            // All Flavors selected
            case MOVIE:{
                retCursor = dbHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_FAVORITES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }

            case MOVIE_WITH_ID:{
                retCursor = dbHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_FAVORITES,
                        projection,
                        MoviesContract.MovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default:{
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int type = sUriMatcher.match(uri);
        switch (type) {
            case MOVIE:
                break;
            case MOVIE_WITH_ID:
                break;
            default:
                Log.e(TAG, "Unknown URI: " + uri);
                break;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                long _id = db.insert(MoviesContract.MovieEntry.TABLE_FAVORITES, null, values);

                if (_id > 0) {
                    returnUri = MoviesContract.MovieEntry.BuildMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch(match){
            case MOVIE:
                numDeleted = db.delete(MoviesContract.MovieEntry.TABLE_FAVORITES, selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
                numDeleted = db.delete(MoviesContract.MovieEntry.TABLE_FAVORITES, MoviesContract.MovieEntry._ID + " = ?", new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numUpdated = 0;

        switch(sUriMatcher.match(uri)){
            case MOVIE:{
                numUpdated = db.update(MoviesContract.MovieEntry.TABLE_FAVORITES,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case MOVIE_WITH_ID: {
                numUpdated = db.update(MoviesContract.MovieEntry.TABLE_FAVORITES, values,
                        MoviesContract.MovieEntry._ID + " = ?", new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }
}
