package com.radach.gpstrackerreal;

import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Looper;
import android.provider.Settings;

import com.google.android.gms.location.*;
import com.google.android.gms.location.FusedLocationProviderClient;

public class LocationTracker {
    private Context context;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private DataUploader dataUploader;

    public LocationTracker(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.dataUploader = new DataUploader();

        setupLocationCallback();
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    handleLocationUpdate(location);
                }
            }
        };
    }

    private void handleLocationUpdate(Location location) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

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
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(60000) // 1 minute
                .setFastestInterval(30000) // 30 seconds
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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