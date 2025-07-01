package com.radach.gpstrackerreal;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import okhttp3.*;
import org.json.JSONObject;

public class DataUploader {
    private static final String TAG = "DataUploader";

    // Replace with your Apps Script web app URL
    private static final String APPS_SCRIPT_URL = "https://script.google.com/macros/s/AKfycbwqY9RT6UHEovG2OIk4xvZg1rHXsInypc0nEI0nJQqWjILWXl4VrMMuaeaY3IFyLvpw/exec";

    private final OkHttpClient client;

    public DataUploader() {
        this.client = new OkHttpClient();
    }

    public void uploadLocation(LocationTracker.LocationData locationData) {
        new UploadTask().execute(locationData);
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadTask extends AsyncTask<LocationTracker.LocationData, Void, Void> {
        @Override
        protected Void doInBackground(LocationTracker.LocationData... locationDataArray) {
            if (locationDataArray.length == 0) return null;

            LocationTracker.LocationData data = locationDataArray[0];

            try {
                // Create JSON payload
                JSONObject json = new JSONObject();
                json.put("timestamp", data.timestamp);
                json.put("deviceId", data.deviceId);
                json.put("latitude", data.latitude);
                json.put("longitude", data.longitude);
                json.put("accuracy", data.accuracy);
                json.put("speed", data.speed);

                // Create request body
                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                // Build request
                Request request = new Request.Builder()
                        .url(APPS_SCRIPT_URL)
                        .post(body)
                        .build();

                // Execute request
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Location uploaded successfully: " + response.body().string());
                    } else {
                        Log.e(TAG, "Upload failed: " + response.code() + " " + response.message());
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Error uploading location: " + e.getMessage(), e);
            }

            return null;
        }
    }
}