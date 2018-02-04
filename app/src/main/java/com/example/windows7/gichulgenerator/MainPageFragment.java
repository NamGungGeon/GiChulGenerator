package com.example.windows7.gichulgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class MainPageFragment extends Fragment implements OnBackPressedListener{
    private Button goToStudyBtn;
    private Button searchExamBtn;

    private ImageView menuListBtn;
    private ImageView calendarBtn;
    private ImageView checkHistoryBtn;
    private ImageView helpBtn;

    private Spinner subjectSpinner;
    private Spinner probabilitySpinner;
    private Spinner instituteSpinner;
    private Spinner periodSpinner;

    private TextView yesterdayInfo;
    private TextView todayInfo;
    private TextView schedular;

    private AdView adView;

    private LinearLayout menuList;
    private boolean isOpenedMenuList= false;
    private Button menu_notification;
    private Button menu_allDeleteHistory;
    private Button menu_qna;
    private Button menu_freeBoard;
    private Button menu_donation;
    private Button menu_devInfo;


    private RelativeLayout mainContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_mainmenu, container, false);
        init(rootView);

        return rootView;
    }

    private void init(ViewGroup rootView){
        menuList= rootView.findViewById(R.id.menuList);
        // Prevent to call closeMenuList() from listener of mainContainer
        menuList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        menu_notification= rootView.findViewById(R.id.menuList_notification);
        resizeMenuListElement(menu_notification);
        menu_allDeleteHistory= rootView.findViewById(R.id.menuList_allHistoryDelete);
        resizeMenuListElement(menu_allDeleteHistory);
        menu_qna= rootView.findViewById(R.id.menuList_qna);
        resizeMenuListElement(menu_qna);
        menu_freeBoard= rootView.findViewById(R.id.menuList_freeBoard);
        resizeMenuListElement(menu_freeBoard);
        menu_donation= rootView.findViewById(R.id.menuList_donation);
        resizeMenuListElement(menu_donation);
        menu_devInfo= rootView.findViewById(R.id.menuList_devInfo);
        resizeMenuListElement(menu_devInfo);


        mainContainer= rootView.findViewById(R.id.mainMenuContainer);
        mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpenedMenuList== true){
                    closeMenuList();
                }
            }
        });


        todayInfo= rootView.findViewById(R.id.todayInfo);
        todayInfo.setText("오늘 0문제를 푸셨습니다");

        yesterdayInfo= rootView.findViewById(R.id.yesterdayInfo);
        yesterdayInfo.setText("어제 0문제를 푸셨습니다");

        schedular= rootView.findViewById(R.id.scheduler);
        schedular.setText("수능 D-300");

        goToStudyBtn= rootView.findViewById(R.id.goToStudy);
        goToStudyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToStudy();
            }
        });

        searchExamBtn= rootView.findViewById(R.id.searchExam);
        searchExamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchExam();
            }
        });

        menuListBtn= rootView.findViewById(R.id.menuListBtn);
        menuListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpenedMenuList==false){
                    openMenuList();
                }else{
                    closeMenuList();
                }
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

        helpBtn= rootView.findViewById(R.id.help);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogMaker dialog= new DialogMaker();
                DialogMaker.Callback pos_callback= new DialogMaker.Callback() {
                    @Override
                    public void callbackMethod() {
                        dialog.dismiss();
                    }
                };
                View childView= getActivity().getLayoutInflater().inflate(R.layout.dialog_help, null);
                dialog.setValue("도움말", "확인", "", pos_callback, null, childView);
                dialog.show(getActivity().getSupportFragmentManager(), "Open Help Dialog");
            }
        });

        adView= rootView.findViewById(R.id.mainPageAd);
        AdRequest adRequest= new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void resizeMenuListElement(View view){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = getActivity().getWindowManager().getDefaultDisplay().getWidth()/3;
        params.width= params.height;
        view.setLayoutParams(params);
        view.requestLayout();
    }

    private void openMenuList(){
        menuList.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.appear_menulist));
        menuList.setVisibility(View.VISIBLE);
        isOpenedMenuList= true;
    }

    private void closeMenuList(){
        menuList.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.disappear_menulist));
        menuList.setVisibility(View.GONE);
        isOpenedMenuList= false;

    }

    private void searchExam(){
        final DialogMaker dialog= new DialogMaker();
        dialog.setValue("문제 검색", "검색", "취소",
                new DialogMaker.Callback() {
                    @Override
                    public void callbackMethod() {
                        //start search

                    }
                },
                new DialogMaker.Callback() {
                    @Override
                    public void callbackMethod() {
                        //cancel

                    }
                });
    }

    private void goToStudy(){
        final DialogMaker dialog= new DialogMaker();
        DialogMaker.Callback pos_callback= new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                String subjectOption= (String)subjectSpinner.getSelectedItem();
                String probOption= (String)probabilitySpinner.getSelectedItem();
                String instOption= (String)instituteSpinner.getSelectedItem();
                String periodOption= (String)periodSpinner.getSelectedItem();

                //move Activity
                Intent intent= new Intent(getActivity(), ExamActivity.class);
                intent.putExtra("subj", subjectOption);
                intent.putExtra("prob", probOption);
                intent.putExtra("inst", instOption);
                intent.putExtra("peri", periodOption);

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
        periodSpinner= childView.findViewById(R.id.selectPeriod);
        periodSpinner.setSelection(0);

        dialog.setValue("문제 옵션 선택", "확인", "취소", pos_callback, nag_callback, childView);
        dialog.show(getActivity().getSupportFragmentManager(), "Option Select!");
    }


    @Override
    public boolean onBackPressed() {
        if(isOpenedMenuList== true){
            closeMenuList();
            return false;
        }else{
            return true;
        }
    }
}
