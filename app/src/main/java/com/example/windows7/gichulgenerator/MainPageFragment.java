package com.example.windows7.gichulgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class MainPageFragment extends Fragment {
    private Button goToStudyBtn;
    private ImageView menuListBtn;
    private ImageView calendarBtn;
    private ImageView checkHistoryBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_mainmenu, container, false);
        init(rootView);
        return rootView;
    }

    private void init(ViewGroup rootView){
        goToStudyBtn= rootView.findViewById(R.id.goToStudy);
        goToStudyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogMaker dialog= new DialogMaker();
                DialogMaker.Callback pos_callback= new DialogMaker.Callback() {
                    @Override
                    public void callbackMethod() {
                        //move Activity
                        dialog.dismiss();
                        startActivity(new Intent(getActivity(), ExamActivity.class));
                    }
                };
                DialogMaker.Callback nag_callback= new DialogMaker.Callback() {
                    @Override
                    public void callbackMethod() {
                        dialog.dismiss();
                    }
                };
                dialog.setValue("문제 옵션 선택", "확인", "취소", pos_callback, nag_callback);
                dialog.show(getActivity().getSupportFragmentManager(), "Option Select!");
            }
        });

        menuListBtn= rootView.findViewById(R.id.menuList);
        menuListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        calendarBtn= rootView.findViewById(R.id.calender);
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        checkHistoryBtn= rootView.findViewById(R.id.checkHistory);
        checkHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
