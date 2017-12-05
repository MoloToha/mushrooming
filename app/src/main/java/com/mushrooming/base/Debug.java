package com.mushrooming.base;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.antonl.mushrooming.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by barto on 21.11.2017.
 */

public class Debug {
    private Activity _activity;
    private ArrayList<String> _logArrayAdapter;

    public Debug() {
        _logArrayAdapter = new ArrayList<>();//new ArrayAdapter<>(_activity, R.layout.message);
        //ListView mLogView = _activity.findViewById(R.id.in);
        //mLogView.setAdapter(_logArrayAdapter);

        //initializeButtons();
    }

    public ArrayList<String> getLogs(){
        return _logArrayAdapter;
    }

    public void write(String s){
        _logArrayAdapter.add(s);
    }

}
