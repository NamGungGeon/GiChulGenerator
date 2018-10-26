package com.satisfactoryplace.gichul.gichulgenerator.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.satisfactoryplace.gichul.gichulgenerator.data.QuestionNameBuilder;
import com.satisfactoryplace.gichul.gichulgenerator.data.QuestionResultSaver;
import com.satisfactoryplace.gichul.gichulgenerator.server.FirebaseConnection;
import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.utils.CheckListUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.Common;
import com.satisfactoryplace.gichul.gichulgenerator.utils.HistoryListUtil;
import com.satisfactoryplace.gichul.gichulgenerator.model.Question;
import com.satisfactoryplace.gichul.gichulgenerator.utils.BitmapManager;
import com.satisfactoryplace.gichul.gichulgenerator.utils.DialogMaker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by WINDOWS7 on 2018-02-09.
 */

public class SearchResultSolutionFragment extends Fragment {
    @BindView(R.id.searchResult_solution_loadingContainer)RelativeLayout loadingContainer;
    @BindView(R.id.searchResult_solution_container)RelativeLayout solutionContainer;
    @BindView(R.id.searchResult_solution_title)TextView title;
    @BindView(R.id.searchResult_solution_checker)TextView answerChecker;
    @BindView(R.id.searchResult_solution_solution)ImageView solution;
    @BindView(R.id.rsearchResult_solution_question)ImageView question;
    @BindView(R.id.searchResult_solution_addToCheckListBtn)Button addToCheckListBtn;
    @BindView(R.id.searchResult_solution_changeImageBtn) Button changeImageBtn;

    @BindView(R.id.searchResultSolution_ad)AdView adView;

    private String inputAnswer;
    private String rightAnswer;

    private Unbinder unbinder;

    private final int SOLUTION= 1234;
    private final int EXAM= 1235;
    private int imageStatus= SOLUTION;

    private BitmapManager bManager= new BitmapManager();

    private QuestionNameBuilder qn= QuestionNameBuilder.inst;
    private QuestionResultSaver qs= QuestionResultSaver.inst;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_searchresult_solution, container, false);
        unbinder= ButterKnife.bind(this, rootView);
        init();

        return rootView;
    }

    private void loadAnswer(){
        String answerPath= qn.createRightAnswerPath();
        FirebaseConnection.getInstance().loadData(answerPath, new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                rightAnswer= snapshot.getValue().toString();
                inputAnswer= qs.input;

                checkAnswer();
                saveHistory();
            }

            @Override
            public void fail(String errorMessage) {
                Toast.makeText(getActivity().getApplicationContext(), "데이터베이스 통신 실패: "+ errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadImages(){
        loadQuestionImage();
        loadSolutionImage();
    }
    private void loadSolutionImage(){
        String solutionPath= qn.createImagePath(QuestionNameBuilder.TYPE_A);

        FirebaseConnection.getInstance().loadImage(solutionPath, solution, getActivity().getApplicationContext(), new FirebaseConnection.ImageLoadFinished() {
            @Override
            public void success(Bitmap bitmap) {
                loadingOff();
                bManager.addBitmap(bitmap);
            }

            @Override
            public void fail(Exception e) {
                Toast.makeText(getContext(), "이미지를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }
    private void loadQuestionImage(){
        String questionPath= qn.createImagePath(QuestionNameBuilder.TYPE_Q);
        FirebaseConnection.getInstance().loadImage(questionPath, question, getActivity().getApplicationContext(), new FirebaseConnection.ImageLoadFinished() {
            @Override
            public void success(Bitmap bitmap) {
                bManager.addBitmap(bitmap);
            }

            @Override
            public void fail(Exception e) {
                Toast.makeText(getContext(), "이미지를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }
    private void loadingOff(){
        loadingContainer.setVisibility(View.GONE);
        solutionContainer.setVisibility(View.VISIBLE);
    }

    private void init(){
        initTitle();
        initAdView();
        loadAnswer();
        loadImages();
    }
    private void initTitle(){
        title.setText(qn.createTitileText());
    }
    private void initAdView(){
        Common.initAdView(adView);
    }

    private void checkAnswer(){
        if(inputAnswer.equals(rightAnswer)){
            //정답
            answerChecker.setText("정답입니다! \n입력하신 답안은 "+ inputAnswer+" 입니다.");
            answerChecker.setTextColor(getResources().getColor(R.color.green));
        }else{
            //오답
            answerChecker.setText("오답입니다! \n입력하신 답안은 "+ inputAnswer+" 이지만, 정답은 "+ rightAnswer+ " 입니다.");
            answerChecker.setTextColor(getResources().getColor(R.color.red));
        }
    }
    private void saveHistory(){
        HistoryListUtil.getInstance().addToList(
                new Question(qn.createTitileText(), qn.createFileName(),
                qn.potential, inputAnswer, rightAnswer, String.valueOf(qs.t_sec), ""));
    }

    @OnClick(R.id.searchResult_solution_changeImageBtn)
    void changeImage(){
        if(imageStatus== SOLUTION){
            changeImageBtn.setText("해설 다시 확인");
            question.setVisibility(View.VISIBLE);
            solution.setVisibility(View.GONE);
            imageStatus= EXAM;
        }else if(imageStatus== EXAM){
            changeImageBtn.setText("문제 다시 확인");
            question.setVisibility(View.GONE);
            solution.setVisibility(View.VISIBLE);
            imageStatus= SOLUTION;
        }
    }
    @OnClick(R.id.searchResult_solution_addToCheckListBtn)
    void saveCheckList(){
        Question currentQuestion= new Question(qn.createTitileText(), qn.createFileName(),
                qn.potential, inputAnswer, rightAnswer, String.valueOf(qs.t_sec), "");
        CheckListUtil.saveQuestion(currentQuestion, this);
    }

    @OnClick(R.id.searchResult_searchSolution)
    void searchSolution(){
        Toast.makeText(getContext(), "ebs 강의 검색 페이지로 이동합니다. (로그인 필요)", Toast.LENGTH_SHORT).show();

        String url = "http://www.ebsi.co.kr/ebs/xip/xipa/retrieveSCVLastExamList.ebs";
        Common.openUrl(getContext(), url);
    }

    @Override
    public void onDestroy() {
        bManager.recycleAllBitmaps();
        unbinder.unbind();
        super.onDestroy();
    }

}