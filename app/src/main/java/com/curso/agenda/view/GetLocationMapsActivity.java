package com.curso.agenda.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nico.curso.agenda.R;

public class GetLocationMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng lastKnowLoc;
    private String contactName;
    private Marker marker;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar = (Toolbar) findViewById(R.id.get_location_toolbar);

        Bundle extras = getIntent().getExtras();
        lastKnowLoc = extras.getParcelable(CreateContactActivity.Constants.LAST_KNOW_LOCATION);
        if (lastKnowLoc == null) {
            lastKnowLoc = new LatLng(-34.5875, -58.672);
        }
        contactName = extras.getParcelable(CreateContactActivity.Constants.CONTACT_NAME);
        this.displayView();
    }


    private void displayView() {
        setSupportActionBar(toolbar);

        ActionBar t = getSupportActionBar();

        if (t != null) {
            t.setDisplayHomeAsUpEnabled(true);
            t.setDisplayShowTitleEnabled(false);
            t.setDisplayShowCustomEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_contact, menu);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        changeLoc(lastKnowLoc);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastKnowLoc, 12.0f));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                changeLoc(latLng);
            }
        });
    }

    private void changeLoc(LatLng latLng) {
        lastKnowLoc = latLng;
        if (marker != null) {
            marker.remove();
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLng(lastKnowLoc));
        marker = mMap.addMarker(new MarkerOptions().position(lastKnowLoc).title(contactName));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
            return true;
        }

        if (menuItem.getItemId() == R.id.save_action) {
            Intent i = new Intent();
            i.putExtra(CreateContactActivity.Constants.RESULT_DATA_KEY, lastKnowLoc);
            setResult(Activity.RESULT_OK, i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

}
