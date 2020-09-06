package com.imagine.myapplication.PostActivitys;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;
import com.imagine.myapplication.Comment;
import com.imagine.myapplication.CommentsCallback;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.Post_Fragment_Classes.GifPostFragment;
import com.imagine.myapplication.R;
import com.imagine.myapplication.VoteHelper;
import com.imagine.myapplication.post_classes.GIFPost;
import com.imagine.myapplication.post_classes.Post;
import com.imagine.myapplication.user_classes.UserActivity;

import java.util.ArrayList;
import java.util.Date;

public class GifPostActivity extends AppCompatActivity {
    private static final String TAG = "GifPostActivity";
    public ArrayList<Comment> comments;
    public GIFPost post;
    public Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mContext = this;

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.post_frame,new GifPostFragment()).commit();
        // gets the post object from the intent
        Intent intent = getIntent();
        String objString = intent.getStringExtra("post");
        Gson gson = new Gson();
        this.post = gson.fromJson(objString, GIFPost.class);
    }

    @Override
    protected void onStart() {
        // stars the bind method and fetches the comments
        super.onStart();
        bind();
        Post_Helper helper = new Post_Helper();
        helper.getComments(post.documentID, new CommentsCallback() {
            @Override
            public void onCallback(ArrayList<Comment> comms) {
                System.out.println("!!");
                comments = comms;
                initRecyclerView();
            }
        });
    }

    public void bind(){
        // sets up the post specific views
        init();
        TextView title_textView = findViewById(R.id.title_textView);
        TextView createTime_textView = findViewById(R.id.createDate_textView);
        TextView username_textView = findViewById(R.id.name_textView);
        TextView description_textView = findViewById(R.id.description_tv);
        ImageView profilePicture_imageView = findViewById(
                R.id.profile_picture_imageView);
        final VideoView videoView = findViewById(R.id.gif_videoView);
        ConstraintLayout videoFrame = findViewById(R.id.video_frame);
        videoFrame.setClipToOutline(true);
        videoFrame.setClipToOutline(true);

        title_textView.setText(post.title);
        createTime_textView.setText(post.createTime);

        ConstraintLayout descriptionView = findViewById(R.id.description_view);
        if (post.description.equals("")) {
            descriptionView.setVisibility(View.INVISIBLE);
        } else {
            String description = post.description.replace("\\n", "\n");
            description_textView.setText(description);
        }
        videoView.setVideoPath(post.link);
        videoView.start();

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
            }
        });

        if(post.originalPoster.equals("anonym")){
            username_textView.setText("Anonym");
            Glide.with(this).load(R.drawable.default_user).into(
                    profilePicture_imageView);
        }else{
            username_textView.setText(post.user.name);
            profilePicture_imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    String userString = gson.toJson(post.user);
                    Intent intent = new Intent(mContext, UserActivity.class);
                    intent.putExtra("user",userString);
                    mContext.startActivity(intent);
                }
            });
            if(post.user.imageURL != null || !post.user.imageURL.equals("")){
                Glide.with(this).load(post.user.imageURL).into(
                        profilePicture_imageView);
            } else  {
                Glide.with(this).load(R.drawable.default_user).into(
                        profilePicture_imageView);
            }
        }
    }

    public void initRecyclerView(){
        // initializes the recyclerView
        // TODO: set up the onScrollistener!
        RecyclerView recyclerView = findViewById(R.id.post_activity_recyclerView);
        Comments_Adapter adapter = new Comments_Adapter(comments,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void init(){
        // sets up the onClickListeners for the buttons
        final VoteHelper vote = new VoteHelper();
        ImageButton thanksButton = findViewById(R.id.thanks_button);
        ImageButton wowButton = findViewById(R.id.wow_button);
        ImageButton haButton = findViewById(R.id.ha_button);
        ImageButton niceButton = findViewById(R.id.nice_button);
        thanksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote.updateVotes("thanks",post);
                upDateButtonUI("thanks",post);
            }
        });
        wowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote.updateVotes("wow",post);
                upDateButtonUI("wow",post);
            }
        });
        haButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote.updateVotes("ha",post);
                upDateButtonUI("ha",post);
            }
        });
        niceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vote.updateVotes("nice",post);
                upDateButtonUI("nice",post);
            }
        });
    }

    public void upDateButtonUI(String type, Post post){
        // changes the ButtonUI when a button is clicked
        switch(type){
            case "thanks":
                ImageButton thanksBUtton = findViewById(R.id.thanks_button);
                thanksBUtton.setBackground(null);
                break;
            case "wow":
                ImageButton wowButton = findViewById(R.id.wow_button);
                wowButton.setBackground(null);
                break;
            case "ha":
                ImageButton haButton = findViewById(R.id.ha_button);
                haButton.setBackground(null);
                break;
            case "nice":
                ImageButton niceButton = findViewById(R.id.nice_button);
                niceButton.setBackground(null);
                break;
            default:
                System.out.println("default case! "+TAG);
                break;
        }
    }
}
