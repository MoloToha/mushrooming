package com.example.antonl.mushrooming;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Array adapter for logs
    private ArrayAdapter<String> mLogArrayAdapter;

    // Member object for the bluetooth services
    private BluetoothService mBluetoothService = null;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }

        ListView mLogView = (ListView) findViewById(R.id.in);
        initializeButtons();

        // Initialize the array adapter for logs
        mLogArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.message);
        mLogView.setAdapter(mLogArrayAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // BluetoothService will then be initialized during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, initialize BluetoothService
        }
        else if (mBluetoothService == null) {
            mBluetoothService = new BluetoothService(mHandler);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth services
                mBluetoothService.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null) {
            mBluetoothService.stop();
        }
    }

    private void initializeButtons() {
        Button mSecureConnectButton = (Button) findViewById(R.id.connect_secure_button);
        Button mInsecureConnectButton = (Button) findViewById(R.id.connect_insecure_button);
        Button mDiscoverableButton = (Button) findViewById(R.id.make_discoverable_button);

        // Set listeners to buttons
        mSecureConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            }
        });
        mInsecureConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            }
        });
        mDiscoverableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ensureDiscoverable();
            }
        });
    }

    //Makes this device discoverable for 300 seconds (5 minutes).
    private void ensureDiscoverable() {
        Log.d(TAG, "ensureDiscoverable()");

        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    // Establish connection with other device
    private void connectDevice(Intent data, boolean secure) {
        Log.d(TAG, "connectDevice()");

        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBluetoothService.connect(device, secure);
    }

    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            mLogArrayAdapter.add(getString(R.string.title_connected_to, mConnectedDeviceName));
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            mLogArrayAdapter.add(getString(R.string.title_connecting));
                            break;
                        case BluetoothService.STATE_LISTEN:
                            break;
                        case BluetoothService.STATE_NONE:
                            mLogArrayAdapter.add(getString(R.string.not_connected));
                            break;
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(MainActivity.this, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so initialize BluetoothService
                    mBluetoothService = new BluetoothService(mHandler);
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(MainActivity.this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    MainActivity.this.finish();
                }
        }
    }

}
