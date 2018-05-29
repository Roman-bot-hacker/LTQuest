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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eliot.ltq.ltquest.authentication.FirebaseAuthManager;
import com.eliot.ltq.ltquest.authentication.ProfileActivity;
import com.eliot.ltq.ltquest.authentication.UserInformation;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Administrator on 3/21/2018.
 */

public class Balance extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private FirebaseDataManager firebaseDataManager = new FirebaseDataManager();
    private FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager();
    private EditText editDollars;
    private TextView exchangedPoints;
    private Button buyPointsButton;
    private TextView availblePoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        editDollars = findViewById(R.id.edit_dollars);
        exchangedPoints = findViewById(R.id.points_exchange);
        configureNavigationDrawer();
        configureToolbar();
        pointsExchange();
        buyPoints();
        updatePointsInformation();
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
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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
                    startActivity(new Intent(Balance.this, MainActivity.class));
                }

                if (id == R.id.nav_balance) {

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        navigationView.getMenu().getItem(1).setChecked(true);
    }

    public void setToolbarUserInf() {
        firebaseDataManager.getCurrentUserData(firebaseAuthManager.getCurrentUser().getUid(), new FirebaseDataManager.DataRetrieveListenerForUserInformation() {
            @Override
            public void onSuccess(UserInformation userInformation) {
                TextView toolbarUserName = (TextView) findViewById(R.id.navbar_user_name);
                toolbarUserName.setText(userInformation.getName());
                TextView toolbarEmail = (TextView) findViewById(R.id.navbar_email);
                if (!(userInformation.getGoogleEmail() == null)) {
                    toolbarEmail.setText(userInformation.getGoogleEmail());
                } else if (!(userInformation.getEmail() == null)) {
                    toolbarEmail.setText(userInformation.getEmail());
                } else if (!(userInformation.getFacebookLink() == null)) {
                    toolbarEmail.setText(userInformation.getFacebookLink());
                }
                LinearLayout toolbarProfile = (LinearLayout) findViewById(R.id.navbar_profile);
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

    public void pointsExchange(){
        editDollars.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0) {
                    int dollars = Integer.parseInt(editDollars.getText().toString()) * 10;
                    exchangedPoints.setText(String.valueOf(dollars));
                }
                else {
                    exchangedPoints.setText("");
                }
            }
        });
    }

    public void buyPoints(){
        buyPointsButton = findViewById(R.id.buy_points);
        buyPointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int requestedPoints = Integer.parseInt(exchangedPoints.getText().toString());
                firebaseDataManager.getCurrentUserData(firebaseAuthManager.getCurrentUser().getUid(), new FirebaseDataManager.DataRetrieveListenerForUserInformation() {
                    @Override
                    public void onSuccess(UserInformation userInformation) {
                        int currentPoints = userInformation.getPoints();
                        int pointsToWrite = currentPoints + requestedPoints;
                        firebaseDataManager.writeUserPoints(firebaseAuthManager.getCurrentUser().getUid(), pointsToWrite);
                        updatePointsInformation();
                    }

                    @Override
                    public void onError(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public void updatePointsInformation(){
        availblePoints = findViewById(R.id.available_points);
        firebaseDataManager.getCurrentUserData(firebaseAuthManager.getCurrentUser().getUid(), new FirebaseDataManager.DataRetrieveListenerForUserInformation() {
            @Override
            public void onSuccess(UserInformation userInformation) {
                availblePoints.setText(String.valueOf(userInformation.getPoints()));
            }

            @Override
            public void onError(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }
}
