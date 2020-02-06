package com.ng.campusbuddy.social.match;

public class Match {
    private String userId;
    private String username;
    private String profileImageUrl;

    public Match(String userId, String username, String profileImageUrl){
        this.userId = userId;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
    }


    public Match() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }
}
