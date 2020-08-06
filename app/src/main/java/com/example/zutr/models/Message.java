package com.example.zutr.models;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {


    public static final String KEY_MSG_BODY = "body";
    public static final String KEY_AUTHOR_ID = "authorID";
    public static final String KEY_CREATEDAT = "createdAt";
    public static final String KEY_HIDDENBY = "hiddenBy";

    private String body;
    private String hiddenBy;
    private String authorID;
    private Date createdAt;


    public Message(String body, String hiddenBy, String authorID, Date createdAt) {
        this.body = body;
        this.hiddenBy = hiddenBy;
        this.authorID = authorID;
        this.createdAt = createdAt;
    }

    public Message() {

    }

//    public Message(String body, String authorID, Date createdAt) {
//        this.body = body;
//        this.authorID = authorID;
//        this.createdAt = createdAt;
//    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo() {
        String rawDate = getCreatedAt().toString();
        String postFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(postFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public String getHiddenBy() {
        return hiddenBy;
    }

    public void setHiddenBy(String hiddenBy) {
        this.hiddenBy = hiddenBy;
    }
}
