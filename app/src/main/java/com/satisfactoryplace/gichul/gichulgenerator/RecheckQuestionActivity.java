package com.satisfactoryplace.gichul.gichulgenerator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.satisfactoryplace.gichul.gichulgenerator.data.QuestionNameBuilder;
import com.satisfactoryplace.gichul.gichulgenerator.server.FirebaseConnection;
import com.satisfactoryplace.gichul.gichulgenerator.utils.Common;
import com.satisfactoryplace.gichul.gichulgenerator.utils.QuestionUtil;

import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WINDOWS7 on 2018-01-31.
 */

public class RecheckQuestionActivity extends AppCompatActivity {

    @BindView(R.id.recheck_title) TextView title;
    @BindView(R.id.recheck_potential) TextView potential;
    @BindView(R.id.recheck_exam) ImageView examImage;
    @BindView(R.id.recheck_solution) ImageView solutionImage;
    @BindView(R.id.recheck_imageChange) Button imageChangeBtn;

    @BindView(R.id.recheck_loadingContainer)
    RelativeLayout loadingContainer;
    @BindView(R.id.recheck_container)
    RelativeLayout container;

    @BindView(R.id.recheck_sunungMessage) TextView invisibleReason;

    private final int exam= 15223;
    private final int solution= 115223;
    private int imageStatus= exam;

    private QuestionNameBuilder qn= QuestionNameBuilder.inst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recheckquestion);
        ButterKnife.bind(this);

        init();
    }

    //Will be called after finishing load examImage
    private void loadExamImage(){
        FirebaseConnection.getInstance().loadImage(qn.createImagePath(QuestionNameBuilder.TYPE_Q), examImage, getApplicationContext(), new FirebaseConnection.ImageLoadFinished() {
            @Override
            public void success(Bitmap bitmap) {
                loadSolutionImage();
            }

            @Override
            public void fail(Exception e) {
                Toast.makeText(RecheckQuestionActivity.this, "이미지를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    private void loadSolutionImage(){
        FirebaseConnection.getInstance().loadImage(qn.createImagePath(QuestionNameBuilder.TYPE_A), solutionImage, getApplicationContext(),
                new FirebaseConnection.ImageLoadFinished() {
                    @Override
                    public void success(Bitmap bitmap) {
                        loadingContainer.setVisibility(View.GONE);
                        container.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void fail(Exception e) {
                        Toast.makeText(RecheckQuestionActivity.this, "이미지를 불러올 수 없습니다\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void init(){
        initTitle();
        initPotentialText();
        loadExamImage();
    }

    private void initTitle(){
        title.setText(qn.createTitileText());
    }
    private void initPotentialText(){
        potential.setText(QuestionUtil.getPotentialText(Integer.valueOf(qn.potential)));
    }

    @OnClick(R.id.recheck_imageChange)
    void changeImage(){
        if(imageStatus== exam){
            examImage.setVisibility(View.GONE);
            solutionImage.setVisibility(View.VISIBLE);
            imageStatus= solution;

            imageChangeBtn.setText("문제 확인");
        }else{
            examImage.setVisibility(View.VISIBLE);
            solutionImage.setVisibility(View.GONE);
            invisibleReason.setVisibility(View.GONE);
            imageStatus= exam;

            imageChangeBtn.setText("해설 확인");
        }
    }
    @OnClick(R.id.recheck_searchSolution)
    void searchSolution(){
        Toast.makeText(getApplicationContext(), "ebs 강의 검색 페이지로 이동합니다. (로그인 필요)", Toast.LENGTH_SHORT).show();

        String url = "http://www.ebsi.co.kr/ebs/xip/xipa/retrieveSCVLastExamList.ebs";
        Common.openUrl(getApplicationContext(), url);
    }

    @OnClick(R.id.recheck_openMemo)
    void openMemo(){
        String memo= getIntent().getStringExtra("memo");
        if(memo.equals("")){
            Toast.makeText(this, "이 문제에 저장된 메모가 없습니다.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, memo, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        if(examImage.getDrawable()!= null){
            ((BitmapDrawable)examImage.getDrawable()).getBitmap().recycle();
        }
        if(solutionImage.getDrawable()!= null){
            ((BitmapDrawable)solutionImage.getDrawable()).getBitmap().recycle();
        }
        super.onDestroy();
    }
}
