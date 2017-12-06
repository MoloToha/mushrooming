package com.example.antonl.mushrooming;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Bundle bundle = getIntent().getExtras();

        int _userId = bundle.getInt("ID");
        double _xPos = bundle.getDouble("PosX");
        double _yPos = bundle.getDouble("PosY");

        TextView userText = (TextView) findViewById(R.id.userTextData);
        userText.setText(getFormattedUserText(_userId, _xPos, _yPos));
    }

    private String getFormattedUserText(int id, double xPos, double yPos) {
        return "User ID: " + id + "\nPosition X: " + String.format("%.3f", xPos)+ "\nPosition Y: " + String.format("%.3f", yPos);
    }
}