package com.imagine.myapplication.post_classes;

import com.imagine.myapplication.user_classes.User;

public abstract class Post {

    // Attribute die jeder Post hat!---------------
    //---------------------------------------------
    public String title;
    public String documentID;
    public String description;
    public String report;
    public String createTime;
    public String originalPoster;   //Kann aber Anonym sein!
    public long thanksCount;
    public long wowCount;
    public long haCount;
    public long niceCount;
    public String type;
    public User user;               // WIrd durch Methode getUser() eingefügt
    // Attribute die NICHT immer bei allen
    // vergeben wurden!
    public String linkedFactId = "";
    public String[] tags = null;
    public Boolean isTopicPost = false;
    // Constructor
    public Post(String title, String documentID, String description, String report, String createTime,
                String originalPoster, long thanksCount, long wowCount, long haCount,
                long niceCount, String type) {
        this.title = title;
        this.documentID=documentID;
        this.description = description;
        this.report = report;
        this.createTime = createTime;
        this.originalPoster =   originalPoster;
        this.thanksCount = thanksCount;
        this.wowCount = wowCount;
        this.haCount = haCount;
        this.niceCount = niceCount;
        this.type = type;
    }

    //------------Setter-Functionen------------
    //------------Setter-Functionen------------
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public void setOriginalPoster(String originalPoster) {
        this.originalPoster = originalPoster;
    }

    public void setThanksCount(long thanksCount) {
        this.thanksCount = thanksCount;
    }

    public void setWowCount(long wowCount) {
        this.wowCount = wowCount;
    }

    public void setHaCount(long haCount) {
        this.haCount = haCount;
    }

    public void setNiceCount(long niceCount) {
        this.niceCount = niceCount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLinkedFactId(String linkedFactId) {
        this.linkedFactId = linkedFactId;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    //------------Getter-Functionen------------
    //------------Getter-Functionen------------
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getReport() {
        return report;
    }

    public String getOriginalPoster() {
        return originalPoster;
    }

    public long getThanksCount() {
        return thanksCount;
    }

    public long getWowCount() {
        return wowCount;
    }

    public long getHaCount() {
        return haCount;
    }

    public long getNiceCount() {
        return niceCount;
    }

    public String getType() {
        return type;
    }

    public String getLinkedFactId() {
        return linkedFactId;
    }

    public String[] getTags() {
        return tags;
    }
}
