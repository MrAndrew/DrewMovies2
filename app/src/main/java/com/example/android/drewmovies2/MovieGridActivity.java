package com.example.android.drewmovies2;

import android.annotation.SuppressLint;
import android.support.design.widget.FloatingActionButton;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
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

public class MovieGridActivity extends AppCompatActivity implements
//        SharedPreferences.OnSharedPreferenceChangeListener
    View.OnClickListener {

//    final static private String TAG = MovieGridActivity.class.getSimpleName();
    @BindView(R.id.movie_posters_gv)
    GridView moviesGridView;
    @BindView(R.id.fab_prime)
    FloatingActionButton fabPrime;
    @BindView(R.id.fab_pop)
    FloatingActionButton fabPop;
    @BindView(R.id.fab_top)
    FloatingActionButton fabTop;
    @BindView(R.id.fab_fav)
    FloatingActionButton fabFav;
    @BindView(R.id.fab_pop_tv)
    TextView fabPopTv;
    @BindView(R.id.fab_top_tv)
    TextView fabTopTv;
    @BindView(R.id.fab_fav_tv)
    TextView fabFavTv;

    private Boolean isFabOpen = false;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);
        ButterKnife.bind(this);

//        //set animation resources
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        //set click listeners
        fabPrime.setOnClickListener(this);
        fabPop.setOnClickListener(this);
        fabTop.setOnClickListener(this);
        fabFav.setOnClickListener(this);
    }

    //I found a tutorial and used in a previous practice project before starting this course and I
    //couldn't find the link again for this project submission, but I think this functions better for
    //the user than multiple clicks to a shared preference screen and I've significantly changed the
    //implementation to be far from the original source
    @Override
    public void onClick(View v) {
        int id = v.getId();
        URL movieListUrl;
        boolean favoriteSelection;
        switch (id){
            case R.id.fab_prime:
                //closes or opens fab once user selects plus icon
                animateFAB();
                break;
            case R.id.fab_pop:
                // sets view to and loads popular movies
                setTitle(getResources().getText(R.string.movie_list_pop_title));
                movieListUrl = BuildUrlUtils.buildMovieListPopRequestUrl();
                favoriteSelection = false;
                loadVideoList(movieListUrl, favoriteSelection);
                //closes fab once user selects an option
                animateFAB();
                break;
            case R.id.fab_top:
                // sets view to and loads top rated movies
                setTitle(getResources().getText(R.string.movie_list_top_title));
                movieListUrl = BuildUrlUtils.buildMovieListRatedRequestUrl();
                favoriteSelection = false;
                loadVideoList(movieListUrl, favoriteSelection);
                //closes fab once user selects an option
                animateFAB();
                break;
            case R.id.fab_fav:
                // sets view to and loads user's favorite movies
                setTitle(getResources().getText(R.string.movie_list_fav_title));
                movieListUrl = null;
                favoriteSelection = true;
                loadVideoList(movieListUrl, favoriteSelection);
                //closes fab once user selects an option
                animateFAB();
                break;
        }
    }

    public void animateFAB(){
    //animates opening/showing of fab button choices
        if(isFabOpen){
            fabPrime.startAnimation(rotate_backward);
            fabPop.startAnimation(fab_close);
            fabTop.startAnimation(fab_close);
            fabFav.startAnimation(fab_close);
            fabPopTv.startAnimation(fab_close);
            fabTopTv.startAnimation(fab_close);
            fabFavTv.startAnimation(fab_close);
            fabPop.setClickable(false);
            fabTop.setClickable(false);
            fabFav.setClickable(false);
            isFabOpen = false;

        } else {
            fabPrime.startAnimation(rotate_forward);
            fabPop.startAnimation(fab_open);
            fabTop.startAnimation(fab_open);
            fabFav.startAnimation(fab_open);
            fabPopTv.startAnimation(fab_open);
            fabTopTv.startAnimation(fab_open);
            fabFavTv.startAnimation(fab_open);
            fabPop.setClickable(true);
            fabTop.setClickable(true);
            fabFav.setClickable(true);
            isFabOpen = true;
        }
    }

    //TODO FIX SLOW PERFORMANCE ISSUE WITH LOADING FAVORITES IN GRID VIEW :(
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
        //makes removal of a favorite item appear as soon as the user returns from the detail view
        //but costs the user some performance as loading movie objects from the db takes some time :(
        if(this.getTitle() == getResources().getText(R.string.movie_list_fav_title)) {
            URL movieListUrl = null;
            boolean favoriteSelection = true;
            loadVideoList(movieListUrl, favoriteSelection);
        }
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
                movieDbResults = LoadAndParseUtils.loadMoviesJsonFromUrl(loadMoviesUrl);
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