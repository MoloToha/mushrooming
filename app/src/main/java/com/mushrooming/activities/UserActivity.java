package com.mushrooming.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.antonl.mushrooming.R;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Bundle bundle = getIntent().getExtras();

        String address = bundle.getString("Address");
        double xPos = bundle.getDouble("PosX");
        double yPos = bundle.getDouble("PosY");

        TextView userText = (TextView) findViewById(R.id.userTextData);
        userText.setText(getFormattedUserText(address, xPos, yPos));
    }

    private String getFormattedUserText(String address, double xPos, double yPos) {
        return "Address: " + address + "\nPosition X: " + String.format("%.3f", xPos)+ "\nPosition Y: " + String.format("%.3f", yPos);
    }
}