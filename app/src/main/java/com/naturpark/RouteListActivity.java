package com.naturpark;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.naturpark.data.Route;
import com.naturpark.data.Obstacle;
import com.naturpark.data.Poi;

public class RouteListActivity extends AppCompatActivity {

    List<Route> _list_route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _list_route = new DbManager(this).queryRouteList();
        init();
    }

    public void init() {
        TableLayout table = (TableLayout) findViewById(R.id.displayLinear);
        table.removeAllViews();

        System.out.println("route size:%d\n" + _list_route.size());
        for (int i = 0; i < _list_route.size(); i++) {
            TableRow row = new TableRow(this);

            row.addView(_create_text_view("<" + _list_route.get(i).id() + ">"));
            row.addView(_create_text_view(_list_route.get(i).name()));
            row.addView(_create_text_view(_list_route.get(i).length()));
            table.addView(row, i);

        }
    }


    private TextView _create_text_view(int val) {
        TextView view = new TextView(this);
        view.setText(Integer.toString(val));

        return view;
    }

    private TextView _create_text_view(String text) {
        TextView view = new TextView(this);
        view.setText(text);

        return view;
    }
}
