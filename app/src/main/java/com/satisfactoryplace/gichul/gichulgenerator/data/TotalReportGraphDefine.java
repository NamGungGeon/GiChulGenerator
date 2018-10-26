package com.satisfactoryplace.gichul.gichulgenerator.data;

import android.content.Context;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.satisfactoryplace.gichul.gichulgenerator.model.GraphDefine;
import com.satisfactoryplace.gichul.gichulgenerator.model.Question;
import com.satisfactoryplace.gichul.gichulgenerator.utils.HistoryListUtil;

import java.util.ArrayList;

public class TotalReportGraphDefine implements GraphDefine{
    final int vhigh= 13;
    final int high= 14;
    final int normal= 15;
    final int low= 16;
    final int vlow= 17;
    final int total= 18;
    @Override
    public String getGraphTitle() {
        return "전체 문제풀이 분석";
    }

    @Override
    public DefaultLabelFormatter getLabelFormatter(Context context) {
        return new DefaultLabelFormatter(){
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
                            return "";
                    }
                }else{
                    return super.formatLabel(value, isValueX);
                }
            }
        };
    }

    @Override
    public ArrayList<DataPoint> getAllDataList() {
        ArrayList<DataPoint> dataList= new ArrayList<>();

        //난이도별 푼 문제 갯수
        int vh=0, h=0, n=0, l=0, vl=0;

        ArrayList<Question> historyList= HistoryListUtil.getInstance().getHistoryList();
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
            }else if(potentialFilter>=0){
                vl++;
            }
        }

        dataList.add(new DataPoint(vhigh, vh));
        dataList.add(new DataPoint(high, h));
        dataList.add(new DataPoint(normal, n));
        dataList.add(new DataPoint(low, l));
        dataList.add(new DataPoint(vlow, vl));

        return dataList;
    }

    @Override
    public ArrayList<DataPoint> getRightDataList() {
        ArrayList<DataPoint> dataList= new ArrayList<>();

        //난이도별 푼 정답 갯수
        int vh=0, h=0, n=0, l=0, vl=0;

        ArrayList<Question> historyList= HistoryListUtil.getInstance().getHistoryList();
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
                }else if(potentialFilter>=0){
                    vl++;
                }
            }
        }

        dataList.add(new DataPoint(vhigh, vh));
        dataList.add(new DataPoint(high, h));
        dataList.add(new DataPoint(normal, n));
        dataList.add(new DataPoint(low, l));
        dataList.add(new DataPoint(vlow, vl));

        return dataList;
    }
}
