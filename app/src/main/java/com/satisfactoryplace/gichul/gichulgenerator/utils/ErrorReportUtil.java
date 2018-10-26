package com.satisfactoryplace.gichul.gichulgenerator.utils;

import com.satisfactoryplace.gichul.gichulgenerator.model.ErrorInfo;
import com.satisfactoryplace.gichul.gichulgenerator.server.FirebaseConnection;

public class ErrorReportUtil {
    public static void report(ErrorInfo error){
        try{
            FirebaseConnection.getInstance().saveErrorInfo(error);
        }catch(Exception e){
            //No Action
        }
    }
}
