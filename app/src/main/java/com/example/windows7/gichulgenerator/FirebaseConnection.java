package com.example.windows7.gichulgenerator;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    public void loadImage(String fileName, ImageView imageView, Context context){
        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = storage.getReferenceFromUrl(basicUrl+fileName+ ".png");
        Glide.with(context).using(new FirebaseImageLoader()).load(gsReference).into(imageView);
    }

    public String loadData(final String path, final Context context){
        final String[] data = new String[1];
        data[0]= "TEST";
        Thread networkConnection= new Thread() {
            @Override
            public void run() {

                mDatabase = FirebaseDatabase.getInstance().getReference().child(path);
                // Read from the database
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        data[0] = dataSnapshot.getValue(String.class);
                        Toast.makeText(context, data[0], Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        data[0] = "네트워크 연결이 불안정합니다";
                        Toast.makeText(context, data[0], Toast.LENGTH_LONG).show();
                    }
                });

            }
        };

        networkConnection.start();
        try {
            networkConnection.join();
        } catch (InterruptedException e) {
            return "네트워크 연결이 불안정합니다";
        }


        return data[0];
    }
}
