package com.mushrooming.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.antonl.mushrooming.R;

/**
 * Created by barto on 03.01.2018.
 */

public class DataManager {

    private static  SharedPreferences getPref(Activity activity){
        return activity.getSharedPreferences(activity.getString(R.string.data_location), Context.MODE_PRIVATE);
    }
    public static String getMyName(Activity activity){
        SharedPreferences pref = getPref(activity);
        return pref.getString(activity.getString(R.string.my_name), activity.getString(R.string.default_name));
    }

    public static void setMyName(Activity activity, String newName){
        SharedPreferences.Editor editor = getPref(activity).edit();
        editor.putString(activity.getString(R.string.my_name), newName);
        editor.commit();
    }
}
