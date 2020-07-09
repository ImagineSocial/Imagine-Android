package com.imagine.myapplication.user_classes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
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
import java.util.Collections;
import java.util.Comparator;

public class UserActivity extends AppCompatActivity {
    public ArrayList<Post> posts = new ArrayList<>();
    public Post_Helper helper = new Post_Helper();
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
        helper.getPostsForUserFeed(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<Post> values) {
                ArrayList<Post> sortedPosts = sortPostList(values);
                posts = sortedPosts;
                initRecyclerView();
            }
        },user.userID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.user_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            boolean loading = true;
            int previousTotal =0;
            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = lm.getChildCount();
                int totalItemCount = lm.getItemCount();
                int pastVisibleItems = lm.findFirstVisibleItemPosition();

                if(loading && totalItemCount > previousTotal){
                    loading = false;
                    previousTotal = totalItemCount;
                }
                if((totalItemCount-(pastVisibleItems+visibleItemCount))<=2&&!loading){
                    loading = true;
                    helper.getMorePostsForUserFeed(new FirebaseCallback() {
                        @Override
                        public void onCallback(ArrayList<Post> values) {
                            ArrayList<Post> sortedValues = sortPostList(values);
                            posts = sortedValues;
                            UserFeedAdapter adapter = (UserFeedAdapter) recyclerView.getAdapter();
                            adapter.addMorePosts(sortedValues);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    public ArrayList<Post> sortPostList(ArrayList<Post> posts){
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                if(o1.createTimestamp.getSeconds()>=
                        o2.createTimestamp.getSeconds()){
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        for(Post post : posts){
            System.out.println(post.createTimestamp.getNanoseconds());
        }
        return posts;

    }
}
