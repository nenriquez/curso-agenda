package com.curso.agenda.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nico.myapplication.R;

public class FragmentCall extends Fragment {

    public static Fragment newInstance(Context context) {
        FragmentCall f = new FragmentCall();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_call, null);
        return root;
    }

}