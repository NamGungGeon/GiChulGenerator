package com.satisfactoryplace.gichul.gichulgenerator.model;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class ErrorInfo {
    public ErrorInfo(String errorMsg, String comment, String activityName) {
        this.errorMsg = errorMsg;
        this.comment = comment;
        this.activityName = activityName;
        this.date= new Date().toString();
        this.uid= FirebaseAuth.getInstance().getUid();
    }

    public String errorMsg;
    public String date;
    public String comment;
    public String activityName;
    public String uid;
}
