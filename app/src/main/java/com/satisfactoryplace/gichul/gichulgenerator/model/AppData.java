package com.satisfactoryplace.gichul.gichulgenerator.model;

import java.util.HashMap;

/**
 * Created by WINDOWS7 on 2018-02-17.
 */

public class AppData {
    public static String currentVersion= null;
    public static String freeboardStatus= null;
    public static String qnaStatus= null;

    public static void setValue(HashMap<String, String> value) {
        if (value != null) {
            currentVersion = value.get("currentVersion");
            freeboardStatus = value.get("freeboardStatus");
            qnaStatus = value.get("qnaStatus");
        }
    }

}
