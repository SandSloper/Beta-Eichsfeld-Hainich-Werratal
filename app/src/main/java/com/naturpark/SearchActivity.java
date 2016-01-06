package com.naturpark;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Loren on 30.12.2015.
 */

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private DbManager dbHelper;
    private SimpleCursorAdapter dataAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search);

        dbHelper = new DbManager(this);
        dbHelper.open();

        //Generate ListView from SQLite Database
        displayListView();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


        // Initializing Toolbar and setting it as the actionbar
        toolbar=(Toolbar)

                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        //Initializing NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new

                        NavigationViewListener(this,drawerLayout)

        );

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void displayListView() {


        Cursor cursor = dbHelper.fetchAllPoi();

        // The desired columns to be bound
        String[] columns = new String[]{
                DbManager.KEY_TYPE,
                DbManager.KEY_NAME,
                DbManager.KEY_KLASSIFIKATION,
                DbManager.KEY_LATITUDE,
                DbManager.KEY_LONGITUDE,
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.typ,
                R.id.name,
                R.id.klassifikation,
                R.id.lat,
                R.id.lng,
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.poi_info,
                cursor,
                columns,
                to,
                0);

        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                String poiName =
                        cursor.getString(cursor.getColumnIndexOrThrow("name"));
                Toast.makeText(getApplicationContext(),
                        poiName, Toast.LENGTH_SHORT).show();

                // Get the state's capital from this row in the database.
                String poiId =
                        cursor.getString(cursor.getColumnIndexOrThrow("_id"));

                String poiLatitude =
                        cursor.getString(cursor.getColumnIndexOrThrow("latitude"));

                String poiLongitude =
                        cursor.getString(cursor.getColumnIndexOrThrow("longitude"));

                double lat = Double.parseDouble(poiLatitude);
                double lon = Double.parseDouble(poiLongitude);
                int _id = Integer.parseInt(poiId);

                view.setBackgroundColor(Color.GRAY);

                System.out.print("Seleceted Poi ID:" + view.getId());

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("Lat", lat);
                intent.putExtra("Lon", lon);
                intent.putExtra("ID", _id);
                startActivity(intent);

            }
        });

        EditText myFilter = (EditText) findViewById(R.id.myFilter);
        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return dbHelper.fetchPoiByName(constraint.toString());
            }
        });

    }
}


