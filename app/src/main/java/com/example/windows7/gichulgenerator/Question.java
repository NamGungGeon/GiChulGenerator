package com.example.windows7.gichulgenerator;

import android.support.annotation.NonNull;

import java.util.StringTokenizer;

/**
 * Created by WINDOWS7 on 2018-01-25.
 */

// No exist setter
// Setter is never used, and also must not used.
public class Question {

    private String title= "";
    private String period_y= "";
    private String period_m= "";
    private String institute= "";
    private String subject= "";
    private String number= "";
    private String potential= "";
    private String inputAnswer= "";
    private String rightAnswer= "";
    private String time= "";
    private String memo= "";

    //fileName is period_y+ "_"+ period_m+ "_"+ institute+ "_"+ subject+ "_"+ number
    //not include potential and type( ex- a_ or q_)
    private String fileName= "";

    private long timeStamp;

    public Question(String title, String period_y, String period_m, String institute, String subject, String number, String potential, String inputAnswer, String rightAnswer, String time, String memo) {
        this.title= title;
        this.period_y = period_y;
        this.period_m = period_m;
        this.institute = institute;
        this.subject = subject;
        this.number = number;
        this.potential = potential;
        this.inputAnswer = inputAnswer;
        this.rightAnswer = rightAnswer;
        this.time = time;
        this.memo = memo;

        fileName= period_y+ "_"+ period_m+ "_"+ institute+ "_"+ subject+ "_"+ number;

        timeStamp= System.currentTimeMillis();
    }
    public Question(String title, String basicFileName, String potential, String inputAnswer, String rightAnswer, String time, String memo){
        this.title= title;

        StringTokenizer token= new StringTokenizer(basicFileName, "_", false);

        period_y= token.nextToken();
        period_m= token.nextToken();
        institute= token.nextToken();
        subject= token.nextToken();
        number= token.nextToken();

        this.potential= potential;
        this.inputAnswer = inputAnswer;
        this.rightAnswer = rightAnswer;
        this.time = time;
        this.memo = memo;
        this.fileName= basicFileName;

        timeStamp= System.currentTimeMillis();
    }

    //fileName is period_y+ "_"+ period_m+ "_"+ institute+ "_"+ subject+ "_"+ number
    //not include potential and type( ex- a_ or q_)
    public String getFileName() {
        return fileName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getTitle() {
        return title;
    }

    public String getPeriod_y() {
        return period_y;
    }

    public String getPeriod_m() {
        return period_m;
    }

    public String getInstitute() {
        return institute;
    }

    public String getSubject() {
        return subject;
    }

    public String getNumber() {
        return number;
    }

    public String getPotential() {
        return potential;
    }

    public String getInputAnswer() {
        return inputAnswer;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public String getTime() {
        return time;
    }

    public String getMemo() {
        return memo;
    }

}
