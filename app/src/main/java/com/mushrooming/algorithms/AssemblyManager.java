package com.mushrooming.algorithms;

import com.mushrooming.base.Position;
import com.mushrooming.base.Team;

/**
 * Created by piotrek on 05.11.17.
 */

public interface AssemblyManager {
    Position chooseGPSAssemblyPlace(Team team, AvMap terrainOKmap);
    MapPosition chooseMapAssemblyPlace(Team team, AvMap terrainOKmap);
}
