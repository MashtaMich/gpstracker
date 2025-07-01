package com.radach.gpstrackerreal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class LocationService extends Service {
    private static final String CHANNEL_ID = "LocationServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private LocationTracker locationTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        locationTracker = new LocationTracker(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());
        locationTracker.startLocationUpdates();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationTracker != null) {
            locationTracker.stopLocationUpdates();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "GPS Tracker Service",
                NotificationManager.IMPORTANCE_LOW
        );
        channel.setDescription("Tracking location in background");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("GPS Tracker Active")
                .setContentText("Tracking location and uploading to Google Sheets")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }
}