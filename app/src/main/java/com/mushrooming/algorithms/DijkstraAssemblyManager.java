package com.mushrooming.algorithms;

import com.mushrooming.base.Position;
import com.mushrooming.base.Team;
import com.mushrooming.base.User;

import java.util.PriorityQueue;

import static java.lang.Math.sqrt;


/**
 * Created by piotrek on 04.11.17.
 */

public class DijkstraAssemblyManager implements AssemblyManager {

    // TODO maybe add option for some team chef to choose assembly place, but only if
    // the device graph is still consistent - to avoid problem with obstacles like fences and accuracy (see AvMap TODOs)

    // TODO maybe also add another strategy of choosing place, as another manager

    private static int infty = 1000000000;
    private PriorityQueue<PairSortedBy1<Double, MapPosition>> q = new PriorityQueue<>();

    private static DijkstraAssemblyManager instance = new DijkstraAssemblyManager();

    public static DijkstraAssemblyManager getOne() {
        return instance;
    }

    private void computeNewDijkstra(double[][] map, AvMap avMap, MapPosition centerRel) {
        if (centerRel == null || map == null || avMap == null) return;
        for(int i=0 ; i<map.length; ++i) {
            for (int j=0; j<map[i].length; ++j) {
                map[i][j] = infty;
            }
        }
        MapPosition pos = avMap.getAbsoluteMapPositionFromCenterRelative(centerRel);
        map[pos.getIntX()][pos.getIntY()] = 0;
        q.clear();
        q.add(new PairSortedBy1(0.0,pos));
        PairSortedBy1<Double, MapPosition> now;
        double nowDist, newDist, modif;
        int nowX, nowY;
        while (!q.isEmpty()) {
            now = q.poll(); // also removes
            nowDist = now.getF();
            nowX = now.getS().getIntX();
            nowY = now.getS().getIntY();
            if (map[nowX][nowY] < nowDist) continue;
            for (int i=nowX-1; i<nowX+2; ++i) {
                for (int j=nowY-1; j<nowY+2; ++j) {
                    if (AvMap.notIn(i) || AvMap.notIn(j)) continue;
                    if (i==nowX || j==nowY) modif=2.0; else modif = 2*sqrt(2);
                    if (avMap.availableTerrain(i,j)) modif = 0.5*modif;
                    newDist = nowDist + modif;
                    if (newDist < map[i][j]) {
                        q.add(new PairSortedBy1(newDist, new MapPosition(i, j)));
                        map[i][j] = newDist;
                    }
                }
            }
        }
    }


    // can be static, but when no interface
    // TODO this is only for tests, maybe delete it and move code to function returning GPS position
    @Override
    public MapPosition chooseMapAssemblyPlace(Team team, AvMap terrainOKmap) {
        // one grid for Dijkstra from current device and one for sum of distances (squares) from previous Dijkstras
        double[][] thisDijkstra = new double[AvMap.size][AvMap.size];
        double[][] sumDijkstra = new double[AvMap.size][AvMap.size];

        //run one Dijkstra for each user, accumulate results and choose best place (from marked as available)
        for (User u : team.getUsers() ){
            computeNewDijkstra(thisDijkstra, terrainOKmap, terrainOKmap.getCenterRelativeMapPositionFromGPS(u.getGpsPosition())/*u.getMapPosition()*/);
            for (int i=0; i<AvMap.size; ++i) {
                for (int j=0; j<AvMap.size; ++j) {
                    sumDijkstra[i][j] += (thisDijkstra[i][j])*(thisDijkstra[i][j]);
                }
            }
        }

        double bestDist = infty;
        int bestx = AvMap.center, besty = AvMap.center;

        for (int i=0; i<AvMap.size; ++i) {
            for (int j=0; j<AvMap.size; ++j) {
                if (sumDijkstra[i][j] < bestDist) {
                    bestDist = sumDijkstra[i][j];
                    bestx = i;
                    besty = j;
                }
            }
        }

        // bestx, besty are center-relative (called)

        return terrainOKmap.getCenterRelativeMapPositionFromAbsolute(new MapPosition(bestx, besty));
               //terrainOKmap.getCenterRelativeMapPosition(new MapPosition(bestx, besty));
               //terrainOKmap.getRelativeToCurrentMapPosition(new MapPosition(bestx, besty));
        // NOPE - we need to get GPS position to show; same as with "available" terrain
        // we want non-relative GPS position
    }

    @Override
    public Position chooseGPSAssemblyPlace(Team team, AvMap terrainOKmap) {
        MapPosition mpCenterRelative = chooseMapAssemblyPlace(team, terrainOKmap);
        return terrainOKmap.getNonRelativeGPSposition(mpCenterRelative);
    }


}
