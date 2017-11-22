package com.mushrooming.algorithms;

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
        av.markPosition(mp);
        MapPosition mp2 = av.getNonRelativePosition(mp);
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
        av.markPositions(list);
        MapPosition mp11 = av.getNonRelativePosition(mp1);
        MapPosition mp21 = av.getNonRelativePosition(mp2);
        MapPosition mp31 = av.getNonRelativePosition(mp3);
        assertEquals(true, av.availableTerrain(mp11.getIntX(),mp11.getIntY()));
        assertEquals(true, av.availableTerrain(mp21.getIntX(),mp21.getIntY()));
        assertEquals(true, av.availableTerrain(mp31.getIntX(),mp31.getIntY()));
    }


    @Test
    public void getNonRelativePosition() throws Exception {
        AvMap av = new AvMap();
        av.moveToRelativePosition(new MapPosition(100,100));
        assertEquals(new MapPosition(652,652), av.getNonRelativePosition(new MapPosition(100,100)));
    }

    @Test
    public void recenter() throws Exception {
        AvMap av = new AvMap();
        MapPosition mp = av.getNonRelativePosition(new MapPosition(0,0));
        av.moveToRelativePosition(new MapPosition(400,400));
        assertEquals(mp, av.getNonRelativePosition(new MapPosition(-400,-400)));
    }

}