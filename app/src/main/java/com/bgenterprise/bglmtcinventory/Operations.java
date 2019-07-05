package com.bgenterprise.bglmtcinventory;

/**
 * The Home page of the activity where the Navigational drawer displays buttons to the various sections.
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;

public class Operations extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager session;
    HashMap<String, String> allDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operations);
        session = new SessionManager(Operations.this);
        allDetails = session.getAllDetails();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        File dir = new File(Environment.getExternalStorageDirectory().getPath(), "LMTCExports");
        try {
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Fragment beginFragment = new FragmentWelcome();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameForButtons, beginFragment);
        transaction.commit();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Operations.this, new String[]{Manifest.permission.CAMERA}, 21);

        }else if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Operations.this, new String[]{Manifest.permission.INTERNET}, 23);
        }else if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Operations.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 31);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.operations, menu);
        TextView tv_staffName, tv_staffID, tv_appVersion, tv_lastSync;
        tv_staffName = findViewById(R.id.tv_staffName);
        tv_staffID = findViewById(R.id.tv_staffID);
        tv_appVersion = findViewById(R.id.tv_appVersion);
//        tv_lastSync = findViewById(R.id.tv_lastSync);

        tv_staffName.setText("Staff Name: "+ allDetails.get(SessionManager.KEY_STAFF_NAME));
        tv_staffID.setText("Staff ID: "+ allDetails.get(SessionManager.KEY_STAFF_ID));
        tv_appVersion.setText("App Version: " + allDetails.get(SessionManager.KEY_APP_VERSION));
//        tv_lastSync.setText("Last Sync Date: 02-01-2019 05:15:30");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        // Handle navigation view item clicks here and inflates the appropriate fragments.
        int id = item.getItemId();

        if (id == R.id.itemStockManagement) {
            fragment  = new FragmentStockManagement();
        } else if (id == R.id.itemPayments) {
            fragment = new FragmentPayments();
        } else if (id == R.id.itemReports) {
            fragment = new FragmentReports();
        } else if (id == R.id.itemSyncing) {
            fragment = new FragmentSyncing();
        }

        //PUT THE FRAGMENT STUFF HERE.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameForButtons, fragment);
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String getLastSyncDate() {

        return null;
    }


}
