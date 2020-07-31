package com.example.zutr.models;

public class Resource {


    public static final String KEY_TITLE = "title";
    public static final String KEY_CREATED = "year";
    public static final String KEY_URL = "link";
    public static final String KEY_SUBJECT = "subject";
    public static final String KEY_IMAGE = "image_url";
    public static final String KEY_DESCRIPTION = "abstract";

    private String title;
    private String subject;
    private String created;
    private String imageURL;
    private String resrcLink;
    private String description;


    public Resource(String title, String subject, String created, String imageURL, String rsrcLink, String description) {
        this.title = title;
        this.subject = subject;
        this.created = created;
        this.imageURL = imageURL;
        this.resrcLink = rsrcLink;
        this.description = description;
    }

    public Resource() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getResrcLink() {
        return resrcLink;
    }

    public void setResrcLink(String resrcLink) {
        this.resrcLink = resrcLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public boolean isEmpty() {
        return title == null || subject == null || description == null;
    }
}
