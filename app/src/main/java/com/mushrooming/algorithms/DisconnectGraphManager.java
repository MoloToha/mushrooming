package com.mushrooming.algorithms;

import com.mushrooming.base.Team;

/**
 * Created by piotrek on 05.11.17.
 */

public class DisconnectGraphManager extends GraphManager {

    private static DisconnectGraphManager instance = new DisconnectGraphManager();

    public static DisconnectGraphManager getOne() {
        return instance;
    }

    @Override
    public boolean checkIfAssemblyNeeded(Team team) {
        return !checkConsistent(team);
    }
}
