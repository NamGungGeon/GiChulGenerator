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
    private ArrayList<ListViewItem_HistoryList> data;
    private int layout;
    private Context context;

    public ListViewAdapter_HistoryList(Context context, int layout, ArrayList<ListViewItem_HistoryList> data){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context= context;
        this.data=data;
        this.layout=layout;
    }
    @Override
    public int getCount(){return data.size();}
    @Override
    public String getItem(int position){return data.get(position).getExamTitle();}
    @Override
    public long getItemId(int position){return position;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView=inflater.inflate(layout, parent,false);
        }

        ListViewItem_HistoryList item=data.get(position);

        TextView examTitle= convertView.findViewById(R.id.historyListItem_examTitle);
        examTitle.setText(item.getExamTitle());

        TextView examInfo= convertView.findViewById(R.id.historyListItem_examInfo);

        // Decide Text Color
        String info= item.getExamInfo();
        if(info.equals("정답")){
            examInfo.setTextColor(context.getResources().getColor(R.color.green));
        }else if(info.equals("오답")){
            examInfo.setTextColor(context.getResources().getColor(R.color.red));
        }
        examInfo.setText(item.getExamInfo());

        return convertView;
    }
}
