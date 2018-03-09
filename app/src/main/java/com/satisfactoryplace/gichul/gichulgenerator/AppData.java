package com.satisfactoryplace.gichul.gichulgenerator;

import java.util.HashMap;

/**
 * Created by WINDOWS7 on 2018-02-17.
 */

public class AppData {
    static String currentVersion= null;
    static String freeboardStatus= null;
    static String qnaStatus= null;

    public static void setValue(HashMap<String, String> value) {
        if (value != null) {
            currentVersion = value.get("currentVersion");
            freeboardStatus = value.get("freeboardStatus");
            qnaStatus = value.get("qnaStatus");
        }
    }

}
