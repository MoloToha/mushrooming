package com.example.antonl.mushrooming;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class DisplayUserDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_data);

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