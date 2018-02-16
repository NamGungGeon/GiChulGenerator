package com.example.windows7.gichulgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class HistoryListActivity extends AppCompatActivity {

    @BindView(R.id.historyList)
    ListView historyList;
    @BindView(R.id.historyList_filter)
    Spinner filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_historylist);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setListView(convertFilterValue());
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setListView(convertFilterValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String convertFilterValue() {
        String subjectFilter = filter.getSelectedItem().toString();
        //Converting...
        if (subjectFilter.equals("상관없음")) {
            subjectFilter = null;
        } else if (subjectFilter.equals("수학(이과)")) {
            subjectFilter = "imath";
        } else if (subjectFilter.equals("수학(문과)")) {
            subjectFilter = "mmath";
        } else if (subjectFilter.equals("국어")) {
            subjectFilter = "korean";
        } else if (subjectFilter.equals("영어")) {
            subjectFilter = "english";
        } else if (subjectFilter.equals("사회탐구")) {
            subjectFilter = "social";
        } else if (subjectFilter.equals("과학탐구")) {
            subjectFilter = "science";
        }
        return subjectFilter;
    }


    private void setListView(String subjectFilter) {
        //Load data...
        final ArrayList<Exam> historyListData = new ArrayList<>();
        HashMap<String, Exam> loadedData = HistoryList.getInstance().getHistoryList();

        for (String key : loadedData.keySet()) {
            if (subjectFilter == null) {
                //상관없음
                historyListData.add(loadedData.get(key));
            } else {
                if (subjectFilter.equals(loadedData.get(key).getSubject())) {
                    historyListData.add(loadedData.get(key));
                }
            }
        }
        Collections.sort(historyListData, new Comparator<Exam>() {
            @Override
            public int compare(Exam e1, Exam e2) {
                return Long.valueOf(e2.getTimeStamp()).compareTo(Long.valueOf(e1.getTimeStamp()));
            }
        });

        ListViewAdapter_HistoryList historyListAdapter = new ListViewAdapter_HistoryList(getApplicationContext(), R.layout.item_historylist, historyListData);
        historyList.setAdapter(historyListAdapter);

        // Set Listener
        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), RecheckActivity.class);
                intent.putExtra("fileName", historyListData.get(i).getFileName());
                intent.putExtra("title", historyListData.get(i).getTitle());
                intent.putExtra("potential", historyListData.get(i).getPotential());

                startActivity(intent);
            }
        });
        }
    }
