package com.satisfactoryplace.gichul.gichulgenerator.fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import com.google.android.gms.ads.AdView;
import com.satisfactoryplace.gichul.gichulgenerator.data.ExamNameBuilder;
import com.satisfactoryplace.gichul.gichulgenerator.data.ExamResultSaver;
import com.satisfactoryplace.gichul.gichulgenerator.model.ErrorInfo;
import com.satisfactoryplace.gichul.gichulgenerator.server.FirebaseConnection;
import com.satisfactoryplace.gichul.gichulgenerator.model.OnBackPressedListener;
import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.utils.AnswerUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.BitmapManager;
import com.satisfactoryplace.gichul.gichulgenerator.utils.Common;
import com.satisfactoryplace.gichul.gichulgenerator.utils.DialogMaker;
import com.satisfactoryplace.gichul.gichulgenerator.utils.ErrorReportUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.ProgressUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.TimerUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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

    @BindView(R.id.exam_ad) AdView adView;

    private BitmapManager bManager= new BitmapManager();

    private ExamNameBuilder enBuilder= ExamNameBuilder.inst;

    private int currentCursor= -1;
    private int[] answers= new int[30];

    private Unbinder unbinder;

    private TimerUtil timerUtil;

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
        initAnswerList();
        initListSelector();
        initAdView();
        loadExamImage();
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

    private void initTitle(){
        String titleString= enBuilder.y+ "년 "+ enBuilder.k_inst+ "\n"+ enBuilder.k_sub+ "과목 "+ enBuilder.m+ "월 시험";
        title.setText(titleString);
    }

    private void initAdView(){
        Common.initAdView(adView);
    }
    private void loadExamImage(){
        final ProgressDialog progressDialog= DialogMaker.showProgressDialog(getActivity(), "시험지를 가져오는 중입니다...", "");
        loadingCheck(progressDialog);
        for(int i=0; i<30; i++){
            final int _i= i;
            String targetImageName= enBuilder.createImagePath(ExamNameBuilder.TYPE_Q, String.valueOf(i+1));
            FirebaseConnection.getInstance().loadImage(targetImageName, null, getContext()
                    , new FirebaseConnection.ImageLoadFinished() {
                        @Override
                        public void success(Bitmap bitmap) {
                            bManager.addBitmap(bitmap, _i);
                        }
                        @Override
                        public void fail(Exception e) {
                            Toast.makeText(getContext(), "이미지를 가져올 수 없습니다.\n"+ e.getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            getActivity().finish();
                        }
                    });
        }

    }
    private void loadingCheck(final ProgressDialog progressDialog){
        ProgressUtil.loadingCheck(progressDialog, 30, ()->{
            int finishNumber= bManager.getBitmaps().size();
            getActivity().runOnUiThread(()->{
                progressDialog.setMessage("이미지 30개 중 "+ finishNumber+ "개 로딩 완료");
            });
            return finishNumber;
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

                    ViewUtil.makeCanMagnify(examImage);
                    inputAnswer.setVisibility(View.VISIBLE);
                    saveAnswerBtn.setVisibility(View.VISIBLE);

                    if(currentCursor!= -1){
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

    /**
     * Set Exam answer to inputBox text
     * @param idx: idx is index. not question number.
     */
    private void setExamAnswer(int idx){
        if(answers[idx]== -1)
            flushInputAnswer();
        else
            inputAnswer.setText(String.valueOf(answers[idx]));

        currentCursor= idx;
    }

    /**
     * Set Exam answer to inputBox text
     * @param idx: idx is index. not question number.
     */
    private void setExamImage(int idx){
        examImage.setImageBitmap(bManager.getBitmap(idx));
        int displayWidth= getActivity().getWindowManager().getDefaultDisplay().getWidth();
        ViewUtil.imageViewResize_fillDisplayWidth(displayWidth, bManager.getBitmap(idx), examImage);
        ViewUtil.makeCanMagnify(examImage);
    }

    private void submit(){
        if(timerUtil!= null){
            int sec= timerUtil.stopTimer();

            //Convert inputAnswers to ArrayList type
            ArrayList<Integer> inputAnswerArray= new ArrayList<>();
            for(int i=0; i<answers.length; i++)
                inputAnswerArray.add(answers[i]);

            ExamResultSaver.inst= new ExamResultSaver(inputAnswerArray, sec);

            //change fragment to resultFragment
            getActivity().getIntent().putExtra("resultType", "examTry");
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.examActivity_container, new ExamResultFragment()).commit();
        }else{
            Toast.makeText(getContext(), "아직 첫 문제에 시도하지 않으셨습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshListSelector(){
        for(int i=0; i<answers.length; i++){
            if(answers[i]!= -1){
                listSelector.get(i).setBackground(getResources().getDrawable(R.drawable.button_border_background_blue, null));
            }
        }
    }

    private void saveAnswer(String input){
        //Check input is not empty
        if(!input.equals("")){
            String answerString= inputAnswer.getText().toString();
            //Check input is valid
            if(!AnswerUtil.isValidAnswer(answerString))
                return;

            answers[currentCursor]= Integer.valueOf(input);
            refreshListSelector();
            Toast.makeText(getContext(), (currentCursor+1)+ "번 문제의 입력한 답안이 "+ input+ "(으)로 저장되었습니다", Toast.LENGTH_LONG).show();
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
                    submit();
                    dialogMaker.dismiss();
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
        dialog.setValue("정말 시험을 그만두고 나가시겠습니까?", "예", "아니오", () -> {
            if(timerUtil!= null){
                timerUtil.stopTimer();
            }
            dialog.dismiss();
            getActivity().finish();
        }, null);
        dialog.show(getActivity().getSupportFragmentManager(), "Check really want to exit");
        return false;
    }
    @Override
    public void onDestroy() {
        unbinder.unbind();
        bManager.recycleAllBitmaps();
        super.onDestroy();
    }
}
