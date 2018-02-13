package com.example.windows7.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Windows10 on 2018-02-12.
 */

public class CommentActivity extends AppCompatActivity {
    @BindView(R.id.comment_list)
    ListView commentList;
    @BindView(R.id.comment_newComment)
    EditText newComment;
    @BindView(R.id.comment_loadingContainer)
    RelativeLayout loadingContainer;
    @BindView(R.id.comment_container)
    RelativeLayout container;

    private Unbinder unbinder;
    private ArrayList<Comment> loadedComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_comment);
        unbinder= ButterKnife.bind(this);
        init();
    }

    private void init(){
        setCommentList();
    }

    private void setCommentList(){
        String key= getIntent().getStringExtra("articleKey");

        FirebaseConnection.getInstance().loadDataWithQuery(FirebaseConnection.getInstance().getReference("freeboard/" + key + "/comments/").orderByKey(), new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                if(snapshot==null){
                    //Exception
                    Toast.makeText(CommentActivity.this, "관리자에 의해 삭제된 게시글입니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                loadedComments= new ArrayList<>();
                for(DataSnapshot postSnapShot: snapshot.getChildren()){
                    Comment comment= postSnapShot.getValue(Comment.class);
                    loadedComments.add(comment);
                }

                ListViewAdapter_Comment listViewAdapter_comment= new ListViewAdapter_Comment(getApplicationContext(), R.layout.item_comment, loadedComments);
                commentList.setAdapter(listViewAdapter_comment);

                loadingContainer.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
            }

            @Override
            public void fail(String errorMessage) {
                Toast.makeText(getApplicationContext(), "데이터베이스 통신 오류: "+ errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @OnClick(R.id.comment_submit)
    void submitComment(){
        String text= newComment.getText().toString();
        if(text.equals("")){
            Toast.makeText(this, "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
        }else{
            DatabaseReference ref= FirebaseConnection.getInstance().getReference("freeboard/"+ getIntent().getStringExtra("articleKey")+ "/comments/");

            String userName= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            String uid= FirebaseAuth.getInstance().getUid();

            Comment comm= new Comment(userName, text, uid);
            loadedComments.add(comm);
            ref.push().setValue(comm);

            ListViewAdapter_Comment listViewAdapter_comment= new ListViewAdapter_Comment(getApplicationContext(), R.layout.item_comment, loadedComments);
            commentList.setAdapter(listViewAdapter_comment);

            Toast.makeText(this, "댓글이 등록되었습니다", Toast.LENGTH_SHORT).show();
            newComment.setText("");
            loadingContainer.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
            init();
        }
    }
}
