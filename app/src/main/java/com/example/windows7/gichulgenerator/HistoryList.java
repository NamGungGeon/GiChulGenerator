package com.example.windows7.gichulgenerator;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by WINDOWS7 on 2018-01-25.
 */

//Before using data, Should call loadHistoryListFromFirebase() to sync firebase database.
public class HistoryList {
    private HashMap<String, ExamInfo> historyList;

    private static HistoryList inst= null;
    private HistoryList() {}

    public static HistoryList getInstance(){
        if(inst== null){
            inst= new HistoryList();
        }
        return inst;
    }

    public HashMap<String, ExamInfo> getHistoryList(){
        return historyList;
    }

    public void addToList(ExamInfo exam){
        historyList.put(String.valueOf(System.currentTimeMillis()), exam);
        saveHistoryListToServer();
    }

    public void deleteAllData(){
        historyList= new HashMap<>();
        saveHistoryListToServer();
    }


    public interface Callback{
        void success();
        void fail();
    }

    // Load CheckList from firebase
    public void loadHistoryListFromServer(final Callback _callback){

        final FirebaseConnection.Callback callback= new FirebaseConnection.Callback() {
            @Override
            public void success(Object data) {
                HashMap<String, HashMap<String, String>> temp= (HashMap<String, HashMap<String, String>>)data;
                historyList= new HashMap<>();

                //Case: There is no data in database
                if(temp== null || temp.size()==0){
                    historyList= new HashMap<>();
                }else{
                    //Case: Success to read
                    for(String key: temp.keySet()){
                        HashMap<String, String> value= temp.get(key);
                        ExamInfo info= new ExamInfo(value.get("title"), value.get("period_y"), value.get("period_m"), value.get("institute"), value.get("subject"), value.get("number"),
                                value.get("potential"), value.get("inputAnswer"), value.get("rightAnswer"), value.get("time"), value.get("memo"));
                        historyList.put(key, info);
                    }
                }

                Log.i("Firebase  Check", String.valueOf(historyList.size()));
                _callback.success();
            }

            @Override
            public void fail(String errorMessage) {
                //Case: Connection Fail
                historyList= new HashMap<>();
                _callback.fail();
            }
        };

        FirebaseConnection.getInstance().loadExamInfoList("userdata/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/historyList", callback);
    }

    // Save CheckList to firebase
    private void saveHistoryListToServer(){
        FirebaseConnection.getInstance().saveExamInfoList("userdata/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/historyList", historyList);
    }

    public int getTodayHistoryNumber(){
        int todayNumber= 0;

        Calendar today= Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());

        Calendar todayChecker;
        for(String key: historyList.keySet()){
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
            int right= 0;
            for(String key: historyList.keySet()){
                if(historyList.get(key).getInputAnswer().equals(historyList.get(key).getRightAnswer())){
                    right++;
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
        for(String key: historyList.keySet()){
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
            for(String key: historyList.keySet()){
                if(historyList.get(key).getInputAnswer().equals(historyList.get(key).getRightAnswer())){
                    right++;
                }
            }
            return (int)(((float)right/(float)monthNumber)*100);
        }
    }

    public int getSubjectPotential(String subject){
        int totalNumber= 0;
        int right= 0;
        for(String key: historyList.keySet()){
            if(historyList.get(key).getSubject().equals(subject)){
                totalNumber++;
                if(historyList.get(key).getInputAnswer().equals(historyList.get(key).getRightAnswer())){
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
