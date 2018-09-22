package com.satisfactoryplace.gichul.gichulgenerator.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.satisfactoryplace.gichul.gichulgenerator.ExamResultList;
import com.satisfactoryplace.gichul.gichulgenerator.data.ExamNameBuilder;
import com.satisfactoryplace.gichul.gichulgenerator.data.ExamResultSaver;
import com.satisfactoryplace.gichul.gichulgenerator.server.FirebaseConnection;
import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.model.CheckList;
import com.satisfactoryplace.gichul.gichulgenerator.model.ExamResult;
import com.satisfactoryplace.gichul.gichulgenerator.model.HistoryList;
import com.satisfactoryplace.gichul.gichulgenerator.model.Question;
import com.satisfactoryplace.gichul.gichulgenerator.utils.AsyncTaskUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.Common;
import com.satisfactoryplace.gichul.gichulgenerator.utils.DialogMaker;
import com.satisfactoryplace.gichul.gichulgenerator.utils.QuestionUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by WINDOWS7 on 2018-02-28.
 */

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

    private ArrayList<Integer> inputAnswers= resultSaver.inputAnswers;

    private Bitmap examBitmap[]= new Bitmap[30];
    private Bitmap solutionBitmap[]= new Bitmap[30];

    // currentCursor is current number of exam, not "index"
    // When currentCursor is 0, that means "not selected any examNumber"
    // When currentCursor is 1~30, that means "Displayed exam number is currentCursor"
    private int currentCursor= 0;

    private final String QUESTION= "q_";
    private final String ANSWER= "a_";
    private final String POTENTIAL= "potential";
    private final String RIGHTANSWER= "answer";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_examresult, container, false);

        unbinder= ButterKnife.bind(this, rootView);
        initAdView();
        loadNeededAllData();
        return rootView;
    }

    private void loadImages(String type){
        for(int i=0; i<30; i++){
            final int _i= i;
            String path= "exam/" + enBuilder.createFileName() + "/" + type
                    + enBuilder.createFileName()+ "_"+ (i+1);
            FirebaseConnection.getInstance().loadImage(path, null, getContext(), new FirebaseConnection.ImageLoadFinished() {
                @Override
                public void success(Bitmap bitmap) {
                    examBitmap[_i]= bitmap;
                }

                @Override
                public void fail(Exception e) {
                    Toast.makeText(getContext(), "이미지 로딩 실패\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            });
        }
    }
    private void loadData(String type, final FirebaseConnection.Callback callback){
        String path= type+ "/"+ enBuilder.y+ "/"+ enBuilder.e_inst+ "/"+ enBuilder.m+ "/"+ enBuilder.e_sub;
        FirebaseConnection.getInstance().loadData(path, new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                potentials= (ArrayList<Long>)snapshot.getValue();
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

    //After loading all data, will call init()
    private void loadNeededAllData(){
        final ProgressDialog progressDialog= DialogMaker.showProgressDialog(getActivity(), "", "정답을 불러오는 중입니다.");
        loadData(POTENTIAL, new FirebaseConnection.Callback(){
            @Override
            public void success(DataSnapshot snapshot) {
                progressDialog.setMessage("정답률을 불러오는 중입니다.");
                loadData(RIGHTANSWER, new FirebaseConnection.Callback() {
                    @Override
                    public void success(DataSnapshot snapshot) {
                        progressDialog.setMessage("해설 이미지를 불러오는 중입니다.");
                        loadingCheck(progressDialog);
                        loadImages(QUESTION);
                        loadImages(ANSWER);
                    }
                    @Override
                    public void fail(String errorMessage) {
                        progressDialog.dismiss();
                    }
                });
            }
            @Override
            public void fail(String errorMessage) {
                progressDialog.dismiss();
            }
        });
    }

    private void init(){
        initResultReport();
        initSelectorBackground();
        initSelectorListener();

        if(getActivity().getIntent().getStringExtra("type")!= null && getActivity().getIntent().getStringExtra("type").equals("recheck")){
            //Case: From ExamResultListActivity
            // Not do any action
        }else{
            //Case: From ExamFragment
            saveToExamResultList();
            saveToHistoryList();
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

                examImage.setImageBitmap(examBitmap[_i]);
                solutionImage.setImageBitmap(solutionBitmap[_i]);

                int displayWidth= getActivity().getWindowManager().getDefaultDisplay().getWidth();
                Common.imageViewResize_fillDisplayWidth(displayWidth, examBitmap[_i], examImage);
                Common.imageViewResize_fillDisplayWidth(displayWidth, solutionBitmap[_i], solutionImage);

                Common.makeCanMagnify(examImage);
                Common.makeCanMagnify(solutionImage);

                refreshCheckMessage(inputAnswers.get(_i).intValue(), rightAnswers.get(_i+1).intValue(), potentials.get(_i+1).intValue());

                currentCursor= _i+1;
            });
        }
    }
    private void refreshCheckMessage(int input, int right, int potential){
        String checkMessage;
        if(input== right){
            checkMessage= "정답입니다!\n입력하신 답안은 "+input+ "입니다.\n\n";
            answerCheck.setTextColor(getResources().getColor(R.color.green));
        }else{
            checkMessage= "오답입니다!\n입력하신 답안은 "+ input+ "이지만, 정답은 "+ right+ "입니다.\n\n";
            answerCheck.setTextColor(getResources().getColor(R.color.red));
        }

        checkMessage+= "이 문제의 정답률은 "+potential+ " 입니다";
        answerCheck.setText(checkMessage);
    }

    private void initSelectorBackground(){
        for(int i=0; i<30; i++){
            if(inputAnswers.get(i).intValue()== rightAnswers.get(i+1).intValue()){
                examList.get(i).setBackground(getResources().getDrawable(R.drawable.button_border_background_green));
            }else{
                examList.get(i).setBackground(getResources().getDrawable(R.drawable.button_border_background_red));
            }
        }
    }
    private void initResultReport(){
        String titleText= enBuilder.y+ "년 "+ enBuilder.k_inst+ "\n"+ enBuilder.k_sub+ "과목 "+ enBuilder.m+ "월 시험";
        title.setText(titleText);

        int min= resultSaver.t_sec/60;
        int sec= resultSaver.t_sec%60;

        report.setText("30문제 중 "+ Common.getRightAnswerNumber(inputAnswers, rightAnswers)+ "문제를 맞췄습니다!\n\n"
                + "소요시간: "+ min+"분 "+ sec+ "초\n\n" +
                "맞힌 문제는 녹색, 틀린 문제는 적색으로 표시됩니다.");
    }
    private void initAdView(){
        MobileAds.initialize(getContext(), "ca-app-pub-5333091392909120~5084648179");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void saveToHistoryList(){
        for(int i=0; i<30; i++){
            String titleText= title.getText().toString()+ " "+ (i+1)+ "번 문제";
            String imageFileName= enBuilder.createFileName()+  "_"+ (i+1);
            String potentialText= potentials.get((i+1)).toString();
            HistoryList.getInstance().addToList(new Question(titleText, imageFileName, potentialText,
                    inputAnswers.get(i).toString(), rightAnswers.get(i+1).toString(), String.valueOf(0), ""));
        }
    }
    private void saveToExamResultList(){
        ArrayList<Long> inputAnswers_long= new ArrayList();
        for(int i=0; i<inputAnswers.size(); i++){
            inputAnswers_long.add(inputAnswers.get(i).longValue());
        }

        String titleText= title.getText().toString();
        String basicFileName= enBuilder.createFileName();
        ExamResult er= new ExamResult(titleText, basicFileName, inputAnswers_long, rightAnswers, resultSaver.t_sec);
        ExamResultList.getInstance().addToList(er);
    }

    private void loadingCheck(final ProgressDialog progressDialog){
        AsyncTaskUtil.startAsyncTask(()->{
            while(true){
                int loadEndNumber= 0;
                for(Bitmap bitmap: solutionBitmap){
                    if(bitmap!= null)
                        loadEndNumber++;
                }
                final int _loadEndNumber= loadEndNumber;

                getActivity().runOnUiThread(()->{
                    progressDialog.setMessage("이미지 로딩 중... (60개 중 "+ _loadEndNumber+ "개 완료)");
                });

                if(loadEndNumber== 60){
                    getActivity().runOnUiThread(()->{
                        progressDialog.dismiss();
                        init();
                    });
                    return;
                }

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    Toast.makeText(getContext(), "스레드 오류\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        });
    }

    @OnClick(R.id.examResult_addToCheckList)
    void addToCheckList(){
        final DialogMaker dialogMaker= new DialogMaker();
        final View childView= getActivity().getLayoutInflater().inflate(R.layout.dialog_addtochecklist, null);

        String titleText= title.getText().toString()+ " "+ currentCursor+ "번 문제";
        String qImageFileName= ExamNameBuilder.inst.createFileName()+ "_"+ currentCursor;
        String potentialText= potentials.get(currentCursor).toString();
        String inputText= inputAnswers.get(currentCursor-1).toString();
        String rightText= rightAnswers.get(currentCursor).toString();
        String memo=  ((EditText)(childView.findViewById(R.id.memoBox))).getText().toString();


        dialogMaker.setValue("", "추가", "취소", ()->{
            Question q= new Question(titleText, qImageFileName,potentialText,
                    inputText, rightText,"0", memo);
            CheckList.getInstance().addToList(q);

            Toast.makeText(getContext(), currentCursor+ "번 문제가 오답노트에 추가되었습니다", Toast.LENGTH_SHORT).show();
            dialogMaker.dismiss();
        }, null, childView);
        dialogMaker.show(getActivity().getSupportFragmentManager(), "");
    }

    @OnClick(R.id.examResult_close)
    void close(){
        getActivity().finish();
    }

    @OnClick(R.id.examResult_searchSolution)
    void searchSolution(){
        Toast.makeText(getContext(), "ebs 강의 검색 페이지로 이동합니다. (로그인 필요)", Toast.LENGTH_SHORT).show();

        String url = "http://www.ebsi.co.kr/ebs/xip/xipa/retrieveSCVLastExamList.ebs";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onDestroy() {
        allBitmapRecycle();
        unbinder.unbind();
        super.onDestroy();
    }

    private void allBitmapRecycle(){
        for(int i=0; i<30; i++){
            if(examBitmap[i]!= null && examBitmap[i].isRecycled()== false){
                examBitmap[i].recycle();
            }
        }
        for(int i=0; i<30; i++){
            if(solutionBitmap[i]!= null && solutionBitmap[i].isRecycled()== false){
                solutionBitmap[i].recycle();
            }
        }
    }
}
