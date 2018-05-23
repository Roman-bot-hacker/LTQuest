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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.androidmapsextensions.PolylineOptions;
import com.androidmapsextensions.SupportMapFragment;
import com.eliot.ltq.ltquest.authentication.FirebaseAuthManager;
import com.eliot.ltq.ltquest.authentication.ProfileActivity;
import com.eliot.ltq.ltquest.authentication.UserInformation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eliot.ltq.ltquest.R.drawable;
import static com.eliot.ltq.ltquest.R.id;
import static com.eliot.ltq.ltquest.R.layout;
import static com.eliot.ltq.ltquest.R.raw;
import static com.google.android.gms.maps.model.JointType.BEVEL;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private GoogleMap mMap;
    TextView numberOfPoint;
    private LocationManager locationManager;
    private boolean firstCameraOnMyPosition = true;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 111;
    private static final double DEFAULT_LATITUDE = 49.841787;
    private static final double DEFAULT_LONGITUDE = 24.031686;
    private Marker mPositionMarker;
    private Toolbar toolbar;
    private ImageView myLocationButton;
    private View screen1;
    private View screen2;
    private DrawerLayout drawerLayout;
    private LatLng currentLatLng = new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
    private FirebaseDataManager firebaseDataManager = new FirebaseDataManager();
    private FirebaseAuthManager firebaseAuthManager;
    private NavigationView navigationView;
    private ArrayList<InfoFromJson> data = new ArrayList<>();
    private ArrayList<InfoFromJson> dataPart2 = new ArrayList<>();
    private static final Type contentType = new TypeToken<List<InfoFromJson>>() {
    }.getType();
    private View changedMarkerInflated;
    private TextView markerTextView;
    private LayoutInflater inflater;
    private View markerInflated;
    private LatLng origin2;
    private LatLng dest2;
    private String url2;
    private static ArrayList<LatLng> polylinesList = new ArrayList<>();
    private static ArrayList<LatLng> polylinesList1 = new ArrayList<>();
    private List<InfoFromJson> waypointslist;
    private LatLng origin;
    private LatLng dest;
    private Intent intent;
    private int counter;
    private View bottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private TextView bottomSheetName;
    private TextView bottomSheetInfo;
    private Button bottomSheetSkipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        firebaseAuthManager = new FirebaseAuthManager();
        setContentView(layout.activity_main);
        setCategoriesText();
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        changedMarkerInflated = inflater.inflate(layout.changed_marker, null);
        markerTextView = changedMarkerInflated.findViewById(id.number_text_view);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(id.map);
        mapFragment.getExtendedMapAsync(this);
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
        numberOfPoint = (TextView) markerInflated.findViewById(R.id.number_text_view);
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
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
                    this, raw.silver_style_maps));
        } catch (Resources.NotFoundException e) {
            e.getMessage();
        }
        intent = getIntent();
        if (intent.getStringExtra("quest_name")!= null) {
            switch (intent.getStringExtra("quest_name")){
                case "justName":
                    screen1.setVisibility(View.GONE);
                    screen1.setVisibility(View.GONE);
                    toolbar.setTitle("justName");
                    drawRoute();
                    break;
            }
        }
    }

    private void drawRoute(){
        String myJsonPart1 = inputStreamToString(this.getResources().openRawResource(raw.quest_part1));
        Gson gson = new Gson();
        data = gson.fromJson(myJsonPart1, contentType);
        createMarker(data);

        Callback<DirectionResults> directionResults = new Callback<DirectionResults>() {
            @Override
            public void onResponse(Call<DirectionResults> call, Response<DirectionResults> response) {
                DirectionResults res = response.body();
                counter -= 1;
                for (int i = 0; i < res.getRoutes().get(0).getLegs().size(); i++) {
                    for (Steps step : res.getRoutes().get(0).getLegs().get(i).getSteps()) {
                        DirectionsJSONParser.decodePoly(polylinesList, step.getPolyline().getPoints());
                        Log.d("Polylines recieved", String.valueOf(polylinesList.size()));
                    }
                }
                if (counter == 0) {
                    createPolylines(polylinesList);
                }

            }

            @Override
            public void onFailure(Call<DirectionResults> call, Throwable t) {
                Toast.makeText(MainActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
            }
        };


//        origin = new LatLng(data.get(0).getLat(), data.get(0).getLng());
//        dest = new LatLng(data.get(7).getLat(), data.get(7).getLng());
//        waypointslist = data.subList(0, 7);
//        for (int i = 0; i < waypointslist.size(); i++) {
//
//            LatLng point = new LatLng(waypointslist.get(i).getLat(), waypointslist.get(i).getLng());
//            if (i == waypointslist.size() - 1) {
//                waypoints.append(point.latitude).append(",").append(point.longitude);
//            } else {
//                waypoints.append(point.latitude).append(",").append(point.longitude).append("|");
//            }
//        }
//        App.getApi().getJson(
//                origin.latitude + "," + origin.longitude,
//                dest.latitude + "," + dest.longitude, waypoints.toString(), "false",
//                "walking").enqueue(directionResults);

        int i = 0;
        counter = data.size() / 7 + 1;
        if (data.size() > 7) {

            while (data.size() - i > 7) {

                origin = new LatLng(data.get(i).getLat(), data.get(i).getLng());
                dest = new LatLng(data.get(i + 7).getLat(), data.get(i + 7).getLng());
                StringBuilder waypoints = new StringBuilder();
                waypointslist = data.subList(i, i + 7);
                createWaypointsString(waypointslist, waypoints);
                App.getApi().getJson(
                        origin.latitude + "," + origin.longitude,
                        dest.latitude + "," + dest.longitude, waypoints.toString(), "false",
                        "walking").enqueue(directionResults);
                i += 7;
            }
            origin = new LatLng(data.get(i).getLat(), data.get(i).getLng());
            dest = new LatLng(data.get(data.size() - 1).getLat(), data.get(data.size() - 1).getLng());
            StringBuilder waypoints = new StringBuilder();
            waypointslist = data.subList(i, data.size() - 1);
            createWaypointsString(waypointslist, waypoints);
            App.getApi().getJson(
                    origin.latitude + "," + origin.longitude,
                    dest.latitude + "," + dest.longitude, waypoints.toString(), "false",
                    "walking").enqueue(directionResults);

        } else {
            origin = new LatLng(data.get(i).getLat(), data.get(i).getLng());
            dest = new LatLng(data.get(data.size() - 1).getLat(), data.get(data.size() - 1).getLng());
            StringBuilder waypoints = new StringBuilder();
            waypointslist = data.subList(i, data.size() - 1);
            createWaypointsString(waypointslist, waypoints);
            App.getApi().getJson(
                    origin.latitude + "," + origin.longitude,
                    dest.latitude + "," + dest.longitude, waypoints.toString(), "false",
                    "walking").enqueue(directionResults);
        }
    }

    private void createWaypointsString(List<InfoFromJson> pointsList, StringBuilder waypointsBuilder){
        for (int j = 0; j < pointsList.size(); j++) {

            LatLng point = new LatLng(pointsList.get(j).getLat(), pointsList.get(j).getLng());
            if (j == pointsList.size() - 1) {
                waypointsBuilder.append(point.latitude).append(",").append(point.longitude);
            } else {
                waypointsBuilder.append(point.latitude).append(",").append(point.longitude).append("|");
            }
        }
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

    private void createMarker(ArrayList<InfoFromJson> list) {
        for (int i = 0; i < list.size(); i++) {
            int j = i + 1;
            numberOfPoint.setText("" + j);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(list.get(i).getLat(), list.get(i).getLng()))
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromView(markerInflated))));
        }
        changeMarkerListener();
    }

    private void createPolylines(ArrayList<LatLng> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            mMap.addPolyline(new PolylineOptions()
                    .add(list.get(i), list.get(i + 1))
                    .width(11)
                    .jointType(BEVEL)
                    .color(Color.rgb(145, 121, 241)));
        }
    }

    public void changeMarkerListener() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {


            @Override
            public boolean onMarkerClick(Marker marker) {
                markerTextView.setText("3");
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(getBitmapFromView(changedMarkerInflated)));
                mBottomSheetBehavior.setHideable(true);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                bottomSheetName.setText("Other Point");
                bottomSheetInfo.setText("Some other Info");
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

                Handler handler =new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                },300);

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
                    0, new LocationListener() {
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
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

        if (id == R.id.nav_home) {

        }

        if (id == R.id.nav_balance) {
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
                screen1.setVisibility(View.GONE);
                screen2.setVisibility(View.VISIBLE);
                configureToolbarForSecondScreen();
            }
        });
        continueQuest.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                TextView toolbarUserName = (TextView) findViewById(R.id.toolbar_user_name);
                toolbarUserName.setText(userInformation.getName());
                TextView toolbarEmail = (TextView) findViewById(id.toolbarEmail);
                if (!(userInformation.getGoogleEmail() == null)) {
                    toolbarEmail.setText(userInformation.getGoogleEmail());
                } else if (!(userInformation.getEmail() == null)) {
                    toolbarEmail.setText(userInformation.getEmail());
                } else if (!(userInformation.getFacebookLink() == null)) {
                    toolbarEmail.setText(userInformation.getFacebookLink());
                }
                LinearLayout toolbarProfile = (LinearLayout) findViewById(R.id.toolbarProfile);
                toolbarProfile.setOnClickListener(new View.OnClickListener() {
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
                if (screen1.getVisibility() == View.VISIBLE) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else if (screen2.getVisibility() == View.VISIBLE) {
                    screen2.setVisibility(View.GONE);
                    screen1.setVisibility(View.VISIBLE);
                    configureToolbarForFirstScreen();
                }
                return true;
        }

        return true;
    }

    public void bottomSheetInit(){
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
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    screen1.setVisibility(View.GONE);
                    screen2.setVisibility(View.VISIBLE);
                }
            }
        }
        

    @Override
    public void onBackPressed() {
        // do nothing.
    }
}
