package com.satisfactoryplace.gichul.gichulgenerator.data;

import java.util.ArrayList;
import java.util.List;

public class ExamResultSaver {
    public static ExamResultSaver inst;

    public ArrayList<Integer> inputAnswers;
    public int t_sec;

    public ExamResultSaver(ArrayList<Integer> inputAnswers, int t_sec) {
        this.inputAnswers = inputAnswers;
        this.t_sec = t_sec;
    }

}
