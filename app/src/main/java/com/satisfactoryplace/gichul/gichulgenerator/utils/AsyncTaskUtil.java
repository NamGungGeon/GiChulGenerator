package com.satisfactoryplace.gichul.gichulgenerator.utils;

import android.os.AsyncTask;

public class AsyncTaskUtil {

    public interface Callback{
        void callback();
    }

    public static void startAsyncTask(final Callback callback){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                callback.callback();
                return null;
            }
        }.execute();
    }
}
