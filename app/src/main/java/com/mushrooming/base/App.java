package com.mushrooming.base;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.mushrooming.algorithms.AlgorithmModule;
import com.mushrooming.algorithms.DijkstraAssemblyManager;
import com.mushrooming.algorithms.DisconnectGraphManager;
import com.mushrooming.algorithms.GraphManager;
import com.mushrooming.bluetooth.BluetoothEventHandler;
import com.mushrooming.bluetooth.BluetoothModule;
import com.mushrooming.bluetooth.DefaultBluetoothHandler;

import static android.content.ContentValues.TAG;

/**
 * Created by barto on 08.11.2017.
 */

public class App {
    private static App _instance = null;
    public static App instance(){
        if(_instance == null){
            _instance = new App();
        }
        return _instance;
    }

    private Debug _debug;
    private Context _applicationContext;
    private BluetoothModule _bluetooth;
    private Team _team = new Team();

    private AlgorithmModule _algorithms;

    public Context getApplicationContext() {return _applicationContext;}

    public Team getTeam(){
        return _team;
    }
    public BluetoothModule getBluetooth() { return _bluetooth; }
    public Debug getDebug() { return _debug; }

    private static int UPDATE_MY_POSITION_TIME = User.MAX_INACTIVITY_TIME / 4;
    private static int CHECK_DISCONNECTION_PROBLLEM_TIME = 2000;

    private Handler _updateHandler;
    private Runnable _updateRunnable;
    private Handler _disconnectionHandler;
    private Runnable _disconnectionRunnable;

    // We need to store reference to handler, because MyHandler class
    // stores a weak reference to this field in order to flush all
    // messages when application is destroyed
    @SuppressWarnings("FieldCanBeLocal")
    private BluetoothEventHandler _bluetoothHandler;

    public void init(Activity mainActivity){
        _applicationContext = mainActivity.getApplicationContext();
		_debug = new Debug();

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

        _bluetoothHandler = new DefaultBluetoothHandler();
        _bluetooth = new BluetoothModule(mainActivity, _bluetoothHandler);
        _bluetooth.start();

        _algorithms = new AlgorithmModule(DisconnectGraphManager.getOne(), DijkstraAssemblyManager.getOne());
    }

    public void startSending(){
        _updateHandler.postDelayed(_updateRunnable, UPDATE_MY_POSITION_TIME);
        _disconnectionHandler.postDelayed(_disconnectionRunnable, CHECK_DISCONNECTION_PROBLLEM_TIME);
    }

    public void finish(){
        _updateHandler.removeCallbacks(_updateRunnable);
        _disconnectionHandler.removeCallbacks(_disconnectionRunnable);
        _bluetooth.stop();
    }

    private void updateMyPosition(){
        // GPS: get position
        _bluetooth.sendPosition(new Position(42,666));
    }

    private void checkDisconnectionProblem() {
        Logger.error(this, "checkDisconnectionProblem");
        if (_algorithms.checkIfAssemblyNeeded(_team)) {
            //MapPosition assemblyPos = _assemblyManager.chooseAssemblyPlace(_team);

            Logger.error(this, "checkDisconnectionProblem() : assembly needed");
            // assembly may also be ordered when graph is still consistent, depending on used manager

            // here order an assembly - show that user should go there
        }
    }
}
