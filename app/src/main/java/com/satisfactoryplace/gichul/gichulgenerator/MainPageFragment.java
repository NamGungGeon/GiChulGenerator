package com.satisfactoryplace.gichul.gichulgenerator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class MainPageFragment extends Fragment implements OnBackPressedListener{

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
    @BindView(R.id.mainmenu_univImage) ImageView univImage;

    // Menu List
    private boolean isOpenedMenuList= false;
    @BindView(R.id.menuList) LinearLayout menuList;
    @BindView(R.id.menuList_allHistoryDelete) ImageView menu_allDeleteHistory;
    @BindView(R.id.menuList_qna) ImageView menu_qna;
    @BindView(R.id.menuList_freeBoard) ImageView menu_freeBoard;
    @BindView(R.id.menuList_donation) ImageView menu_donation;
    @BindView(R.id.menuList_devInfo) ImageView menu_devInfo;
    @BindView(R.id.menuList_goToStudy) ImageView menu_goToStudy;
    @BindView(R.id.menuList_searchExam) ImageView menu_searchExam;
    @BindView(R.id.menuList_changeBackground) ImageView menu_changeBackground;
    @BindView(R.id.menuList_checkList) ImageView menu_checkListBtn;
    @BindView(R.id.menuList_historyList) ImageView menu_historyList;
    @BindView(R.id.menuList_checkAppVersion) ImageView menu_checkAppVersion;
    @BindView(R.id.menuList_examResult) ImageView menu_examResult;
    @BindView(R.id.menuList_goExam) ImageView menu_goExam;

    // Bottom Menu
    @BindView(R.id.menuListBtn) ImageView menuListBtn;
    @BindView(R.id.help) ImageView helpBtn;

    private final int EXAM_ACTIVITY= 1335;
    private final int SEARCH_ACTIVITY= 1336;

    private Unbinder unbinder;
    private String appVersion= "1.3";

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_mainmenu, container, false);
        unbinder= ButterKnife.bind(this, rootView);

        init();
        return rootView;
    }

    private void init(){
        setBackground();
        setMyGoal();
        resizeMenuListElements();

        setTodayReport();
        setMonthReport();
        setTotalReport();

        //Set Schedule
        String between= "";
        String year= "";
        SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
        try {
            String sunung= getActivity().getIntent().getStringExtra("schedule");
            Date sunungDate= format.parse(sunung);

            long currentDate= Calendar.getInstance().getTime().getTime();
            long differentMills= sunungDate.getTime()- currentDate;
            long differentDays= differentMills/(1000*60*60*24);
            between= String.valueOf(differentDays);

            Calendar sunungCalendar= Calendar.getInstance();
            sunungCalendar.setTimeInMillis(sunungDate.getTime());
            year+= sunungCalendar.get(Calendar.YEAR)+ 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }


        schedular.setText(year+ "학년도 수능까지 \n\n"+ between+"일 남았습니다");

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
                String url = "http://satisfactoryplace.tistory.com/47";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void setTodayReport(){
        HistoryList historyList= HistoryList.getInstance();

        String todayMessage= "오늘 요약\n\n";
        int todayNumber= HistoryList.getInstance().getTodayHistoryNumber();
        todayMessage+= "오늘 "+ todayNumber+ "문제를 풀었습니다\n";
        todayMessage+= "오늘 나의 정답률은 "+HistoryList.getInstance().getTodayPotential()+ "% 입니다\n\n";

        todayMessage+= "수학(문과) "+ historyList.getTodaySubjectNumber("mmath")+ "문제 중 정답률 "+ historyList.getTodaySubjectPotential("mmath")+ "%\n";
        todayMessage+= "수학(이과) "+ historyList.getTodaySubjectNumber("imath")+ "문제 중 정답률 "+ historyList.getTodaySubjectPotential("imath")+ "%";
        todayInfo.setText(todayMessage);
    }

    private void setMonthReport(){
        HistoryList historyList= HistoryList.getInstance();

        String monthMessage= "이번 달 요약\n\n";
        int monthNumber= HistoryList.getInstance().getMonthHistoryNumber();
        monthMessage+= "이번 달에 "+ monthNumber+ "문제를 풀었습니다\n";
        monthMessage+= "이번 달 나의 정답률은 "+ HistoryList.getInstance().getMonthPotential()+ "% 입니다\n\n";

        monthMessage+= "수학(문과) "+ historyList.getMonthSubjectNumber("mmath")+"문제 중 정답률 "+ historyList.getMonthSubjectPotential("mmath")+ "%\n";
        monthMessage+= "수학(이과) "+ historyList.getMonthSubjectNumber("imath")+ "문제 중 정답률 "+ historyList.getMonthSubjectPotential("imath")+ "%";
        monthInfo.setText(monthMessage);
    }

    private void setTotalReport(){
        HistoryList historyList= HistoryList.getInstance();

        String totalMessage= "전체 요약\n\n";
        int totalNumber= HistoryList.getInstance().getTotalNumber();
        totalMessage+= "전체 "+ totalNumber+ "문제를 풀었습니다\n";
        totalMessage+= "전체 나의 정답률은 "+ HistoryList.getInstance().getTotalPotential()+ "% 입니다\n\n";

        totalMessage+= "수학(문과) "+ historyList.getTotalSubjectNumber("mmath")+"문제 중 정답률 "+ historyList.getTotalSubjectPotential("mmath")+ "%\n";
        totalMessage+= "수학(이과) "+ historyList.getTotalSubjectNumber("imath")+ "문제 중 정답률 "+ historyList.getTotalSubjectPotential("imath")+ "%";
        subjectInfo.setText(totalMessage);
    }

    private void setBackground(){
        int width= getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int height= getActivity().getWindowManager().getDefaultDisplay().getHeight();
        Bitmap bitmap;

        String backgroundPath= getActivity().getSharedPreferences("background", MODE_PRIVATE).getString("path", "");
        if(backgroundPath.equals("")){
            //Not set background
            bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper);
        }else{
            File backgroundFile= new File(backgroundPath);
            if(backgroundFile== null || backgroundFile.exists()== false){
                //No Exist File
                bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper);
            }else{
                bitmap= BitmapFactory.decodeFile(backgroundPath);
            }
        }
        bitmap= Bitmap.createScaledBitmap(bitmap, width, height, true);
        BitmapDrawable background= new BitmapDrawable(bitmap);

        mainContainer.setBackgroundDrawable(background);
    }

    private void resizeMenuListElements(){
        resizeMenuListElement(menu_allDeleteHistory);
        resizeMenuListElement(menu_qna);
        resizeMenuListElement(menu_freeBoard);
        resizeMenuListElement(menu_donation);
        resizeMenuListElement(menu_devInfo);
        resizeMenuListElement(menu_goToStudy);
        resizeMenuListElement(menu_searchExam);
        resizeMenuListElement(menu_changeBackground);
        resizeMenuListElement(menu_checkListBtn);
        resizeMenuListElement(menu_historyList);
        resizeMenuListElement(menu_checkAppVersion);
        resizeMenuListElement(menu_goExam);
        resizeMenuListElement(menu_examResult);
    }

    //Only used in resizedMenuListElements
    private void resizeMenuListElement(ImageView view){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = getActivity().getWindowManager().getDefaultDisplay().getWidth()/3;
        params.width= params.height;
        view.setLayoutParams(params);
        view.requestLayout();

        Bitmap bitmap= ((BitmapDrawable)(view.getBackground())).getBitmap();
        if(bitmap!= null && bitmap.isRecycled()== false){
            Bitmap resizedBitmap= Bitmap.createScaledBitmap(bitmap, params.width, params.height, false);
            BitmapDrawable bitmapDrawable= new BitmapDrawable(resizedBitmap);
            view.setBackground(bitmapDrawable);
        }
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

    void setMyGoal(){
        int width= 128;
        int height= 128;
        Bitmap bitmap;

        String imagePath= getActivity().getSharedPreferences("goal", MODE_PRIVATE).getString("path", "");
        if(imagePath.equals("")){
            //Not set background
            bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.konkuk);
        }else{
            File imageFile= new File(imagePath);
            if(imageFile== null || imageFile.exists()== false){
                //No Exist File
                bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.konkuk);
            }else{
                bitmap= BitmapFactory.decodeFile(imagePath);
            }
        }
        bitmap= Bitmap.createScaledBitmap(bitmap, width, height, true);
        BitmapDrawable image= new BitmapDrawable(bitmap);

        univImage.setBackgroundDrawable(image);
    }

    private boolean checkNickName(String nickName){
        if(nickName!= null){
            //Check Length
            if(nickName.length()>= 2 && nickName.length()<= 12){
                if(nickName.contains(" ") || nickName.contains("\n")){
                    Toast.makeText(getContext(), "공백과 개행은 사용 불가능합니다.", Toast.LENGTH_SHORT).show();
                    return false;
                }

                //Literal Check
                boolean isCanUsingNickName= true;
                for(int i=0; i<nickName.length(); i++){
                    if(nickName.charAt(i)>= 'a' && nickName.charAt(i)<= 'z'){

                    }else if(nickName.charAt(i)>= 'A' && nickName.charAt(i)<= 'Z'){

                    }else if(nickName.charAt(i)>= '가' && nickName.charAt(i)<= '힣'){

                    }else{
                        //Incldue literal Not allowed
                        isCanUsingNickName= false;
                    }
                }

                if(isCanUsingNickName== false){
                    Toast.makeText(getContext(), "닉네임에는 한국어와 영어만 사용 가능합니다\n(한국어 자음/모음 단독사용 불가)", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if(nickName.equals("관리자")){
                    Toast.makeText(getContext(), "해당 닉네임은 사용 불가능합니다.", Toast.LENGTH_SHORT).show();
                    return false;
                }else {
                    return true;
                }
            }else{
                Toast.makeText(getContext(), "닉네임은 2자 이상, 12자 이하로 설정해야 합니다.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(getContext(), "사용할 닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private int checkPermission(){
        if (android.os.Build.VERSION.SDK_INT < 23) {
            //not need permission
            return 1;
        }

        return ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void getPermission(){
        //권한이 부여되어 있는지 확인
        int permissonCheck= checkPermission();

        if(permissonCheck == PackageManager.PERMISSION_GRANTED){
            //Permission Granted
        }else{
            Toast.makeText(getContext(), "이 권한이 없으면 이미지 등록이 불가능합니다.", Toast.LENGTH_SHORT).show();

            //권한설정 dialog에서 거부를 누르면
            //ActivityCompat.shouldShowRequestPermissionRationale 메소드의 반환값이 true가 된다.
            //단, 사용자가 "Don't ask again"을 체크한 경우
            //거부하더라도 false를 반환하여, 직접 사용자가 권한을 부여하지 않는 이상, 권한을 요청할 수 없게 된다.
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                //이곳에 권한이 왜 필요한지 설명하는 Toast나 dialog를 띄워준 후, 다시 권한을 요청한다.
                Toast.makeText(getContext(), "이 권한이 없으면 이미지 등록이 불가능합니다.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            }else{
                Toast.makeText(getContext(), "파일 읽기 권한 있음", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE},123);
            }
        }
    }

    private boolean isValidPeriod(String period_y, String period_m){
        int y= Integer.valueOf(period_y);
        int m= Integer.valueOf(period_m);
        if(y> 2018 || y< 2015){
            return false;
        }
        if(m> 11 || m<3){
            return false;
        }

        // 이 버전에서는 2018년 4월 시험까지 지원
        if(y== 2018 && m>4){
            return false;
        }

        return true;
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
        switch (requestCode){
            case 123:
                //change background image
                if (null != data) {
                    Cursor cursor = getActivity().getContentResolver().query(data.getData(), null, null, null, null );
                    cursor.moveToNext();
                    String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
                    cursor.close();

                    //save
                    SharedPreferences pref = getActivity().getSharedPreferences("background", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("path", path);
                    editor.commit();

                    setBackground();
                }
                break;
            case 155:
                //change univ image
                if (null != data) {
                    Cursor cursor = getActivity().getContentResolver().query(data.getData(), null, null, null, null );
                    cursor.moveToNext();
                    String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
                    cursor.close();

                    //save
                    SharedPreferences pref = getActivity().getSharedPreferences("goal", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("path", path);
                    editor.commit();

                    setMyGoal();
                }
                break;
            default:
                init();
                break;
        }
    }

    /* Listener List */
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
        dialog.setValue("오답노트/문제기록/시험결과를 전부 삭제하시겠습니까?\n(복구 불가능)", "예", "아니오", new DialogMaker.Callback() {
            @Override
            public void callbackMethod() {
                CheckList.getInstance().deleteAllData();
                HistoryList.getInstance().deleteAllData();
                ExamResultList.getInstance().deleteAllData();
                Toast.makeText(getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
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
                        if(isValidPeriod(period_y.getSelectedItem().toString(), period_m.getSelectedItem().toString())== false){
                            Toast.makeText(getContext(), "2018년 4월 시험까지만 지원됩니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //start search
                        String basicFileName= "";

                        //Decide period_y
                        String filter_period_y= period_y.getSelectedItem().toString();
                        if(filter_period_y.equals("2018")){
                            basicFileName+= "2018";
                        }
                        if(filter_period_y.equals("2017")){
                            basicFileName+= "2017";
                        }else if(filter_period_y.equals("2016")){
                            basicFileName+= "2016";
                        }else if(filter_period_y.equals("2015")){
                            basicFileName+= "2015";
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
    void goRandomQuestion(){
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

                if(periodOption.equals("2018")){
                    Toast.makeText(getContext(), "2018년 4월 교육청 시험까지 지원됩니다.", Toast.LENGTH_SHORT).show();
                    if(instOption.equals("교육청")==false && instOption.equals("상관없음")== false){
                        return;
                    }
                }
                //move Activity
                Intent intent= new Intent(getActivity(), RandomQuestionActivity.class);
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

    @OnClick(R.id.menuList_changeBackground)
    void changeBackground(){
        if(checkPermission()== 1 || checkPermission()== PackageManager.PERMISSION_GRANTED){
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent ,123 );
        }else{
            getPermission();
        }
    }

    @OnClick(R.id.menuList_devInfo)
    void devInfo(){
        final DialogMaker dialog= new DialogMaker();
        View childView= getActivity().getLayoutInflater().inflate(R.layout.dialog_devinfo, null);
        final TextView blogLink= childView.findViewById(R.id.devInfo_blogLink);
        blogLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = blogLink.getText().toString();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        dialog.setValue(null, null, null, null, null, childView);
        dialog.show(getActivity().getSupportFragmentManager(), "Open Developer Information");
    }

    @OnClick(R.id.menuList_donation)
    void donation(){
        Toast.makeText(getContext(), "미구현 기능입니다", Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.menuList_freeBoard, R.id.freeboard})
    void openFreeboard(){
        if(AppData.freeboardStatus== null || AppData.freeboardStatus.equals("close")){
            Toast.makeText(getContext(), "죄송합니다. 게시판 점검 중입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(Status.canUseFreeboard== false){
            Toast.makeText(getContext(), "관리자에 의해 자유게시판 이용이 정지되었습니다.", Toast.LENGTH_SHORT).show();
        }else{
            if(Status.nickName== null){
                final DialogMaker dialog= new DialogMaker();
                final View childView= getLayoutInflater().inflate(R.layout.dialog_setnickname, null);
                dialog.setValue("", "설정", "취소",
                        new DialogMaker.Callback() {
                            @Override
                            public void callbackMethod() {
                                final EditText inputNickname= childView.findViewById(R.id.setNickName_nickName);
                                if(checkNickName(inputNickname.getText().toString())== true){
                                    final ProgressDialog progressDialog= DialogMaker.showProgressDialog(getActivity(), "", "닉네임 설정 중입니다");
                                    //Check if nickname is duplicated
                                    FirebaseConnection.getInstance().loadData("nickNames/", new FirebaseConnection.Callback() {
                                                @Override
                                                public void success(DataSnapshot snapshot) {
                                                    if(snapshot.getValue()!= null){
                                                        //Case: Duplicated
                                                        HashMap<String, String> loadedNicknames= ((HashMap<String, String>)snapshot.getValue());
                                                        for(String value: loadedNicknames.keySet()){
                                                            if(loadedNicknames.get(value).equals(inputNickname.getText().toString())){
                                                                Toast.makeText(getContext(), "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                                return;
                                                            }
                                                        }
                                                    }

                                                    //Case: Not Duplicated
                                                    Status.nickName= inputNickname.getText().toString();

                                                    //Save...
                                                    FirebaseConnection.getInstance().getReference("nickNames").push().setValue(inputNickname.getText().toString());
                                                    FirebaseConnection.getInstance().getReference("userdata/"+ FirebaseAuth.getInstance().getUid()+ "/status/nickName/")
                                                            .setValue(inputNickname.getText().toString());
                                                    Toast.makeText(getContext(), inputNickname.getText().toString()+ "(으)로 설정되었습니다.", Toast.LENGTH_SHORT).show();

                                                    progressDialog.dismiss();
                                                    dialog.dismiss();
                                                    startActivity(new Intent(getContext(), FreeboardActivity.class));
                                                }
                                                @Override
                                                public void fail(String errorMessage) {
                                                    Toast.makeText(getContext(), "데이터베이스 통신 실패\n"+ errorMessage, Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            }
                                    );
                                }
                            }
                        }, null, childView);
                dialog.show(getActivity().getSupportFragmentManager(), "set NickName");
            }else{
                startActivity(new Intent(getContext(), FreeboardActivity.class));
            }
        }
    }

    @OnClick({R.id.menuList_qna, R.id.qna})
    void openQna(){
        if(AppData.qnaStatus== null || AppData.qnaStatus.equals("close")){
            Toast.makeText(getContext(), "죄송합니다. 게시판 점검 중입니다.", Toast.LENGTH_SHORT).show();
            return;
        }


        if(Status.canUseQna== false){
            Toast.makeText(getContext(), "관리자에 의해 질문과 답변 게시판 이용이 정지되었습니다.", Toast.LENGTH_SHORT).show();
        }else{
            if(Status.nickName== null){
                DialogMaker dialog= new DialogMaker();
                final View childView= getLayoutInflater().inflate(R.layout.dialog_setnickname, null);
                dialog.setValue("", "설정", "취소",
                        new DialogMaker.Callback() {
                            @Override
                            public void callbackMethod() {
                                EditText inputNickname= childView.findViewById(R.id.setNickName_nickName);
                                if(checkNickName(inputNickname.getText().toString())){
                                    //Success to set
                                    Status.nickName= inputNickname.getText().toString();
                                    startActivity(new Intent(getContext(), QnaBoardActivity.class));
                                }
                            }
                        }, null, childView);
                dialog.show(getActivity().getSupportFragmentManager(), "set NickName");
            }else{
                startActivity(new Intent(getContext(), QnaBoardActivity.class));
            }
        }
    }

    @OnClick(R.id.menuList_checkAppVersion)
    void checkAppVersion(){
        String message;
        if(appVersion.equals(AppData.currentVersion)){
            message= "앱이 최신 버전입니다.";
        }else{
            message= "앱이 최신 버전이 아닙니다.\n업데이트가 필요합니다.\n\n";
            message+= "현재 설치된 앱 버전: "+ appVersion+ "\n";
            message+= "최신 앱 버전: "+ AppData.currentVersion;
        }
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.goToExam, R.id.menuList_goExam})
    void goToExam(){
        final DialogMaker dialog= new DialogMaker();
        View childView= getActivity().getLayoutInflater().inflate(R.layout.dialog_search, null);

        final Spinner subject= childView.findViewById(R.id.searchSubject);
        final Spinner institute= childView.findViewById(R.id.searchInstitute);
        final Spinner period_y= childView.findViewById(R.id.searchPeriod_y);
        final Spinner period_m= childView.findViewById(R.id.searchPeriod_m);
        final Spinner number= childView.findViewById(R.id.searchNumber);
        TextView numberExplain= childView.findViewById(R.id.searchNumber_text);

        //Not Need question's number
        number.setVisibility(View.GONE);
        numberExplain.setVisibility(View.GONE);

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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
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
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        dialog.setValue("문제 검색", "검색", "취소",
                new DialogMaker.Callback() {
                    @Override
                    public void callbackMethod() {
                        if(isValidPeriod(period_y.getSelectedItem().toString(), period_m.getSelectedItem().toString())== false){
                            Toast.makeText(getContext(), "2018년 4월 시험까지만 지원됩니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Intent intent= new Intent(getActivity().getApplicationContext(), ExamActivity.class);
                        String basicFileName= "";

                        //Decide period_y
                        String filter_period_y= period_y.getSelectedItem().toString();
                        if(filter_period_y.equals("2018")){
                            basicFileName+= "2018";
                        }else if(filter_period_y.equals("2017")){
                            basicFileName+= "2017";
                        }else if(filter_period_y.equals("2016")){
                            basicFileName+= "2016";
                        }else if(filter_period_y.equals("2015")){
                            basicFileName+= "2015";
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
                            intent.putExtra("encodedInstitute", "sunung");
                        }else if(filter_institute.equals("교육청")){
                            basicFileName+= "gyoyuk";
                            intent.putExtra("encodedInstitute", "gyoyuk");
                        }else if(filter_institute.equals("교육과정평가원")){
                            basicFileName+= "pyeong";
                            intent.putExtra("encodedInstitute", "pyeong");
                        }
                        basicFileName+="_";

                        // Decide Subject
                        String filter_subj= subject.getSelectedItem().toString();
                        if (filter_subj.equals("수학(이과)")) {
                            basicFileName+= "imath";
                            intent.putExtra("encodedSubject", "imath");
                        }else if (filter_subj.equals("수학(문과)")) {
                            basicFileName+= "mmath";
                            intent.putExtra("encodedSubject", "mmath");
                        }

                        //Go to result page
                        intent.putExtra("period_y", filter_period_y);
                        intent.putExtra("period_m", filter_period_m);
                        intent.putExtra("institute", filter_institute);
                        intent.putExtra("subject", filter_subj);

                        // In this case, basicFileName is not include "Number"
                        intent.putExtra("basicFileName", basicFileName);

                        startActivityForResult(intent, 1335);
                        dialog.dismiss();
                    }
                }, null, childView);
        dialog.show(getActivity().getSupportFragmentManager(), "");
    }

    @OnClick({R.id.examResultBtn, R.id.menuList_examResult})
    void openExamResult(){
        Intent intent= new Intent(getContext(), ExamResultListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.mainmenu_univImage)
    void changeMyGoal(){
        if(checkPermission()== PackageManager.PERMISSION_GRANTED){
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent ,155 );
        }else{
            getPermission();
        }
    }

}