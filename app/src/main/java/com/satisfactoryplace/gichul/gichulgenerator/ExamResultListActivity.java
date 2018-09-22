package com.satisfactoryplace.gichul.gichulgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.satisfactoryplace.gichul.gichulgenerator.adapter.ExamResultListAdapter;
import com.satisfactoryplace.gichul.gichulgenerator.model.ExamResult;
import com.satisfactoryplace.gichul.gichulgenerator.utils.DialogMaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Windows10 on 2018-03-03.
 */

public class ExamResultListActivity extends AppCompatActivity {
    @BindView(R.id.examResultList)
    ListView examResultList;
    @BindView(R.id.examResultListAd)
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_examresultlist);
        ButterKnife.bind(this);

        init();
    }

    private void init(){
        initAdView();
        initListView();
    }
    private void initAdView(){
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
    private void initListView(){
        ArrayList<ExamResult> resultListData= ExamResultList.getInstance().getExamResultList();
        //시간순으로 정렬
        Collections.sort(resultListData, new Comparator<ExamResult>() {
            @Override
            public int compare(ExamResult e1, ExamResult e2) {
                return Long.valueOf(e2.getTimeStamp()).compareTo(Long.valueOf(e1.getTimeStamp()));
            }
        });

        final ExamResultListAdapter listViewAdapter= new ExamResultListAdapter(getApplicationContext(), R.layout.item_examresultlist,
                resultListData);
        examResultList.setAdapter(listViewAdapter);
        examResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                passRequiredData(ExamResultList.getInstance().getExamResultList().get(i));
            }
        });

        examResultList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final DialogMaker dialog= new DialogMaker();
                dialog.setValue("시험 기록에서 삭제하시겠습니까?", "예", "아니오",
                        new DialogMaker.Callback() {
                            @Override
                            public void callbackMethod() {
                                ExamResultList.getInstance().deleteFromList(ExamResultList.getInstance().getExamResultList().get(i));
                                Toast.makeText(ExamResultListActivity.this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                                init();
                                dialog.dismiss();
                            }
                        }, null);
                dialog.show(getSupportFragmentManager(), "");
                return true;
            }
        });
    }

    private void passRequiredData(ExamResult examResult){
        Bundle bundle= new Bundle();

        ArrayList<Integer> inputAnswerArrayList= new ArrayList<>();
        for(int i=0; i<30; i++){
            inputAnswerArrayList.add(i, examResult.getInputAnswers().get(i).intValue());
        }
        bundle.putSerializable("inputAnswers", inputAnswerArrayList);

        Intent intent= new Intent(getApplicationContext(), RecheckExamResultActivity.class);
        intent.putExtra("timer", examResult.getRunningTime());
        intent.putExtra("title", examResult.getTitle());
        intent.putExtra("period_y", examResult.getPeriod_y());
        intent.putExtra("period_m", examResult.getPeriod_m());
        intent.putExtra("encodedSubject", examResult.getSubject());
        intent.putExtra("encodedInstitute", examResult.getInstitute());
        intent.putExtra("basicFileName", examResult.getBasicFileName());

        intent.putExtras(bundle);
        intent.putExtra("type", "recheck");

        startActivity(intent);
    }
}
