package com.eliot.ltq.ltquest.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.eliot.ltq.ltquest.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser user;
    UserInformation userInformation = new UserInformation();

    private TextView textViewUserEmail;
    private TextView textViewName;
    private FirebaseAuthManager authManager;
    private TextView logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseApp.initializeApp(this);
        authManager = new FirebaseAuthManager(this);
        user = authManager.getCurrentUser();

        if(user == null){
            AuthActivity.setAuthType(AuthType.REGISTRATION);
            finish();
            startActivity(new Intent(ProfileActivity.this, AuthActivity.class));
        }

        textViewUserEmail = (TextView) findViewById(R.id.email);
        textViewUserEmail.setText(user.getEmail());
        textViewName = (TextView) findViewById(R.id.name_user);
        textViewName.setText(userInformation.getName());
        logOut = (TextView) findViewById(R.id.logout);
        logOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == logOut){
            authManager.signOut();
            AuthActivity.setAuthType(AuthType.LOGIN);
            finish();
            startActivity(new Intent(ProfileActivity.this, AuthActivity.class));
        }
    }
}
