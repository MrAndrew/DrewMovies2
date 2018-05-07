package com.example.android.drewmovies2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.drewmovies2.models.VideoParcelable;

import java.util.ArrayList;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

//    private static final String TAG = VideoListAdapter.class.getSimpleName();

    private static ArrayList<VideoParcelable> videos;

    private final int mNumberitems;

    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(String videoKey);
    }

    public VideoListAdapter(ArrayList<VideoParcelable> inVideos, ListItemClickListener listener) {
        videos = inVideos;
        mNumberitems = inVideos.size();
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutId = R.layout.video_link_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutId, viewGroup, false);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberitems;
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View videoView;
        final Button playBtn;
        final TextView videoTitle;

        VideoViewHolder(View v) {
            super(v);

            videoView = v.findViewById(R.id.video_item_ll);
            playBtn = v.findViewById(R.id.video_link_btn);
            videoTitle = v.findViewById(R.id.video_title);
        }

        void bind(final int index) {
            //set title and link values here
            videoView.setTag(videos.get(index).getVideoKey());
            videoView.setOnClickListener(this);

            //TODO //CREATE SHARE INTENT TO SHARE YOUTUBE VIDEO LINK
            //TODO //ADD THE LINK BUTTON IN video_link_item.xml and remove view listener so
            //TODO //only play button opens youtube and share button opens Intent.createChooser()

            videoTitle.setText(videos.get(index).getVideoName());

            playBtn.setTag(videos.get(index).getVideoKey());
            playBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String videoKey = (String) v.getTag();
            mOnClickListener.onListItemClick(videoKey);
        }

    }

}
