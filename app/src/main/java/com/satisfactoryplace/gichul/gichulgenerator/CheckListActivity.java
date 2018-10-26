package com.satisfactoryplace.gichul.gichulgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.ads.AdView;
import com.satisfactoryplace.gichul.gichulgenerator.adapter.CheckListAdapter;
import com.satisfactoryplace.gichul.gichulgenerator.data.QuestionNameBuilder;
import com.satisfactoryplace.gichul.gichulgenerator.utils.CheckListUtil;
import com.satisfactoryplace.gichul.gichulgenerator.model.Question;
import com.satisfactoryplace.gichul.gichulgenerator.utils.Common;
import com.satisfactoryplace.gichul.gichulgenerator.utils.DialogMaker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WINDOWS7 on 2018-02-09.
 */

public class CheckListActivity extends AppCompatActivity {

    @BindView(R.id.checkList) ListView checkList;
    @BindView(R.id.checkList_filter) Spinner filter;

    @BindView(R.id.checkListAd)
    AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_checklist);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        initAdView();
        initListView(Common.getFilterValue(filter));
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initListView(Common.getFilterValue(filter));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initAdView(){
        Common.initAdView(adView);
    }

    private void initListView(String subjectFilter){
        final ArrayList<Question> checkListData= CheckListUtil.getFilteredList(subjectFilter);

        CheckListAdapter CheckListAdapter=new CheckListAdapter(getApplicationContext(), R.layout.item_checklist, checkListData);
        checkList.setAdapter(CheckListAdapter);

        // 클릭 시 문제 재확인 가능
        checkList.setOnItemClickListener((adapterView, view, i, l) -> {
            Question q= checkListData.get(i);
            QuestionNameBuilder.inst= new QuestionNameBuilder(q.getPeriod_y(), q.getPeriod_m(), q.getInstitute()
                    , q.getSubject(), q.getNumber(), q.getPotential(), QuestionNameBuilder.TYPE_ENG);

            Intent intent= new Intent(getApplicationContext(), RecheckQuestionActivity.class);
            intent.putExtra("memo", q.getMemo());
            startActivity(intent);
        });

        //삭제 시도
        checkList.setOnItemLongClickListener((adapterView, view, i, l) -> {
            final DialogMaker dialog= new DialogMaker();
            dialog.setValue("오답노트에서 삭제하시겠습니까?", "예", "아니오",
                    () -> {
                        CheckListUtil.getInstance().deleteFromList(checkListData.get(i));
                        init();
                        dialog.dismiss();
                    }, null);
            dialog.show(getSupportFragmentManager(), "Ask to user: Delete this exam?");
            return true;
        });
    }

    @OnClick(R.id.checkList_random)
    void clickedRandomBtn(){
        Question randomQuestion= CheckListUtil.getInstance().getRandomQuestion();
        QuestionNameBuilder.inst= new QuestionNameBuilder(randomQuestion.getPeriod_y(), randomQuestion.getPeriod_m(), randomQuestion.getInstitute()
                , randomQuestion.getSubject(), randomQuestion.getNumber(), randomQuestion.getPotential(), QuestionNameBuilder.TYPE_ENG);

        Intent intent= new Intent(getApplicationContext(), RecheckQuestionActivity.class);
        intent.putExtra("memo", randomQuestion.getMemo());
        startActivity(intent);
    }
}
