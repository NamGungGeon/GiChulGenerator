package com.example.windows7.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by WINDOWS7 on 2018-02-11.
 */

public class FreeboardActivity extends AppCompatActivity {
    @BindView(R.id.freeboard_loadingContainer) RelativeLayout loadingContainer;
    @BindView(R.id.freeboard_container) RelativeLayout container;
    @BindView(R.id.freeboard_list) ListView list;

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
        FirebaseConnection.getInstance().loadDataWithQuery(reference.limitToFirst(10), new FirebaseConnection.Callback() {
                    @Override
                    public void success(Object data) {
                        DataSnapshot dataSnapshot= (DataSnapshot)data;
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Article article= postSnapshot.getValue(Article.class);
                            articles.add(article);
                        }

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
                Toast.makeText(FreeboardActivity.this, "CLICK", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
