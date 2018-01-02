package com.mushrooming.base;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.mushrooming.algorithms.AlgorithmModule;
import com.mushrooming.algorithms.DijkstraAssemblyManager;
import com.mushrooming.algorithms.DisconnectGraphManager;
import com.mushrooming.bluetooth.BluetoothEventHandler;
import com.mushrooming.bluetooth.BluetoothModule;
import com.mushrooming.bluetooth.DefaultBluetoothHandler;
import com.mushrooming.location.LocationService;
import com.mushrooming.map.MapModule;

import org.osmdroid.views.overlay.ItemizedIconOverlay;

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
    private LocationService _locationService;
    private  User _myUser;
    private Team _team;

    private AlgorithmModule _algorithms;
    private MapModule _map;

    public Context getApplicationContext() {return _applicationContext;}

    public Team getTeam(){
        return _team;
    }
    public BluetoothModule getBluetooth() { return _bluetooth; }
    public LocationService getLocationService() { return _locationService; }
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
                //_bluetooth.sendConnections();

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

        _locationService = new LocationService();

        _algorithms = new AlgorithmModule(DisconnectGraphManager.getOne(), DijkstraAssemblyManager.getOne());

        initDefaultTeam();
    }

    private void initDefaultTeam(){
        _team = new Team();

        int myUserId = _bluetooth.getMyUserId();
        _myUser = new User(myUserId);
        _team.addUser(_myUser);
    }
    public void set_map(MapModule _map) {
        this._map = _map;
    }

    public void markPosition(Context ctx, ItemizedIconOverlay.OnItemGestureListener listen) {
        this._map.markPosition(ctx, listen);
    }

    public void startSending(){
        Logger.debug(this, "startSending()");

        _updateHandler.postDelayed(_updateRunnable, UPDATE_MY_POSITION_TIME);
        _disconnectionHandler.postDelayed(_disconnectionRunnable, CHECK_DISCONNECTION_PROBLLEM_TIME);
    }

    public void finish(){
        _updateHandler.removeCallbacks(_updateRunnable);
        _disconnectionHandler.removeCallbacks(_disconnectionRunnable);
        _bluetooth.stop();
        _locationService.stop();
    }

    private void updateMyPosition(){
        Position myPos = _locationService.getLastPosition();
        if(myPos != null) {
            _bluetooth.sendPosition(myPos);
            _myUser.update(myPos);
        }
    }

    private void checkDisconnectionProblem() {
        Logger.debug(this, "checkDisconnectionProblem()");
        if (_algorithms.checkIfAssemblyNeeded(_team)) {
            //MapPosition assemblyPos = _assemblyManager.chooseAssemblyPlace(_team);

            Logger.error(this, "checkDisconnectionProblem() : assembly needed");
            // assembly may also be ordered when graph is still consistent, depending on used manager

            // here order an assembly - show that user should go there
        }
    }
}
