package com.satisfactoryplace.gichul.gichulgenerator.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.satisfactoryplace.gichul.gichulgenerator.utils.ExamResultListUtil;
import com.satisfactoryplace.gichul.gichulgenerator.data.ExamNameBuilder;
import com.satisfactoryplace.gichul.gichulgenerator.data.ExamResultSaver;
import com.satisfactoryplace.gichul.gichulgenerator.server.FirebaseConnection;
import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.utils.CheckListUtil;
import com.satisfactoryplace.gichul.gichulgenerator.model.ExamResult;
import com.satisfactoryplace.gichul.gichulgenerator.model.Question;
import com.satisfactoryplace.gichul.gichulgenerator.utils.AnswerUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.BitmapManager;
import com.satisfactoryplace.gichul.gichulgenerator.utils.Common;
import com.satisfactoryplace.gichul.gichulgenerator.utils.DialogMaker;
import com.satisfactoryplace.gichul.gichulgenerator.utils.HistoryListUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.ProgressUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ExamResultFragment extends Fragment {
    @BindView(R.id.examResult_title)
    TextView title;

    @BindViews({R.id.examResult_1, R.id.examResult_2, R.id.examResult_3, R.id.examResult_4, R.id.examResult_5, R.id.examResult_6, R.id.examResult_7,
            R.id.examResult_8, R.id.examResult_9, R.id.examResult_10, R.id.examResult_11, R.id.examResult_12, R.id.examResult_13, R.id.examResult_14,
            R.id.examResult_15, R.id.examResult_16, R.id.examResult_17, R.id.examResult_18, R.id.examResult_19, R.id.examResult_20, R.id.examResult_21,
            R.id.examResult_22, R.id.examResult_23, R.id.examResult_24, R.id.examResult_25, R.id.examResult_26, R.id.examResult_27, R.id.examResult_28,
            R.id.examResult_29, R.id.examResult_30})
    List<TextView> examList;

    @BindView(R.id.examResult_imageScroll)ScrollView imageScroll;
    @BindView(R.id.examResult_answerCheck)TextView answerCheck;
    @BindView(R.id.examResult_exam)ImageView examImage;
    @BindView(R.id.examResult_solution)ImageView solutionImage;
    @BindView(R.id.examResult_report)TextView report;

    @BindView(R.id.examResult_ad) AdView adView;

    private ExamResultSaver resultSaver= ExamResultSaver.inst;
    private ExamNameBuilder enBuilder= ExamNameBuilder.inst;

    private Unbinder unbinder;

    /* The index of rightAnswer is started at 1*/
    /* The 0 index is always null*/
    private ArrayList<Long> rightAnswers= new ArrayList<>();

    /* The index of potentials is started at 1*/
    /* The 0 index is always null*/
    private ArrayList<Long> potentials;

    private BitmapManager examBitMapManager= new BitmapManager();
    private BitmapManager solutionBitMapManager= new BitmapManager();

    //Current Question's index.
    //If current Quesition number is 1, cursor is 0.
    //-1 means user is still not start exam.
    private int currentCursor= -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_examresult, container, false);

        unbinder= ButterKnife.bind(this, rootView);
        initAdView();
        loadNeededAllData();

        return rootView;
    }

    //After loading all data, will call init()
    private void loadNeededAllData(){
        final ProgressDialog progressDialog= DialogMaker.showProgressDialog(getActivity(), "정답률을 불러오는 중입니다....", "로딩 중");
        loadData(ExamNameBuilder.TYPE_POTENTIAL, new FirebaseConnection.Callback(){
            @Override
            public void success(DataSnapshot snapshot) {
                potentials= (ArrayList<Long>)snapshot.getValue();
                progressDialog.setTitle("정답을 불러오는 중입니다....");
                loadData(ExamNameBuilder.TYPE_ANSWER, new FirebaseConnection.Callback() {
                    @Override
                    public void success(DataSnapshot snapshot) {
                        rightAnswers= (ArrayList<Long>)snapshot.getValue();
                        progressDialog.setTitle("해설 이미지를 불러오는 중입니다....");
                        loadingCheck(progressDialog);
                        loadImages(ExamNameBuilder.TYPE_Q);
                        loadImages(ExamNameBuilder.TYPE_A);
                    }
                    @Override
                    public void fail(String errorMessage) {
                        progressDialog.dismiss();
                        getActivity().finish();
                    }
                });
            }
            @Override
            public void fail(String errorMessage) {
                progressDialog.dismiss();
                getActivity().finish();
            }
        });
    }

    private void loadImages(int type){
        for(int i=0; i<30; i++){
            final int _i= i;
            String path= enBuilder.createImagePath(type, String.valueOf(i+1));
            FirebaseConnection.getInstance().loadImage(path, null, getContext(), new FirebaseConnection.ImageLoadFinished() {
                @Override
                public void success(Bitmap bitmap) {
                    switch (type){
                        case ExamNameBuilder.TYPE_Q:
                            examBitMapManager.addBitmap(bitmap, _i);
                            break;
                        case ExamNameBuilder.TYPE_A:
                            solutionBitMapManager.addBitmap(bitmap, _i);
                            break;
                    }
                }
                @Override
                public void fail(Exception e) {
                    Toast.makeText(getContext(), "이미지 로딩 실패\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            });
        }
    }
    private void loadData(int type, final FirebaseConnection.Callback callback){
        String path= enBuilder.createPath(type);
        FirebaseConnection.getInstance().loadData(path, new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                switch (type){
                    case ExamNameBuilder.TYPE_POTENTIAL:
                        potentials= (ArrayList<Long>)snapshot.getValue();
                        break;
                    case ExamNameBuilder.TYPE_ANSWER:
                        rightAnswers= (ArrayList<Long>)snapshot.getValue();
                        break;
                }
                if(callback!= null){
                    callback.success(snapshot);
                }
            }

            @Override
            public void fail(String errorMessage) {
                if(callback!= null){
                    callback.fail(errorMessage);
                }
                Toast.makeText(getContext(), "데이터를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }

    private void init(){
        initResultReport();
        initSelectorBackground();
        initSelectorListener();
        if(getActivity().getIntent().getStringExtra("resultType").equals("examTry")){
            saveToExamResultList();
            saveToHistoryList();
        }
    }
    private void saveToHistoryList(){
        for(int i=0; i<30; i++){
            Question q= new Question(enBuilder.createTitleText()+ (i+1)+ "번 문제", enBuilder.createFileName()+ "_"+ (i+1), potentials.get((i+1)).toString(),
                    resultSaver.inputAnswers.get(i).toString(), rightAnswers.get(i+1).toString(), String.valueOf(0), "");
            HistoryListUtil.getInstance().addToList(q);
        }
    }
    private void initSelectorListener(){
        //Set OnClickListener to Each ListSelector
        //When each listSelector is clicked, show result and question, answer image about that number.
        for(int i=0; i<30; i++){
            final int _i= i;
            examList.get(i).setOnClickListener((View v)->{
                report.setVisibility(View.GONE);
                imageScroll.setVisibility(View.VISIBLE);

                examImage.setImageBitmap(examBitMapManager.getBitmap(_i));
                solutionImage.setImageBitmap(solutionBitMapManager.getBitmap(_i));

                int displayWidth= getActivity().getWindowManager().getDefaultDisplay().getWidth();
                ViewUtil.imageViewResize_fillDisplayWidth(displayWidth, examBitMapManager.getBitmap(_i), examImage);
                ViewUtil.imageViewResize_fillDisplayWidth(displayWidth, solutionBitMapManager.getBitmap(_i), solutionImage);
                ViewUtil.makeCanMagnify(examImage);
                ViewUtil.makeCanMagnify(solutionImage);

                refreshCheckMessage(resultSaver.inputAnswers.get(_i).intValue(), rightAnswers.get(_i+1).intValue(), potentials.get(_i+1).intValue());
                currentCursor= _i;
            });
        }
    }
    private void refreshCheckMessage(int input, int right, int potential){
        AnswerUtil.answerCheck(answerCheck, getResources(), input, right, potential);
    }

    private void initSelectorBackground(){
        for(int i=0; i<30; i++){
            if(resultSaver.inputAnswers.get(i).intValue()== rightAnswers.get(i+1).intValue()){
                examList.get(i).setBackground(getResources().getDrawable(R.drawable.button_border_background_green));
            }else{
                examList.get(i).setBackground(getResources().getDrawable(R.drawable.button_border_background_red));
            }
        }
    }

    private void initResultReport(){
        String titleText= enBuilder.createTitleText();
        title.setText(titleText);

        int min= resultSaver.t_sec/60;
        int sec= resultSaver.t_sec%60;

        report.setText("30문제 중 "+ AnswerUtil.getRightAnswerNumber(resultSaver.inputAnswers, rightAnswers)+ "문제를 맞췄습니다!\n\n"
                + "소요시간: "+ min+"분 "+ sec+ "초\n\n" +
                "맞힌 문제는 녹색, 틀린 문제는 적색으로 표시됩니다.");
    }

    private void initAdView(){
        Common.initAdView(adView);
    }

    private void saveToExamResultList(){
        ArrayList<Long> inputAnswers_long= new ArrayList();
        for(int i=0; i<resultSaver.inputAnswers.size(); i++){
            inputAnswers_long.add(resultSaver.inputAnswers.get(i).longValue());
        }

        String titleText= title.getText().toString();
        String basicFileName= enBuilder.createFileName();
        ExamResult er= new ExamResult(titleText, basicFileName, inputAnswers_long, rightAnswers, resultSaver.t_sec);
        ExamResultListUtil.getInstance().addToList(er);
    }

    private void loadingCheck(final ProgressDialog progressDialog){
        ProgressUtil.loadingCheck(progressDialog, 60, ()->{
            int finishNumber= examBitMapManager.getBitmaps().size()+ solutionBitMapManager.getBitmaps().size();
            getActivity().runOnUiThread(()->{
                progressDialog.setMessage("이미지 60개 중 "+ finishNumber+ "개 로딩 완료");
            });
            return finishNumber;
        }, ()->{
            getActivity().runOnUiThread(()->{
                init();
            });
        });
    }

    @OnClick(R.id.examResult_addToCheckList)
    void addToCheckList(){
        String titleText= title.getText().toString()+ " "+ (currentCursor+1)+ "번 문제";
        String qImageFileName= ExamNameBuilder.inst.createFileName()+ "_"+ (currentCursor+1);
        String potentialText= potentials.get(currentCursor+1).toString();
        String inputText= resultSaver.inputAnswers.get(currentCursor).toString();
        String rightText= rightAnswers.get(currentCursor+1).toString();

        Question q= new Question(titleText, qImageFileName, potentialText,
                inputText, rightText,"0", "");
        CheckListUtil.saveQuestion(q, this);
    }

    @OnClick(R.id.examResult_close)
    void close(){
        getActivity().finish();
    }

    @OnClick(R.id.examResult_searchSolution)
    void searchSolution(){
        Toast.makeText(getContext(), "ebs 강의 검색 페이지로 이동합니다. (로그인 필요)", Toast.LENGTH_SHORT).show();

        String url = "http://www.ebsi.co.kr/ebs/xip/xipa/retrieveSCVLastExamList.ebs";
        Common.openUrl(getContext(), url);
    }

    @Override
    public void onDestroy() {
        allBitmapRecycle();
        unbinder.unbind();
        super.onDestroy();
    }

    private void allBitmapRecycle(){
        examBitMapManager.recycleAllBitmaps();
        solutionBitMapManager.recycleAllBitmaps();
    }
}
