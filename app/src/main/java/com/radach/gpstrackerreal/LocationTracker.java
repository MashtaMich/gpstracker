package com.radach.gpstrackerreal;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.location.*;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.UUID;

public class LocationTracker {
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private final DataUploader dataUploader;

    public LocationTracker(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.dataUploader = new DataUploader();

        setupLocationCallback();
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    handleLocationUpdate(location);
                }
            }
        };
    }

    private void handleLocationUpdate(Location location) {
        SharedPreferences prefs = context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        String deviceId = prefs.getString("device_id", null);
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            prefs.edit().putString("device_id", deviceId).apply();
        }

        LocationData locationData = new LocationData(
                System.currentTimeMillis(),
                deviceId,
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy(),
                location.hasSpeed() ? location.getSpeed() : 0
        );

        dataUploader.uploadLocation(locationData);
    }

    public void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, // priority
                600000L // interval in milliseconds
        )
                .setMinUpdateIntervalMillis(300000L)  // fastest interval
                .build();

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public static class LocationData {
        public long timestamp;
        public String deviceId;
        public double latitude;
        public double longitude;
        public float accuracy;
        public float speed;

        public LocationData(long timestamp, String deviceId, double latitude, double longitude,
                            float accuracy, float speed) {
            this.timestamp = timestamp;
            this.deviceId = deviceId;
            this.latitude = latitude;
            this.longitude = longitude;
            this.accuracy = accuracy;
            this.speed = speed;
        }
    }
}