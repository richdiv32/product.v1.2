package com.ng.campusbuddy.social;

public class User {
    private String profile_status;
    private String id;
    private String username;
    private String fullname;
    private String imageurl;
    private String bio;
    private String gender;
    private String birthday;
    private String telephone;
    private String faculty;
    private String department;
    private String institution;
    private String email;
    private String online_status;
    private String relationship_status;
    private String typingTo;
    private boolean isSelected = false;

    public User(String profile_status, String id, String username, String fullname,
                String imageurl, String bio, String gender, String birthday, String telephone,
                String faculty, String department, String institution, String email,
                String online_status, String relationship_status, String typingTo) {
        this.profile_status = profile_status;
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.imageurl = imageurl;
        this.bio = bio;
        this.gender = gender;
        this.birthday = birthday;
        this.telephone = telephone;
        this.faculty = faculty;
        this.department = department;
        this.institution = institution;
        this.email = email;
        this.online_status = online_status;
        this.relationship_status = relationship_status;
        this.typingTo = typingTo;
    }

    public User() {
    }

    public String getProfile_status() {
        return profile_status;
    }
    public void setProfile_status(String profile_status) {
        this.profile_status = profile_status;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImageurl() {
        return imageurl;
    }
    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFaculty() {
        return faculty;
    }
    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getInstitution() {
        return institution;
    }
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getOnline_status() {
        return online_status;
    }

    public void setOnline_status(String online_status) {
        this.online_status = online_status;
    }

    public String getRelationship_status() {
        return relationship_status;
    }
    public void setRelationship_status(String relationship_status) {
        this.relationship_status = relationship_status;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected(){
        return  isSelected;
    }
}
