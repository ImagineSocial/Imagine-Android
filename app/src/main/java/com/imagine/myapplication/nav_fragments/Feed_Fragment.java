package com.imagine.myapplication.nav_fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.health.SystemHealthManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.FeedAdapter;
import com.imagine.myapplication.FirebaseCallback;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.ImagineCommunity.InfoDialogFragment;
import com.imagine.myapplication.ImagineCommunity.InfoDialogType;
import com.imagine.myapplication.R;
import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class Feed_Fragment extends Fragment {

    private static final String TAG = "Feed_Fragment";
    public RecyclerView recyclerView;
    public int lastPosition;
    public ArrayList<Post> postList = new ArrayList<Post>();
    public Post_Helper helper;
    public Activity mainActivity;
    public FeedAdapter adapter;
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
        if(this.helper == null){
            helper = new Post_Helper(getContext());
        }
        final SwipeRefreshLayout swipe = view.findViewById(R.id.swipeMainFeed);
        if(postList.size() == 0){
            swipe.setRefreshing(true);
            helper.getPostsForMainFeed( new FirebaseCallback() {
                @Override
                public void onCallback(ArrayList<Post> values) {
                    postList = sortPostList(values);
                    swipe.setRefreshing(false);
                    initRecyclerView(view);
                }

            });
        }else {
            initRecyclerView(view);
            loadPosition();
        }
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFeed(swipe);
            }
        });
    }

    public void refreshFeed(final SwipeRefreshLayout refreshLayout) {
        helper.getPostsForMainFeed( new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<Post> values) {
                postList = sortPostList(values);
                if (refreshLayout != null) {
                    refreshLayout.setRefreshing(false);
                }
                if(recyclerView != null){
                    FeedAdapter adapter = (FeedAdapter) recyclerView.getAdapter();
                    adapter.refreshPosts(sortPostList(values));
                    adapter.notifyDataSetChanged();
                }
            }

        });
    }

    private void initRecyclerView (final View view){
        // initializes the recyclerView for the feed
        this.recyclerView = view.findViewById(R.id.feed_recyclerView);
        Context context = view.getContext();
        if(this.adapter == null){
            this.adapter = new FeedAdapter(postList,context);
        }
        adapter.mainActivity = this.mainActivity;
        recyclerView.setAdapter(this.adapter);
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
                LocaleList localeList = getResources().getConfiguration().getLocales();
                Locale locale = localeList.get(0);
                System.out.println(locale.getLanguage());
                if(loading && totalItemCount > previousTotal){
                    loading = false;
                    previousTotal = totalItemCount;
                }
                if((totalItemCount-(pastVisibleItems+visibleItemCount))<=2&&!loading){
                    loading = true;
                   helper.getMorePostsForFeed(new FirebaseCallback() {
                       @Override
                       public void onCallback(ArrayList<Post> values) {
                           postList = sortPostList(values);
                           adapter.addMorePosts(postList);
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

    public void setLinkedFact(){

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void checkForIntro(){
        System.out.println("!");
        String langPref = "info_likes";
        SharedPreferences prefs = getContext().getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        Boolean alreadyLaunched = prefs.getBoolean(langPref, false);
        if (!alreadyLaunched) {
            InfoDialogFragment frag = new InfoDialogFragment(getContext());
            frag.type = InfoDialogType.likes;
            frag.show();
        }
    }
}
