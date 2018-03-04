package com.example.windows7.gichulgenerator;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by WINDOWS7 on 2018-01-25.
 */

public class CheckList {
    private ArrayList<Question> checkList= new ArrayList<>();

    private static CheckList inst= null;
    private CheckList() {
    }

    public static CheckList getInstance(){
        if(inst== null){
            inst= new CheckList();
        }
        return inst;
    }

    public ArrayList<Question> getCheckList(){
        return checkList;
    }

    public void addToList(Question question){
        checkList.add(question);
        saveCheckListToServer();
    }

    public void deleteFromList(Question question){
        checkList.remove(question);
    }

    public void deleteAllData(){
        checkList= new ArrayList<>();
        saveCheckListToServer();
    }


    public interface Callback{
        void success();
        void fail();
    }

    // Load CheckList from firebase
    public void loadCheckListFromServer(final Callback callback){
        FirebaseConnection.getInstance().loadDataWithQuery(FirebaseConnection.getInstance().getReference("userdata/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/checkList").orderByKey(),
                new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                //Case: There is no data in database
                if(snapshot.getValue()== null){

                }else{
                    //Case: Success to read
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        // Action...
                        checkList.add(postSnapshot.getValue(Question.class));
                    }
                }
                callback.success();
            }

            @Override
            public void fail(String errorMessage) {
                //Case: Connection Fail
                checkList= new ArrayList<>();
                callback.fail();
            }
        });
    }

    // Save CheckList to firebase
    private void saveCheckListToServer(){
        FirebaseConnection.getInstance().saveData("userdata/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/checkList", checkList);
    }
}
