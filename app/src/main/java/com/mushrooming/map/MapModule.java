package com.mushrooming.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;


import com.example.antonl.mushrooming.R;

import com.mushrooming.base.App;

import com.mushrooming.base.Logger;
import com.mushrooming.base.Position;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

/**
 * Created by piotrek on 20.12.17.
 */

public class MapModule {

    private MapView mv;
    private int whichPos;
    private Context appctx;

    public MapModule(Context context, MapView map) {

        //MapView map = (MapView) context.findViewById(R.id.map); //it does not work here...
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        //map.setMaxZoomLevel(22); // tiles blurry with that zoom, and to see nearby tiles you sometimes need to zoom out and in
        map.setTilesScaledToDpi(true); // but this works great

        IMapController mapController = map.getController();
        mapController.setZoom(18); // biggest zoom available on Mapnik
        GeoPoint startPoint = new GeoPoint(51.110825, 17.053549);
        mapController.setCenter(startPoint);

        mv = map;
        whichPos = 0;
        appctx = context;
    }

    // currently switches between marking two different hardcoded positions
    public void markPosition(Context ctx) {

        Position myPos = App.instance().getMyUser().getGpsPosition();
        GeoPoint geoPoint = new GeoPoint(myPos.getX(), myPos.getY());

        //  if mv.getOverlays().contains(marker) {  mv.getOverlays().remove(marker); }
        mv.getOverlays().clear();
        if (whichPos == 1) {
            Marker marker = new Marker(mv);
            Drawable ic1 = appctx.getResources().getDrawable(R.drawable.common_full_open_on_phone); //common_full_open_on_phone white, MULTIPLY will be OK
            // maybe use person or our own icon (with person icon there is problem adjusting color - how to change it composing with eg. plain blue)
            // MAYBE create our own white person icon and then adjust color with MULTIPLY mode

            ic1.mutate(); // so that not all that icons will be changed
            // careful, color IS NOT hexadecimal color value because so
            ic1.setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY); //SRC_IN, SRC_ATOP, OVERLAY, ...
            marker.setPosition(geoPoint);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(ic1);
            marker.setTitle("My position");
            mv.getOverlays().add(marker);
        } else {
            Marker marker = new Marker(mv);
            Drawable ic1 = appctx.getResources().getDrawable(R.drawable.common_full_open_on_phone);
            // maybe use person or our own icon (with person icon there is problem adjusting color - how to change it composing with eg. plain blue)
            // MAYBE create our own white person icon and then adjust color with MULTIPLY mode

            ic1.mutate(); // so that not all that icons will be changed
            // careful, color IS NOT hexadecimal color value because so
            ic1.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY); //SRC_IN, OVERLAY, ...
            marker.setPosition(geoPoint);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(ic1);
            marker.setTitle("My position");
            mv.getOverlays().add(marker);
        }
        Logger.debug(this, "marking position: " + myPos);
        whichPos = (whichPos+1)%2;

        mv.invalidate(); //to make it refresh overlays with marked positions
    }
}
