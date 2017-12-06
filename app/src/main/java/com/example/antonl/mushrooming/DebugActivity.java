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
        ListView logView = findViewById(R.id.in);
        logView.setAdapter(_logArrayAdapter);

        App.instance().getDebug().attachAdapter(_logArrayAdapter);
    }

    @Override
    protected void onDestroy() {
        App.instance().getDebug().detachAdapter();
        super.onDestroy();
    }
}
