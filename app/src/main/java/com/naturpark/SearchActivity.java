package com.naturpark;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Loren on 30.12.2015.
 */

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private DbManager mDbManager;


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search);

         mDbManager = new DbManager(this);
         mDbManager.open();
         final EditText SEdit = (EditText)findViewById(R.id.suchtext);
         final TextView tvResults = (TextView) findViewById(R.id.tvResults);
         ImageButton SButton = (ImageButton)findViewById(R.id.SearchButton);
         SButton.setOnClickListener(new View.OnClickListener(){;

            public void onClick(View v) {
                LinkedList<String> results = mDbManager.search(SEdit.getText().toString());

                if (results.isEmpty()) {
                    tvResults.setText("No results found");
                } else {
                    Iterator<String> i = results.iterator();
                    tvResults.setText("");
                    while (i.hasNext()) {
                        tvResults.setText(tvResults.getText() + i.next() + "\n");
                    }
                }
            }
            });


        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer);


        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationViewListener(this, drawerLayout));

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

    public void starMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
    public void startListRouteActivity() {
        startActivity(new Intent(this, RouteListActivity.class));
    }

    public void startListPoiTypeActivity() {
        startActivity(new Intent(this, PoiTypeListActivity.class));
    }
    public void startSearchPoiActivity() {
        startActivity(new Intent(this, SearchActivity.class));
    }

}



