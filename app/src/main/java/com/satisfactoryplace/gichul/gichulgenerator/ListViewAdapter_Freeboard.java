package com.satisfactoryplace.gichul.gichulgenerator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by WINDOWS7 on 2018-02-11.
 */

public class ListViewAdapter_Freeboard extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Article> data;
    private int layout;
    private Context context;

    public ListViewAdapter_Freeboard(Context context, int layout, ArrayList<Article> data) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.data = data;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return String.valueOf(data.get(position).getTimeStamp());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }
        if(data== null){
            convertView.setVisibility(View.GONE);
            return convertView;
        }

        TextView title= convertView.findViewById(R.id.freeboardListItem_title);
        TextView userName= convertView.findViewById(R.id.freeboardListItem_userName);

        // Title is "title [commentNumber]"
        int commentNumber;
        if(data.get(position).getComments()== null || data.get(position).getComments().size()== 0){
            commentNumber= 0;
        }else{
            commentNumber= data.get(position).getComments().size();
        }

        if(data.get(position).getTitle()!= null){
            title.setText(data.get(position).getTitle()+ " ["+ commentNumber+ "]");
        }else{
            title.setText("???");
        }

        if(data.get(position).getUserName()!= null){
            if(data.get(position).getUserName().equals("관리자")){
                userName.setText("관리자");
                userName.setTextColor(context.getResources().getColor(R.color.red));
            }else{
                userName.setText(data.get(position).getUserName());
            }
        }else{
            userName.setText("???");
        }



        return convertView;
    }
}