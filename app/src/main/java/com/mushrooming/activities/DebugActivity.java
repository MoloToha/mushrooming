package com.mushrooming.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.antonl.mushrooming.R;
import com.mushrooming.base.App;
import com.mushrooming.base.Debug;

public class DebugActivity extends AppCompatActivity {
    private ArrayAdapter<String> _logArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        _logArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.message){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(R.id.message_text);

                String message = _logArrayAdapter.getItem(position);
                textView.setTextColor(getColorForMessage(message));

                return view;
            }
        };

        ListView logView = findViewById(R.id.logView);
        logView.setAdapter(_logArrayAdapter);

        App.instance().getDebug().attachAdapter(_logArrayAdapter);

        setBoxListeners();
    }

    private void setBoxListeners(){
        Debug debug = App.instance().getDebug();

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
                App.instance().getDebug().setVisible(Debug.LogType.DEBUG, isChecked);
            }
        });
        box.setChecked(debug.getVisible(Debug.LogType.DEBUG));
    }

    private int getColorForMessage(String message){
        if(message.contains(Debug.LogType.INFO.toString())){
            return Color.BLACK;
        }
        if(message.contains(Debug.LogType.WARNING.toString())){
            return Color.YELLOW;
        }
        if(message.contains(Debug.LogType.ERROR.toString())){
            return Color.RED;
        }
        return Color.GRAY;
    }

    @Override
    protected void onDestroy() {
        App.instance().getDebug().detachAdapter();
        super.onDestroy();
    }
}
