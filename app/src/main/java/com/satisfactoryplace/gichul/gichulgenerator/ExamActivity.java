package com.satisfactoryplace.gichul.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.satisfactoryplace.gichul.gichulgenerator.fragment.ExamFragment;
import com.satisfactoryplace.gichul.gichulgenerator.model.OnBackPressedListener;

public class ExamActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        getSupportFragmentManager().beginTransaction().replace(R.id.examActivity_container, new ExamFragment(), "try").commit();
    }

    @Override
    public void onBackPressed() {
        ExamFragment examFragment= (ExamFragment)getSupportFragmentManager().findFragmentByTag("try");
        if(examFragment!= null && examFragment.isVisible()){
            if(examFragment instanceof OnBackPressedListener){
                boolean result= examFragment.onBackPressed();
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
