package com.example.windows7.gichulgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by WINDOWS7 on 2018-02-11.
 */

public class FreeboardActivity extends AppCompatActivity {
    @BindView(R.id.freeboard_loadingContainer) RelativeLayout loadingContainer;
    @BindView(R.id.freeboard_container) RelativeLayout container;
    @BindView(R.id.freeboard_list) ListView list;

    private final int PUBLISH_ACTIVITY= 1524;
    private final int ARTICLE_ACTIVITY= 1552;

    private Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_freeboard);
        unbinder= ButterKnife.bind(this);

        setArticleList();
    }

    private void setArticleList(){
        final ArrayList<Article> articles= new ArrayList<>();

        DatabaseReference reference= FirebaseConnection.getInstance().getReference("freeboard/");
        FirebaseConnection.getInstance().loadDataWithQuery(reference.orderByKey().limitToLast(10), new FirebaseConnection.Callback() {
                    @Override
                    public void success(DataSnapshot snapshot) {
                        DataSnapshot dataSnapshot= snapshot;
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Article article= postSnapshot.getValue(Article.class);
                            articles.add(article);
                        }
                        //sorting
                        Collections.sort(articles, new Comparator<Article>() {
                            @Override
                            public int compare(Article article, Article t1) {
                                return Long.valueOf(t1.getTimeStamp()).compareTo(Long.valueOf(article.getTimeStamp()));
                            }
                        });

                        ListViewAdapter_Freeboard adapter= new ListViewAdapter_Freeboard(getApplicationContext(), R.layout.item_freeboard, articles);
                        list.setAdapter(adapter);

                        loadingContainer.setVisibility(View.GONE);
                        container.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void fail(String errorMessage) {
                        Toast.makeText(FreeboardActivity.this, "데이터베이스 통신 오류: "+ errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(getApplicationContext(), ArticleActivity.class);
                intent.putExtra("articleKey", articles.get(i).getKey());
                startActivityForResult(intent, ARTICLE_ACTIVITY);
            }
        });
    }


    @OnClick(R.id.freeboard_refresh)
    void refresh(){
        loadingContainer.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
        setArticleList();
    }

    @OnClick(R.id.freeboard_publishArticle)
    void publishArticle(){
        startActivityForResult(new Intent(getApplicationContext(), ArticlePublishActivity.class), PUBLISH_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refresh();
    }
}
