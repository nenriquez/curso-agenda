package com.curso.agenda.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.curso.agenda.dao.ContactDAO;
import com.curso.agenda.model.Contact;
import com.example.nico.myapplication.R;

import java.io.File;

public class FragmentContacts extends Fragment {

    public static final String TAG = "fragment_contacts";

    private ListView list;
    private SimpleCursorAdapter dataAdapter;
    private ContactDAO dao;

    public static Fragment newInstance(Context context) {
        FragmentContacts f = new FragmentContacts();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_contacts, null);

        dao = new ContactDAO(getContext());
        list = (ListView) root.findViewById(R.id.list);

        this.displayView();
        this.addListeners();

        return root;
    }

    private void displayView() {
        Cursor cursor = dao.getAll();

        dataAdapter = new SimpleCursorAdapter(getContext(), R.layout.contact_item, cursor,
                new String[] {ContactDAO.KEY_NAME, ContactDAO.KEY_SURNAME, ContactDAO.KEY_PHONE_TYPE, ContactDAO.KEY_PHONE, ContactDAO.KEY_IMG},
                new int[] {R.id.name, R.id.surname, R.id.phone, R.id.phoneType, R.id.img}, 0);

        dataAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                if (view.getId() == R.id.img) {
                    ImageView img = (ImageView) view;
                    File file = new File(cursor.getString(columnIndex));
                    if (file.exists()) {
                        img.setImageURI(Uri.fromFile(file));
                    }
                    return true; //true because the data was bound to the view
                }
                return false;
            }
        });

        list.setAdapter(dataAdapter);
    }

    private void addListeners() {

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View itemView, int index, long id) {
                ViewGroup parent = (ViewGroup) itemView;
                parent = (ViewGroup) parent.getChildAt(0);

                final String name = ((TextView) parent.getChildAt(1)).getText().toString();
                final String surname = ((TextView) parent.getChildAt(2)).getText().toString();


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                // set title
                alertDialogBuilder
                        .setTitle("Alert")
                        .setMessage("Are u sure that you want to delete " + name + " " + surname)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dao.delete(name, surname);
                                dataAdapter.changeCursor(dao.getAll()); // refresh data
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    public void refreshList() {
        dataAdapter.changeCursor(dao.getAll());
    }
}