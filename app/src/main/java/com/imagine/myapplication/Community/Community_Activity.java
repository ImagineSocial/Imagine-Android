package com.imagine.myapplication.Community;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.FeedAdapter;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.FirebaseCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;

public class Community_Activity extends AppCompatActivity {
    boolean recyclerSet = false;
    ArrayList<Post> postList = new ArrayList<>();
    Post_Helper helper = new Post_Helper();
    RecyclerView recyclerView;
    Community community;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_activity);
        recyclerView = findViewById(R.id.comm_activity_recyclerView);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String imageURL = intent.getStringExtra("imageURL");
        String commID = intent.getStringExtra("commID");
        String description = intent.getStringExtra("description");

        if(imageURL == null){
            imageURL = "";
        }

        this.community = new Community(name,imageURL,commID,description);
        helper.getPostsForCommunityFeed(commID,new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<Post> values) {
                postList = values;
                if(!recyclerSet){
                    initRecyclerView();
                }else{
                    FeedAdapter adapter =(FeedAdapter) recyclerView.getAdapter();
                    adapter.addMorePosts(values);
                    adapter.notifyDataSetChanged();
                }
            }
        });


    }

    public void initRecyclerView(){
        this.recyclerSet = true;
        FeedAdapter adapter = new CommunityFeedAdapter(postList,this.community,this);
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // TODO
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
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
                    helper.getMorePostsForCommunityFeed(new FirebaseCallback() {
                        @Override
                        public void onCallback(ArrayList<Post> values) {

                            postList = values;
                            FeedAdapter adapter = (FeedAdapter) recyclerView.getAdapter();
                            adapter.addMorePosts(values);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}
