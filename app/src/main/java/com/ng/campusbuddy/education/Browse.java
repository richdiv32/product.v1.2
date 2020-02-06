package com.ng.campusbuddy.education;

public class Browse {

    private String id;
    private String Title;

    public Browse(String id, String title) {
        this.id = id;
        Title = title;
    }

    public Browse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
