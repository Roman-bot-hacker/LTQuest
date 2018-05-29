package com.eliot.ltq.ltquest;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FirebaseStorageManager {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    public StorageReference getStorageRef() {
        return storageRef;
    }

    public void setImageIntoImageView(Context context, String path, ImageView imageView) {
        StorageReference reference = storageRef.child(path);
        Glide.with(context)
                .load(reference)
                .into(imageView);
    }
}
