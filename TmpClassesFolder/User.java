package com.example.barto.tmp;

/**
 * Created by barto on 24.10.2017.
 */

public class User {
    private Position _pos;
    private boolean _isConnected;

    public User(Position pos) {
        setPos(pos);
        setConnection(true);
    }

    public Position getPos() {
        return _pos;
    }

    public void setPos(Position _pos) {
        this._pos = _pos;
    }

    public void setConnection(boolean isConnected) {
        _isConnected = isConnected;
    }


}
