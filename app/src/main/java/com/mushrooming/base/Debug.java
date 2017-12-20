package com.mushrooming.base;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by barto on 21.11.2017.
 */

public class Debug {
    public enum LogType {
        INFO,
        WARNING,
        ERROR
    }

    public class Log {
        public LogType type;
        public String msg;
        public int amount;

        public Log(LogType type, String msg) {
            this.type = type;
            this.msg = msg;
            amount = 1;
        }

        @Override
        public String toString() {
            String num = "";
            if(amount > 1){
                num = amount + "x ";
            }
            return num + type.toString() + ": " + msg;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Log){
                Log logObj = (Log)obj;
                return type.equals(logObj.type) && msg.equals(logObj.msg);
            }
            return false;
        }
    }

    private ArrayList<Log> _logs;
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
        Log newLog = new Log(type, msg);

        int idx = _logs.indexOf(newLog);
        if(idx != -1){
            newLog.amount = _logs.get(idx).amount + 1;
            _logs.remove(idx);
        }

        _logs.add(newLog);
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
        for(Log l : _logs){
            if(_isTypeVisible.get(l.type) == true) {
                _logAdapter.add(l.toString());
            }
        }
    }
}
