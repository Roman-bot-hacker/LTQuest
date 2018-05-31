package com.eliot.ltq.ltquest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.eliot.ltq.ltquest.authentication.FirebaseAuthManager;
import com.eliot.ltq.ltquest.authentication.ProfileActivity;
import com.eliot.ltq.ltquest.authentication.UserInformation;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;

import java.util.List;

import static com.eliot.ltq.ltquest.R.drawable;
import static com.eliot.ltq.ltquest.R.id;
import static com.eliot.ltq.ltquest.R.layout;
import static com.eliot.ltq.ltquest.R.raw;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private GoogleMap mMap;
    private LocationManager locationManager;
    private boolean firstCameraOnMyPosition = true;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 111;
    private static final double DEFAULT_LATITUDE = 49.841787;
    private static final double DEFAULT_LONGITUDE = 24.031686;
    private Marker mPositionMarker;
    private Toolbar toolbar;
    private View myLocationButton;
    private View screen1;
    private View screen2;
    private DrawerLayout drawerLayout;
    private LatLng currentLatLng = new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
    private FirebaseDataManager firebaseDataManager = new FirebaseDataManager();
    private FirebaseAuthManager firebaseAuthManager;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        firebaseAuthManager = new FirebaseAuthManager();
        setContentView(layout.activity_main);
        setCategoriesText();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(id.map);
        mapFragment.getMapAsync(this);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar = findViewById(R.id.toolbar);
        screen1 = findViewById(id.screen1);
        screen2 = findViewById(id.screen2);
        screen1.setVisibility(View.VISIBLE);
        screen2.setVisibility(View.GONE);
        screen1ButtonsOnClickListener();
        screen2ButtonsOnClickListener();
        configureNavigationDrawer();
        configureToolbarForFirstScreen();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (isNetworkProviderEnabled()) {
            askMyLocationPermissions();
            checkMyFineLocationUpdates();
            checkMyCoarseLocationUpdates();
        }
        else return;
    }

    @Override
    public void onMapReady (GoogleMap googleMap){
        mMap = googleMap;
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)||(ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            myLocationButton = (View) findViewById(id.myLocationButton);
            myLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f));
                }
            });
            mPositionMarker = mMap.addMarker(new MarkerOptions()
                    .flat(false)
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmap(drawable.current_position)))
                    .anchor(0.5f, 1f)
                    .position(currentLatLng)
                    .draggable(false));
        }

        try {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    this, raw.silver_style_maps));}
        catch (Resources.NotFoundException e) {
            e.getMessage();
        }
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    private LatLng getMyLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        return latLng;
    }

    void askMyLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            com.eliot.ltq.ltquest.PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            com.eliot.ltq.ltquest.PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_COARSE_LOCATION, true);
        }
    }

    public boolean isNetworkProviderEnabled() {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void checkMyFineLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                    1, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            if (location == null)
                                return;
                            if (firstCameraOnMyPosition) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getMyLocation(location), 17f));
                                firstCameraOnMyPosition = false;
                            }
                            currentLatLng = getMyLocation(location);
                            mPositionMarker.setPosition(currentLatLng);
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {

                        }

                        @Override
                        public void onProviderDisabled(String s) {

                        }
                    });
        }
    }

    public void checkMyCoarseLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location == null)
                        return;
                    if (firstCameraOnMyPosition) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getMyLocation(location), 17f));
                        firstCameraOnMyPosition = false;
                    }
                    currentLatLng = getMyLocation(location);
                    mPositionMarker.setPosition(currentLatLng);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        }
    }

    public Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id==R.id.nav_home){

        }

        if (id== R.id.nav_balance){
            startActivity(new Intent(MainActivity.this, Balance.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void screen1ButtonsOnClickListener() {
            Button startNew = findViewById(id.start_new);
            Button continueQuest = findViewById(id.continue_quest);
            startNew.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isNetworkAvailable()) {
                        screen1.setVisibility(View.GONE);
                        screen2.setVisibility(View.VISIBLE);
                        configureToolbarForSecondScreen();
                    } else {
                        Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            continueQuest.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isNetworkAvailable()){
                        Toast.makeText(MainActivity.this, "This function is disable in this app version", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    public void setCategoriesText(){
        final TextView firstButtonText = findViewById(R.id.button0_text);
        final TextView secondButtonText = findViewById(R.id.button1_text);
        final TextView thirdButtonText = findViewById(R.id.button2_text);
        firebaseDataManager.categoriesNamesListRetriever(new FirebaseDataManager.DataRetrieveListenerForQuestCategory(){
            @Override
            public void onSuccess(List<QuestCategory> questCategoryList) {
                firstButtonText.setText(questCategoryList.get(0).getName());
                secondButtonText.setText(questCategoryList.get(1).getName());
                thirdButtonText.setText(questCategoryList.get(2).getName());
            }

            @Override
            public void onError(DatabaseError databaseError) {
                Log.e("FirebaseDataManager", "Can not retrieve QuestCategory");
            }
        });
    }


    public void setNavbarUserInf(){
        firebaseDataManager.getCurrentUserData(firebaseAuthManager.getCurrentUser().getUid(), new FirebaseDataManager.DataRetrieveListenerForUserInformation() {
            @Override
            public void onSuccess(UserInformation userInformation) {
                TextView navbarUserName = (TextView) findViewById(R.id.navbar_user_name);
                navbarUserName.setText(userInformation.getName());
                TextView navbarEmail = (TextView) findViewById(id.navbar_email);
                if(!(userInformation.getGoogleEmail()==null)){
                    navbarEmail.setText(userInformation.getGoogleEmail());
                }
                else if(!(userInformation.getEmail()==null)){
                    navbarEmail.setText(userInformation.getEmail());
                }
                else if(!(userInformation.getFacebookLink()==null)){
                    navbarEmail.setText(userInformation.getFacebookLink());
                }
                LinearLayout navbarProfile = (LinearLayout) findViewById(R.id.navbar_profile);
                navbarProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    }
                });
            }

            @Override
            public void onError(DatabaseError databaseError) {
                Log.e("FirebaseDataManager","Can not retrieve userInformation");
            }
        });
    }

    public void screen2ButtonsOnClickListener() {
        View category0 = findViewById(id.button0);
        View category1 = findViewById(id.button1);
        View category2 = findViewById(id.button2);
        View seeAll = findViewById(id.see_all);
        category0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityChooseLevel.class);
                intent.putExtra("Category","0");
                startActivityForResult(intent, 1);
            }
        });
        category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityChooseLevel.class);
                intent.putExtra("Category","1");
                startActivityForResult(intent, 1);
            }
        });
        category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityChooseLevel.class);
                intent.putExtra("Category","2");
                startActivityForResult(intent, 1);
            }
        });
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityChooseLevel.class);
                intent.putExtra("Category","all");
                startActivityForResult(intent, 1);
            }
        });
    }

    public void configureNavigationDrawer(){
        drawerLayout = findViewById(id.drawer_layout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                setNavbarUserInf();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void configureToolbarForFirstScreen() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Home page");
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private void configureToolbarForSecondScreen() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Choose Category");
        actionbar.setHomeAsUpIndicator(drawable.ic_arrow_back_white_24dp);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                if(screen1.getVisibility() == View.VISIBLE) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                else if(screen2.getVisibility() == View.VISIBLE){
                    screen2.setVisibility(View.GONE);
                    screen1.setVisibility(View.VISIBLE);
                    configureToolbarForFirstScreen();
                }
                return true;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                screen1 = findViewById(id.screen1);
                screen2 = findViewById(id.screen2);
                screen1.setVisibility(View.GONE);
                screen2.setVisibility(View.VISIBLE);
            }
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }

}
