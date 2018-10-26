package com.satisfactoryplace.gichul.gichulgenerator.model;

import android.content.Context;
import android.support.annotation.ColorInt;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import java.util.ArrayList;

public interface GraphDefine {
    String getGraphTitle();
    DefaultLabelFormatter getLabelFormatter(Context context);
    ArrayList<DataPoint> getAllDataList();
    ArrayList<DataPoint> getRightDataList();
}
