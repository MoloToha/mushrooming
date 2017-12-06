package com.mushrooming.location;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import com.mushrooming.base.App;
import com.mushrooming.base.Position;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by antonl on 06.12.17.
 */

public class LocationProvider {
    private Activity activity;

    public LocationProvider(Activity activity) {
        this.activity = activity;
    }

    public Position getLocation() throws Exception {
        // Get the location manager
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                + ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        Location location = locationManager.getLastKnownLocation(bestProvider);

        Double lat,lon;
        try {
            lat = location.getLatitude ();
            lon = location.getLongitude ();
            return new Position(lat, lon);
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }
}
