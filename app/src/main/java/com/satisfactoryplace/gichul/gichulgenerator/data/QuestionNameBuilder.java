package com.satisfactoryplace.gichul.gichulgenerator.data;

import android.support.annotation.NonNull;
import android.util.Log;

public class QuestionNameBuilder {
    public static QuestionNameBuilder inst;

    public static final int TYPE_KOR= 2222;
    public static final int TYPE_ENG= 3333;

    public String y;
    public String m;
    public String k_inst;
    public String e_inst;
    public String k_sub;
    public String e_sub;
    public String number;
    public String potential;

    public QuestionNameBuilder(@NonNull String y, @NonNull String m, @NonNull String inst, @NonNull String sub, @NonNull String number, @NonNull String potential, int type) {
        switch (type){
            case TYPE_KOR:
                this.k_inst= inst;
                this.k_sub= sub;
                break;
            case TYPE_ENG:
                this.e_inst= inst;
                this.e_inst= sub;
                break;
            default:
                Log.d("Invalid String type", "QuestionNameBuilder's constructor parameter is invalid");
                System.exit(-1);
                break;
        }

        this.y = y;
        this.m = m;
        this.number = number;
        this.potential= potential;
        initInstAndSub();
    }

    public void initInstAndSub(){
        if(k_inst== null && k_sub== null){

        }else if(e_inst== null&& e_sub== null){
            //generate e_inst
            switch (k_inst){
                case "수능":
                case "대학수학능력평가시험":
                    e_inst= "sunung";
                    break;
                case "교육청":
                    e_inst= "gyoyuk";
                    break;
                case "평가원":
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
    public String createFileName(){
        return y+ "_"+ m+ "_"+ e_inst+ "_"+ e_sub+ "_"+ number;
    }
    public String createTitileText(){
        return y+ "년 "+ k_inst+ "\n"+ k_sub+ "과목 "+ m+ "월 시험 "+ number+ "번 문제";
    }
}
