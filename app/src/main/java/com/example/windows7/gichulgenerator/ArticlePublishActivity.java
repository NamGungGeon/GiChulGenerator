package com.example.windows7.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
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

public class ArticlePublishActivity extends AppCompatActivity{

    @BindView(R.id.articlePublish_title)
    EditText title;
    @BindView(R.id.articlePublish_context)
    EditText context;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_articlepublish);
        unbinder= ButterKnife.bind(this);
        init();
        openWarningMessage();
    }

    private void init(){

    }

    private void openWarningMessage(){
        final DialogMaker dialog= new DialogMaker();
        String message= "게시판에 욕설, 음란한 내용이나 링크, 사진을 공유할 시 사용자의 서비스 이용이 중지됩니다.\n" +
                "또한, 정보통신망법에 의거 처벌 받을 수 있으니 유의해 주시기 바랍니다.\n\n" +
                "그 외에도 다른 사용자에게 불편을 줄 수 있는 행위를 하는 사용자는 관리자의 판단 하에 삭제되거나 이용이 정지될 수 있습니다.";
        dialog.setValue(message, "알겠습니다", "", null, null);
        dialog.show(getSupportFragmentManager(), "Waring Message");
    }

    @OnClick(R.id.articlePublish_publish)
    void publishArticle(){
        if(title.getText().equals("") || context.getText().toString().equals("")){
            Toast.makeText(this, "제목과 본문을 입력하세요.", Toast.LENGTH_SHORT).show();
        }else{
            DatabaseReference ref= FirebaseConnection.getInstance().getReference("freeboard").push();
            ArrayList<Comment> coms= new ArrayList<>();
            coms.add(new Comment("test", "testt", "test", "test"));
            Article article= new Article(title.getText().toString(), context.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                    , FirebaseAuth.getInstance().getUid(), ref.getKey(), coms);
            ref.setValue(article);
            finish();
        }

    }

    @OnClick(R.id.articlePublish_cancel)
    void cancel(){
        final DialogMaker dialogMaker= new DialogMaker();
        dialogMaker.setValue("작성한 글이 모두 삭제됩니다. 돌아가시겠습니까?", "예", "아니오", new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                dialogMaker.dismiss();
                finish();
            }
        }, null);
        dialogMaker.show(getSupportFragmentManager(), "cancel publish");
    }

    @Override
    public void onBackPressed() {
        cancel();
    }
}
