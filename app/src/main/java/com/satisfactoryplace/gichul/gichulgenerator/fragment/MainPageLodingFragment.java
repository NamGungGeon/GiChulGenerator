package com.satisfactoryplace.gichul.gichulgenerator.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.satisfactoryplace.gichul.gichulgenerator.model.OnBackPressedListener;
import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.utils.Common;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class MainPageLodingFragment extends Fragment implements OnBackPressedListener {

    private int loadingNumber= 0;
    private final int LOGIN_GOOGLE=1235;

    private GoogleSignInOptions gso=null;
    private GoogleApiClient mGoogleApiClient=null;
    private FirebaseAuth firebaseAuth=null;

    @BindView(R.id.loadingText) TextView loadingText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_loading, container, false);
        ButterKnife.bind(this, rootView);

        login_google();
        return rootView;
    }

    public void login_google(){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), connectionResult -> {
                    Toast.makeText(getContext(), "로그인 실패. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, LOGIN_GOOGLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case LOGIN_GOOGLE:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if(result.isSuccess()){
                    firebaseAuthWithGoogle(result.getSignInAccount());
                }else{
                    Toast.makeText(getContext(),"로그인 실패. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                    if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()){
                        mGoogleApiClient.stopAutoManage(getActivity());
                        mGoogleApiClient.disconnect();
                    }
                    getActivity().finish();
                }
                break;

        }
    }

    private void loadDataFromServer(){
        openFullSizeAd();

        loadingText.setText("데이터 로딩 중입니다... (6개 중 "+ loadingNumber+ "개 완료)");

        Common.loadBaseData(()->{
            Toast.makeText(getContext(), "데이터베이스 통신 실패. 다시 시작하세요.", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }, ()->{
            loadingProgress();
        });
    }
    private void openFullSizeAd(){
        final InterstitialAd mInterstitialAd= new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-5333091392909120/9585231317");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
        });
    }
    private synchronized void loadingProgress(){
        loadingNumber++;
        loadingText.setText("데이터 로딩 중입니다... (6개 중 "+ loadingNumber+ "개 완료)");

        if(loadingNumber== 6){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainContainer, new MainPageFragment(), "mainPage").commit();
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Firebase Auth", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    //User is login
                    loadDataFromServer();
                }else{
                    //User is logout
                }
            }
        });

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Firebase Auth", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Firebase Auth", "signInWithCredential", task.getException());
                            Toast.makeText(getContext(), "인증 실패. 앱을 종료합니다.",Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }
    }
    @Override
    public void onDestroy() {
        if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()){
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }
    @Override
    public boolean onBackPressed() {
        return true;
    }
}
