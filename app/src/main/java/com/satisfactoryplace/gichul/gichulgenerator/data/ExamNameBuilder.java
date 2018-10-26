package com.satisfactoryplace.gichul.gichulgenerator.data;

import android.support.annotation.NonNull;
import android.util.Log;

public class ExamNameBuilder {
    public static ExamNameBuilder inst;

    public static final int TYPE_KOR= 2222;
    public static final int TYPE_ENG= 3333;

    public static final int TYPE_Q= 13000;
    public static final int TYPE_A= 13001;
    public static final int TYPE_POTENTIAL= 15000;
    public static final int TYPE_ANSWER= 15001;

    public String y;
    public String m;
    public String k_inst;
    public String e_inst;
    public String k_sub;
    public String e_sub;

    public ExamNameBuilder(@NonNull String y, @NonNull String m, @NonNull String inst, @NonNull String sub, int type) {
        switch (type){
            case TYPE_KOR:
                this.k_inst= inst;
                this.k_sub= sub;
                break;
            case TYPE_ENG:
                this.e_inst= inst;
                this.e_sub= sub;
                break;
            default:
                Log.d("Invalid String type", "ExamNameBuilder's constructor parameter is invalid");
                System.exit(-1);
                break;
        }

        this.y = y;
        this.m = m;
        initInstAndSub();
    }

    public void initInstAndSub(){
        if(k_inst== null && k_sub== null){
            switch (e_inst){
                case "sunung":
                    k_inst= "대학수학능력평가시험";
                    break;
                case "gyoyuk":
                    k_inst= "교육청";
                    break;
                case "pyeong":
                    k_inst= "교육과정평가원";
                    break;
            }
            switch (e_sub){
                case "imath":
                    k_sub= "수학(이과)";
                    break;
                case "mmath":
                    k_sub= "수학(문과)";
                    break;
            }
        }else if(e_inst== null&& e_sub== null){
            //generate e_inst
            switch (k_inst){
                case "대학수학능력평가시험":
                    e_inst= "sunung";
                    break;
                case "교육청":
                    e_inst= "gyoyuk";
                    break;
                case "교육과정평가원":
                    e_inst= "pyeong";
                    break;
            }
            //generate e_sub
            switch (k_sub){
                case "수학(이과)":
                    e_sub= "imath";
                    break;
                case "수학(문과)":
                    e_sub= "mmath";
                    break;
            }
        }
    }
    @NonNull
    public String createFileName(){
        return y+ "_"+ m+ "_"+ e_inst+ "_"+ e_sub;
    }
    @NonNull
    public String createTitleText(){
        String titleText= y+ "년 "+ k_inst+ "\n"+ k_sub+ "과목 "+ m+ "월 시험";
        return titleText;
    }
    @NonNull
    public String createPath(int type){
        String typeText= null;
        switch (type){
            case TYPE_POTENTIAL:
                typeText= "potential";
                break;
            case TYPE_ANSWER:
                typeText= "answer";
                break;
        }
        String path= typeText+ "/"+ y+ "/"+ e_inst+ "/"+ m+ "/"+ e_sub;
        return path;
    }
    @NonNull
    public String createImagePath(int type, @NonNull String number){
        String typeText;
        switch (type){
            case TYPE_A:
                typeText= "a_";
                break;
            case TYPE_Q:
                typeText= "q_";
                break;
            default:
                return null;
        }

        return "exam/"+ createFileName()+ "/"
                + typeText+ createFileName()+ "_"+ number;
    }
}
