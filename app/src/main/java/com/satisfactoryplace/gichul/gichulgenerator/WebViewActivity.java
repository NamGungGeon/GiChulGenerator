package com.satisfactoryplace.gichul.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WINDOWS7 on 2018-02-28.
 */

public class WebViewActivity extends AppCompatActivity{
    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.webview_title)
    TextView webViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        webView.getSettings().setJavaScriptEnabled(true);

        String title= getIntent().getStringExtra("title");
        if(title!= null){
            webViewTitle.setText(title);
        }
        String url= getIntent().getStringExtra("url");
        if(url!= null){
            webView.loadUrl(url);
        }else{
            Toast.makeText(this, "url을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @OnClick(R.id.webview_close)
    void closeWebView(){
        finish();
    }

    @Override
    public void onBackPressed() {
        if(webView!= null){
            if(webView.canGoBack()== true){
                webView.goBack();
            }else{
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }
    }
}
