package com.example.android.drewmovies2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.drewmovies2.models.ReviewParcelable;

import java.util.ArrayList;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {

//    private static final String TAG = ReviewListAdapter.class.getSimpleName();

    private static ArrayList<ReviewParcelable> reviews;

    private final int mNumberitems;

    public ReviewListAdapter(ArrayList<ReviewParcelable> inReviews) {
        reviews = inReviews;
        if(inReviews.size() > 0) {
            mNumberitems = inReviews.size();
        } else {
            mNumberitems = 1;
        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutId = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutId, viewGroup, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewListAdapter.ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberitems;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        //initiate views
        final TextView authorTv;
        final TextView contentTv;

        ReviewViewHolder(View v) {
            super(v);

            authorTv = v.findViewById(R.id.review_author_tv);
            contentTv = v.findViewById(R.id.review_content_tv);
        }

        void bind(final int index) {
            //set title and link values here
            if(reviews.size() > 0) {
                authorTv.setText(reviews.get(index).getReviewAuthor());
                contentTv.setText(reviews.get(index).getReviewContent());
            } else {
                authorTv.setText(R.string.empty_reviews);
            }
        }
    }

}
