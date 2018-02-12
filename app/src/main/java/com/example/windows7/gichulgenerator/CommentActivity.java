package com.example.windows7.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

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

    private Unbinder unbinder;
    private Article article;

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
        article= getIntent().getParcelableExtra("Article");
        ListViewAdapter_Comment listViewAdapter_comment= new ListViewAdapter_Comment(getApplicationContext(), R.layout.item_comment, article.getComments());
        commentList.setAdapter(listViewAdapter_comment);
    }

    @OnClick(R.id.comment_submit)
    void submitComment(){
        String text= newComment.getText().toString();
        if(text.equals("")){
            Toast.makeText(this, "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
        }else{
            DatabaseReference ref= FirebaseConnection.getInstance().getReference("freeboard/"+ article.getKey()+ "/comments/").push();
            String key= ref.getKey();
            ArrayList<Comment> tempList= article.getComments();
            tempList.add(new Comment(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), text, FirebaseAuth.getInstance().getUid(), key));

            ref.setValue(tempList);
        }
    }
}
