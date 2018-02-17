package com.example.windows7.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
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
    @BindView(R.id.comment_loadingContainer)
    RelativeLayout loadingContainer;
    @BindView(R.id.comment_container)
    RelativeLayout container;

    private Unbinder unbinder;
    private ArrayList<Comment> loadedComments;
    private String articleType;

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
        articleType= getIntent().getStringExtra("articleType");
        String key= getIntent().getStringExtra("articleKey");

        FirebaseConnection.getInstance().loadDataWithQuery(FirebaseConnection.getInstance().getReference(articleType+ "/" + key + "/comments/").orderByKey(), new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                loadedComments= new ArrayList<>();
                for(DataSnapshot postSnapShot: snapshot.getChildren()){
                    Comment comment= postSnapShot.getValue(Comment.class);
                    loadedComments.add(comment);
                }

                ListViewAdapter_Comment listViewAdapter_comment= new ListViewAdapter_Comment(getApplicationContext(), R.layout.item_comment, loadedComments);
                commentList.setAdapter(listViewAdapter_comment);
                commentList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                        final DialogMaker dialog= new DialogMaker();
                        dialog.setValue("댓글을 삭제하시겠습니까?\n(본인만 삭제할 수 있습니다)", "예", "아니오",
                                new DialogMaker.Callback() {
                                    @Override
                                    public void callbackMethod() {
                                        if(loadedComments.get(i).getUid().equals(FirebaseAuth.getInstance().getUid())
                                                || Status.nickName.equals("관리자")){
                                            FirebaseConnection.getInstance().getReference(articleType+ "/"+ getIntent().getStringExtra("articleKey")+ "/comments/"+ loadedComments.get(i).getKey()+ "/").removeValue();
                                            Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();

                                            loadingContainer.setVisibility(View.GONE);
                                            container.setVisibility(View.VISIBLE);
                                            init();

                                        }else{
                                            Toast.makeText(getApplicationContext(), "본인이 작성한 댓글이 아닙니다.", Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    }
                                }, null);
                        dialog.show(getSupportFragmentManager(), "Try to remove comment");

                        return true;
                    }
                });

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
        final String text= newComment.getText().toString();
        if(text.equals("")){
            Toast.makeText(this, "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
        }else{
            //Check whether article is removed
            final DialogMaker dialog= new DialogMaker();
            dialog.setValue(null, null, null, null, null
                    , getLayoutInflater().inflate(R.layout.dialog_loadingcomment,  null));
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(), "");

            //Check whether article is removed
            FirebaseConnection.getInstance().loadData(articleType+ "/" + getIntent().getStringExtra("articleKey") + "/", new FirebaseConnection.Callback() {
                @Override
                public void success(DataSnapshot snapshot) {
                    dialog.dismiss();
                    if(snapshot.getValue()== null){
                        //Case: Article is removed
                        Toast.makeText(CommentActivity.this, "삭제된 게시글입니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        //Case: Article is not removed
                        DatabaseReference ref= FirebaseConnection.getInstance().getReference(articleType+ "/"+ getIntent().getStringExtra("articleKey")+ "/comments/").push();

                        String uid= FirebaseAuth.getInstance().getUid();

                        Comment comm= new Comment(Status.nickName, text, uid, ref.getKey());
                        loadedComments.add(comm);
                        ref.setValue(comm);

                        ListViewAdapter_Comment listViewAdapter_comment= new ListViewAdapter_Comment(getApplicationContext(), R.layout.item_comment, loadedComments);
                        commentList.setAdapter(listViewAdapter_comment);

                        Toast.makeText(getApplicationContext(), "댓글이 등록되었습니다", Toast.LENGTH_SHORT).show();
                        newComment.setText("");
                        loadingContainer.setVisibility(View.VISIBLE);
                        container.setVisibility(View.GONE);
                        init();
                    }
                }

                @Override
                public void fail(String errorMessage) {
                    Toast.makeText(getApplicationContext(), "데이터베이스 통신 오류: "+ errorMessage, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}
