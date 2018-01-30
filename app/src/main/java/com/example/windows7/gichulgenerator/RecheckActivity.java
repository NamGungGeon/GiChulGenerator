package com.example.windows7.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by WINDOWS7 on 2018-01-31.
 */

public class RecheckActivity extends AppCompatActivity {
    private TextView title;
    private TextView potential;
    private ImageView examImage;
    private ImageView solutionImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_recheckexam);

        init();
    }

    private void init(){
        title= findViewById(R.id.recheck_title);
        title.setText(getIntent().getStringExtra("title"));

        potential= findViewById(R.id.recheck_potential);
        potential.setText("정답률 "+ getIntent().getStringExtra("potential"));

        examImage= findViewById(R.id.recheck_exam);
        solutionImage= findViewById(R.id.recheck_solution);

        FirebaseConnection.getInstance().loadImage("q_"+ getIntent().getStringExtra("fileName")+ "_"+getIntent().getStringExtra("potential"), examImage, getApplicationContext());
        FirebaseConnection.getInstance().loadImage("a_"+ getIntent().getStringExtra("fileName"), solutionImage, getApplicationContext());
    }
}