package com.eliot.ltq.ltquest.authentication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseAuthManager {

    private FirebaseAuth auth;

    public FirebaseAuthManager(Context context) {
        FirebaseApp.initializeApp(context);
        auth = FirebaseAuth.getInstance();
    }

    public void registerUser(AuthType authType, Context context, String email, String password, String name, UserLoginListener listener) {
        switch (authType) {
            case REGISTRATION: {
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(context, "Please, enter name", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(context, "Please, enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(context, "Please, enter password", Toast.LENGTH_SHORT).show();
                    return;
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                }
                            })
                }
            }
        }
    }

        public boolean isUserLoggedIn () {
            return auth.getCurrentUser() != null;
        }

        public interface UserLoginListener {
            void onSuccess();

            void onError();
        }
}
