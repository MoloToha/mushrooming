package com.example.antonl.mushrooming;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class DisplayUserDataActivity extends AppCompatActivity {

    private int _userId;
    private double _xPos;
    private double _yPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_data);

        Bundle bundle = getIntent().getExtras();

        _userId = bundle.getInt("ID");
        _xPos = bundle.getDouble("PosX");
        _yPos = bundle.getDouble("PosY");

        TextView userText = (TextView) findViewById(R.id.userTextData);
        userText.setText(getFormattedUserText(_userId, _xPos, _yPos));
    }

    private String getFormattedUserText(int id, double xPos, double yPos) {
        return "User ID: " + id + "\nPosition X: " + String.format("%.3f", xPos)+ "\nPosition Y: " + String.format("%.3f", yPos);
    }
}