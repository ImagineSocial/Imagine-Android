package com.imagine.myapplication;

import com.imagine.myapplication.post_classes.Post;

import java.util.ArrayList;

public interface ItemCallback {
    public void onCallback(ArrayList<Post> items);
}
