package com.example.antonl.mushrooming;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mushrooming.base.App;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.instance().init();
    }
}