package com.mushrooming.algorithms;

import com.mushrooming.base.Position;

/**
 * Created by piotrek on 04.11.17.
 */

public class MapPosition{
    private double _x = AvMap.size/2;
    private double _y = AvMap.size/2;

    // TODO think if remember MapPos as centered or relative

    // we don't need constructor taking position, MapPosition should be only used internally in AvMap,
    // and perhaps in AssemblyManager

//    public MapPosition(Position pos) {
//        // compute integer position from pos, knowing sth about map - public static AvMap.size
//        // pos should probably contain latitude and longitude
//
//        // pos has X (latitude) and Y (longtitude)
//
//        _x = pos.getX();
//        _y = pos.getY();
//        // TODO write this constructor when final Position format will be known
//    }

    public MapPosition(double x, double y) {
        // maybe order an assembly if gets out of map range, but rather in invocation place
        _x = x;
        _y = y;
    }

    public int getIntX() {
        return ((int) Math.round(_x));
    }

    public int getIntY() {
        return ((int) Math.round(_y));
    }

    public double getX() {
        return _x;
    }

    public double getY() {
        return _y;
    }

    @Override
    public String toString(){
        return String.format("Position: x: %f, y: %f", _x, _y);
    }

    @Override
    public boolean equals(Object p2) {
        if(p2 instanceof MapPosition){
            return (_x == ((MapPosition)p2)._x && _y==((MapPosition)p2)._y);
        }
        // exception?
        return false;

    }
}
