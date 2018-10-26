package com.satisfactoryplace.gichul.gichulgenerator.utils;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.satisfactoryplace.gichul.gichulgenerator.model.BitmapSaver;

import java.util.ArrayList;

public class BitmapManager {
    private ArrayList<BitmapSaver> bitmaps;
    public BitmapManager(){
        bitmaps= new ArrayList<>();
    }

    public synchronized ArrayList<BitmapSaver> getBitmaps(){
        return bitmaps;
    }
    public synchronized void addBitmap(Bitmap bitmap){
        bitmaps.add(new BitmapSaver(bitmap));
    }

    @NonNull
    public Bitmap getBitmap(int idx){
        for(BitmapSaver saver: bitmaps){
            if(saver.idx== idx){
                return saver.imageBitmap;
            }
        }
        return null;
    }
    public synchronized void addBitmap(Bitmap bitmap, int index){
        bitmaps.add(new BitmapSaver(index, bitmap));
    }
    public void recycleAllBitmaps(){
        for(int i=0; i<bitmaps.size(); i++){
            bitmaps.get(i).imageBitmap.recycle();
        }
        flush();
    }
    private void flush(){
        bitmaps= new ArrayList<>();
    }
}
