package com.ng.campusbuddy.model;

public class Contest {
    String imageURL;
    String username;
    String telephone;
    String gender;

    public Contest(String imageURL, String username, String telephone, String gender) {
        this.imageURL = imageURL;
        this.username = username;
        this.telephone = telephone;
        this.gender = gender;
    }

    public Contest() {
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
