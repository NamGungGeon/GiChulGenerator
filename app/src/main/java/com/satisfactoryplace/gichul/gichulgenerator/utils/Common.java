package com.satisfactoryplace.gichul.gichulgenerator.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

public class Common {
    public static <T> T[] initArray(T[] array, T value){
        for(int i=0; i<array.length; i++){
            array[i]= value;
        }
        return array;
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

    public static void makeCanMagnify(ImageView imageView){
        PhotoViewAttacher attacher1= new PhotoViewAttacher(imageView);
        attacher1.update();
    }

    public static void imageViewResize_fillDisplayWidth(int displayWidth, Bitmap bitmap, ImageView imageView){
        float magnifyScale= (float)displayWidth/(float)bitmap.getWidth();
        imageView.getLayoutParams().height= (int)((float)bitmap.getHeight()* magnifyScale);
        imageView.getLayoutParams().width= (int)((float)bitmap.getWidth()* magnifyScale);
        imageView.requestLayout();
    }

}
