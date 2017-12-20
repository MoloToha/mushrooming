
//this file is .java only for syntax highlighting

public class map {

    static int size = 905;

    bool availableTerrain[size][size]; //primitive, no need to initialize by hand
    int xpos, ypos;

    public map() {
        xpos = size/2;
        ypos = size/2;
    }
    
    public markPositions(positionList posl) {
        for (position p : posl) {
            availableTerrain[p.getX()][p.getY()] = true;
        }
    }
    
    public notIn(int p) {
        return (p<0 || p>=size); //compare with defined
    }
    
    public recenter() {
        if (xpos< size/3 || xpos > (2*size)/3 || ypos < size/3 || ypos > (s*size)/3) {
        
            int xdelta = size/2 - xpos;
            int ydelta = size/2 - ypos;
            int x1,x2,y1,y2,xd,yd;
            
            //maybe put in some other funct
            if (xdelta < 0) {
                x1 = 0; x2 = size; xd = 1;
            } else {
                x1 = size; x2 = 0; xd = -1;  //change to some max value stored
            }
            if (ydelta < 0) {
                y1 = 0; y2 = size; yd = 1;
            } else {
                y1 = size; y2 = 0;  yd = -1; //change to some max value stored
            }
            
            for (int i=x1; i!=x2; i+=xd) {
                for (int y=y1; y!=y2; y+=yd) {
                    if (notIn(i) || notIn(j)) continue;
                    availableTerrain[i][j] = availableTerrain[i-xdelta][j-ydelta];
                }
            }
        }
        
    }
    
    public chooseAssemplyPlace (positionList posl) {
        // one grid for Dijkstra from current device and one for sum of distances (squares) from previous Dijkstras
        
        int thisDijkstra[size][size];
        int sumDijkstra[size][size];
        
        //run posl.size() Dijkstras, accumulate results and choose best place which is marked as available
    }

}



//in other file
public class position {
    
    int x = map.size/2;
    int y = map.size/2;
    
    public getX() {
        return x;
    }
    
    public getY() {
        return y;
    }

}


public class positionList {
    
    ArrayList <position> l = new ArrayList<>();
    
    public push(position pos) {
        l.add(pos);
    }

}
