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
import com.satisfactoryplace.gichul.gichulgenerator.utils.QuestionUtil;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by WINDOWS7 on 2018-01-28.
 */

public class CheckListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Question> data;
    private int layout;
    private Context context;

    public CheckListAdapter(Context context, int layout, ArrayList<Question> data){
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

        TextView examTitle= convertView.findViewById(R.id.checkListItem_examTitle);
        TextView examInfo= convertView.findViewById(R.id.checkListItem_examInfo);
        TextView examMemo= convertView.findViewById(R.id.checkListItem_examMemo);

        Question q=data.get(position);

        initTitleText(examTitle, q);
        initInfoText(examInfo, q);
        initMemoText(examMemo, q);

        return convertView;
    }
    private void initMemoText(@NonNull TextView text, @NonNull Question q){
        if(q.getMemo().length()== 0){
            text.setVisibility(View.GONE);
            return;
        }

        text.setText(q.getMemo());
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
        String potentialText= QuestionUtil.getPotentialText(Integer.valueOf(q.getPotential()));
        String result= getTimeInfo(q)+ "/"+ potentialText +"\n"+ getDateInfo(q);
        return result;
    }

    private String getTimeInfo(Question q){
        int sec= Integer.valueOf(q.getTime())%60;
        int min= sec/60;

        return min+"분 "+ sec+ " 초 소요";
    }
    private String getDateInfo(Question q){
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(q.getTimeStamp());
        String result= calendar.get(Calendar.YEAR)+ "년 "+ (calendar.get(Calendar.MONTH)+1)+ "월 "
                        + calendar.get(Calendar.DAY_OF_MONTH)+ "일에 푼 문제입니다.";

        return result;
    }
}