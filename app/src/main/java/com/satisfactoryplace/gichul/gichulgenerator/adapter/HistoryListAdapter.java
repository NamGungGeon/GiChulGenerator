package com.satisfactoryplace.gichul.gichulgenerator.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.model.Question;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Windows10 on 2018-01-28.
 */

public class HistoryListAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private ArrayList<Question> data;
    private int layout;
    private Context context;

    public HistoryListAdapter(Context context, int layout, ArrayList<Question> data){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context= context;
        this.data=data;
        this.layout=layout;
    }
    @Override
    public int getCount(){return data.size();}
    @Override
    public String getItem(int position){return data.get(position).getFileName();}
    @Override
    public long getItemId(int position){return position;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView=inflater.inflate(layout, parent,false);
        }

        Question q=data.get(position);

        TextView examTitle= convertView.findViewById(R.id.historyListItem_examTitle);
        TextView examInfo= convertView.findViewById(R.id.historyListItem_examInfo);

        initTitleText(examTitle, q);
        initInfoText(examInfo, q);

        return convertView;
    }

    private void initTitleText(@NonNull TextView text, @NonNull Question q){
        text.setText(q.getTitle());
    }
    private void initInfoText(@NonNull TextView text, @NonNull Question q){
        text.setText(getInfoText(q));
        initInfoTextColor(text, q);
    }
    private void initInfoTextColor(TextView text, Question q){
        String input= q.getInputAnswer();
        String right= q.getRightAnswer();

        if(input.equals(right))
            text.setTextColor(context.getResources().getColor(R.color.green));
        else
            text.setTextColor(context.getResources().getColor(R.color.red));
    }
    private String getInfoText(Question q){
        String result= getTimeInfo(q)+ "/"+ getPotenInfo(q) +"\n"+ getDateInfo(q);
        return result;
    }

    private String getTimeInfo(Question q){
        int sec= Integer.valueOf(q.getTime())%60;
        int min= sec/60;

        return min+"분 "+ sec+ " 초 소요";
    }
    private String getPotenInfo(Question q){
        int potential= Integer.valueOf(q.getPotential());

        String potentialText= "정답률: ";
        if(potential>= 80){
            potentialText+= "매우높음.";
        }else if(potential>= 60){
            potentialText+= "높음.";
        }else if(potential>= 40){
            potentialText+= "보통.";
        }else if(potential>= 20){
            potentialText+= "낮음.";
        }else{
            potentialText+= "매우낮음.";
        }

        return potentialText;
    }
    private String getDateInfo(Question q){
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(q.getTimeStamp());
        String result= calendar.get(Calendar.YEAR)+ "년 "+ (calendar.get(Calendar.MONTH)+1)+ "월 "
                + calendar.get(Calendar.DAY_OF_MONTH)+ "일에 푼 문제입니다.";

        return result;
    }
}
