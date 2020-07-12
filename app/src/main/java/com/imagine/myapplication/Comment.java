package com.imagine.myapplication;

import com.google.firebase.Timestamp;
import com.imagine.myapplication.user_classes.User;

public class Comment {

    public String body;
    public String userID;
    public long id;
    public Timestamp sentAt;
    public String sentAtString;
    public User user;

    public void setUser(User user){
        this.user = user;
    }

}
