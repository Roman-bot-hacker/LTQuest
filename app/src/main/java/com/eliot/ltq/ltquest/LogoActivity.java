package com.eliot.ltq.ltquest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.eliot.ltq.ltquest.authentication.AuthActivity;
import com.eliot.ltq.ltquest.authentication.FirebaseAuthManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

public class LogoActivity extends AppCompatActivity {

    private static final int TIME_TO_SHOW_LOGO = 2500;
    FirebaseAuthManager manager = new FirebaseAuthManager();
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        layout = (RelativeLayout) findViewById(R.id.logo_screen_layout);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setVisibility(View.VISIBLE);
                if (manager.isUserLoggedIn()) {
                    finish();
                    startActivity(new Intent(LogoActivity.this, MainActivity.class));
                } else {
                    finish();
                    startActivity(new Intent(LogoActivity.this, AuthActivity.class));
                }
            }
        }, TIME_TO_SHOW_LOGO);
    }
}
