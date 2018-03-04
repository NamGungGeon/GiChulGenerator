package com.example.windows7.gichulgenerator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

    private String inputAnswer;
    private String rightAnswer;

    private Unbinder unbinder;

    private final int SOLUTION= 1234;
    private final int EXAM= 1235;
    private int imageStatus= SOLUTION;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_searchresult_solution, container, false);
        unbinder= ButterKnife.bind(this, rootView);

        final Intent intent= getActivity().getIntent();
        String answerPath= "answer/" + intent.getStringExtra("period_y") + "/" +intent.getStringExtra("institute")+ "/"+ intent.getStringExtra("period_m") + "/" +intent.getStringExtra("subject") + "/" + intent.getStringExtra("number");
        FirebaseConnection.getInstance().loadData(answerPath, new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                rightAnswer= snapshot.getValue().toString();
                inputAnswer= intent.getStringExtra("inputAnswer");

                init();
            }

            @Override
            public void fail(String errorMessage) {
                Toast.makeText(getActivity().getApplicationContext(), "데이터베이스 통신 실패: "+ errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void init(){
        Intent intent= getActivity().getIntent();

        title.setText(intent.getStringExtra("title"));

        String solutionPath= intent.getStringExtra("basicFileName")+"/"+ "a_"+ intent.getStringExtra("basicFileName")+ "_"+ intent.getStringExtra("number");
        String questionPath= intent.getStringExtra("basicFileName")+"/"+ "q_"+ intent.getStringExtra("basicFileName")+ "_"+ intent.getStringExtra("number");

        Log.i("Solution Image Path", solutionPath);
        FirebaseConnection.getInstance().loadImage("exam/" + solutionPath, solution, getActivity().getApplicationContext(), new FirebaseConnection.ImageLoadFinished() {
            @Override
            public void success(Bitmap bitmap) {
                loadingContainer.setVisibility(View.GONE);
                solutionContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void fail(Exception e) {
                Toast.makeText(getContext(), "이미지를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
        FirebaseConnection.getInstance().loadImage("exam/"+ questionPath, question, getActivity().getApplicationContext());

        checkAnswer();
        saveHistory();
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
        int totalTime_sec= getActivity().getIntent().getIntExtra("min", 0)*60+ getActivity().getIntent().getIntExtra("sec", 0);
        String basicFileName= getActivity().getIntent().getStringExtra("basicFileName");

        HistoryList.getInstance().addToList(
                new Question(getActivity().getIntent().getStringExtra("title"), basicFileName+ "_"+ getActivity().getIntent().getStringExtra("number"),
                getActivity().getIntent().getStringExtra("potential"), inputAnswer, rightAnswer, String.valueOf(totalTime_sec), ""));
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
        final DialogMaker dialog= new DialogMaker();
        final View childView= getLayoutInflater().inflate(R.layout.dialog_addtochecklist, null);

        dialog.setValue("문제를 오답노트에 추가합니다.", "저장", "취소", new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                EditText memoBox= childView.findViewById(R.id.memoBox);
                int totalTime_sec= getActivity().getIntent().getIntExtra("min", 0)*60+ getActivity().getIntent().getIntExtra("sec", 0);
                CheckList.getInstance()
                        .addToList(new Question(getActivity().getIntent().getStringExtra("title"), getActivity().getIntent().getStringExtra("basicFileName")+ "_"+ getActivity().getIntent().getStringExtra("number"),
                                getActivity().getIntent().getStringExtra("potential"), inputAnswer, rightAnswer, String.valueOf(totalTime_sec), memoBox.getText().toString()));
                dialog.dismiss();
                Toast.makeText(getContext(), "오답노트에 저장되었습니다", Toast.LENGTH_SHORT).show();
            }
        }, null, childView);
        dialog.show(getActivity().getSupportFragmentManager(), "addToCheckList");
    }

    @OnClick(R.id.searchResult_searchSolution)
    void searchSolution(){
        Toast.makeText(getContext(), "ebs 강의 검색 페이지로 이동합니다. (로그인 필요)", Toast.LENGTH_SHORT).show();

        Intent intent= new Intent(getContext(), WebViewActivity.class);
        intent.putExtra("title", title.getText());
        intent.putExtra("url", "http://www.ebsi.co.kr/ebs/xip/xipa/retrieveSCVLastExamList.ebs");
        startActivity(intent);
    }
    @Override
    public void onDestroy() {
        if(question.getDrawable()!= null){
            ((BitmapDrawable)question.getDrawable()).getBitmap().recycle();
        }
        if(question.getDrawable()!= null){
            ((BitmapDrawable)question.getDrawable()).getBitmap().recycle();
        }

        unbinder.unbind();
        super.onDestroy();
    }

}