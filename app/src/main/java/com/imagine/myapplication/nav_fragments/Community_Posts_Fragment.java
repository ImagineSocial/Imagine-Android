package com.imagine.myapplication.nav_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.Community_Posts_Tab.Community_Posts_Adapter;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.FirebaseCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;

public class Community_Posts_Fragment extends Fragment {
    private static final String TAG = "Community_Posts_Fragment";
    public RecyclerView recyclerView;
    public int lastPostition;
    public GridLayoutManager gridLayoutManager;
    public ArrayList<Post> postList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Post_Helper helper = new Post_Helper();
        if(postList.size() == 0){
            helper.getCommunityPosts(new FirebaseCallback() {
                @Override
                public void onCallback(ArrayList<Post> values) {
                    postList = values;
                    initRecyclerView();
                }
            });
        }else{
            initRecyclerView();
            loadPosition();
        }
    }

    public void initRecyclerView(){
        this.recyclerView = getView().findViewById(R.id.community_posts_recyclerView);
        Community_Posts_Adapter adapter = new Community_Posts_Adapter(postList,getContext());
        this.gridLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(this.gridLayoutManager);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.gridLayoutManager =(GridLayoutManager) recyclerView.getLayoutManager();
        lastPostition = gridLayoutManager.findFirstVisibleItemPosition();
    }

    public void loadPosition(){
        this.gridLayoutManager.scrollToPosition(lastPostition);
    }
}
