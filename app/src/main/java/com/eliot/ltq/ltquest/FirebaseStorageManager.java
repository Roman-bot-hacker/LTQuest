package com.eliot.ltq.ltquest;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseStorageManager {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference testImageRef = storage.getReference().child("circle-6-150x150.png");

    public void setImageFromStorage(){}

}
