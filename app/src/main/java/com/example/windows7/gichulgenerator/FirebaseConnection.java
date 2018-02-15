package com.example.windows7.gichulgenerator;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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

import java.io.File;
import java.util.StringTokenizer;
/**
 * Created by WINDOWS7 on 2018-01-21.
 */

// This class is designed as Singleton pattern
public class FirebaseConnection {
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

    public interface Callback{
        void success(DataSnapshot snapshot);
        void fail(String errorMessage);
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

    public void loadImage(String fileName, ImageView imageView, Context context){
        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = storage.getReferenceFromUrl(basicUrl+fileName+ ".png");
        Glide.with(context).using(new FirebaseImageLoader()).load(gsReference).into(imageView);
    }


    public void saveData(String path, Object o){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        StringTokenizer token= new StringTokenizer(path, "/", false);
        while(token.hasMoreTokens()){
            mDatabase= mDatabase.child(token.nextToken());
        }

        mDatabase.setValue(o);
    }

    public void uploadImage(String key, File localFile, final Callback callback){
        if(localFile== null || localFile.length()>= 1024*512){
            callback.fail("512KB 이하의 파일만 업로드 할 수 있습니다.");
        }

        Uri file = Uri.fromFile(localFile);
        StorageReference riversRef = storage.getReference().child("freeboard/"+key+".jpg");
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                callback.fail(exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                callback.success(null);
            }
        });
    }


}
