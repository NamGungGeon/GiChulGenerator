package com.satisfactoryplace.gichul.gichulgenerator;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
    @BindView(R.id.examResult_invisibleReason) TextView invisibleReason;

    @BindView(R.id.examResult_ad)
    AdView adView;

    private Unbinder unbinder;
    private ArrayList<Long> inputAnswers= new ArrayList<>();

    /* The index of rightAnswer is started at 1*/
    /* The 0 index is always null*/
    private ArrayList<Long> rightAnswers= new ArrayList<>();

    /* The index of potentials is started at 1*/
    /* The 0 index is always null*/
    private ArrayList<Long> potentials;

    private Bitmap examBitmap[]= new Bitmap[30];
    private Bitmap solutionBitmap[]= new Bitmap[30];

    private int runningTime= 0;

    // currentCursor is current number of exam, not "index"
    // When currentCursor is 0, that means "not selected any examNumber"
    // When currentCursor is 1~30, that means "Displayed exam number is currentCursor"
    private int currentCursor= 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_examresult, container, false);

        unbinder= ButterKnife.bind(this, rootView);
        loadNeededAllData();
        setAdView();
        return rootView;
    }

    private void setAdView(){
        MobileAds.initialize(getContext(), "ca-app-pub-5333091392909120~5084648179");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    //After loading all data, will call init()
    private void loadNeededAllData(){

        final ProgressDialog progressDialog= DialogMaker.showProgressDialog(getActivity(), "", "문제와 정답을 불러오는 중입니다.");

        //Load ExamImage
        if(getActivity().getIntent().getStringExtra("type")!= null && getActivity().getIntent().getStringExtra("type").equals("recheck")){
            //Case: From ExamResultListActivity
            for(int i=0; i<30; i++){
                final int _i= i;
                Log.i("PATH", "exam/" + getActivity().getIntent().getStringExtra("basicFileName") + "/q_" +
                        getActivity().getIntent().getStringExtra("basicFileName")+ "_"+ (i+1));
                FirebaseConnection.getInstance().loadImage("exam/" + getActivity().getIntent().getStringExtra("basicFileName") + "/q_" +
                        getActivity().getIntent().getStringExtra("basicFileName")+ "_"+ (i+1), null, getContext(), new FirebaseConnection.ImageLoadFinished() {
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
        }else{
            //Case: From ExamFragment
            Bundle bundle= getActivity().getIntent().getExtras();
            ArrayList<Bitmap> examBitmapArrayList= (ArrayList<Bitmap>)(bundle.getSerializable("examBitmaps"));
            for(int i=0; i<30; i++){
                examBitmap[i]= examBitmapArrayList.get(i);
            }
        }
        //Load Answer List
        String answerPath= "answer/"+  getActivity().getIntent().getStringExtra("period_y")+ "/"+ getActivity().getIntent().getStringExtra("encodedInstitute")
                + "/"+ getActivity().getIntent().getStringExtra("period_m")+ "/"+ getActivity().getIntent().getStringExtra("encodedSubject");
        FirebaseConnection.getInstance().loadData(answerPath, new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                rightAnswers= (ArrayList<Long>)snapshot.getValue();

                progressDialog.setMessage("정답률을 불러오는 중입니다");
                //Load Potential List
                String potentialPath= "potential/"+ getActivity().getIntent().getStringExtra("period_y")+ "/"+ getActivity().getIntent().getStringExtra("encodedInstitute")
                        + "/"+ getActivity().getIntent().getStringExtra("period_m")+ "/"+ getActivity().getIntent().getStringExtra("encodedSubject");
                FirebaseConnection.getInstance().loadData(potentialPath, new FirebaseConnection.Callback() {
                    @Override
                    public void success(DataSnapshot snapshot) {
                        potentials= (ArrayList<Long>)snapshot.getValue();

                        //Load SolutionImage
                        progressDialog.setTitle("해설 이미지를 불러오는 중입니다");
                        progressDialog.setMessage("0%");
                        loadingCheck(progressDialog);
                        String imagePath= "exam/"+ getActivity().getIntent().getStringExtra("basicFileName")+ "/a_"+ getActivity().getIntent().getStringExtra("basicFileName");
                        for(int i=0; i<30; i++){
                            final int _i= i;
                            FirebaseConnection.getInstance().loadImage(imagePath + "_"+ String.valueOf(i+1), null, getContext(), new FirebaseConnection.ImageLoadFinished() {
                                @Override
                                public void success(Bitmap bitmap) {
                                    solutionBitmap[_i]= bitmap;
                                }

                                @Override
                                public void fail(Exception e) {
                                    Toast.makeText(getContext(), "이미지를 불러올 수 없습니다\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    getActivity().finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void fail(String errorMessage) {
                        Toast.makeText(getContext(), "데이터를 불러올 수 없습니다\n"+ errorMessage, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        getActivity().finish();
                    }
                });
            }

            @Override
            public void fail(String errorMessage) {
                Toast.makeText(getContext(), "데이터를 불러올 수 없습니다\n"+ errorMessage, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                getActivity().finish();
            }
        });
    }
    private void init(){
        //Get InputAnswers
        Bundle bundle= getActivity().getIntent().getExtras();
        ArrayList<Integer> _inputAnswers= bundle.getIntegerArrayList("inputAnswers");
        for(int i=0; i<30; i++){
            inputAnswers.add(i, _inputAnswers.get(i).longValue());
        }

        runningTime= getActivity().getIntent().getIntExtra("timer", 0);

        setResultReport();
        setListSelectorBackground();
        setAllSelectorListener();

        if(getActivity().getIntent().getStringExtra("type")!= null && getActivity().getIntent().getStringExtra("type").equals("recheck")){
            //Case: From ExamResultListActivity
            // Not do any action
        }else{
            //Case: From ExamFragment
            saveToExamResultList();
            saveToHistoryList();
        }
    }

    private void saveToHistoryList(){
        for(int i=0; i<30; i++){
            HistoryList.getInstance().addToList(new Question(title.getText().toString()+ " "+ (i+1)+ "번 문제", getActivity().getIntent().getStringExtra("basicFileName")+ "_"+ (i+1), potentials.get((i+1)).toString(),
                    inputAnswers.get(i).toString(), rightAnswers.get(i+1).toString(), String.valueOf(0), ""));
        }
    }
    private void saveToExamResultList(){
        ExamResultList.getInstance().addToList(new ExamResult(title.getText().toString(), getActivity().getIntent().getStringExtra("basicFileName"), inputAnswers,
                rightAnswers, runningTime));
    }

    private void setAllSelectorListener(){
        //Set OnClickListener to Each ListSelector
        //When each listSelector is clicked, show result and question, answer image about that number.
        for(int i=0; i<30; i++){
            final int _i= i;
            examList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    report.setVisibility(View.GONE);
                    imageScroll.setVisibility(View.VISIBLE);

                    examImage.setImageBitmap(examBitmap[_i]);
                    solutionImage.setImageBitmap(solutionBitmap[_i]);

                    float magnifyScale= (float)getActivity().getWindowManager().getDefaultDisplay().getWidth()/(float)examBitmap[_i].getWidth();
                    examImage.getLayoutParams().height= (int)((float)examBitmap[_i].getHeight()* magnifyScale);
                    examImage.getLayoutParams().width= (int)((float)examBitmap[_i].getWidth()* magnifyScale);
                    examImage.requestLayout();

                    magnifyScale= (float)getActivity().getWindowManager().getDefaultDisplay().getWidth()/(float)solutionBitmap[_i].getWidth();
                    solutionImage.getLayoutParams().height= (int)((float)solutionBitmap[_i].getHeight()* magnifyScale);
                    solutionImage.getLayoutParams().width= (int)((float)solutionBitmap[_i].getWidth()* magnifyScale);
                    solutionImage.requestLayout();

                    String checkMessage= "";
                    if(inputAnswers.get(_i).intValue()== rightAnswers.get(_i+1).intValue()){
                        checkMessage+= "정답입니다!\n입력하신 답안은 "+ inputAnswers.get(_i).toString()+ "입니다.\n\n";
                        answerCheck.setTextColor(getResources().getColor(R.color.green));
                    }else{
                        checkMessage+= "오답입니다!\n입력하신 답안은 "+ inputAnswers.get(_i).toString()+ "이지만, 정답은 "+ rightAnswers.get(_i+1).toString()+ "입니다.\n\n";
                        answerCheck.setTextColor(getResources().getColor(R.color.red));
                    }

                    checkMessage+= "이 문제의 정답률은 "+ getPotentialText(potentials.get(_i+1).intValue())+ " 입니다";
                    answerCheck.setText(checkMessage);

                    currentCursor= _i+1;
                }
            });
        }
    }

    private String getPotentialText(int potential){
        String result;
        if(potential>=80){
            result= "매우높음";
        }else if(potential>=60){
            result= "높음";
        }else if(potential>= 40){
            result= "보통";
        }else if(potential>= 20){
            result= "낮음";
        }else{
            result= "매우낮음";
        }
        return result;
    }
    private void setResultReport(){
        String titleString= getActivity().getIntent().getStringExtra("title");
        title.setText(titleString);

        int min= runningTime/60;
        int sec= runningTime%60;
        report.setText("30문제 중 "+ String.valueOf(getRightAnswerNumber())+ "문제를 맞췄습니다!\n\n"+ "소요시간: "+ min+"분 "+ sec+ "초\n\n" +
                "맞힌 문제는 녹색, 틀린 문제는 적색으로 표시됩니다.");
    }

    private void setListSelectorBackground(){
        for(int i=0; i<30; i++){
            if(inputAnswers.get(i).intValue()== rightAnswers.get(i+1).intValue()){
                examList.get(i).setBackground(getResources().getDrawable(R.drawable.button_border_background_green));
            }else{
                examList.get(i).setBackground(getResources().getDrawable(R.drawable.button_border_background_red));
            }
        }
    }

    private int getRightAnswerNumber(){
        int rightNumber= 0;
        for(int i=0; i<30; i++){
            if(inputAnswers.get(i).intValue()== rightAnswers.get(i+1).intValue()){
                rightNumber++;
            }
        }
        return rightNumber;
    }


    private void loadingCheck(final ProgressDialog progressDialog){
        final Handler handler = new Handler(){
            public void handleMessage(Message msg){
                progressDialog.setMessage("이미지 로딩 중... (60개 중 "+ String.valueOf(msg.arg1)+ "개 완료)");

                if(msg.arg1>= 60){
                    init();
                }
            }
        };

        Thread loadingObserver= new Thread(){
            @Override
            public void run() {
                while(true){
                    boolean isAllLoaded= true;
                    int loadEndNumber= 0;
                    for(Bitmap bitmap: solutionBitmap){
                        if(bitmap== null){
                            isAllLoaded= false;
                        }else{
                            loadEndNumber++;
                        }
                    }
                    for(Bitmap bitmap: examBitmap){
                        if(bitmap== null){
                            isAllLoaded= false;
                        }else{
                            loadEndNumber++;
                        }
                    }

                    Message msg = handler.obtainMessage();
                    msg.arg1= loadEndNumber;
                    handler.sendMessage(msg);

                    if(isAllLoaded== true){
                        progressDialog.dismiss();
                        break;
                    }

                    try {
                        Thread.sleep(300L);
                    } catch (InterruptedException e) {
                        Toast.makeText(getContext(), "스레드 오류\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                }
            }
        };
        loadingObserver.start();
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

    @OnClick(R.id.examResult_addToCheckList)
    void addToCheckList(){
        final DialogMaker dialogMaker= new DialogMaker();
        final View childView= getActivity().getLayoutInflater().inflate(R.layout.dialog_addtochecklist, null);
        dialogMaker.setValue("", "추가", "취소", new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                CheckList.getInstance().addToList(new Question(title.getText().toString()+ " "+ currentCursor+ "번 문제", getActivity().getIntent().getStringExtra("basicFileName")+ "_"+ currentCursor, potentials.get(currentCursor).toString(),
                        inputAnswers.get(currentCursor-1).toString(), rightAnswers.get(currentCursor).toString(), String.valueOf(0), ((EditText)(childView.findViewById(R.id.memoBox))).getText().toString()));
                Toast.makeText(getContext(), currentCursor+ "번 문제가 오답노트에 추가되었습니다", Toast.LENGTH_SHORT).show();
                dialogMaker.dismiss();
            }
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
}
