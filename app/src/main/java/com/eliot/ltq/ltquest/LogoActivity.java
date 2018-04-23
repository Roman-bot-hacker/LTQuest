package com.eliot.ltq.ltquest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.eliot.ltq.ltquest.authentication.AuthActivity;
import com.eliot.ltq.ltquest.authentication.FirebaseAuthManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

public class LogoActivity extends AppCompatActivity {

    FirebaseAuthManager manager = new FirebaseAuthManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        if((manager.isUserLoggedIn())||(manager.isGoogleAccountEnabled())){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        else {
            finish();
            startActivity(new Intent(this, AuthActivity.class));
        }
    }
}
