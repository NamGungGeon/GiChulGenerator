package com.example.windows7.gichulgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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

    private Unbinder unbinder;
    Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_article);
        unbinder= ButterKnife.bind(this);
        init();
    }

    private void init(){
        String key= getIntent().getStringExtra("articleKey");
        FirebaseConnection.getInstance().loadData("freeboard/" + key + "/", new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
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
                    title.setText(article.getTitle());
                    if(article.getUserName().equals("관리자")){
                        userName.setText("관리자");
                        userName.setTextColor(getResources().getColor(R.color.red));
                    }else{
                        userName.setText(article.getUserName());
                    }
                    context.setText(article.getText());
                }
            }

            @Override
            public void fail(String errorMessage) {
                Toast.makeText(ArticleActivity.this, "데이터베이스 통신 오류: "+ errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @OnClick(R.id.article_comment)
    void openCommnetLIst(){
        Intent intent= new Intent(getApplicationContext(), CommentActivity.class);
        intent.putExtra("articleKey", getIntent().getStringExtra("articleKey"));
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadingContainer.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
        init();
    }
}
