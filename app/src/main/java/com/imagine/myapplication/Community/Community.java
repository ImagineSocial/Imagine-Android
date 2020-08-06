package com.imagine.myapplication.Community;

public class Community {
    public String name;
    public String imageURL;
    public String topicID;
    public String description;
    public String displayOption;
    public String type;
    public long popularity;

    public Community(String name, String imageURL, String topicID, String description){
        this.name = name;
        this.imageURL = imageURL;
        this.topicID = topicID;
        this.description = description;
    }
}
