package com.imagine.myapplication.Community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.FirebaseCallback;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CommunityFragmentTwo extends Fragment {
    ArrayList<Post> postList = new ArrayList<>();
    Post_Helper helper = new Post_Helper();
    RecyclerView recyclerView;
    Community community;
    HashMap<String,String> args;

    public CommunityFragmentTwo(HashMap<String, String> args) {
        this.args = args;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.viewpager_test_two,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.comm_activity_recyclerView);
        String name = args.get("name");
        String imageURL = args.get("imageURL");
        String commID = args.get("commID");
        String description = args.get("description");

        if(imageURL == null){
            imageURL = "";
        }
        this.community = new Community(name,imageURL,commID,description);
        helper.getPostsForCommunityFeed(commID,new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<Post> values) {
                ArrayList<Post> sortedPosts = sortPostList(values);
                postList.addAll(sortedPosts);
                initRecyclerView();
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

    public void initRecyclerView(){
        CommunityFeedAdapter adapter = new CommunityFeedAdapter(postList,this.community,getContext());
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

                            postList.addAll(sortPostList(values));
                            CommunityFeedAdapter adapter = (CommunityFeedAdapter) recyclerView.getAdapter();
                            adapter.addMorePosts(values);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}
