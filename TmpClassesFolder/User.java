package com.example.barto.tmp;

/**
 * Created by barto on 24.10.2017.
 */

public class User {
    private int _id; // uniqe id for every user
    private Position _pos;
    private long _lastUpdate = 0;

    public User(int id, Position pos) {
        this._id = id;
        update(pos);
    }

    public void update(Position pos) {
        _lastUpdate = System.currentTimeMillis();
        setPosition(pos);
    }

    public int getId(){
        return _id;
    }

    public Position getPosition() {
        return _pos;
    }

    private void setPosition(Position _pos) {
        this._pos = _pos;
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
