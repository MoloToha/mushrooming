package com.mushrooming.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.antonl.mushrooming.R;
import com.mushrooming.base.Logger;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button button = (Button) findViewById(R.id.button_set_name);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setName();
            }
        });
    }

    private void setName() {
        EditText inputName = (EditText) findViewById(R.id.input_user_name);
        String name = inputName.getText().toString();
        Logger.info(this, "Name: " + name);
    }
}
