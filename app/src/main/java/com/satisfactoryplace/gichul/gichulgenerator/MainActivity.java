package com.satisfactoryplace.gichul.gichulgenerator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new MainPageLodingFragment()).commit();
        MobileAds.initialize(this,"ca-app-pub-5333091392909120~5084648179");
    }

    private long time= 0;
    @Override
    public void onBackPressed(){
        Fragment currentFragment= getSupportFragmentManager().findFragmentByTag("mainPage");
        if(currentFragment instanceof OnBackPressedListener){
            OnBackPressedListener backPressedListener= (OnBackPressedListener)currentFragment;
            boolean isContinueExecute= backPressedListener.onBackPressed();
            if(isContinueExecute== true){
                if(System.currentTimeMillis()-time>=1500){
                    time=System.currentTimeMillis();
                    Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
                }else if(System.currentTimeMillis()-time<1500){
                    openFullSizeAd();
                    finish();
                }
            }
        }else{
            if(System.currentTimeMillis()-time>=1500){
                time=System.currentTimeMillis();
                Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
            }else if(System.currentTimeMillis()-time<1500){
                openFullSizeAd();
                finish();
            }
        }
    }

    private void openFullSizeAd(){
        final InterstitialAd mInterstitialAd= new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5333091392909120/9585231317");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mInterstitialAd.show();
            }
        });
    }

}
