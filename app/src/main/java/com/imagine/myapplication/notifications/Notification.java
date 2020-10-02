package com.imagine.myapplication.notifications;

import com.google.firebase.Timestamp;
import com.imagine.myapplication.post_classes.Post;

public class Notification {

    public String message;
    public String messageID;
    public String title;
    public String type;
    public String chatID;
    public Timestamp sentAt;
    public String button;
    public String postID;
    public String comment;
    public String friendRequestName;
    public int count = 1;
    public boolean isTopicPost = false;
    public Post post;
}
