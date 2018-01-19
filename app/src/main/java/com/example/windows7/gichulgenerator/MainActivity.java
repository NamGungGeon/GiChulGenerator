package com.example.windows7.gichulgenerator;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new MainPageLodingFragment()).commit();

        Runnable thread= new Runnable() {
            @Override
            public void run() {
                long time= System.currentTimeMillis();
                while(true){
                    if(System.currentTimeMillis()- time>= 3000){
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new MainPageLodingFragment()).commit();
                        break;
                    }
                }
            }
        };
        thread.run();


    }

    private long timer= 0;
    @Override
    public void onBackPressed() {
        timer= System.currentTimeMillis();
        if(System.currentTimeMillis()- timer<= 2000){
            super.onBackPressed();
        }else{
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_LONG);
        }
    }
}
