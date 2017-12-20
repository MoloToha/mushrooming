package com.mushrooming.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.example.antonl.mushrooming.R;
import com.mushrooming.base.App;
import com.mushrooming.base.Debug;

public class DebugActivity extends AppCompatActivity {
    private ArrayAdapter<String> _logArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        _logArrayAdapter = new ArrayAdapter<>(this, R.layout.message);
        ListView logView = findViewById(R.id.in);
        logView.setAdapter(_logArrayAdapter);

        Debug debug = App.instance().getDebug();
        debug.attachAdapter(_logArrayAdapter);

        CheckBox box = findViewById(R.id.errorCheckBox);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                App.instance().getDebug().setVisible(Debug.LogType.ERROR, isChecked);
            }
        });
        box.setChecked(debug.getVisible(Debug.LogType.ERROR));

        box = findViewById(R.id.warningCheckBox);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                App.instance().getDebug().setVisible(Debug.LogType.WARNING, isChecked);
            }
        });
        box.setChecked(debug.getVisible(Debug.LogType.WARNING));

        box = findViewById(R.id.infoCheckBox);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                App.instance().getDebug().setVisible(Debug.LogType.INFO, isChecked);
            }
        });
        box.setChecked(debug.getVisible(Debug.LogType.INFO));

        box = findViewById(R.id.debugCheckBox);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                App.instance().getDebug().setVisible(Debug.LogType.Debug, isChecked);
            }
        });
        box.setChecked(debug.getVisible(Debug.LogType.Debug));
    }

    @Override
    protected void onDestroy() {
        App.instance().getDebug().detachAdapter();
        super.onDestroy();
    }
}
