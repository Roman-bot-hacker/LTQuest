package com.eliot.ltq.ltquest.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eliot.ltq.ltquest.FirebaseDataManager;
import com.eliot.ltq.ltquest.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser user;
    UserInformation userInformation = new UserInformation();

    private FirebaseDataManager firebaseDataManager;
    private TextView textViewUserEmail;
    private TextView textViewName;
    private TextView textViewSex;
    private TextView textViewFacebookLink;
    private TextView textViewGoogleEmail;
    private LinearLayout facebookLayout;
    private LinearLayout googleLayout;
    private LinearLayout emailLayout;
    private ImageView imageViewUserPhoto;
    private FirebaseAuthManager authManager;
    private TextView logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_profile);

        FirebaseApp.initializeApp(this);
        firebaseDataManager = new FirebaseDataManager();
        authManager = new FirebaseAuthManager();
        user = authManager.getCurrentUser();

        if (user == null) {
            AuthActivity.setAuthType(AuthType.LOGIN);
            finish();
            startActivity(new Intent(ProfileActivity.this, AuthActivity.class));
        }

        imageViewUserPhoto = (ImageView) findViewById(R.id.avatar);
        facebookLayout = (LinearLayout) findViewById(R.id.liner_facebook);
        googleLayout = (LinearLayout) findViewById(R.id.liner_google);
        emailLayout = (LinearLayout) findViewById(R.id.liner_mail);


        if (authManager.isUserLoggedIn())
            firebaseDataManager.getCurrentUserData(authManager.getCurrentUser().getUid(), new FirebaseDataManager.DataRetrieveListenerForUserInformation() {
                @Override
                public void onSuccess(UserInformation userInformation) {
                    textViewName = (TextView) findViewById(R.id.name_user);
                    textViewName.setText(userInformation.getName());
                    textViewSex = (TextView) findViewById(R.id.sex);
                    switch (userInformation.getSex()){
                        case MALE: { textViewSex.setText("Male"); } break;
                        case FEMALE: { textViewSex.setText("Female"); } break;
                        default: {textViewSex.setText("Choose sex"); }
                    };
                    textViewFacebookLink = (TextView) findViewById(R.id.email_facebok);
                    if(userInformation.getFacebookLink()==null) {facebookLayout.setVisibility(View.GONE); }
                    else { textViewFacebookLink.setText(userInformation.getFacebookLink()); }
                    textViewGoogleEmail = (TextView) findViewById(R.id.email_google);
                    if(userInformation.getGoogleEmail()==null) {googleLayout.setVisibility(View.GONE);}
                    else {textViewGoogleEmail.setText(userInformation.getGoogleEmail());}
                    textViewUserEmail = (TextView) findViewById(R.id.email_mail);
                    if(userInformation.getEmail()==null){emailLayout.setVisibility(View.GONE);}
                    else {textViewUserEmail.setText(userInformation.getEmail());}
                }

                @Override
                public void onError(DatabaseError databaseError) {
                    Log.e("FirebaseDataManager","Can not retrieve userInformation");
                }

            });

    }

    @Override
    public void onClick(View view) {

    }
}
