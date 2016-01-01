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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.naturpark.data.PoiType;

import java.util.List;

public class PoiTypeListActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private List<PoiType> _list_poi_type;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_type_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Button button = (Button) findViewById(R.id.button_close_poi_type_list);
        //button.setOnClickListener(this);

        _list_poi_type = new DbManager(this).queryPoiTypeList();
        init();

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()

                                                         {

                                                             // This method will trigger on item Click of navigation menu
                                                             @Override
                                                             public boolean onNavigationItemSelected(MenuItem menuItem) {
                                                                 System.out.println("xxxxxxxxxxxxxx" + menuItem.getItemId());
                                                                 //Checking if the item is in checked state or not, if not make it in checked state
                                                                 if (menuItem.isChecked())
                                                                     menuItem.setChecked(false);
                                                                 else menuItem.setChecked(true);
                                                                 //Closing drawer on item click
                                                                 drawerLayout.closeDrawers();
                                                                 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                                                                 //Check to see which item was being clicked and perform appropriate action
                                                                 switch (menuItem.getItemId()) {
                                                                     //Replacing the main content with ContentFragment Which is our Inbox View;
                                                                     case R.id.karte:
                                                                         startMainActivity();
                                                                         return true;

                                                                     case R.id.list_route:
                                                                         startListRouteActivity();
                                                                         return true;

                                                                     case R.id.list_poi_type:
                                                                         startListPoiTypeActivity();
                                                                         return true;
                                                                     case R.id.search:
                                                                         startSearchPoiActivity();
                                                                         return true;

                                                                     default:
                                                                         // fehlt noch.....
                                                                         return true;
                                                                 }
                                                             }
                                                         }
        );

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

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

    public void init() {

        TableLayout table = (TableLayout) findViewById(R.id.displayPoiTypeList);
        table.removeAllViews();

        for (PoiType poiType : _list_poi_type) {
            TableRow row = new TableRow(this);

            row.addView(_create_text_view("<" + poiType.id() + ">"));
            row.addView(_create_text_view(poiType.name()));
            row.addView(_create_check_box(poiType));
            table.addView(row);

        }
    }


    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        PoiType poiType = (PoiType)button.getTag();

        if(button.isChecked() && !poiType.is_visible()) {
            poiType.show();
            new DbManager(this).update(poiType);
        }
        else if (!button.isChecked() && poiType.is_visible()) {
            poiType.hide();
            new DbManager(this).update(poiType);
        }
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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

    private CheckBox _create_check_box(PoiType poiType) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setOnCheckedChangeListener(this);
        checkBox.setTag(poiType);
        checkBox.setChecked(poiType.is_visible());

        return checkBox;
    }

    public void startMainActivity() {
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
