package com.satisfactoryplace.gichul.gichulgenerator.fragment;

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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.satisfactoryplace.gichul.gichulgenerator.CheckListActivity;
import com.satisfactoryplace.gichul.gichulgenerator.ExamActivity;
import com.satisfactoryplace.gichul.gichulgenerator.data.AppDataKeeper;
import com.satisfactoryplace.gichul.gichulgenerator.utils.Common;
import com.satisfactoryplace.gichul.gichulgenerator.utils.ExamResultListUtil;
import com.satisfactoryplace.gichul.gichulgenerator.ExamResultListActivity;
import com.satisfactoryplace.gichul.gichulgenerator.data.ExamNameBuilder;
import com.satisfactoryplace.gichul.gichulgenerator.data.MonthReportGraphDefine;
import com.satisfactoryplace.gichul.gichulgenerator.data.QuestionNameBuilder;
import com.satisfactoryplace.gichul.gichulgenerator.HistoryListActivity;
import com.satisfactoryplace.gichul.gichulgenerator.data.Schedule;
import com.satisfactoryplace.gichul.gichulgenerator.data.TodayReportGraphDefine;
import com.satisfactoryplace.gichul.gichulgenerator.data.TotalReportGraphDefine;
import com.satisfactoryplace.gichul.gichulgenerator.model.OnBackPressedListener;
import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.RandomQuestionActivity;
import com.satisfactoryplace.gichul.gichulgenerator.SearchResultActivity;
import com.satisfactoryplace.gichul.gichulgenerator.model.AppData;
import com.satisfactoryplace.gichul.gichulgenerator.utils.CheckListUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.HistoryListUtil;
import com.satisfactoryplace.gichul.gichulgenerator.utils.DialogMaker;
import com.satisfactoryplace.gichul.gichulgenerator.utils.QuestionUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private String appVersion= "3.0";

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
        msgCheck();
    }
    private void msgCheck(){
        if(!AppDataKeeper.inst.msg.equals("")){
            DialogMaker dialog= new DialogMaker();
            dialog.setValue("알림사항\n\n"+ AppDataKeeper.inst.msg, "닫기", null, null, null);
            dialog.show(getFragmentManager(), "Notice!!");
        }
    }
    private void initAdView(){
        Common.initAdView(adView);
    }
    private void initStatusPager(){
        statusPager.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                GraphReportFragment gf= new GraphReportFragment();
                switch(position){
                    //일별 그래프
                    case 0:
                        gf.setGraphDefine(new TodayReportGraphDefine());
                        return gf;
                    //월별 문제풀이 분석
                    case 1:
                        gf.setGraphDefine(new MonthReportGraphDefine());
                        return gf;
                    //전체 문제풀이 분석
                    case 2:
                        gf.setGraphDefine(new TotalReportGraphDefine());
                        return gf;
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
            String sunung= Schedule.sunungDate;
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
        String key= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAorXcU15UsFjV0tml82MbjXsz9b0VKfIfDQOaJpJwSu/TOph9pr+pxKvGchF90E5C/3TF6pqsPqlEDA6/" +
                "iSYBI1ka1wOCE1YPIt1vzFhG6rfY8hNPwz/pT+JQSA42FoW+N/v5Y4UN5FGxA0RFT1I/jSME2IU9fRFFnArdiMoq0HRKUzeo8f9txnoYgKme5ItuAmD6VU94ddpKyUXkJ83mKOvqLiYTs/" +
                "PR2y99y9NTd/a2R5Gb6lgBpbjTR8vSvK+0zCFYRydSPNnN/krNJ5h+ne0raMXFYnCp5ZOFZ9cR1KzwfcaLYg7c6cthzb+FNqDew8qY6quT6j7RhU5opuJukwIDAQAB";
        bp= new BillingProcessor(getContext(), key, this);
    }

    private void checkAppVersion(){
        if(appVersion.equals(AppDataKeeper.inst.currentVersion)== false){
            String message;
            message= "앱이 최신 버전이 아닙니다.\n업데이트가 필요합니다.\n\n";
            message+= "현재 설치된 앱 버전: "+ appVersion+ "\n";
            message+= "최신 앱 버전: "+ AppDataKeeper.inst.currentVersion;

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
        dialog.setValue("정말로 사용자의 모든 정보를 삭제하시겠습니까?\n(복구 불가)", "예", "아니오", () -> {
            CheckListUtil.getInstance().deleteAllData();
            HistoryListUtil.getInstance().deleteAllData();
            ExamResultListUtil.getInstance().deleteAllData();

            Toast.makeText(getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
            init();
            dialog.dismiss();
        }, null);
        dialog.show(getActivity().getSupportFragmentManager(), "");
    }

    @OnClick(R.id.searchExam)
    void searchQuestion(){

        final DialogMaker dialog= new DialogMaker();
        View childView= getActivity().getLayoutInflater().inflate(R.layout.dialog_search, null);

        final Spinner subject= childView.findViewById(R.id.searchSubject);
        final Spinner institute= childView.findViewById(R.id.searchInstitute);
        final Spinner period_y= childView.findViewById(R.id.searchPeriod_y);
        final Spinner period_m= childView.findViewById(R.id.searchPeriod_m);
        final Spinner number= childView.findViewById(R.id.searchNumber);

        //문제 번호 추가
        //1~30
        ArrayList<String> numbers= new ArrayList<>();
        for(int k=0; k<30; k++){
            numbers.add(String.valueOf(k+1));
        }
        ArrayAdapter<String> adapter=  new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, numbers);
        number.setAdapter(adapter);

        //선택된 기관 별, 기간(월) 조정
        institute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<String> adapter;
                String list[]= {};

                switch(institute.getSelectedItem().toString()){
                    case "대학수학능력평가시험":
                        list= new String[]{"11"};
                        break;
                    case "교육청":
                        list= new String[] {"3", "4", "7", "10"};
                        break;
                    case "교육과정평가원":
                        list= new String[]{"6", "9"};
                        break;
                }

                adapter=  new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, list);
                period_m.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        dialog.setValue("문제 검색", "검색", "취소", ()-> {
                String selectedY = period_y.getSelectedItem().toString();
                String selectedM = period_m.getSelectedItem().toString();
                String selectedInst = institute.getSelectedItem().toString();
                String selectedSubj = subject.getSelectedItem().toString();
                String selectedNum = number.getSelectedItem().toString();

                if (!QuestionUtil.isValidPeriod(selectedY, selectedM)) {
                    Toast.makeText(getContext(), "2018년 10월 교육청 시험까지만 지원됩니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                QuestionNameBuilder.inst= new QuestionNameBuilder(selectedY, selectedM, selectedInst, selectedSubj, selectedNum, "NOT_DEFINED", QuestionNameBuilder.TYPE_KOR);

                Intent intent = new Intent(getActivity().getApplicationContext(), SearchResultActivity.class);
                startActivityForResult(intent, SEARCH_ACTIVITY);

                dialog.dismiss();
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

        dialog.setValue("문제 옵션 선택", "확인", "취소", () -> {
            String subjectOption= (String)subjectSpinner.getSelectedItem();
            String probOption= (String)probabilitySpinner.getSelectedItem();
            String instOption= (String)instituteSpinner.getSelectedItem();
            String periodOption= (String)periodSpinner.getSelectedItem();

            if(periodOption.equals("2018")){
                Toast.makeText(getContext(), "2018년 10월 교육청 시험까지 지원됩니다.", Toast.LENGTH_SHORT).show();
                if(instOption.equals("수능")){
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
        }, null, childView);
        dialog.show(getActivity().getSupportFragmentManager(), "Option Select!");
    }

    @OnClick(R.id.menuList_devInfo)
    void devInfo(){
        Common.openUrl(getContext(), "http://satisfaction.dothome.co.kr/MyWebPage/MainPage.html");
    }

    @OnClick(R.id.menuList_donation)
    void donation(){
        bp.initialize();
        bp.purchase(getActivity(), "donation");
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
                String selectedInst= institute.getSelectedItem().toString();
                String monthList[]= {};
                switch (selectedInst){
                    case "대학수학능력평가시험":
                        monthList= new String[]{"11"};
                        break;
                    case "교육청":
                        monthList= new String[]{"3", "4", "7", "10"};
                        break;
                    case "교육과정평가원":
                        monthList= new String[]{"6", "9"};
                        break;
                }

                adapter=  new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, monthList);
                period_m.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        dialog.setValue("문제 검색", "검색", "취소",()->{
                    //검색 터치 시 동작
                    String selectedY= period_y.getSelectedItem().toString();
                    String selectedM= period_m.getSelectedItem().toString();
                    String selectedInst= institute.getSelectedItem().toString();
                    String selectedSubj= subject.getSelectedItem().toString();

                    ExamNameBuilder.inst= new ExamNameBuilder(selectedY, selectedM, selectedInst, selectedSubj, QuestionNameBuilder.TYPE_KOR);

                    if(!QuestionUtil.isValidPeriod(selectedY, selectedM)){
                        Toast.makeText(getContext(), "2018년 6월 시험까지만 지원됩니다.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent intent= new Intent(getActivity().getApplicationContext(), ExamActivity.class);
                    startActivityForResult(intent, 1335);

                    dialog.dismiss();
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
        Common.openUrl(getContext(), url);
    }

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
        initStatusPager();
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
    public void onPurchaseHistoryRestored() {}

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        getActivity().runOnUiThread(()->{
            Toast.makeText(getContext(), "결제 시스템에 에러가 발생하였습니다.\n에러코드: "+ errorCode, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onBillingInitialized() {}
}
