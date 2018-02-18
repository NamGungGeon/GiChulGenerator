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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class ExamSolutionFragment extends Fragment implements OnBackPressedListener{
    //해답 파일 명명 규칙
    //타입_기간(년)_기간(월)_주최기관_과목_문제번호
    private String solutionFileName= "";

    @BindView(R.id.solutionLoadingContainer) RelativeLayout loadingContainer;
    @BindView(R.id.solutionContainer) RelativeLayout solutionContainer;
    @BindView(R.id.solution_examInfo) TextView examInfo;
    @BindView(R.id.solutionTitle) TextView solutionTitle;
    @BindView(R.id.solutionImage) ImageView solutionImage;
    @BindView(R.id.recheck_examImage) ImageView recheckExamImage;

    @BindView(R.id.changeImageBtn) Button changeImageBtn;
    @BindView(R.id.addToCheckListBtn) Button addToCheckListBtn;
    @BindView(R.id.continueTryBtn) Button continueTryBtn;

    private final int SOLUTION= 1234;
    private final int EXAM= 1235;
    private int imageStatus= SOLUTION;

    private String inputAnswer;
    private String rightAnswer;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_examsolution, container, false);

        unbinder= ButterKnife.bind(this, rootView);

        // descript action after loading data
        FirebaseConnection.Callback callback= new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                inputAnswer= getActivity().getIntent().getStringExtra("inputAnswer");
                rightAnswer= String.valueOf(snapshot.getValue());
                if(inputAnswer.equals(rightAnswer)){
                    //정답
                    solutionTitle.setText("정답입니다! \n입력하신 답안은 "+ inputAnswer+" 입니다.");
                    solutionTitle.setTextColor(getResources().getColor(R.color.green));
                }else{
                    //오답
                    solutionTitle.setText("오답입니다! \n입력하신 답안은 "+ inputAnswer+" 이지만, 정답은 "+ rightAnswer+ " 입니다.");
                    solutionTitle.setTextColor(getResources().getColor(R.color.red));
                }

                loadingContainer.setVisibility(View.INVISIBLE);
                solutionContainer.setVisibility(View.VISIBLE);

                saveHistory();
                init();
            }

            @Override
            public void fail(String errorMessage) {
                Toast.makeText(getContext(), "Error: "+ errorMessage, Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        };
        FirebaseConnection.getInstance().loadData(
                "answer/"+getActivity().getIntent().getStringExtra("period_y")+"/"+getActivity().getIntent().getStringExtra("institute")
                        + "/"+getActivity().getIntent().getStringExtra("period_m")+"/"+getActivity().getIntent().getStringExtra("subject")
                        + "/"+ getActivity().getIntent().getStringExtra("examNumber"), callback);

        return rootView;
    }

    private void init(){
        setSolutionFileName();

        examInfo.setText(getActivity().getIntent().getStringExtra("examInfo"));

        String basicPath= getActivity().getIntent().getStringExtra("period_y")+ "_"+ getActivity().getIntent().getStringExtra("period_m")+ "_"+
                getActivity().getIntent().getStringExtra("institute")+ "_"+ getActivity().getIntent().getStringExtra("subject");

        FirebaseConnection.getInstance().loadImage(basicPath+ "/"+ solutionFileName, solutionImage, getContext());
        FirebaseConnection.getInstance().loadImage(basicPath+ "/"+ getActivity().getIntent().getStringExtra("examFileName"), recheckExamImage, getContext());
    }

    private void setSolutionFileName(){
        String examFileName= getActivity().getIntent().getStringExtra("examFileName");
        StringTokenizer tokenizer= new StringTokenizer(examFileName, "_", false);

        solutionFileName+= "a_";
        //"q_"
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
    }

    private void saveHistory(){
        int totalTime_sec= getActivity().getIntent().getIntExtra("min", 0)*60+ getActivity().getIntent().getIntExtra("sec", 0);
        String basicFileName= getActivity().getIntent().getStringExtra("examFileName");
        basicFileName= basicFileName.substring(2);
        HistoryList.getInstance().addToList(new Exam(getActivity().getIntent().getStringExtra("examInfo"), basicFileName,
                getActivity().getIntent().getStringExtra("potential"), inputAnswer, rightAnswer, String.valueOf(totalTime_sec), ""));
    }

    @OnClick(R.id.continueTryBtn)
    void continueTry(){
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.examContainer, new ExamTryFragment()).commit();
    }


    @OnClick(R.id.addToCheckListBtn)
    void saveCheckList(){
        final DialogMaker dialog= new DialogMaker();
        final View childView= getLayoutInflater().inflate(R.layout.dialog_addtochecklist, null);
        DialogMaker.Callback pos_callback= new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                EditText memoBox= childView.findViewById(R.id.memoBox);
                int totalTime_sec= getActivity().getIntent().getIntExtra("min", 0)*60+ getActivity().getIntent().getIntExtra("sec", 0);
                CheckList.getInstance()
                        .addToList(new Exam(getActivity().getIntent().getStringExtra("examInfo"), getActivity().getIntent().getStringExtra("examFileName").substring(2),
                                getActivity().getIntent().getStringExtra("potential"),inputAnswer, rightAnswer, String.valueOf(totalTime_sec), memoBox.getText().toString()));
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

    @Override
    public boolean onBackPressed() {
        unbinder.unbind();
        return true;
    }
}
