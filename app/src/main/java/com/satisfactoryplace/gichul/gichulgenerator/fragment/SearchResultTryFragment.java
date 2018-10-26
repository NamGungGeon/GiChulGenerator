package com.satisfactoryplace.gichul.gichulgenerator.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.satisfactoryplace.gichul.gichulgenerator.data.QuestionNameBuilder;
import com.satisfactoryplace.gichul.gichulgenerator.data.QuestionResultSaver;
import com.satisfactoryplace.gichul.gichulgenerator.model.ErrorInfo;
import com.satisfactoryplace.gichul.gichulgenerator.server.FirebaseConnection;
import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.utils.AsyncTaskUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.Common;
import com.satisfactoryplace.gichul.gichulgenerator.utils.DialogMaker;
import com.satisfactoryplace.gichul.gichulgenerator.utils.ErrorReportUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.QuestionUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.TimerUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SearchResultTryFragment extends Fragment {
    @BindView(R.id.searchResult_loadingContainer) RelativeLayout loadingContainer;
    @BindView(R.id.searchResult_container) RelativeLayout mainContainer;
    @BindView(R.id.searchResult_examTitle) TextView title;
    @BindView(R.id.searchResult_examProbability) TextView potential;
    @BindView(R.id.searchResult_question) ImageView question;
    @BindView(R.id.searchResult_answer_radio) RadioGroup answer_radio;
    @BindView(R.id.searchResult_answer_text) EditText answer_text;
    @BindView(R.id.searchResult_timer) Button timer;
    @BindView(R.id.searchResult_submit) Button submit;

    @BindView(R.id.searchResult_ad)AdView adView;

    private int answerType;
    private final int choiceType= 1511;
    private final int inputType= 1512;

    private TimerUtil timerUtil;

    private Unbinder unbinder;

    private QuestionNameBuilder qn= QuestionNameBuilder.inst;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_searchresult_try, container, false);
        unbinder= ButterKnife.bind(this, rootView);

        initAdView();
        initAnswerType();
        loadPotential();

        return rootView;
    }

    //Will be called init() after finish load potential
    private void loadPotential(){
        String path= qn.createPotentialPath();
        FirebaseConnection.getInstance().loadData(path, new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                qn.potential= snapshot.getValue().toString();
                loadQuestionImage();
            }

            @Override
            public void fail(String errorMessage) {
                Toast.makeText(getActivity().getApplicationContext(), "데이터베이스 통신 실패: "+ errorMessage, Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        });
    }
    private void loadQuestionImage(){
        String imagePath= qn.createImagePath(QuestionNameBuilder.TYPE_Q);
        FirebaseConnection.getInstance().loadImage(imagePath, question, getContext(), new FirebaseConnection.ImageLoadFinished() {
            @Override
            public void success(Bitmap bitmap) {
                init();
            }

            @Override
            public void fail(Exception e) {
                Toast.makeText(getContext(), "이미지를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }

    private void init(){
        startTimer();
        initPotentialText();
        initTitle();
        loadingOff();
    }
    private void loadingOff(){
        loadingContainer.setVisibility(View.GONE);
        mainContainer.setVisibility(View.VISIBLE);
    }
    private void initTitle(){
        title.setText(qn.createTitileText());
    }

    private void initAdView(){
        Common.initAdView(adView);
    }
    private void initPotentialText(){
        String potentialText= QuestionUtil.getPotentialText(Integer.valueOf(qn.potential));
        potential.setText(potentialText);
    }
    private void initAnswerType(){
        if(Integer.valueOf(qn.number)>=22){
            //주관식
            answer_text.setVisibility(View.VISIBLE);
            answerType = inputType;
        }else{
            //객관식
            answer_radio.setVisibility(View.VISIBLE);
            answerType = choiceType;
        }
    }


    private void startTimer(){
        timerUtil= new TimerUtil(getActivity(), timer);
        timerUtil.startTimer((Exception e)->{
            ErrorReportUtil.report(new ErrorInfo(e.toString(), "타이머 스레드 오류", "ExamFragment"));
            getActivity().runOnUiThread(()->{
                Toast.makeText(getContext(), "타이머 스레드 오류\n"+ e.toString(), Toast.LENGTH_SHORT).show();
            });
        });
    }

    private String getUserAnswer(){
        String result= "";
        if(answerType == choiceType){
            //객관식
            int answer=0;
            int checkedAnswer= answer_radio.getCheckedRadioButtonId();
            switch(checkedAnswer){
                case R.id.searchResult_answer_radio_one:
                    answer= 1;
                    break;
                case R.id.searchResult_answer_radio_two:
                    answer= 2;
                    break;
                case R.id.searchResult_answer_radio_three:
                    answer= 3;
                    break;
                case R.id.searchResult_answer_radio_four:
                    answer= 4;
                    break;
                case R.id.searchResult_answer_radio_five:
                    answer= 5;
                    break;
            }
            result= String.valueOf(answer);
            result= result+ "번";
        }else if(answerType == inputType){
            //주관식
            result= answer_text.getText().toString();
        }

        return result;
    }

    @OnClick(R.id.searchResult_submit)
    void recheckAnswer(){
        if(getUserAnswer()== null || getUserAnswer().equals("")){
            Toast.makeText(getContext(), "아직 답안을 입력하지 않으셨습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        final DialogMaker dialog= new DialogMaker();
        DialogMaker.Callback pos_callback= () -> {
            //submit user's answer. move solution page
            submit();
            dialog.dismiss();
        };
        dialog.setValue("선택하신 답안은 "+ getUserAnswer()+ "입니다.\n제출하시겠습니까?", "제출", "취소", pos_callback, null);
        dialog.show(getActivity().getSupportFragmentManager(), "AnswerSubmit");
    }
    private void submit(){
        if(timerUtil!= null){
            int sec= timerUtil.stopTimer();

            String answer= getUserAnswer();
            //주관식일 경우
            if(answerType == choiceType)
                answer= String.valueOf(answer.charAt(0));

            QuestionResultSaver.inst= new QuestionResultSaver(answer, sec);

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.searchResultContainer, new SearchResultSolutionFragment()).commit();
        }
    }

    @Override
    public void onDestroy() {
        if(timerUtil!= null){
            timerUtil.stopTimer();
        }

        unbinder.unbind();
        super.onDestroy();
    }
}