package com.example.windows7.gichulgenerator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class ExamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        getSupportFragmentManager().beginTransaction().replace(R.id.examContainer, new ExamTryFragment(), "examTry").commit();
        setContentView(R.layout.activity_exam);
    }

    @Override
    public void onBackPressed() {
        for(Fragment fragment: getSupportFragmentManager ().getFragments()){
            if(fragment instanceof OnBackPressedListener){
                ((OnBackPressedListener) fragment).onBackPressed();
            }
        }
        super.onBackPressed();
    }
}
