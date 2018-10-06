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
import com.satisfactoryplace.gichul.gichulgenerator.server.FirebaseConnection;
import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.utils.DialogMaker;

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

    private Thread timerThread;
    private boolean isRunningTimer= true;
    private int answerType;
    private final int choiceType= 1511;
    private final int inputType= 1512;

    //0 is sec
    //1 is min
    final int timeSaver[]= new int[2];

    //문제 파일 이름 규칙
    //타입_기간(년)_기간(월)_주최기관_과목_문제번호
    private String examPeriod_y;
    private String examPeriod_m;
    private String examInstitute;
    private String examSubject;
    private String examNumber;
    private String examPotential;

    private String titleText;

    private Unbinder unbinder;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_searchresult_try, container, false);
        unbinder= ButterKnife.bind(this, rootView);

        initAdView();
        initPassedData();
        initAnswerType();

        convertSubject_toCode();
        convertInstitute_toCode();

        loadPotential();
        return rootView;
    }

    //Will be called init() after finish load potential
    private void loadPotential(){
        Log.i("Potential Path", "potential/" + examPeriod_y + "/" + examInstitute+ "/"+ examPeriod_m + "/" + examSubject + "/" + examNumber);
        FirebaseConnection.getInstance().loadData("potential/" + examPeriod_y + "/" + examInstitute+ "/"+ examPeriod_m + "/" + examSubject + "/" + examNumber, new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                examPotential= snapshot.getValue().toString();
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

        Intent intent= getActivity().getIntent();
        String imagePath= intent.getStringExtra("basicFileName")+"/"+ "q_"+ intent.getStringExtra("basicFileName")+ "_"+ intent.getStringExtra("number");
        Log.i("Question Path", imagePath);
        FirebaseConnection.getInstance().loadImage("exam/" + imagePath, question, getActivity().getApplicationContext(), new FirebaseConnection.ImageLoadFinished() {
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
        loadingContainer.setVisibility(View.GONE);
        mainContainer.setVisibility(View.VISIBLE);
        title.setText(titleText);

        startTimer();
        initPotentialText();
    }

    private void initAdView(){
        MobileAds.initialize(getContext(), "ca-app-pub-5333091392909120~5084648179");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
    private void initPotentialText(){
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
    }
    private void initAnswerType(){
        if(Integer.valueOf(examNumber)>=22){
            //주관식
            answer_text.setVisibility(View.VISIBLE);
            answerType = inputType;
        }else{
            //객관식
            answer_radio.setVisibility(View.VISIBLE);
            answerType = choiceType;
        }
    }
    private void initPassedData(){
        examPeriod_y= getActivity().getIntent().getStringExtra("period_y");
        examPeriod_m= getActivity().getIntent().getStringExtra("period_m");
        examInstitute= getActivity().getIntent().getStringExtra("institute");
        examSubject= getActivity().getIntent().getStringExtra("subject");
        examNumber= getActivity().getIntent().getStringExtra("number");
        titleText= examPeriod_y+"년 "+ examInstitute+ "\n"+ examSubject+ "과목 "+ examPeriod_m+ "월 시험 "+ examNumber+ "번 문제";
    }

    private void convertSubject_toCode(){
        // exam code change
        if (examSubject.equals("수학(이과)")) {
            examSubject= "imath";
        }else if (examSubject.equals("수학(문과)")) {
            examSubject= "mmath";
        }
    }
    private void convertInstitute_toCode(){
        if(examInstitute.equals("대학수학능력평가시험")){
            examInstitute= "sunung";
        }else if(examInstitute.equals("교육청")){
            examInstitute= "gyoyuk";
        }else if(examInstitute.equals("교육과정평가원")){
            examInstitute= "pyeong";
        }
    }


    private void startTimer(){
        final Handler handler= new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle= msg.getData();
                int min= bundle.getInt("min");
                int sec= bundle.getInt("sec");
                timeSaver[0]= sec;
                timeSaver[1]= min;
                timer.setText(min + "분 " + sec + "초");
            }
        };

        timerThread= new Thread(){
            @Override
            public void run() {
                int sec= -1;
                int min= 0;

                while(isRunningTimer) {
                    sec++;
                    if (sec == 60) {
                        min++;
                        sec -= 60;
                    }
                    Message msg= handler.obtainMessage();
                    Bundle bundle= new Bundle();
                    bundle.putInt("min", min);
                    bundle.putInt("sec", sec);
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                    try {
                        this.sleep(1000);
                    } catch (InterruptedException e) {
                        Toast.makeText(getContext(), "스레드 오류: "+e.getMessage(), Toast.LENGTH_LONG).show();
                        getActivity().finish();
                        break;
                    }
                }
            }
        };
        timerThread.start();
    }
    private void stopTimer(){
        isRunningTimer= false;
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
        DialogMaker.Callback pos_callback= new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                //submit user's answer. move solution page
                submit();
                dialog.dismiss();
            }
        };
        DialogMaker.Callback nag_callback= new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                dialog.dismiss();
            }
        };
        dialog.setValue("선택하신 답안은 "+ getUserAnswer()+ "입니다.\n제출하시겠습니까?", "제출", "취소", pos_callback, nag_callback);
        dialog.show(getActivity().getSupportFragmentManager(), "AnswerSubmit");
    }
    private void submit(){
        stopTimer();

        String answer= getUserAnswer();
        if(answerType == choiceType){
            if(answer.charAt(answer.length()-1)== '번'){
                answer= String.valueOf(answer.charAt(0));
            }
        }

        getActivity().getIntent().putExtra("inputAnswer", answer);
        getActivity().getIntent().putExtra("title", titleText);
        getActivity().getIntent().putExtra("sec", timeSaver[0]);
        getActivity().getIntent().putExtra("min", timeSaver[1]);
        getActivity().getIntent().putExtra("subject", examSubject);
        getActivity().getIntent().putExtra("institute", examInstitute);
        getActivity().getIntent().putExtra("potential", examPotential);

        //Already saved String List
        /*
        getActivity().getStringExtra("basicFileName")
        getActivity().getIntent().getStringExtra("period_y");
        getActivity().getIntent().getStringExtra("period_m");
        getActivity().getIntent().getStringExtra("number");
         */

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.searchResultContainer, new SearchResultSolutionFragment()).commit();
    }

    @Override
    public void onDestroy() {
        stopTimer();

        if(question.getDrawable()!= null){
            ((BitmapDrawable)question.getDrawable()).getBitmap().recycle();
        }

        unbinder.unbind();
        super.onDestroy();
    }
}