package com.example.windows7.gichulgenerator;

import java.util.ArrayList;

/**
 * Created by WINDOWS7 on 2018-01-25.
 */

public class HistoryList {
    private ArrayList<ExamInfo> historyList;
    // true= conection success
    // false= connection fail
    private boolean connectionStatus;

    private static HistoryList inst= null;
    private HistoryList() {
        if(inst== null){

        }

        boolean isSuccessLoad= loadHistoryListFromServer();
        if(isSuccessLoad!= true){
            historyList= new ArrayList();
        }
    }

    public static HistoryList getInstance(){
        if(inst== null){
            inst= new HistoryList();
        }

        return inst;
    }

    public ArrayList<ExamInfo> getHistoryList(){
        return historyList;
    }

    public void addToList(ExamInfo exam){
        historyList.add(exam);
    }

    public void deleteFromList(ExamInfo exam){
        historyList.remove(exam);
    }

    // Load CheckList from firebase
    private boolean loadHistoryListFromServer(){
        return false;
    }

    // Save CheckList to firebase
    private boolean savehistoryListToServer(){
        return false;
    }
}
