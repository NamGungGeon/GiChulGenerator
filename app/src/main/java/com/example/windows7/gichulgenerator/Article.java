package com.example.windows7.gichulgenerator;

import java.util.HashMap;

/**
 * Created by WINDOWS7 on 2018-02-11.
 */

public class Article{
    private String title;
    private String text;
    private String userName;

    private HashMap<String, Comment> comments= new HashMap<>();
    private long timeStamp;
    private String uid;
    private String key;

    public Article(String title, String text, String userName, String uid, String key, HashMap<String, Comment> comments) {
        this.title = title;
        this.text = text;
        this.userName = userName;
        this.comments= comments;
        timeStamp= System.currentTimeMillis();
        this.uid= uid;
        this.key= key;
    }

    public Article(){}

    public HashMap<String, Comment> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, Comment> comments) {
        this.comments = comments;
    }

    public String getKey() {
        return key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getUserName() {
        return userName;
    }
    public long getTimeStamp() {
        return timeStamp;
    }


}
