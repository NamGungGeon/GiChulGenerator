package com.example.windows7.gichulgenerator;

import java.util.ArrayList;

/**
 * Created by WINDOWS7 on 2018-02-11.
 */

public class Article {
    private String title;
    private String text;
    private String userName;
    private ArrayList<Comment> comments;
    private long timeStamp;

    public Article(String title, String text, String userName) {
        this.title = title;
        this.text = text;
        this.userName = userName;
        comments= new ArrayList<>();
        timeStamp= System.currentTimeMillis();
    }

    public Article(){}

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getUserName() {
        return userName;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void addComment(Comment comment){
        comments.add(comment);
    }



}
