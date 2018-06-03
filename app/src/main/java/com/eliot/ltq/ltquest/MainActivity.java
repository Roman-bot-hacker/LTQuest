package com.eliot.ltq.ltquest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
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

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.androidmapsextensions.PolylineOptions;
import com.androidmapsextensions.SupportMapFragment;
import com.eliot.ltq.ltquest.authentication.FirebaseAuthManager;
import com.eliot.ltq.ltquest.authentication.ProfileActivity;
import com.eliot.ltq.ltquest.authentication.UserInformation;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.eliot.ltq.ltquest.R.drawable;
import static com.eliot.ltq.ltquest.R.id;
import static com.eliot.ltq.ltquest.R.layout;
import static com.eliot.ltq.ltquest.R.raw;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, DirectionCallback {
    private GoogleMap mMap;
    TextView numberOfPoint;
    private LocationManager locationManager;
    private boolean firstCameraOnMyPosition = true;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 111;
    private static final double DEFAULT_LATITUDE = 49.841787;
    private static final double DEFAULT_LONGITUDE = 24.031686;
    private Marker mPositionMarker;
    private Toolbar toolbar;
    private View myLocationButton;
    private ActionBar actionbar;
    private View screen1;
    private View screen2;
    private DrawerLayout drawerLayout;
    private LatLng currentLatLng = new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
    private FirebaseDataManager firebaseDataManager = new FirebaseDataManager();
    private FirebaseAuthManager firebaseAuthManager;
    private NavigationView navigationView;
    private ArrayList<LatLng> data = new ArrayList<>();
    private View changedMarkerInflated;
    private TextView changedMarkerNumber;
    private TextView distanceBetweenPoint;
    private TextView cMarkerdistanceBetweenPoint;
    private LayoutInflater inflater;
    private View markerInflated;
    private View secretMarkerInflated;
    private static ArrayList<LatLng> polylinesList = new ArrayList<>();
    private LatLng origin;
    private LatLng dest;
    private Intent intent;
    private int counter;
    private int requestIndex = 0;
    private View bottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private TextView bottomSheetName;
    private TextView bottomSheetInfo;
    private Button bottomSheetSkipButton;
    private boolean isQuestOn;
    private int currentQuestCategory;
    private List<String> distanceList = new ArrayList<>();
    private List<LocationStructure> locationListFromDatabase;
    private List<RequestClass> requestList = new ArrayList<>();
    private String currentQuestName;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        firebaseAuthManager = new FirebaseAuthManager();
        keepDataSynced();
        setContentView(layout.activity_main);
        setCategoriesText();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(id.map);
        mapFragment.getExtendedMapAsync(this);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        actionbar = getSupportActionBar();
        screen1 = findViewById(id.screen1);
        screen2 = findViewById(id.screen2);
        screen1.setVisibility(View.VISIBLE);
        screen2.setVisibility(View.GONE);
        screen1ButtonsOnClickListener();
        screen2ButtonsOnClickListener();
        configureNavigationDrawer();
        configureToolbarForFirstScreen();
        bottomSheetInit();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (isNetworkProviderEnabled()) {
            askMyLocationPermissions();
            checkMyFineLocationUpdates();
            checkMyCoarseLocationUpdates();
        } else return;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        markerInflated = inflater.inflate(R.layout.marker, null);
        changedMarkerInflated = inflater.inflate(layout.changed_marker, null);
        secretMarkerInflated = inflater.inflate(R.layout.secret_marker, null);
        changedMarkerNumber = (TextView) changedMarkerInflated.findViewById(id.number_text_view);
        numberOfPoint = (TextView) markerInflated.findViewById(R.id.number_text_view);
        distanceBetweenPoint = (TextView) markerInflated.findViewById(R.id.text_text_view);
        cMarkerdistanceBetweenPoint = (TextView) changedMarkerInflated.findViewById(R.id.text_text_view);
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            enableMyLocationButton();
            addMyPositionMarker();
        }

        try {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    this, raw.silver_style_maps));
        } catch (Resources.NotFoundException e) {
            e.getMessage();
        }
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    public void enableMyLocationButton() {
        myLocationButton = (ImageView) findViewById(id.myLocationButton);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f));
            }
        });
    }

    public void addMyPositionMarker() {
        mPositionMarker = mMap.addMarker(new MarkerOptions()
                .flat(false)
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmap(drawable.current_position)))
                .anchor(0.5f, 1f)
                .position(currentLatLng)
                .draggable(false));
    }
    private void drawRoute() {

        firebaseDataManager.questRetrieverByName("justName", new FirebaseDataManager.DataRetrieverListenerForSingleQuestStructure() {
            @Override
            public void onSuccess(QuestStructure questStructure, List<Integer> locationsIdList) {
                currentQuestCategory = questStructure.getParentCategoryID();
                firebaseDataManager.locationsListRetriever(locationsIdList, new FirebaseDataManager.DataRetrieveListenerForLocationsStructure() {
                    @Override
                    public void onSuccess(List<LocationStructure> locationStructureList) {
                        locationListFromDatabase = locationStructureList;
                        prepareDataAndDrawingRoute(locationStructureList);
                    }

                    @Override
                    public void onError(DatabaseError databaseError) {
                        Log.e("FirebaseDataManager", "eeeedgfde");
                    }
                });
            }

            @Override
            public void onError(DatabaseError databaseError) {

            }
        });
    }

    private void prepareDataAndDrawingRoute(List<LocationStructure> locationStructureList) {
        data = getLatLngList(locationStructureList);
        polylinesList.clear();

        if (data.size() == 8) {
            counter = 1;
        } else {
            counter = data.size() / 8 + 1;
        }
        List<LatLng> latlngList;
        if (data.size() > 7) {
            for (int i = 0; i < data.size() - 1; i += 7) {

                origin = new LatLng(data.get(i).latitude, data.get(i).longitude);
                if (i + 7 > data.size() - 1) {
                    dest = new LatLng(data.get(data.size() - 1).latitude, data.get(data.size() - 1).longitude);
                    latlngList = data.subList(i + 1, data.size() - 1);
                } else {
                    dest = new LatLng(data.get(i + 7).latitude, data.get(i + 7).longitude);
                    latlngList = data.subList(i + 1, i + 7);
                }
                RequestClass request = new RequestClass(origin, dest, latlngList);
                requestList.add(request);
            }

            GoogleDirection.withServerKey("AIzaSyALGNj3GZI8DpCLzYeoqQz2Kr0HuqUdiGg")
                    .from(requestList.get(0).getOrigin())
                    .and(requestList.get(0).getWaypoints())
                    .to(requestList.get(0).getDest())
                    .transportMode(TransportMode.WALKING)
                    .execute(this);


        } else {
            origin = new LatLng(data.get(0).latitude, data.get(0).longitude);
            dest = new LatLng(data.get(data.size() - 1).latitude, data.get(data.size() - 1).longitude);
            latlngList = data.subList(1, data.size() - 1);

            GoogleDirection.withServerKey("AIzaSyALGNj3GZI8DpCLzYeoqQz2Kr0HuqUdiGg")
                    .from(origin)
                    .and(latlngList)
                    .to(dest)
                    .transportMode(TransportMode.WALKING)
                    .execute(this);
        }
    }

    private ArrayList<LatLng> getLatLngList(List<LocationStructure> locationStructureList) {
        ArrayList<LatLng> latlngList = new ArrayList<>();
        for (LocationStructure locationStructure : locationStructureList) {
            LatLng point = new LatLng(locationStructure.getLat(), locationStructure.getLon());
            latlngList.add(point);
        }
        return latlngList;
    }

    private String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            return new String(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    private void createMarkers(List<LocationStructure> locationStructureList) {
        for (int i = 0; i < locationStructureList.size() - 1; i++) {
            locationStructureList.get(i + 1).setDistanceToPrevious(distanceList.get(i));
        }
        distanceList.clear();
        for (int i = 0; i < locationStructureList.size(); i++) {
            int j = i + 1;
            if (locationStructureList.get(i).isSecret() == false) {
                numberOfPoint.setText("" + j);
                distanceBetweenPoint.setText(locationStructureList.get(i).getDistanceToPrevious());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(locationStructureList.get(i).getLat(), locationStructureList.get(i).getLon()))
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromView(markerInflated))));
                locationStructureList.get(i).setLocationID(i + 1);
                marker.setData(locationStructureList.get(i));
            } else {
                Marker secretMarker1 = mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromView(secretMarkerInflated)))
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(locationStructureList.get(i).getLat(), locationStructureList.get(i).getLon())));
                secretMarker1.setData(locationStructureList.get(i));
            }
        }
        changeMarkerListener();
    }

    private void createPolylines(ArrayList<LatLng> list) {
        mMap.addPolyline(new PolylineOptions()
                .addAll(list).width(11)
                .jointType(JointType.BEVEL)
                .color(Color.rgb(145, 121, 241)));

    }

    public void changeMarkerListener() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {


            @Override
            public boolean onMarkerClick(Marker marker) {
                LocationStructure locationStructure = marker.getData();
                if (locationStructure.isSecret() == false) {
                    int number = locationStructure.getLocationID();
                    changedMarkerNumber.setText("" + number);
                    cMarkerdistanceBetweenPoint.setText("done");
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(getBitmapFromView(changedMarkerInflated)));
                    mBottomSheetBehavior.setHideable(true);
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                    bottomSheetName.setText(locationStructure.getLocationName());
                    bottomSheetInfo.setText(locationStructure.getLocationDescription());
                    bottomSheetSkipButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    });


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                        }
                    });

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    }, 300);
                } else {

                }

                return true;

            }
        });
    }


    public static Bitmap getBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        switch (item.getItemId()) {
            case R.id.nav_home: {
                drawer.closeDrawer(GravityCompat.START);
            } break;

            case R.id.nav_balance: {
                startActivity(new Intent(MainActivity.this, Balance.class));
                drawer.closeDrawer(GravityCompat.START);
            } break;

            case R.id.nav_high_score: {
                Toast.makeText(this, "Sorry, high score is disabled in this application version", Toast.LENGTH_SHORT).show();
            } break;
        }
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

    public void setCategoriesText() {
        final TextView firstButtonText = findViewById(R.id.button0_text);
        final TextView secondButtonText = findViewById(R.id.button1_text);
        final TextView thirdButtonText = findViewById(R.id.button2_text);
        firebaseDataManager.categoriesNamesListRetriever(new FirebaseDataManager.DataRetrieveListenerForQuestCategory() {
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


    public void setToolbarUserInf() {
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
                Log.e("FirebaseDataManager", "Can not retrieve userInformation");
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
                intent.putExtra("Category", "0");
                startActivityForResult(intent, 1);
            }
        });
        category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityChooseLevel.class);
                intent.putExtra("Category", "1");
                startActivityForResult(intent, 1);
            }
        });
        category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityChooseLevel.class);
                intent.putExtra("Category", "2");
                startActivityForResult(intent, 1);
            }
        });
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityChooseLevel.class);
                intent.putExtra("Category", "all");
                startActivityForResult(intent, 1);
            }
        });
    }

    public void configureNavigationDrawer() {
        drawerLayout = findViewById(id.drawer_layout);
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
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void configureToolbarForFirstScreen() {
        actionbar.setTitle("Home page");
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private void configureToolbarForSecondScreen() {
        actionbar.setTitle("Choose Category");
        actionbar.setHomeAsUpIndicator(drawable.ic_arrow_back_white_24dp);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                if (screen1.getVisibility() == View.VISIBLE) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                if (screen2.getVisibility() == View.VISIBLE) {
                    screen2.setVisibility(View.GONE);
                    screen1.setVisibility(View.VISIBLE);
                    configureToolbarForFirstScreen();
                }
                if (isQuestOn) {
                    isQuestOn = false;
                    Intent intent = new Intent(MainActivity.this, ActivityChooseLevel.class);
                    intent.putExtra("Category", "" + currentQuestCategory);
                    mMap.clear();
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    startActivityForResult(intent, 1);
                }
                return true;
        }

        return true;
    }

    public void bottomSheetInit() {
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetName = findViewById(R.id.bottom_sheet_name);
        bottomSheetInfo = findViewById(id.bottom_sheet_info);
        bottomSheetSkipButton = findViewById(id.bottom_sheet_skip);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setPeekHeight(384);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (extras != null) {
                    if (extras.containsKey("button")) {
                        if (data.getStringExtra("button").equals("back")) {
                            screen1.setVisibility(View.GONE);
                            screen2.setVisibility(View.VISIBLE);
                            mMap.clear();
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            addMyPositionMarker();
                            enableMyLocationButton();
                        }
                    }
                    if (extras.containsKey("quest_name")) {
                        actionbar.setTitle("Quest");
                        actionbar.setHomeAsUpIndicator(drawable.ic_arrow_back_white_24dp);
                        actionbar.setDisplayHomeAsUpEnabled(true);
                        navigationView.setVisibility(View.GONE);
                        switch (data.getStringExtra("quest_name")) {
                            case "justName":
                                screen1.setVisibility(View.GONE);
                                screen2.setVisibility(View.GONE);
                                toolbar.setTitle("justName");
                                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                                drawRoute();
                                isQuestOn = true;
                                break;
                        }
                    }
                }
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


    private void keepDataSynced() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");
        DatabaseReference userDataRef = FirebaseDatabase.getInstance().getReference("userData").child(firebaseAuthManager.getCurrentUser().getUid());
        categoriesRef.keepSynced(true);
        userDataRef.keepSynced(true);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            counter--;
            requestIndex++;
            for (int j = 0; j < direction.getRouteList().get(0).getLegList().size(); j++) {
                Leg leg = direction.getRouteList().get(0).getLegList().get(j);
                distanceList.add(direction.getRouteList().get(0).getLegList().get(j).getDistance().getText());
                for (int i = 0; i < leg.getStepList().size(); i++) {
                    polylinesList.addAll(leg.getStepList().get(i).getPolyline().getPointList());
                }
            }

            if (counter == 0) {
                requestIndex = 0;
                createPolylines(polylinesList);
                createMarkers(locationListFromDatabase);
                polylinesList.clear();
                distanceList.clear();
            } else {
                GoogleDirection.withServerKey("AIzaSyALGNj3GZI8DpCLzYeoqQz2Kr0HuqUdiGg")
                        .from(requestList.get(requestIndex).getOrigin())
                        .and(requestList.get(requestIndex).getWaypoints())
                        .to(requestList.get(requestIndex).getDest())
                        .transportMode(TransportMode.WALKING)
                        .execute(this);
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Log.e("Error", t.getLocalizedMessage());
    }

    private void drawBlackAndVioletLines(){
        firebaseDataManager.getLastVisitedLocationInQuest(currentQuestName, currentUserId, new FirebaseDataManager.lastVisitedLocationInQuestRetriewer() {
            @Override
            public void onSuccess(Integer lastLocation, DatabaseReference databaseReference) {
                if(lastLocation != -1){
                    List<Integer> locationList = new ArrayList<>();
                }
            }

            @Override
            public void onError(DatabaseError databaseError) {

            }
        });
    }
}
