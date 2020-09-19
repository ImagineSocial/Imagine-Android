package com.imagine.myapplication.Community_Posts_Tab;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.imagine.myapplication.PostActivitys.GifPostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.GIFPost;
import com.imagine.myapplication.post_classes.Post;
import com.imagine.myapplication.user_classes.User;

public class CommunityGIFViewHolder extends CommunityCustomViewHolder {

    public int frameWidth;
    public int frameHeight;


    public CommunityGIFViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(final GIFPost post){
        setLinkedFact(post.linkedFactId);
        if(post.user == null){
            post_helper.getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    post.user = user;
                    finishBind(post);
                }
            });
        }else{
            finishBind(post);
        }
    }

    public void finishBind(final GIFPost post){
        final VideoView videoView = itemView.findViewById(R.id.community_gif_videoView);
        final ConstraintLayout frame = itemView.findViewById(R.id.comm_posts_video_frame);
        ViewTreeObserver viewTreeObserver = frame.getViewTreeObserver();
        if(viewTreeObserver.isAlive()){
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    frame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    frameWidth = videoView.getWidth();
                    frameHeight = videoView.getHeight();
                }
            });
        }
        videoView.setVideoPath(post.link);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setVolume(0f,0f);

                //Get your video's width and height
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();
                //Get VideoView's current width and height
                float scale = (float) frameWidth / videoWidth;
                //For Center Crop use the Math.max to calculate the scale
                //float scale = Math.max(xScale, yScale);
                //For Center Inside use the Math.min scale.
                //I prefer Center Inside so I am using Math.min
                float scaledWidth = scale * videoWidth;
                float scaledHeight = scale * videoHeight;
                //Set the new size for the VideoView based on the dimensions of the video
                //ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
                //layoutParams.width = (int)frameWidth;
                //layoutParams.height = (int)frameHeight;
                //videoView.setLayoutParams(layoutParams);
                videoView.start();
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String objString = gson.toJson(post);
                Intent intent = new Intent(itemView.getContext(), GifPostActivity.class);
                intent.putExtra("post",objString);
                if(community != null && !community.equals("")){
                    String commString = gson.toJson(community);
                    intent.putExtra("comm",commString);
                }
                itemView.getContext().startActivity(intent);
            }
        });
    }


}
