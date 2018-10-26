package com.satisfactoryplace.gichul.gichulgenerator.utils;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.satisfactoryplace.gichul.gichulgenerator.R;

import java.util.ArrayList;

public class AnswerUtil {
    public static boolean isValidAnswer(String input){
        for(int i=0; i<input.length(); i++){
            if(input.charAt(i)<'0' || input.charAt(i)>'9'){
                // included not number character in String
                return false;
            }
        }
        return true;
    }

    public static void answerCheck(@NonNull TextView answerCheck, @NonNull Resources resources, int input, int right, int potential){
        String checkMessage;
        if(input== right){
            checkMessage= "정답입니다!\n입력하신 답안은 "+input+ "입니다.\n\n";
            answerCheck.setTextColor(resources.getColor(R.color.green));
        }else{
            if(input<0){
                checkMessage= "정답을 입력하지 않으셨습니다. 정답은 "+ right+ "입니다\n\n";
            }else{
                checkMessage= "오답입니다!\n입력하신 답안은 "+ input+ "이지만, 정답은 "+ right+ "입니다.\n\n";
            }
            answerCheck.setTextColor(resources.getColor(R.color.red));
        }

        checkMessage+= QuestionUtil.getPotentialText(potential);
        answerCheck.setText(checkMessage);
    }

    public static int getRightAnswerNumber(ArrayList<Integer> inputAnswers, ArrayList<Long> rightAnswers){
        int rightNumber= 0;
        for(int i=0; i<30; i++){
            if(inputAnswers.get(i).intValue()== rightAnswers.get(i+1).intValue()){
                rightNumber++;
            }
        }
        return rightNumber;
    }
}
