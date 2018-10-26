package com.satisfactoryplace.gichul.gichulgenerator.model;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Windows10 on 2018-03-02.
 */

public class ExamResult {
    public String title;
    public String basicFileName;
    public String period_y= "";
    public String period_m= "";
    public String institute= "";
    public String subject= "";
    public ArrayList<Long> inputAnswers;
    public ArrayList<Long> rightAnswers;
    public int runningTime;
    public long timeStamp;

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


}
