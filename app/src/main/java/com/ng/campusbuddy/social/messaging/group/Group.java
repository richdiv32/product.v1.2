package com.ng.campusbuddy.social.messaging.group;

public class Group {

    private String group_image;
    private String groupid;
    private String group_title;
    private String group_creator;
    private String group_userid;

    public Group(String group_image, String groupid, String group_title, String group_creator, String group_userid) {
        this.group_image = group_image;
        this.groupid = groupid;
        this.group_title = group_title;
        this.group_creator = group_creator;
        this.group_userid = group_userid;
    }

    public Group() {
    }

    public String getGroup_image() {
        return group_image;
    }

    public void setGroup_image(String group_image) {
        this.group_image = group_image;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroup_title() {
        return group_title;
    }

    public void setGroup_title(String group_title) {
        this.group_title = group_title;
    }

    public String getGroup_creator() {
        return group_creator;
    }

    public void setGroup_creator(String group_creator) {
        this.group_creator = group_creator;
    }

    public String getGroup_userid() {
        return group_userid;
    }

    public void setGroup_userid(String group_userid) {
        this.group_userid = group_userid;
    }
}
