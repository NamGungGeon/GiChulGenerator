package com.satisfactoryplace.gichul.gichulgenerator.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.satisfactoryplace.gichul.gichulgenerator.model.ErrorInfo;
import com.satisfactoryplace.gichul.gichulgenerator.server.FirebaseConnection;
import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.utils.CheckListUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.Common;
import com.satisfactoryplace.gichul.gichulgenerator.utils.HistoryListUtil;
import com.satisfactoryplace.gichul.gichulgenerator.model.Question;
import com.satisfactoryplace.gichul.gichulgenerator.utils.DialogMaker;
import com.satisfactoryplace.gichul.gichulgenerator.utils.ErrorReportUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class RandomQuestionSolutionFragment extends Fragment{
    //해답 파일 명명 규칙
    //타입_기간(년)_기간(월)_주최기관_과목_문제번호
    private QuestionNameBuilder qn= QuestionNameBuilder.inst;
    private QuestionResultSaver qs= QuestionResultSaver.inst;

    @BindView(R.id.solutionLoadingContainer) RelativeLayout loadingContainer;
    @BindView(R.id.solutionContainer) RelativeLayout solutionContainer;
    @BindView(R.id.solution_examInfo) TextView title;
    @BindView(R.id.solutionTitle) TextView resultText;
    @BindView(R.id.solutionImage) ImageView solutionImage;
    @BindView(R.id.recheck_examImage) ImageView recheckExamImage;

    @BindView(R.id.changeImageBtn) Button changeImageBtn;

    @BindView(R.id.randomQuestion_solution_ad)AdView adView;

    private final int SOLUTION= 1234;
    private final int EXAM= 1235;
    private int imageStatus= SOLUTION;

    private String inputAnswer;
    private String rightAnswer;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_randomquestionsolution, container, false);
        unbinder= ButterKnife.bind(this, rootView);

        initAdView();
        loadInputAnswer();
        loadRightAnswer();

        return rootView;
    }

    private void loadInputAnswer(){
        inputAnswer= qs.input;
    }
    private void loadRightAnswer(){
        String path= qn.createRightAnswerPath();
        FirebaseConnection.getInstance().loadData(path, new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                rightAnswer= String.valueOf(snapshot.getValue());
                init();
            }

            @Override
            public void fail(String errorMessage) {
                Toast.makeText(getContext(), "ErrorInfo: "+ errorMessage, Toast.LENGTH_LONG).show();
                ErrorReportUtil.report(
                        new ErrorInfo(errorMessage, "RightAnswer 로드 실패.\n시도한 경로: "+ path,
                                "RandomQuestionSolutionFragment"));
                getActivity().finish();
            }
        });

    }

    //Will be Called init() after finishing load rightAnswer
    private void init(){
        initResultText();
        initImages();
        initTitle();
     }
     private void initTitle(){
         title.setText(qn.createTitileText());
     }

    //initialize examImage, solutionImage.
    private void initImages(){
        initSolutionImage();
        initExamImage();
    }

    private void initSolutionImage(){
        String solutionImgPath= qn.createImagePath(QuestionNameBuilder.TYPE_A);
        FirebaseConnection.getInstance().loadImage(solutionImgPath, solutionImage, getContext(), new FirebaseConnection.ImageLoadFinished() {
            @Override
            public void success(Bitmap bitmap) {
                loadingContainer.setVisibility(View.INVISIBLE);
                solutionContainer.setVisibility(View.VISIBLE);
                saveToHistoryList();
            }

            @Override
            public void fail(Exception e) {
                Toast.makeText(getContext(), "이미지를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                ErrorReportUtil.report(
                        new ErrorInfo(e.toString(), "Solution Image 로드 실패.\n시도한 경로: "+ solutionImgPath,
                                "RandomQuestionSolutionFragment"));
                getActivity().finish();
            }
        });
    }

    //RandomQuestionFragment에서 ExamImage는 캐시되기 때문에 대기를 기다릴 필요가 없다.
    private void initExamImage(){
        String examImgPath= qn.createImagePath(QuestionNameBuilder.TYPE_Q);
        FirebaseConnection.getInstance().loadImage(examImgPath, recheckExamImage, getContext());
    }


    private void initAdView(){
        Common.initAdView(adView);
    }

    private void initResultText(){
        if(rightAnswer== null || inputAnswer== null){
            //ErrorInfo
            ErrorReportUtil.report(
                    new ErrorInfo("NullPointerException", "Input or Right Answer 중 하나가 Null입니다",
                            "RandomQuestionSolutionFragment"));
            getActivity().finish();
            return;
        }

        if(inputAnswer.equals(rightAnswer)){
            //정답
            resultText.setText("정답입니다! \n입력하신 답안은 "+ inputAnswer+" 입니다.");
            resultText.setTextColor(getResources().getColor(R.color.green));
        }else{
            //오답
            resultText.setText("오답입니다! \n입력하신 답안은 "+ inputAnswer+" 이지만, 정답은 "+ rightAnswer+ " 입니다.");
            resultText.setTextColor(getResources().getColor(R.color.red));
        }
    }

    void saveToHistoryList(){
        Question q=  new Question(qn.createTitileText(), qn.createFileName(), qn.potential, inputAnswer, rightAnswer, String.valueOf(qs.t_sec), "");
        HistoryListUtil.getInstance().addToList(q);
    }

    @OnClick(R.id.continueTryBtn)
    void continueTry(){
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.examContainer, new RandomQuestionFragment()).commit();
    }
    @OnClick(R.id.addToCheckListBtn)
    void saveToCheckList(){
        Question q= new Question(qn.createTitileText(), qn.createFileName(), qn.potential, inputAnswer, rightAnswer, String.valueOf(qs.t_sec), "");
        CheckListUtil.saveQuestion(q, this);
    }

    @OnClick(R.id.changeImageBtn)
    void changeImage(){
        if(imageStatus== SOLUTION){
            changeImageBtn.setText("해설 다시 확인");
            recheckExamImage.setVisibility(View.VISIBLE);
            solutionImage.setVisibility(View.GONE);
            imageStatus= EXAM;
        }else if(imageStatus== EXAM){
            changeImageBtn.setText("문제 다시 확인");
            recheckExamImage.setVisibility(View.GONE);
            solutionImage.setVisibility(View.VISIBLE);
            imageStatus= SOLUTION;
        }
    }
    @OnClick(R.id.solutionSearch)
    void solutionSearch(){
        Toast.makeText(getContext(), "ebs 강의 검색 페이지로 이동합니다. (로그인 필요)", Toast.LENGTH_SHORT).show();

        String url = "http://www.ebsi.co.kr/ebs/xip/xipa/retrieveSCVLastExamList.ebs";
        Common.openUrl(getContext(), url);
    }

    @Override
    public void onDestroy() {
        if(solutionImage.getDrawable()!= null){
            ((BitmapDrawable)solutionImage.getDrawable()).getBitmap().recycle();
        }
        if(recheckExamImage.getDrawable()!= null){
            ((BitmapDrawable)recheckExamImage.getDrawable()).getBitmap().recycle();
        }

        unbinder.unbind();
        super.onDestroy();
    }
}
