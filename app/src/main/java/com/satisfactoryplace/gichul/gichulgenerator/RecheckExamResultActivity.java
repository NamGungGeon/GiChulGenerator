package com.satisfactoryplace.gichul.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Windows10 on 2018-03-03.
 */

public class RecheckExamResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_recheckexam);
        getSupportFragmentManager().beginTransaction().replace(R.id.recheckExam_container, new ExamResultFragment()).commit();
    }
}
