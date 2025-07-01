package com.radach.gpstrackerreal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestLocationPermissions();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestLocationPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        };

        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                startLocationService();
                Toast.makeText(this, "GPS Tracker Started", Toast.LENGTH_SHORT).show();
                updateUI();
            } else {
                Toast.makeText(this, "Location permissions required", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startLocationService() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        startForegroundService(serviceIntent);

        // Auto-minimize after 1 second
        findViewById(android.R.id.content).postDelayed(() -> moveTaskToBack(true), 1000);
    }

    private void updateUI() {
        TextView statusText = findViewById(R.id.statusText);
        statusText.setText("✓ GPS Tracker Active\n\nTracking location every minute\nUploading to Google Sheets\n\nApp will minimize automatically...");
    }
}