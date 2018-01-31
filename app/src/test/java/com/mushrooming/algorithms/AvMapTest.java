package com.mushrooming.algorithms;

import com.mushrooming.base.Position;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by piotrek on 22.11.17.
 */
public class AvMapTest {

    @Test
    public void markPosition() throws Exception {
        MapPosition mp = new MapPosition(123, 324);
        AvMap av = new AvMap();
        av.markCenterRelativeMapPosition(mp);
        MapPosition mp2 = av.getAbsoluteMapPositionFromCenterRelative(mp);
        assertEquals(true, av.availableTerrain(mp2.getIntX(),mp2.getIntY()));
    }

    @Test
    public void markPositions() throws Exception {
        MapPosition mp1 = new MapPosition(123, 324);
        MapPosition mp2 = new MapPosition(124, 324);
        MapPosition mp3 = new MapPosition(25, 324);
        AvMap av = new AvMap();
        ArrayList<MapPosition> list = new ArrayList<>();
        list.add(mp1);
        list.add(mp2);
        list.add(mp3);
        av.markCenterRelativeMapPositions(list);
        MapPosition mp11 = av.getAbsoluteMapPositionFromCenterRelative(mp1);
        MapPosition mp21 = av.getAbsoluteMapPositionFromCenterRelative(mp2);
        MapPosition mp31 = av.getAbsoluteMapPositionFromCenterRelative(mp3);
        assertEquals(true, av.availableTerrain(mp11.getIntX(),mp11.getIntY()));
        assertEquals(true, av.availableTerrain(mp21.getIntX(),mp21.getIntY()));
        assertEquals(true, av.availableTerrain(mp31.getIntX(),mp31.getIntY()));
    }


    /*@Test
    public void getNonRelativePosition() throws Exception {
        AvMap av = new AvMap();
        av.moveToRelativeToCurrentMapPosition(new MapPosition(100,100));  // not used anymore
        assertEquals(new MapPosition(652,652), av.getCenterRelativeMapPositionFromAbsolute(new MapPosition(100,100)));
    }*/

    @Test
    public void recenter() throws Exception {
        AvMap av = new AvMap();
        Position oldCenter = av.getNonRelativeGPSposition(new MapPosition(0,0));  // we get GPS coord of center
        //MapPosition mp = av.getCenterRelativeMapPositionFromAbsolute(new MapPosition(0,0));
        av.recenter(av.getNonRelativeGPSposition(new MapPosition(400,400)));
        MapPosition mp = av.getCenterRelativeMapPositionFromGPS(oldCenter);
        assertEquals(mp.getIntX(), -400);
        assertEquals(mp.getIntY(), -400);
    }

}