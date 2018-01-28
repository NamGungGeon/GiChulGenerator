package com.example.windows7.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class CheckHistoryActivity extends AppCompatActivity {
    private ViewFlipper flipper;
    private Button checkListBtn;
    private Button historyListBtn;

    private final int VIEW_HISTORYLIST= 0;
    private final int VIEW_CHECKLIST= 1;

    private ListView checkList;
    private ListView historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_checkhistory);
        init();
    }


    private void init(){
        flipper= findViewById(R.id.checkHistory_flipper);
        checkListBtn= findViewById(R.id.checkHistory_checkListBtn);
        checkListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipper.setDisplayedChild(VIEW_CHECKLIST);
            }
        });
        historyListBtn= findViewById(R.id.checkHistory_historyListBtn);
        historyListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipper.setDisplayedChild(VIEW_HISTORYLIST);
            }
        });

        // checkList - ListView setting
        checkList= findViewById(R.id.checkList);
        final ArrayList<ListViewItem_CheckList> checkListData= new ArrayList<>();
        CheckList.Callback callback_checkList= new CheckList.Callback() {
            @Override
            public void success() {
                //Load data...
                HashMap<String, ExamInfo> loadedData= CheckList.getInstance().getCheckList();
                Log.d("Firebase Check", String.valueOf(loadedData.size()));

                int index=0;
                for(String key: loadedData.keySet()){
                    String rightAnswer= ((ExamInfo)(loadedData.get(key))).getRightAnswer();
                    String inputAnswer= ((ExamInfo)(loadedData.get(key))).getInputAnswer();
                    String info;
                    if(rightAnswer.equals(inputAnswer)){
                        info= "정답";
                    }else{
                        info= "오답";
                    }
                    checkListData.add(index, new ListViewItem_CheckList(loadedData.get(key).getTitle(), info, loadedData.get(key).getMemo()));
                    index++;
                }
                ListViewAdapter_CheckList CheckListAdapter=new ListViewAdapter_CheckList(getApplicationContext(), R.layout.item_checklist, checkListData);
                checkList.setAdapter(CheckListAdapter);
            }

            @Override
            public void fail() {
                //Connection fail
                Toast.makeText(getApplicationContext(), "데이터베이스 통신 오류", Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        CheckList.getInstance().loadCheckListFromServer(callback_checkList);

        // historyList - ListView setting
        final ArrayList<ListViewItem_HistoryList> historyListData= new ArrayList<>();
        historyList= findViewById(R.id.historyList);
        HistoryList.Callback callback_historyList= new HistoryList.Callback() {
            @Override
            public void success() {
                RelativeLayout loadingContainer= findViewById(R.id.checkHistory_loadingContainer);
                loadingContainer.setVisibility(View.GONE);
                RelativeLayout container= findViewById(R.id.checkHistory_container);
                container.setVisibility(View.VISIBLE);

                //Load data...
                HashMap<String, ExamInfo> loadedData= HistoryList.getInstance().getHistoryList();
                Log.d("Firebase Check", String.valueOf(loadedData.size()));

                int index=0;
                for(String key: loadedData.keySet()){
                    String rightAnswer= ((ExamInfo)(loadedData.get(key))).getRightAnswer();
                    String inputAnswer= ((ExamInfo)(loadedData.get(key))).getInputAnswer();
                    String info;
                    if(rightAnswer.equals(inputAnswer)){
                        info= "정답";
                    }else{
                        info= "오답";
                    }
                    historyListData.add(index, new ListViewItem_HistoryList(loadedData.get(key).getTitle(), info));
                    index++;
                }
                ListViewAdapter_HistoryList historyListAdapter=new ListViewAdapter_HistoryList(getApplicationContext(), R.layout.item_historylist, historyListData);
                historyList.setAdapter(historyListAdapter);
            }

            @Override
            public void fail() {
                //Connection fail
                Toast.makeText(getApplicationContext(), "데이터베이스 통신 오류", Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        HistoryList.getInstance().loadHistoryListFromServer(callback_historyList);
    }

}
