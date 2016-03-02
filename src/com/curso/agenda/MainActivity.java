package com.curso.agenda;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.curso.agenda.model.Contact;

public class MainActivity extends Activity {

	private Button add;
	private ListView list;
	
	private SimpleCursorAdapter dataAdapter;
	private ContactDAO dao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		add = (Button) findViewById(R.id.add);
		list = (ListView) findViewById(R.id.list);

		dao = new ContactDAO(this);
		
		displayListView();
		addListeners();
	}
		
	private void displayListView() {
		Cursor cursor = dao.getAll();
		dataAdapter = new SimpleCursorAdapter(this, R.layout.row, cursor,
				new String[] {ContactDAO.KEY_NAME, ContactDAO.KEY_SURNAME, ContactDAO.KEY_PHONE, ContactDAO.KEY_PHONE_TYPE, ContactDAO.KEY_IMG},
				new int[] {R.id.name, R.id.surname, R.id.phone, R.id.phoneType, R.id.img}, 0);

		list.setAdapter(dataAdapter);
	}
	
	private void addListeners() {

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View itemView, int index, long id) {
				ViewGroup parent = (ViewGroup) itemView;
				
				final String name = ((TextView) parent.getChildAt(0)).getText().toString();
				final String surname = ((TextView) parent.getChildAt(1)).getText().toString();
				
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

				// set title
				alertDialogBuilder
					.setTitle("Alert")
					.setMessage("Are u sure that you want to delete " + name + " "+ surname)
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
		
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), NewContactActivity.class);
				startActivityForResult(i, 100);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		if (requestCode == 100) {
			Contact contact = (Contact) data.getSerializableExtra("contact");
			if (dao.add(contact) > 0) {
				dataAdapter.changeCursor(dao.getAll()); // refresh data
			} else {
				Toast.makeText(getApplicationContext(), "Error on saving contact", Toast.LENGTH_LONG).show();
			}
		}
		
	}

}
