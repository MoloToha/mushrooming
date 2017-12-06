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
    private ArrayList<String> _logs;
    private ArrayAdapter<String> _logArrayAdapter = null;

    public Debug() {
        _logs = new ArrayList<>();
    }

    public ArrayList<String> getLogs(){
        return _logs;
    }

    public void attachAdapter(ArrayAdapter<String> adapter){
        _logArrayAdapter = adapter;
        _logArrayAdapter.addAll(_logs);
    }
    public void detachAdapter(){
        _logArrayAdapter = null;
    }

    public void write(String s){
        _logs.add(s);
        if(_logArrayAdapter != null){
            _logArrayAdapter.add(s);
        }
    }

}
