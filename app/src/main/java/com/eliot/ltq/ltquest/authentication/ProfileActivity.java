package com.eliot.ltq.ltquest.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eliot.ltq.ltquest.FirebaseDataManager;
import com.eliot.ltq.ltquest.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser user;
    UserInformation userInformation = new UserInformation();

    private FirebaseDataManager firebaseDataManager;
    private TextView textViewUserEmail;
    private TextView textViewName;
    private ImageView imageViewUserPhoto;
    private FirebaseAuthManager authManager;
    private TextView logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseApp.initializeApp(this);
        firebaseDataManager = new FirebaseDataManager();
        authManager = new FirebaseAuthManager();
        user = authManager.getCurrentUser();

        if (user == null) {
            AuthActivity.setAuthType(AuthType.LOGIN);
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

        if (authManager.isUserLoggedIn())
            firebaseDataManager.getCurrentUserData(authManager.getCurrentUser().getUid(), new FirebaseDataManager.DataRetrieveListenerForUserInformation() {
                @Override
                public void onSuccess(UserInformation userInformation) {
                    textViewName = (TextView) findViewById(R.id.name_user);
                    textViewName.setText(userInformation.getName());
                }

                @Override
                public void onError(DatabaseError databaseError) {
                    Log.e("FirebaseDataManager","Can not retrieve userInformation");
                }

            });

    }

    @Override
    public void onClick(View view) {
        if (view == logOut) {
            authManager.signOut();
            AuthActivity.setAuthType(AuthType.LOGIN);
            finish();
            startActivity(new Intent(ProfileActivity.this, AuthActivity.class));
        }
    }
}
