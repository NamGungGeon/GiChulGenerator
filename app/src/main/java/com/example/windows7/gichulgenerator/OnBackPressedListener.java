package com.example.windows7.gichulgenerator;

/**
 * Created by WINDOWS7 on 2018-02-04.
 */

//This interface is only used to activity and fragment where you want to call onBackPressed() in.
public interface OnBackPressedListener {
    //if return value is true, continue execute onBackPressed() in activity.
    // but false, don't execute next code.
    boolean onBackPressed();
}
