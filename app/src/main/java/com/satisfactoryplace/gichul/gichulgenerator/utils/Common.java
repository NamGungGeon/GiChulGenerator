package com.satisfactoryplace.gichul.gichulgenerator.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.satisfactoryplace.gichul.gichulgenerator.data.AppDataKeeper;
import com.satisfactoryplace.gichul.gichulgenerator.data.Schedule;
import com.satisfactoryplace.gichul.gichulgenerator.model.AppData;
import com.satisfactoryplace.gichul.gichulgenerator.model.ErrorInfo;
import com.satisfactoryplace.gichul.gichulgenerator.model.Status;
import com.satisfactoryplace.gichul.gichulgenerator.server.FirebaseConnection;

import java.util.HashMap;

public class Common {
    public interface SimpleCallBack{
        void start();
    }

    public static void openUrl(Context context, String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }
    public static void initAdView(AdView adView){
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    public static String getFilterValue(Spinner filter) {
        String subjectFilter = filter.getSelectedItem().toString();
        //Converting...
        if (subjectFilter.equals("상관없음")) {
            subjectFilter = null;
        } else if (subjectFilter.equals("수학(이과)")) {
            subjectFilter = "imath";
        } else if (subjectFilter.equals("수학(문과)")) {
            subjectFilter = "mmath";
        }

        return subjectFilter;
    }


    public static void loadBaseData(@NonNull SimpleCallBack fail, @NonNull SimpleCallBack progressCheck){

        //Start to connection firebase and Load data
        //Load CheckListUtil
        CheckListUtil.getInstance().loadCheckListFromServer(new CheckListUtil.Callback() {
            @Override
            public void success() {
                progressCheck.start();
            }
            @Override
            public void fail() {
                ErrorReportUtil.report(new ErrorInfo("Can't Load CheckListUtil from server", "원인불명", "MainPageLoadingFragment"));
                fail.start();
            }
        });
        //Load HistoryListUtil
        HistoryListUtil.getInstance().loadHistoryListFromServer(new HistoryListUtil.Callback() {
            @Override
            public void success() {
                progressCheck.start();
            }

            @Override
            public void fail() {
                ErrorReportUtil.report(new ErrorInfo("Can't Load HistoryListUtil from server", "원인불명", "MainPageLoadingFragment"));
                fail.start();
            }
        });
        //Load Schedule
        FirebaseConnection.getInstance().loadData("appdata/schedule/sunung", new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                progressCheck.start();

                Schedule.sunungDate= (String)snapshot.getValue();
            }

            @Override
            public void fail(String errorMessage) {
                ErrorReportUtil.report(new ErrorInfo(errorMessage, "Can't load Schedule", "MainPageLoadingFragment"));
                fail.start();
            }
        });
        //Load UserStatus
        FirebaseConnection.getInstance().loadData("userdata/" + FirebaseAuth.getInstance().getUid() + "/status", new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                Status.setValues((HashMap<String, String>)snapshot.getValue());

                progressCheck.start();
            }

            @Override
            public void fail(String errorMessage) {
                ErrorReportUtil.report(new ErrorInfo(errorMessage, "Can't load UserStatus", "MainPageLoadingFragment"));
                fail.start();
            }
        });
        //Load AppData
        FirebaseConnection.getInstance().loadData("appdata/", new FirebaseConnection.Callback() {
            @Override
            public void success(DataSnapshot snapshot) {
                AppDataKeeper.inst= snapshot.getValue(AppData.class);
                progressCheck.start();
            }
            @Override
            public void fail(String errorMessage) {
                ErrorReportUtil.report(new ErrorInfo(errorMessage, "Can't load AppData", "MainPageLoadingFragment"));
                fail.start();
            }
        });
        //Load ExamResult
        ExamResultListUtil.getInstance().loadExamResultListFromFirebase(new ExamResultListUtil.Callback() {
            @Override
            public void success() {
                progressCheck.start();
            }

            @Override
            public void fail() {
                ErrorReportUtil.report(new ErrorInfo("Can't load ExamResult", "원인불명", "MainPageLoadingFragment"));
                fail.start();
            }
        });
    }

}
