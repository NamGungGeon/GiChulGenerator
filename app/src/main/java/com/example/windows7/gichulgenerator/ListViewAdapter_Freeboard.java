package com.example.windows7.gichulgenerator;

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

        TextView title= convertView.findViewById(R.id.freeboardListItem_title);
        title.setText(data.get(position).getTitle());
        TextView userName= convertView.findViewById(R.id.freeboardListItem_userName);
        userName.setText(data.get(position).getUserName());

        return convertView;
    }
}