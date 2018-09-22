package com.satisfactoryplace.gichul.gichulgenerator.utils;

import android.widget.Toast;

public class AnswerChecker {
    public static boolean isValidAnswer(String input){
        for(int i=0; i<input.length(); i++){
            if(input.charAt(i)<'0' || input.charAt(i)>'9'){
                // included not number character in String
                return false;
            }
        }
        return true;
    }
}
