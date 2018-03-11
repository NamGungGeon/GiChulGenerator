package com.satisfactoryplace.gichul.gichulgenerator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    private ProgressDialog progressDialog= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_recheckquestion);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        progressDialog= DialogMaker.showProgressDialog(this, "", "문제와 해답을 가져오는 중입니다...");

        title.setText(getIntent().getStringExtra("title"));

        // Hide real potential
        String examPotential= getIntent().getStringExtra("potential");
        int _potential= Integer.valueOf(examPotential);
        String potentialText= "정답률: ";
        if(_potential>= 80){
            potentialText+= "매우높음";
        }else if(_potential>=60){
            potentialText+= "높음";
        }else if(_potential>= 40){
            potentialText+= "보통";
        }else if(_potential>= 20){
            potentialText+= "낮음";
        }else{
            potentialText+= "매우낮음";
        }
        potential.setText(potentialText);

        String temp= getIntent().getStringExtra("fileName");
        String basicPath= "";
        StringTokenizer token= new StringTokenizer(temp, "_", false);
        basicPath+= token.nextToken()+"_";
        basicPath+= token.nextToken()+"_";
        basicPath+= token.nextToken()+"_";
        basicPath+= token.nextToken();

        final String _basicPath= basicPath;
        FirebaseConnection.getInstance().loadImage("exam/" + basicPath + "/" + "q_" + getIntent().getStringExtra("fileName"), examImage, getApplicationContext(), new FirebaseConnection.ImageLoadFinished() {
            @Override
            public void success(Bitmap bitmap) {
                FirebaseConnection.getInstance().loadImage("exam/" + _basicPath + "/" + "a_" + getIntent().getStringExtra("fileName"), solutionImage, getApplicationContext(),
                        new FirebaseConnection.ImageLoadFinished() {
                            @Override
                            public void success(Bitmap bitmap) {
                                loadingContainer.setVisibility(View.GONE);
                                container.setVisibility(View.VISIBLE);
                                if(progressDialog!= null){
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void fail(Exception e) {
                                if(progressDialog!= null){
                                    progressDialog.dismiss();
                                }

                                Toast.makeText(RecheckQuestionActivity.this, "이미지를 불러올 수 없습니다\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
            }

            @Override
            public void fail(Exception e) {
                if(progressDialog!= null){
                    progressDialog.dismiss();
                }

                Toast.makeText(RecheckQuestionActivity.this, "이미지를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // Check whether institute is sunung
    // In Case of sunung, copyright problem is exist
    // Must called whenever solution is visible.
    private void checkInstitute(){
        String titleMessage= title.getText().toString();
        if(titleMessage.contains("대학수학능력평가시험") || titleMessage.contains("수능")){
            //Case: Institute is sunung
            hideSolutionImage();
        }else{
            //Case: Institute is not sunung
            //Do not any action
        }
    }

    // Instead, Move to EBS text is visible.
    private void hideSolutionImage(){
        solutionImage.setVisibility(View.GONE);
        invisibleReason.setVisibility(View.VISIBLE);
        invisibleReason.setText("수능의 경우 해설지가 제공되지 않습니다.\n" +
                "아래에서 해설강의 검색 버튼을 눌러 해설지를 다운로드하세요");
    }

    @OnClick(R.id.recheck_imageChange)
    void changeImageBtn(){
        if(imageStatus== exam){
            examImage.setVisibility(View.GONE);
            solutionImage.setVisibility(View.VISIBLE);
            imageStatus= solution;

            checkInstitute();

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
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
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
