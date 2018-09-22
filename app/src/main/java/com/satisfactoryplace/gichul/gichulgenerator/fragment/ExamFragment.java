package com.satisfactoryplace.gichul.gichulgenerator.fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.satisfactoryplace.gichul.gichulgenerator.data.ExamNameBuilder;
import com.satisfactoryplace.gichul.gichulgenerator.data.ExamResultSaver;
import com.satisfactoryplace.gichul.gichulgenerator.server.FirebaseConnection;
import com.satisfactoryplace.gichul.gichulgenerator.model.OnBackPressedListener;
import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.utils.AnswerChecker;
import com.satisfactoryplace.gichul.gichulgenerator.utils.AsyncTaskUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.Common;
import com.satisfactoryplace.gichul.gichulgenerator.utils.DialogMaker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ExamFragment extends Fragment implements OnBackPressedListener {
    @BindViews({R.id.exam_1,R.id.exam_2,R.id.exam_3,R.id.exam_4,R.id.exam_5,R.id.exam_6,R.id.exam_7,R.id.exam_8,R.id.exam_9,R.id.exam_10,
            R.id.exam_11,R.id.exam_12,R.id.exam_13,R.id.exam_14,R.id.exam_15,R.id.exam_16,R.id.exam_17,R.id.exam_18,R.id.exam_19,R.id.exam_20,
            R.id.exam_21,R.id.exam_22,R.id.exam_23,R.id.exam_24,R.id.exam_25,R.id.exam_26,R.id.exam_27,R.id.exam_28,R.id.exam_29,R.id.exam_30})
    List<TextView> listSelector;

    @BindView(R.id.exam_startMessage)TextView startMessage;
    @BindView(R.id.exam_title)TextView title;
    @BindView(R.id.exam_examImage)ImageView examImage;
    @BindView(R.id.exam_inputAnswer)EditText inputAnswer;
    @BindView(R.id.exam_saveAnswer)Button saveAnswerBtn;
    @BindView(R.id.exam_timer)Button timer;

    @BindView(R.id.exam_ad)
    AdView adView;

    private final Bitmap examBitmap[]= new Bitmap[30];

    private ExamNameBuilder enBuilder= ExamNameBuilder.inst;

    private int currentCursor= 0;
    private int[] answers= new int[30];
    private int sec= 0;
    private boolean isRunningTimer= true;

    private Unbinder unbinder;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_examtry, container, false);
        unbinder= ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    private void init(){
        inputAnswer.setHint("정답을 입력하세요\n(객관식의 경우 번호만)");
        startMessage.setText("시험을 시작합니다!\n아래에서 문제 번호를 선택해서 시작하세요.\n\n\n\n\n" +
                "답안을 작성한 다음\n답안 저장 버튼을 클릭하거나\n다른 문제 번호를 터치하면 자동으로 답안이 저장됩니다.\n\n\n\n\n" +
                "문제를 모두 풀었다면\n제출 버튼을 눌러 결과를 확인해보세요!");
        saveAnswerBtn.setText("답안 저장");

        initTitle();
        loadExamImage();
        initAnswerList();
        initListSelector();
        initAdView();
    }

    private void startTimer(){
        AsyncTaskUtil.startAsyncTask(()->{
            sec= -1;
            while(isRunningTimer){
                sec++;
                getActivity().runOnUiThread(()->refreshTimer(sec));

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    Toast.makeText(getContext(), "스레드 오류\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        });
    }
    private void refreshTimer(int sec){
        int min= sec/60;
        sec= sec%60;
        timer.setText(String.valueOf(min)+ "분 "+ String.valueOf(sec%60)+ "초");
    }

    private void stopTimer(){
        isRunningTimer= false;
    }

    private void initTitle(){
        String titleString= enBuilder.y+ "년 "+ enBuilder.k_inst+ "\n"+ enBuilder.k_sub+ "과목 "+ enBuilder.m+ "월 시험";
        title.setText(titleString);
    }

    private void initAdView(){
        MobileAds.initialize(getContext(), "ca-app-pub-5333091392909120~5084648179");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
    private void loadExamImage(){
        for(int i=0; i<examBitmap.length; i++){
            examBitmap[i]= null;
        }

        final ProgressDialog progressDialog= DialogMaker.showProgressDialog(getActivity(), "시험지를 가져오는 중입니다...", "");
        for(int i=0; i<30; i++){
            final int _i= i;
            String targetImageName= "exam/"+ enBuilder.createFileName()+ "/"
                    + enBuilder.createFileName()+ String.valueOf(_i+1);
            FirebaseConnection.getInstance().loadImage(targetImageName, null,
                    getContext(), new FirebaseConnection.ImageLoadFinished() {
                        @Override
                        public void success(Bitmap bitmap) {
                            examBitmap[_i]= bitmap;
                        }

                        @Override
                        public void fail(Exception e) {
                            Toast.makeText(getContext(), "이미지를 가져올 수 없습니다.\n"+ e.getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            getActivity().finish();
                        }
                    });
        }

        loadingCheck(progressDialog);
    }
    private void loadingCheck(final ProgressDialog progressDialog){
        AsyncTaskUtil.startAsyncTask(()->{
            while(true){
                int progress= 0;
                for(Bitmap bitmap: examBitmap){
                    if(bitmap!= null){
                        progress++;
                    }
                }
                final int _progress= progress;

                getActivity().runOnUiThread(()->progressDialog.setMessage("이미지 30개 중 "+ _progress+ "개 다운로드 완료"));

                if(progress== 30){
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
                    progressDialog.dismiss();
                    getActivity().finish();
                }
            }
        });
    }

    private void initAnswerList(){
        for(int i=0; i<30; i++){
            answers[i]= -1;
        }
}
    private void initListSelector(){
        for(int i=0; i< listSelector.size(); i++){
            final int _i= i;
            listSelector.get(i).setOnClickListener((View v)-> {
                    startMessage.setVisibility(View.GONE);
                    examImage.setVisibility(View.VISIBLE);

                    PhotoViewAttacher attacher= new PhotoViewAttacher(examImage);
                    inputAnswer.setVisibility(View.VISIBLE);
                    saveAnswerBtn.setVisibility(View.VISIBLE);

                    if(currentCursor!= 0){
                        saveAnswer(inputAnswer.getText().toString());
                    }else{
                        // timer is started when user click exam firstly.
                        startTimer();
                    }

                    setExamAnswer(_i);
                    setExamImage(_i);
                });
        }
    }
    private void flushInputAnswer(){
        inputAnswer.setText("");
    }
    private void setExamAnswer(int _i){
        if(answers[_i]== -1){
            flushInputAnswer();
        }else{
            inputAnswer.setText(String.valueOf(answers[_i]));
        }
        currentCursor= _i+1;
    }

    private void setExamImage(int _i){
        examImage.setImageBitmap(examBitmap[_i]);

        float magnifyScale= (float)getActivity().getWindowManager().getDefaultDisplay().getWidth()/(float)examBitmap[_i].getWidth();
        examImage.getLayoutParams().height= (int)((float)examBitmap[_i].getHeight()* magnifyScale);
        examImage.getLayoutParams().width= (int)((float)examBitmap[_i].getWidth()* magnifyScale);
        examImage.requestLayout();

        PhotoViewAttacher attacher= new PhotoViewAttacher(examImage);
        attacher.update();
    }

    private void submit(){
        stopTimer();

        //Convert inputAnswers to ArrayList type
        ArrayList<Integer> inputAnswerArray= new ArrayList<>();
        for(int i=0; i<answers.length; i++)
            inputAnswerArray.add(answers[i]);

        ExamResultSaver.inst= new ExamResultSaver(inputAnswerArray, sec);

        //change fragment to resultFragment
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.examActivity_container, new ExamResultFragment()).commit();
        recycleAllBitmaps();
    }

    private void refreshListSelector(){
        for(int i=0; i<answers.length; i++){
            if(answers[i]!= -1){
                listSelector.get(i).setBackground(getResources().getDrawable(R.drawable.button_border_background_blue, null));
            }
        }
    }
    private void saveAnswer(String input){
        if(!input.equals("")){
            //input is not empty
            String answerString= inputAnswer.getText().toString();
            //Check input is valid
            if(!AnswerChecker.isValidAnswer(answerString))
                return;

            answers[currentCursor-1]= Integer.valueOf(input);
            refreshListSelector();
            Toast.makeText(getContext(), currentCursor+ "번 문제의 입력한 답안이 "+ input+ "(으)로 저장되었습니다", Toast.LENGTH_LONG).show();
        }
    }
    private void recycleAllBitmaps(){
        for(int i=0; i< examBitmap.length; i++){
            if(examBitmap[i]!= null && !examBitmap[i].isRecycled()){
                examBitmap[i].recycle();
            }
        }
    }
    private boolean isAnswerAllChecked(){
        for(int answer: answers){
            if(answer== -1){
                return false;
            }
        }
        return true;
    }

    @OnClick(R.id.exam_submit)
    void clickedSubmitBtn(){
        String message;
        if(!isAnswerAllChecked()){
            message= "아직 입력하지 않은 답안이 있습니다.\n" +
                    "그래도 답안을 제출하시겠습니까?";
        }else{
            message= "답안을 제출하시겠습니까?\n" +
                    "(다시 한 번 검토해보세요)";
        }

        final DialogMaker dialogMaker=  new DialogMaker();
        dialogMaker.setValue(message, "예", "아니오",
                ()-> {
                    dialogMaker.dismiss();
                    submit();
                }, null);
        dialogMaker.show(getActivity().getSupportFragmentManager(), "Exam Submit?");
    }
    @OnClick(R.id.exam_saveAnswer)
    void clickSaveBtn(){
        saveAnswer(inputAnswer.getText().toString());
    }

    @Override
    public boolean onBackPressed() {
        final DialogMaker dialog= new DialogMaker();
        dialog.setValue("정말 시험을 그만두고 나가시겠습니까?", "예", "아니오", new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                stopTimer();
                dialog.dismiss();
                getActivity().finish();
            }
        }, null);
        dialog.show(getActivity().getSupportFragmentManager(), "Check really want to exit");
        return false;
    }
    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
