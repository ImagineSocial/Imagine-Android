package com.imagine.myapplication.user_classes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.FeedAdapter;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.FirebaseCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {
    public ArrayList<Post> posts;
    public Context mContext;
    public User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        this.mContext = this;
        Gson gson = new Gson();
        Intent intent = getIntent();
        String userString = intent.getStringExtra("user");
        user = gson.fromJson(userString,User.class);
        Post_Helper helper = new Post_Helper();
        helper.getPostsForUserFeed(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<Post> values) {
                posts = values;
                initRecyclerView();
            }
        },user.userID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.user_toolbar);
        setSupportActionBar(toolbar);

        Button logout_button = findViewById(R.id.toolbar_logout_button);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (user != null) {
            if (currentUser.getUid().equals(user.userID)) {
                logout_button.setVisibility(View.VISIBLE);

                logout_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();

                        UserActivity.super.finish();
                    }
                });
            }
        } else {
            String test1 = user.userID;
            String test2 = currentUser.getUid();
            System.out.println("!!!");
        }
    }

    public void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.user_recyclerView);
        UserFeedAdapter adapter = new UserFeedAdapter(posts,mContext,user);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }
}
