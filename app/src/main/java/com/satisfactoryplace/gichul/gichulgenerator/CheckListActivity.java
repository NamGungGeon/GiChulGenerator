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
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

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

        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_checklist);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        //setListView with filter
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
        setAdView();
    }

    private void setAdView(){
        MobileAds.initialize(this, "ca-app-pub-5333091392909120/8285897711");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    private String convertFilterValue(){
        String subjectFilter= filter.getSelectedItem().toString();
        //Converting...
        if(subjectFilter.equals("상관없음")){
            subjectFilter= null;
        }else if(subjectFilter.equals("수학(이과)")){
            subjectFilter= "imath";
        }else if(subjectFilter.equals("수학(문과)")){
            subjectFilter= "mmath";
        }

        return subjectFilter;
    }

    private void setListView(String subjectFilter){
        ArrayList<Question> temp= new ArrayList();
        if(subjectFilter!= null){
            if(subjectFilter.equals("imath")){
                for(Question q: CheckList.getInstance().getCheckList()){
                    if(q.getSubject().equals("imath")){
                        temp.add(q);
                    }
                }
            }else if(subjectFilter.equals("mmath")){
                for(Question q: CheckList.getInstance().getCheckList()){
                    if(q.getSubject().equals("mmath")){
                        temp.add(q);
                    }
                }
            }
        }else{
            temp= CheckList.getInstance().getCheckList();
        }
        final ArrayList<Question> checkListData= temp;

        Collections.sort(checkListData, new Comparator<Question>() {
            @Override
            public int compare(Question e1, Question e2) {
                return Long.valueOf(e2.getTimeStamp()).compareTo(Long.valueOf(e1.getTimeStamp()));
            }
        });

        ListViewAdapter_CheckList CheckListAdapter=new ListViewAdapter_CheckList(getApplicationContext(), R.layout.item_checklist, checkListData);
        checkList.setAdapter(CheckListAdapter);

        // Set Listener
        checkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(getApplicationContext(), RecheckQuestionActivity.class);
                intent.putExtra("fileName", checkListData.get(i).getFileName());
                intent.putExtra("title", checkListData.get(i).getTitle());
                intent.putExtra("potential", checkListData.get(i).getPotential());

                startActivity(intent);
            }
        });
        checkList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int i, long l) {
                final DialogMaker dialog= new DialogMaker();
                dialog.setValue("오답노트에서 삭제하시겠습니까?", "예", "아니오",
                        new DialogMaker.Callback() {
                            @Override
                            public void callbackMethod() {
                                CheckList.getInstance().deleteFromList(checkListData.get(i));
                                init();
                                dialog.dismiss();
                            }
                        }, null);
                dialog.show(getSupportFragmentManager(), "Ask to user: Delete this exam?");
                return true;
            }
        });
    }
}
