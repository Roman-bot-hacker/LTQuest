package com.eliot.ltq.ltquest.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eliot.ltq.ltquest.FirebaseDataManager;
import com.eliot.ltq.ltquest.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser user;
    UserInformation userInformation = new UserInformation();

    private FirebaseDataManager firebaseDataManager = new FirebaseDataManager();
    private TextView textViewUserEmail;
    private TextView textViewName;
    private ImageView imageViewUserPhoto;
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
        //set email

        //set name
        imageViewUserPhoto = (ImageView) findViewById(R.id.user_avatar);
        //set photo (only if photo != null
        logOut = (TextView) findViewById(R.id.logout);
        logOut.setOnClickListener(this);

        firebaseDataManager.getCurrentUserData(new FirebaseDataManager.DataRetrieveListener() {
            @Override
            public void onSuccess() {
                textViewName = (TextView) findViewById(R.id.name_user);
                textViewName.setText(userInformation.getName());
            }
        });

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
