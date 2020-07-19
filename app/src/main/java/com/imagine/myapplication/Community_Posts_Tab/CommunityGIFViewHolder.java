package com.imagine.myapplication.Community_Posts_Tab;

import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.GIFPost;

public class CommunityGIFViewHolder extends CommunityCustomViewHolder {
    public CommunityGIFViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(GIFPost post){
        final VideoView videoView = itemView.findViewById(R.id.community_gif_videoView);
        videoView.setVideoPath(post.link);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                //Get your video's width and height
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();
                //Get VideoView's current width and height
                int videoViewWidth = videoView.getWidth();
                int videoViewHeight = videoView.getHeight();
                float xScale = (float) videoViewWidth / videoWidth;
                float yScale = (float) videoViewHeight / videoHeight;
                //For Center Crop use the Math.max to calculate the scale
                //float scale = Math.max(xScale, yScale);
                //For Center Inside use the Math.min scale.
                //I prefer Center Inside so I am using Math.min
                float scale = Math.max(xScale, yScale);
                float scaledWidth = scale * videoWidth;
                float scaledHeight = scale * videoHeight;
                //Set the new size for the VideoView based on the dimensions of the video
                ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
                layoutParams.width = (int)scaledWidth;
                layoutParams.height = (int)scaledHeight;
                videoView.setLayoutParams(layoutParams);
                videoView.start();
            }
        });
    }
}
