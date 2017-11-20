package com.mushrooming.base;

import android.util.Log;

import com.mushrooming.algorithms.AssemblyManager;
import com.mushrooming.algorithms.AvMap;
import com.mushrooming.algorithms.DijkstraAssemblyManager;
import com.mushrooming.algorithms.DisconnectGraphManager;
import com.mushrooming.algorithms.GraphManager;
import com.mushrooming.algorithms.MapPosition;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * Created by barto on 08.11.2017.
 */

public class App {
    private static App _instance = new App();
    public static App instance(){
        return _instance;
    }

    private Team _team = new Team();
    private GraphManager _graphManager = DisconnectGraphManager.getOne(); // default
    private AssemblyManager _assemblyManager = DijkstraAssemblyManager.getOne(); // default
    private AvMap _terrainOKmap = new AvMap();
    public AvMap getAvMap(){
        return _terrainOKmap;
    }

    private static int UPDATE_MY_POSITION_TIME = User.MAX_INACTIVITY_TIME / 4;
    private static int CHECK_DISCONNECTION_PROBLLEM_TIME = 2000;
    Timer _timer;
    public void init(){
        _timer = new Timer();
        _timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateMyPosition();
            }
        }, 0 ,UPDATE_MY_POSITION_TIME);

        _timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkDisconnectionProblem();
            }
        }, 0 ,CHECK_DISCONNECTION_PROBLLEM_TIME);
    }

    private void updateMyPosition(){
        System.out.print("updateMyPosition");
        Log.d(TAG, "updateMyPosition");
        // GPS: get position
        // Bluetooth: send my position to other
    }

    private void checkDisconnectionProblem(){
        Log.d(TAG, "checkDisconnectionProblem");
        if(_graphManager.checkIfAssemblyNeeded(_team)) {
            MapPosition assemblyPos = _assemblyManager.chooseAssemblyPlace(_team);
            // assembly may also be ordered when graph is still consistent, depending on used manager

            // here order an assembly - show that user should go there
        }
    }



}
