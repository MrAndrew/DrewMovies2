package com.example.android.drewmovies2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class FavoriteMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favoriteMoviesDb.db";

    private static final int VERSION = 1;

    // constructor
    FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    //Assumes each movie has an id and title, but allows nulls for other values that may be missing
    private static final String DATABASE_CREATE_FAVORITES = "CREATE TABLE " +
            FavoriteMoviesContract.FavoriteEntry.TABLE_NAME +
            " (" +
            FavoriteMoviesContract.FavoriteEntry._ID +
            "INTEGER PRIMARY KEY, " +
            FavoriteMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID +
            " INTEGER NOT NULL, " +
            FavoriteMoviesContract.FavoriteEntry.COLUMN_TITLE +
            " TEXT NOT NULL, " +
            FavoriteMoviesContract.FavoriteEntry.COLUMN_IMAGE_URL +
            " TEXT, " +
            FavoriteMoviesContract.FavoriteEntry.COLUMN_PLOT +
            " TEXT, " +
            FavoriteMoviesContract.FavoriteEntry.COLUMN_RELEASE_DATE +
            " TEXT, " +
            FavoriteMoviesContract.FavoriteEntry.COLUMN_RATING +
            " DOUBLE);";

    //example upgrade string for production release apps
    private static final String DATABASE_ALTER_FAVORITES_1 = "ALTER TABLE " +
            FavoriteMoviesContract.FavoriteEntry.TABLE_NAME + " ADD COLUMN " +
            FavoriteMoviesContract.FavoriteEntry.COLUMN_ORIGINAL_LANGUAGE + " string;";
    //add second alter table string here in another variable

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_FAVORITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //won't upgrade user data unless version number variable is changed, a nice way to start an update
        //but only change VERSION variable as soon as everything is ready to be released
        if (oldVersion < newVersion) {
            db.execSQL(DATABASE_ALTER_FAVORITES_1);
        }
        //add another if (NOT ELSE!) for subsequent database updates with each string altering the
        //table by either adding or removing columns for less error prone upgradeability (and allowing
        //flexible feature adds without losing old user data)
    }
}
