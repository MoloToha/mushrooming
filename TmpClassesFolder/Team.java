package com.example.barto.tmp;

import java.util.ArrayList;

/**
 * Created by barto on 24.10.2017.
 */

public class Team {
    private ArrayList<User> _users;

    public Team() {

    }

    public void addUsers(User user) {
        _users.add(user);
    }

    public void removeUser(User user) {
        _users.remove(user);
    }
}
