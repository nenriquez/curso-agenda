package com.curso.agenda.view;

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

import com.curso.agenda.dao.ContactDAO;
import com.curso.agenda.model.Contact;
import com.nico.curso.agenda.R;

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
                new String[] {ContactDAO.KEY_NAME, ContactDAO.KEY_PHONE, ContactDAO.KEY_IMG},
                new int[] {R.id.name, R.id.phone, R.id.img}, 0);

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

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                final long contactId = c.getLong(0);

                final String name = c.getString(c.getColumnIndex(ContactDAO.KEY_NAME));

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                // set title
                alertDialogBuilder
                    .setTitle("Contact Deletion")
                    .setMessage("Are u sure that you want to delete " + name)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dao.delete(contactId);
                            dataAdapter.changeCursor(dao.getAll()); // refresh data
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();

                return true;
            }

        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact c = dao.getContactBy(id);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + c.getPhone()));
                startActivity(intent);
            }
        });
    }




    public void refreshList() {
        dataAdapter.changeCursor(dao.getAll());
    }
}