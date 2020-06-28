package com.imagine.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.FeedAdapter;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {
    public ArrayList<Post> posts;
    public Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        this.mContext = this;
        Post_Helper helper = new Post_Helper();
        helper.getPostsForUserFeed(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<Post> values) {
                posts = values;
                initRecyclerView();
            }
        },"CZOcL3VIwMemWwEfutKXGAfdlLy1");

        Button logoutButton = findViewById(R.id.user_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                UserActivity.super.finish();
            }
        });
    }

    public void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.user_recyclerView);
        FeedAdapter adapter = new FeedAdapter(posts,mContext);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }
}
