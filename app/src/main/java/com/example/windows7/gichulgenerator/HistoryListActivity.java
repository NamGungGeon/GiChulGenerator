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
 * Created by WINDOWS7 on 2018-01-20.
 */

public class HistoryListActivity extends AppCompatActivity {

    private ListView historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_historylist);
        init();
    }

    private void init(){
        setListView_historyList();
    }

    private void setListView_historyList(){
        // historyList - ListView setting
        historyList= findViewById(R.id.historyList);

        //Load data...
        final ArrayList<Exam> historyListData= new ArrayList<>();
        HashMap<String, Exam> loadedData= HistoryList.getInstance().getHistoryList();

        int index=0;
        for(String key: loadedData.keySet()){
            historyListData.add(index, loadedData.get(key));
            index++;
        }
        Collections.sort(historyListData, new Comparator<Exam>() {
            @Override
            public int compare(Exam e1, Exam e2) {
                return Long.valueOf(e2.getTimeStamp()).compareTo(Long.valueOf(e1.getTimeStamp()));
            }
        });

        ListViewAdapter_HistoryList historyListAdapter=new ListViewAdapter_HistoryList(getApplicationContext(), R.layout.item_historylist, historyListData);
        historyList.setAdapter(historyListAdapter);

        // Set Listener
        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(getApplicationContext(), RecheckActivity.class);
                intent.putExtra("fileName", historyListData.get(i).getFileName());
                intent.putExtra("title", historyListData.get(i).getTitle());
                intent.putExtra("potential", historyListData.get(i).getPotential());

                startActivity(intent);
            }
        });
    }
}
