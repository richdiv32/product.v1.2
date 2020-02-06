package com.ng.campusbuddy.education;

public class Books {

    String id;
    String coverImage;
    String publisher;
    String pages;
    String title;
    String bookURL;

    public Books(String id, String coverImage, String publisher, String pages, String title, String bookURL) {
        this.id = id;
        this.coverImage = coverImage;
        this.publisher = publisher;
        this.pages = pages;
        this.title = title;
        this.bookURL = bookURL;
    }

    public Books() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBookURL() {
        return bookURL;
    }

    public void setBookURL(String bookURL) {
        this.bookURL = bookURL;
    }
}
