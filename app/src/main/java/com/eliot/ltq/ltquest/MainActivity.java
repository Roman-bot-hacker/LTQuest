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
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.androidmapsextensions.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.eliot.ltq.ltquest.R.*;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    TextView numberOfPoint;
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
    private ArrayList<InfoFromJson> data = new ArrayList<>();
    private static final Type contentType = new TypeToken<List<InfoFromJson>>() {
    }.getType();
    private  View changedMarkerInflated ;
    private TextView markerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        changedMarkerInflated = inflater.inflate(layout.changed_marker, null);
        markerTextView = changedMarkerInflated.findViewById(id.number_text_view);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(id.map);
        mapFragment.getExtendedMapAsync(this);
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

        //AssetFileDescriptor descriptor = getAssets().openFd("quest.json");
        String myJson = inputStreamToString(this.getResources().openRawResource(raw.quest));
        Gson gson = new Gson();
        //JsonReader reader = new JsonReader(myJson);
        data = gson.fromJson(myJson, contentType);
        createMarker(data);
    }

    private String inputStreamToString(InputStream inputStream){
        try{
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes,0,bytes.length);
            return new String(bytes);
        }catch(IOException e){
            return null;
        }
    }

    private void createMarker(ArrayList<InfoFromJson> list) {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View markerInflated = inflater.inflate(R.layout.marker, null);
        numberOfPoint = (TextView) markerInflated.findViewById(R.id.number_text_view);
        TextView distanceBetweenPoint = (TextView) markerInflated.findViewById(R.id.text_text_view);
        for (int i = 0; i < list.size(); i++) {
            int j = i + 1;
            numberOfPoint.setText("" + j);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(list.get(i).getLat(),list.get(i).getLng()))
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromView(markerInflated))));
        }
        changeMarkerListener();
    }

    public void changeMarkerListener() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {



            @Override
            public boolean onMarkerClick(Marker marker) {
                /*mMap.addMarker(new MarkerOptions()
                        .position(marker.getPosition())
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromView(changedMarkerInflated))));*/
                markerTextView.setText("3");
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(getBitmapFromView(changedMarkerInflated)));
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

    private void askMyLocationPermissions() {
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

    public void screen2ButtonsOnClickListener() {
        View category1 = findViewById(id.button1);
        View category2 = findViewById(id.button2);
        View category3 = findViewById(id.button3);
        View seeAll = findViewById(id.see_all);
        category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
