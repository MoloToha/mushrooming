package com.mushrooming.algorithms;

import com.mushrooming.base.Position;
import com.mushrooming.base.Team;

/**
 * Created by piotrek on 29.11.17.
 */

public class AlgorithmModule {
    private GraphManager _graphManager;
    private AssemblyManager _assemblyManager;
    private AvMap _terrainOKmap = new AvMap();

    public AlgorithmModule(GraphManager graphM, AssemblyManager assemblyM) {
        _graphManager = graphM;
        _assemblyManager = assemblyM;
    }

    public Position chooseAssemblyPlace(Team team){
        return _assemblyManager.chooseGPSAssemblyPlace(team, _terrainOKmap); // not map position
    }

    public boolean checkIfAssemblyNeeded(Team team) {
        return _graphManager.checkIfAssemblyNeeded(team);
    }

    public AvMap get_terrainOKmap() {
        return _terrainOKmap;
    }
}
