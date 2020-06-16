package com.imagine.myapplication;

import java.util.List;

public class User {
    public String name;
    public String surName;
    public String imageURL ="";
    public String userID;
    public List<String> blocked = null;
    public String statusQuote= "";

    public User(String name, String surName, String userID) {
        this.name = name;
        this.surName = surName;
        this.userID = userID;
    }
    //------------Setter-Functionen------------
    //------------Setter-Functionen------------
    public void setName(String name) {
        this.name = name;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setBlocked(List<String> blocked) {
        this.blocked = blocked;
    }

    public void setStatusQuote(String statusQuote) {
        this.statusQuote = statusQuote;
    }
    //------------Getter-Functionen------------
    //------------Getter-Functionen------------
    public String getName() {
        return name;
    }

    public String getSurName() {
        return surName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getUserID() {
        return userID;
    }

    public List<String> getBlocked() {
        return blocked;
    }

    public String getStatusQuote() {
        return statusQuote;
    }
}
