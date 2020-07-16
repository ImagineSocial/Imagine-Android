package com.imagine.myapplication.post_classes;

import com.google.firebase.Timestamp;

public class RepostPost extends Post {
    public String OGPostDocumentID;

    public RepostPost(String title, String documentID, String description,
                      String report, String createTime,Timestamp createTimestamp, String originalPoster,
                      long thanksCount, long wowCount, long haCount, long niceCount,
                      String type,String OGPostDocumentID) {
        super(title, documentID, description, report, createTime,createTimestamp, originalPoster,
                thanksCount, wowCount, haCount, niceCount, type);
        this.OGPostDocumentID = OGPostDocumentID;
    }
    //------------Setter-Functionen------------
    //------------Setter-Functionen------------
    public void setOGPostDocumentID(String OGPostDocumentID) {
        this.OGPostDocumentID = OGPostDocumentID;
    }
    //------------Getter-Functionen------------
    //------------Getter-Functionen------------
    public String getOGPostDocumentID() {
        return OGPostDocumentID;
    }
}
