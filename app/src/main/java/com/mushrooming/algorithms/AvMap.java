package com.mushrooming.algorithms;

import com.mushrooming.base.App;
import com.mushrooming.base.Position;

import java.util.Collection;


/**
 * Created by piotrek on 04.11.17.
 */

public class AvMap {
    public static int size = 905;
    public static int center = size/2; 

    // https://gis.stackexchange.com/questions/2951/algorithm-for-offsetting-a-latitude-longitude-by-some-amount-of-meters/2964
    public static double XSCALE = 111111; // approx 111111 meters for one degree
    public static double YSCALE = 111111; // approx 111111*cos(xGPS) is one degree

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

    private Position positionGPSofCenter; // use latitude and longitude as int coordinate on plane
    // update this on every mark?
    // OF CENTER BECAUSE WHEN UPDATING CURRENT POS ERROR CAN GROW - BECAUSE OF ROUNDING TO INTEGER ON EACH SET
    // GPS coordinate of center or (xpos, ypos) position - my position - to decide
    private double xpos, ypos; // x and y pos of center!!! - always size/2, except for recenter
    private int xmin, xmax, ymin, ymax; // range of used area of a map

    // all functions (except getRelativeToCurrentMapPosition) take relative positions as arguments!

    public AvMap() {
        Position pos = null;
        if (App.instance().getLocationService() != null) {
            pos = App.instance().getLocationService().getLastPosition();
        }
        if (pos == null) pos = new Position(0,0);
        positionGPSofCenter = pos;
        xpos = size/2;
        ypos = size/2;
        xmin = (int)xpos;
        xmax = (int)xpos;
        ymin = (int)ypos;
        ymax = (int)ypos;
        //availableTerrain[(int)xpos][(int)ypos] = true;
    }

    public void setZeroPos() {
        positionGPSofCenter = new Position(0,0);
    }

    public boolean availableTerrain(int i, int j) {
        return availableTerrain[i][j];
    }

    public void markCenterRelativeMapPosition(MapPosition relativeToCenter) {
        int x1 = (int) (relativeToCenter.getX() + xpos), y1 = (int) (relativeToCenter.getY() + ypos);
        availableTerrain[x1][y1] = true;
        xmin = Basic.min(xmin, x1);
        xmax = Basic.max(xmax, x1);
        ymin = Basic.min(ymin, y1);
        ymax = Basic.max(ymax, y1);
    }


    public void markCenterRelativeMapPositions(Collection<MapPosition> relativeToCenterList) {
        for (MapPosition p : relativeToCenterList) {
            markCenterRelativeMapPosition(p);
        }
    }


    public void markPosition(Position posGPS) {

        MapPosition centerRelative = getCenterRelativeMapPositionFromGPS(posGPS);

        int x1 = centerRelative.getIntX();
        int y1 = centerRelative.getIntY();

        availableTerrain[x1][y1] = true;
        xmin = Basic.min(xmin, x1);
        xmax = Basic.max(xmax, x1);
        ymin = Basic.min(ymin, y1);
        ymax = Basic.max(ymax, y1);

        recenter(posGPS);
    }


    public void markPositions(Collection<Position> posGPSlist) {
        for (Position p : posGPSlist) {
            markPosition(p);
        }
    }

    //public void moveToRelativeToCurrentMapPosition(MapPosition relativeToCurrent) {
    //    xpos += relativeToCurrent.getX();
    //    ypos += relativeToCurrent.getY();
    //}

    //public MapPosition getCenterRelativeMapPosition(MapPosition relativeToCurrent) {
    //    return new MapPosition(relativeToCurrent.getX()+xpos, relativeToCurrent.getY()+ypos);
    //}

    public MapPosition getCenterRelativeMapPositionFromAbsolute(MapPosition absolute) {
        return new MapPosition(absolute.getX() - xpos, absolute.getY() - ypos);
    }

    public MapPosition getAbsoluteMapPositionFromCenterRelative(MapPosition centerRel) {
        return new MapPosition(centerRel.getX() + xpos, centerRel.getY() + ypos);
    }

    // center relative means absolute position, so to some corner...
    public MapPosition getCenterRelativeMapPositionFromGPS(Position posGPS) {
        double xGPS = posGPS.getX();
        double yGPS = posGPS.getY();
        return new MapPosition(
            (xGPS - positionGPSofCenter.getX())*XSCALE,
            (yGPS - positionGPSofCenter.getY())*YSCALE*Math.cos(xGPS)
        );
    }

    public MapPosition getRelativeToCurrentMapPosition(MapPosition relativeToCenter) {
        return new MapPosition(relativeToCenter.getX()-xpos, relativeToCenter.getY()-ypos);
    }

    public Position getNonRelativeGPSposition(MapPosition centerRelative){
        double xGPS = centerRelative.getX() * (1.0/XSCALE) + positionGPSofCenter.getX();
        double yGPS = centerRelative.getY() * (1.0/ (YSCALE*Math.cos(xGPS)) ) + positionGPSofCenter.getY();
        return new Position(xGPS, yGPS);
    }

    public static boolean notIn(int p) {
        return (p<0 || p>=size); //compare with defined
    }

    public void recenter(Position posGPS) {

        MapPosition centerRelative = getCenterRelativeMapPositionFromGPS(posGPS);

        int x1_ = centerRelative.getIntX();
        int y1_ = centerRelative.getIntY();

        xpos = x1_;
        ypos = y1_;  //does part of the job for recentering

        positionGPSofCenter = posGPS;

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
