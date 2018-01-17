package com.mushrooming.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;

import com.example.antonl.mushrooming.R;
import com.mushrooming.algorithms.AlgorithmModule;
import com.mushrooming.algorithms.DijkstraAssemblyManager;
import com.mushrooming.algorithms.DisconnectGraphManager;
import com.mushrooming.bluetooth.BluetoothEventHandler;
import com.mushrooming.bluetooth.BluetoothModule;
import com.mushrooming.bluetooth.DefaultBluetoothHandler;
import com.mushrooming.location.LocationService;
import com.mushrooming.map.MapModule;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

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
    public User getMyUser(){
        return _myUser;
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
                updateMapPositions();
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

        _map = new MapModule((MapView) mainActivity.findViewById(R.id.map));

        initDefaultTeam(mainActivity);
    }

    private void initDefaultTeam(Activity mainActivity){
        _team = new Team();

        int myUserId = _bluetooth.getMyUserId();
        String myName = DataManager.getMyName(mainActivity);

        _team.createUser(myUserId);
        _myUser = _team.getUser(myUserId);
        _team.updateUserName(myUserId, myName);
    }

    public void testMarkPosition() {
        this._map.testMarkPosition();
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

    public void updateMapPositions()
    {
        Logger.debug(this, "updateMapPositions()");
        _map.clearAllMarkers();
        for(User user : _team.getUsers())
        {
            Position userPos = user.getGpsPosition();
            GeoPoint geoPos = new GeoPoint(userPos.getX(), userPos.getY());
            _map.markPosition(true, geoPos, user.getName(), user.getColor());
        }
    }

    public void focusMyPosition()
    {
        Position userPos = _myUser.getGpsPosition();
        GeoPoint geoPos = new GeoPoint(userPos.getX(), userPos.getY());
        _map.centerMap(geoPos);
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
