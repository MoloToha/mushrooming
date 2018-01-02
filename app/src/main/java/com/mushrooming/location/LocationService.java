package com.mushrooming.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.mushrooming.base.App;
import com.mushrooming.base.Position;

public class LocationService {

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private Position currentPosition;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1;

    public LocationService() {
        App.instance().getDebug().write("LocationService starting...");

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        if (ContextCompat.checkSelfPermission(App.instance().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location currentLocation = locationResult.getLastLocation();

                currentPosition = new Position(currentLocation.getLatitude(), currentLocation.getLongitude());
                App.instance().getDebug().write("Location Callback results: " + currentPosition);
            }
        };

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(App.instance().getApplicationContext());
        requestLocationUpdates();
    }

    public Position getLastPosition() {
        requestLocationUpdates();
        return currentPosition;
    }

    private void requestLocationUpdates() {
        try {
            this.mFusedLocationClient.requestLocationUpdates(locationRequest,
                    this.locationCallback, Looper.myLooper());
        } catch (SecurityException e) {
            App.instance().getDebug().write("Failed to request location updates");
        }
    }

    public void stop() {
        App.instance().getDebug().write("LocationService stopping");
        this.mFusedLocationClient.removeLocationUpdates(this.locationCallback);
    }

}