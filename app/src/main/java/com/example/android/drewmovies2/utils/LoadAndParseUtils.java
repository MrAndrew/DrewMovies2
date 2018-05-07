package com.example.android.drewmovies2.utils;

import com.example.android.drewmovies2.models.MovieParcelable;
import com.example.android.drewmovies2.models.ReviewParcelable;
import com.example.android.drewmovies2.models.VideoParcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LoadAndParseUtils {

//    private static final String TAG = LoadAndParseUtils.class.getSimpleName();

    //JSON Movie Keys
    private static final String KEY_RESULTS_ARRAY = "results";
    private static final String KEY_MOVIE_ID = "id";
    private static final String KEY_MOVIE_TITLE = "title";
    private static final String KEY_MOVIE_IMAGE_PATH = "backdrop_path";
    private static final String KEY_DESCRIPTION = "overview";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_USER_RATING = "vote_average";

    //JSON Review Keys
    private static final String REVIEW_KEY_AUTHOR = "author";
    private static final String REVIEW_KEY_CONTENT = "content";
    private static final String REVIEW_KEY_ID = "id";
    private static final String REVIEW_KEY_URL = "url";

    //JSON Video Keys
    private static final String VIDEO_KEY_ID = "id";
    private static final String VIDEO_KEY_KEY = "key";
    private static final String VIDEO_KEY_SITE = "site";
    private static final String VIDEO_KEY_TYPE = "type";
    private static final String VIDEO_KEY_NAME = "name";

    public static ArrayList<MovieParcelable> loadMoviesJsonFromUrl(URL url) throws IOException {
        String dlJsonString = null;
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            dlJsonString = responseStrBuilder.toString();

//            String veryLongString = dlJsonString;
//            int maxLogSize = 1000;
//            for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
//                int start = i * maxLogSize;
//                int end = (i+1) * maxLogSize;
//                end = end > veryLongString.length() ? veryLongString.length() : end;
//                Log.v(TAG, "dl movies json string: " + veryLongString.substring(start, end));
//            }

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return parseMoviesJson(dlJsonString);
    }

    private static ArrayList<MovieParcelable> parseMoviesJson(String jsonString) {

        //create new arraylist of movie objects to return
        ArrayList<MovieParcelable> moviesList = new ArrayList<>();

//        String veryLongString = jsonString;
//        int maxLogSize = 1000;
//        for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
//            int start = i * maxLogSize;
//            int end = (i+1) * maxLogSize;
//            end = end > veryLongString.length() ? veryLongString.length() : end;
//            Log.v(TAG, "JSON string passed into parseMoviesJson: " + veryLongString.substring(start, end));
//        }

        //get and set movies from results object array in the json string
        try {
            JSONObject resultsObj = new JSONObject(jsonString);

//            String veryLongString1 = resultsObj.toString();
//            for(int i = 0; i <= veryLongString1.length() / maxLogSize; i++) {
//                int start = i * maxLogSize;
//                int end = (i+1) * maxLogSize;
//                end = end > veryLongString1.length() ? veryLongString1.length() : end;
//                Log.v(TAG, "JSON Movies object from jsonString: " + veryLongString1.substring(start, end));
//            }

            JSONArray moviesJsonArray = resultsObj.getJSONArray(KEY_RESULTS_ARRAY);

//            String veryLongString2 = moviesJsonArray.toString();
//            for(int i = 0; i <= veryLongString2.length() / maxLogSize; i++) {
//                int start = i * maxLogSize;
//                int end = (i+1) * maxLogSize;
//                end = end > veryLongString2.length() ? veryLongString2.length() : end;
//                Log.v(TAG, "JSON Movies array: " + veryLongString2.substring(start, end));
//            }

            //loop through all the objects in the results array that should be returned in the JSON
            for(int i = 0; i < moviesJsonArray.length(); i++) {
                //create new movie object
                MovieParcelable movie = new MovieParcelable();
                JSONObject movieJsonObj = moviesJsonArray.getJSONObject(i);
//                Log.v(TAG, "movieJsonArray[" + i + "] : " + movieJsonObj);
                //set values to object within the array
                movie.setMovieId(movieJsonObj.getInt(KEY_MOVIE_ID));
                movie.setMovieTitle(movieJsonObj.getString(KEY_MOVIE_TITLE));
                movie.setImageUrlPath(movieJsonObj.getString(KEY_MOVIE_IMAGE_PATH));
                movie.setAbout(movieJsonObj.getString(KEY_DESCRIPTION));
                movie.setReleaseDate(movieJsonObj.getString(KEY_RELEASE_DATE));
                movie.setUserRating(movieJsonObj.getDouble(KEY_USER_RATING));
                //set the movie obj to the corresponding position in the movie obj array
                moviesList.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.v(TAG, "moviesList: " + moviesList.toString());
//        Log.v(TAG, "movie list array size: " + moviesList.size());
        return moviesList;
    }

    public static ArrayList<VideoParcelable> loadVideos (URL url) throws IOException {
        String dlJsonString = null;
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            dlJsonString = responseStrBuilder.toString();

//            String veryLongString = dlJsonString;
//            int maxLogSize = 1000;
//            for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
//                int start = i * maxLogSize;
//                int end = (i+1) * maxLogSize;
//                end = end > veryLongString.length() ? veryLongString.length() : end;
//                Log.v(TAG, "dl videos json string: " + veryLongString.substring(start, end));
//            }

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return parseVideosJson(dlJsonString);
    }

    private static ArrayList<VideoParcelable> parseVideosJson(String jsonString) {
        //create new arraylist of video objects to return
        ArrayList<VideoParcelable> videosList = new ArrayList<>();

//        String veryLongString = jsonString;
//        int maxLogSize = 1000;
//        for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
//            int start = i * maxLogSize;
//            int end = (i+1) * maxLogSize;
//            end = end > veryLongString.length() ? veryLongString.length() : end;
//            Log.v(TAG, "JSON string passed into parseVideosJson: " + veryLongString.substring(start, end));
//        }

        //get and set videos from results object array in the json string
        try {
            JSONObject resultsObj = new JSONObject(jsonString);

//            String veryLongString1 = resultsObj.toString();
//            for(int i = 0; i <= veryLongString1.length() / maxLogSize; i++) {
//                int start = i * maxLogSize;
//                int end = (i+1) * maxLogSize;
//                end = end > veryLongString1.length() ? veryLongString1.length() : end;
//                Log.v(TAG, "JSON Videos object from jsonString: " + veryLongString1.substring(start, end));
//            }

            JSONArray videosJsonArray = resultsObj.getJSONArray(KEY_RESULTS_ARRAY);

//            String veryLongString2 = videosJsonArray.toString();
//            for(int i = 0; i <= veryLongString2.length() / maxLogSize; i++) {
//                int start = i * maxLogSize;
//                int end = (i+1) * maxLogSize;
//                end = end > veryLongString2.length() ? veryLongString2.length() : end;
//                Log.v(TAG, "JSON Videos array: " + veryLongString2.substring(start, end));
//            }

            //loop through all the objects in the results array that should be returned in the JSON
            for(int i = 0; i < videosJsonArray.length(); i++) {
                //create new video object
                VideoParcelable video = new VideoParcelable();
                JSONObject videoJsonObj = videosJsonArray.getJSONObject(i);
//                Log.v(TAG, "videosJsonArray[" + i + "] : " + videoJsonObj);
                //set values to object within the array
                video.setVideoId(videoJsonObj.getString(VIDEO_KEY_ID));
                video.setVideoKey(videoJsonObj.getString(VIDEO_KEY_KEY));
                video.setVideoSite(videoJsonObj.getString(VIDEO_KEY_SITE));
                video.setVideoType(videoJsonObj.getString(VIDEO_KEY_TYPE));
                video.setVideoName(videoJsonObj.getString(VIDEO_KEY_NAME));
                //set the movie obj to the corresponding position in the movie obj array
                videosList.add(video);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.v(TAG, "videosList: " + videosList.toString());
//        Log.v(TAG, "video list array size: " + videosList.size());
        return videosList;
    }

    public static ArrayList<ReviewParcelable> loadReviews (URL url) throws IOException {
        String dlJsonString = null;
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            dlJsonString = responseStrBuilder.toString();

//            String veryLongString = dlJsonString;
//            int maxLogSize = 1000;
//            for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
//                int start = i * maxLogSize;
//                int end = (i+1) * maxLogSize;
//                end = end > veryLongString.length() ? veryLongString.length() : end;
//                Log.v(TAG, "dl reviews json string: " + veryLongString.substring(start, end));
//            }

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return parseReviewsJson(dlJsonString);
    }

    private static ArrayList<ReviewParcelable> parseReviewsJson(String jsonString) {
        //create new arraylist of video objects to return
        ArrayList<ReviewParcelable> reviewsList = new ArrayList<>();

//        String veryLongString = jsonString;
//        int maxLogSize = 1000;
//        for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
//            int start = i * maxLogSize;
//            int end = (i+1) * maxLogSize;
//            end = end > veryLongString.length() ? veryLongString.length() : end;
//            Log.v(TAG, "JSON string passed into parseReviewsJson: " + veryLongString.substring(start, end));
//        }

        //get and set videos from results object array in the json string
        try {
            JSONObject resultsObj = new JSONObject(jsonString);

//            String veryLongString1 = resultsObj.toString();
//            for(int i = 0; i <= veryLongString1.length() / maxLogSize; i++) {
//                int start = i * maxLogSize;
//                int end = (i+1) * maxLogSize;
//                end = end > veryLongString1.length() ? veryLongString1.length() : end;
//                Log.v(TAG, "JSON Reviews object from jsonString: " + veryLongString1.substring(start, end));
//            }

            JSONArray reviewsJsonArray = resultsObj.getJSONArray(KEY_RESULTS_ARRAY);

//            String veryLongString2 = reviewsJsonArray.toString();
//            for(int i = 0; i <= veryLongString2.length() / maxLogSize; i++) {
//                int start = i * maxLogSize;
//                int end = (i+1) * maxLogSize;
//                end = end > veryLongString2.length() ? veryLongString2.length() : end;
//                Log.v(TAG, "JSON Reviews array: " + veryLongString2.substring(start, end));
//            }

            //loop through all the objects in the results array that should be returned in the JSON
            for(int i = 0; i < reviewsJsonArray.length(); i++) {
                //create new video object
                ReviewParcelable review = new ReviewParcelable();
                JSONObject reviewJsonObj = reviewsJsonArray.getJSONObject(i);
//                Log.v(TAG, "reviewJsonArray[" + i + "] : " + reviewJsonObj);
                //set values to object within the array
                review.setReviewAuthor(reviewJsonObj.getString(REVIEW_KEY_AUTHOR));
                review.setReviewContent(reviewJsonObj.getString(REVIEW_KEY_CONTENT));
                review.setReviewId(reviewJsonObj.getString(REVIEW_KEY_ID));
                review.setReviewUrl(reviewJsonObj.getString(REVIEW_KEY_URL));
                //set the movie obj to the corresponding position in the movie obj array
                reviewsList.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.v(TAG, "reviesList: " + reviewsList.toString());
//        Log.v(TAG, "review list array size: " + reviewsList.size());
        return reviewsList;
    }

}
