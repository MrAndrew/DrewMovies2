package com.example.android.drewmovies2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.drewmovies2.data.FavoriteMoviesContract;
import com.example.android.drewmovies2.models.MovieParcelable;
import com.example.android.drewmovies2.utils.BuildUrlUtils;
import com.example.android.drewmovies2.utils.LoadAndParseUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieGridActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

//    final static private String TAG = MovieGridActivity.class.getSimpleName();
    @BindView(R.id.movie_posters_gv)
    GridView moviesGridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);
        ButterKnife.bind(this);

        //shared preference methods will handle calling the async task to load movie data
        setupSharedPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    //returns URL because it's only used to change search atm
    private void setupSharedPreferences() {
        URL returnUrl;
        boolean favoriteSelection;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortPref = sharedPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_popular_value));
//        Log.v(TAG, "sortPref: " + sortPref);
        if(sortPref.equals(getString(R.string.pref_popular_value))) {
            //set activity title to show user
            setTitle(getResources().getText(R.string.movie_list_pop_title));
            returnUrl = BuildUrlUtils.buildMovieListPopRequestUrl();
            favoriteSelection = false;
//            Log.v(TAG, "returnURLpop: " + returnUrl);Log.v(TAG, "returnURL: " + returnUrl);
        } else if (sortPref.equals(getString(R.string.pref_top_rated_value))) {
            //set activity title to show user
            setTitle(getResources().getText(R.string.movie_list_top_title));
            returnUrl = BuildUrlUtils.buildMovieListRatedRequestUrl();
            favoriteSelection = false;
//            Log.v(TAG, "returnURLrated: " + returnUrl);Log.v(TAG, "returnURL: " + returnUrl);
        } else if (sortPref.equals(getString(R.string.pref_favorites_value))) {
            //set activity title to show user
            setTitle(getResources().getText(R.string.movie_list_fav_title));
            returnUrl = null;
            favoriteSelection = true;
//            Log.v(TAG, "Favorites view option selected");
        } else {
            returnUrl = null;
            favoriteSelection = false;
//            Log.v(TAG, "none of the sort preferences checked in if-else");
        }
//        Log.v(TAG, "returnURL: " + returnUrl);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        URL movieListUrl = returnUrl;

        loadVideoList(movieListUrl, favoriteSelection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_pref_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.movie_display_settings) {
            Intent startSettingsActivity = new Intent(this, UserSettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_key))) {
            URL returnUrl;
            boolean favoriteSelection;
            String sortPref = sharedPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_popular_value));
//            Log.v(TAG, "sortPref: " + sortPref);
            if(sortPref.equals(getString(R.string.pref_popular_value))) {
                returnUrl = BuildUrlUtils.buildMovieListPopRequestUrl();
                favoriteSelection = false;
//                Log.v(TAG, "returnURLpop: " + returnUrl);Log.v(TAG, "returnURL: " + returnUrl);
            } else if (sortPref.equals(getString(R.string.pref_top_rated_value))) {
                returnUrl = BuildUrlUtils.buildMovieListRatedRequestUrl();
                favoriteSelection = false;
//                Log.v(TAG, "returnURLrated: " + returnUrl);Log.v(TAG, "returnURL: " + returnUrl);
            } else if (sortPref.equals(getString(R.string.pref_favorites_value))) {
                returnUrl = null;
                favoriteSelection = true;
//                Log.v(TAG, "Favorites view option selected");
            } else {
                returnUrl = null;
                favoriteSelection = false;
//                Log.v(TAG, "none of the sort preferences checked in if-else");
            }

//            Log.v(TAG, "returnURL: " + returnUrl);
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
            URL movieListUrl = returnUrl;

            loadVideoList(movieListUrl, favoriteSelection);

        }
    } //end onSharedPreferenceChanged

    private void loadVideoList(URL movieListUrl, boolean favoriteSelection) {
        //reloads movie list as soon as the user selects a different search/sort option
        if (movieListUrl != null && !favoriteSelection) {
            //only starts async task if url exists and user didn't select to view favorites
            boolean isConnected = isConnected();
            if(isConnected) {
                new MovieDbQueryTask().execute(movieListUrl);
            } else {
                Toast.makeText(MovieGridActivity.this, "No Internet Connection Detected",
                        Toast.LENGTH_SHORT).show();
                moviesGridView.setAdapter(null);
            }
        } else if ( movieListUrl == null && favoriteSelection) {
            //loads movies from db save and recreates the objects to mimic same UI functionality
            final ArrayList<MovieParcelable> favMovies = new ArrayList<>();
            Uri uri = FavoriteMoviesContract.FavoriteEntry.CONTENT_URI;
            Cursor moviesCursor = getContentResolver().query(uri, null, null, null, null);
            //sets movies into new array list from content resolver returned cursor
            try {
                for (int i = 0; i < Objects.requireNonNull(moviesCursor).getCount(); i++) {
                    MovieParcelable movie = new MovieParcelable();
                    // Indices for the _id, description, and priority columns
//                    int idIndex = moviesCursor.getColumnIndex(FavoriteMoviesContract.FavoriteEntry._ID);
                    int movieIdIndex = moviesCursor.getColumnIndex(FavoriteMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID);
                    int titleIndex = moviesCursor.getColumnIndex(FavoriteMoviesContract.FavoriteEntry.COLUMN_TITLE);
                    int imageUrlIndex = moviesCursor.getColumnIndex(FavoriteMoviesContract.FavoriteEntry.COLUMN_IMAGE_URL);
                    int plotIndex = moviesCursor.getColumnIndex(FavoriteMoviesContract.FavoriteEntry.COLUMN_PLOT);
                    int releaseDateIndex = moviesCursor.getColumnIndex(FavoriteMoviesContract.FavoriteEntry.COLUMN_RELEASE_DATE);
                    int ratingIndex = moviesCursor.getColumnIndex(FavoriteMoviesContract.FavoriteEntry.COLUMN_RATING);

                    moviesCursor.moveToPosition(i); // get to the right location in the cursor

                    // Determine the values of the wanted data
                    String title = moviesCursor.getString(titleIndex);
                    Integer movieId = moviesCursor.getInt(movieIdIndex);
                    String movieImagePath = moviesCursor.getString(imageUrlIndex);
                    String plot = moviesCursor.getString(plotIndex);
                    String releaseDate = moviesCursor.getString(releaseDateIndex);
                    Double userRating = moviesCursor.getDouble(ratingIndex);

                    //Set values
                    movie.setMovieTitle(title);
                    movie.setMovieId(movieId);
                    movie.setImageUrlPath(movieImagePath);
                    movie.setAbout(plot);
                    movie.setReleaseDate(releaseDate);
                    movie.setUserRating(userRating);

                    favMovies.add(movie);
                }
            } finally {
                Objects.requireNonNull(moviesCursor).close();
            }
            //same as AsyncTask to replicate UI
            if (favMovies != null) {
                MoviePostersAdapter adapter = new MoviePostersAdapter(getApplicationContext(), favMovies);
                moviesGridView.setAdapter(adapter);
                moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Toast.makeText(MovieGridActivity.this, "" + favMovies.get(position).getMovieTitle(),
                                Toast.LENGTH_SHORT).show();
                        Intent startMovieDetailIntent = new Intent(MovieGridActivity.this, MovieDetailActivity.class);
                        startMovieDetailIntent.putExtra("movie_object", favMovies.get(position));
                        startActivity(startMovieDetailIntent);
                    }
                });
            }
        } else if (movieListUrl == null && !favoriteSelection) {
            Toast.makeText(MovieGridActivity.this, "No valid preference selected!!!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setupSharedPreferences();
    }

    //inner class to async load movie list (suppress to get lint error to go away, cannot make it static)
    @SuppressLint("StaticFieldLeak")
    class MovieDbQueryTask extends AsyncTask<URL, Void, ArrayList<MovieParcelable>> {

        @Override
        protected ArrayList<MovieParcelable> doInBackground(URL... urls) {
            URL loadMoviesUrl = urls[0];
            if (loadMoviesUrl == null) {
                this.cancel(true);
            }
//            Log.v(TAG, "loadMoviesUrl: "+ urls[0]);
            ArrayList<MovieParcelable> movieDbResults = null;
            try {
                movieDbResults = LoadAndParseUtils.loadMoviesJsonFromUrl(Objects.requireNonNull(loadMoviesUrl));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieDbResults;
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(MovieGridActivity.this, getString(R.string.network_error),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(final ArrayList<MovieParcelable> movies) {
//            Log.d(TAG, "ArrayList<Movie>: " + movies);
            if (movies != null) {
                MoviePostersAdapter adapter = new MoviePostersAdapter(getApplicationContext(), movies);
                moviesGridView.setAdapter(adapter);
                moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Toast.makeText(MovieGridActivity.this, "" + movies.get(position).getMovieTitle(),
                                Toast.LENGTH_SHORT).show();
                        Intent startMovieDetailIntent = new Intent(MovieGridActivity.this, MovieDetailActivity.class);
                        startMovieDetailIntent.putExtra("movie_object", movies.get(position));
                        startActivity(startMovieDetailIntent);
                    }
                });
            } else {
                Toast.makeText(MovieGridActivity.this, getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
            }
        }

    } //end movielist query Async task

}