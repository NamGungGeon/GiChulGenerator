package com.satisfactoryplace.gichul.gichulgenerator.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.satisfactoryplace.gichul.gichulgenerator.model.OnBackPressedListener;
import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.utils.AsyncTaskUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.Common;
import com.satisfactoryplace.gichul.gichulgenerator.utils.DialogMaker;
import com.satisfactoryplace.gichul.gichulgenerator.utils.ErrorReportUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.QuestionUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.TimerUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import uk.co.senab.photoview.PhotoViewAttacher;

public class RandomQuestionFragment extends Fragment implements OnBackPressedListener {
    @BindView(R.id.examtry_loadingContainer) RelativeLayout loadingContainer;
    @BindView(R.id.examtry_container) RelativeLayout examTryContainer;

    @BindView(R.id.examTitle) TextView title;
    @BindView(R.id.examProbability) TextView potential;
    @BindView(R.id.question) ImageView questionImage;
    @BindView(R.id.answer_text) EditText answer_text;
    @BindView(R.id.answer_radio) RadioGroup answer_radio;
    @BindView(R.id.timer) TextView timer;

    @BindView(R.id.randomQuestion_ad) AdView adView;

    private TimerUtil timerUtil= null;
    private int questionType;
    private final int choiceType= 1511;
    private final int inputType= 1512;

    private HashMap<String, String> potentialList= new HashMap<>();

    private Unbinder unbinder;

    //inst is null.
    //must be initialized.
    private QuestionNameBuilder qnBuilder= QuestionNameBuilder.inst;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_randomquestion, container, false);
        unbinder= ButterKnife.bind(this, rootView);

        init();

        return rootView;
    }

    //After end to load potentials, will call init().
    private void init(){
        //Selected value in Spinner
        String selectedSubj= getActivity().getIntent().getStringExtra("subj");
        String selectedPoten= getActivity().getIntent().getStringExtra("prob");
        String selectedInst= getActivity().getIntent().getStringExtra("inst");
        String selectedY= getActivity().getIntent().getStringExtra("peri");

        if(selectedY.equals("2015~2017")){
            String yList[]= {"2015", "2016", "2017"};
            selectedY= yList[new Random().nextInt(yList.length)];
        }

        String e_inst= QuestionUtil.create_eInst(selectedY, selectedInst);
        String k_inst= QuestionUtil.create_kInst(e_inst);
        String m= QuestionUtil.createPeriodM(selectedY, e_inst);

        QuestionNameBuilder.inst= new QuestionNameBuilder(selectedY, m, k_inst, selectedSubj, QuestionNameBuilder.UNDEFINED
                , QuestionNameBuilder.UNDEFINED, QuestionNameBuilder.TYPE_KOR);
        qnBuilder= QuestionNameBuilder.inst;

        //Potential Load
        loadPotentials(selectedPoten);
    }
    private void initPotentialText(){
        potential.setText(QuestionUtil.getPotentialText(Integer.valueOf(qnBuilder.potential)));
    }

    private void loadPotentials(String selectedPoten){
        String path= qnBuilder.createPotentialPath();
        FirebaseConnection.getInstance().loadData(path, new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                ArrayList<Long> temp= (ArrayList<Long>)snapshot.getValue();

                for(int i=1; i<temp.size(); i++){
                    potentialList.put(String.valueOf(i), String.valueOf(temp.get(i)));
                }

                ArrayList<String> numberList= QuestionUtil.getNumberList_asPotential(potentialList, selectedPoten);
                qnBuilder.number= QuestionUtil.getRandomNumber(numberList);
                qnBuilder.potential= potentialList.get(qnBuilder.number);

                // init
                initAdView();
                initPotentialText();
                initTitle();
                loadQuestionImage();
                initAnswerType();
            }

            @Override
            public void fail(String errorMessage) {
                Toast.makeText(getContext(), "데이터베이스 통신 실패: "+ errorMessage, Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }
    private void loadQuestionImage(){
        String path= qnBuilder.createImagePath(QuestionNameBuilder.TYPE_Q);
        FirebaseConnection.getInstance().loadImage(path, questionImage, getContext(), new FirebaseConnection.ImageLoadFinished() {
                    @Override
                    public void success(Bitmap bitmap) {
                        loadingContainer.setVisibility(View.GONE);
                        examTryContainer.setVisibility(View.VISIBLE);
                        startTimer();
                    }

                    @Override
                    public void fail(Exception e) {
                        Toast.makeText(getContext(), "이미지를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                });
        ViewUtil.makeCanMagnify(questionImage);
    }

    private void initAdView(){
        Common.initAdView(adView);
    }

    private void initAnswerType(){
        if(Integer.valueOf(qnBuilder.number)> 21){
            //주관식
            answer_text.setVisibility(View.VISIBLE);
            questionType = inputType;
        }else{
            //객관식
            answer_radio.setVisibility(View.VISIBLE);
            questionType = choiceType;
        }
    }
    private void initTitle(){
        title.setText(qnBuilder.createTitileText());
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

    @NonNull
    private String getUserAnswer(){
        String result= null;

        switch (questionType){
            //객관식
            case choiceType:
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
                break;
            //주관식
            case inputType:
                result= answer_text.getText().toString();
                break;
        }

        return result;
    }
    private void submit(){
        if(timerUtil!= null){
            int sec= timerUtil.stopTimer();
            QuestionResultSaver.inst= new QuestionResultSaver(getUserAnswer(), sec);

            // Go ResultPage
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.examContainer, new RandomQuestionSolutionFragment()).commit();
        }
    }

    @OnClick(R.id.regenerateBtn)
    void regenerateQuestion(){
        timerUtil.stopTimer();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.examContainer, new RandomQuestionFragment()).commit();
    }
    @OnClick(R.id.submit)
    void clickedSubmitBtn(){
        if(getUserAnswer().equals("")){
            //No exist inputAnswer
            Toast.makeText(getContext(), "아직 정답을 입력하지 않으셨습니다", Toast.LENGTH_SHORT).show();
        }else{
            final DialogMaker dialog= new DialogMaker();
            DialogMaker.Callback pos_callback= () -> {
                submit();
                dialog.dismiss();
            };
            dialog.setValue("선택하신 답안은 "+ getUserAnswer()+ "입니다.\n제출하시겠습니까?", "제출", "취소", pos_callback, null);
            dialog.show(getActivity().getSupportFragmentManager(), "AnswerSubmit");
        }
    }

    @Override
    public boolean onBackPressed() {
        if(timerUtil!= null){
            timerUtil.stopTimer();
        }
        return true;
    }
    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
