package com.mushrooming.algorithms;

import com.mushrooming.base.Position;

/**
 * Created by piotrek on 04.11.17.
 */

public class MapPosition {
    private double _intx = AvMap.size/2;
    private double _inty = AvMap.size/2;

    public MapPosition(Position pos) {
        // compute integer position from pos, knowing sth about map - public static AvMap.size
        // pos should probably contain latitude and longitude

        // TODO write this constructor when final Position format will be known
    }

    public MapPosition(double x, double y) {
        // maybe order an assembly if gets out of map range, but rather in invocation place
        _intx = x;
        _inty = y;
    }

    public int getIntX() {
        return ((int) _intx);
    }

    public int getIntY() {
        return ((int) _inty);
    }

    public double getX() {
        return _intx;
    }

    public double getY() {
        return _inty;
    }
}
