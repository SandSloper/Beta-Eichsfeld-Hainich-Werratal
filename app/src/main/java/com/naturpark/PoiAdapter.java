package com.naturpark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.naturpark.data.Poi;

import java.util.ArrayList;

/**
 * Created by Loren on 04.01.2016.
 */
public class PoiAdapter extends ArrayAdapter<Poi> {

    public PoiAdapter(Context context, ArrayList<Poi> poi) {
        super(context, 0, poi);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Poi poi = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_search, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.poiType);
        TextView tvHome = (TextView) convertView.findViewById(R.id.poiName);

        return convertView;
    }
}
