package com.satisfactoryplace.gichul.gichulgenerator.utils;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
public class TimerUtil {
    public interface TimerFail{
        void handling(Exception e);
    }

    private FragmentActivity fActivity;
    private int sec;
    private boolean isRunning;
    private TextView timerText;

    public TimerUtil(@NonNull FragmentActivity fActivity, @NonNull TextView timerText) {
        this.fActivity = fActivity;
        this.timerText = timerText;

        sec= -1;
        isRunning= true;
    }
    public void startTimer(TimerFail fail){
        AsyncTaskUtil.startAsyncTask(()->{
            while(isRunning){
                sec++;
                fActivity.runOnUiThread(()->refreshTimer());
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    if(fail!= null)
                        fail.handling(e);
                }
            }
        });
    }
    private void refreshTimer(){
        int _min= sec/60;
        int _sec= sec%60;
        timerText.setText(String.valueOf(_min)+ "분 "+ String.valueOf(_sec)+ "초");
    }
    public int stopTimer(){
        isRunning= false;
        return sec;
    }
}
