package com.mushrooming.activities;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.antonl.mushrooming.R;
import com.mushrooming.base.App;
import com.mushrooming.base.DataManager;
import com.mushrooming.base.Logger;

public class SettingsActivity extends AppCompatActivity {
    private EditText _input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        _input = (EditText) findViewById(R.id.input_user_name);
        _input.setText(App.instance().getMyUser().getName());

        Button button = (Button) findViewById(R.id.button_set_name);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setName();
            }
        });
    }

    private void setName() {
        String name = _input.getText().toString();
        DataManager.setMyName(this, name);
        App.instance().getMyUser().setName(name);
    }
}
