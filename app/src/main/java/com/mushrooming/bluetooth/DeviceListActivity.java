package com.mushrooming.bluetooth;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.antonl.mushrooming.R;
import com.mushrooming.base.Logger;

/*
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListActivity extends AppCompatActivity {

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter _btAdapter;

    // Newly discovered devices
    private ArrayAdapter<String> _devicesArrayAdapter;

    // Button for scanning new devices
    private Button scanButton;

    // Progress bar indicating device discovery
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        setContentView(R.layout.activity_device_list);

        setSupportActionBar( (Toolbar) findViewById(R.id.device_list_toolbar) );
        progress = findViewById(R.id.device_list_progress_bar);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
        scanButton = findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        // Initialize array adapter for discovered devices
        _devicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);

        // Find and set up the ListView for discovered devices
        ListView devicesListView = findViewById(R.id.new_devices);
        devicesListView.setAdapter(_devicesArrayAdapter);
        devicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        _btAdapter = BluetoothAdapter.getDefaultAdapter();

        // Start discovering devices
        doDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (_btAdapter != null) {
            _btAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    // Start device discover with the BluetoothAdapter
    private void doDiscovery() {
        Logger.debug(this, "doDiscovery()");

        // Indicate scanning in the title
        setTitle(R.string.scanning);
        progress.setVisibility(View.VISIBLE);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (_btAdapter.isDiscovering()) {
            _btAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        _btAdapter.startDiscovery();
    }


    // The on-click listener for all devices in the ListViews
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            _btAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    // The BroadcastReceiver that listens for discovered devices and changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceEntry = device.getName() + "\n" + device.getAddress();

                // check if entry already exists
                boolean exists = false;
                for(int i = 0; i < _devicesArrayAdapter.getCount(); ++i ){
                    if (_devicesArrayAdapter.getItem(i).equals(deviceEntry)) {
                        exists = true;
                        break;
                    }
                }

                // If there is no such entry in array adapter, add it
                if( !exists ) {
                    _devicesArrayAdapter.add(deviceEntry);
                }

            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // When discovery is finished, change the Activity title
                setTitle(R.string.select_device);
                progress.setVisibility(View.GONE);
                scanButton.setVisibility(View.VISIBLE);

                if (_devicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    _devicesArrayAdapter.add(noDevices);
                }
            }
        }
    };

}
