package com.curso.agenda.view;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.curso.agenda.dao.ContactDAO;
import com.curso.agenda.model.Contact;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nico.curso.agenda.R;

import java.util.Iterator;
import java.util.List;

public class FragmentMap extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "fragment_map";

    private GoogleMap mMap;
    private Toolbar toolbar;
    private ContactDAO dao;


    public static Fragment newInstance(Context context) {
        FragmentMap f = new FragmentMap();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_map, null);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.contacts_map);
        mapFragment.getMapAsync(this);
        dao = new ContactDAO(getContext());

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Iterator<Contact> it = dao.getAllContacts().iterator();
        while (it.hasNext()) {
            Contact c = it.next();
            LatLng l = new LatLng(c.getLocX(), c.getLocY());
            mMap.addMarker(new MarkerOptions().position(l).title(c.getName()));
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-34.5875, -58.672), 12.0f));

    }

}