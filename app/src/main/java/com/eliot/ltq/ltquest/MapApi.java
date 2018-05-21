package com.eliot.ltq.ltquest;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MapApi {
    @GET("/maps/api/directions/json")
    Call<DirectionResults> getJson(@Query("origin") String origin, @Query("destination") String destination,
                                   @Query("waypoints") String waypoints,
                                   @Query("sensor") String sensor, @Query("mode") String mode);
}
