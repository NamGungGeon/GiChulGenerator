package com.example.windows7.gichulgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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
        article= getIntent().getParcelableExtra("Article");
        title.setText(article.getTitle());
        userName.setText(article.getUserName());
        context.setText(article.getText());
    }

    @OnClick(R.id.article_comment)
    void openCommnetLIst(){
        Intent intent= new Intent(getApplicationContext(), CommentActivity.class);
        intent.putExtra("Article", article);
        startActivity(intent);
    }


}
