package com.example.zutr.models;

public class Resource {


    public static final String KEY_TITLE = "title";
    public static final String KEY_CREATED = "timestamp";
    public static final String KEY_IMAGE = "image_url";
    public static final String KEY_DESCRIPTION = "description";

    private String title;
    private String author;
    private String created;
    private String imageURL;


    public Resource(String title, String author, String created, String imageURL) {
        this.title = title;
        this.author = author;
        this.created = created;
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
