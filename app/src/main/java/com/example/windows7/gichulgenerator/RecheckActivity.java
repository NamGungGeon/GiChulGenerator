package com.example.windows7.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.StringTokenizer;

/**
 * Created by WINDOWS7 on 2018-01-31.
 */

public class RecheckActivity extends AppCompatActivity {
    private TextView title;
    private TextView potential;
    private ImageView examImage;
    private ImageView solutionImage;
    private Button changeImageBtn;

    private final int exam= 15223;
    private final int solution= 115223;
    private int imageStatus= exam;

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

        changeImageBtn= findViewById(R.id.recheck_imageChange);
        changeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageStatus== exam){
                    examImage.setVisibility(View.GONE);
                    solutionImage.setVisibility(View.VISIBLE);
                    imageStatus= solution;

                    changeImageBtn.setText("문제 확인");
                }else{
                    examImage.setVisibility(View.VISIBLE);
                    solutionImage.setVisibility(View.GONE);
                    imageStatus= exam;

                    changeImageBtn.setText("해설 확인");
                }
            }
        });

        potential= findViewById(R.id.recheck_potential);
        // Hide real potential
        String examPotential= getIntent().getStringExtra("potential");
        int _potential= Integer.valueOf(examPotential);
        String potentialText= "정답률: ";
        if(_potential>= 80){
            potentialText+= "매우높음";
        }else if(_potential>=60){
            potentialText+= "높음";
        }else if(_potential>= 40){
            potentialText+= "보통";
        }else if(_potential>= 20){
            potentialText+= "낮음";
        }else{
            potentialText+= "매우낮음";
        }
        potential.setText(potentialText);

        examImage= findViewById(R.id.recheck_exam);
        solutionImage= findViewById(R.id.recheck_solution);

        String temp= getIntent().getStringExtra("fileName");
        String basicPath= "";
        StringTokenizer token= new StringTokenizer(temp, "_", false);
        basicPath+= token.nextToken()+"_";
        basicPath+= token.nextToken()+"_";
        basicPath+= token.nextToken()+"_";
        basicPath+= token.nextToken();

        FirebaseConnection.getInstance().loadImage(basicPath+"/"+ "q_"+ getIntent().getStringExtra("fileName"), examImage, getApplicationContext());
        FirebaseConnection.getInstance().loadImage(basicPath+"/"+ "a_"+ getIntent().getStringExtra("fileName"), solutionImage, getApplicationContext());
    }
}
