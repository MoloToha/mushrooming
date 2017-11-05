package com.mushrooming.base;

import com.mushrooming.algorithms.MapPosition;

/**
 * Created by barto on 24.10.2017.
 */

public class User {
    private int _id; // uniqe id for every user
    private Position _GPSpos;
    private MapPosition _MAPpos; // position on THIS device's map, may be different on other devices
    private long _lastUpdate = 0;

    public User(int id, Position pos) {
        this._id = id;
        update(pos);
    }

    public void update(Position pos) {
        _lastUpdate = System.currentTimeMillis();
        setGpsPosition(pos);
        setMapPosition(new MapPosition(pos));
    }

    public int getId(){
        return _id;
    }

    public Position getGpsPosition() {
        return _GPSpos;
    }

    private void setGpsPosition(Position _pos) {
        this._GPSpos = _pos;
    }

    public MapPosition getMapPosition() {
        return _MAPpos;
    }

    public void setMapPosition(MapPosition _MAPpos) {
        this._MAPpos = _MAPpos;
    }

    public static final long MAX_INACTIVITY_TIME = 10000;
    public boolean isConnected() {
        return System.currentTimeMillis() - _lastUpdate < MAX_INACTIVITY_TIME;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof User){
            return getId() == ((User)obj).getId();
        }
        // exception?
        return false;
    }
}