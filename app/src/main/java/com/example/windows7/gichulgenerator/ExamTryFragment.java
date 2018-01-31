package com.example.windows7.gichulgenerator;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class ExamTryFragment extends Fragment{
    private String selectedSubject;
    private String selectedProb;
    private String selectedInst;
    private String selectedPeriod;

    //문제 파일 이름 규칙
    //타입_기간(년)_기간(월)_주최기관_과목_문제번호_정답률
    private String examFileName;

    private String examType;
    private String examPeriod_y;
    private String examPeriod_m;
    private String examInstitute;
    private String examSubject;
    private String examNumber;
    private String examPotential;

    private RelativeLayout loadingContainer;
    private RelativeLayout examTryContainer;

    private TextView title;
    private TextView potential;
    private ImageView questionImage;
    private EditText answer_text;
    private RadioGroup answer_radio;
    private TextView timer;

    private Thread timerThread;
    private boolean isRunningTimer= true;
    //0 is sec
    //1 is min
    final int timeSaver[]= new int[2];

    private Button regenerateExamBtn;
    private Button submitButton;

    private HashMap<String, String> potentialList= new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_examtry, container, false);

        String filter_subj= getActivity().getIntent().getStringExtra("subj");
        String filter_inst= getActivity().getIntent().getStringExtra("inst");
        String filter_peri= getActivity().getIntent().getStringExtra("peri");

        // Decide Subject
        if(filter_subj.equals("상관없음")){
            String[] subjectList= getResources().getStringArray(R.array.subjectArray);
            examSubject= subjectList[new Random().nextInt(subjectList.length)];
        } else if (filter_subj.equals("수학(이과)")) {
            examSubject= "imath";
        }else if (filter_subj.equals("수학(문과)")) {
            examSubject= "mmath";
        }else if (filter_subj.equals("국어")) {
            examSubject= "korean";
        }else if (filter_subj.equals("영어")) {
            examSubject= "english";
        }else if (filter_subj.equals("사회탐구")) {
            examSubject= "social";
        }else if (filter_subj.equals("과학탐구")) {
            examSubject= "science";
        }

        //Decide period_y
        if(filter_peri.equals("상관없음")){
            String[] yList= {"2017", "2016", "2015", "2014", "2013", "2012"};
            examPeriod_y= yList[new Random().nextInt(yList.length)];
        }else if(filter_peri.equals("최근 6년 내")){
            String[] yList= {"2017", "2016", "2015", "2014", "2013", "2012"};
            examPeriod_y= yList[new Random().nextInt(yList.length)];
        }else if(filter_peri.equals("최근 3년 내")){
            String[] yList= {"2017", "2016", "2015"};
            examPeriod_y= yList[new Random().nextInt(yList.length)];
        }else if(filter_peri.equals("2017")){
            examPeriod_y= "2017";
        }else if(filter_peri.equals("2016")){
            examPeriod_y= "2016";
        }else if(filter_peri.equals("2015")){
            examPeriod_y= "2015";
        }else if(filter_peri.equals("2014")){
            examPeriod_y= "2014";
        }else if(filter_peri.equals("2013")){
            examPeriod_y= "2013";
        }else if(filter_peri.equals("2012")){
            examPeriod_y= "2012";
        }

        //Decide institute
        if(filter_inst.equals("상관없음")){
            String instList[]= {"sunung", "pyeong", "gyoyuk"};
            examInstitute= instList[new Random().nextInt(instList.length)];
        }else if(filter_inst.equals("교육청")){
            examInstitute= "gyoyuk";
        }else if(filter_inst.equals("평가원")){
            examInstitute= "pyeong";
        }else if(filter_inst.equals("수능")){
            examInstitute= "sunung";
        }

        //Decide period_m
        if(examInstitute.equals("sunung")){
            examPeriod_m= "11";
        }else if(examInstitute.equals("gyoyuk")){
            String mList[]= {"3", "4", "7", "10"};
            examPeriod_m= mList[new Random().nextInt(mList.length)];
        }else if(examInstitute.equals("pyeong")){
            String mList[]= {"6", "9"};
            examPeriod_m= mList[new Random().nextInt(mList.length)];
        }
        loadingContainer= rootView.findViewById(R.id.examtry_loadingContainer);
        examTryContainer= rootView.findViewById(R.id.examtry_container);

        FirebaseConnection.getInstance().loadData("potential/" + examPeriod_y + "/" + examInstitute + "/" + examPeriod_m + "/" + examSubject, new FirebaseConnection.Callback() {
            @Override
            public void success(Object data) {
                ArrayList<Long> temp= (ArrayList<Long>)data;
                for(int i=1; i<temp.size(); i++){
                    potentialList.put(String.valueOf(i), String.valueOf(temp.get(i)));
                    Log.i("ValueCheck", String.valueOf(temp.get(i)));
                }
                init(rootView);

                loadingContainer.setVisibility(View.GONE);
                examTryContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void fail(String errorMessage) {
                Toast.makeText(getActivity().getApplicationContext(), "데이터베이스 통신 실패: "+ errorMessage, Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
        return rootView;
    }

    private void init(final ViewGroup rootView){
        isRunningTimer= true;

        questionImage= rootView.findViewById(R.id.question);
        selectedSubject= getActivity().getIntent().getStringExtra("subj");
        selectedProb= getActivity().getIntent().getStringExtra("prob");
        selectedInst= getActivity().getIntent().getStringExtra("inst");
        selectedPeriod= getActivity().getIntent().getStringExtra("Period");

        setQuestion();

        title= rootView.findViewById(R.id.examTitle);
        title.setText(examPeriod_y+"년 "+ examInstitute+ "\n"+ examSubject+ "과목 " +examPeriod_m+ "월 시험 "+examNumber+ "번 문제");
        potential = rootView.findViewById(R.id.examProbability);
        potential.setText("정답률 "+ examPotential + "%");

        answer_radio= rootView.findViewById(R.id.answer_radio);
        answer_radio.setVisibility(View.VISIBLE);

        answer_text= rootView.findViewById(R.id.answer_text);
        answer_text.setVisibility(View.INVISIBLE);

        timer= rootView.findViewById(R.id.timer);

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

        regenerateExamBtn= rootView.findViewById(R.id.regenerateBtn);
        regenerateExamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.examContainer, new ExamTryFragment()).commit();
            }
        });

        submitButton= rootView.findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recheckAnswer(getUserAnswer());
            }
        });
    }

    private void setQuestion(){
        //일단은 임시적으로 설정값은 고려하지 않고 한 이미지로 대체
        examFileName= generateExamFileName();
        FirebaseConnection.getInstance().loadImage(examFileName, questionImage, getContext());

        if(examSubject.equals("imath")){
            examSubject= "수학(이과)";
        }else if(examSubject.equals("mmath")){
            examSubject= "수학(문과)";
        }else if(examSubject.equals("korean")){
            examSubject= "국어";
        }else if(examSubject.equals("english")){
            examSubject= "영어";
        }else if(examSubject.equals("social")){
            examSubject= "사회탐구";
        }else if(examSubject.equals("science")){
            examSubject= "과학탐구";
        }

        if(examInstitute.equals("sunung")){
            examInstitute= "대학수학능력평가시험";
        }else if(examInstitute.equals("pyeong")){
            examInstitute= "교육과정평가원";
        }else if(examInstitute.equals("gyoyuk")){
            examInstitute= "교육청";
        }
    }

    // Return value of this method is examFileName that used to load from firebase
    // Return value is only using for first parameter of FirebaseConnection.getInstance().loadImage()
    private String generateExamFileName(){

        String fileName= "q_";

        String filter_prob= getActivity().getIntent().getStringExtra("prob");
        ArrayList<String> numberList= new ArrayList<>();
        //Decide potential
        if(filter_prob.equals("상관없음")){
            numberList.addAll(potentialList.keySet());
        }else if(filter_prob.equals("80%~ 100%")){
            for(String number: potentialList.keySet()){
                if(Integer.valueOf(potentialList.get(number))>=80){
                    numberList.add(number);
                }
            }
        }else if(filter_prob.equals("60%~ 80%")){
            for(String number: potentialList.keySet()){
                if(Integer.valueOf(potentialList.get(number))>=60 || Integer.valueOf(potentialList.get(number))<=80){
                    numberList.add(number);
                }
            }
        }else if(filter_prob.equals("40%~ 60%")){
            for(String number: potentialList.keySet()){
                if(Integer.valueOf(potentialList.get(number))>=40 || Integer.valueOf(potentialList.get(number))<=60){
                    numberList.add(number);
                }
            }
        }else if(filter_prob.equals("20%~ 40%")){
            for(String number: potentialList.keySet()){
                if(Integer.valueOf(potentialList.get(number))>=20 || Integer.valueOf(potentialList.get(number))<=40){
                    numberList.add(number);
                }
            }
        }else if(filter_prob.equals("20% 이하")){
            for(String number: potentialList.keySet()){
                if(Integer.valueOf(potentialList.get(number))<=20){
                    numberList.add(number);
                }
            }
        }

        //Decide number
        examNumber= numberList.get(new Random().nextInt(numberList.size()));
        examPotential= potentialList.get(examNumber);

        fileName+= examPeriod_y+"_"+examPeriod_m+"_"+examInstitute+"_"+examSubject+"_"+examNumber;
        return fileName;
    }

    private String getUserAnswer(){
        String result;
        if(answer_radio.getVisibility()== View.VISIBLE){
            //객관식
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
            result= result+ "번";
        }else{
            //주관식
            result= answer_text.getText().toString();
        }

        return result;
    }

    private void recheckAnswer(final String answer){
        final DialogMaker dialog= new DialogMaker();
        DialogMaker.Callback pos_callback= new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                //submit user's answer. move solution page
                isRunningTimer= false;
                submitSolution(answer);
                dialog.dismiss();
            }
        };
        DialogMaker.Callback nag_callback= new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                dialog.dismiss();
            }
        };
        dialog.setValue("선택하신 답안은 "+ answer+ "입니다.\n제출하시겠습니까?", "제출", "취소", pos_callback, nag_callback);
        dialog.show(getActivity().getSupportFragmentManager(), "AnswerSubmit");
    }

    private void submitSolution(String answer){
        if(answer.charAt(answer.length()-1)== '번'){
            answer= String.valueOf(answer.charAt(0));
        }
        getActivity().getIntent().putExtra("answer", answer);
        getActivity().getIntent().putExtra("examInfo", title.getText());
        getActivity().getIntent().putExtra("examFileName", examFileName);
        getActivity().getIntent().putExtra("sec", timeSaver[0]);
        getActivity().getIntent().putExtra("min", timeSaver[1]);
        getActivity().getIntent().putExtra("potential", examPotential);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.examContainer, new ExamSolutionFragment()).commit();
    }
}
