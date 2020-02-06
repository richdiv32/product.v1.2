package com.ng.campusbuddy.model;

public class AD {

    String id;
    String title;
    String image;
    String full_image;
    String description;

    public AD(String id, String title, String image, String full_image, String description) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.full_image = full_image;
        this.description = description;
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
}
