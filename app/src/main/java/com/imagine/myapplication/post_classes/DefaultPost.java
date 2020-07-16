package com.imagine.myapplication.post_classes;

import com.google.firebase.Timestamp;

public class DefaultPost extends Post {
    public DefaultPost(String title, String documentID, String description, String report,
                       String createTime,Timestamp createTimestamp, String originalPoster, long thanksCount, long wowCount,
                       long haCount, long niceCount, String type) {
        super(title, documentID, description, report, createTime,createTimestamp, originalPoster, thanksCount,
                wowCount, haCount, niceCount, type);
    }
}
