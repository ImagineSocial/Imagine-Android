package com.imagine.myapplication;

import com.google.firebase.Timestamp;

public class Comment {

    public String body;
    public String userID;
    public long id;
    public Timestamp sentAt;
    public User user;

    public void setUser(User user){
        this.user = user;
    }

}
