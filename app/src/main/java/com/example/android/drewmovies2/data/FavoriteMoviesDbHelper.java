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

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Assumes each movie has an id and title, but allows nulls for other values that may be missing
        final String CREATE_TABLE = "CREATE TABLE " +
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

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
