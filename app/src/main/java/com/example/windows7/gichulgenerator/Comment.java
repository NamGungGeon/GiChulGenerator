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

    public Comment(){}
    public Comment(String userName, String text, String uid) {
        this.userName = userName;
        this.text = text;
        timeStamp= System.currentTimeMillis();
        this.uid= uid;
    }

    //This Constructor is only using to load Comment Object from Firebase
    public Comment(String userName, String text, String uid, long timeStamp) {
        this.userName = userName;
        this.text = text;
        timeStamp= System.currentTimeMillis();
        this.uid= uid;
        this.timeStamp= timeStamp;
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

}
