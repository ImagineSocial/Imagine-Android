package com.imagine.myapplication;

import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;
import java.util.List;

public interface FirebaseCallback {
    void onCallback(ArrayList<Post> values);
}
