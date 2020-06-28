package com.imagine.myapplication.post_classes;

import com.google.firebase.Timestamp;

public class TranslationPost extends Post{
    // Attribute von TranslationPost
    public String OGpostDocumentID;
    //Constructor
    public TranslationPost(String title, String documentID, String description, String report,
                           String createTime, String originalPoster, long thanksCount, long wowCount,
                           long haCount, long niceCount, String type, String OGpostDocumentID) {
        super(title, documentID, description, report, createTime, originalPoster, thanksCount,
                wowCount, haCount, niceCount, type);
        this.OGpostDocumentID = OGpostDocumentID;
    }
    //------------Setter-Functionen------------
    //------------Setter-Functionen------------
    public void setOGpostDocumentID(String OGpostDocumentID) {
        this.OGpostDocumentID = OGpostDocumentID;
    }
    //------------Getter-Functionen------------
    //------------Getter-Functionen------------
    public String getOGpostDocumentID() {
        return OGpostDocumentID;
    }
}
