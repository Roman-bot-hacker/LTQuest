package com.eliot.ltq.ltquest.authentication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.eliot.ltq.ltquest.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthManager {

    private FirebaseAuth auth;

    public FirebaseAuthManager(Context context) {
        FirebaseApp.initializeApp(context);
        auth = FirebaseAuth.getInstance();
    }

    public void registerUser(final AuthActivity activity, String email, String password, final UserLoginListener listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            listener.onSuccess();
                            } else {
                            listener.onError();
                            }
                    }
                });

    }

    public void loginUser(AuthActivity activity, String email, String password, final UserLoginListener listener){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            listener.onSuccess();
                        }
                        else{
                            listener.onError();
                        }
                    }
                });
    }

        public boolean isUserLoggedIn () {
            return auth.getCurrentUser() != null;
        }

        public interface UserLoginListener {
            void onSuccess();

            void onError();
        }
}
