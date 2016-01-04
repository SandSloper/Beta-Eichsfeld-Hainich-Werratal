package com.naturpark;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;

public class GPSManager {

    private Activity activity;
    private LocationManager locationManager;
    private LocationListener gpsListener;

    public GPSManager(Activity activity) {
        this.activity = activity;
    }

    public void start() {
        locationManager = (LocationManager) activity
                .getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            setUp();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    activity);
            alertDialogBuilder
                    .setMessage("GPS ist deaktiviert, um die Standortortbestimmung nutzen zu können ?")
                    .setCancelable(false)
                    .setPositiveButton("Aktiviere GPS",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    activity.startActivity(callGPSSettingIntent);
                                }
                            });
            alertDialogBuilder.setNegativeButton("Abbrechen",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();

        }
    }

    public void setUp() {
        gpsListener = new GPSTracker(activity, locationManager);
    }

}
