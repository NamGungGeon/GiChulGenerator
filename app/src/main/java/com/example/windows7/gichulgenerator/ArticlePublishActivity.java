package com.example.windows7.gichulgenerator;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.util.HashMap;

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
    @BindView(R.id.articlePublish_imageUpload)
    Button imageUploadBtn;
    @BindView(R.id.articlePublish_imagePreview)
    ImageView previewImage;

    private Unbinder unbinder;
    private String imagePath= null;

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
        resizeView(imageUploadBtn);
        resizeView(previewImage);
    }

    private void resizeView(View view){
        view.measure(0, 0);

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = view.getMeasuredWidth();
        params.width= params.height;
        view.setLayoutParams(params);
        view.requestLayout();
    }

    private void openWarningMessage(){
        final DialogMaker dialog= new DialogMaker();
        String message= "게시판에 욕설, 음란한 내용이나 링크, 사진을 공유할 시 사용자의 서비스 이용이 중지됩니다.\n" +
                "또한, 정보통신망법에 의거 처벌 받을 수 있으니 유의해 주시기 바랍니다.\n\n" +
                "그 외에도 도배와 같은 다른 사용자에게 불편을 줄 수 있는 행위를 하는 사용자는 관리자의 판단 하에 삭제되거나 이용이 정지될 수 있습니다.";
        dialog.setValue(message, "알겠습니다", "", null, null);
        dialog.show(getSupportFragmentManager(), "Waring Message");
    }

    @OnClick(R.id.articlePublish_publish)
    void publishArticle(){
        if(title.getText().equals("") || context.getText().toString().equals("")){
            Toast.makeText(this, "제목과 본문을 입력하세요.", Toast.LENGTH_SHORT).show();
        }else{
            final ProgressDialog dialog = ProgressDialog.show(this, "","글을 업로드하는 중입니다...", true);
            dialog.show();

            final DatabaseReference ref= FirebaseConnection.getInstance().getReference("freeboard").push();
            if(imagePath!= null){
                FirebaseConnection.getInstance().uploadImage(ref.getKey(), new File(imagePath), new FirebaseConnection.Callback() {
                    @Override
                    public void success(DataSnapshot snapshot) {
                        Article article= new Article(title.getText().toString(), context.getText().toString(), Status.nickName
                                , FirebaseAuth.getInstance().getUid(), ref.getKey(), new HashMap<String, Comment>());
                        ref.setValue(article);
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void fail(String errorMessage) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Article article= new Article(title.getText().toString(), context.getText().toString(), Status.nickName
                        , FirebaseAuth.getInstance().getUid(), ref.getKey(), new HashMap<String, Comment>());
                ref.setValue(article);
                finish();
            }
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

    @OnClick(R.id.articlePublish_imageUpload)
    void uploadImage(){
        if(checkPermission()== PackageManager.PERMISSION_GRANTED){
            Intent galleryIntent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent ,123 );
        }else{
            getPermission();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 123 :
                if (null != data) {
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null );
                    cursor.moveToNext();
                    String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
                    cursor.close();

                    imagePath= path;

                    //Set Preview Image
                    Bitmap bitmap= null;
                    bitmap= BitmapFactory.decodeFile(imagePath);
                    bitmap= Bitmap.createScaledBitmap(bitmap, 128, 128, true);
                    BitmapDrawable background= new BitmapDrawable(bitmap);
                    previewImage.setBackground(background);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        cancel();
    }

    private int checkPermission(){
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void getPermission(){
        //권한이 부여되어 있는지 확인
        int permissonCheck= checkPermission();

        if(permissonCheck == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "파일 읽기 권한 있음", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "파일 읽기 권한 없음", Toast.LENGTH_SHORT).show();

            //권한설정 dialog에서 거부를 누르면
            //ActivityCompat.shouldShowRequestPermissionRationale 메소드의 반환값이 true가 된다.
            //단, 사용자가 "Don't ask again"을 체크한 경우
            //거부하더라도 false를 반환하여, 직접 사용자가 권한을 부여하지 않는 이상, 권한을 요청할 수 없게 된다.
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                //이곳에 권한이 왜 필요한지 설명하는 Toast나 dialog를 띄워준 후, 다시 권한을 요청한다.
                Toast.makeText(this, "파일 읽기 권한 없음", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            }else{
                Toast.makeText(this, "파일 읽기 권한 있음", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},123);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[]){
        switch(requestCode){
            case 123:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "파일 읽기 권한 있음", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "이 권한이 없으면 이미지 업로드가 제한됩니다", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
