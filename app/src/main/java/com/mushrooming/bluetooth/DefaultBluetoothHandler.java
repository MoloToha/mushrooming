package com.mushrooming.bluetooth;

import android.content.Context;

import com.example.antonl.mushrooming.R;
import com.mushrooming.base.App;
import com.mushrooming.base.Logger;
import com.mushrooming.base.Position;
import com.mushrooming.base.Debug;

import java.util.ArrayList;

/**
 * Created by barto on 20.11.2017.
 */

public class DefaultBluetoothHandler implements BluetoothEventHandler {

    private Context _applicationContext;
    public DefaultBluetoothHandler(){
        _applicationContext = App.instance().getApplicationContext();
    }
    
    public void connecting(String device){
        Logger.debug(this, _applicationContext.getString(R.string.connecting, device));
    }

    public void connected(String device){
        Logger.debug(this, _applicationContext.getString(R.string.connected, device));
    }

    public void connectionFailed(String device){
        Logger.error(this, _applicationContext.getString(R.string.connection_failed, device));
    }

    public void connectionLost(String device){
        Logger.warning(this, _applicationContext.getString(R.string.connection_lost, device));
    }

    public void positionReceived(String device, double x, double y){
        Logger.debug(this, _applicationContext.getString(R.string.position_received, device, x, y));

        App.instance().getTeam().updateUser(device.hashCode(), new Position(x,y));
    }

    public void connectionsReceived(String device, ArrayList<String> connections){
        Logger.debug(this, _applicationContext.getString(R.string.connections_received, device));

        for( String connection : connections ){
            Logger.debug(this, connection);
            App.instance().getBluetooth().connectDevice(connection);
        }
    }
}
