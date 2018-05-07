package com.example.android.drewmovies2;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.drewmovies2.models.MovieParcelable;
import com.example.android.drewmovies2.models.ReviewParcelable;
import com.example.android.drewmovies2.utils.BuildUrlUtils;
import com.example.android.drewmovies2.utils.LoadAndParseUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieReviewsActivity extends AppCompatActivity {

//    private String TAG = MovieReviewsActivity.class.getSimpleName();

    @BindView(R.id.movie_detail_iv_thumbnail)
    ImageView mMovieIv;
    @BindView(R.id.movie_title_tv)
    TextView mTitleTv;
    @BindView(R.id.movie_reviews_rv)
    RecyclerView mReviewListRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_review_activity);
        ButterKnife.bind(this);
        //set activity title to show user
        setTitle(getResources().getText(R.string.movie_reviews_title));

        //have to getParcelableExtra to allow abstract object to pass between intents
        MovieParcelable movie = getIntent().getParcelableExtra("movie_object");

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

        //build urls for reviews and videos
        URL reviewsUrl = BuildUrlUtils.buildMovieReviewsRequestUrl(String.valueOf(movie.getMovieId()));
//        Log.v(TAG, "reviewsUrl: " + reviewsUrl);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mReviewListRv.setLayoutManager(layoutManager);
        mReviewListRv.setHasFixedSize(true);

        new ReviewQueryTask().execute(reviewsUrl);
    }

    @SuppressLint("StaticFieldLeak")
    class ReviewQueryTask extends AsyncTask<URL, Void, ArrayList<ReviewParcelable>> {

        @Override
        protected ArrayList<ReviewParcelable> doInBackground(URL... urls) {
            URL loadReviewsUrl = urls[0];
//            Log.v(TAG, "loadReviewsUrl: "+ urls[0]);
            ArrayList<ReviewParcelable> reviewResults = null;
            try {
                reviewResults = LoadAndParseUtils.loadReviews(loadReviewsUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reviewResults;
        }

        @Override
        protected void onPostExecute(final ArrayList<ReviewParcelable> reviews) {
//            Log.d(TAG, "ArrayList<Review>: " + reviews);
            ReviewListAdapter mReviewAdapter = new ReviewListAdapter(reviews);
            mReviewListRv.setAdapter(mReviewAdapter);
        }

    }//end async task

}
