package com.satisfactoryplace.gichul.gichulgenerator.data;

import android.content.Context;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.satisfactoryplace.gichul.gichulgenerator.model.GraphDefine;
import com.satisfactoryplace.gichul.gichulgenerator.utils.HistoryListUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class MonthReportGraphDefine implements GraphDefine {
    @Override
    public String getGraphTitle() {
        return "월별 문제풀이 분석";
    }

    @Override
    public DefaultLabelFormatter getLabelFormatter(Context context) {
        return new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Calendar mCalendar = Calendar.getInstance();
                    mCalendar.setTimeInMillis((long) value);
                    return mCalendar.get(Calendar.YEAR) + "/" + (mCalendar.get(Calendar.MONTH) + 1);
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        };
    }

    @Override
    public ArrayList<DataPoint> getAllDataList() {
        Calendar calendar= Calendar.getInstance();
        calendar.add(Calendar.MONTH, -4);

        ArrayList<DataPoint> dataList= new ArrayList<>();
        for(int i=0; i<5; i++){
            dataList.add( new DataPoint(calendar.getTime(), HistoryListUtil.getInstance().getHistoryNumber_month(calendar)));
            calendar.add(Calendar.MONTH, 1);
        }

        return dataList;
    }

    @Override
    public ArrayList<DataPoint> getRightDataList() {
        Calendar calendar= Calendar.getInstance();
        calendar.add(Calendar.MONTH, -4);

        ArrayList<DataPoint> dataList= new ArrayList<>();
        for(int i=0; i<5; i++){
            dataList.add( new DataPoint(calendar.getTime(), HistoryListUtil.getInstance().getHistoryRightNumber_month(calendar)));
            calendar.add(Calendar.MONTH, 1);
        }

        return dataList;
    }
}
