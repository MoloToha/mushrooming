package com.mushrooming.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.antonl.mushrooming.R;
import com.mushrooming.base.App;
import com.mushrooming.base.Position;

import java.util.Random;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get local Bluetooth adapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }

        App.instance().init(this);

        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.open_team);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TeamActivity.class);
                startActivity(intent);
            }
        });

        button = findViewById(R.id.open_debug);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DebugActivity.class);
                startActivity(intent);
            }
        });

        button = findViewById(R.id.open_map);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        initializeButtons();
    }


    private void initializeButtons() {
        Button mConnectButton = findViewById(R.id.connect_button);
        Button mDiscoverableButton = findViewById(R.id.make_discoverable_button);
        Button mSendPositionButton = findViewById(R.id.send_position_button);

        // Set listeners to buttons
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.instance().getBluetooth().newConnection();
            }
        });
        mDiscoverableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.instance().getBluetooth().ensureDiscoverable();
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
        App.instance().getDebug().write("Sending random position: " + x + " " + y);

        App.instance().getBluetooth().sendPosition( new Position(x,y) );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        App.instance().finish();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //This is necessary, because BluetoothModule starts activities in context of Main Activity
        //so the results will be passed to this method, but need to be handled in BluetoothModule
        App.instance().getBluetooth().onActivityResult(requestCode,resultCode,data);
    }

}