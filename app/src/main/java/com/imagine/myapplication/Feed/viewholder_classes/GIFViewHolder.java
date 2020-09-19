package com.imagine.myapplication.Feed.viewholder_classes;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.PostActivitys.GifPostActivity;
import com.imagine.myapplication.R;
import com.imagine.myapplication.user_classes.User;
import com.imagine.myapplication.user_classes.UserActivity;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.post_classes.GIFPost;

import java.net.CacheRequest;
import java.util.LinkedHashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class GIFViewHolder extends CustomViewHolder {
    private static final String TAG = "GIFViewHolder";
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public GIFPost post;
    public User userObj;
    public Post_Helper helper = new Post_Helper();
    public int frameWidth;
    public int frameHeight;
    private MediaPlayer mediaPlayer;

    public GIFViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }



    public void bind(final GIFPost post){
        // calls the init method and sets up the post specific views
        this.post = post;
        init(post);
        //GIF Widgets
        TextView title_textView = itemView.findViewById(R.id.title_textView);
        TextView createTime_textView = itemView.findViewById(R.id.createDate_textView);
        TextView name_textView = itemView.findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);

        final VideoView videoView = itemView.findViewById(R.id.gif_videoView);
        title_textView.setText(post.title);
        createTime_textView.setText(post.createTime);
        videoView.setVideoPath(post.link);
        if(post.originalPoster.equals("anonym")){
            name_textView.setText("Anonym");
            Glide.with(itemView).load(R.drawable.anonym_user).into(
                    profilePicture_imageView);
        }else{
            helper.getUser(post.originalPoster, new UserCallback() {
                @Override
                public void onCallback(User user) {
                    userObj = user;
                    post.user = user;
                    setName(post);
                }
            });
        }
        setLinkedFact(post.linkedFactId);
        ConstraintLayout videoFrame = itemView.findViewById(R.id.video_frame);
        videoFrame.setClipToOutline(true);
        //Adjust the videoView to show the right ratio

        ViewTreeObserver viewTreeObserver = videoView.getViewTreeObserver();
        if(viewTreeObserver.isAlive()){
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    videoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    frameWidth = videoView.getWidth();
                    frameHeight = videoView.getHeight();
                }
            });
        }

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
                ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
                layoutParams.width = (int)scaledWidth;
                layoutParams.height = (int)scaledHeight;
                videoView.setLayoutParams(layoutParams);
                videoView.start();
            }
        });
        ImageButton options = itemView.findViewById(R.id.feed_menu_button);
        if(auth.getCurrentUser()!= null&& post.originalPoster.equals(auth.getCurrentUser().getUid())){
            options.setVisibility(View.VISIBLE);
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenu();
                }
            });
        } else {
            options.setVisibility(View.INVISIBLE);
        }

        CircleImageView topicPostImageView = itemView.findViewById(R.id.topicPostImageView);
        if (post.isTopicPost) {
            topicPostImageView.setVisibility(View.VISIBLE);
        }

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

    public void setName(final GIFPost post){
        // sets up the users views
        TextView username_textView = itemView.findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = itemView.findViewById(
                R.id.profile_picture_imageView);
        username_textView.setText(post.user.name);
        if(post.user.imageURL == null || post.user.imageURL == ""){
            Glide.with(itemView).load(R.drawable.default_user).into(
                    profilePicture_imageView
            );
        }
        else{
            Glide.with(itemView).load(post.user.imageURL).into(
                    profilePicture_imageView
            );
            profilePicture_imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    String userString = gson.toJson(userObj);
                    Intent intent = new Intent(mContext, UserActivity.class);
                    intent.putExtra("user",userString);
                    mContext.startActivity(intent);
                }
            });
        }
    }



    public void showMenu(){
        ImageButton options = itemView.findViewById(R.id.feed_menu_button);
        PopupMenu menu = new PopupMenu(itemView.getContext(),options);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.remove_post:
                        removePost(post);
                        return true;
                    case R.id.link_community:
                        linkCommunity(post);
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.feed_post_menu, menu.getMenu());
        menu.show();
    }

    public String getType(){
        return "GIF";
    }
}
