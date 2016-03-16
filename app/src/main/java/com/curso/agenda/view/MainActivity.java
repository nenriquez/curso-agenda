package com.curso.agenda.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.curso.agenda.dao.ContactDAO;
import com.curso.agenda.model.Contact;
import com.example.nico.myapplication.R;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private NavigationView navView;
    private ContactDAO dao;
    private DrawerLayout drawer;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.fab = (FloatingActionButton) findViewById(R.id.fab);
        this.dao = new ContactDAO(getApplicationContext());
        this.navView = (NavigationView) findViewById(R.id.nav_view);
        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);

        this.displayView();
        this.addListeners();
        this.loadConfig();

        // select default fragment
        navView.getMenu().findItem(R.id.nav_contacts).setChecked(true);
        this.selectFragmentBy(R.id.nav_contacts);
    }

    private void displayView() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void addListeners() {
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // Handle navigation view item clicks here.
                selectFragmentBy(menuItem.getItemId());
                setTitle(menuItem.getTitle());
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), NewContactActivity.class), 100);
            }
        });
    }

    private void loadConfig() {
        SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Boolean first = pref.getBoolean("firstExecution", true);

        if (first) {
            // do sometrhing ...
            editor.putBoolean("firstExecution", false);
            editor.commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    private void selectFragmentBy(int sectionId) {
        Fragment fragment = null;
        String tag = "";

        if (sectionId == R.id.nav_map) {
            fragment = FragmentMap.newInstance(getApplicationContext());
            tag = FragmentMap.TAG;
        } else if (sectionId == R.id.nav_call) {
            fragment = FragmentCall.newInstance(getApplicationContext());
            tag = FragmentCall.TAG;
        } else {
            fragment = FragmentContacts.newInstance(getApplicationContext());
            tag = FragmentContacts.TAG;
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment, tag).commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == 100) {
            Contact contact = (Contact) data.getSerializableExtra("contact");
            if (dao.add(contact) > 0) {
                // refresh data
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentContacts concatcs = (FragmentContacts) fragmentManager.findFragmentByTag(FragmentContacts.TAG);
                if (concatcs != null && concatcs.isVisible()) {
                     concatcs.refreshList();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error on saving contact", Toast.LENGTH_LONG).show();
            }
        }
    }
}
