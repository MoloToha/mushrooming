package com.example.antonl.mushrooming;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mushrooming.bluetooth.BluetoothEventHandler;
import com.mushrooming.bluetooth.BluetoothModule;
import com.mushrooming.base.Position;

import java.util.Random;

public class BluetoothTest extends AppCompatActivity implements BluetoothEventHandler
{
    private static final String TAG = "MainActivity";

    // Array adapter for logs
    private ArrayAdapter<String> mLogArrayAdapter;

    // Module for managing bluetooth connections
    private BluetoothModule mBluetoothModule;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get local Bluetooth adapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }

        setContentView(R.layout.activity_bluetoothtest);

        initializeButtons();

        // Initialize the array adapter for logs
        mLogArrayAdapter = new ArrayAdapter<>(this, R.layout.message);
        ListView mLogView = findViewById(R.id.in);
        mLogView.setAdapter(mLogArrayAdapter);

        mBluetoothModule = new BluetoothModule(this, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBluetoothModule.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothModule != null) {
            mBluetoothModule.stop();
            mBluetoothModule = null;
        }
    }

    private void initializeButtons() {
        Button mConnectButton = findViewById(R.id.connect_button);
        Button mDiscoverableButton = findViewById(R.id.make_discoverable_button);
        Button mSendPositionButton = findViewById(R.id.send_position_button);

        // Set listeners to buttons
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothModule.newConnection();
            }
        });
        mDiscoverableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothModule.ensureDiscoverable();
            }
        });
        mSendPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRandomPosition();
            }
        });
    }

    private void sendRandomPosition() {
        Random gen = new Random();
        double x = gen.nextGaussian();
        double y = gen.nextGaussian();
        mLogArrayAdapter.add("Sending random position: " + x + " " + y);

        mBluetoothModule.sendPosition( new Position(x,y) );
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //This is necessary, because BluetoothModule starts activities in context of Main Activity
        //so the results will be passed to this method, but need to be handled in BluetoothModule
        mBluetoothModule.onActivityResult(requestCode,resultCode,data);
    }

    // methods that handle different bluetooth events

    public void connecting(String device){
        mLogArrayAdapter.add(getString(R.string.connecting, device));
    }

    public void connected(String device){
        mLogArrayAdapter.add(getString(R.string.connected, device));
    }

    public void connection_failed(String device){
        mLogArrayAdapter.add(getString(R.string.connection_failed, device));
    }

    public void connection_lost(String device){
        mLogArrayAdapter.add(getString(R.string.connection_lost, device));
    }

    public void position_sent(String device){
        mLogArrayAdapter.add(getString(R.string.position_sent, device));
    }

    public void position_received(String device, double x, double y){
        mLogArrayAdapter.add(getString(R.string.position_received, device, x, y));
    }

}