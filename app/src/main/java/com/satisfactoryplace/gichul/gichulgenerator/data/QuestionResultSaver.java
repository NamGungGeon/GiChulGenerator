package com.satisfactoryplace.gichul.gichulgenerator.data;

public class QuestionResultSaver {

    public static QuestionResultSaver inst;

    public String input;
    public int t_sec;

    public QuestionResultSaver(String input, int t_sec) {
        this.input= input;
        this.t_sec = t_sec;
    }

}
