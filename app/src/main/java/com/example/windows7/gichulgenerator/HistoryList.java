package com.example.windows7.gichulgenerator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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

    public int getTodayHistoryNumber(){
        int todayNumber= 0;

        Calendar today= Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());

        Calendar todayChecker;
        for(Question question: historyList){
            String key= String.valueOf(question.getTimeStamp());
            todayChecker= Calendar.getInstance();
            todayChecker.setTimeInMillis(Long.valueOf(key));
            if(today.get(Calendar.YEAR)== todayChecker.get(Calendar.YEAR)
                    && today.get(Calendar.MONTH)== todayChecker.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_MONTH)== todayChecker.get(Calendar.DAY_OF_MONTH)){
                todayNumber++;
            }
        }
        return todayNumber;
    }

    public int getTodayPotential(){
        int todayNumber= getTodayHistoryNumber();
        if(todayNumber== 0){
            return 0;
        }else{
            Calendar today= Calendar.getInstance();
            today.setTimeInMillis(System.currentTimeMillis());

            Calendar todayChecker;
            int right= 0;

            for(Question question: historyList){
                String key= String.valueOf(question.getTimeStamp());
                todayChecker= Calendar.getInstance();
                todayChecker.setTimeInMillis(Long.valueOf(key));
                if(today.get(Calendar.YEAR)== todayChecker.get(Calendar.YEAR)
                        && today.get(Calendar.MONTH)== todayChecker.get(Calendar.MONTH)
                        && today.get(Calendar.DAY_OF_MONTH)== todayChecker.get(Calendar.DAY_OF_MONTH)){
                    if(question.getInputAnswer().equals(question.getRightAnswer())){
                        right++;
                    }
                }
            }
            return (int)(((float)right/(float)todayNumber)*100);
        }
    }

    public int getTodaySubjectNumber(String subject){
        int todayNumber= 0;

        Calendar today= Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());

        Calendar todayChecker;
        for(Question question: historyList){
            String key= String.valueOf(question.getTimeStamp());
            todayChecker= Calendar.getInstance();
            todayChecker.setTimeInMillis(Long.valueOf(key));
            if(today.get(Calendar.YEAR)== todayChecker.get(Calendar.YEAR)
                    && today.get(Calendar.MONTH)== todayChecker.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_MONTH)== todayChecker.get(Calendar.DAY_OF_MONTH)){
                if(question.getSubject().equals(subject)){
                    todayNumber++;
                }
            }
        }
        return todayNumber;
    }

    public int getTodaySubjectPotential(String subject){
        int todayNumber= getTodaySubjectNumber(subject);
        if(todayNumber== 0){
            return 0;
        }else{
            int right= 0;

            Calendar today= Calendar.getInstance();
            today.setTimeInMillis(System.currentTimeMillis());

            Calendar todayChecker;
            for(Question question: historyList){
                String key= String.valueOf(question.getTimeStamp());
                todayChecker= Calendar.getInstance();
                todayChecker.setTimeInMillis(Long.valueOf(key));
                if(today.get(Calendar.YEAR)== todayChecker.get(Calendar.YEAR)
                        && today.get(Calendar.MONTH)== todayChecker.get(Calendar.MONTH)
                        && today.get(Calendar.DAY_OF_MONTH)== todayChecker.get(Calendar.DAY_OF_MONTH)) {
                    if(question.getSubject().equals(subject)){
                        if(question.getInputAnswer().equals(question.getRightAnswer())){
                            right++;
                        }
                    }
                }
            }
            return (int)(((float)right/(float)todayNumber)*100);
        }
    }

    public int getMonthHistoryNumber(){
        int monthNumber= 0;

        Calendar today= Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());

        Calendar todayChecker;
        for(Question question: historyList){
            String key= String.valueOf(question.getTimeStamp());
            todayChecker= Calendar.getInstance();
            todayChecker.setTimeInMillis(Long.valueOf(key));
            if(today.get(Calendar.YEAR)== todayChecker.get(Calendar.YEAR)
                    && today.get(Calendar.MONTH)== todayChecker.get(Calendar.MONTH)){
                monthNumber++;
            }
        }
        return monthNumber;
    }

    public int getMonthPotential(){
        int monthNumber= getMonthHistoryNumber();
        if(monthNumber== 0){
            return 0;
        }else{
            int right= 0;
            for(Question question: historyList){
                String key= String.valueOf(question.getTimeStamp());
                if(question.getInputAnswer().equals(question.getRightAnswer())){
                    right++;
                }
            }
            return (int)(((float)right/(float)monthNumber)*100);
        }
    }

    public int getMonthSubjectNumber(String subject){
        int monthNumber= 0;

        Calendar today= Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());

        Calendar todayChecker;
        for(Question question: historyList){
            String key= String.valueOf(question.getTimeStamp());
            todayChecker= Calendar.getInstance();
            todayChecker.setTimeInMillis(Long.valueOf(key));
            if(today.get(Calendar.YEAR)== todayChecker.get(Calendar.YEAR)
                    && today.get(Calendar.MONTH)== todayChecker.get(Calendar.MONTH)){
                if(question.getSubject().equals(subject)){
                    monthNumber++;
                }
            }
        }
        return monthNumber;
    }

    public int getMonthSubjectPotential(String subject){
        int monthNumber= getMonthSubjectNumber(subject);
        if(monthNumber== 0){
            return 0;
        }else{
            int right= 0;

            Calendar today= Calendar.getInstance();
            today.setTimeInMillis(System.currentTimeMillis());

            Calendar todayChecker;
            for(Question question: historyList){
                String key= String.valueOf(question.getTimeStamp());
                todayChecker= Calendar.getInstance();
                todayChecker.setTimeInMillis(Long.valueOf(key));
                if(today.get(Calendar.YEAR)== todayChecker.get(Calendar.YEAR)
                        && today.get(Calendar.MONTH)== todayChecker.get(Calendar.MONTH)) {
                    if(question.getSubject().equals(subject)){
                        if(question.getInputAnswer().equals(question.getRightAnswer())){
                            right++;
                        }
                    }
                }
            }
            return (int)(((float)right/(float)monthNumber)*100);
        }
    }



    public int getTotalNumber(){
        return historyList.size();
    }
    public int getTotalPotential(){
        int total= getTotalNumber();
        if(total== 0){
            return 0;
        }else{
            int right= 0;
            for(Question question: historyList){
                if(question.getInputAnswer().equals(question.getRightAnswer())){
                    right++;
                }
            }
            return (int)(((float)right/(float)total)*100);
        }
    }
    public int getTotalSubjectNumber(String subject){
        int number= 0;
        for(Question question: historyList){
            if(question.getSubject().equals(subject)){
                number++;
            }
        }
        return number;
    }
    public int getTotalSubjectPotential(String subject){
        int totalNumber= getTotalSubjectNumber(subject);
        int right= 0;
        for(Question question: historyList){
            if(question.getSubject().equals(subject)){
                if(question.getInputAnswer().equals(question.getRightAnswer())){
                    right++;
                }
            }
        }
        if(totalNumber== 0){
            return 0;
        }else{
            return (int)(((float)right/(float)totalNumber)*100);
        }
    }




}
