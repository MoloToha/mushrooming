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
import android.widget.Toast;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.antonl.mushrooming.R;
import com.mushrooming.base.App;
import com.mushrooming.base.Logger;
import com.mushrooming.base.Position;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class BluetoothModule{

    // Local Bluetooth adapter
    private BluetoothAdapter _bluetoothAdapter = null;

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT_DEVICE = 2;

    // Constants representing bluetooth message types
    private static final int MESSAGE_POSITION = 1; // x:double, y:double
    private static final int MESSAGE_CONNECTED_DEVICES = 2; // N:int, Dev1:byte(17) ... DevN:byte(17)
    private static final int MESSAGE_NAME = 3; // N:int, name:byte(N)

    // Member object for the bluetooth services
    private BluetoothService _bluetoothService = null;

    private final Handler _handler;
    private final Activity _activity;

    public BluetoothModule(Activity activity, BluetoothEventHandler handler){
        Logger.debug(this, "CREATE BluetoothModule");

        _activity = activity;
        _handler = new MyHandler<>(handler);
        _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void start() {
        Logger.debug(this, "start()");

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
        Logger.debug(this, "stop()");

        if (_bluetoothService != null) {
            _bluetoothService.stop();
        }
    }

    //Makes this device discoverable for 300 seconds (5 minutes).
    public void ensureDiscoverable() {
        Logger.debug(this, "ensureDiscoverable()");

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
        Logger.debug(this, "newConnection()");

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

    public int getMyUserId(){
        return _bluetoothAdapter.getName().hashCode();
    }
    // Establish connection with other device
    public void connectDevice(String address) {
        Logger.debug(this, "connectDevice()");

        if( !BluetoothAdapter.checkBluetoothAddress(address) ){
            Logger.error(this, "connectDevice() - invalid mac address");
            return;
        }

        // Get the BluetoothDevice object
        BluetoothDevice device = _bluetoothAdapter.getRemoteDevice(address);

        if( device == null ){
            Logger.error(this, "connectDevice() - device is null");
            return;
        }

        // Attempt to connect to the device
        _bluetoothService.connect(device);
    }

    public void sendPosition(Position pos) {
        Logger.debug(this, "sendPosition()");

        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.putInt(BluetoothModule.MESSAGE_POSITION);
        buffer.putDouble(pos.getX());
        buffer.putDouble(pos.getY());
        _bluetoothService.writeAll(buffer.array());
    }

    public void sendConnections() {
        Logger.debug(this, "sendConnections()");

        ArrayList<String> connections = _bluetoothService.getConnections();
        ByteBuffer buffer = ByteBuffer.allocate(8 + 17 * connections.size());

        buffer.putInt(BluetoothModule.MESSAGE_CONNECTED_DEVICES);
        buffer.putInt(connections.size());

        for( String connection : connections )
            buffer.put(connection.getBytes());

        _bluetoothService.writeAll(buffer.array());
    }

    public void sendName() {
        Logger.debug(this, "sendName()");

        String name = App.instance().getMyUser().getName();
        byte[] nameb = name.getBytes(Charset.forName("UTF-8"));

        ByteBuffer buffer = ByteBuffer.allocate(8 + nameb.length);
        buffer.putInt(BluetoothModule.MESSAGE_NAME);
        buffer.putInt(nameb.length);
        buffer.put(nameb);

        _bluetoothService.writeAll(buffer.array());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    Bundle extras = data.getExtras();
                    if( extras != null ) {
                        String address = extras.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                        connectDevice(address);
                    }
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Logger.debug(this, "enabled bluetooth");
                    // Bluetooth is now enabled, so initialize BluetoothService
                    _bluetoothService = new BluetoothService(_handler);
                    _bluetoothService.start();
                    App.instance().startSending();
                }
                else {
                    // User did not enable Bluetooth or an error occurred
                    Logger.error(this, "bluetooth not enabled");
                    Toast.makeText(_activity, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    _activity.finish();
                }
        }
    }

    // The Handler that gets information back from the BluetoothService
    public static class MyHandler<T extends BluetoothEventHandler> extends Handler {

        private final WeakReference<T> mClassReference;

        MyHandler( T handler ) {
            mClassReference = new WeakReference<>(handler);
        }

        @Override
        public void handleMessage(Message msg) {
            T handler = mClassReference.get();
            if ( handler == null ){
                Logger.error(this, "Can't get reference to handler");
            }
            else {
                switch (msg.what) {
                    case BluetoothService.HANDLER_CONNECTING:
                        handler.connecting((String) msg.obj);
                        break;
                    case BluetoothService.HANDLER_CONNECTED:
                        handler.connected((String) msg.obj);
                        break;
                    case BluetoothService.HANDLER_CONNECTION_FAILED:
                        handler.connectionFailed((String) msg.obj);
                        break;
                    case BluetoothService.HANDLER_CONNECTION_LOST:
                        handler.connectionLost((String) msg.obj);
                        break;
                    case BluetoothService.HANDLER_READ:

                        Bundle b = (Bundle) msg.obj;
                        String deviceName = b.getString(BluetoothService.KEY_DEVICE_NAME);
                        ByteBuffer buffer = ByteBuffer.wrap(b.getByteArray(BluetoothService.KEY_BUFFER));

                        handleBluetoothMessage(handler, deviceName, buffer);
                }
            }
        }

        private void handleBluetoothMessage(T handler, String deviceName, ByteBuffer buffer) {
            try{
                int messageType = buffer.getInt();
                switch (messageType){
                    case BluetoothModule.MESSAGE_POSITION:
                        double x = buffer.getDouble();
                        double y = buffer.getDouble();

                        handler.positionReceived(deviceName, x, y);
                        break;
                    case BluetoothModule.MESSAGE_CONNECTED_DEVICES:
                        int nrDevices = buffer.getInt();
                        ArrayList<String> devices = new ArrayList<>();
                        for( int i = 0; i < nrDevices; i++ ){
                            byte[] address = new byte[17];
                            buffer.get(address, 0, 17);

                            String device = new String(address, "UTF-8");
                            devices.add(device);
                        }

                        handler.connectionsReceived(deviceName, devices);
                }

            } catch (BufferUnderflowException e) {
                Logger.errorWithException(this, e, "incorrect format of bluetooth message!");
            } catch (UnsupportedEncodingException e) {
                // This shouldn't really happen, but it's checked exception, so it needs te be handled
                Logger.errorWithException(this, e, "UTF-8 encoding is not supported (WHAT?)");
            }
        }
    }
}
