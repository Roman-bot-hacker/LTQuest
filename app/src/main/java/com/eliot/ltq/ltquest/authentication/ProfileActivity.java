package com.eliot.ltq.ltquest.authentication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.eliot.ltq.ltquest.Balance;
import com.eliot.ltq.ltquest.FirebaseDataManager;
import com.eliot.ltq.ltquest.MainActivity;
import com.eliot.ltq.ltquest.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private FirebaseUser user;
    private UserInformation currentUserInformation = new UserInformation();
    private FirebaseDataManager firebaseDataManager = new FirebaseDataManager();
    private FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager();
    private UserSex userSexInOptions = UserSex.CHOOSE_SEX;

    private TextView textViewUserEmail;
    private TextView textViewName;
    private TextView textViewSex;
    private TextView textViewFacebookLink;
    private TextView textViewGoogleEmail;
    private TextView textViewLayout;
    private LinearLayout facebookLayout;
    private LinearLayout googleLayout;
    private LinearLayout emailLayout;
    private ImageView userSettings;
    private ImageView imageViewUserPhoto;
    private FirebaseAuthManager authManager;
    private Toolbar toolbar;
    private View editProfile;
    private ImageView userPhotoSetttings;
    private TextView userNameSetttings;
    private ImageView maleImageSetttings;
    private ImageView femaleImageSetttings;
    private LinearLayout maleLayoutSetttings;
    private LinearLayout femaleLayoutSetttings;
    private LinearLayout facebookLayoutSetttings;
    private LinearLayout googleLayoutSetttings;
    private LinearLayout mailLayoutSetttings;

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
        textViewLayout = (TextView) findViewById(R.id.logout);
        emailLayout = (LinearLayout) findViewById(R.id.liner_mail);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        userSettings = (ImageView) findViewById(R.id.user_settings);
        configureNavigationDrawer();
        configureToolbar();
        profileUpdate();
        textViewLayout.setOnClickListener(this);
        userSettings.setOnClickListener(this);

    }

    public void profileUpdate() {
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
                    } else {facebookLayout.setVisibility(View.GONE);}
                    textViewGoogleEmail = (TextView) findViewById(R.id.email_google);
                    if(!(userInformation.getGoogleEmail()==null)) {
                        textViewGoogleEmail.setText(userInformation.getGoogleEmail());
                    } else {googleLayout.setVisibility(View.GONE);}
                    textViewUserEmail = (TextView) findViewById(R.id.email_mail);
                    if(!(userInformation.getEmail()==null)) {
                        textViewUserEmail.setText(userInformation.getEmail());
                    } else {emailLayout.setVisibility(View.GONE);}
                    currentUserInformation = userInformation;
                }

                @Override
                public void onError(DatabaseError databaseError) {
                    Log.e("FirebaseDataManager","Can not retrieve userInformation");
                }

            });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout: {
                authManager.logout(new FirebaseAuthManager.UserLoginListener() {
                    @Override
                    public void onSuccess() {
                        AuthActivity.setAuthType(AuthType.LOGIN);
                        currentUserInformation = null;
                        finish();
                        startActivity(new Intent(ProfileActivity.this, AuthActivity.class));
                    }

                    @Override
                    public void onError(String massage) {
                        Log.e("User LogOut: ",massage);
                        Toast.makeText(ProfileActivity.this, "Cannot logout, something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            } break;
            case R.id.user_settings: {
                editProfile = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.edit_options, null);
                AlertDialog.Builder userSettingsDialogBuilder = new AlertDialog.Builder(this);
                userSettingsDialogBuilder
                        .setView(editProfile)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editOptionsOnSaveClicked();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCancelable(false);
                AlertDialog userSettingsDialog = userSettingsDialogBuilder.create();
                userSettingsDialog.show();
                userSettingsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#907AEC"));
                userSettingsDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#907AEC"));
                editOptionsObjectsInit();
                editOptionsLisneter();
            }
        }
    }

    private void configureToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
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
                toolbarUserName.setText(userInformation.getName());
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

    //HERE PART OF CODE FOR EDIT_OPTIONS DIALOG
    public void editOptionsObjectsInit(){
        userPhotoSetttings = editProfile.findViewById(R.id.ava);
        userNameSetttings = editProfile.findViewById(R.id.user_name_options);
        facebookLayoutSetttings = editProfile.findViewById(R.id.facebook_options_layout);
        googleLayoutSetttings = editProfile.findViewById(R.id.google_options_layout);
        mailLayoutSetttings = editProfile.findViewById(R.id.mail_options_layout);
        maleLayoutSetttings = editProfile.findViewById(R.id.male_layout);
        femaleLayoutSetttings = editProfile.findViewById(R.id.female_layout);
        maleImageSetttings = editProfile.findViewById(R.id.on_male_click);
        femaleImageSetttings = editProfile.findViewById(R.id.on_female_click);
        if (currentUserInformation!=null){
            if (currentUserInformation.getName()!=null) userNameSetttings.setText(currentUserInformation.getName());
            if (currentUserInformation.getSex()!=null) {
                switch (currentUserInformation.getSex()) {
                    case MALE: {
                        maleImageSetttings.setImageResource(R.drawable.yes);
                        femaleImageSetttings.setImageResource(R.drawable.no);
                        userSexInOptions = UserSex.MALE;
                    } break;
                    case FEMALE: {
                        maleImageSetttings.setImageResource(R.drawable.no);
                        femaleImageSetttings.setImageResource(R.drawable.yes);
                        userSexInOptions = UserSex.FEMALE;
                    } break;
                    default: {
                        maleImageSetttings.setImageResource(R.drawable.no);
                        femaleImageSetttings.setImageResource(R.drawable.no);
                    }
                }
            }
        }

    }

    public void editOptionsLisneter(){
        maleLayoutSetttings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleImageSetttings.setImageResource(R.drawable.yes);
                femaleImageSetttings.setImageResource(R.drawable.no);
                userSexInOptions = UserSex.MALE;
            }
        });
        femaleLayoutSetttings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleImageSetttings.setImageResource(R.drawable.no);
                femaleImageSetttings.setImageResource(R.drawable.yes);
                userSexInOptions = UserSex.FEMALE;
            }
        });

    }

    public void editOptionsOnSaveClicked() {
        if (currentUserInformation != null) {
            if (!(userNameSetttings.getText().toString().equals(currentUserInformation.getName()))) {
                currentUserInformation.setName(userNameSetttings.getText().toString());
            }
            if (!(userSexInOptions.equals(currentUserInformation.getSex()))) {
                currentUserInformation.setSex(userSexInOptions);
            }
            firebaseDataManager.writeCurrentUserData(user.getUid(),currentUserInformation, new FirebaseDataManager.UserInformationWritingListener() {
                @Override
                public void onSuccess() {
                    profileUpdate();
                }

                @Override
                public void onError() {
                    Toast.makeText(ProfileActivity.this, "Sorry, some problems found. Your settings were canceled", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            firebaseDataManager.getCurrentUserData(user.getUid(), new FirebaseDataManager.DataRetrieveListenerForUserInformation() {
                @Override
                public void onSuccess(UserInformation userInformation) {
                    if (!(userNameSetttings.getText().toString().equals(userInformation.getName()))) {
                        userInformation.setName(userNameSetttings.getText().toString());
                    }
                    if (!(userSexInOptions.equals(userInformation.getSex()))) {
                        userInformation.setSex(userSexInOptions);
                    }
                    firebaseDataManager.writeCurrentUserData(user.getUid(), userInformation, new FirebaseDataManager.UserInformationWritingListener() {
                        @Override
                        public void onSuccess() {
                            profileUpdate();
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(ProfileActivity.this, "Sorry, some problems found. Your settings were canceled", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(DatabaseError databaseError) {
                    Toast.makeText(ProfileActivity.this, "Sorry, some problems found. Your settings were canceled", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //--------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        // do nothing.
    }

}