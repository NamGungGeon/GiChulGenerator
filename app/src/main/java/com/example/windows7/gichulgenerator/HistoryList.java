package com.example.windows7.gichulgenerator;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

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

    public void deleteFromList(ExamInfo exam){
        historyList.remove(exam);
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

                //Case: There is no data in database
                if(historyList== null || data== null){
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
}
