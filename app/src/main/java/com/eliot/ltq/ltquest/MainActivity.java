package com.eliot.ltq.ltquest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eliot.ltq.ltquest.authentication.FirebaseAuthManager;
import com.eliot.ltq.ltquest.authentication.ProfileActivity;
import com.eliot.ltq.ltquest.authentication.UserInformation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
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
    private ImageView myLocationButton;
    private View screen1;
    private View screen2;
    private LatLng currentLatLng = new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
    private FirebaseDataManager firebaseDataManager = new FirebaseDataManager();
    private FirebaseAuthManager firebaseAuthManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        firebaseAuthManager = new FirebaseAuthManager();
        setContentView(layout.activity_main);
        setToolbarUserInf();
        setCategoriesText();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(id.map);
        mapFragment.getMapAsync(this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        screen1 = findViewById(id.screen1);
        screen2 = findViewById(id.screen2);
        screen1.setVisibility(View.VISIBLE);
        screen2.setVisibility(View.GONE);
        screen1ButtonsOnClickListener();
        screen2ButtonsOnClickListener();
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
            myLocationButton = (ImageView) findViewById(id.myLocationButton);
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
                    0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            if (location == null)
                                return;
                            if (firstCameraOnMyPosition) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getMyLocation(location), 17f));
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location == null)
                        return;
                    if (firstCameraOnMyPosition) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getMyLocation(location), 17f));
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

        if(id==R.id.nav_settings){
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
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
                screen1.setVisibility(View.GONE);
                screen2.setVisibility(View.VISIBLE);
            }
        });
        continueQuest.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void setCategoriesText(){
        final TextView firstButtonText = findViewById(R.id.button1_text);
        final TextView secondButtonText = findViewById(R.id.button2_text);
        final TextView thirdButtonText = findViewById(R.id.button3_text);
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

    public void setToolbarUserInf(){
        firebaseDataManager.getCurrentUserData(firebaseAuthManager.getCurrentUser().getUid(), new FirebaseDataManager.DataRetrieveListenerForUserInformation() {
            @Override
            public void onSuccess(UserInformation userInformation) {
                TextView toolbarUserName = (TextView) findViewById(id.toolbarUserName);
                toolbarUserName.setText(userInformation.getName());
                TextView toolbarEmail = (TextView) findViewById(id.toolbarEmail);
                if(!(userInformation.getGoogleEmail()==null)){
                    toolbarEmail.setText(userInformation.getGoogleEmail());
                }
                else if(!(userInformation.getEmail()==null)){
                    toolbarEmail.setText(userInformation.getEmail());
                }
                else {
                    toolbarEmail.setText(userInformation.getFacebookLink());
                }
            }

            @Override
            public void onError(DatabaseError databaseError) {
                Log.e("FirebaseDataManager","Can not retrieve userInformation");
            }
        });
    }

    public void screen2ButtonsOnClickListener() {
        View category1 = findViewById(id.button1);
        View category2 = findViewById(id.button2);
        View category3 = findViewById(id.button3);
        View seeAll = findViewById(id.see_all);
        category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ActivityChooseLevel.class));
            }
        });
        category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        category3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
