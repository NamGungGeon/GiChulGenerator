package com.example.windows7.gichulgenerator;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

/**
 * Created by WINDOWS7 on 2018-01-25.
 */

public class CheckList {
    private HashMap<String, Exam> checkList= new HashMap<>();

    private static CheckList inst= null;
    private CheckList() {
    }

    public static CheckList getInstance(){
        if(inst== null){
            inst= new CheckList();
        }
        return inst;
    }

    public HashMap<String, Exam> getCheckList(){
        return checkList;
    }

    public void addToList(Exam exam){
        checkList.put(String.valueOf(System.currentTimeMillis()), exam);
        saveCheckListToServer();
    }

    public void deleteFromList(Exam exam){
        for(String key: checkList.keySet()){
            if(checkList.get(key).getTimeStamp()== exam.getTimeStamp()){
                checkList.remove(key);
                return;
            }
        }
        saveCheckListToServer();
    }

    public void deleteAllData(){
        checkList= new HashMap<>();
        saveCheckListToServer();
    }


    public interface Callback{
        void success();
        void fail();
    }

    // Load CheckList from firebase
    public void loadCheckListFromServer(final Callback callback){
        FirebaseConnection.getInstance().loadData("userdata/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/checklist", new FirebaseConnection.Callback() {
            @Override
            public void success(Object data) {
                HashMap<String, HashMap<String, String>> temp= (HashMap<String, HashMap<String, String>>)data;

                //Case: There is no data in database
                if(checkList== null || data== null){
                    checkList= new HashMap<>();
                }else{
                    //Case: Success to read
                    for(String key: temp.keySet()){
                        HashMap<String, String> value= temp.get(key);
                        Exam info= new Exam(value.get("title"), value.get("period_y"), value.get("period_m"), value.get("institute"), value.get("subject"), value.get("number"),
                                value.get("potential"), value.get("inputAnswer"), value.get("rightAnswer"), value.get("time"), value.get("memo"));
                        checkList.put(key, info);
                    }
                }

                Log.i("Firebase  Check", String.valueOf(checkList.size()));
                callback.success();
            }

            @Override
            public void fail(String errorMessage) {
                //Case: Connection Fail
                checkList= new HashMap<>();
                callback.fail();
            }
        });
    }

    // Save CheckList to firebase
    private void saveCheckListToServer(){
        FirebaseConnection.getInstance().saveData("userdata/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/checklist", checkList);
    }
}
