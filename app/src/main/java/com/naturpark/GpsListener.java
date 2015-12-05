package com.naturpark; /**
 * Created by frenzel on 12/5/15.
 */

import android.support.v7.app.AppCompatActivity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;



public class GpsListener implements LocationListener {

    GpsListener(AppCompatActivity activity) {
        _activity = activity;
    }

    public void onLocationChanged(Location location) {
        String message = String.format(
                "New Location \n Longitude: %1$s \n Latitude: %2$s",
                location.getLongitude(), location.getLatitude()
        );
        Toast.makeText(_activity, message, Toast.LENGTH_LONG).show();
    }

    public void onStatusChanged(String s, int i, Bundle b) {
        Toast.makeText(_activity, "Provider status changed",
                Toast.LENGTH_LONG).show();
    }

    public void onProviderDisabled(String s) {
        Toast.makeText(_activity,
                "Provider disabled by the user. GPS turned off",
                Toast.LENGTH_LONG).show();
    }

    public void onProviderEnabled(String s) {
        Toast.makeText(_activity,
                "Provider enabled by the user. GPS turned on",
                Toast.LENGTH_LONG).show();
    }

    AppCompatActivity _activity;
}
