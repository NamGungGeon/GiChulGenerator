package com.satisfactoryplace.gichul.gichulgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.satisfactoryplace.gichul.gichulgenerator.adapter.ExamResultListAdapter;
import com.satisfactoryplace.gichul.gichulgenerator.data.ExamNameBuilder;
import com.satisfactoryplace.gichul.gichulgenerator.data.ExamResultSaver;
import com.satisfactoryplace.gichul.gichulgenerator.model.ExamResult;
import com.satisfactoryplace.gichul.gichulgenerator.utils.Common;
import com.satisfactoryplace.gichul.gichulgenerator.utils.DialogMaker;
import com.satisfactoryplace.gichul.gichulgenerator.utils.ExamResultListUtil;

import java.util.ArrayList;
import java.util.Collections;

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

        setContentView(R.layout.activity_examresultlist);
        ButterKnife.bind(this);

        init();
    }

    private void init(){
        initAdView();
        initListView();
    }
    private void initAdView(){
        Common.initAdView(adView);
    }
    private void initListView(){
        ArrayList<ExamResult> resultListData= ExamResultListUtil.getInstance().getExamResultList();
        //시간순으로 정렬
        Collections.sort(resultListData, (e1, e2) -> Long.valueOf(e2.timeStamp).compareTo(Long.valueOf(e1.timeStamp)));

        final ExamResultListAdapter listViewAdapter= new ExamResultListAdapter(getApplicationContext(), R.layout.item_examresultlist, resultListData);
        examResultList.setAdapter(listViewAdapter);
        examResultList.setOnItemClickListener((adapterView, view, i, l) -> passRequiredData(ExamResultListUtil.getInstance().getExamResultList().get(i)));

        examResultList.setOnItemLongClickListener((adapterView, view, i, l) -> {
            final DialogMaker dialog= new DialogMaker();
            dialog.setValue("시험 기록에서 삭제하시겠습니까?", "예", "아니오",
                    () -> {
                        ExamResultListUtil.getInstance().deleteFromList(ExamResultListUtil.getInstance().getExamResultList().get(i));
                        Toast.makeText(ExamResultListActivity.this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                        init();
                        dialog.dismiss();
                    }, null);
            dialog.show(getSupportFragmentManager(), "");
            return true;
        });
    }

    private void passRequiredData(ExamResult examResult){
        ArrayList<Integer> inputAnswers= new ArrayList();
        for(int i=0; i<examResult.inputAnswers.size(); i++){
            inputAnswers.add(i, examResult.inputAnswers.get(i).intValue());
        }

        ExamNameBuilder.inst= new ExamNameBuilder(examResult.period_y, examResult.period_m, examResult.institute, examResult.subject, ExamNameBuilder.TYPE_ENG);
        ExamResultSaver.inst= new ExamResultSaver(inputAnswers, examResult.runningTime);

        Intent intent= new Intent(getApplicationContext(), RecheckExamResultActivity.class);
        intent.putExtra("resultType", "recheck");

        startActivity(intent);
    }
}
