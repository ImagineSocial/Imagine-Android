package com.imagine.myapplication.PostActivitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;
import com.imagine.myapplication.Comment;
import com.imagine.myapplication.CommentsCallback;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.Post_Fragment_Classes.TranslationPostFragment;
import com.imagine.myapplication.R;
import com.imagine.myapplication.VoteHelper;
import com.imagine.myapplication.post_classes.Post;
import com.imagine.myapplication.post_classes.TranslationPost;
import com.imagine.myapplication.user_classes.UserActivity;

import java.util.ArrayList;
import java.util.Date;

public class TranslationPostActivity extends AppCompatActivity {

    private static final String TAG = "TranslationPostActivity";
    public ArrayList<Comment> comments;
    public TranslationPost post;
    public Context mContext = this;
    public Post_Helper helper = new Post_Helper();
    public ImageButton anonym;
    public ImageButton sendComment;
    public boolean anonymToggle = false;
    public EditText commentText;
    public String comment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.post_frame, new TranslationPostFragment()).commit();
        // gets the post object from the intent
        Intent intent = getIntent();
        String objString = intent.getStringExtra("post");
        Gson gson = new Gson();
        post = gson.fromJson(objString, TranslationPost.class);
        this.anonym = findViewById(R.id.post_commentview_anonym_button);
        this.sendComment = findViewById(R.id.post_commentview_send_button);
        this.commentText = findViewById(R.id.post_comment_edit_text);
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
                System.out.println("!!");
                comments = comms;
                initRecyclerView();
            }
        });
    }
    public void bind(){
        //sets up the postspecific views
        init();
        TextView title_textView = findViewById(R.id.title_textView);
        TextView createTime_textView = findViewById(R.id.createDate_textView);
        TextView name_textView = findViewById(R.id.name_textView);
        TextView description_textView = findViewById(R.id.description_tv);
        ImageView profilePicture_imageView = findViewById(
                R.id.profile_picture_imageView);

        title_textView.setText(post.title);
        createTime_textView.setText(post.createTime);

        ConstraintLayout descriptionView = findViewById(R.id.description_view);
        if (post.description.equals("")) {
            descriptionView.setVisibility(View.INVISIBLE);
        } else {
            String description = post.description.replace("\\n", "\n");
            description_textView.setText(description);
        }

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

        sendComment.setAlpha(0.5f);
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                comment  = s.toString();
                if(comment.equals("")){
                    sendComment.setAlpha(0.5f);
                    sendComment.setOnClickListener(null);
                }else{
                    sendComment.setAlpha(1f);
                    sendComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            helper.addCommentToFirebase(new CommentsCallback() {
                                @Override
                                public void onCallback(ArrayList<Comment> comms) {

                                }
                            },post,anonymToggle,comment);
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        anonym.setAlpha(0.5f);
        this.anonym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anonymToggle = !anonymToggle;
                if(anonymToggle){
                    anonym.setAlpha(1f);
                }else{
                    anonym.setAlpha(0.5f);
                }
            }
        });

        this.sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentText.getText().toString();
                if(comment.equals("")){
                    Toast.makeText(mContext,"Kein Text",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext,"Hat Text",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initRecyclerView(){
        //initializes the recyclerView
        //TODO: setting onScrollListener
        RecyclerView recyclerView = findViewById(R.id.post_activity_recyclerView);
        Comments_Adapter adapter = new Comments_Adapter(comments,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void init(){
        //sets up the onClicklisteners for the buttons
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
        //changes the ButtonUI if a button is clicked
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
