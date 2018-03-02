package com.example.windows7.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Windows10 on 2018-03-03.
 */

public class ExamResultListActivity extends AppCompatActivity {
    @BindView(R.id.examResultList)
    ListView examResultList;

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
        setListView();
    }

    private void setListView(){
        final ListViewAdapter_ExamResultList listViewAdapter= new ListViewAdapter_ExamResultList(getApplicationContext(), R.layout.item_examresultlist,
                ExamResultList.getInstance().getExamResultList());

        examResultList.setAdapter(listViewAdapter);
        examResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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

}
