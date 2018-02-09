package com.example.windows7.gichulgenerator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    @BindView(R.id.checkListBtn) Button checkListBtn;
    @BindView(R.id.historyListBtn) Button historyListBtn;

    // Right Status window
    @BindView(R.id.todayInfo) TextView todayInfo;
    @BindView(R.id.scheduler) TextView schedular;
    @BindView(R.id.monthInfo) TextView monthInfo;
    @BindView(R.id.subjectInfo) TextView subjectInfo;

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
    @BindView(R.id.menuList_checkList) Button menu_checkListBtn;
    @BindView(R.id.menuList_calendar) Button menu_calendar;
    @BindView(R.id.menuList_historyList) Button menu_historyList;

    // Bottom Menu
    @BindView(R.id.menuListBtn) ImageView menuListBtn;
    @BindView(R.id.help) ImageView helpBtn;

    private final int EXAM_ACTIVITY= 1335;
    private final int SEARCH_ACTIVITY= 1336;

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
        setBackground();
        resizeMenuListElements();

        //Set TodayInfo
        String todayMessage= "";
        int todayNumber= HistoryList.getInstance().getTodayHistoryNumber();
        todayMessage+= "오늘 "+ todayNumber+ "문제를 풀었습니다";
        todayMessage+= "\n\n오늘 나의 정답률은 "+HistoryList.getInstance().getTodayPotential()+ "% 입니다";
        todayInfo.setText(todayMessage);

        //Set MonthInfo
        String monthMessage= "";
        int monthNumber= HistoryList.getInstance().getMonthHistoryNumber();
        monthMessage+= "이번 달에 "+ monthNumber+ "문제를 풀었습니다";
        monthMessage+= "\n\n이번 달 나의 정답률은 "+ HistoryList.getInstance().getMonthPotential()+ "% 입니다";
        monthInfo.setText(monthMessage);

        //Set SubjectInfo
        String subjectMessage= "과목별 전체 정답률\n\n";
        int koreanNumber= HistoryList.getInstance().getSubjectPotential("korean");
        subjectMessage+= "국어 정답률 "+ koreanNumber+ "%\n";
        int mathNumber= HistoryList.getInstance().getSubjectPotential("imath")+ HistoryList.getInstance().getSubjectPotential("mmath");
        subjectMessage+= "수학 정답률 "+ mathNumber+ "%\n";
        int englishNumber= HistoryList.getInstance().getSubjectPotential("english");
        subjectMessage+= "영어 정답률 "+ englishNumber+ "%\n";
        int socialNumber= HistoryList.getInstance().getSubjectPotential("social");
        subjectMessage+= "사회탐구 정답률 "+ socialNumber+ "%\n";
        int scienceNumber= HistoryList.getInstance().getSubjectPotential("science");
        subjectMessage+= "과학탐구 정답률 "+ scienceNumber+ "%";
        subjectInfo.setText(subjectMessage);

        //Set Schedule
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
        schedular.setText("2019학년도 수능까지 \n\n"+ between+"일 남았습니다");

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

    private void setBackground(){
        int width= getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int height= getActivity().getWindowManager().getDefaultDisplay().getHeight();

        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper);
        bitmap= Bitmap.createScaledBitmap(bitmap, width, height, true);

        BitmapDrawable background= new BitmapDrawable(bitmap);

        mainContainer.setBackgroundDrawable(background);
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
        resizeMenuListElement(menu_checkListBtn);
        resizeMenuListElement(menu_calendar);
        resizeMenuListElement(menu_historyList);
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

    @OnClick({R.id.historyListBtn, R.id.menuList_historyList})
    void openHistoryList(){
        startActivity(new Intent(getActivity(), HistoryListActivity.class));
    }

    @OnClick({R.id.checkListBtn, R.id.menuList_checkList})
    void openCheckList(){
        startActivity(new Intent(getActivity(), CheckListActivity.class));
    }

    @OnClick(R.id.menuList_allHistoryDelete)
    void deleteAllData(){
        final DialogMaker dialog= new DialogMaker();
        dialog.setValue("오답노트와 문제 기록을 전부 삭제하시겠습니까?\n(복구 불가능)", "예", "아니오", new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                CheckList.getInstance().deleteAllData();
                HistoryList.getInstance().deleteAllData();
                init();
                dialog.dismiss();
            }
        }, null);
        dialog.show(getActivity().getSupportFragmentManager(), "");
    }

    @OnClick({R.id.searchExam, R.id.menuList_searchExam})
    void searchExam(){
        final DialogMaker dialog= new DialogMaker();
        View childView= getActivity().getLayoutInflater().inflate(R.layout.dialog_search, null);

        final Spinner subject= childView.findViewById(R.id.searchSubject);
        final Spinner institute= childView.findViewById(R.id.searchInstitute);
        final Spinner period_y= childView.findViewById(R.id.searchPeriod_y);
        final Spinner period_m= childView.findViewById(R.id.searchPeriod_m);
        final Spinner number= childView.findViewById(R.id.searchNumber);
        subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<String> adapter;

                if(subject.getSelectedItem().equals("수학(이과)") || institute.getSelectedItem().equals("수학(문과)")){
                    ArrayList<String> numbers= new ArrayList<>();
                    for(int k=0; k<30; k++){
                        numbers.add(String.valueOf(k+1));
                    }
                    adapter=  new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, numbers);
                    number.setAdapter(adapter);
                }else if(subject.getSelectedItem().equals("국어")){
                    ArrayList<String> numbers= new ArrayList<>();
                    for(int k=0; k<45; k++){
                        numbers.add(String.valueOf(k+1));
                    }
                    adapter=  new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, numbers);
                    number.setAdapter(adapter);
                }else if(subject.getSelectedItem().equals("영어")){
                    ArrayList<String> numbers= new ArrayList<>();
                    for(int k=0; k<45; k++){
                        numbers.add(String.valueOf(k+1));
                    }
                    adapter=  new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, numbers);
                    number.setAdapter(adapter);
                }else if(subject.getSelectedItem().equals("과학탐구")){
                    ArrayList<String> numbers= new ArrayList<>();
                    for(int k=0; k<20; k++){
                        numbers.add(String.valueOf(k+1));
                    }
                    adapter=  new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, numbers);
                    number.setAdapter(adapter);
                }else if(subject.getSelectedItem().equals("사회탐구")){
                    ArrayList<String> numbers= new ArrayList<>();
                    for(int k=0; k<20; k++){
                        numbers.add(String.valueOf(k+1));
                    }
                    adapter=  new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, numbers);
                    number.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        institute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<String> adapter;

                if(institute.getSelectedItem().equals("대학수학능력평가시험")){
                    String list[]= {"11"};
                    adapter=  new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, list);
                    period_m.setAdapter(adapter);
                }else if(institute.getSelectedItem().equals("교육청")){
                    String list[]= {"3", "4", "7", "10"};
                    adapter=  new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, list);
                    period_m.setAdapter(adapter);
                }else if(institute.getSelectedItem().equals("교육과정평가원")){
                    String list[]= {"6", "9"};
                    adapter=  new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, list);
                    period_m.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dialog.setValue("문제 검색", "검색", "취소",
                new DialogMaker.Callback() {
                    @Override
                    public void callbackMethod() {
                        //start search
                        String basicFileName= "";

                        //Decide period_y
                        String filter_period_y= period_y.getSelectedItem().toString();
                        if(filter_period_y.equals("2017")){
                            basicFileName+= "2017";
                        }else if(filter_period_y.equals("2016")){
                            basicFileName+= "2016";
                        }else if(filter_period_y.equals("2015")){
                            basicFileName+= "2015";
                        }else if(filter_period_y.equals("2014")){
                            basicFileName+= "2014";
                        }else if(filter_period_y.equals("2013")){
                            basicFileName+= "2013";
                        }else if(filter_period_y.equals("2012")){
                            basicFileName+= "2012";
                        }
                        basicFileName+= "_";

                        //Decide period_m
                        String filter_period_m= period_m.getSelectedItem().toString();
                        basicFileName+= filter_period_m;
                        basicFileName+= "_";

                        //Decide institute
                        String filter_institute= institute.getSelectedItem().toString();
                        if(filter_institute.equals("대학수학능력평가시험")){
                            basicFileName+= "sunung";
                        }else if(filter_institute.equals("교육청")){
                            basicFileName+= "gyoyuk";
                        }else if(filter_institute.equals("교육과정평가원")){
                            basicFileName+= "pyeong";
                        }
                        basicFileName+="_";

                        // Decide Subject
                        String filter_subj= subject.getSelectedItem().toString();
                        if (filter_subj.equals("수학(이과)")) {
                            basicFileName+= "imath";
                        }else if (filter_subj.equals("수학(문과)")) {
                            basicFileName+= "mmath";
                        }else if (filter_subj.equals("국어")) {
                            basicFileName+= "korean";
                        }else if (filter_subj.equals("영어")) {
                            basicFileName+= "english";
                        }else if (filter_subj.equals("사회탐구")) {
                            basicFileName+= "social";
                        }else if (filter_subj.equals("과학탐구")) {
                            basicFileName+= "science";
                        }

                        //Decide Number
                        String filter_number= number.getSelectedItem().toString();

                        //Go to result page
                        Intent intent= new Intent(getActivity().getApplicationContext(), SearchResultActivity.class);
                        intent.putExtra("period_y", filter_period_y);
                        intent.putExtra("period_m", filter_period_m);
                        intent.putExtra("institute", filter_institute);
                        intent.putExtra("subject", filter_subj);
                        intent.putExtra("number", filter_number);
                        // basicFileName is not include "Number"
                        intent.putExtra("basicFileName", basicFileName);

                        startActivityForResult(intent, SEARCH_ACTIVITY);
                        dialog.dismiss();
                    }
                }, null, childView);
        dialog.show(getActivity().getSupportFragmentManager(), "");
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
                startActivityForResult(intent, EXAM_ACTIVITY);
            }
        };

        dialog.setValue("문제 옵션 선택", "확인", "취소", pos_callback, null, childView);
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
        init();
    }
}
