package com.satisfactoryplace.gichul.gichulgenerator.utils;

public class QuestionUtil {
    public static String getPotentialText(int potential){
        String result;
        if(potential>=80){
            result= "매우높음";
        }else if(potential>=60){
            result= "높음";
        }else if(potential>= 40){
            result= "보통";
        }else if(potential>= 20){
            result= "낮음";
        }else{
            result= "매우낮음";
        }
        return result;
    }
}
