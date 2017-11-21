package com.mushrooming.algorithms;

import java.util.Collection;
import java.util.List;


/**
 * Created by piotrek on 04.11.17.
 */

public class AvMap {
    public static int size = 905;
    public static int center = size/2; 

    private boolean[][] availableTerrain = new boolean[size][size];
    // TODO maybe also draw directions in which someone went ??
    // to try to avoid getting stuck on the other side of the fence etc
    // BUT GPS accuracy may be a big problem, determining on which side of a fence we are
    // probably will be impossible

    // TODO maybe add option to mark obstacles?
    // maybe users would mark obstacles? but again, accuracy determining on which side we are
    // so maybe users would mark obstacles like fences, but they would not be
    // taken into consideration by assembly place ordering algorithm?
    // or maybe would be taken into consideration

    private double xpos, ypos;
    private int xmin, xmax, ymin, ymax; // range of used area of a map

    public AvMap() {
        xpos = size/2;
        ypos = size/2;
        xmin = (int)xpos;
        xmax = (int)xpos;
        ymin = (int)ypos;
        ymax = (int)ypos;
        availableTerrain[(int)xpos][(int)ypos] = true;
    }

    public boolean[][] getAvailableTerrain() {
        return availableTerrain;
    }

    public void markPosition(MapPosition pos) {
        int x1 = pos.getIntX(), y1 = pos.getIntY();
        availableTerrain[x1][y1] = true;
        xmin = Basic.min(xmin, x1);
        xmax = Basic.max(xmax, x1);
        ymin = Basic.min(ymin, y1);
        ymax = Basic.max(ymax, y1);
    }

    public void markPositions(Collection<MapPosition> posl) {
        for (MapPosition p : posl) {
            markPosition(p);
        }
    }

    public void moveToPosition(MapPosition mp) {
        xpos = mp.getX();
        ypos = mp.getY();
    }

    public MapPosition getNonRelativePostition(MapPosition mp) {
        return new MapPosition(mp.getX()+xpos, mp.getY()+ypos);
    }

    public static boolean notIn(int p) {
        return (p<0 || p>=size); //compare with defined
    }

    public void recenter() {
        if (xpos< size/3 || xpos > (2*size)/3 || ypos < size/3 || ypos > (2*size)/3) {

            int xdelta = (int)xpos - center;
            int ydelta = (int)ypos - center;
            int x1,x2,y1,y2,xd,yd;

            // determine direction of offset and thus sequence of map update
            if (xdelta > 0) {
                x1 = Basic.max(xmin-xdelta, 0);
                x2 = xmax;
                xd = 1;
            } else {
                x1 = Basic.min(xmax-xdelta, size);
                x2 = xmin;
                xd = -1;
            }
            if (ydelta > 0) {
                y1 = Basic.max(ymin-ydelta, 0);
                y2 = ymax;
                yd = 1;
            } else {
                y1 = Basic.min(ymax-ydelta, size);
                y2 = ymin;
                yd = -1;
            }

            // update map without copying, writing on fields whose data will be out of map range first
            for (int i=x1; i!=x2; i+=xd) {
                for (int j=y1; j!=y2; j+=yd) {
                    if (notIn(i-xdelta) || notIn(j-ydelta)) availableTerrain[i][j] = false;
                    else availableTerrain[i][j] = availableTerrain[i-xdelta][j-ydelta];
                }
            }

            xmin = Basic.max(xmin-xdelta, 0);
            xmax = Basic.min(xmax-xdelta, size);
            ymin = Basic.max(ymin-ydelta, 0);
            ymax = Basic.min(ymax-ydelta, size);

        }

    }

}
