package com.satisfactoryplace.gichul.gichulgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.satisfactoryplace.gichul.gichulgenerator.adapter.HistoryListAdapter;
import com.satisfactoryplace.gichul.gichulgenerator.model.HistoryList;
import com.satisfactoryplace.gichul.gichulgenerator.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    @BindView(R.id.historyListAd)
    AdView adView;

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
        setListView(getFilterValue());
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setListView(getFilterValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setAdView();
    }

    private void setAdView(){
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
    private void setListView(String subjectFilter) {

        final ArrayList<Question> historyListData= getFilteredList(subjectFilter);

        HistoryListAdapter historyListAdapter = new HistoryListAdapter(getApplicationContext(), R.layout.item_historylist, historyListData);
        historyList.setAdapter(historyListAdapter);

        // Set Listener
        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), RecheckQuestionActivity.class);
                intent.putExtra("fileName", historyListData.get(i).getFileName());
                intent.putExtra("title", historyListData.get(i).getTitle());
                intent.putExtra("potential", historyListData.get(i).getPotential());

                startActivity(intent);
            }
        });
    }
    private ArrayList<Question> getFilteredList(String subjectFilter){
        ArrayList<Question> temp= new ArrayList();
        if(subjectFilter!= null){
            if(subjectFilter.equals("imath")){
                for(Question q: HistoryList.getInstance().getHistoryList()){
                    if(q.getSubject().equals("imath")){
                        temp.add(q);
                    }
                }
            }else if(subjectFilter.equals("mmath")){
                for(Question q: HistoryList.getInstance().getHistoryList()){
                    if(q.getSubject().equals("mmath")){
                        temp.add(q);
                    }
                }
            }
        }else{
            temp= HistoryList.getInstance().getHistoryList();
        }



        Collections.sort(temp, new Comparator<Question>() {
            @Override
            public int compare(Question e1, Question e2) {
                return Long.valueOf(e2.getTimeStamp()).compareTo(Long.valueOf(e1.getTimeStamp()));
            }
        });

        return temp;
    }
    private String getFilterValue() {
        String subjectFilter = filter.getSelectedItem().toString();
        //Converting...
        if (subjectFilter.equals("상관없음")) {
            subjectFilter = null;
        } else if (subjectFilter.equals("수학(이과)")) {
            subjectFilter = "imath";
        } else if (subjectFilter.equals("수학(문과)")) {
            subjectFilter = "mmath";
        }

        return subjectFilter;
    }
}
