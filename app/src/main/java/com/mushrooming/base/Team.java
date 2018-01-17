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
        _users = new ArrayList<User>();
    }

    public void updateUserPosition(int id, Position pos) {
        User u = getUser(id);
        if(u == null){
            createUser(id);
            u = getUser(id);
            u.setColor(COLORS[_colorIdx]);
            _colorIdx = (_colorIdx + 1)%4;
        }

        u.update(pos);
    }

    public void updateUserName(int id, String name) {
        User u = getUser(id);
        if(u == null){
            createUser(id);
        }

        u.setName(name);
    }

    public ArrayList<User> getUsers() {
        return _users;
    }

    public boolean removeUser(int id) {
        User u = getUser(id);
        if(u != null){
            return _users.remove(u);
        }
        return false;
    }

    public boolean createUser(int id) {
        if(getUser(id) == null)
        {
            User newUser = new User(id);
            _users.add(newUser);
            newUser.setColor(COLORS[_colorIdx]);
            _colorIdx = (_colorIdx + 1)%4;
            return true;
        }
        return false;
    }

    public User getUser(int id) {
        for(User u : _users) {
            if(u.getId() == id) {
                return u;
            }
        }
        return null;
    }

}
