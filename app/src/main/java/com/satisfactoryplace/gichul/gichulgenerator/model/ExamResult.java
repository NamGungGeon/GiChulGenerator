package com.satisfactoryplace.gichul.gichulgenerator.model;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Windows10 on 2018-03-02.
 */

public class ExamResult {
    private String title;
    private String basicFileName;
    private String period_y= "";
    private String period_m= "";
    private String institute= "";
    private String subject= "";
    private ArrayList<Long> inputAnswers;
    private ArrayList<Long> rightAnswers;
    private int runningTime;
    private long timeStamp;

    public ExamResult(String title, String basicFileName, ArrayList<Long> inputAnswers, ArrayList<Long> rightAnswers, int runningTime) {
        this.title = title;

        StringTokenizer token= new StringTokenizer(basicFileName, "_", false);

        period_y= token.nextToken();
        period_m= token.nextToken();
        institute= token.nextToken();
        subject= token.nextToken();

        this.inputAnswers = inputAnswers;
        this.rightAnswers = rightAnswers;
        this.runningTime = runningTime;
        timeStamp= System.currentTimeMillis();
        this.basicFileName= basicFileName;
    }

    // For loading from firebase
    public ExamResult(){}

    public String getBasicFileName() {
        return basicFileName;
    }

    public void setBasicFileName(String basicFileName) {
        this.basicFileName = basicFileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPeriod_y() {
        return period_y;
    }

    public void setPeriod_y(String period_y) {
        this.period_y = period_y;
    }

    public String getPeriod_m() {
        return period_m;
    }

    public void setPeriod_m(String period_m) {
        this.period_m = period_m;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public ArrayList<Long> getInputAnswers() {
        return inputAnswers;
    }

    public void setInputAnswers(ArrayList<Long> inputAnswers) {
        this.inputAnswers = inputAnswers;
    }

    public ArrayList<Long> getRightAnswers() {
        return rightAnswers;
    }

    public void setRightAnswers(ArrayList<Long> rightAnswers) {
        this.rightAnswers = rightAnswers;
    }

    public int getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(int runningTime) {
        this.runningTime = runningTime;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
