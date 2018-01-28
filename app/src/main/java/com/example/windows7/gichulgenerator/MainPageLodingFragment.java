package com.example.windows7.gichulgenerator;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class MainPageLodingFragment extends Fragment {

    private final int LOGIN_GOOGLE=1235;


    private GoogleSignInOptions gso=null;
    private GoogleApiClient mGoogleApiClient=null;
    private FirebaseAuth firebaseAuth=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_loading, container, false);
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
                        getActivity().finish();
                    }
                }
                break;

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
                    Toast.makeText(getContext(), firebaseAuth.getCurrentUser().getDisplayName()+ "님 로그인되었습니다.", Toast.LENGTH_SHORT).show();
                    CheckList.Callback checkList_callback= new CheckList.Callback() {
                        @Override
                        public void success() {
                            HistoryList.getInstance().loadHistoryListFromServer(new HistoryList.Callback() {
                                @Override
                                public void success() {
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new MainPageFragment()).commit();
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
                    };
                    CheckList.getInstance().loadCheckListFromServer(checkList_callback);
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
}
