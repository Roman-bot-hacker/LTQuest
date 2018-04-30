package com.eliot.ltq.ltquest.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eliot.ltq.ltquest.MainActivity;
import com.eliot.ltq.ltquest.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class FirebaseAuthManager {

    private static final int RC_SIGN_IN = 121;
    private static FirebaseAuth auth;
    private static GoogleSignInAccount gSingInAccount;
    private Activity activity;
    private GoogleSignInOptions gSingInOptions;
    private GoogleSignInClient gSingInClient;

    public FirebaseAuthManager(Activity activity) {
        FirebaseApp.initializeApp(activity);
        auth = FirebaseAuth.getInstance();
        this.activity = activity;
        gSingInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }

    public FirebaseUser getCurrentUser(){
        return auth.getCurrentUser();
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

    public void singInWithGoogle() {
        gSingInClient = GoogleSignIn.getClient(activity, gSingInOptions);
        Intent signInIntent = gSingInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, UserLoginListener listener){
        gSingInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                gSingInAccount = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(gSingInAccount, listener);
                // Signed in successfully, show authenticated UI.
                listener.onSuccess();
            } catch (ApiException e) {
                listener.onError(e.getLocalizedMessage());
            }
        }
        else {
            listener.onError("Cannot sing in with your Google account");
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account, final UserLoginListener listener) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(activity, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError("Cannot sing in with your Google account");
                    }
                });
    }

    public boolean isUserLoggedIn () {
        return auth.getCurrentUser() != null; }

        public interface UserLoginListener {
            void onSuccess();

            String onError(String massage);
        }
}
