package com.example.windows7.gichulgenerator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.StringTokenizer;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class ExamSolutionFragment extends Fragment {

    //해답 파일 명명 규칙
    //타입_기간(년)_기간(월)_주최기관_과목_문제번호
    private String solutionFileName= "";

    private TextView solutionTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_examsolution, container, false);
        init(rootView);
        return rootView;
    }

    private void init(ViewGroup rootView){
        String examFileName= getActivity().getIntent().getStringExtra("info");
        StringTokenizer tokenizer= new StringTokenizer(examFileName, "_", false);

        solutionFileName+= "q_";
        tokenizer.nextToken();
        //기간(년)
        solutionFileName+= tokenizer.nextToken()+ "_";
        //기간(월)
        solutionFileName+= tokenizer.nextToken()+ "_";
        //주최기관
        solutionFileName+= tokenizer.nextToken()+ "_";
        //과목
        solutionFileName+= tokenizer.nextToken()+ "_";
        //문제번호
        solutionFileName+= tokenizer.nextToken()+ "_";

        //solution info
        solutionTitle= rootView.findViewById(R.id.solutionTitle);

        // descript action after loading data
        FirebaseConnection.Callback callback= new FirebaseConnection.Callback() {
            @Override
            public void success(String data) {
                solutionTitle.setText(data);
            }

            @Override
            public void fail(String errorMessage) {
                solutionTitle.setText(errorMessage);
            }
        };
        FirebaseConnection.getInstance().loadData("answer/2018/sunung/11/imath/7", callback);
    }

}
