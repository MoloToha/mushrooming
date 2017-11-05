package com.mushrooming.algorithms;

import com.mushrooming.base.Team;
import com.mushrooming.base.User;

/**
 * Created by piotrek on 04.11.17.
 */

public abstract class GraphManager {
    // for deciding if an assembly is necessary

    public boolean checkConsistent(Team team) {
        for(User user : team.getUsers()){
            if(!user.isConnected()) {
                return false;
            }
        }
        return true;
    }

    public abstract boolean checkIfAssemblyNeeded(Team team);

}
