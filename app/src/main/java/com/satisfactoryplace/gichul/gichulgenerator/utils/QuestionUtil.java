package com.satisfactoryplace.gichul.gichulgenerator.utils;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.satisfactoryplace.gichul.gichulgenerator.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class QuestionUtil {

    public static boolean isValidPeriod(String period_y, String period_m){
        int y= Integer.valueOf(period_y);
        int m= Integer.valueOf(period_m);
        if(y> 2018 || y< 2015){
            return false;
        }
        if(m> 11 || m<3){
            return false;
        }

        // 이 버전에서는 2018년 6월 시험까지 지원
        if(y== 2018 && m>10){
            return false;
        }

        return true;
    }
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
        return "정답률: "+ result;
    }

    @NonNull
    public static String createPeriodM(@NonNull String y, @NonNull String inst){
        String m= null;
        String mList[];
        if(y.equals("2018")){
            switch (inst){
                case "gyoyuk":
                    mList= new String[]{"3", "4", "7", "10"};
                    m= mList[new Random().nextInt(mList.length)];
                    break;
                case "pyeong":
                    mList= new String[]{"6", "9"};
                    m= mList[new Random().nextInt(mList.length)];
                    break;
            }
        }else{
            switch (inst){
                case "sunung":
                    m= "11";
                    break;
                case "gyoyuk":
                    mList= new String[]{"3", "4", "7", "10"};
                    m= mList[new Random().nextInt(mList.length)];
                    break;
                case "pyeong":
                    mList= new String[]{"6", "9"};
                    m= mList[new Random().nextInt(mList.length)];
                    break;
            }
        }
        return m;
    }

    @NonNull
    public static String create_eInst(@NonNull String y, @NonNull String k_inst){
        String e_inst= null;
        if(y.equals("2018")){
            switch (k_inst){
                case "상관없음":
                    String instList[]= {"pyeong", "gyoyuk"};
                    e_inst= instList[new Random().nextInt(instList.length)];
                    break;
                case "교육청":
                    e_inst= "gyoyuk";
                    break;
                case "평가원":
                    e_inst= "pyeong";
                    break;
            }
        }else{
            switch (k_inst){
                case "상관없음":
                    String instList[]= {"sunung", "pyeong", "gyoyuk"};
                    e_inst= instList[new Random().nextInt(instList.length)];
                    break;
                case "교육청":
                    e_inst= "gyoyuk";
                    break;
                case "평가원":
                    e_inst= "pyeong";
                    break;
                case "수능":
                    e_inst= "sunung";
                    break;
            }
        }


        return e_inst;
    }

    @NonNull
    public static String create_kInst(@NonNull String e_inst){
        String k_inst= null;
        switch (e_inst){
            case "gyoyuk":
                k_inst= "교육청";
                break;
            case "pyeong":
                k_inst= "평가원";
                break;
            case "sunung":
                k_inst= "수능";
                break;
        }

        return k_inst;
    }

    public static ArrayList<String> getNumberList_asPotential(HashMap<String, String> potentialList, String filter_prob){
        ArrayList<String> numberList= new ArrayList<>();
        //Decide potential
        if(filter_prob.equals("상관없음")){
            numberList.addAll(potentialList.keySet());
        }else if(filter_prob.equals("매우높음")){
            for(String number: potentialList.keySet()){
                if(Integer.valueOf(potentialList.get(number))>=80){
                    numberList.add(number);
                }
            }
        }else if(filter_prob.equals("높음")){
            for(String number: potentialList.keySet()){
                if(Integer.valueOf(potentialList.get(number))>=60 && Integer.valueOf(potentialList.get(number))<80){
                    numberList.add(number);
                }
            }
        }else if(filter_prob.equals("보통")){
            for(String number: potentialList.keySet()){
                if(Integer.valueOf(potentialList.get(number))>=40 && Integer.valueOf(potentialList.get(number))<60){
                    numberList.add(number);
                }
            }
        }else if(filter_prob.equals("낮음")){
            for(String number: potentialList.keySet()){
                if(Integer.valueOf(potentialList.get(number))>=20 && Integer.valueOf(potentialList.get(number))<40){
                    numberList.add(number);
                }
            }
        }else if(filter_prob.equals("매우낮음")){
            for(String number: potentialList.keySet()){
                if(Integer.valueOf(potentialList.get(number))<20){
                    numberList.add(number);
                }
            }
        }

        return numberList;
    }

    public static String getRandomNumber(ArrayList<String> numberList){
        return numberList.get(new Random().nextInt(numberList.size()));
    }

}
