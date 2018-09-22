package com.satisfactoryplace.gichul.gichulgenerator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.model.ExamResult;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Windows10 on 2018-03-03.
 */

public class ExamResultListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<ExamResult> data;
    private int layout;

    public ExamResultListAdapter(Context context, int layout, ArrayList<ExamResult> data){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(data== null){
            this.data= new ArrayList<>();
        }else{
            this.data=data;
        }
        this.layout=layout;
    }

    @Override
    public int getCount(){return data.size();}
    @Override
    public String getItem(int position){return data.get(position).getTitle();}
    @Override
    public long getItemId(int position){return position;}
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView=inflater.inflate(layout, parent,false);
        }

        TextView title= convertView.findViewById(R.id.examResultListItem_examTitle);
        TextView info= convertView.findViewById(R.id.examResultListItem_examInfo);

        ExamResult er= data.get(position);

        title.setText(er.getTitle());
        initInfoText(info, er);

        return convertView;
    }

    private void initInfoText(TextView text, ExamResult er){
        String info= getTimeInfo(er)+ "/ "+ getRightNumberInfo(er)
                        +"\n"+ getDateInfo(er);
        text.setText(info);
    }
    private String getTimeInfo(ExamResult er){

        int sec= er.getRunningTime()%60;
        int min= er.getRunningTime()/60;

        return min+ "분 "+ sec+ "초 소요";
    }
    private String getRightNumberInfo(ExamResult er){
        int rightAnswerNumber= 0;
        for(int i=0; i<30; i++){
            if(er.getInputAnswers().get(i)== er.getRightAnswers().get(i+1)){
                rightAnswerNumber++;
            }
        }
        return "30문제 중 "+ String.valueOf(rightAnswerNumber)+ "문제 정답";
    }
    private String getDateInfo(ExamResult er){
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(er.getTimeStamp());
        return calendar.get(Calendar.YEAR)+ "년 "+ (calendar.get(Calendar.MONTH)+1)+ "월 "
                + calendar.get(Calendar.DAY_OF_MONTH)+ "일에 응시한 시험입니다.";
    }

}
