package com.imagine.myapplication.Runnables;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.storage.FirebaseStorage;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.FirebaseCallback;
import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;

public class PostFetchRunnable implements Runnable {

    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseStorage db = FirebaseStorage.getInstance();
    public Post_Helper helper;
    public Context mContext;
    ArrayList<Post> postlist;

    public PostFetchRunnable(Context mContext){
        this.mContext = mContext;
        this.helper = new Post_Helper(mContext);
    }

    @Override
    public void run() {
        helper.getPostsForMainFeed(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<Post> values) {
                postlist = values;
                signalUIThread();
            }
        });
    }

    public void signalUIThread(){
        //TODO
    }
}
