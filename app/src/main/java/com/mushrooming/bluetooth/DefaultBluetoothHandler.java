package com.mushrooming.bluetooth;

import android.app.Activity;
import android.content.Context;

import com.example.antonl.mushrooming.R;
import com.mushrooming.base.App;
import com.mushrooming.base.Logger;
import com.mushrooming.base.Position;
import com.mushrooming.base.Debug;

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
        Logger.debug(this, _applicationContext.getString(R.string.connection_failed, device));
    }

    public void connectionLost(String device){
        Logger.debug(this, _applicationContext.getString(R.string.connection_lost, device));
    }

    public void positionSent(String device){
        Logger.debug(this, _applicationContext.getString(R.string.position_sent, device));
    }

    public void positionReceived(String device, double x, double y){
        Logger.debug(this, _applicationContext.getString(R.string.position_received, device, x, y));

        App.instance().getTeam().updateUser(device.hashCode(), new Position(x,y));
    }
}
