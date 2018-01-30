package com.example.windows7.gichulgenerator;

import java.util.StringTokenizer;

/**
 * Created by WINDOWS7 on 2018-01-25.
 */

public class ExamInfo {

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

    public ExamInfo(String title, String period_y, String period_m, String institute, String subject, String number, String potential, String inputAnswer, String rightAnswer, String time, String memo) {
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
    }
    public ExamInfo(String title, String examFileName, String inputAnswer, String rightAnswer, String time, String memo ){
        this.title= title;

        StringTokenizer token= new StringTokenizer(examFileName, "_", false);
        String type= token.nextToken();
        period_y= token.nextToken();
        period_m= token.nextToken();
        institute= token.nextToken();
        subject= token.nextToken();
        number= token.nextToken();
        potential= token.nextToken();

        this.inputAnswer = inputAnswer;
        this.rightAnswer = rightAnswer;
        this.time = time;
        this.memo = memo;

        this.fileName= examFileName;
    }

    //fileName is period_y+ "_"+ period_m+ "_"+ institute+ "_"+ subject+ "_"+ number
    //not include potential and type( ex- a_ or q_)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title){
        this.title= title;
    }


    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPotential() {
        return potential;
    }

    public void setPotential(String potential) {
        this.potential = potential;
    }

    public String getInputAnswer() {
        return inputAnswer;
    }

    public void setInputAnswer(String inputAnswer) {
        this.inputAnswer = inputAnswer;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }


}
