package com.mushrooming.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.antonl.mushrooming.BuildConfig;
import com.example.antonl.mushrooming.R;
import com.mushrooming.base.App;
import com.mushrooming.map.MapModule;


import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

/**
 * Created by piotrek on 12.12.17.
 */

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "OSM map viewing activity";

    private static final int REQUEST_FOR_OSMDROID = 10;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Button returnToMain = findViewById(R.id.return_button);
        returnToMain.setOnClickListener((v)->{
            Intent intent = new Intent(MapActivity.this, MainActivity.this);
        });*/

        // need to request "dangerous permissions" at runtime since android 6.0
        requestPermissionsForOsmdroid();

        Context ctx = getApplicationContext();

        // needed because of OSM ban rules or sth
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID); //ctx.getPackageName()

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_map);

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        //map.setMaxZoomLevel(22); // tiles blurry with that zoom, and to see nearby tiles you sometimes need to zoom out and in
        map.setTilesScaledToDpi(true); // but this works great

        IMapController mapController = map.getController();
        mapController.setZoom(18); // biggest zoom available on Mapnik
        GeoPoint startPoint = new GeoPoint(51.110825, 17.053549);
        mapController.setCenter(startPoint);

// fragment from example (changed a bit) https://github.com/osmdroid/osmdroid/wiki/Markers,-Lines-and-Polygons
        //your items
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        // on beginning no position is marked

        // overlay
        final ItemizedIconOverlay.OnItemGestureListener listen = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                // some function from example invoked on tap, think what to do here
                return true;
            }
            @Override
            public boolean onItemLongPress(final int index, final OverlayItem item) {
                return false;
            }
        };

        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(ctx, items,
                listen);
        mOverlay.setFocusItemsOnTap(true);

        map.getOverlays().add(mOverlay); //mMapView
//

        // maybe create new mapViews like in example? https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library
        // but it works like it is written now
        App.instance().set_map(new MapModule(map, items, mOverlay));

        /*Button markPosition = findViewById(R.id.mark_position_button);
        markPosition.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        App.instance().markPosition(getApplicationContext(), listen);
                    }
                }
        );*/
    }

    public void onResume() {
        super.onResume();
        // more if changes to configuration made
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    private void requestPermissionsForOsmdroid() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_FOR_OSMDROID);
        }
    }

}
