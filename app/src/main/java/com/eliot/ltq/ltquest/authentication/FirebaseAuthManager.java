package com.eliot.ltq.ltquest.authentication;

import android.content.Intent;
import android.support.annotation.NonNull;

import android.widget.Toast;
import com.eliot.ltq.ltquest.FirebaseDataManager;
import com.eliot.ltq.ltquest.MainActivity;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class FirebaseAuthManager {

    private final FirebaseDataManager firebaseDataManager = new FirebaseDataManager();
    private static FirebaseAuth auth;


    public FirebaseAuthManager() {
        auth = FirebaseAuth.getInstance();

    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public static FirebaseAuth getAuth() {
        return auth;
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
                        boolean isNewUser = authResult.getAdditionalUserInfo().isNewUser();
                        AuthActivity.setIsNewUser(isNewUser);
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

    public void logout(UserLoginListener listener){
        try {
            auth.signOut();
            listener.onSuccess();
        } catch (Exception e) {
            listener.onError(e.getLocalizedMessage());
        }
    }

    public void deleteUser(FirebaseUser user, final UserLoginListener listener){
        user.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(e.getLocalizedMessage());
                    }
                });
    }


    public void firebaseAuthWithGoogle(AuthCredential credential, final UserLoginListener listener) {

        auth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if(authResult.getAdditionalUserInfo().isNewUser()){
                            AuthActivity.setIsNewUser(true);
                        }
                        else {
                            AuthActivity.setIsNewUser(false);
                        }
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

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public interface UserLoginListener {
        void onSuccess();

        void onError(String massage);
    }
}