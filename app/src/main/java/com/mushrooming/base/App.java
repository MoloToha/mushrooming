package com.mushrooming.base;

import android.app.Activity;
import android.util.Log;

import com.mushrooming.algorithms.AssemblyManager;
import com.mushrooming.algorithms.AvMap;
import com.mushrooming.algorithms.DijkstraAssemblyManager;
import com.mushrooming.algorithms.DisconnectGraphManager;
import com.mushrooming.algorithms.GraphManager;
import com.mushrooming.algorithms.MapPosition;
import com.mushrooming.bluetooth.BluetoothModule;
import com.mushrooming.bluetooth.DefaultBluetoothHandler;

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
    private Timer _timer;

    public void init(Activity activity){
        _activity = activity;

        _ui = new UI(activity);
        _bluetooth = new BluetoothModule(activity, new DefaultBluetoothHandler());
        _bluetooth.start();

        _timer = new Timer();
        _timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateMyPosition();
            }
        }, START_DELAY , UPDATE_MY_POSITION_TIME);

        _timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkDisconnectionProblem();
            }
        }, START_DELAY, CHECK_DISCONNECTION_PROBLLEM_TIME);
    }

    public void finish(){
        _timer.cancel();
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
