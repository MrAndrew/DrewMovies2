package com.example.android.drewmovies2.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteMoviesContract {

    public static final String AUTHORITY = "com.example.android.drewmovies2";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITES = "favorite_movies";

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorite_movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "movie_title";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RATING = "user_rating";

        //example for adding more detailed values
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
    }

}
