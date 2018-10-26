package com.satisfactoryplace.gichul.gichulgenerator.data;

import android.content.Context;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.satisfactoryplace.gichul.gichulgenerator.model.GraphDefine;
import com.satisfactoryplace.gichul.gichulgenerator.utils.HistoryListUtil;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TodayReportGraphDefine implements GraphDefine{

    @Override
    public DefaultLabelFormatter getLabelFormatter(Context context) {
        return new DateAsXAxisLabelFormatter(context);
    }

    @Override
    public String getGraphTitle() {
        return "일별 문제풀이 분석";
    }

    @Override
    public ArrayList<DataPoint> getAllDataList() {
        Calendar calendar= Calendar.getInstance();
        calendar.add(Calendar.DATE, -4);

        ArrayList<DataPoint> dataList= new ArrayList<>();
        for(int i=0; i<5; i++){
            dataList.add( new DataPoint(calendar.getTime(), HistoryListUtil.getInstance().getHistoryNumber_day(calendar)));
            calendar.add(Calendar.DATE, 1);
        }

        return dataList;
    }

    @Override
    public ArrayList<DataPoint> getRightDataList() {
        Calendar calendar= Calendar.getInstance();
        calendar.add(Calendar.DATE, -4);

        ArrayList<DataPoint> dataList= new ArrayList<>();
        for(int i=0; i<5; i++){
            dataList.add( new DataPoint(calendar.getTime(), HistoryListUtil.getInstance().getHistoryRightNumber_day(calendar)));
            calendar.add(Calendar.DATE, 1);
        }

        return dataList;
    }
}
