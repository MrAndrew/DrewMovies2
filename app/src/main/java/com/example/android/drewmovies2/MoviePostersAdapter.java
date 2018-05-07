package com.example.android.drewmovies2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.drewmovies2.models.MovieParcelable;
import com.example.android.drewmovies2.utils.BuildUrlUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class MoviePostersAdapter extends ArrayAdapter {

    private final LayoutInflater inflater;
    private ArrayList<String> imageUrls;
    private ArrayList<String> movieTitles;
//    private final String TAG = MoviePostersAdapter.class.getSimpleName();

    @SuppressWarnings("unchecked")
    public MoviePostersAdapter(Context context, ArrayList<MovieParcelable> movies) {
        super(context, R.layout.grid_item_movie_poster, movies);

        if (movies != null) {
            ArrayList<String> moviePosterUrls = new ArrayList<>();
            for (int i = 0; i < movies.size(); i++) {
                String urlPath = movies.get(i).getImageUrlPath();
                String imageUrl = BuildUrlUtils.buildImageRequestUrl(urlPath);
                moviePosterUrls.add(imageUrl);
            }
            this.imageUrls = moviePosterUrls;

            ArrayList<String> moviePosterTitles = new ArrayList<>();
            for (int i = 0; i < movies.size(); i++) {
                String title = movies.get(i).getMovieTitle();
                moviePosterTitles.add(title);
            }
            this.movieTitles = moviePosterTitles;
        }

        inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return movieTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    //didn't include a lot of the normal adapter methods because picasso seems to handle the position
    //of each item to associate with the position within the ArrayList<Movies> well enough, I would
    //probably have to change that if the app is changed to load longer lists of data than the currently
    //supplied amount of 20
    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            convertView = inflater.inflate(R.layout.grid_item_movie_poster, parent, false);
        }

        ImageView posterIv = convertView.findViewById(R.id.movie_poster_iv);
        TextView posterTv = convertView.findViewById(R.id.movie_poster_tv);

        //if/else statements prevent crash when user removes the last item from favorites
        //which creates an empty load of this adapter
        if(TextUtils.isEmpty(imageUrls.get(position))) {
            Picasso.get()
                    .cancelRequest(posterIv);
            posterIv.setImageDrawable(null);
        } else {
            Picasso.get()
                    .load(imageUrls.get(position))
                    .placeholder(R.drawable.the_movie_db_icon)
                    .error(R.drawable.the_movie_db_icon)
                    .into(posterIv);
            posterIv.setContentDescription(String.valueOf(R.string.movie_iv_content_description));
        }

        if(TextUtils.isEmpty(movieTitles.get(position))) {
            posterTv.setText(null);
        } else {
            posterTv.setText(movieTitles.get(position));
            posterTv.setTextColor(R.color.posterTextColor);
        }

        return convertView;
    }

}