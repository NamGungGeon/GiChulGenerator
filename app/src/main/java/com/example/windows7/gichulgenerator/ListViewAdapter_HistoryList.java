package com.example.windows7.gichulgenerator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Windows10 on 2018-01-28.
 */

public class ListViewAdapter_HistoryList extends BaseAdapter{
    private LayoutInflater inflater;
    private ArrayList<ExamInfo> data;
    private int layout;
    private Context context;

    public ListViewAdapter_HistoryList(Context context, int layout, ArrayList<ExamInfo> data){
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

        ExamInfo item=data.get(position);

        TextView examTitle= convertView.findViewById(R.id.historyListItem_examTitle);
        examTitle.setText(item.getTitle());

        // Decide Text Color
        TextView examInfo= convertView.findViewById(R.id.historyListItem_examInfo);
        String info= item.getInputAnswer();
        int sec= Integer.valueOf(data.get(position).getTime());
        int min= 0;
        if(sec>= 60){
            min= sec/60;
            sec= sec%60;
        }

        String result= min+"분 "+ sec+ " 초 소요. 정답률 "+ data.get(position).getPotential()+"%. ";
        if(info.equals(item.getRightAnswer())){
            //정답
            examInfo.setTextColor(context.getResources().getColor(R.color.green));
            result+= "정답";
        }else{
            //오답
            examInfo.setTextColor(context.getResources().getColor(R.color.red));
            result+= "오답";
        }
        examInfo.setText(result);

        return convertView;
    }
}
