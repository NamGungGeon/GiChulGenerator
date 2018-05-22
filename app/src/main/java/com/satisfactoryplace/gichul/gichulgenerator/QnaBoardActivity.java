package com.satisfactoryplace.gichul.gichulgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by WINDOWS7 on 2018-02-17.
 */

public class QnaBoardActivity extends AppCompatActivity {

    @BindView(R.id.qna_loadingContainer) RelativeLayout loadingContainer;
    @BindView(R.id.qna_container) RelativeLayout container;
    @BindView(R.id.qna_list) ListView list;
    @BindView(R.id.qnaboardAd) AdView adView;

    private final int PUBLISH_ACTIVITY= 1524;
    private final int ARTICLE_ACTIVITY= 1552;

    private int listNumber= 15;

    private Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_qnaboard);
        unbinder= ButterKnife.bind(this);

        DatabaseReference reference= FirebaseConnection.getInstance().getReference("qna/");
        setArticleList(reference.orderByKey().limitToLast(listNumber));
        openWarningMessage();
        setAdView();
    }

    private void setAdView(){
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void openWarningMessage(){
        final DialogMaker dialog= new DialogMaker();
        String message= "이 게시판은 관리자에게 문의할 내용이 있을 경우에 사용하는 게시판입니다.\n\n" +
                "글은 관리자와 본인만 읽을 수 있습니다.";
        dialog.setValue(message, "알겠습니다", null, null, null);
        dialog.show(getSupportFragmentManager(), "Open WarningMessage");
    }

    private void setArticleList(Query query){
        final ArrayList<Article> articles= new ArrayList<>();
        FirebaseConnection.getInstance().loadDataWithQuery(query, new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                DataSnapshot dataSnapshot= snapshot;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Article article= postSnapshot.getValue(Article.class);
                    if(article!= null){
                        articles.add(article);
                    }
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
                list.deferNotifyDataSetChanged();
                if(listNumber!= 15){
                    list.setSelection(listNumber-20);
                }

                loadingContainer.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);


                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if(articles.get(i).getUid().equals(FirebaseAuth.getInstance().getUid())
                                || Status.nickName.equals("관리자")){
                            Intent intent= new Intent(getApplicationContext(), ArticleActivity.class);
                            intent.putExtra("articleKey", articles.get(i).getKey());
                            intent.putExtra("articleType", "qna");
                            startActivityForResult(intent, ARTICLE_ACTIVITY);
                        }else{
                            Toast.makeText(getApplicationContext(), "본인이 작성한 글이 아닙니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                        final DialogMaker dialog= new DialogMaker();
                        dialog.setValue("게시글을 삭제하시겠습니까?\n(본인만 삭제할 수 있습니다)", "예", "아니오",
                                new DialogMaker.Callback() {
                                    @Override
                                    public void callbackMethod() {
                                        if(articles.get(i).getUid().equals(FirebaseAuth.getInstance().getUid())
                                                || Status.nickName.equals("관리자")){
                                            FirebaseConnection.getInstance().getReference("qna/"+ articles.get(i).getKey()+ "/").removeValue();
                                            FirebaseConnection.getInstance().deleteImage("qna/"+ articles.get(i).getKey());
                                            Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                            refresh();
                                        }else{
                                            Toast.makeText(getApplicationContext(), "본인이 작성한 글이 아닙니다.", Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    }
                                }, null);
                        dialog.show(getSupportFragmentManager(), "Try to remove article");
                        return true;
                    }
                });

                if(list.getAdapter().getCount()< listNumber){
                    //There is no data to load from server
                    list.setOnScrollListener(null);
                }else{
                    list.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                                    && (list.getLastVisiblePosition() - list.getHeaderViewsCount() -
                                    list.getFooterViewsCount()) >= (list.getAdapter().getCount() - 1)) {
                                // Now your listview has hit the bottom
                                listNumber+= 15;
                                DatabaseReference reference= FirebaseConnection.getInstance().getReference("qna/");
                                setArticleList(reference.orderByKey().limitToLast(listNumber));
                            }
                        }

                        @Override
                        public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                        }
                    });
                }
            }
            @Override
            public void fail(String errorMessage) {
                Toast.makeText(getApplicationContext(), "데이터베이스 통신 오류: "+ errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }
    @OnClick(R.id.qna_search)
    void searchBtn(){
        final DialogMaker dialog= new DialogMaker();
        final View childView= getLayoutInflater().inflate(R.layout.dialog_searcharticle, null);
        dialog.setValue(null, "검색", "취소",
                new DialogMaker.Callback() {
                    @Override
                    public void callbackMethod() {
                        EditText target= childView.findViewById(R.id.searchArticle_target);
                        String searchTarget= target.getText().toString();
                        if(searchTarget.equals("")){
                            Toast.makeText(getApplicationContext(), "검색 단어를 입력하세요", Toast.LENGTH_SHORT).show();
                        }else{
                            search(searchTarget);
                            dialog.dismiss();
                        }
                    }
                }, null, childView);
        dialog.show(getSupportFragmentManager(), "");
    }
    private void search(String target){
        DatabaseReference reference= FirebaseConnection.getInstance().getReference("qna/");
        setArticleList(reference.orderByChild("title").equalTo(target));

        loadingContainer.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.qna_refresh)
    void refresh(){
        loadingContainer.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
        DatabaseReference reference= FirebaseConnection.getInstance().getReference("qna/");
        listNumber= 10;
        setArticleList(reference.orderByKey().limitToLast(listNumber));
    }

    @OnClick(R.id.qna_publishArticle)
    void publishArticle(){
        Intent intent= new Intent(getApplicationContext(), ArticlePublishActivity.class);
        intent.putExtra("articleType", "qna");
        startActivityForResult(intent, PUBLISH_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refresh();
    }
}
