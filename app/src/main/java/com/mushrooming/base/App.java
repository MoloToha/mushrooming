package com.mushrooming.base;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.mushrooming.algorithms.AssemblyManager;
import com.mushrooming.algorithms.AvMap;
import com.mushrooming.algorithms.DijkstraAssemblyManager;
import com.mushrooming.algorithms.DisconnectGraphManager;
import com.mushrooming.algorithms.GraphManager;
import com.mushrooming.algorithms.MapPosition;
import com.mushrooming.bluetooth.BluetoothModule;
import com.mushrooming.bluetooth.DefaultBluetoothHandler;

import static android.content.ContentValues.TAG;

/**
 * Created by barto on 08.11.2017.
 */

public class App {
    private static App _instance = new App();
    public static App instance(){
        return _instance;
    }

    private Activity _activity;
    private UI _ui;
    private BluetoothModule _bluetooth;
    private Team _team = new Team();

    // move to Algorithm module
    private GraphManager _graphManager = DisconnectGraphManager.getOne(); // default
    private AssemblyManager _assemblyManager = DijkstraAssemblyManager.getOne(); // default
    private AvMap _terrainOKmap = new AvMap();

    public Activity getActivity() {return _activity;}
    public AvMap getAvMap(){
        return _terrainOKmap;
    }
    public Team getTeam(){
        return _team;
    }
    public BluetoothModule getBluetooth() { return _bluetooth; }
    public UI getUI() { return _ui; }

    private static  int START_DELAY = 100;
    private static int UPDATE_MY_POSITION_TIME = User.MAX_INACTIVITY_TIME / 4;
    private static int CHECK_DISCONNECTION_PROBLLEM_TIME = 2000;

    private Handler _updateHandler;
    private Runnable _updateRunnable;
    private Handler _disconnectionHandler;
    private Runnable _disconnectionRunnable;

    public void init(Activity activity){
        _activity = activity;

        _ui = new UI(activity);

        _updateHandler = new Handler();
        _updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateMyPosition();

                _updateHandler.postDelayed(this, UPDATE_MY_POSITION_TIME);
            }
        };

        _disconnectionHandler = new Handler();
        _disconnectionRunnable = new Runnable() {
            @Override
            public void run() {
                checkDisconnectionProblem();

                _disconnectionHandler.postDelayed(this, CHECK_DISCONNECTION_PROBLLEM_TIME);
            }
        };
        _disconnectionHandler.postDelayed(_disconnectionRunnable, CHECK_DISCONNECTION_PROBLLEM_TIME);

        _bluetooth = new BluetoothModule(activity, new DefaultBluetoothHandler());
        _bluetooth.start();
    }

    public void startSending(){
        _updateHandler.postDelayed(_updateRunnable, UPDATE_MY_POSITION_TIME);
    }

    public void finish(){
        _updateHandler.removeCallbacks(_updateRunnable);
        _disconnectionHandler.removeCallbacks(_disconnectionRunnable);
        _bluetooth.stop();
    }

    private void updateMyPosition(){
        _ui.write("sending my position");
        // GPS: get position
        _bluetooth.sendPosition(new Position(42,666));
    }

    private void checkDisconnectionProblem() {
        Log.d(TAG, "checkDisconnectionProblem");
        if (_graphManager.checkIfAssemblyNeeded(_team)) {
            //MapPosition assemblyPos = _assemblyManager.chooseAssemblyPlace(_team);

            _ui.write("checkDisconnectionProblem() : assembly needed");
            // assembly may also be ordered when graph is still consistent, depending on used manager

            // here order an assembly - show that user should go there
        }
    }
}
