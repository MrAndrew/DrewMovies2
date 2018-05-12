package com.example.android.drewmovies2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
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
    @BindView(R.id.movie_detail_sv)
    ScrollView svDetails;

    private Parcelable videoRvState;
    private ArrayList<VideoParcelable> mvideos;

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
        if (savedInstanceState == null) {
            boolean isConnected = isConnected();
            if(isConnected) {
                new VideoQueryTask().execute(videosUrl);
            }
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void onListItemClick(Integer v, String videoKey) {
        //use integers because passing in a view only works for the first item in the list
        if(v == 1) {
            //used answer from StackOverflow at: "https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent"
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKey));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + videoKey));
            if (appIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(appIntent);
            } else if (webIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(webIntent);
            }
        } else if(v == 2) {
//            Toast.makeText(MovieDetailActivity.this, "Clicked Share button.",
//                    Toast.LENGTH_SHORT).show();
            //found solution here: "https://developer.android.com/training/sharing/send"
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            Uri videoLink = Uri.parse("http://www.youtube.com/watch?v=" + videoKey);
            sendIntent.putExtra(Intent.EXTRA_TEXT, videoLink.toString());
            sendIntent.setType("text/plain");
            if (sendIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(sendIntent);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //saves variables when rotation changes or activity is paused, etc.
        final int[] position = savedInstanceState.getIntArray("SV_POSITION");
        if(position != null) {
            svDetails.post(new Runnable() {
                @Override
                public void run() {
                    svDetails.scrollTo(position[0], position[1]);
                }
            });
        }
        if(savedInstanceState != null){
            //referenced this for solution: "https://stackoverflow.com/questions/36568168/how-to-save-scroll-position-of-recyclerview-in-android"
            mvideos = savedInstanceState.getParcelableArrayList("VIDEO_LIST");
            videoRvState = savedInstanceState.getParcelable("VIDEO_LIST_STATE");
            VideoListAdapter mVideoAdapter = new VideoListAdapter(mvideos, MovieDetailActivity.this);
            mVideoListRv.setAdapter(mVideoAdapter);
            mVideoListRv.getLayoutManager().onRestoreInstanceState(videoRvState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //restores saved instances between lifecyle events
        //referenced this for solution: "https://stackoverflow.com/questions/36568168/how-to-save-scroll-position-of-recyclerview-in-android"
        outState.putParcelable("VIDEO_LIST_STATE", mVideoListRv.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArrayList("VIDEO_LIST", mvideos);
        //saves sv, but not rv
        outState.putIntArray("SV_POSITION", new int[]{svDetails.getScrollX(), svDetails.getScrollY()});
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (videoRvState != null) {
            mVideoListRv.getLayoutManager().onRestoreInstanceState(videoRvState);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    //NOTE: Lint suggests removing view variable, but it's needed to keep app from crashing because using
    //onClick attribute in XML for the button instead of onClickListener
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
            mvideos = videos;
            VideoListAdapter mVideoAdapter = new VideoListAdapter(videos, MovieDetailActivity.this);
            mVideoListRv.setAdapter(mVideoAdapter);
        }

    }//end async task

}
