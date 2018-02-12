package com.example.windows7.gichulgenerator;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by WINDOWS7 on 2018-02-11.
 */

public class Article implements Parcelable{
    private String title;
    private String text;
    private String userName;
    private ArrayList<Comment> comments= new ArrayList<>();
    private long timeStamp;
    private String uid;
    private String key;

    public Article(String title, String text, String userName, String uid, String key, ArrayList<Comment> comments) {
        this.title = title;
        this.text = text;
        this.userName = userName;
        this.comments= comments;
        timeStamp= System.currentTimeMillis();
        this.uid= uid;
        this.key= key;
    }

    public Article(){}

    protected Article(Parcel in) {
        title = in.readString();
        text = in.readString();
        userName = in.readString();
        timeStamp = in.readLong();
        in.readList(comments, null);
        uid= in.readString();
        key= in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };


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

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void addComment(Comment comment){
        comments.add(comment);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(text);
        parcel.writeString(userName);
        parcel.writeLong(timeStamp);
        parcel.writeList(comments);
        parcel.writeString(uid);
        parcel.writeString(key);
    }
}
