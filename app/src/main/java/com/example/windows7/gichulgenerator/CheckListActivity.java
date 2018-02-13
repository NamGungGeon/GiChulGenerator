package com.example.windows7.gichulgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by WINDOWS7 on 2018-02-09.
 */

public class CheckListActivity extends AppCompatActivity {

    private ListView checkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_checklist);

        init();
    }

    private void init(){
        setListView_checkList();
    }

    private void setListView_checkList(){
        // checkList - ListView setting
        checkList= findViewById(R.id.checkList);
        final ArrayList<Exam> checkListData= new ArrayList<>();

        //Load data...
        HashMap<String, Exam> loadedData= CheckList.getInstance().getCheckList();
        int index=0;
        for(String key: loadedData.keySet()){
            checkListData.add(index, loadedData.get(key));
            index++;
        }
        Collections.sort(checkListData, new Comparator<Exam>() {
            @Override
            public int compare(Exam e1, Exam e2) {
                return Long.valueOf(e2.getTimeStamp()).compareTo(Long.valueOf(e1.getTimeStamp()));
            }
        });

        ListViewAdapter_CheckList CheckListAdapter=new ListViewAdapter_CheckList(getApplicationContext(), R.layout.item_checklist, checkListData);
        checkList.setAdapter(CheckListAdapter);

        // Set Listener
        checkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(getApplicationContext(), RecheckActivity.class);
                intent.putExtra("fileName", checkListData.get(i).getFileName());
                intent.putExtra("title", checkListData.get(i).getTitle());
                intent.putExtra("potential", checkListData.get(i).getPotential());

                startActivity(intent);
            }
        });
        checkList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int i, long l) {
                final DialogMaker dialog= new DialogMaker();
                dialog.setValue("오답노트에서 삭제하시겠습니까?", "예", "아니오",
                        new DialogMaker.Callback() {
                            @Override
                            public void callbackMethod() {
                                CheckList.getInstance().deleteFromList(checkListData.get(i));
                                init();
                                dialog.dismiss();
                            }
                        }, null);
                dialog.show(getSupportFragmentManager(), "Ask to user: Delete this exam?");
                return true;
            }
        });
    }
}
