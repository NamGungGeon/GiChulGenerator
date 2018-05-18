package com.satisfactoryplace.gichul.gichulgenerator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Windows10 on 2018-02-12.
 */

public class ArticleActivity extends AppCompatActivity {
    @BindView(R.id.article_title)
    TextView title;
    @BindView(R.id.article_userName)
    TextView userName;
    @BindView(R.id.article_context)
    TextView context;
    @BindView(R.id.article_container)
    RelativeLayout container;
    @BindView(R.id.article_loadingContainer)
    RelativeLayout loadingContainer;
    @BindView(R.id.article_comment)
    Button commentBtn;
    @BindView(R.id.article_articleImage)
    ImageView image;
    @BindView(R.id.articleAd)
    AdView adView;

    Article article;
    private String articleType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_article);
        ButterKnife.bind(this);
        articleType= getIntent().getStringExtra("articleType");
        setAdView();

        loadArticle();
    }

    private void loadArticle(){
        String key= getIntent().getStringExtra("articleKey");
        FirebaseConnection.getInstance().loadData(articleType+ "/" + key + "/", new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                init(snapshot);
            }

            @Override
            public void fail(String errorMessage) {
                Toast.makeText(ArticleActivity.this, "데이터베이스 통신 오류: "+ errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void init(DataSnapshot snapshot){
        loadingContainer.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);

        article= snapshot.getValue(Article.class);
        if(article== null){
            // If Article is deleted...
            title.setText("삭제된 게시물입니다.");
            userName.setText("???");
            context.setText("삭제된 게시글입니다.");
            commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }else{
            //Set Title
            if(article.getTitle()!= null){
                title.setText(article.getTitle());
            }else{
                title.setText("???");
            }
            //Set UserName
            if(article.getUserName()!= null){
                if(article.getUserName().equals("관리자")){
                    userName.setText("관리자");
                    userName.setTextColor(getResources().getColor(R.color.red));
                }else{
                    userName.setText(article.getUserName());
                }
            }else{
                userName.setText("???");
            }
            //Set Content
            if(article.getText()!= null){
                context.setText(article.getText());
            }else{
                context.setText("???");
            }
            //Set Image
            if(article.getIsExistImage()){
                final ProgressDialog dialog= DialogMaker.showProgressDialog(ArticleActivity.this, "", "이미지 로딩 중입니다.");
                FirebaseConnection.getInstance().loadImage(articleType + "/" + article.getKey(), image, getApplicationContext(), new FirebaseConnection.ImageLoadFinished() {
                    @Override
                    public void success(Bitmap bitmap) {
                        dialog.dismiss();
                    }

                    @Override
                    public void fail(Exception e) {
                        Toast.makeText(ArticleActivity.this, "이미지 로딩 실패\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        }
    }

    private void setAdView(){
        MobileAds.initialize(this, "ca-app-pub-5333091392909120/9915083275");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @OnClick(R.id.article_comment)
    void openCommentLIst(){
        Intent intent= new Intent(getApplicationContext(), CommentActivity.class);
        intent.putExtra("articleKey", getIntent().getStringExtra("articleKey"));
        intent.putExtra("articleType", articleType);
        startActivityForResult(intent, 1);
    }
    @OnClick(R.id.article_delete)
    void delete(){
        final DialogMaker dialog= new DialogMaker();
        dialog.setValue("게시글을 삭제하시겠습니까?\n(본인만 삭제가 가능합니다)", "예", "아니오",
                new DialogMaker.Callback() {
                    @Override
                    public void callbackMethod() {
                        if(article.getUid().equals(FirebaseAuth.getInstance().getUid()) || Status.nickName.equals("관리자")){
                            FirebaseConnection.getInstance().getReference(articleType+ "/"+ article.getKey()).removeValue();
                            Toast.makeText(ArticleActivity.this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                        }else{
                            Toast.makeText(ArticleActivity.this, "본인이 작성한 글만 삭제할 수 있습니다", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                }, null);
        dialog.show(getSupportFragmentManager(), "");
    }

    @OnClick(R.id.article_correct)
    void correct(){
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadingContainer.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);

        loadArticle();
    }

    @Override
    protected void onDestroy() {
        if(image.getDrawable()!= null){
            Bitmap imageBitmap= ((BitmapDrawable)image.getDrawable()).getBitmap();

            if(imageBitmap!= null && imageBitmap.isRecycled()== false){
                imageBitmap.recycle();
            }
        }
        super.onDestroy();
    }
}
