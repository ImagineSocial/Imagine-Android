package com.imagine.myapplication;

public class Votes {
    public String thanks;
    public String wow;
    public String ha;
    public String nice;

    public Votes(String thanks, String wow, String ha, String nice) {
        this.thanks = thanks;
        this.wow = wow;
        this.ha = ha;
        this.nice = nice;
    }
    //------------Setter-Functionen------------
    //------------Setter-Functionen------------
    public void setThanks(String thanks) {
        this.thanks = thanks;
    }

    public void setWow(String wow) {
        this.wow = wow;
    }

    public void setHa(String ha) {
        this.ha = ha;
    }

    public void setNice(String nice) {
        this.nice = nice;
    }
    //------------Getter-Functionen------------
    //------------Getter-Functionen------------
    public String getThanks() {
        return thanks;
    }

    public String getWow() {
        return wow;
    }

    public String getHa() {
        return ha;
    }

    public String getNice() {
        return nice;
    }
}
