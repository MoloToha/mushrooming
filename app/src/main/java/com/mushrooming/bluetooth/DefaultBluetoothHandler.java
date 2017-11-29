package com.mushrooming.bluetooth;

import android.app.Activity;
import android.content.Context;

import com.example.antonl.mushrooming.R;
import com.mushrooming.base.App;
import com.mushrooming.base.Position;
import com.mushrooming.base.Debug;

/**
 * Created by barto on 20.11.2017.
 */

public class DefaultBluetoothHandler implements BluetoothEventHandler {

    private Context _applicationContext;
	private Debug _ui;
    public DefaultBluetoothHandler(){
		applicationContext = App.instance().getApplicationContext();
        _ui = App.instance().getDebug();
    }
    
    public void connecting(String device){
        _ui.write(_applicationContext.getString(R.string.connecting, device));
    }

    public void connected(String device){
        _ui.write(_applicationContext.getString(R.string.connected, device));
    }

    public void connection_failed(String device){
        _ui.write(_applicationContext.getString(R.string.connection_failed, device));
    }

    public void connection_lost(String device){
        _ui.write(_applicationContext.getString(R.string.connection_lost, device));
    }

    public void position_sent(String device){
        _ui.write(_applicationContext.getString(R.string.position_sent, device));
    }

    public void position_received(String device, double x, double y){
        _ui.write(_applicationContext.getString(R.string.position_received, device, x, y));

        App.instance().getTeam().updateUser(device.hashCode(), new Position(x,y));
    }
}
