package com.satisfactoryplace.gichul.gichulgenerator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.satisfactoryplace.gichul.gichulgenerator.fragment.RandomQuestionFragment;
import com.satisfactoryplace.gichul.gichulgenerator.model.OnBackPressedListener;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class RandomQuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction().replace(R.id.examContainer, new RandomQuestionFragment(), "examTry").commit();
        setContentView(R.layout.activity_randomquestion);
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
