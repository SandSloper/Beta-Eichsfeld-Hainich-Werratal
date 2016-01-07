package com.naturpark;

import android.content.Intent;
import android.os.Bundle;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.util.GeoPoint;

import static com.naturpark.R.drawable.poi_marker;

/**
 * Created by Loren on 07.01.2016.
 */
public class ZoomToActivity extends MainActivity {

    double poi_lat;
    double poi_lon;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent it = getIntent();

        Bundle b = it.getExtras();

        poi_lat = Double.parseDouble(b.getString("Lat"));
        poi_lon = Double.parseDouble(b.getString("Lon"));
        name = b.getString("Name");

        super.map.getController().setZoom(18);
        super.map.getController().setCenter(new GeoPoint(poi_lat, poi_lon));
    }

}
