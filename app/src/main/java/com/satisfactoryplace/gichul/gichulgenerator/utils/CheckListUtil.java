package com.satisfactoryplace.gichul.gichulgenerator.utils;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.model.Question;
import com.satisfactoryplace.gichul.gichulgenerator.server.FirebaseConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by WINDOWS7 on 2018-01-25.
 */

public class CheckListUtil {

    public interface Callback{
        void success();
        void fail();
    }

    private ArrayList<Question> checkList= new ArrayList<>();
    private static CheckListUtil inst= null;
    private CheckListUtil() {
    }

    public static CheckListUtil getInstance(){
        if(inst== null){
            inst= new CheckListUtil();
        }
        return inst;
    }

    public ArrayList<Question> getCheckList(){
        return checkList;
    }

    public Question getRandomQuestion(){
        int size= checkList.size();
        return checkList.get(new Random().nextInt(size));
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

    // Load CheckListUtil from firebase
    public void loadCheckListFromServer(final Callback callback){
        FirebaseConnection.getInstance().loadDataWithQuery(FirebaseConnection.getInstance().getReference("userdata/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/checkList").orderByKey(),
                new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    checkList.add(postSnapshot.getValue(Question.class));
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

    // Save CheckListUtil to firebase
    private void saveCheckListToServer(){
        FirebaseConnection.getInstance().saveData("userdata/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/checkList", checkList);
    }
    public static ArrayList<Question> getFilteredList(String filter){
        ArrayList<Question> temp= new ArrayList();
        //Adjust Filter Option
        if(filter!= null){
            if(filter.equals("imath")){
                //수학(이과)
                for(Question q: CheckListUtil.getInstance().getCheckList()){
                    if(q.getSubject().equals("imath")){
                        temp.add(q);
                    }
                }
            }else if(filter.equals("mmath")){
                //수학(문과)
                for(Question q: CheckListUtil.getInstance().getCheckList()){
                    if(q.getSubject().equals("mmath")){
                        temp.add(q);
                    }
                }
            }
        }else{
            //상관없음
            temp= CheckListUtil.getInstance().getCheckList();
        }

        //시간순으로 정렬
        Collections.sort(temp, (e1, e2) -> Long.valueOf(e2.getTimeStamp()).compareTo(Long.valueOf(e1.getTimeStamp())));

        return temp;
    }

    public static void saveQuestion(@NonNull Question q, @NonNull Fragment fragment){
        final DialogMaker dialog= new DialogMaker();
        final View childView= fragment.getLayoutInflater().inflate(R.layout.dialog_addtochecklist, null);
        DialogMaker.Callback pos_callback= () -> {
            //입력한 메모 추가
            EditText memoBox= childView.findViewById(R.id.memoBox);
            q.setMemo(memoBox.getText().toString());
            CheckListUtil.getInstance().addToList(q);

            Toast.makeText(fragment.getContext(), "오답노트에 저장되었습니다", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        };

        dialog.setValue("문제를 오답노트에 추가합니다.", "저장", "취소", pos_callback,null, childView);
        dialog.show(fragment.getFragmentManager(), "addToCheckList");
    }
}
