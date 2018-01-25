package com.example.windows7.gichulgenerator;

import java.util.ArrayList;

/**
 * Created by WINDOWS7 on 2018-01-25.
 */

public class CheckList {
    private ArrayList<ExamInfo> checkList;
    // true= conection success
    // false= connection fail
    private boolean connectionStatus;

    private static CheckList inst= null;
    private CheckList() {
        if(inst== null){

        }

        boolean isSuccessLoad= loadCheckListFromServer();
        if(isSuccessLoad!= true){
            checkList= new ArrayList();
        }
    }

    public static CheckList getInstance(){
        if(inst== null){
            inst= new CheckList();
        }

        return inst;
    }

    public ArrayList<ExamInfo> getCheckList(){
        return checkList;
    }

    public void addToList(ExamInfo exam){
        checkList.add(exam);
    }

    public void deleteFromList(ExamInfo exam){
        checkList.remove(exam);
    }

    // Load CheckList from firebase
    private boolean loadCheckListFromServer(){
        return false;
    }

    // Save CheckList to firebase
    private boolean saveCheckListToServer(){
        return false;
    }
}
