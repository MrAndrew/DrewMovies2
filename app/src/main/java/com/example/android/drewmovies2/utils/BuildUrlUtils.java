package com.example.android.drewmovies2.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class BuildUrlUtils {

//    private static final String TAG = BuildUrlUtils.class.getSimpleName();

    //base urls for different types of requests
    final private static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    final private static String MOVIE_LIST_POP_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
    final private static String MOVIE_LIST_RATE_BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";
    final private static String MOVIE_VIDEOS_BASE_URL = "https://api.themoviedb.org/3/movie/";
    final private static String MOVIE_REVIEWS_BASE_URL = "https://api.themoviedb.org/3/movie/";

    //TODO KEEP A LIST OF DIFFERENT SIZE VARIABLES TO ALLOW THE USER TO CUSTOMIZE MOVIE POSTER SIZE
    //current size seems to be the best size for now, might change to user preference value in phase 2
    final private static String PARAM_IMAGE_SIZE = "w500";
    //sort order Strings
    final private static String PARAM_SORT = "sort_by";
    final private static String sortByPop = "popularity.desc";
    //lang and page num strings
    // ex: &language=en-US&page=1
    // might be better to change to selectable strings from value resource folder, especially for
    //language portability for different users
    final private static String PARAM_LANG = "language";
    final private static String lang_en_us = "en-US";
    final private static String PARAM_PAGE = "page";
    final private static String page_num = "1";
    //TODO API KEY, REMOVE BEFORE PUTTING ON GIT!
    final private static String api_key = "72edd0418060deba2a08171d52d9a084";
    final private static String PARAM_API_KEY = "api_key";
    //video and reviews requests
    final private static String video_path = "videos";
    final private static String review_path = "reviews";



    /**
     * Video Links DB url request example:
     * https://api.themoviedb.org/3/movie/<<MOVIE_ID>>/videos?api_key=<<API_KEY>>&language=en-US
     *
     * json returned contains array of objects called "results" which may be different lengths
     * (like 5 in the array for 5 different videos)
     * results.key is the youtube url ending to add like:
     * https://www.youtube.com/watch?v=<<results.key>>
     */
    public static URL buildMovieVideosRequestUrl(String id) {
        Uri builtUri = Uri.parse(MOVIE_VIDEOS_BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(video_path)
                .appendQueryParameter(PARAM_API_KEY, api_key)
                .appendQueryParameter(PARAM_LANG, lang_en_us)
                .build();
//        Log.d(TAG, "video request url: " + builtUri.toString());

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Reviews DB url request example:
     * https://api.themoviedb.org/3/movie/<<MOVIE_ID>>/reviews?api_key=<<API_KEY>>&language=en-US&page=1
     *
     *  json returned contains array of objects called "results" which may be different lengths
     * (like 11 in the array for 11 different reviews all on themoviedb.org)
     * results.author is the username of the reviewer (String)
     * results.content is the actual text of the review (String)
     *
     */
    public static URL buildMovieReviewsRequestUrl(String id) {
        Uri builtUri = Uri.parse(MOVIE_REVIEWS_BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(review_path)
                .appendQueryParameter(PARAM_API_KEY, api_key)
                .appendQueryParameter(PARAM_LANG, lang_en_us)
                .build();
//        Log.d(TAG, "reviews request url: " + builtUri.toString());

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Builds the URL to request list of movies in JSON format for popularity
     * ex: http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=<APIKEY>
     */
    public static URL buildMovieListPopRequestUrl() {
        Uri builtUri = Uri.parse(MOVIE_LIST_POP_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_SORT, sortByPop)
                .appendQueryParameter(PARAM_API_KEY, api_key)
                .build();
//        Log.d(TAG, "movie list url: " + builtUri.toString());

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Builds the URL to request list of movies in JSON format based on rating
     * ex: https://api.themoviedb.org/3/movie/top_rated?api_key=<APIKEY>&language=en-US&page=1
     */
    public static URL buildMovieListRatedRequestUrl() {
        Uri builtUri = Uri.parse(MOVIE_LIST_RATE_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, api_key)
                .appendQueryParameter(PARAM_LANG, lang_en_us)
                .appendQueryParameter(PARAM_PAGE, page_num)
                .build();
//        Log.d(TAG, "movie list url: " + builtUri.toString());

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    /**
     * Builds the URL used to query themoviedb.org for a poster picture
     * ex: http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
     *
     * returns string because it will be used by picasso
     */
    public static String buildImageRequestUrl(String posterPath) {
        // removes the first annoying "\" included in the api json response
        String imagePath = posterPath.substring(1);
        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(PARAM_IMAGE_SIZE)
                .appendPath(imagePath)
                .build();
//        Log.d(TAG, "image request url: " + builtUri.toString());

        //changed to reduce redundancy caught in lint
        return builtUri.toString();
    }

}
