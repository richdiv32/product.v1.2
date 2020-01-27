package com.ng.campusbuddy.utils;

public class Notification {
    private String userid;
    private String text;
    private String postid;
    private String publisher;
    private boolean ispost;

    public Notification(String userid, String text, String postid, boolean ispost, String publisher) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.ispost = ispost;
        this.publisher = publisher;
    }

    public Notification() {
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
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

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }
}
