package com.imagine.myapplication.PostActivitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;
import com.imagine.myapplication.Comment;
import com.imagine.myapplication.CommentsCallback;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.Post_Fragment_Classes.YouTubePostFragment;
import com.imagine.myapplication.R;
import com.imagine.myapplication.VoteHelper;
import com.imagine.myapplication.post_classes.Post;
import com.imagine.myapplication.post_classes.YouTubePost;
import com.imagine.myapplication.user_classes.UserActivity;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubePostActivity extends AppCompatActivity {
    private static final String TAG = "YouTubePostActivity";
    public ArrayList<Comment> comments;
    public YouTubePost post;
    public Context mContext = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_post);
    FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.post_frame, new YouTubePostFragment()).commit();
    // gets the post Object from the intent
    Intent intent = getIntent();
    String objString = intent.getStringExtra("post");
    Gson gson = new Gson();
    post = gson.fromJson(objString, YouTubePost.class);
}

    @Override
    protected void onStart() {
        //starts bind method and fetches the comments
        super.onStart();
        bind();
        Post_Helper helper = new Post_Helper();
        helper.getComments(post.documentID, new CommentsCallback() {
            @Override
            public void onCallback(ArrayList<Comment> comms) {
                comments = comms;
                initRecyclerView();
            }
        });
    }

    public void bind(){
        // sets up the postspecific Views
        init();
        TextView title_textView = findViewById(R.id.title_textView);
        TextView createTime_textView = findViewById(R.id.createDate_textView);
        TextView name_textView = findViewById(R.id.name_textView);
        ImageView profilePicture_imageView = findViewById(
                R.id.profile_picture_imageView);
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player);
        youTubePlayerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
            @Override
            public void onYouTubePlayer(YouTubePlayer youTubePlayer) {
                String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
                Pattern compilesPattern = Pattern.compile(pattern);
                Matcher matcher = compilesPattern.matcher(post.link);
                if(matcher.find()){
                    String result = matcher.group();
                    youTubePlayer.cueVideo(result,0);
                }
            }
        });

        title_textView.setText(post.title);
        createTime_textView.setText(post.createTime);

        if(post.originalPoster.equals("anonym")){
            name_textView.setText("Anonym");
            Glide.with(this).load(R.drawable.default_user).into(
                    profilePicture_imageView);
        }else{
            name_textView.setText(post.user.name);
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
        // initializes the recyclerView containing the comments
        // TODO: setting onScrollListener
        RecyclerView recyclerView = findViewById(R.id.post_activity_recyclerView);
        Comments_Adapter adapter = new Comments_Adapter(comments,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void init(){
        // adds the onClickListeners for the Buttons
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
        // changes ButtonUI when the buttons are clicked
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
                System.out.println("default case "+TAG);
                break;
        }
    }
}
