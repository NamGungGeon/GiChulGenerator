package com.example.windows7.gichulgenerator;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class ExamTryFragment extends Fragment{
    private String selectedSubject;
    private String selectedProb;
    private String selectedInst;

    private ImageView questionImage;
    private EditText answer_text;
    private RadioGroup answer_radio;

    private Button submitButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_examtry, container, false);
        init(rootView);
        return rootView;
    }

    private void init(ViewGroup rootView){
        selectedSubject= getActivity().getIntent().getStringExtra("subj");
        selectedProb= getActivity().getIntent().getStringExtra("prob");
        selectedInst= getActivity().getIntent().getStringExtra("inst");

        questionImage= rootView.findViewById(R.id.question);
        questionImage.setImageDrawable(getQuestion());

        answer_radio= rootView.findViewById(R.id.answer_radio);
        answer_radio.setVisibility(View.VISIBLE);

        answer_text= rootView.findViewById(R.id.answer_text);
        answer_text.setVisibility(View.INVISIBLE);

        submitButton= rootView.findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recheckAnswer(getAnswer());
            }
        });
    }

    private Drawable getQuestion(){
        //일단은 임시적으로 설정값은 고려하지 않고 한 이미지로 대체
        Drawable drawable= getResources().getDrawable(R.drawable.sunung_2018_imath_7, null);
        return drawable;
    }

    private String getAnswer(){
        String result;
        if(answer_radio.getVisibility()== View.VISIBLE){
            //객관식
            int answer=0;
            int checkedAnswer= answer_radio.getCheckedRadioButtonId();
            switch(checkedAnswer){
                case R.id.one:
                    answer= 1;
                    break;
                case R.id.two:
                    answer= 2;
                    break;
                case R.id.three:
                    answer= 3;
                    break;
                case R.id.four:
                    answer= 4;
                    break;
                case R.id.five:
                    answer= 5;
                    break;
            }
            result= String.valueOf(answer);
        }else{
            //주관식
            result= answer_text.getText().toString();
        }

        return result;
    }

    private void recheckAnswer(final String answer){
        final DialogMaker dialog= new DialogMaker();
        DialogMaker.Callback pos_callback= new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                //submit user's answer. move solution page
                submitSolution(answer);
                dialog.dismiss();
            }
        };
        DialogMaker.Callback nag_callback= new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                dialog.dismiss();
            }
        };
        dialog.setValue("정답 제출", "제출", "취소", pos_callback, nag_callback);
    }
    private void submitSolution(String answer){
        getActivity().getIntent().putExtra("answer", answer);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.examContainer, new ExamSolutionFragment()).commit();
    }
}
