package com.example.windows7.gichulgenerator;

/**
 * Created by WINDOWS7 on 2018-02-11.
 */

public class Comment {
    private String userName;
    private String text;
    private long timeStamp;

    public Comment(String userName, String text) {
        this.userName = userName;
        this.text = text;
        timeStamp= System.currentTimeMillis();
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
