package com.eliot.ltq.ltquest;

import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class QuestActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LocationManager locationManager;
    private MapsSupport mapsSupport = new MapsSupport();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quest_screen);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_quest);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (mapsSupport.isNetworkProviderEnabled(locationManager)) {
            mapsSupport.askMyLocationPermissions(this);
            mapsSupport.checkMyFineLocationUpdates(locationManager, this);
            mapsSupport.checkMyCoarseLocationUpdates(locationManager, this);
        }
        else return;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapsSupport.setmMap(googleMap);
        mapsSupport.mapReadySupport(this);
    }
}
