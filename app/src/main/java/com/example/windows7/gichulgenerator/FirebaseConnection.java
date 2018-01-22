package com.example.windows7.gichulgenerator;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        void success(String data);
        void fail(String errorMessage);
    }

    public void loadData(String path, final Callback callback){

        mDatabase = FirebaseDatabase.getInstance().getReference();
        StringTokenizer token= new StringTokenizer(path, "/", false);
        while(token.hasMoreTokens()){
            mDatabase= mDatabase.child(token.nextToken());
        }

        // Read from the database
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.success(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                callback.fail(error.getMessage());
            }
        });
    }

    public void loadImage(String fileName, ImageView imageView, Context context){
        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = storage.getReferenceFromUrl(basicUrl+fileName+ ".png");
        Glide.with(context).using(new FirebaseImageLoader()).load(gsReference).into(imageView);
    }

}
