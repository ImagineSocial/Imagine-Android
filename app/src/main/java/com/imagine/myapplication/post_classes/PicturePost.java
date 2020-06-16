package com.imagine.myapplication.post_classes;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.imagine.myapplication.User;
import com.imagine.myapplication.UserCallback;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PicturePost extends Post {
    // Attribute von PicturePosts
    public long imageHeight;
    public long imageWidth;
    public String imageURL = "";
    //Constructor
    public PicturePost(String title,String documentID, String description, String report, Timestamp createTime,
                            String originalPoster, long thanksCount, long wowCount, long haCount,
                            long niceCount, String type,long imageHeight,long imageWidth,
                            String imageURL) {
        super(title,documentID,description,report,createTime,originalPoster,
                thanksCount,wowCount,haCount,niceCount,type);
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.imageURL = imageURL;
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
