package com.ng.campusbuddy.model;

public class Notification {
    private String userid;
    private String text;
    private String postid;
    private String publisher;
    private String type;


    public Notification(String userid, String text, String postid, String publisher, String type) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.publisher = publisher;
        this.type = type;
    }

    public Notification() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
