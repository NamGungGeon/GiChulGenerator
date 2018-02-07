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

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class MainPageFragment extends Fragment implements OnBackPressedListener{
    //private AdView adView;

    @BindView(R.id.mainMenuContainer) RelativeLayout mainContainer;
    //Left Quick Menu
    @BindView(R.id.goToStudy) Button goToStudyBtn;
    @BindView(R.id.searchExam) Button searchExamBtn;
    @BindView(R.id.calender) Button calendarBtn;
    @BindView(R.id.checkHistory) Button checkHistoryBtn;

    // Right Status window
    @BindView(R.id.todayInfo) TextView todayInfo;
    @BindView(R.id.scheduler) TextView schedular;

    // Menu List
    private boolean isOpenedMenuList= false;
    @BindView(R.id.menuList) LinearLayout menuList;
    @BindView(R.id.menuList_notification) Button menu_notification;
    @BindView(R.id.menuList_allHistoryDelete) Button menu_allDeleteHistory;
    @BindView(R.id.menuList_qna) Button menu_qna;
    @BindView(R.id.menuList_freeBoard) Button menu_freeBoard;
    @BindView(R.id.menuList_donation) Button menu_donation;
    @BindView(R.id.menuList_devInfo) Button menu_devInfo;
    @BindView(R.id.menuList_goToStudy) Button menu_goToStudy;
    @BindView(R.id.menuList_searchExam) Button menu_searchExam;
    @BindView(R.id.menuList_changeBackground) Button menu_changeBackground;
    @BindView(R.id.menuList_checkList) Button menu_checkHistory;
    @BindView(R.id.menuList_calendar) Button menu_calendar;

    // Bottom Menu
    @BindView(R.id.menuListBtn) ImageView menuListBtn;
    @BindView(R.id.help) ImageView helpBtn;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_mainmenu, container, false);
        unbinder= ButterKnife.bind(this, rootView);
        init();

        return rootView;
    }

    private void init(){
        /*
        adView= rootView.findViewById(R.id.mainPageAd);
        AdRequest adRequest= new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        */
        resizeMenuListElements();

        int todayNumber= getActivity().getIntent().getIntExtra("todayExamNumber", 0);
        todayInfo.setText("오늘 "+ todayNumber+ "문제를 풀었습니다");

        String between= "";
        SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
        try {
            String sunung= getActivity().getIntent().getStringExtra("schedule");
            Date sunungDate= format.parse(sunung);
            long currentDate= Calendar.getInstance().getTime().getTime();
            long differentMills= sunungDate.getTime()- currentDate;
            long differentDays= differentMills/(1000*60*60*24);

            between= String.valueOf(differentDays);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        schedular.setText("2018학년도 수능까지 \n\n"+ between+"일 남았습니다");

        // Prevent to call closeMenuList() from listener of mainContainer
        mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpenedMenuList== true){
                    closeMenuList();
                }
            }
        });
        menuList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
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
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CalendarActivity.class));
            }
        });

        checkHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CheckHistoryActivity.class));
            }
        });

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
    }

    private void resizeMenuListElements(){
        resizeMenuListElement(menu_notification);
        resizeMenuListElement(menu_allDeleteHistory);
        resizeMenuListElement(menu_qna);
        resizeMenuListElement(menu_freeBoard);
        resizeMenuListElement(menu_donation);
        resizeMenuListElement(menu_devInfo);
        resizeMenuListElement(menu_goToStudy);
        resizeMenuListElement(menu_searchExam);
        resizeMenuListElement(menu_changeBackground);
        resizeMenuListElement(menu_checkHistory);
        resizeMenuListElement(menu_calendar);
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

    @OnClick({R.id.searchExam, R.id.menuList_searchExam})
    void searchExam(){
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

    @OnClick({R.id.goToStudy, R.id.menuList_goToStudy})
    void goToStudy(){
        final DialogMaker dialog= new DialogMaker();
        View childView= getLayoutInflater().inflate(R.layout.dialog_setfilter, null);

        final Spinner subjectSpinner= childView.findViewById(R.id.selectSubject);
        final Spinner probabilitySpinner= childView.findViewById(R.id.selectProbability);
        final Spinner instituteSpinner= childView.findViewById(R.id.selectInstitute);
        final Spinner periodSpinner= childView.findViewById(R.id.selectPeriod);

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
        subjectSpinner.setSelection(0);
        probabilitySpinner.setSelection(0);
        instituteSpinner.setSelection(0);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int todayNumber= HistoryList.getInstance().getTodayHistoryNumber();
        todayInfo.setText("오늘 "+ todayNumber+ "문제를 풀었습니다");

    }
}
