package com.satisfactoryplace.gichul.gichulgenerator.model;

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

    private boolean isExistImage;

    public Article(String title, String text, String userName, String uid, String key, HashMap<String, Comment> comments, boolean isExistImage) {
        this.title = title;
        this.text = text;
        this.userName = userName;
        this.comments= comments;
        timeStamp= System.currentTimeMillis();
        this.uid= uid;
        this.key= key;
        this.isExistImage= isExistImage;
    }

    // only used for loading from firebase
    // never use directly
    public Article(){}

    public HashMap<String, Comment> getComments() {
        return comments;
    }
    public void setComments(HashMap<String, Comment> comments) {
        this.comments = comments;
    }

    // not need setter about key, title, text
    public String getKey() {
        return key;
    }
    public String getTitle() {
        return title;
    }
    public String getText() {
        return text;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }
    public long getTimeStamp() {
        return timeStamp;
    }

    public boolean getIsExistImage() {
        return isExistImage;
    }
    public void setExistImage(boolean isExistImage) {
        isExistImage = isExistImage;
    }
}
