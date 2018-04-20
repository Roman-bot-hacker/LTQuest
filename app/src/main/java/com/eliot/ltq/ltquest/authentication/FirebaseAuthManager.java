package com.eliot.ltq.ltquest.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eliot.ltq.ltquest.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthManager {

    private FirebaseAuth auth;
    private Activity activity;

    public FirebaseAuthManager(Activity activity) {
        FirebaseApp.initializeApp(activity);
        auth = FirebaseAuth.getInstance();
        this.activity = activity;
    }

    public void registerUser(String email, String password, final UserLoginListener listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(e.getLocalizedMessage());
                    }
                })
                .addOnSuccessListener(activity, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        listener.onSuccess();
                    }
                });

    }

    public void loginUser(String email, String password, final UserLoginListener listener){
        auth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(e.getLocalizedMessage());
                    }
                })
                .addOnSuccessListener(activity, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        listener.onSuccess();
                    }
                });
    }

        public boolean isUserLoggedIn () {
            return auth.getCurrentUser() != null;
        }

        public interface UserLoginListener {
            void onSuccess();

            String onError(String massage);
        }
}
