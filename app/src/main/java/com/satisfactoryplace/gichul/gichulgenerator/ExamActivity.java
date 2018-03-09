package com.satisfactoryplace.gichul.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by WINDOWS7 on 2018-02-28.
 */

public class ExamActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_exam);
        getSupportFragmentManager().beginTransaction().replace(R.id.examActivity_container, new ExamFragment(), "try").commit();
    }

    @Override
    public void onBackPressed() {
        ExamFragment examFragment= (ExamFragment)getSupportFragmentManager().findFragmentByTag("try");
        if(examFragment!= null && examFragment.isVisible()){
            if(examFragment instanceof OnBackPressedListener){
                boolean result= ((OnBackPressedListener)examFragment).onBackPressed();
                if(result== false){
                    return;
                }else{
                    super.onBackPressed();
                }
            }
        }
        super.onBackPressed();
    }
}
