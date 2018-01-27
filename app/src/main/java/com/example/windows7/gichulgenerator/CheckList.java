package com.example.windows7.gichulgenerator;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by WINDOWS7 on 2018-01-25.
 */

public class CheckList {
    private HashMap<String, ExamInfo> checkList= new HashMap<>();

    // true= conection success
    // false= connection fail
    private boolean connectionStatus;

    private static CheckList inst= null;
    private CheckList() {
    }

    public static CheckList getInstance(){
        if(inst== null){
            inst= new CheckList();
        }
        return inst;
    }

    public HashMap<String, ExamInfo> getCheckList(){
        return checkList;
    }

    public void addToList(ExamInfo exam){
        checkList.put(String.valueOf(System.currentTimeMillis()), exam);
        saveCheckListToServer();
    }

    public void deleteFromList(ExamInfo exam){
        checkList.remove(exam);
        saveCheckListToServer();
    }

    public interface Callback{
        void success();
        void fail();
    }

    // Load CheckList from firebase
    public void loadCheckListFromServer(final Callback _callback){

        final FirebaseConnection.Callback callback= new FirebaseConnection.Callback() {
            @Override
            public void success(Object data) {
                checkList= (HashMap<String, ExamInfo>)data;

                //Case: There is no data in database
                if(checkList== null || checkList.size()==0){
                    checkList= new HashMap<>();
                }else{
                    //Case: Success to read
                }

                _callback.success();
            }

            @Override
            public void fail(String errorMessage) {
                //Case: Connection Fail
                checkList= new HashMap<>();
                _callback.fail();
            }
        };

        FirebaseConnection.getInstance().loadExamInfoList("userdata/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/checklist", callback);
    }

    // Save CheckList to firebase
    private void saveCheckListToServer(){
        FirebaseConnection.getInstance().saveExamInfoList("userdata/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/checklist", checkList);
    }
}
