package com.satisfactoryplace.gichul.gichulgenerator;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Purchase;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import org.json.JSONObject;

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

/**
 * Created by WINDOWS7 on 2018-01-20.
 */

public class MainPageFragment extends Fragment implements OnBackPressedListener{

    @BindView(R.id.main_statusPager) ViewPager statusPager;
    @BindView(R.id.status_back) ImageView status_back;
    @BindView(R.id.status_next) ImageView status_next;

    @BindView(R.id.scheduler) TextView d_day;
    @BindView(R.id.mainmenu_ad)AdView adView;

    private final int EXAM_ACTIVITY= 1335;
    private final int SEARCH_ACTIVITY= 1336;

    private Unbinder unbinder;
    private String appVersion= "2.0";

    IInAppBillingService mService;
    ServiceConnection mServiceConn;
    IabHelper mHelper;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView= (ViewGroup)inflater.inflate(R.layout.frag_mainmenu, container, false);
        unbinder= ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    private void init(){
        set_Dday();
        setStatusPager();
        setAdView();
        setBillProcess();
    }
    private void setAdView(){
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void setStatusPager(){
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
    private void set_Dday(){
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
    private void setBillProcess(){

        mServiceConn= new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };

        Intent intent= new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        getActivity().bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAorXcU15UsFjV0tml82MbjXsz9b0VKfIfDQOaJpJwSu/TOph9pr+pxKvGchF90E5C/3TF6pqsPqlEDA6/iSYBI1ka1wOCE1YPIt1vzFhG6rfY8hNPwz/pT+JQSA42FoW+N/v5Y4UN5FGxA0RFT1I/jSME2IU9fRFFnArdiMoq0HRKUzeo8f9txnoYgKme5ItuAmD6VU94ddpKyUXkJ83mKOvqLiYTs/PR2y99y9NTd/a2R5Gb6lgBpbjTR8vSvK+0zCFYRydSPNnN/krNJ5h+ne0raMXFYnCp5ZOFZ9cR1KzwfcaLYg7c6cthzb+FNqDew8qY6quT6j7RhU5opuJukwIDAQAB";
        mHelper = new IabHelper(getContext(), base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                AlreadyPurchaseItems();
                if (!result.isSuccess()) {
                    Toast.makeText(getContext(), "인앱 결제 시스템 초기화 실패", Toast.LENGTH_SHORT).show();
                }
                // AlreadyPurchaseItems(); 메서드는 구매목록을 초기화하는 메서드입니다.
                // v3으로 넘어오면서 구매기록이 모두 남게 되는데 재구매 가능한 상품( 게임에서는 코인같은아이템은 ) 구매후 삭제해주어야 합니다.
                // 이 메서드는 상품 구매전 혹은 후에 반드시 호출해야합니다. ( 재구매가 불가능한 1회성 아이템의경우 호출하면 안됩니다 )
            }
        });
    }
    private void AlreadyPurchaseItems() {
        try {
            Bundle ownedItems = mService.getPurchases(3, getActivity().getPackageName(), "inapp", null);
            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                String[] tokens = new String[purchaseDataList.size()];
                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = (String) purchaseDataList.get(i);
                    JSONObject jo = new JSONObject(purchaseData);
                    tokens[i] = jo.getString("purchaseToken");
                    // 여기서 tokens를 모두 컨슘 해주기
                    mService.consumePurchase(3, getActivity().getPackageName(), tokens[i]);
                }
            }

            // 토큰을 모두 컨슘했으니 구매 메서드 처리
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buy(String id_item) {
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, getActivity().getPackageName(),	id_item, "inapp", "donation");
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            if (pendingIntent != null) {
                //startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                mHelper.launchPurchaseFlow(getActivity(), getActivity().getPackageName(), 1001, null, "test");
             } else {
                // 결제가 막혔다면
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "결제 시스템을 시작할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
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
        buy("donation");
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

    @OnClick( R.id.qna)
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        init();
        if(requestCode == 1001)
            if (resultCode == getActivity().RESULT_OK) {
                if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                    super.onActivityResult(requestCode, resultCode, data);

                    int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                    String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                    String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

                    // 여기서 아이템 추가 해주시면 됩니다.
                    // 만약 서버로 영수증 체크후에 아이템 추가한다면, 서버로 purchaseData , dataSignature 2개 보내시면 됩니다.
                    Toast.makeText(getContext(), "기부해주셔서 감사합니다.\n더 나은 서비스를 제공하기 위해 노력하겠습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 구매취소 처리
                }
            }else{
                // 구매취소 처리
            }
        else{
            // 구매취소 처리
        }
    }
    @Override public void onDestroy() {
        super.onDestroy();
        if (mServiceConn != null) {
            getActivity().unbindService(mServiceConn);
        }
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

}
