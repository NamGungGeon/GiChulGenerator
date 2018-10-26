package com.satisfactoryplace.gichul.gichulgenerator.utils;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;

public class ProgressUtil {
    public interface SimpleCallBack{
        void start();
    }
    public interface Progress{
        int getProgress();
    }
    public static void loadingCheck(@NonNull final ProgressDialog progressDialog, int maxProgress, @NonNull Progress progress){
        AsyncTaskUtil.startAsyncTask(()->{
            while(true){
                int prog= progress.getProgress();
                if(prog>= maxProgress){
                    progressDialog.dismiss();
                    return;
                }

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void loadingCheck(@NonNull final ProgressDialog progressDialog, int maxProgress, @NonNull Progress progress, @NonNull SimpleCallBack endProgress){
        AsyncTaskUtil.startAsyncTask(()->{
            while(true){
                int prog= progress.getProgress();
                if(prog>= maxProgress){
                    progressDialog.dismiss();
                    endProgress.start();
                    return;
                }

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void loadingCheck(int maxProgress, @NonNull Progress progress){
        AsyncTaskUtil.startAsyncTask(()->{
            while(true){
                int prog= progress.getProgress();
                if(prog>= maxProgress){
                    return;
                }

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void loadingCheck(int maxProgress, @NonNull Progress progress, @NonNull SimpleCallBack endProgress){
        AsyncTaskUtil.startAsyncTask(()->{
            while(true){
                int prog= progress.getProgress();
                if(prog>= maxProgress){
                    endProgress.start();
                    return;
                }

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
