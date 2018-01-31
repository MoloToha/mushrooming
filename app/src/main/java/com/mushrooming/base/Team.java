package com.mushrooming.base;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by barto on 24.10.2017.
 */

public class Team {

    private ArrayList<User> _users;
    private int _colorIdx = 0;
    private static int[] COLORS = {Color.BLACK, Color.RED, Color.BLUE, Color.YELLOW};
    // managers to be changed by team chef in some menu

    public Team() {
        _users = new ArrayList<>();
    }

    public void updateUserPosition(String address, Position pos) {
        User u = getUser(address);
        if(u == null){
            createUser(address);
            u = getUser(address);
        }

        u.update(pos);
    }

    public void updateUserName(String address, String name) {
        User u = getUser(address);
        if(u == null){
            createUser(address);
            u = getUser(address);
        }

        u.setName(name);
    }

    public ArrayList<User> getUsers() {
        return _users;
    }

    public boolean removeUser(String address) {
        User u = getUser(address);
        if(u != null){
            return _users.remove(u);
        }
        return false;
    }

    public boolean createUser(String address) {
        if(getUser(address) == null)
        {
            User newUser = new User(address);
            _users.add(newUser);
            newUser.setColor(COLORS[_colorIdx]);
            _colorIdx = (_colorIdx + 1)%4;
            return true;
        }
        return false;
    }

    public User getUser(String address) {
        for(User u : _users) {
            if(u.getAddress().equals(address))
                return u;
        }
        return null;
    }

}
