package com.satisfactoryplace.gichul.gichulgenerator.server;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.satisfactoryplace.gichul.gichulgenerator.model.ErrorInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by WINDOWS7 on 2018-01-21.
 */

// This class is designed as Singleton pattern
public class FirebaseConnection {

    public interface Callback{
        void success(DataSnapshot snapshot);
        void fail(String errorMessage);
    }
    public interface ImageLoadFinished{
        void success(Bitmap bitmap);
        void fail(Exception e);
    }


    private FirebaseStorage storage= null;
    private DatabaseReference mDatabase= null;

    private String basicUrl= "gs://gichulgenerator.appspot.com/";

    private static FirebaseConnection instance= null;

    private FirebaseConnection(){
        storage = FirebaseStorage.getInstance();
    }

    public static FirebaseConnection getInstance(){
        if(instance== null){
            instance= new FirebaseConnection();
        }
        return instance;
    }

    public void loadData(String path, final Callback callback){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        StringTokenizer token= new StringTokenizer(path, "/", false);
        while(token.hasMoreTokens()){
            mDatabase= mDatabase.child(token.nextToken());
        }

        // Read from the database
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.success(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                callback.fail(error.getMessage());
            }
        });
    }

    public DatabaseReference getReference(String path){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        StringTokenizer token= new StringTokenizer(path, "/", false);
        while(token.hasMoreTokens()){
            mDatabase= mDatabase.child(token.nextToken());
        }

        return mDatabase;
    }

    // Must use this form in callback
    /*
    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
        // Action...
    }
    */
    public void loadDataWithQuery(Query query, final Callback callback){
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.success(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.fail(databaseError.getMessage());
            }
        });
    }

    //No Listener
    public void loadImage(String fileName, final ImageView imageView, Context context){
        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = storage.getReferenceFromUrl(basicUrl+fileName+ ".png");
        Glide.with(context).using(new FirebaseImageLoader()).load(gsReference).asBitmap().skipMemoryCache(true).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                imageView.setImageBitmap(resource);
                PhotoViewAttacher attacher= new PhotoViewAttacher(imageView);
                attacher.update();
            }
        });
    }

    //With Listener
    public void loadImage(String fileName, final ImageView imageView, Context context, final ImageLoadFinished loadFinished){
        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = storage.getReferenceFromUrl(basicUrl+fileName+ ".png");
        Glide.with(context).using(new FirebaseImageLoader()).load(gsReference).asBitmap().skipMemoryCache(true).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                loadFinished.success(resource);

                if(imageView!= null){
                    PhotoViewAttacher attacher= new PhotoViewAttacher(imageView);
                    imageView.setImageBitmap(resource);
                    attacher.update();
                }
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                loadFinished.fail(e);
            }
        });
    }


    public void saveData(String path, Object o){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        StringTokenizer token= new StringTokenizer(path, "/", false);
        while(token.hasMoreTokens()){
            mDatabase= mDatabase.child(token.nextToken());
        }

        mDatabase.setValue(o);
    }
    public void saveErrorInfo(ErrorInfo error){
        final DatabaseReference ref= getReference("error/").push();
        ref.setValue(error);
        mDatabase.push().setValue(error);
    }
}
