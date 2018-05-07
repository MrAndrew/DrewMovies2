package com.example.android.drewmovies2.models;

import android.os.Parcel;
import android.os.Parcelable;

//might not need Parcelable now, but will make it easier to include new features and speed up the
//process of transferring objects between activities and intents in the future if needed
public class ReviewParcelable implements Parcelable {

    private String reviewAuthor;
    private String reviewContent;
    private String reviewId;
    private String reviewUrl;

    private ReviewParcelable(Parcel in) {
        this.reviewAuthor = in.readString();
        this.reviewContent = in.readString();
        this.reviewId = in.readString();
        this.reviewUrl = in.readString();
    }

    public ReviewParcelable () {

    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reviewAuthor);
        dest.writeString(reviewContent);
        dest.writeString(reviewId);
        dest.writeString(reviewUrl);
    }

    public int describeContents() {
        return 0;
    }

    //getters
    public String getReviewAuthor() {
        return reviewAuthor;
    }
    public String getReviewContent() {
        return reviewContent;
    }
    public String getReviewId() {
        return reviewId;
    }
    public String getReviewUrl() {
        return reviewUrl;
    }

    //setters
    public void setReviewAuthor(String reviewAuthor) {
        this.reviewAuthor = reviewAuthor;
    }
    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }

    static final Creator<ReviewParcelable> CREATOR
            = new Creator<ReviewParcelable>() {

        public ReviewParcelable createFromParcel(Parcel in) {
            return new ReviewParcelable(in);
        }

        public ReviewParcelable[] newArray(int size) {
            return new ReviewParcelable[size];
        }
    };

}
