package com.example.windows7.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class CheckHistoryActivity extends AppCompatActivity {
    private ViewFlipper flipper;
    private TextView checkListBtn;
    private TextView historyListBtn;

    private final int VIEW_CHECKLIST= 0;
    private final int VIEW_HISTORYLIST= 1;

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

        checkListBtn= findViewById(R.id.checkHistory_checklistBtn);
        checkListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipper.setDisplayedChild(VIEW_CHECKLIST);
            }
        });

        historyListBtn= findViewById(R.id.checkHistory_checklistBtn);
        historyListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipper.setDisplayedChild(VIEW_HISTORYLIST);
            }
        });

        final ArrayList<ListViewItem_CheckList> checkListData= new ArrayList<>();
        final ArrayList<ListViewItem_HistoryList> historyListData= new ArrayList<>();

        CheckList.Callback callback= new CheckList.Callback() {
            @Override
            public void success() {
                RelativeLayout loadingContainer= findViewById(R.id.checkHistory_loadingContainer);
                loadingContainer.setVisibility(View.GONE);
                RelativeLayout container= findViewById(R.id.checkHistory_container);
                container.setVisibility(View.VISIBLE);

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
                    checkListData.add(index, new ListViewItem_CheckList(loadedData.get(key).getSubject(), info, loadedData.get(key).getMemo()));
                    index++;
                }
                ListViewAdapter_CheckList adapter=new ListViewAdapter_CheckList(getApplicationContext(), R.layout.item_checklist, checkListData);
                checkList.setAdapter(adapter);

            }

            @Override
            public void fail() {
                //Connection fail
                Toast.makeText(getApplicationContext(), "데이터베이스 통신 오류", Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        CheckList.getInstance().loadCheckListFromServer(callback);


    }
}
