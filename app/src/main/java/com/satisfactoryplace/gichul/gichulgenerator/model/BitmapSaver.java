package com.satisfactoryplace.gichul.gichulgenerator.model;

import android.graphics.Bitmap;

public class BitmapSaver {
    public int idx;
    public Bitmap imageBitmap;

    public BitmapSaver(int idx, Bitmap imageBitmap) {
        this.idx= idx;
        this.imageBitmap = imageBitmap;
    }
    public BitmapSaver(Bitmap imageBitmap){
        this.idx= 0;
        this.imageBitmap= imageBitmap;
    }
}
