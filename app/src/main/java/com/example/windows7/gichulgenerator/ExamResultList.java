package com.example.windows7.gichulgenerator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

/**
 * Created by Windows10 on 2018-03-03.
 */

public class ExamResultList {
    private ArrayList<ExamResult> examResultList= new ArrayList<>();

    private static ExamResultList inst= null;
    private ExamResultList(){}

    public static ExamResultList getInstance(){
        if(inst== null){
            inst= new ExamResultList();
        }
        return inst;
    }

    public ArrayList<ExamResult> getExamResultList(){
        return examResultList;
    }

    public void addToList(ExamResult examResult){
        examResultList.add(examResult);
        saveExamResultListToFirebase();
    }
    public void deleteFromList(ExamResult examResult){
        for(ExamResult result: examResultList){
            if(result.getTimeStamp()== examResult.getTimeStamp()){
                examResultList.remove(result);
                break;
            }
        }
        saveExamResultListToFirebase();
    }
    public void deleteAllData(){
        examResultList= new ArrayList<>();
        saveExamResultListToFirebase();
    }

    public interface Callback{
        void success();
        void fail();
    }

    public void loadExamResultListFromFirebase(final Callback callback){
        FirebaseConnection.getInstance().loadData("userdata/" + FirebaseAuth.getInstance().getUid() + "/examResultList", new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                if(snapshot!= null){
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        if(postSnapshot.getValue()!= null){
                            examResultList.add(postSnapshot.getValue(ExamResult.class));
                        }else{
                            examResultList= new ArrayList<>();
                            callback.fail();
                        }
                    }
                    callback.success();
                }else{
                    examResultList= new ArrayList<>();
                    callback.fail();
                }
            }

            @Override
            public void fail(String errorMessage) {
                examResultList= new ArrayList<>();
                callback.fail();
            }
        });
    }

    private void saveExamResultListToFirebase(){
        FirebaseConnection.getInstance().saveData("userdata/" + FirebaseAuth.getInstance().getUid() + "/examResultList", examResultList);
    }
}
