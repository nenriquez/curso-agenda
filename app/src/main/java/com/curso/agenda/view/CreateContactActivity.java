package com.curso.agenda.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.curso.agenda.model.Contact;
import com.curso.agenda.service.FetchAddressIntentService;
import com.example.nico.myapplication.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateContactActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener { //LocationListener, {

    private EditText inputName;
    private EditText inputPhone;
    private EditText inputEmail;
    private EditText inputAddress;

    private ImageView editAddress;
    private Toolbar toolbar;
    private ImageView headerImage;
    private Bitmap imageBitmap;
    private FloatingActionButton takeImage;

    private String imagesThumsPath;

    private GoogleApiClient mGoogleApiClient;

//    private LocationManager locationManager;
    private Location lastKnowLocation;
    private Boolean addressRequested = false;
    private Intent fetchAddressIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        inputName = (EditText) findViewById(R.id.input_name);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputAddress = (EditText) findViewById(R.id.input_address);
        takeImage = (FloatingActionButton) findViewById(R.id.take_image);
        headerImage = (ImageView) findViewById(R.id.header);
        toolbar = (Toolbar) findViewById(R.id.create_contacts_toolbar);
        editAddress = (ImageView) findViewById(R.id.set_address);

        registerForContextMenu(editAddress);

        this.addListeners();
        this.displayView();
        this.buildGoogleApiClient();
        // create directory for images
        this.imagesThumsPath = this.getOrCreateDir();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.address_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.use_loc:
                Toast.makeText(getApplicationContext(), "Looking for address begin", Toast.LENGTH_LONG).show();

                if (mGoogleApiClient.isConnected() && lastKnowLocation != null) {
                    startLookingUpAddresses();
                }

                addressRequested = true;

                return true;
            case R.id.use_map:
                Toast.makeText(getApplicationContext(), "2 newognei", Toast.LENGTH_LONG);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void startLookingUpAddresses() {
        if (fetchAddressIntent == null) {
            fetchAddressIntent = new Intent(this, FetchAddressIntentService.class);
            fetchAddressIntent.putExtra(FetchAddressIntentService.Constants.RECEIVER, new AddressResultReceiver());
            fetchAddressIntent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, lastKnowLocation);
        }
        startService(fetchAddressIntent);
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
//            close();
            return true;
        }

        if (menuItem.getItemId() == R.id.save_action) {
            File file = null;
            try {
                if (imageBitmap != null) {
                    file = saveFile(imageBitmap);
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            String name = inputName.getText().toString();
            String phone = inputPhone.getText().toString();
            String email = inputEmail.getText().toString();

            String path = file != null ? file.getAbsolutePath() : "";
            Contact contact = new Contact(name, phone, email, path);
            setResult(Activity.RESULT_OK, new Intent().putExtra("contact", contact));
            finish();
            //            close();
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

        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.showContextMenu();
            }
        });
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Gets the best and most recent location currently available,
        // which may be null in rare cases when a location is not available.
        lastKnowLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (lastKnowLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            }

            if (addressRequested) {
                startLookingUpAddresses();
            }
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(fetchAddressIntent);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
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
                Log.e(this.getClass().toString(), "Problem creating Image folder " + path);
            }
        }
        return path;
    }

//    private void initLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//        }
//    }

//    private void close() {
//        // Remove the listener you previously added
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationManager.removeUpdates(this);
//        }
//        finish();
//    }

    class UpdateUI implements Runnable
    {
        String updateString;

        public UpdateUI(String updateString) {
            this.updateString = updateString;
        }
        public void run() {
            inputAddress.setText(updateString);
        }
    }


    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver() {
            super(null);
        }

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }


        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String address = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);
            runOnUiThread(new UpdateUI(address));

            // Show a toast message if an address was found.
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
                Toast.makeText(getApplicationContext(), "Address found " + address, Toast.LENGTH_LONG);
            }

            if (resultCode == FetchAddressIntentService.Constants.FAILURE_RESULT) {
                // TODO: do shomething
            }
        }
    }

    //------------- LocationListener impl-------------------------------------------------

//    public void onLocationChanged(Location location) {
//        lastKnowLocation = location;
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationManager.removeUpdates(this);
//        }
//    }
//
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//    }
//
//    public void onProviderEnabled(String provider) {
//        if (addressRequested) {
//
//        }
//    }
//
//    public void onProviderDisabled(String provider) {
//    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(this.getClass().toString(), "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(this.getClass().toString(), "Connection suspended");
        mGoogleApiClient.connect();
    }
}
