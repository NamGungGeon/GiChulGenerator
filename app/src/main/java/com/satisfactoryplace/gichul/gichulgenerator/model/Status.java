package com.satisfactoryplace.gichul.gichulgenerator.model;

import java.util.HashMap;

/**
 * Created by WINDOWS7 on 2018-02-16.
 */

public class Status {
    private Status(){}

    public static String nickName;
    public static boolean canUseFreeboard= true;
    public static boolean canUseQna= true;

    public static void setValues(HashMap<String, String> values){
        if(values!= null){
            nickName= values.get("nickName");

            String useFreeboard= values.get("canUseFreeboard");
            if(useFreeboard== null){
                canUseFreeboard= true;
            }else{
                canUseFreeboard= Boolean.valueOf(useFreeboard);
            }

            String useQna= values.get("canUseQna");
            if(useQna== null){
                canUseQna= true;
            }else{
                canUseQna= Boolean.valueOf(useQna);
            }
        }else{
            nickName= null;
            canUseFreeboard= true;
            canUseQna= true;
        }
    }
}
