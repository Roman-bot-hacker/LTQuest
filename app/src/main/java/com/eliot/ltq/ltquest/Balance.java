package com.eliot.ltq.ltquest;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eliot.ltq.ltquest.authentication.FirebaseAuthManager;
import com.eliot.ltq.ltquest.authentication.ProfileActivity;
import com.eliot.ltq.ltquest.authentication.UserInformation;
import com.google.firebase.database.DatabaseError;

/**
 * Created by Administrator on 3/21/2018.
 */

public class Balance extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private FirebaseDataManager firebaseDataManager = new FirebaseDataManager();
    private FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager();
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance);
        configureToolbar();
        drawerLayout = findViewById(R.id.drawer_layout);
        configureNavigationDrawer();
        setNavigationHeaderUserInf();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void configureToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionbar.setDisplayHomeAsUpEnabled(true);
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

    private void configureNavigationDrawer() {
       navigationView = findViewById(R.id.nav_view);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

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
                    startActivity(new Intent(Balance.this, MainActivity.class));
                }

                if (id == R.id.nav_balance) {

                }

                if (id == R.id.nav_settings) {
                    startActivity(new Intent(Balance.this, ProfileActivity.class));
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public void setNavigationHeaderUserInf() {
        firebaseDataManager.getCurrentUserData(firebaseAuthManager.getCurrentUser().getUid(), new FirebaseDataManager.DataRetrieveListenerForUserInformation() {
            @Override
            public void onSuccess(UserInformation userInformation) {
                TextView toolbarUserName = navigationView.getHeaderView(0).findViewById(R.id.toolbar_user_name);
                toolbarUserName.setText(userInformation.getName());
                TextView toolbarEmail = navigationView.getHeaderView(0).findViewById(R.id.toolbarEmail);
                if (!(userInformation.getGoogleEmail() == null)) {
                    toolbarEmail.setText(userInformation.getGoogleEmail());
                } else if (!(userInformation.getEmail() == null)) {
                    toolbarEmail.setText(userInformation.getEmail());
                } else if (!(userInformation.getFacebookLink() == null)) {
                    toolbarEmail.setText(userInformation.getFacebookLink());
                }
                LinearLayout toolbarProfile = navigationView.getHeaderView(0).findViewById(R.id.toolbarProfile);
                toolbarProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Balance.this, ProfileActivity.class));
                    }
                });
            }

            @Override
            public void onError(DatabaseError databaseError) {
                Log.e("FirebaseDataManager", "Can not retrieve userInformation");
            }
        });
    }
}
