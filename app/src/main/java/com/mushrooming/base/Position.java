package com.mushrooming.base;

/**
 * Created by barto on 24.10.2017.
 */

public class Position {
    private double _x, _y;
    // remember latitude and longitude, +/-: N/S E/W
    // ,aybe remember it in more fields: degs, mins, secs

    public Position(double x, double y) {
        _x = x;
        _y = y;
    }

    public double getX() {
        return _x;
    }

    public void setX(double x) {
        _x = x;
    }

    public double getY() {
        return _y;
    }

    public void setY(double y) {
        _y = y;
    }


}
