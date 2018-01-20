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
import android.widget.Spinner;

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class MainPageFragment extends Fragment {
    private Button goToStudyBtn;
    private ImageView menuListBtn;
    private ImageView calendarBtn;
    private ImageView checkHistoryBtn;
    private Spinner subjectSpinner;
    private Spinner probabilitySpinner;
    private Spinner instituteSpinner;

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
                        String subjectOption= (String)subjectSpinner.getSelectedItem();
                        String probOption= (String)probabilitySpinner.getSelectedItem();
                        String instOption= (String)instituteSpinner.getSelectedItem();

                        //move Activity
                        Intent intent= new Intent(getActivity(), ExamActivity.class);
                        intent.putExtra("subj", subjectOption);
                        intent.putExtra("prob", probOption);
                        intent.putExtra("inst", instOption);

                        dialog.dismiss();
                        startActivity(intent);
                    }
                };
                DialogMaker.Callback nag_callback= new DialogMaker.Callback() {
                    @Override
                    public void callbackMethod() {
                        dialog.dismiss();
                    }
                };
                View childView= getLayoutInflater().inflate(R.layout.dialog_setfilter, null);
                subjectSpinner= childView.findViewById(R.id.selectSubject);
                subjectSpinner.setSelection(0);
                probabilitySpinner= childView.findViewById(R.id.selectProbability);
                probabilitySpinner.setSelection(0);
                instituteSpinner= childView.findViewById(R.id.selectInstitute);
                instituteSpinner.setSelection(0);

                dialog.setValue("문제 옵션 선택", "확인", "취소", pos_callback, nag_callback, childView);
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
                startActivity(new Intent(getActivity(), CalendarActivity.class));
            }
        });

        checkHistoryBtn= rootView.findViewById(R.id.checkHistory);
        checkHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CheckHistoryActivity.class));
            }
        });
    }
}
