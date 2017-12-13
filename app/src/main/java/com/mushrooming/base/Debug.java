package com.mushrooming.base;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.antonl.mushrooming.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by barto on 21.11.2017.
 */

public class Debug {
    public enum LogType {
        INFO,
        WARNING,
        ERROR
    }

    public class LogPair{
        public LogType type;
        public String msg;

        public LogPair(LogType type, String msg) {
            this.type = type;
            this.msg = msg;
        }

        @Override
        public String toString() {
            return type.toString() + ": " + msg;
        }
    }

    private ArrayList<LogPair> _logs;
    private ArrayAdapter<String> _logAdapter = null;
    HashMap<LogType, Boolean> _isTypeVisible;
    public Debug() {
        _logs = new ArrayList<>();

        _isTypeVisible = new HashMap<>();
        _isTypeVisible.put(LogType.INFO, true);
        _isTypeVisible.put(LogType.WARNING, true);
        _isTypeVisible.put(LogType.ERROR, true);
    }

    public void attachAdapter(ArrayAdapter<String> adapter){
        _logAdapter = adapter;
        fillAdapter();
    }
    public void detachAdapter(){
        _logAdapter = null;
    }

    public void addLog(LogType type, String msg){
        _logs.add(new LogPair(type, msg));
        fillAdapter();
    }


    public void setVisible(LogType type, boolean isVisible){
        _isTypeVisible.put(type, isVisible);
        fillAdapter();
    }

    private void fillAdapter(){
        if(_logAdapter == null){
            return;
        }

        _logAdapter.clear();
        for(LogPair p : _logs){
            if(_isTypeVisible.get(p.type) == true) {
                _logAdapter.add(p.toString());
            }
        }
    }

}
