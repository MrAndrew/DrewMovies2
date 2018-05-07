package com.example.android.drewmovies2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

import static com.example.android.drewmovies2.data.FavoriteMoviesContract.FavoriteEntry.TABLE_NAME;

public class MoviesContentProvider extends ContentProvider {

    private static final int FAVORITES = 100;
    private static final int FAVORITES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_FAVORITES + "/#", FAVORITES_WITH_ID);

        return uriMatcher;
    }

    private FavoriteMoviesDbHelper mFavoriteMoviesDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavoriteMoviesDbHelper = new FavoriteMoviesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        // Query for the tasks directory and write a default case
        switch (match) {
            // Query for the tasks directory
            case FAVORITES:
                retCursor =  db.query(FavoriteMoviesContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITES_WITH_ID:
                // Get the Movie ID from the URI path
                String stringId = uri.getPathSegments().get(1);
                //column searched for
                String mSelection = FavoriteMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?";
                String[] mSelectArgs = new String[] {stringId};
                retCursor = db.query(FavoriteMoviesContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITES:
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(FavoriteMoviesContract.FavoriteEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int movieDeleted;

        switch (match) {
            case FAVORITES_WITH_ID:
                // Get the Movie ID from the URI path
                String stringId = uri.getPathSegments().get(1);
                // filters for a movie who's id in the uri matched a movieId in the db
                movieDeleted = db.delete(TABLE_NAME, FavoriteMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?", new String[]{stringId});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (movieDeleted != 0) {
            // A movie was deleted, set notification
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        // Return the number of movies deleted
        return movieDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
