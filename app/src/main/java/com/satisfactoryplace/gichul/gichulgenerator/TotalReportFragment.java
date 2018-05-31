package com.satisfactoryplace.gichul.gichulgenerator;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class TotalReportFragment extends Fragment {
    public TotalReportFragment(){}

    private final int vhigh= 13;
    private final int high= 14;
    private final int normal= 15;
    private final int low= 16;
    private final int vlow= 17;
    private final int total= 18;

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

        graph.setTitle("난이도 별 전체 문제풀이 결과 분석");
        graph.setTitleTextSize(50);
        graph.setTitleColor(Color.WHITE);

        graph.getGridLabelRenderer().setGridColor(Color.WHITE);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setTextSize(30);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getLegendRenderer().setTextColor(Color.WHITE);

        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(HistoryList.getInstance().getHistoryList().size()+10);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX){
                    switch ((int)value){
                        case vhigh:
                            return "매우 쉬움";
                        case high:
                            return "쉬움";
                        case normal:
                            return "보통";
                        case low:
                            return "어려움";
                        case vlow:
                            return "매우 어려움";
                        default:
                            return "전체";
                    }
                }else{
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        graph.getGridLabelRenderer().setNumHorizontalLabels(6);
        graph.getGridLabelRenderer().setNumVerticalLabels(6);
    }

    private ArrayList<DataPoint> getNumberReports(){
        ArrayList<DataPoint> dataList= new ArrayList<>();

        //난이도별 푼 문제 갯수
        int vh=0, h=0, n=0, l=0, vl=0, t=0;

        ArrayList<Question> historyList= HistoryList.getInstance().getHistoryList();
        for(Question q: historyList){
            int potentialFilter= Integer.valueOf(q.getPotential());
            if(potentialFilter>=80){
                vh++;
            }else if(potentialFilter>=60){
                h++;
            }else if(potentialFilter>=40){
                n++;
            }else if(potentialFilter>=20){
                l++;
            }else{
                vl++;
            }
            t++;
        }

        dataList.add(new DataPoint(vhigh, vh));
        dataList.add(new DataPoint(high, h));
        dataList.add(new DataPoint(normal, n));
        dataList.add(new DataPoint(low, l));
        dataList.add(new DataPoint(vlow, vl));
        dataList.add(new DataPoint(total, t));

        return dataList;
    }
    private ArrayList<DataPoint> getRightReports(){
        ArrayList<DataPoint> dataList= new ArrayList<>();

        //난이도별 푼 정답 갯수
        int vh=0, h=0, n=0, l=0, vl=0, t=0;

        ArrayList<Question> historyList= HistoryList.getInstance().getHistoryList();
        for(Question q: historyList){
            if(q.getInputAnswer().equals(q.getRightAnswer())){
                int potentialFilter= Integer.valueOf(q.getPotential());
                if(potentialFilter>=80){
                    vh++;
                }else if(potentialFilter>=60){
                    h++;
                }else if(potentialFilter>=40){
                    n++;
                }else if(potentialFilter>=20){
                    l++;
                }else{
                    vl++;
                }
                t++;
            }
        }

        dataList.add(new DataPoint(vhigh, vh));
        dataList.add(new DataPoint(high, h));
        dataList.add(new DataPoint(normal, n));
        dataList.add(new DataPoint(low, l));
        dataList.add(new DataPoint(vlow, vl));
        dataList.add(new DataPoint(total, t));

        return dataList;
    }
}
