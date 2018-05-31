package com.satisfactoryplace.gichul.gichulgenerator;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class MainPageLodingFragment extends Fragment implements OnBackPressedListener{

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
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getContext(), "로그인 실패. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
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
        //Start to connection firebase and Load data
        //Load CheckList
        loadingText.setText("오답노트 가져오는 중...");
        CheckList.getInstance().loadCheckListFromServer(new CheckList.Callback() {
            @Override
            public void success() {
                loadingText.setText("문제 기록 가져오는 중...");
                //Load HistoryList
                HistoryList.getInstance().loadHistoryListFromServer(new HistoryList.Callback() {
                    @Override
                    public void success() {
                        //Load Schedule
                        loadingText.setText("일정 가져오는 중...");
                        FirebaseConnection.getInstance().loadData("appdata/schedule/sunung", new FirebaseConnection.Callback() {
                            @Override
                            public void success(DataSnapshot snapshot) {
                                getActivity().getIntent().putExtra("schedule", (String)snapshot.getValue());
                                loadingText.setText("사용자 정보 가져오는 중...");
                                //Load UserStatus
                                FirebaseConnection.getInstance().loadData("userdata/" + FirebaseAuth.getInstance().getUid() + "/status", new FirebaseConnection.Callback() {
                                    @Override
                                    public void success(DataSnapshot snapshot) {
                                        Status.setValues((HashMap<String, String>)snapshot.getValue());
                                        loadingText.setText("앱 정보 가져오는 중...");
                                        //Load AppData
                                        FirebaseConnection.getInstance().loadData("appdata/", new FirebaseConnection.Callback() {
                                            @Override
                                            public void success(DataSnapshot snapshot) {
                                                AppData.setValue((HashMap<String, String>)snapshot.getValue());
                                                loadingText.setText("시험 결과 가져오는 중...");
                                                ExamResultList.getInstance().loadExamResultListFromFirebase(new ExamResultList.Callback() {
                                                    @Override
                                                    public void success() {
                                                        loadingText.setText("잠시만 기다려주세요...");
                                                        openFullSizeAd();
                                                    }

                                                    @Override
                                                    public void fail() {
                                                        Toast.makeText(getContext(), "데이터베이스 통신 실패. 다시 시작하세요.", Toast.LENGTH_SHORT).show();
                                                        getActivity().finish();
                                                    }
                                                });
                                            }
                                            @Override
                                            public void fail(String errorMessage) {
                                                Toast.makeText(getContext(), "데이터베이스 통신 실패. 다시 시작하세요.", Toast.LENGTH_SHORT).show();
                                                getActivity().finish();
                                            }
                                        });
                                    }

                                    @Override
                                    public void fail(String errorMessage) {
                                        Toast.makeText(getContext(), "데이터베이스 통신 실패. 다시 시작하세요.", Toast.LENGTH_SHORT).show();
                                        getActivity().finish();
                                    }
                                });
                            }

                            @Override
                            public void fail(String errorMessage) {
                                Toast.makeText(getContext(), "데이터베이스 통신 실패. 다시 시작하세요.", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        });
                    }

                    @Override
                    public void fail() {
                        Toast.makeText(getContext(), "데이터베이스 통신 실패. 다시 시작하세요.", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                });
            }
            @Override
            public void fail() {
                Toast.makeText(getContext(), "데이터베이스 통신 실패. 다시 시작하세요.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new MainPageFragment(), "mainPage").commit();
                super.onAdClosed();
            }
        });
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
