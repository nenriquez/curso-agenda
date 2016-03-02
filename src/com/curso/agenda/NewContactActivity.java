package com.curso.agenda;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.curso.agenda.model.Contact;

public class NewContactActivity extends Activity {

	private Button ok;
	private EditText inputName;
	private EditText inputSurname;
	private EditText inputPhone;
	private EditText inputPhoneType;
	private ImageButton inputImage;
	private ImageView imageView;
	private Bitmap imageBitmap;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_contact);

		ok = (Button) findViewById(R.id.ok);

		inputName = (EditText) findViewById(R.id.inputName);
		inputSurname = (EditText) findViewById(R.id.inputSurname);
		inputPhone = (EditText) findViewById(R.id.inputPhone);
		inputPhoneType = (EditText) findViewById(R.id.inputPhoneType);
		inputImage = (ImageButton) findViewById(R.id.inputImage);
		imageView = (ImageView) findViewById(R.id.imageView);

		this.addListeners();
	}


	private void addListeners() {
		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				File file = null;
				try {
					file = saveFile(imageBitmap);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
				
				String name = inputName.getText().toString();
				String surname = inputSurname.getText().toString();
				String phone = inputPhone.getText().toString();
				String phoneType = inputPhoneType.getText().toString();

				String path = file != null ? file.getAbsolutePath():"";
				Contact contact = new Contact(name, surname, phone, phoneType, path);
				setResult(Activity.RESULT_OK, new Intent().putExtra("contact", contact));
				finish();
			}
		});
		
		inputImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			        startActivityForResult(takePictureIntent, 1000);
			    }
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == 1000 && resultCode == RESULT_OK) {
	        Bundle extras = data.getExtras();
	        imageBitmap = (Bitmap) extras.get("data");
	        imageView.setImageBitmap(imageBitmap);
	    }
	}


	private File saveFile(Bitmap bmp) throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + ".png";
	    File storageDir = Environment.getExternalStorageDirectory();
	    
	    File file = new File(storageDir.getAbsolutePath() + "/" + imageFileName);
	    Log.e("", storageDir.getAbsolutePath() + "/" + imageFileName);
        FileOutputStream fOut = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();

        return file;
	}
	
}
