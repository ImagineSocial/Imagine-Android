package com.imagine.myapplication.post_classes;

import com.google.firebase.Timestamp;
import com.imagine.myapplication.user_classes.User;

public class PicturePost extends Post {
    // Attribute von PicturePosts
    public long imageHeight;
    public long imageWidth;
    public String imageURL = "";
    //Constructor
    public PicturePost(String title, String documentID, String description, String report, String createTime,
                       Timestamp createTimestamp, String originalPoster, long thanksCount, long wowCount, long haCount,
                       long niceCount, String type, long imageHeight, long imageWidth,
                       String imageURL) {
        super(title,documentID,description,report,createTime,createTimestamp,originalPoster,
                thanksCount,wowCount,haCount,niceCount,type);
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.imageURL = imageURL;
    }

    public PicturePost(){

    }

    //------------Setter-Functionen------------
    //------------Setter-Functionen------------
    public void setImageHeight(long imageHeight) { this.imageHeight = imageHeight; }

    public void setImageWidth(long imageWidth) { this.imageWidth = imageWidth; }

    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public void setUser(User user){
        this.user = user;
    }
    //------------Getter-Functionen------------
    //------------Getter-Functionen------------
    public long getImageHeight() { return imageHeight; }

    public long getImageWidth() { return imageWidth; }

    public String getImageURL() { return imageURL; }
}
