package com.satisfactoryplace.gichul.gichulgenerator.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.satisfactoryplace.gichul.gichulgenerator.server.FirebaseConnection;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by WINDOWS7 on 2018-01-25.
 */

//Before using data, Should call loadHistoryListFromFirebase() to sync firebase database.
public class HistoryList {
    private ArrayList<Question> historyList;

    private static HistoryList inst= null;
    private HistoryList() {}

    public static HistoryList getInstance(){
        if(inst== null){
            inst= new HistoryList();
        }
        return inst;
    }

    public ArrayList<Question> getHistoryList(){
        return historyList;
    }

    public void addToList(Question question){
        historyList.add(question);
        saveHistoryListToServer();
    }

    public void deleteAllData(){
        historyList= new ArrayList<>();
        saveHistoryListToServer();
    }


    public interface Callback{
        void success();
        void fail();
    }

    // Load CheckList from firebase
    public void loadHistoryListFromServer(final Callback callback){
        FirebaseConnection.getInstance().loadDataWithQuery(FirebaseConnection.getInstance().getReference("userdata/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/historyList").orderByKey(),
                new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                historyList= new ArrayList<>();

                //Case: There is no data in database
                if(snapshot.getValue()== null){

                }else{
                    //Case: Success to read
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        // Action...
                        historyList.add(postSnapshot.getValue(Question.class));
                    }
                }
                callback.success();
            }

            @Override
            public void fail(String errorMessage) {
                //Case: Connection Fail
                historyList= new ArrayList<>();
                callback.fail();
            }
        });
    }

    // Save CheckList to firebase
    private void saveHistoryListToServer(){
        FirebaseConnection.getInstance().saveData("userdata/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/historyList", historyList);
    }

    public int getHistoryNumber_day(Calendar calendar){
        int number= 0;
        for(Question question: historyList){
            Calendar checker= Calendar.getInstance();
            checker.setTimeInMillis(question.getTimeStamp());
            if(calendar.get(Calendar.YEAR)== checker.get(Calendar.YEAR)
                    && calendar.get(Calendar.MONTH)== checker.get(Calendar.MONTH)
                    && calendar.get(Calendar.DAY_OF_MONTH)== checker.get(Calendar.DAY_OF_MONTH)){
                number++;
            }
        }
        return number;
    }
    public int getHistoryRightNumber_day(Calendar calendar){
        int rightAnswer= 0;

        for(Question question: historyList){
            Calendar checker= Calendar.getInstance();
            checker.setTimeInMillis(question.getTimeStamp());
            if(calendar.get(Calendar.YEAR)== checker.get(Calendar.YEAR)
                    && calendar.get(Calendar.MONTH)== checker.get(Calendar.MONTH)
                    && calendar.get(Calendar.DAY_OF_MONTH)== checker.get(Calendar.DAY_OF_MONTH)){
                if(question.getInputAnswer().equals(question.getRightAnswer())){
                    rightAnswer++;
                }
            }
        }
        return rightAnswer;
    }


    public int getHistoryNumber_month(Calendar calendar){
        int number= 0;
        for(Question question: historyList){
            Calendar checker= Calendar.getInstance();
            checker.setTimeInMillis(question.getTimeStamp());
            if(calendar.get(Calendar.YEAR)== checker.get(Calendar.YEAR)
                    && calendar.get(Calendar.MONTH)== checker.get(Calendar.MONTH)){
                number++;
            }
        }
        return number;
    }
    public int getHistoryRightNumber_month(Calendar calendar){
        int rightAnswer= 0;

        for(Question question: historyList){
            Calendar checker= Calendar.getInstance();
            checker.setTimeInMillis(question.getTimeStamp());
            if(calendar.get(Calendar.YEAR)== checker.get(Calendar.YEAR)
                    && calendar.get(Calendar.MONTH)== checker.get(Calendar.MONTH)){
                if(question.getInputAnswer().equals(question.getRightAnswer())){
                    rightAnswer++;
                }
            }
        }
        return rightAnswer;
    }

    public int getHistoryNumber_total(){
        return historyList.size();
    }
    public int getHistoryRightNumber_total(){
        int right= 0;
        for(Question question: historyList){
            if(question.getInputAnswer().equals(question.getRightAnswer())){
                right++;
            }
        }
        return right;
    }
}
