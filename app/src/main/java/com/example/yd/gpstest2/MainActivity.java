package com.example.yd.gpstest2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements LocationListener {

        Button btnGPSShowLocation;
        Button btnNWShowLocation;

    private static final long MIN_DISTANCE_FOR_UPDATE = 0; //10;
    private static final long MIN_TIME_FOR_UPDATE = 0; //1000 * 60 * 2;
    private static final boolean IS_SERVICE = false;

    ApplicationService appLocationService;
    private static final int INITIAL_REQUEST=1337;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION },
                        INITIAL_REQUEST);

                Toast.makeText(this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();

                //this.recreate(); //restart activity
                /*Intent intent = getIntent();
                finish();
                startActivity(intent);
                */

            }else{
                Toast.makeText(this, "LOCATION ACCESS enabled.", Toast.LENGTH_LONG).show();
            }

            if(IS_SERVICE) {
                appLocationService = new ApplicationService(MainActivity.this);
            }

            btnGPSShowLocation = (Button) findViewById(R.id.btnGPSShowLocation);
            btnGPSShowLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    Location gpsLocation = null;
                    if(IS_SERVICE){
                        gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
                    }else {
                        gpsLocation = returnLocationService(LocationManager.GPS_PROVIDER);
                    }

                    if (gpsLocation != null) {
                        double latitude = gpsLocation.getLatitude();
                        double longitude = gpsLocation.getLongitude();
                        Toast.makeText(
                                getApplicationContext(),
                                "Mobile Location (GPS): \nLatitude: " + latitude
                                        + "\nLongitude: " + longitude,
                                Toast.LENGTH_LONG).show();
                    } else {
                        showSettingsAlert("GPS :(");
                    }

                }
            });

            btnNWShowLocation = (Button) findViewById(R.id.btnNWShowLocation);
            btnNWShowLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    Location nwLocation = null;
                    if(IS_SERVICE){
                        nwLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
                    }else{
                        nwLocation = returnLocationService(LocationManager.NETWORK_PROVIDER);
                    }

                    if (nwLocation != null) {
                        double latitude = nwLocation.getLatitude();
                        double longitude = nwLocation.getLongitude();
                        Toast.makeText(
                                getApplicationContext(),
                                "Mobile Location (NW): \nLatitude: " + latitude
                                        + "\nLongitude: " + longitude,
                                Toast.LENGTH_LONG).show();

                    } else {
                        showSettingsAlert("NETWORK :(");
                    }

                }
            });

        }

        public Location returnLocationService(String provider) {

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location location;

            if (locationManager.isProviderEnabled(provider)) {

                locationManager.requestLocationUpdates(provider,MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);

                //always not null
                //if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(provider);
                    return location;
                //}
            }
            return null;
        }

        public void showSettingsAlert(String provider) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    MainActivity.this);

            alertDialog.setTitle(provider + " SETTINGS");

            alertDialog.setMessage(provider
                    + " is not enabled! Want to go to settings menu?");

            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            MainActivity.this.startActivity(intent);
                        }
                    });

            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
