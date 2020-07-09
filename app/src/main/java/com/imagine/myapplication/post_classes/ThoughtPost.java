package com.imagine.myapplication.post_classes;

import com.google.firebase.Timestamp;

import java.util.Date;

public class ThoughtPost extends Post {

    // Constructor
    public ThoughtPost(String title, String documentID, String description, String report, String createTime,
                       Timestamp createTimestamp, String originalPoster, long thanksCount, long wowCount, long haCount,
                       long niceCount, String type) {
        super(title, documentID, description, report, createTime, createTimestamp, originalPoster,
                thanksCount, wowCount, haCount, niceCount, type);
    }

    public ThoughtPost(){

    }

    //------------Setter-Functionen------------
    //------------Setter-Functionen------------
    //ThoughtPost hat keine eigenen Setter und
    //Getter die benötigten werden von Post
    //geerbt
    //------------Getter-Functionen------------
    //------------Getter-Functionen------------

}