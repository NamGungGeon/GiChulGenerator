package com.example.windows7.gichulgenerator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Windows10 on 2018-02-12.
 */

public class ListViewAdapter_Comment extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<Comment> data;
    private int layout;
    private Context context;

    public ListViewAdapter_Comment(Context context, int layout, ArrayList<Comment> data){
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
    public String getItem(int position){return data.get(position).getText();}
    @Override
    public long getItemId(int position){return position;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView=inflater.inflate(layout, parent,false);
        }
        TextView context= convertView.findViewById(R.id.commentListItem_text);
        TextView userName= convertView.findViewById(R.id.commentListItem_userName);
        context.setText(data.get(position).getText());
        userName.setText(data.get(position).getUserName());
        return convertView;
    }
}
