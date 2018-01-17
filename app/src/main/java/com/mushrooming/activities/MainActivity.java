package com.mushrooming.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.antonl.mushrooming.BuildConfig;
import com.example.antonl.mushrooming.R;
import com.mushrooming.base.App;
import com.mushrooming.base.Logger;
import com.mushrooming.base.Position;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Random;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private static final int REQUEST_FOR_OSMDROID = 10;

    private ListView _drawerList;
    private DrawerLayout _drawerLayout;
    private ArrayAdapter<String> _optionsArrayAdapter;
    private ActionBarDrawerToggle _drawerToggle;

    // overlay
    private final ItemizedIconOverlay.OnItemGestureListener listen = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
        @Override
        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
            // some function from example invoked on tap, think what to do here
            return true;
        }
        @Override
        public boolean onItemLongPress(final int index, final OverlayItem item) {
            return false;
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get local Bluetooth adapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }


        // need to request "dangerous permissions" at runtime since android 6.0
        requestPermissionsForOsmdroid();

        Context ctx = getApplicationContext();

        configClientForOSM(ctx);
        setContentView(R.layout.activity_main); // has to be before App.instance().init because latter uses 'map' from layout

        //drawer
        _drawerList = (ListView)findViewById(R.id.navList);

        addDrawerItems();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        _drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        setupDrawer();

        App.instance().init(this);

    }

    private void addDrawerItems() {
        final String[] menuItems = { "Open team", "Connect a device", "Mark position", "Make discoverable",
                "Send random position", "Send connections", "Settings", "Open debug" };
        _optionsArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems);
        _drawerList.setAdapter(_optionsArrayAdapter);

        _drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (menuItems[position].equals("Open team")) {
                    Intent intent = new Intent(MainActivity.this, TeamActivity.class);
                    startActivity(intent);
                }
                else if (menuItems[position].equals("Connect a device")) {
                    App.instance().getBluetooth().newConnection();
                }
                else if (menuItems[position].equals("Mark position")){
                    App.instance().testMarkPosition();

                }
                else if (menuItems[position].equals("Make discoverable")) {
                    App.instance().getBluetooth().ensureDiscoverable();
                }
                else if (menuItems[position].equals("Send random position")) {
                    sendRandomPosition();
                    //App.instance().updateMapPositions();
                    App.instance().focusMyPosition();
                }
                else if (menuItems[position].equals("Send connections")) {
                    App.instance().getBluetooth().sendConnections();
                }
                else if (menuItems[position].equals("Settings")) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
                else if (menuItems[position].equals("Open debug")) {
                    Intent intent = new Intent(MainActivity.this, DebugActivity.class);
                    startActivity(intent);
                }
            }
        });
    }


    private void setupDrawer() {
        _drawerToggle = new ActionBarDrawerToggle(this, _drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //  getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()}catch  (Exception e)
            }
        };

        _drawerToggle.setDrawerIndicatorEnabled(true);
        _drawerLayout.setDrawerListener(_drawerToggle);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Activate the navigation drawer toggle
        if (_drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        _drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        _drawerToggle.onConfigurationChanged(newConfig);
    }


    private void sendRandomPosition() {
        Random gen = new Random();
        double x = gen.nextGaussian();
        double y = gen.nextGaussian();
        Logger.debug(this, "Sending random position: " + x + " " + y);

        App.instance().getBluetooth().sendPosition( new Position(x,y) );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        App.instance().finish();
    }

    public void onResume() {
        super.onResume();
        // more if changes to configuration made
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //This is necessary, because BluetoothModule starts activities in context of Main Activity
        //so the results will be passed to this method, but need to be handled in BluetoothModule
        App.instance().getBluetooth().onActivityResult(requestCode,resultCode,data);
    }

    private void requestPermissionsForOsmdroid() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_FOR_OSMDROID);
        }
    }

    private void configClientForOSM(Context ctx) {
        // needed because of OSM ban rules or sth
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID); //ctx.getPackageName()

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
    }

}