package com.example.windows7.gichulgenerator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.StringTokenizer;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class ExamSolutionFragment extends Fragment {

    //해답 파일 명명 규칙
    //타입_기간(년)_기간(월)_주최기관_과목_문제번호
    private String solutionFileName= "";

    private RelativeLayout loadingContainer;
    private RelativeLayout solutionContainer;
    private TextView examInfo;
    private TextView solutionTitle;
    private ImageView solutionImage;
    private ImageView recheckExamImage;

    private Button changeImageBtn;
    private Button addToCheckListBtn;
    private Button continueTryBtn;

    private final int SOLUTION= 1234;
    private final int EXAM= 1235;
    private int imageStatus= SOLUTION;

    private String inputAnswer;
    private String rightAnswer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_examsolution, container, false);
        init(rootView);
        return rootView;
    }

    private void init(ViewGroup rootView){
        String examFileName= getActivity().getIntent().getStringExtra("examFileName");
        StringTokenizer tokenizer= new StringTokenizer(examFileName, "_", false);

        solutionFileName+= "a_";
        tokenizer.nextToken();
        //기간(년)
        solutionFileName+= tokenizer.nextToken()+ "_";
        //기간(월)
        solutionFileName+= tokenizer.nextToken()+ "_";
        //주최기관
        solutionFileName+= tokenizer.nextToken()+ "_";
        //과목
        solutionFileName+= tokenizer.nextToken()+ "_";
        //문제번호
        solutionFileName+= tokenizer.nextToken();

        examInfo= rootView.findViewById(R.id.solution_examInfo);
        examInfo.setText(getActivity().getIntent().getStringExtra("examInfo"));

        solutionImage= rootView.findViewById(R.id.solutionImage);
        FirebaseConnection.getInstance().loadImage(solutionFileName, solutionImage, getContext());

        solutionTitle= rootView.findViewById(R.id.solutionTitle);

        loadingContainer= rootView.findViewById(R.id.solutionLoadingContainer);
        solutionContainer= rootView.findViewById(R.id.solutionContainer);

        recheckExamImage= rootView.findViewById(R.id.recheck_examImage);
        FirebaseConnection.getInstance().loadImage(getActivity().getIntent().getStringExtra("examFileName"), recheckExamImage, getContext());

        changeImageBtn= rootView.findViewById(R.id.changeImageBtn);
        changeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
        addToCheckListBtn= rootView.findViewById(R.id.addToCheckListBtn);
        addToCheckListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogMaker dialog= new DialogMaker();
                final View childView= getLayoutInflater().inflate(R.layout.dialog_addtochecklist, null);
                DialogMaker.Callback pos_callback= new DialogMaker.Callback() {
                    @Override
                    public void callbackMethod() {
                        EditText memoBox= childView.findViewById(R.id.memoBox);
                        int totalTime_sec= getActivity().getIntent().getIntExtra("min", 0)*60+ getActivity().getIntent().getIntExtra("sec", 0);
                        CheckList.getInstance().addToList(new ExamInfo(getActivity().getIntent().getStringExtra("examFileName"),
                                inputAnswer, rightAnswer, String.valueOf(totalTime_sec), memoBox.getText().toString()));
                        dialog.dismiss();
                    }
                };
                DialogMaker.Callback nag_callback= new DialogMaker.Callback() {
                    @Override
                    public void callbackMethod() {
                        dialog.dismiss();
                    }
                };
                dialog.setValue("문제를 오답노트에 추가합니다.", "저장", "취소", pos_callback, nag_callback, childView);
                dialog.show(getActivity().getSupportFragmentManager(), "addToCheckList");
            }
        });
        continueTryBtn= rootView.findViewById(R.id.continueTryBtn);
        continueTryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.examContainer, new ExamTryFragment()).commit();
            }
        });

        // descript action after loading data
        FirebaseConnection.Callback callback= new FirebaseConnection.Callback() {
            @Override
            public void success(Object data) {
                inputAnswer= getActivity().getIntent().getStringExtra("answer");
                rightAnswer= (String)data;
                if(inputAnswer.equals(rightAnswer)){
                    //정답
                    solutionTitle.setText("정답입니다! \n입력하신 답안은 "+ data+" 입니다.");
                }else{
                    //오답
                    solutionTitle.setText("오답입니다! \n입력하신 답안은 "+ data+" 이지만, 정답은 "+ inputAnswer+ " 입니다.");
                }

                loadingContainer.setVisibility(View.INVISIBLE);
                solutionContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void fail(String errorMessage) {
                Toast.makeText(getContext(), "Error: "+ errorMessage, Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        };
        FirebaseConnection.getInstance().loadData("answer/2018/sunung/11/imath/7", callback);
    }
}
