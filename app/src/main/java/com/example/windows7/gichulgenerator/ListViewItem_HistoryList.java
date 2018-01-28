package com.example.windows7.gichulgenerator;

/**
 * Created by WINDOWS7 on 2018-01-28.
 */

public class ListViewItem_HistoryList {
    private String examTitle;
    private String examInfo;

    public ListViewItem_HistoryList(String examTitle, String examInfo) {
        this.examTitle = examTitle;
        this.examInfo = examInfo;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public String getExamInfo() {
        return examInfo;
    }

}
