package com.ng.campusbuddy.model;

public class Match {

    private String id;
    private String imageurl;
    private String gender;

    public Match(String id, String imageurl, String gender) {
        this.id = id;
        this.imageurl = imageurl;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
