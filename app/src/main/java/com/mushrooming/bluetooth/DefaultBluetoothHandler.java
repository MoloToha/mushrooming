package com.mushrooming.bluetooth;

import android.content.Context;

import com.example.antonl.mushrooming.R;
import com.mushrooming.base.App;
import com.mushrooming.base.Logger;
import com.mushrooming.base.Position;
import com.mushrooming.base.Debug;
import com.mushrooming.base.User;

import java.util.ArrayList;

public class DefaultBluetoothHandler implements BluetoothEventHandler {

    private Context _applicationContext;
    public DefaultBluetoothHandler(){
        _applicationContext = App.instance().getApplicationContext();
    }
    
    public void connecting(String device){
        Logger.info(this, _applicationContext.getString(R.string.connecting, device));
    }

    public void connected(String device){
        Logger.info(this, _applicationContext.getString(R.string.connected, device));

        // Try to create new user. If this user already exists, change its status to connected
        if( !App.instance().getTeam().createUser(device) ){
            User u = App.instance().getTeam().getUser(device);
            u.setConnectionStatus(User.ConnectionStatus.TimeDependent);
        }
    }

    public void connectionFailed(String device){
        Logger.warning(this, _applicationContext.getString(R.string.connection_failed, device));
    }

    public void connectionLost(String device){
        Logger.info(this, _applicationContext.getString(R.string.connection_lost, device));

        // Change user status to disconnected
        User u = App.instance().getTeam().getUser(device);
        if( u != null )
            u.setConnectionStatus(User.ConnectionStatus.ForceDisconnected);
    }

    public void positionReceived(String device, double x, double y){
        Logger.debug(this, _applicationContext.getString(R.string.position_received, device, x, y));

        App.instance().getTeam().updateUserPosition(device, new Position(x,y));
    }

    public void connectionsReceived(String device, ArrayList<String> connections){
        Logger.debug(this, _applicationContext.getString(R.string.connections_received, device));

        for( String connection : connections ){
            Logger.debug(this, connection);
            App.instance().getBluetooth().connectDevice(connection);
        }
    }

    public void nameReceived(String device, String name) {
        Logger.debug(this, _applicationContext.getString(R.string.name_received, device, name));

        App.instance().getTeam().updateUserName(device, name);
    }
}
