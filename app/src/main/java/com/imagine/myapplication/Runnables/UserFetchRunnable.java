package com.imagine.myapplication.Runnables;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.imagine.myapplication.Feed.viewholder_classes.Helpers_Adapters.Post_Helper;
import com.imagine.myapplication.UserCallback;
import com.imagine.myapplication.user_classes.User;

public class UserFetchRunnable implements Runnable {

    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseStorage db = FirebaseStorage.getInstance();
    public Context mContext;
    public Post_Helper helper;
    public String userID;
    public User userObj;

    public UserFetchRunnable(Context mContext, String userID){
        this.mContext = mContext;
        this.helper = new Post_Helper(mContext);
        this.userID = userID;
    }

    @Override
    public void run() {
        helper.getUser(userID, new UserCallback() {
            @Override
            public void onCallback(User user) {
                userObj = user;
                signalUIThread();
            }
        });
    }

    public void signalUIThread(){
        //TODO
    }
}
