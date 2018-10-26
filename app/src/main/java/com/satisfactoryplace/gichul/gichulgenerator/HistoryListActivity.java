package com.satisfactoryplace.gichul.gichulgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.ads.AdView;
import com.satisfactoryplace.gichul.gichulgenerator.adapter.HistoryListAdapter;
import com.satisfactoryplace.gichul.gichulgenerator.data.QuestionNameBuilder;
import com.satisfactoryplace.gichul.gichulgenerator.model.Question;
import com.satisfactoryplace.gichul.gichulgenerator.utils.Common;
import com.satisfactoryplace.gichul.gichulgenerator.utils.HistoryListUtil;

import java.util.ArrayList;

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


        setContentView(R.layout.activity_historylist);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initListView(Common.getFilterValue(filter));
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initListView(Common.getFilterValue(filter));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        initAdView();
    }

    private void initAdView(){
        Common.initAdView(adView);
    }
    private void initListView(String subjectFilter) {
        final ArrayList<Question> historyListData= HistoryListUtil.getFilteredList(subjectFilter);

        HistoryListAdapter historyListAdapter = new HistoryListAdapter(getApplicationContext(), R.layout.item_historylist, historyListData);
        historyList.setAdapter(historyListAdapter);

        // 클릭 시 문제 재확인 가능
        historyList.setOnItemClickListener((adapterView, view, i, l) -> {
            Question q= historyListData.get(i);
            QuestionNameBuilder.inst= new QuestionNameBuilder(q.getPeriod_y(), q.getPeriod_m(), q.getInstitute()
                    , q.getSubject(), q.getNumber(), q.getPotential(), QuestionNameBuilder.TYPE_ENG);

            Intent intent= new Intent(getApplicationContext(), RecheckQuestionActivity.class);
            intent.putExtra("memo", q.getMemo());
            startActivity(intent);
        });
    }

}
