package com.example.windows7.gichulgenerator;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by WINDOWS7 on 2018-02-28.
 */

public class ExamResultFragment extends Fragment {


    private Unbinder unbinder;
    private ArrayList<Integer> inputAnswers;
    private ArrayList<Long> rightAnswers;

    private Bitmap solutionBitmap[];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_examresult, container, false);

        unbinder= ButterKnife.bind(this, rootView);

        final ProgressDialog progressDialog= DialogMaker.showProgressDialog(getActivity(), "", "정답을 불러오는 중입니다.");
        //Load Answer List
        String answerPath= "answer/"+  getActivity().getIntent().getStringExtra("period_y")+ "/"+ getActivity().getIntent().getStringExtra("encodedInstitute")
                + "/"+ getActivity().getIntent().getStringExtra("period_m")+ "/"+ getActivity().getIntent().getStringExtra("encodedSubject");
        FirebaseConnection.getInstance().loadData(answerPath, new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                progressDialog.setMessage("정답률을 불러오는 중입니다");
                //Load Potential List
                String potentialPath= "potential/"+ getActivity().getIntent().getStringExtra("period_y")+ "/"+ getActivity().getIntent().getStringExtra("encodedInstitute")
                        + "/"+ getActivity().getIntent().getStringExtra("period_m")+ "/"+ getActivity().getIntent().getStringExtra("encodedSubject");
                FirebaseConnection.getInstance().loadData(potentialPath, new FirebaseConnection.Callback() {
                    @Override
                    public void success(DataSnapshot snapshot) {
                        progressDialog.setMessage("해설 이미지를 불러오는 중입니다");
                        String imagePath= getActivity().getIntent().getStringExtra("basicFileName")+ "/a_"+ getActivity().getIntent().getStringExtra("basicFileName");
                        for(int i=1; i<=30; i++){
                            final int _i= i;
                            FirebaseConnection.getInstance().loadImage(imagePath + String.valueOf(i), null, getContext(), new FirebaseConnection.ImageLoadFinished() {
                                @Override
                                public void success(Bitmap bitmap) {
                                    solutionBitmap[_i]= bitmap;
                                }

                                @Override
                                public void fail(Exception e) {
                                    Toast.makeText(getContext(), "이미지를 불러올 수 없습니다\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    getActivity().finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void fail(String errorMessage) {
                        Toast.makeText(getContext(), "데이터를 불러올 수 없습니다\n"+ errorMessage, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        getActivity().finish();
                    }
                });
            }

            @Override
            public void fail(String errorMessage) {
                Toast.makeText(getContext(), "데이터를 불러올 수 없습니다\n"+ errorMessage, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                getActivity().finish();
            }
        });
        return rootView;
    }

    private void init(){

    }
}
