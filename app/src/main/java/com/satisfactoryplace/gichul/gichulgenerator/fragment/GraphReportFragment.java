package com.satisfactoryplace.gichul.gichulgenerator.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.satisfactoryplace.gichul.gichulgenerator.model.GraphDefine;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GraphReportFragment extends android.support.v4.app.Fragment{
    @BindView(R.id.graph) GraphView graph;
    private GraphDefine define= null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_mystatuschecker, container, false);
        graph= rootView.findViewById(R.id.graph);
        initGraph();

        return rootView;
    }

    /**
     * Must Be Called setGraphDefine() after instantiating
     * @param define
     * Implement GraphDefine interface.
     * Not allow null
     */
    public void setGraphDefine(GraphDefine define){
        this.define= define;
    }

    private void initGraph(){
        ArrayList<DataPoint> dataPoints= define.getAllDataList();

        LineGraphSeries<DataPoint> number= new LineGraphSeries<>(dataPoints.toArray(new DataPoint[dataPoints.size()]));
        number.setTitle("푼 문제 수");
        number.setColor(getResources().getColor(R.color.purple));
        number.setDrawDataPoints(true);
        number.setDataPointsRadius(10);
        number.setThickness(6);
        graph.addSeries(number);

        dataPoints= define.getRightDataList();
        LineGraphSeries<DataPoint> potential= new LineGraphSeries<>(dataPoints.toArray(new DataPoint[dataPoints.size()]));
        potential.setTitle("맞춘 문제 수");
        potential.setColor(getResources().getColor(R.color.green));
        potential.setDrawDataPoints(true);
        potential.setDataPointsRadius(10);
        potential.setThickness(6);
        graph.addSeries(potential);

        graph.setTitle(define.getGraphTitle());
        graph.setTitleColor(getResources().getColor(R.color.whiteGray));
        graph.setTitleTextSize(50);

        graph.getGridLabelRenderer().setGridColor(getResources().getColor(R.color.whiteGray));
        graph.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.whiteGray));
        graph.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.whiteGray));
        graph.getGridLabelRenderer().setTextSize(30);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getLegendRenderer().setTextColor(getResources().getColor(R.color.whiteGray));

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(define.getLabelFormatter(getContext()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(5); // only 4 because of the space
        graph.getGridLabelRenderer().setNumVerticalLabels(5);

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(define.getAllDataList().get(0).getX());
        graph.getViewport().setMaxX(define.getAllDataList().get(define.getAllDataList().size()-1).getX());
        graph.getViewport().setXAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
    }

}
