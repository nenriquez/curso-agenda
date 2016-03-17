package com.curso.agenda.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.curso.agenda.model.Contact;
import com.example.nico.myapplication.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateContactActivity extends AppCompatActivity {

    private EditText inputName;
    private EditText inputPhone;
    private EditText inputEmail;

    private Button save;
    private Toolbar toolbar;
    private ImageView headerImage;
    private Bitmap imageBitmap;
    private FloatingActionButton takeImage;

    private String imagesThumsPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        inputName = (EditText) findViewById(R.id.input_name);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        inputEmail = (EditText) findViewById(R.id.input_email);
        takeImage = (FloatingActionButton) findViewById(R.id.take_image);
        headerImage = (ImageView) findViewById(R.id.header);
        toolbar = (Toolbar) findViewById(R.id.create_contacts_toolbar);

        this.addListeners();
        this.displayView();
        this.imagesThumsPath = this.getOrCreateDir();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
            return true;
        }

        if (menuItem.getItemId() == R.id.save_action) {
            File file = null;
            try {
                if (imageBitmap != null) {
                    file = saveFile(imageBitmap);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            String name = inputName.getText().toString();
            String phone = inputPhone.getText().toString();
            String email = inputEmail.getText().toString();

            String path = file != null ? file.getAbsolutePath() : "";
            Contact contact = new Contact(name, phone, email, path);
            setResult(Activity.RESULT_OK, new Intent().putExtra("contact", contact));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            headerImage.setImageBitmap(imageBitmap);
        }
    }

    private void addListeners() {
        takeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1000);
                }
            }
        });
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

    private File saveFile(Bitmap bmp) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".png";

        File file = new File(this.imagesThumsPath + "/" + imageFileName);
        FileOutputStream fOut = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();

        return file;
    }

    private String getOrCreateDir() {
        File storageDir = Environment.getExternalStorageDirectory();
        String path = storageDir.getAbsolutePath() + "/conctacthums";
        File FPath = new File(path);
        if (!FPath.exists()) {
            if (!FPath.mkdir()) {
                System.out.println("***Problem creating Image folder " + path);
            }
        }
        return path;
    }

}
