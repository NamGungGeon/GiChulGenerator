package com.satisfactoryplace.gichul.gichulgenerator;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;

public class MonthReportFragment extends Fragment {
    public MonthReportFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.frag_mystatuschecker, container, false);
        setGraph(rootView);

        return rootView;
    }

    private void setGraph(ViewGroup rootView){
        GraphView graph= rootView.findViewById(R.id.graph);

        LineGraphSeries<DataPoint> number= new LineGraphSeries<>(getNumberReports().toArray(new DataPoint[getNumberReports().size()]));
        number.setTitle("푼 문제 수");
        number.setColor(Color.GREEN);
        number.setDrawDataPoints(true);
        number.setDataPointsRadius(10);
        number.setThickness(6);
        graph.addSeries(number);

        LineGraphSeries<DataPoint> potential= new LineGraphSeries<>(getRightReports().toArray(new DataPoint[getRightReports().size()]));
        potential.setTitle("맞춘 문제 수");
        potential.setColor(Color.RED);
        potential.setDrawDataPoints(true);
        potential.setDataPointsRadius(10);
        potential.setThickness(6);
        graph.addSeries(potential);

        graph.setTitle("월별 문제풀이 결과 분석");
        graph.setTitleTextSize(50);
        graph.setTitleColor(Color.WHITE);

        graph.getGridLabelRenderer().setGridColor(Color.WHITE);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setTextSize(30);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getLegendRenderer().setTextColor(Color.WHITE);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // format as date
                    mCalendar.setTimeInMillis((long) value);
                    return mCalendar.get(Calendar.YEAR)+"/"+(mCalendar.get(Calendar.MONTH)+1);
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        graph.getGridLabelRenderer().setNumHorizontalLabels(5); // only 4 because of the space
        graph.getGridLabelRenderer().setNumVerticalLabels(6);

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(getNumberReports().get(0).getX());
        graph.getViewport().setMaxX(getNumberReports().get(getNumberReports().size()-1).getX());
        graph.getViewport().setXAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
    }

    private ArrayList<DataPoint> getNumberReports(){
        Calendar calendar= Calendar.getInstance();
        calendar.add(Calendar.MONTH, -4);

        ArrayList<DataPoint> dataList= new ArrayList<>();
        for(int i=0; i<5; i++){
            dataList.add( new DataPoint(calendar.getTime(), HistoryList.getInstance().getHistoryNumber_month(calendar)));
            calendar.add(Calendar.MONTH, 1);
        }

        return dataList;
    }

    private ArrayList<DataPoint> getRightReports(){
        Calendar calendar= Calendar.getInstance();
        calendar.add(Calendar.MONTH, -4);

        ArrayList<DataPoint> dataList= new ArrayList<>();
        for(int i=0; i<5; i++){
            dataList.add( new DataPoint(calendar.getTime(), HistoryList.getInstance().getHistoryRightNumber_month(calendar)));
            calendar.add(Calendar.MONTH, 1);
        }

        return dataList;
    }
}
