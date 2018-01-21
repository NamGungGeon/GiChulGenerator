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
import android.widget.TextView;

import java.util.StringTokenizer;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class ExamTryFragment extends Fragment{
    private String selectedSubject;
    private String selectedProb;
    private String selectedInst;
    private String selectedPeriod;

    //문제 파일 이름 규칙
    //타입_기간(년)_기간(월)_주최기관_과목_문제번호_정답률
    private String examFileName;

    private String examType;
    private String examPeriod_y;
    private String examPeriod_m;
    private String examInstitute;
    private String examSubject;
    private String examNumber;
    private String examProb;

    private TextView title;
    private TextView probability;
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
        questionImage= rootView.findViewById(R.id.question);
        setQuestion();
        selectedSubject= getActivity().getIntent().getStringExtra("subj");
        selectedProb= getActivity().getIntent().getStringExtra("prob");
        selectedInst= getActivity().getIntent().getStringExtra("inst");
        selectedPeriod= getActivity().getIntent().getStringExtra("Period");

        title= rootView.findViewById(R.id.examTitle);
        title.setText(examPeriod_y+"년 "+ examInstitute+ "\n"+ examPeriod_m+ "월 시험 "+examNumber+ "번 문제");
        probability= rootView.findViewById(R.id.examProbability);
        probability.setText("정답률 "+ examProb+ "%");

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

    private void setQuestion(){
        //일단은 임시적으로 설정값은 고려하지 않고 한 이미지로 대체
        examFileName= "q_2017_11_sunung_imath_7_100";
        FirebaseConnection connection= FirebaseConnection.getInstance();
        connection.loadImage(examFileName, questionImage, getContext());

        StringTokenizer tokenizer= new StringTokenizer(examFileName, "_", false);
        examType= tokenizer.nextToken();
        examPeriod_y= tokenizer.nextToken();
        examPeriod_m= tokenizer.nextToken();
        examInstitute= tokenizer.nextToken();
        examSubject= tokenizer.nextToken();
        examNumber= tokenizer.nextToken();
        examProb= tokenizer.nextToken();

        if(examSubject.equals("imath")){
            examSubject= "수학(이과)";
        }else if(examSubject.equals("mmath")){
            examSubject= "수학(문과)";
        }else if(examSubject.equals("korean")){
            examSubject= "국어";
        }else if(examSubject.equals("english")){
            examSubject= "영어";
        }else if(examSubject.equals("social")){
            examSubject= "사회탐구";
        }else if(examSubject.equals("science")){
            examSubject= "과학탐구";
        }

        if(examInstitute.equals("sunung")){
            examInstitute= "대학수학능력평가시험";
        }else if(examInstitute.equals("pyeong")){
            examInstitute= "교육과정평가원";
        }else if(examInstitute.equals("gyoyuk")){
            examInstitute= "교육청";
        }
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
            result= result+ "번";
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
        dialog.setValue("선택하신 답안은 "+ answer+ "입니다.\n제출하시겠습니까?", "제출", "취소", pos_callback, nag_callback);
        dialog.show(getActivity().getSupportFragmentManager(), "AnswerSubmit");
    }

    private void submitSolution(String answer){
        getActivity().getIntent().putExtra("answer", answer);
        getActivity().getIntent().putExtra("info", examFileName);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.examContainer, new ExamSolutionFragment()).commit();
    }
}
