package com.ng.campusbuddy.model;

public class AD {

    String id;
    String title;
    String image;
    String full_image;
    String description;
    String tel;
    String site;

    public AD(String id, String title, String image, String full_image, String description, String tel, String site) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.full_image = full_image;
        this.description = description;
        this.tel = tel;
        this.site = site;
    }

    public AD() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFull_image() {
        return full_image;
    }

    public void setFull_image(String full_image) {
        this.full_image = full_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
