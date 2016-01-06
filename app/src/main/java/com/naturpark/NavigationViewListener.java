package com.naturpark;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

/**
 * Created by frenzel on 1/3/16.
 */
public class NavigationViewListener implements NavigationView.OnNavigationItemSelectedListener {

    private Activity _activity;
    private DrawerLayout _drawerLayout;

    public NavigationViewListener(Activity activity, DrawerLayout drawerLayout) {
        _activity = activity;
        _drawerLayout = drawerLayout;
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        //Checking if the item is in checked state or not, if not make it in checked state
        if (menuItem.isChecked())
            menuItem.setChecked(false);
        else menuItem.setChecked(true);

        //Closing drawer on item click
        _drawerLayout.closeDrawers();
        _activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        //Check to see which item was being clicked and perform appropriate action
        switch (menuItem.getItemId()) {
            //Replacing the main content with ContentFragment Which is our Inbox View;
            case R.id.karte:
                starMainActivity();
                return true;

            case R.id.list_route:
                startListRouteActivity();
                return true;

            case R.id.search:
                startSearchPoiActivity();
                return true;

            default:
                // fehlt noch.....
                return true;
        }
    }

    public void starMainActivity() {
        _activity.startActivity(new Intent(_activity, MainActivity.class));
    }

    public void startListRouteActivity() {
        _activity.startActivity(new Intent(_activity, RouteListActivity.class));
    }

    public void startSearchPoiActivity() {
        _activity.startActivity(new Intent(_activity, SearchActivity.class));
    }
}
