package com.example.windows7.gichulgenerator;

/**
 * Created by WINDOWS7 on 2018-01-28.
 */

public class ListViewItem_CheckList {
    private String examTitle;
    private String examInfo;
    private String examMemo;

    public ListViewItem_CheckList(String examTitle, String examInfo, String examMemo) {
        this.examTitle = examTitle;
        this.examInfo = examInfo;
        this.examMemo = examMemo;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public String getExamInfo() {
        return examInfo;
    }

    public String getExamMemo() {
        return examMemo;
    }
}
