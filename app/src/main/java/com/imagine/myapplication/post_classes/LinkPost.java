package com.imagine.myapplication.post_classes;

import com.google.firebase.Timestamp;

import java.util.Date;

public class LinkPost extends Post {

    //Attribute von LinkPost
    public String link = "";
    //Constructor
    public LinkPost(String title,String documentID, String description, String report, Timestamp createTime,
                       String originalPoster, long thanksCount, long wowCount, long haCount,
                       long niceCount, String type, String link) {
        super(title,documentID,description,report,createTime,originalPoster,
                thanksCount,wowCount,haCount,niceCount,type);
        this.link = link;

    }

    //------------Setter-Functionen------------
    //------------Setter-Functionen------------

    public void setLink(String link) {
        this.link = link;
    }
    //------------Getter-Functionen------------
    //------------Getter-Functionen------------
    public String getLink() {
        return link;
    }

}