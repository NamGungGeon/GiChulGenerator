package com.example.windows7.gichulgenerator;

import java.io.Serializable;

/**
 * Created by WINDOWS7 on 2018-02-11.
 */

public class Comment implements Serializable{
    private String userName;
    private String text;
    private long timeStamp;
    private String uid;
    private String key;

    public Comment(){}
    public Comment(String userName, String text, String uid, String key) {
        this.userName = userName;
        this.text = text;
        timeStamp= System.currentTimeMillis();
        this.uid= uid;
        this.key= key;
    }
    public String getUid() {
        return uid;
    }
    public String getUserName() {
        return userName;
    }
    public String getText() {
        return text;
    }
    public long getTimeStamp() {
        return timeStamp;
    }
    public String getKey() {
        return key;
    }

}
