package com.eliot.ltq.ltquest.authentication;

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

    public FirebaseAuthManager(Context context) {
        FirebaseApp.initializeApp(context);
        auth = FirebaseAuth.getInstance();
    }

    public String getEmail(EditText editTextEmail){
        String email = editTextEmail.getText().toString().trim();
        return email;
    }

    public String getPassword(EditText editTextPassword){
        String password = editTextPassword.getText().toString().trim();
        return password;
    }

    public void isFieldEmpty(AuthActivity activity, String field){
        if (TextUtils.isEmpty(field)) {
            Toast.makeText(activity, "Please, enter name", Toast.LENGTH_LONG).show();
            //Some code to break login or registration method
        }
    }

    public void registerUser(final AuthActivity activity, EditText editTextEmail, EditText editTextPassword, final UserLoginListener listener) {
        isFieldEmpty(activity, getEmail(editTextEmail));
        isFieldEmpty(activity, getPassword(editTextPassword));
        auth.createUserWithEmailAndPassword(getEmail(editTextEmail), getPassword(editTextPassword))
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError();
                    }
                })
                .addOnSuccessListener(activity, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        listener.onSuccess();
                    }
                });

    }

    public void loginUser(AuthActivity activity, EditText editTextEmail, EditText editTextPassword, final UserLoginListener listener){
        isFieldEmpty(activity, getEmail(editTextEmail));
        isFieldEmpty(activity, getPassword(editTextPassword));
        auth.signInWithEmailAndPassword(getEmail(editTextEmail), getPassword(editTextPassword))
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
