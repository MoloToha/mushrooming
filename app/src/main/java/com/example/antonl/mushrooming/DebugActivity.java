package com.example.antonl.mushrooming;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mushrooming.base.App;

public class DebugActivity extends AppCompatActivity {

    private ArrayAdapter<String> _logArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        _logArrayAdapter = new ArrayAdapter<>(this, R.layout.message);
        ListView mLogView = findViewById(R.id.in);
        mLogView.setAdapter(_logArrayAdapter);

        _logArrayAdapter.addAll(App.instance().getDebug().getLogs());
    }
}
