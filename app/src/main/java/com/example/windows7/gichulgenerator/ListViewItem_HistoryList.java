package com.example.windows7.gichulgenerator;

/**
 * Created by WINDOWS7 on 2018-01-28.
 */

public class ListViewItem_HistoryList {
    private String examTitle;
    private String examInfo;
    private String examFileName;
    private String examPotential;

    public ListViewItem_HistoryList(String examTitle, String examInfo, String examFileName, String examPotential) {
        this.examTitle = examTitle;
        this.examInfo = examInfo;
        this.examFileName= examFileName;
        this.examPotential= examPotential;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public String getExamInfo() {
        return examInfo;
    }

    public String getExamFileName() {
        return examFileName;
    }

    public String getExamPotential() {
        return examPotential;
    }
}
