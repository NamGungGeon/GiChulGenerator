package com.example.windows7.gichulgenerator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by WINDOWS7 on 2018-01-28.
 */

public class ListViewAdapter_CheckList extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<ListViewItem_CheckList> data;
    private int layout;

    public ListViewAdapter_CheckList(Context context, int layout, ArrayList<ListViewItem_CheckList> data){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        ListViewItem_CheckList item=data.get(position);

        TextView examTitle= convertView.findViewById(R.id.checkListItem_examTitle);
        examTitle.setText(item.getExamTitle());
        TextView examInfo= convertView.findViewById(R.id.checkListItem_examInfo);
        examInfo.setText(item.getExamInfo());
        TextView examMemo= convertView.findViewById(R.id.checkListItem_examMemo);
        examMemo.setText(item.getExamMemo());

        return convertView;
    }
}