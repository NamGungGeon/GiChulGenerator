package com.satisfactoryplace.gichul.gichulgenerator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

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

public class MainPageFragment extends Fragment implements OnBackPressedListener, BillingProcessor.IBillingHandler{

    @BindView(R.id.main_statusPager) ViewPager statusPager;
    @BindView(R.id.status_back) ImageView status_back;
    @BindView(R.id.status_next) ImageView status_next;

    @BindView(R.id.scheduler) TextView d_day;
    @BindView(R.id.mainmenu_ad) AdView adView;

    private final int EXAM_ACTIVITY= 1335;
    private final int SEARCH_ACTIVITY= 1336;

    private Unbinder unbinder;
    private String appVersion= "2.4";

    BillingProcessor bp;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_mainmenu, container, false);
        unbinder= ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    private void init(){
        initDday();
        initStatusPager();
        initAdView();
        initBillProcess();
        checkAppVersion();
    }
    private void initAdView(){
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
    private void initStatusPager(){
        statusPager.setAdapter(new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch(position){
                    case 0:
                        return new TodayReportFragment();
                    case 1:
                        return new MonthReportFragment();
                    case 2:
                        return new TotalReportFragment();
                    default:
                        return null;
                }
            }
            @Override
            public int getCount() {
                return 3;
            }
        });

        statusPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0:
                        status_back.setVisibility(View.INVISIBLE);
                        status_next.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        status_back.setVisibility(View.VISIBLE);
                        status_next.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        status_back.setVisibility(View.VISIBLE);
                        status_next.setVisibility(View.INVISIBLE);
                        break;
                }
            }

        });
        statusPager.setCurrentItem(0);
        status_back.setVisibility(View.INVISIBLE);
        status_next.setVisibility(View.VISIBLE);
    }
    private void initDday(){
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

            Calendar sunungCalendar= Calendar.getInstance();
            sunungCalendar.setTimeInMillis(sunungDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        d_day.setText("수능까지 "+ between+ "일 남았습니다");
    }
    private void initBillProcess(){
        String key= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAorXcU15UsFjV0tml82MbjXsz9b0VKfIfDQOaJpJwSu/TOph9pr+pxKvGchF90E5C/3TF6pqsPqlEDA6/iSYBI1ka1wOCE1YPIt1vzFhG6rfY8hNPwz/pT+JQSA42FoW+N/v5Y4UN5FGxA0RFT1I/jSME2IU9fRFFnArdiMoq0HRKUzeo8f9txnoYgKme5ItuAmD6VU94ddpKyUXkJ83mKOvqLiYTs/PR2y99y9NTd/a2R5Gb6lgBpbjTR8vSvK+0zCFYRydSPNnN/krNJ5h+ne0raMXFYnCp5ZOFZ9cR1KzwfcaLYg7c6cthzb+FNqDew8qY6quT6j7RhU5opuJukwIDAQAB";
        bp= new BillingProcessor(getContext(), key, this);
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
    private void checkAppVersion(){
        if(appVersion.equals(AppData.currentVersion)== false){
            String message= "";
            message= "앱이 최신 버전이 아닙니다.\n업데이트가 필요합니다.\n\n";
            message+= "현재 설치된 앱 버전: "+ appVersion+ "\n";
            message+= "최신 앱 버전: "+ AppData.currentVersion;

            final DialogMaker dialog= new DialogMaker();
            dialog.setValue(message, "업데이트", "닫기", new DialogMaker.Callback() {
                @Override
                public void callbackMethod() {
                    String url= "https://play.google.com/store/apps/details?id=com.satisfactoryplace.gichul.gichulgenerator";
                    Intent intent= new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            }, null);
            dialog.show(getActivity().getSupportFragmentManager(), "GoUpdate");
        }

    }

    /* Listener List */
    @OnClick(R.id.status_next)
    void showNextStatus(){
        statusPager.setCurrentItem(statusPager.getCurrentItem()+1);
    }

    @OnClick(R.id.status_back)
    void showBackStatus(){
        statusPager.setCurrentItem(statusPager.getCurrentItem()-1);
    }

    @OnClick(R.id.historyListBtn)
    void openHistoryList(){
        startActivity(new Intent(getActivity(), HistoryListActivity.class));
    }

    @OnClick(R.id.checkListBtn)
    void openCheckList(){
        startActivity(new Intent(getActivity(), CheckListActivity.class));
    }

    @OnClick(R.id.menuList_allHistoryDelete)
    void deleteAllData(){
        final DialogMaker dialog= new DialogMaker();
        dialog.setValue("사용자의 모든 정보를 삭제하시겠습니까? (복구 불가)", "예", "아니오", new DialogMaker.Callback() {
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

    @OnClick(R.id.searchExam)
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

    @OnClick(R.id.goToStudy)
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
        bp.initialize();
        bp.purchase(getActivity(), "donation");
    }

    @OnClick(R.id.freeboard)
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

    @OnClick(R.id.goToExam)
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

    @OnClick(R.id.examResultBtn)
    void openExamResult(){
        Intent intent= new Intent(getContext(), ExamResultListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.help)
    void clickHelpBtn(){
        String url = "http://satisfactoryplace.tistory.com/47";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    /* Override from Fragment */
    @Override
    public void onDestroyView() {
        if (bp != null) {
            bp.release();
        }
        unbinder.unbind();
        super.onDestroyView();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        init();
    }
    @Override
    public boolean onBackPressed() {
        return true;
    }


    /* Implemented method from IBillingHandler */
    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Toast.makeText(getContext(), "기부해주셔서 감사합니다.\n더 나은 서비스를 제공하기 위해 노력하겠습니다.", Toast.LENGTH_SHORT).show();
        bp.consumePurchase(productId);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(getContext(), "결제 시스템에 에러가 발생하였습니다.\n에러코드: "+ errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {

    }
}
