package com.example.android.drewmovies2.models;

import android.os.Parcel;
import android.os.Parcelable;

//might not need Parcelable now, but will make it easier to include new features and speed up the
//process of transferring objects between activities and intents in the future if needed
public class VideoParcelable implements Parcelable {

    private String videoId;
    private String videoKey;
    private String videoSite;
    private String videoType;
    private String videoName;

    private VideoParcelable(Parcel in) {
        this.videoId = in.readString();
        this.videoKey = in.readString();
        this.videoSite = in.readString();
        this.videoType = in.readString();
        this.videoName = in.readString();
    }

    public VideoParcelable () {

    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoId);
        dest.writeString(videoKey);
        dest.writeString(videoSite);
        dest.writeString(videoType);
        dest.writeString(videoName);
    }

    public int describeContents() {
        return 0;
    }

    //getters
    public String getVideoId() {
        return videoId;
    }
    public String getVideoKey() {
        return videoKey;
    }
    public String getVideoSite() {
        return videoSite;
    }
    public String getVideoType() {
        return videoType;
    }
    public String getVideoName() {
        return videoName;
    }

    //setters
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }
    public void setVideoSite(String videoSite) {
        this.videoSite = videoSite;
    }
    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }
    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    static final Creator<VideoParcelable> CREATOR
            = new Creator<VideoParcelable>() {

        public VideoParcelable createFromParcel(Parcel in) {
            return new VideoParcelable(in);
        }

        public VideoParcelable[] newArray(int size) {
            return new VideoParcelable[size];
        }
    };

}
