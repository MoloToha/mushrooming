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
import com.mushrooming.base.Logger;
import com.mushrooming.base.Position;

public class LocationService {

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private Position currentPosition;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    public LocationService() {
        Logger.debug(this, "LocationService starting...");

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
                Location location = locationResult.getLastLocation();
                currentPosition = new Position(location.getLatitude(), location.getLongitude());

                Logger.debug(this, "Location Callback results: " + currentPosition);
            }
        };

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(App.instance().getApplicationContext());
        requestLocationUpdates();
    }

    public Position getLastPosition() {
        return currentPosition;
    }

    private void requestLocationUpdates() {
        try {
            this.mFusedLocationClient.requestLocationUpdates(locationRequest,
                    this.locationCallback, Looper.myLooper());
        } catch (SecurityException e) {
            Logger.errorWithException(this, e, "Failed to request location updates");
        }
    }

    public void stop() {
        Logger.debug(this, "LocationService stopping");
        this.mFusedLocationClient.removeLocationUpdates(this.locationCallback);
    }

}