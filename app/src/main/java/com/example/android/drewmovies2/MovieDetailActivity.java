package com.example.android.drewmovies2;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.drewmovies2.data.FavoriteMoviesContract;
import com.example.android.drewmovies2.models.MovieParcelable;
import com.example.android.drewmovies2.models.VideoParcelable;
import com.example.android.drewmovies2.utils.BuildUrlUtils;
import com.example.android.drewmovies2.utils.LoadAndParseUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity implements VideoListAdapter.ListItemClickListener {

    @BindView(R.id.movie_detail_iv_thumbnail)
    ImageView mMovieIv;
    @BindView(R.id.movie_title_tv)
    TextView mTitleTv;
    @BindView(R.id.movie_plot_tv)
    TextView mPlotTv;
    @BindView(R.id.user_rating_tv)
    TextView mRatingTv;
    @BindView(R.id.release_date_tv)
    TextView mReleaseDateTv;
    @BindView(R.id.video_links_rv)
    RecyclerView mVideoListRv;
    @BindView(R.id.favorite_button)
    CheckBox mFavBtn;
    private ShareActionProvider mShareActionProvider;

//    private final String TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_activity);
        ButterKnife.bind(this);
        //set activity title to show user
        setTitle(getResources().getText(R.string.movie_detail_title));

        //have to getParcelableExtra to allow abstract object to pass between intents
        final MovieParcelable movie = getIntent().getParcelableExtra("movie_object");

        //set view values based on object passed into the intent
        String urlPath = movie.getImageUrlPath();
        String imageUrl = BuildUrlUtils.buildImageRequestUrl(urlPath);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.the_movie_db_icon)
                .error(R.drawable.the_movie_db_icon)
                .into(mMovieIv);
        //set's the content description to update per movie, this should help with accessibility
        mMovieIv.setContentDescription(getString(R.string.movie_iv_content_description) + movie.getMovieTitle());
        mTitleTv.setText(movie.getMovieTitle());
        mPlotTv.setText(movie.getAbout());
        mReleaseDateTv.setText(movie.getReleaseDate());
        Double rating = movie.getUserRating();
        mRatingTv.setText(String.valueOf(rating));

        //build urls for reviews and videos
        URL videosUrl = BuildUrlUtils.buildMovieVideosRequestUrl(String.valueOf(movie.getMovieId()));
//        Log.v(TAG, "reviewsUrl: " + reviewsUrl);
//        Log.v(TAG, "videosurl: " + videosUrl);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mVideoListRv.setLayoutManager(layoutManager);
        mVideoListRv.setHasFixedSize(true);

        //finds the selected movie in the favorites db, if it's there
        String movieId = Integer.toString(movie.getMovieId());

        try (Cursor movieCursor = getContentResolver().query(Uri.withAppendedPath(FavoriteMoviesContract.FavoriteEntry.CONTENT_URI,
                movieId), null, null, null, null)) {
            if (Objects.requireNonNull(movieCursor).getCount() == 0) {
                mFavBtn.setChecked(false);
            } else {
                mFavBtn.setChecked(true);
            }
        }

        mFavBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mFavBtn.isChecked()) {
                    String movieId = Integer.toString(movie.getMovieId());
                    Uri uri = FavoriteMoviesContract.FavoriteEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(movieId).build();
                    int deletedmovie = getContentResolver().delete(uri, null, null);
                    // notifies user of successful deletion because it should return 1
                    if (deletedmovie == 1) {
                        Toast.makeText(MovieDetailActivity.this, movie.getMovieTitle() + " removed from favorites",
                                Toast.LENGTH_SHORT).show();
                    }
                } else if(mFavBtn.isChecked()) {
                    //get values to insert into db
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FavoriteMoviesContract.FavoriteEntry.COLUMN_TITLE, movie.getMovieTitle());
                    contentValues.put(FavoriteMoviesContract.FavoriteEntry.COLUMN_IMAGE_URL, movie.getImageUrlPath());
                    contentValues.put(FavoriteMoviesContract.FavoriteEntry.COLUMN_RATING, movie.getUserRating());
                    contentValues.put(FavoriteMoviesContract.FavoriteEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                    contentValues.put(FavoriteMoviesContract.FavoriteEntry.COLUMN_PLOT, movie.getAbout());
                    contentValues.put(FavoriteMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID, movie.getMovieId());
                    //insert into db
                    Uri uri = getContentResolver().insert(FavoriteMoviesContract.FavoriteEntry.CONTENT_URI, contentValues);
                    //displays successful message to user
                    if(uri != null) {
                        Toast.makeText(MovieDetailActivity.this, movie.getMovieTitle() + " added to favorites.",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        //only loads if device has internet connection to prevent errors and crashes
        boolean isConnected = isConnected();
        if(isConnected) {
            new VideoQueryTask().execute(videosUrl);
        }

    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void onListItemClick(View v, String videoKey) {
        //TODO CHECK IF IT WAS SHARE BTN OR PLAY BUTTON AND START CORRECT INTENT ACCORDINGLY
        if(v == findViewById(R.id.video_link_btn)) {
            //used answer from StackOverflow at: "https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent"
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKey));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + videoKey));
            try {
                this.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                this.startActivity(webIntent);
            }
        } else if(v == findViewById(R.id.video_share_btn)) {
//            Toast.makeText(MovieDetailActivity.this, "Clicked Share button.",
//                    Toast.LENGTH_SHORT).show();
            //found solution here: "https://developer.android.com/training/sharing/send"
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            Uri videoLink = Uri.parse("http://www.youtube.com/watch?v=" + videoKey);
            sendIntent.putExtra(Intent.EXTRA_TEXT, videoLink.toString());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
    }


    public void startReviewsActivity(View view) {
        Intent startReviewsIntent = new Intent(MovieDetailActivity.this, MovieReviewsActivity.class);
        MovieParcelable movie = getIntent().getParcelableExtra("movie_object");
//        Log.v(TAG, "movie for review act: " + movie);
        startReviewsIntent.putExtra("movie_object", movie);
        //checks for internet connection before loading reviews to prevent app crash
        boolean isConnected = isConnected();
        if(isConnected) {
            startActivity(startReviewsIntent);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class VideoQueryTask extends AsyncTask<URL, Void, ArrayList<VideoParcelable>> {

        @Override
        protected ArrayList<VideoParcelable> doInBackground(URL... urls) {
            URL loadVideosUrl = urls[0];
//            Log.v(TAG, "loadVideosUrl: "+ urls[0]);
            ArrayList<VideoParcelable> videosDbResults = null;
            try {
                videosDbResults = LoadAndParseUtils.loadVideos(loadVideosUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return videosDbResults;
        }

        @Override
        protected void onPostExecute(final ArrayList<VideoParcelable> videos) {
//            Log.d(TAG, "ArrayList<Video>: " + videos);
            VideoListAdapter mVideoAdapter = new VideoListAdapter(videos, MovieDetailActivity.this);
            mVideoListRv.setAdapter(mVideoAdapter);
        }

    }//end async task

}
