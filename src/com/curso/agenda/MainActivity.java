package com.curso.agenda;


import java.io.File;

import com.curso.agenda.model.Contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
		
		this.displayListView();
		this.addListeners();
		this.loadConfig();
	}

	private void loadConfig() {
		SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
		Editor editor = pref.edit();
		Boolean first = pref.getBoolean("firstExecution", true);
		
		if (first) {
			// do sometrhing ...
			editor.putBoolean("firstExecution", false);
			editor.commit();
		}
	}

	private void displayListView() {
		Cursor cursor = dao.getAll();
		dataAdapter = new SimpleCursorAdapter(this, R.layout.row, cursor,
				new String[] {ContactDAO.KEY_NAME, ContactDAO.KEY_SURNAME, ContactDAO.KEY_PHONE_TYPE, ContactDAO.KEY_PHONE, ContactDAO.KEY_IMG},
				new int[] {R.id.name, R.id.surname, R.id.phone, R.id.phoneType, R.id.img}, 0);
		
		dataAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder(){
			/** Binds the Cursor column defined by the specified index to the specified view */
			public boolean setViewValue(View view, Cursor cursor, int columnIndex){

				if (view.getId() == R.id.img){
					ImageView img = (ImageView) view;
					img.setImageURI(Uri.fromFile(new File(cursor.getString(columnIndex))));
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
