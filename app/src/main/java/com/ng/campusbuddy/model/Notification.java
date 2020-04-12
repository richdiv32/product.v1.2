package com.ng.campusbuddy.model;

public class Notification {
    private String id;
    private String userid;
    private String text;
    private String postid;
    private String publisher;
    private String type;
    private boolean isseen;

    public Notification(String id, String userid, String text, String postid, String publisher, String type, boolean isseen) {
        this.id = id;
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.publisher = publisher;
        this.type = type;
        this.isseen = isseen;
    }

    public Notification() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
