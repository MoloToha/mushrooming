package com.mushrooming.base;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.antonl.mushrooming.R;

import java.util.Random;

/**
 * Created by barto on 21.11.2017.
 */

public class UI {
    private Activity _activity;
    // Array adapter for logs
    private ArrayAdapter<String> _logArrayAdapter;

    public UI(Activity activity){
        _activity = activity;


        _activity.setContentView(R.layout.activity_bluetoothtest);

        // Initialize the array adapter for logs
        _logArrayAdapter = new ArrayAdapter<>(_activity, R.layout.message);
        ListView mLogView = _activity.findViewById(R.id.in);
        mLogView.setAdapter(_logArrayAdapter);

        initializeButtons();
    }


    private void initializeButtons() {
        Button mConnectButton = _activity.findViewById(R.id.connect_button);
        Button mDiscoverableButton = _activity.findViewById(R.id.make_discoverable_button);
        Button mSendPositionButton = _activity.findViewById(R.id.send_position_button);

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
        write("Sending random position: " + x + " " + y);

        App.instance().getBluetooth().sendPosition( new Position(x,y) );
    }

    public void write(String s){
        _logArrayAdapter.add(s);
    }

}
