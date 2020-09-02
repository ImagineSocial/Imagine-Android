package com.imagine.myapplication.nav_fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.FeedAdapter;
import com.imagine.myapplication.FirebaseCallback;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Feed_Fragment extends Fragment {

    private static final String TAG = "Feed_Fragment";
    public RecyclerView recyclerView;
    public int lastPosition;
    public ArrayList<Post> postList = new ArrayList<Post>();
    public Post_Helper helper = new Post_Helper();
    public boolean isloading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed,container,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        // fetches the post for the feed
        super.onViewCreated(view, savedInstanceState);
        if(postList.size() == 0){
            helper.getPostsForMainFeed( new FirebaseCallback() {
                @Override
                public void onCallback(ArrayList<Post> values) {
                    postList = sortPostList(values);
                    initRecyclerView(view);
                }

            });
        }else {
            initRecyclerView(view);
            loadPosition();
        }
    }

    private void initRecyclerView (final View view){
        // initializes the recyclerView for the feed
        this.recyclerView = view.findViewById(R.id.feed_recyclerView);
        Context context = view.getContext();
        FeedAdapter adapter = new FeedAdapter(postList,context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

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
                   helper.getMorePostsForFeed(new FirebaseCallback() {
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

    public void loadPosition(){
        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPosition(lastPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(recyclerView != null) {
            lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }
    }

    public ArrayList<Post> sortPostList(ArrayList<Post> posts){
        //orders the posts by createTime attribute
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
