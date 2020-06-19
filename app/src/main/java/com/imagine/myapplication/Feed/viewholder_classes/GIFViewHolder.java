package com.imagine.myapplication.Feed.viewholder_classes;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.PostActivitys.GifPostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.User;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.GIFPost;

public class GIFViewHolder extends CustomViewHolder {
    public Context mContext;

    public GIFViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public void bind(final GIFPost post){
        init(post);
        //GIF Widgets
        TextView title_textView = itemView.findViewById(R.id.title_textView);
        TextView createTime_textView = itemView.findViewById(R.id.createDate_textView);
        TextView name_textView = itemView.findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);
        final VideoView videoView = itemView.findViewById(R.id.gif_videoView);

        String date = dateToString(post.createTime);
        title_textView.setText(post.title+"  GIFPOST");
        createTime_textView.setText(date);
        videoView.setVideoPath(post.link);
        videoView.start();
        //MediaController mediaController = new MediaController(itemView.getContext());
        //videoView.setMediaController(mediaController);
        //mediaController.setAnchorView(videoView);

        if(post.originalPoster == "anonym"){
            name_textView.setText("Anonym");
            Glide.with(itemView).load(R.drawable.default_user).into(
                    profilePicture_imageView);
        }else{
            getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    post.user = user;
                    setName(post);
                }
            });
        }

        //Adjust the videoView to show the right ratio
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
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
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), GifPostActivity.class);
                itemView.getContext().startActivity(intent);
            }
        });
    }

    public void setName(GIFPost post){
        TextView username_textView = itemView.findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);
        username_textView.setText(post.user.name+ "  GIFPost");
        if(post.user.imageURL == null || post.user.imageURL == ""){
            Glide.with(itemView).load(R.drawable.default_user).into(
                    profilePicture_imageView
            );
        }
        else{
            Glide.with(itemView).load(post.user.imageURL).into(
                    profilePicture_imageView
            );
        }
    }

    public String getType(){
        return "GIF";
    }
}
