package com.eliot.ltq.ltquest.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eliot.ltq.ltquest.Balance;
import com.eliot.ltq.ltquest.FirebaseDataManager;
import com.eliot.ltq.ltquest.MainActivity;
import com.eliot.ltq.ltquest.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private FirebaseUser user;
    private UserInformation userInformation = new UserInformation();
    private FirebaseDataManager firebaseDataManager = new FirebaseDataManager();
    private FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager();

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

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        configureNavigationDrawer();
        configureToolbar();

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
                    if(!(userInformation.getFacebookLink()==null)) {
                        textViewFacebookLink.setText(userInformation.getFacebookLink());
                    } else {textViewFacebookLink.setText(" ");}
                    textViewGoogleEmail = (TextView) findViewById(R.id.email_google);
                    if(!(userInformation.getGoogleEmail()==null)) {
                        textViewGoogleEmail.setText(userInformation.getGoogleEmail());
                    } else {textViewGoogleEmail.setText(" ");}
                    textViewUserEmail = (TextView) findViewById(R.id.email_mail);
                    if(!(userInformation.getEmail()==null)) {
                        textViewUserEmail.setText(userInformation.getEmail());
                    } else {textViewUserEmail.setText(" ");}
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

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("User profile");
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private void configureNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                setToolbarUserInf();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                }

                if (id == R.id.nav_balance) {
                    startActivity(new Intent(ProfileActivity.this, Balance.class));
                }

                if (id == R.id.nav_settings) {

                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        switch (itemId) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return true;
    }

    public void setToolbarUserInf(){
        firebaseDataManager.getCurrentUserData(firebaseAuthManager.getCurrentUser().getUid(), new FirebaseDataManager.DataRetrieveListenerForUserInformation() {
            @Override
            public void onSuccess(UserInformation userInformation) {
                TextView toolbarUserName = (TextView) findViewById(R.id.toolbar_user_name);
                //toolbarUserName.setText(userInformation.getName());
                TextView toolbarEmail = (TextView) findViewById(R.id.toolbarEmail);
                if(!(userInformation.getGoogleEmail()==null)){
                    toolbarEmail.setText(userInformation.getGoogleEmail());
                }
                else if(!(userInformation.getEmail()==null)){
                    toolbarEmail.setText(userInformation.getEmail());
                }
                else if(!(userInformation.getFacebookLink()==null)){
                    toolbarEmail.setText(userInformation.getFacebookLink());
                }
            }

            @Override
            public void onError(DatabaseError databaseError) {
                Log.e("FirebaseDataManager","Can not retrieve userInformation");
            }
        });
    }

}
