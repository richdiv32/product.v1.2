package com.ng.campusbuddy.social.message;

public class Grouplist {

    public String groupimage;
    public String title;
    public String groupid;
    public String creator;


    public Grouplist(String groupimage, String title, String groupid, String creator) {
        this.groupimage = groupimage;
        this.title = title;
        this.groupid = groupid;
        this.creator = creator;
    }

    public Grouplist() {
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }


    public String getGroupimage() {
        return groupimage;
    }

    public void setGroupimage(String groupimage) {
        this.groupimage = groupimage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
