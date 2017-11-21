package com.mushrooming.bluetooth;

import android.app.Activity;

import com.example.antonl.mushrooming.R;
import com.mushrooming.base.App;
import com.mushrooming.base.Position;
import com.mushrooming.base.UI;

/**
 * Created by barto on 20.11.2017.
 */

public class DefaultBluetoothHandler implements BluetoothEventHandler {
    
    private Activity _activity;
    private UI _ui;
    public DefaultBluetoothHandler(){
        _activity = App.instance().getActivity();
        _ui = App.instance().getUI();
    }
    
    public void connecting(String device){
        _ui.write(_activity.getString(R.string.connecting, device));
    }

    public void connected(String device){
        _ui.write(_activity.getString(R.string.connected, device));
    }

    public void connection_failed(String device){
        _ui.write(_activity.getString(R.string.connection_failed, device));
    }

    public void connection_lost(String device){
        _ui.write(_activity.getString(R.string.connection_lost, device));
    }

    public void position_sent(String device){
        _ui.write(_activity.getString(R.string.position_sent, device));
    }

    public void position_received(String device, double x, double y){
        _ui.write(_activity.getString(R.string.position_received, device, x, y));

        App.instance().getTeam().updateUser(device.hashCode(), new Position(x,y));
    }
}
