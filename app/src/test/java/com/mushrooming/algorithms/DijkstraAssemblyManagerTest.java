package com.mushrooming.algorithms;

import com.mushrooming.base.Position;
import com.mushrooming.base.Team;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by piotrek on 05.11.17.
 */
public class DijkstraAssemblyManagerTest {

    private void markOnAvMap(AvMap map, int[][] what) {
        for (int i=0; i<what.length; ++i) {
            for (int j=0; j<what[i].length; ++j) {
                if (what[i][j] != 0) {
                    map.markMapPosition(new MapPosition(i - what.length / 2,
                            j - what[i].length / 2));
                    //System.out.println((AvMap.size / 2 + i - what.length / 2) +" " + (AvMap.size / 2 + j - what[i].length / 2));
                }
            }
        }
    }

    // here for convenience we don't center map on this user - this is AssemblyManager testing

    @Test
    public void chooseAssemblyPlaceTest1() throws Exception {

        DijkstraAssemblyManager d = DijkstraAssemblyManager.getOne();
        AlgorithmModule mod = new AlgorithmModule(new DisconnectGraphManager(), d);

        AvMap avmap = mod.get_terrainOKmap();
        markOnAvMap(avmap, new int[][]{
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0}
                });

        Team team = new Team();

        team.updateUserPosition(1, new Position(0,0));
        team.updateUserPosition(2, new Position(0,0));

        MapPosition mpos = d.chooseMapAssemblyPlace(team, avmap);

        assertEquals(new MapPosition(0,0), d.chooseMapAssemblyPlace(team,avmap));

    }


    @Test
    public void chooseAssemblyPlaceTest2() throws Exception {

        DijkstraAssemblyManager d = DijkstraAssemblyManager.getOne();
        AlgorithmModule mod = new AlgorithmModule(new DisconnectGraphManager(), d);

        AvMap avmap = mod.get_terrainOKmap();
        markOnAvMap(avmap, new int[][]{
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0},
                {0,0,0,0,0}
        });

        Team team = new Team();

        team.updateUserPosition(1, new Position(2,2));
        team.updateUserPosition(2, new Position(2,-2));

        MapPosition mpos = d.chooseMapAssemblyPlace(team, avmap);

        assertEquals(new MapPosition(2,0), d.chooseMapAssemblyPlace(team, avmap));

    }


    @Test
    public void chooseAssemblyPlaceTest3() throws Exception {

        DijkstraAssemblyManager d = DijkstraAssemblyManager.getOne();
        AlgorithmModule mod = new AlgorithmModule(new DisconnectGraphManager(), d);

        AvMap avmap = mod.get_terrainOKmap();
        markOnAvMap(avmap, new int[][]{
                {0,0,1,0,0},
                {0,0,0,1,0},
                {0,0,0,1,0},
                {0,0,0,1,0},
                {0,0,1,0,0}
        });

        Team team = new Team();

        team.updateUserPosition(1, new Position(-2,0));
        team.updateUserPosition(2, new Position(2,0));

        MapPosition mpos = d.chooseMapAssemblyPlace(team, avmap);

        assertEquals(new MapPosition(0,1), d.chooseMapAssemblyPlace(team, avmap));

    }

    @Test
    public void chooseAssemblyPlaceTest4() throws Exception {

        DijkstraAssemblyManager d = DijkstraAssemblyManager.getOne();
        AlgorithmModule mod = new AlgorithmModule(new DisconnectGraphManager(), d);

        AvMap avmap = mod.get_terrainOKmap();
        markOnAvMap(avmap, new int[][]{
                {0,1,1,1,1},
                {1,0,0,0,1},
                {1,0,0,0,1},
                {1,0,0,0,1},
                {1,1,1,1,1}
        });

        Team team = new Team();

        team.updateUserPosition(1, new Position(-2,0));
        team.updateUserPosition(2, new Position(1,-2));

        MapPosition mpos = d.chooseMapAssemblyPlace(team, avmap);

        assertEquals(new MapPosition(-1,-2), d.chooseMapAssemblyPlace(team, avmap));

    }


    @Test
    public void chooseAssemblyPlaceTest5() throws Exception {

        DijkstraAssemblyManager d = DijkstraAssemblyManager.getOne();
        AlgorithmModule mod = new AlgorithmModule(new DisconnectGraphManager(), d);

        AvMap avmap = mod.get_terrainOKmap();
        markOnAvMap(avmap, new int[][]{
                {0,1,1,1,1},
                {1,0,0,0,1},
                {1,0,0,0,1},
                {1,0,0,0,1},
                {1,1,1,1,1}
        });

        Team team = new Team();

        team.updateUserPosition(1, new Position(-2,2));
        //team.updateUser(2, new Position(-1,2));
        team.updateUserPosition(3, new Position(0,2));
        team.updateUserPosition(4, new Position(2,-1));

        MapPosition mpos = d.chooseMapAssemblyPlace(team, avmap);

        //this is the correct answer because we minimize the sum of squares
        assertEquals(new MapPosition(1,2), d.chooseMapAssemblyPlace(team, avmap));

    }

}