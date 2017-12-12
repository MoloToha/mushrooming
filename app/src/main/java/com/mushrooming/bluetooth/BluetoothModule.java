package com.mushrooming.bluetooth;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.antonl.mushrooming.R;
import com.mushrooming.base.App;
import com.mushrooming.base.Position;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public class BluetoothModule{

    private static final String TAG = "BluetoothModule";

    // Local Bluetooth adapter
    private BluetoothAdapter _bluetoothAdapter = null;

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT_DEVICE = 2;

    // Member object for the bluetooth services
    private BluetoothService _bluetoothService = null;

    private final Handler _handler;
    private final Activity _activity;

    public BluetoothModule(Activity activity, BluetoothEventHandler handler){
        _activity = activity;
        _handler = new MyHandler<>(handler);
        _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void start() {
        // If BT is not on, request that it be enabled.
        // BluetoothService will then be initialized during onActivityResult
        if (!_bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            _activity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, initialize BluetoothService
        }
        else if (_bluetoothService == null) {
            _bluetoothService = new BluetoothService(_handler);
            _bluetoothService.start();
            App.instance().startSending();
        }
    }

    public void stop() {
        if (_bluetoothService != null) {
            _bluetoothService.stop();
        }
    }

    //Makes this device discoverable for 300 seconds (5 minutes).
    public void ensureDiscoverable() {
        Log.d(TAG, "ensureDiscoverable()");

        if (_bluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

            Context appContext = App.instance().getApplicationContext();
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            discoverableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            appContext.startActivity(discoverableIntent);
        }
    }

    public void newConnection() {
        /* Old version
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = _activity.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += _activity.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                _activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }*/

        int permissionCheck = ContextCompat.checkSelfPermission(App.instance().getApplicationContext(),
                                                               "Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += ContextCompat.checkSelfPermission(App.instance().getApplicationContext(),
                                                            "Manifest.permission.ACCESS_COARSE_LOCATION");

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(_activity,
                                              new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                              Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        Intent serverIntent = new Intent(_activity, DeviceListActivity.class);
        _activity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    // Establish connection with other device
    private void connectDevice(Intent data) {
        Log.d(TAG, "connectDevice()");

        // Get the device MAC address

        String address = null;
        Bundle extras = data.getExtras();
        if( extras != null ) {
            address = extras.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        }

        // Get the BluetoothDevice object
        BluetoothDevice device = _bluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        _bluetoothService.connect(device);
    }

    public void sendPosition(Position pos) {
        byte[] buffer = new byte[16];
        ByteBuffer.wrap(buffer,0,8).putDouble(pos.getX());
        ByteBuffer.wrap(buffer,8,8).putDouble(pos.getY());
        _bluetoothService.writeAll(buffer);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so initialize BluetoothService
                    _bluetoothService = new BluetoothService(_handler);
                    _bluetoothService.start();
                    App.instance().startSending();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(_activity, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    _activity.finish();
                }
        }
    }

    // The Handler that gets information back from the BluetoothService
    public static class MyHandler<T extends BluetoothEventHandler> extends Handler {

        private final WeakReference<T> mClassReference;

        MyHandler( T a ){
            mClassReference = new WeakReference<>(a);
        }

        @Override
        public void handleMessage(Message msg) {
            T a = mClassReference.get();
            if ( a != null ) {
                switch (msg.what) {
                    case BluetoothService.MESSAGE_CONNECTING:
                        a.connecting((String) msg.obj);
                        break;
                    case BluetoothService.MESSAGE_CONNECTED:
                        a.connected((String) msg.obj);
                        break;
                    case BluetoothService.MESSAGE_CONNECTION_FAILED:
                        a.connection_failed((String) msg.obj);
                        break;
                    case BluetoothService.MESSAGE_CONNECTION_LOST:
                        a.connection_lost((String) msg.obj);
                        break;
                    case BluetoothService.MESSAGE_WRITE:
                        a.position_sent((String) msg.obj);
                        break;
                    case BluetoothService.MESSAGE_READ:
                        Bundle b = (Bundle) msg.obj;
                        String device_name = b.getString(BluetoothService.KEY_DEVICE_NAME);
                        byte[] buffer = b.getByteArray(BluetoothService.KEY_BUFFER);
                        double x = ByteBuffer.wrap(buffer, 0, 8).getDouble();
                        double y = ByteBuffer.wrap(buffer, 8, 8).getDouble();

                        a.position_received(device_name, x, y);
                        break;
                }
            }
        }
    }
}
