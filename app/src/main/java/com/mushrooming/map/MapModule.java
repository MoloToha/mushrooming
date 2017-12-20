package com.mushrooming.map;

import android.content.Context;

import com.mushrooming.base.Logger;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

/**
 * Created by piotrek on 20.12.17.
 */

public class MapModule {

    private MapView mv;
    private ArrayList<OverlayItem> items;
    private int whichPos;
    private ItemizedOverlayWithFocus<OverlayItem> myiowf;

    public MapModule(MapView map, ArrayList<OverlayItem> its, ItemizedOverlayWithFocus<OverlayItem> iowf) {
        mv = map;
        items = its;
        whichPos = 0;
        myiowf = iowf;
    }

    // currently switches between marking two different hardcoded positions
    public void markPosition(Context ctx, ItemizedIconOverlay.OnItemGestureListener listen) {
        mv.getOverlays().remove(myiowf);
        items.clear();
        if (whichPos == 1) {
            items.add(new OverlayItem("point t1", "descr", new GeoPoint(51.110825d, 17.053549d)));

        } else {
            items.add(new OverlayItem("point t2", "descr", new GeoPoint(51.111025d, 17.053749d)));

        }
        Logger.debug(this, "marking position with whichPos = %d", whichPos);
        whichPos = (whichPos+1)%2;

        // listen can be final and created once, but new mOverlay probably has to be created
        // check if just changing items list is enough, but it is probably not
        ItemizedOverlayWithFocus<OverlayItem> mOverlay =
                new ItemizedOverlayWithFocus<OverlayItem>(ctx, items, listen);

        mOverlay.setFocusItemsOnTap(true);

        // new ItemizedOverlayWithFocus<OverlayItem> object has to be created
        mv.getOverlays().add(mOverlay);

        myiowf = mOverlay;  // for removing this position mark in next marking

        mv.invalidate(); //to make it refresh overlays with marked positions
    }
}
