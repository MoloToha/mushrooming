package com.mushrooming.base;

import java.util.ArrayList;

/**
 * Created by barto on 24.10.2017.
 */

public class Team {
    private ArrayList<User> _users;

    public Team() {
        _users = new ArrayList<User>();
    }

    public void updateUser(int id, Position pos) {
        User u = findUser(id);
        if(u == null){
            _users.add(new User(id, pos));
        }
        else {
            u.update(pos);
        }
    }

    public boolean removeUser(int id) {
        User u = findUser(id);
        if(u != null){
            return _users.remove(u);
        }
        return false;
    }

    private User findUser(int id) {
        for(User u : _users) {
            if(u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    private void checkDisconectionProblem() {
        boolean problemDetected = false;
        for(User user : _users){
            if(!user.isConnected()) {
                problemDetected = true;
            }
        }

        if(problemDetected){
            // use algortihm
        }
    }
}
