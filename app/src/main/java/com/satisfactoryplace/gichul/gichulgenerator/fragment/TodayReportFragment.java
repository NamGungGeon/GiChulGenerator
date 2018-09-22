package com.satisfactoryplace.gichul.gichulgenerator.fragment;

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
import com.satisfactoryplace.gichul.gichulgenerator.R;
import com.satisfactoryplace.gichul.gichulgenerator.model.HistoryList;

import java.util.ArrayList;
import java.util.Calendar;

public class TodayReportFragment extends Fragment {
    public TodayReportFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.frag_mystatuschecker, container, false);
        initGraph(rootView);

        return rootView;
    }

    private void initGraph(ViewGroup rootView){
        GraphView graph= rootView.findViewById(R.id.graph);

        LineGraphSeries<DataPoint> number= new LineGraphSeries<>(getNumberReports().toArray(new DataPoint[getNumberReports().size()]));
        number.setTitle("푼 문제 수");
        number.setColor(Color.GREEN);
        number.setDrawDataPoints(true);
        number.setDataPointsRadius(10);
        number.setThickness(6);
        graph.addSeries(number);

        LineGraphSeries<DataPoint> rightNumber= new LineGraphSeries<>(getRightReports().toArray(new DataPoint[getRightReports().size()]));
        rightNumber.setTitle("맞춘 문제 수");
        rightNumber.setColor(Color.RED);
        rightNumber.setDrawDataPoints(true);
        rightNumber.setDataPointsRadius(10);
        rightNumber.setThickness(6);
        graph.addSeries(rightNumber);

        graph.setTitle("일별 문제풀이 결과 분석");
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
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(5); // only 4 because of the space
        graph.getGridLabelRenderer().setNumVerticalLabels(6);

        // set manual x bounds to have nice stepsu
        graph.getViewport().setMinX(getNumberReports().get(0).getX());
        graph.getViewport().setMaxX(getNumberReports().get(getNumberReports().size()-1).getX());
        graph.getViewport().setXAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
    }

    private ArrayList<DataPoint> getNumberReports(){
        Calendar calendar= Calendar.getInstance();
        calendar.add(Calendar.DATE, -4);

        ArrayList<DataPoint> dataList= new ArrayList<>();
        for(int i=0; i<5; i++){
            dataList.add( new DataPoint(calendar.getTime(), HistoryList.getInstance().getHistoryNumber_day(calendar)));
            calendar.add(Calendar.DATE, 1);
        }

        return dataList;
    }
    private ArrayList<DataPoint> getRightReports(){
        Calendar calendar= Calendar.getInstance();
        calendar.add(Calendar.DATE, -4);

        ArrayList<DataPoint> dataList= new ArrayList<>();
        for(int i=0; i<5; i++){
            dataList.add( new DataPoint(calendar.getTime(), HistoryList.getInstance().getHistoryRightNumber_day(calendar)));
            calendar.add(Calendar.DATE, 1);
        }

        return dataList;
    }

}
