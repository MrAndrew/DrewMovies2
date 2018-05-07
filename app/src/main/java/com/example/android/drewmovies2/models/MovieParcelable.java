package com.example.android.drewmovies2.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieParcelable implements Parcelable {

    private Integer movieId;
    private String movieTitle;
    private String imageUrlPath;
    private String about;
    private String releaseDate;
    private Double userRating;

    private MovieParcelable(Parcel in) {
        this.movieId = in.readInt();
        this.movieTitle = in.readString();
        this.imageUrlPath = in.readString();
        this.about = in.readString();
        this.releaseDate = in.readString();
        this.userRating = in.readDouble();
    }

    public MovieParcelable () {

    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(movieTitle);
        dest.writeString(imageUrlPath);
        dest.writeString(about);
        dest.writeString(releaseDate);
        dest.writeDouble(userRating);
    }

    public int describeContents() {
        return 0;
    }

    //getters
    public Integer getMovieId() {
        return movieId;
    }
    public String getMovieTitle() {
        return movieTitle;
    }
    public String getImageUrlPath() {
        return imageUrlPath;
    }
    public String getAbout() {
        return about;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public Double getUserRating() {
        return userRating;
    }

    //setters
    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }
    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
    public void setImageUrlPath(String imageUrlpath) {
        this.imageUrlPath = imageUrlpath;
    }
    public void setAbout(String about) {
        this.about = about;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    public void setUserRating(Double userRating) { this.userRating = userRating; }

    static final Creator<MovieParcelable> CREATOR
            = new Creator<MovieParcelable>() {

        public MovieParcelable createFromParcel(Parcel in) {
            return new MovieParcelable(in);
        }

        public MovieParcelable[] newArray(int size) {
            return new MovieParcelable[size];
        }
    };

}