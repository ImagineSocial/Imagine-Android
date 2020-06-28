package com.imagine.myapplication.post_classes;

import com.google.firebase.Timestamp;

import java.util.Date;

public class MultiPicturePost extends Post {
    // Attribute von MultiPicturePosts
    public long imageHeight;
    public long imageWidth;
    public String imageURL = "";
    public String[] imageURLs = null;
    // Constructor
    public MultiPicturePost(String title,String documentID, String description, String report, String createTime,
                            String originalPoster, long thanksCount, long wowCount, long haCount,
                            long niceCount, String type,long imageHeight,long imageWidth,
                            String imageURL, String [] imageURLs) {
        super(title,documentID,description,report,createTime,originalPoster,
                thanksCount,wowCount,haCount,niceCount,type);
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.imageURL = imageURL;
        this.imageURLs = imageURLs;
    }

    //------------Setter-Functionen------------
    //------------Setter-Functionen------------

    public void setImageHeight(long imageHeight) {
        this.imageHeight = imageHeight;
    }

    public void setImageWidth(long imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setImageURLs(String[] imageURLs) {
        this.imageURLs = imageURLs;
    }
    //------------Getter-Functionen------------
    //------------Getter-Functionen------------

    public long getImageHeight() {
        return imageHeight;
    }

    public long getImageWidth() {
        return imageWidth;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String [] getImageURLs() {
        return imageURLs;
    }
}