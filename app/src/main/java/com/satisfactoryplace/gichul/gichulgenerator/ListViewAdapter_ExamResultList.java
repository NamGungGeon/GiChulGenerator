package com.satisfactoryplace.gichul.gichulgenerator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Windows10 on 2018-03-03.
 */

public class ListViewAdapter_ExamResultList  extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<ExamResult> data;
    private int layout;
    private Context context;

    public ListViewAdapter_ExamResultList(Context context, int layout, ArrayList<ExamResult> data){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context= context;
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
        title.setText(data.get(position).getTitle());

        TextView info= convertView.findViewById(R.id.examResultListItem_examInfo);

        int sec= data.get(position).getRunningTime()%60;
        int min= data.get(position).getRunningTime()/60;

        int rightAnswerNumber= 0;
        for(int i=0; i<30; i++){
            if(data.get(position).getInputAnswers().get(i)== data.get(position).getRightAnswers().get(i+1)){
                rightAnswerNumber++;
            }
        }

        String infoMessage= min+ "분 "+ sec+ "초 소요. 30문제 중 "+ rightAnswerNumber+ "문제 정답.";
        info.setText(infoMessage);

        return convertView;
    }
}
