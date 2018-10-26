package com.satisfactoryplace.gichul.gichulgenerator.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ViewUtil {
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
