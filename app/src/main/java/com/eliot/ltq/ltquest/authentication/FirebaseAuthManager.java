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

import com.eliot.ltq.ltquest.FirebaseDataManager;
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
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class FirebaseAuthManager {

    private final FirebaseDataManager firebaseDataManager = new FirebaseDataManager();
    private static FirebaseAuth auth;
    private boolean checkIfEmailInFirebase;


    public FirebaseAuthManager() {
        auth = FirebaseAuth.getInstance();

    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void registerUser(String email, String password, final UserLoginListener listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(e.getLocalizedMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        listener.onSuccess();
                    }
                });

    }

    public void loginUser(String email, String password, final UserLoginListener listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(e.getLocalizedMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        listener.onSuccess();
                    }
                });
    }


    /*public void onActivityResult(int requestCode, int resultCode, Intent data, UserLoginListener listener){
        gSingInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                gSingInAccount = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(gSingInAccount, listener);
            } catch (ApiException e) {
                listener.onError(e.getLocalizedMessage());
            }
        }
        else {
            listener.onError("Cannot sing in with your Google account");
        }
    }*/

    public void firebaseAuthWithGoogle(AuthCredential credential, final UserLoginListener listener) {

        auth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError("Cannot sing in with your Google account");
                    }
                });
    }

    public boolean checkEmailInFirebase(String email){
        auth.fetchProvidersForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        checkIfEmailInFirebase = !task.getResult().getProviders().isEmpty();

                    }
                });
        return checkIfEmailInFirebase;
    }

    /*public void createNewUserWithEmail(String name) {
        UserInformation userInformation = new UserInformation(name);
        firebaseDataManager.writeCurrentUserData(userInformation);
    }*/

    /*public void createNewUserWithGoogle(){
        UserInformation userInformation = new UserInformation(gSingInAccount.getDisplayName());
        firebaseDataManager.writeCurrentUserData(userInformation);
    }*/

    public void signOut() {
        auth.signOut();
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public interface UserLoginListener {
        void onSuccess();

        void onError(String massage);
    }
}
