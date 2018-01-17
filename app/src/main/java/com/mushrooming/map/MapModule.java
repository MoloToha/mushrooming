package com.mushrooming.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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

    public MapModule(MapView map) {

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
    }

    public void clearAllMarkers () {
        mv.getOverlays().clear();
        mv.invalidate();
    }

    public void clearOneMarker(Marker marker) {

        if (mv.getOverlays().contains(marker)) {
            mv.getOverlays().remove(marker);
            mv.invalidate();
        }
    }

    // marker is returned so that one can later invoke clearOneMarker with it
    // one can ignore it
    public Marker markPosition(Boolean sure, GeoPoint pos, String userName, int color) {

        GeoPoint markPos = pos;
        if (pos == null) {
            Logger.warning(this, "Given NULL position to mark!");
            return null;
        }

        Drawable icon;
        if (sure) {
            icon = App.instance().getApplicationContext().getResources().getDrawable(R.drawable.location_mark);
        } else {
            icon = App.instance().getApplicationContext().getResources().getDrawable(R.drawable.question_mark);
        }

        icon.mutate(); // probably not needed, but to be sure

        // assuming icons don't have shadows etc and can colour every non-transparent pixel with full colour
        icon.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        Marker marker = new Marker(mv);

        String markerDescr = "user '" + userName + "'";

        marker.setPosition(markPos);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(icon);
        marker.setTitle(markerDescr);
        mv.getOverlays().add(marker);

        Logger.debug(this, "marking position: " + markPos + "; sure: " + sure);

        mv.invalidate();

        return marker;
    }

    public void centerMap(GeoPoint pos) {
        mv.getController().setCenter(pos);
        mv.invalidate();
    }

    // currently switches between marking two different hardcoded positions
    public void testMarkPosition() {

        //  if mv.getOverlays().contains(marker) {  mv.getOverlays().remove(marker); }
        mv.getOverlays().clear();

        GeoPoint point1;
        point1 = new GeoPoint(51.110925, 17.053549);
        markPosition(true, point1, "RED_USER", Color.RED);

        GeoPoint point2;
        point2 = new GeoPoint(51.110625, 17.053749);
        markPosition(false, point2, "BLACK_USER", Color.BLACK);

        Position myPos = App.instance().getMyUser().getGpsPosition();
        GeoPoint geoPoint;
        Marker marker = new Marker(mv);
        Drawable ic1;
        String markerDescr;
        PorterDuff.Mode mode;
        if (myPos == null) {
            geoPoint = new GeoPoint(51.110825, 17.053549);
            ic1 = App.instance().getApplicationContext().getResources().getDrawable(R.drawable.question_mark); //common_full_open_on_phone white, MULTIPLY will be OK
            // maybe use person or our own icon (with person icon there is problem adjusting color - how to change it composing with eg. plain blue)
            // MAYBE create our own white person icon and then adjust color with MULTIPLY mode
            markerDescr = "Default position, couldn't locate";
            Logger.warning(this, "couldn't locate for marking position");
            mode = PorterDuff.Mode.SRC_IN;
        } else {
            geoPoint = new GeoPoint(myPos.getX(), myPos.getY());
            ic1 = App.instance().getApplicationContext().getResources().getDrawable(R.drawable.location_mark); //common_full_open_on_phone white, MULTIPLY will be OK
            // maybe use person or our own icon (with person icon there is problem adjusting color - how to change it composing with eg. plain blue)
            // MAYBE create our own white person icon and then adjust color with MULTIPLY mode
            markerDescr = "My last seen position";
            mode = PorterDuff.Mode.SRC_IN;
        }

        ic1.mutate(); // so that not all that icons will be changed

        if (whichPos == 1) {
            // careful, color IS NOT hexadecimal color value because so
            ic1.setColorFilter(Color.BLUE, mode); //SRC_IN, SRC_ATOP, OVERLAY, MULTIPLY ...

        } else {
            // careful, color IS NOT hexadecimal color value because so
            ic1.setColorFilter(Color.GREEN, mode); //SRC_IN, OVERLAY, ...

        }
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(ic1);
        marker.setTitle(markerDescr);
        mv.getOverlays().add(marker);

        Logger.debug(this, "marking position: " + myPos);
        whichPos = (whichPos+1)%2;
        mv.getController().setCenter(geoPoint);


        mv.getController().setCenter(point2);

        mv.invalidate(); //to make it refresh overlays with marked positions
    }
}
