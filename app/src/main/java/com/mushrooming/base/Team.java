package com.mushrooming.base;

import com.mushrooming.algorithms.AssemblyManager;
import com.mushrooming.algorithms.AvMap;
import com.mushrooming.algorithms.DijkstraAssemblyManager;
import com.mushrooming.algorithms.DisconnectGraphManager;
import com.mushrooming.algorithms.GraphManager;
import com.mushrooming.algorithms.MapPosition;

import java.util.ArrayList;

/**
 * Created by barto on 24.10.2017.
 */

public class Team {

    private ArrayList<User> _users;
    // managers to be changed by team chef in some menu
    private GraphManager _graphManager = DisconnectGraphManager.getOne(); // default
    private AssemblyManager _assemblyManager = DijkstraAssemblyManager.getOne(); // default
    private AvMap _terrainOKmap = new AvMap();

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

    public ArrayList<User> getUsers() {
        return _users;
    }

    public AvMap getAvMap() {
        return _terrainOKmap;
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

    // moved logic to GraphManager implementations
    // team should now have GraphManager and AssemblyManager
    /*
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
    */
    // using managers:
    private void checkIfAssemblyNeeded() {
        if(_graphManager.checkIfAssemblyNeeded(this)){
            MapPosition assemblyPos = _assemblyManager.chooseAssemblyPlace(this);
            // assembly may also be ordered when graph is still consistent, depending on used manager

            // here order an assembly - show that user should go there
        }
    }
}
