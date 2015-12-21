package com.naturpark;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.location.LocationManager;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.bonuspack.overlays.Polyline;

import org.osmdroid.views.overlay.PathOverlay;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.List;

import com.naturpark.DbManager;
import com.naturpark.GpsListener;

import com.naturpark.data.Route;
import com.naturpark.data.Obstacle;
import com.naturpark.data.Poi;

public class MainActivity extends AppCompatActivity {

    public PathOverlay parseGpxFile(Context context, String filename) {

        PathOverlay pathOverlay = new PathOverlay(Color.BLUE, context);

        try {
            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(context.getAssets().open(filename), null);

            int parserEvent = parser.getEventType();
            while (parserEvent != XmlPullParser.END_DOCUMENT) {

                switch (parserEvent) {

                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();

                        /*
                        if (tag.compareTo("number") == 0) {
                            int numberRoutePoints = Integer.parseInt(parser.getAttributeValue(null, "numpoints"));
                            int totalWaypoints = Integer.parseInt(parser.getAttributeValue(null, "numwpts"));
                            Log.i("", "   Total points = " + numberRoutePoints + " Total waypoints = " + totalWaypoints);
                        }
                        */

                        if (tag.compareTo("trkpt") == 0) {
                            double lat = Double.parseDouble(parser.getAttributeValue(null, "lat"));
                            double lon = Double.parseDouble(parser.getAttributeValue(null, "lon"));
                            pathOverlay.addPoint(new GeoPoint(lat, lon));

                            Log.i("", "   trackpoint= latitude=" + lat + " longitude=" + lon);

                        } else if (tag.compareTo("wpt") == 0) {
                            double lat = Double.parseDouble(parser.getAttributeValue(null, "lat"));
                            double lon = Double.parseDouble(parser.getAttributeValue(null, "lon"));
                            String description = parser.getAttributeValue(null, "description");
                            Log.i("", "   waypoint=" + " latitude=" + lat + " longitude=" + lon + " " + description);
                        }
                        break;
                }

                parserEvent = parser.next();
            }

        } catch (Exception e) {
            Log.i("RouteLoader", "Failed in parsing XML", e);
        }


        return pathOverlay;
    }



    public class ResourceProxyImpl extends DefaultResourceProxyImpl {

        private final Context mContext;

        public ResourceProxyImpl(final Context pContext) {
            super(pContext);
            mContext = pContext;
        }

        @Override
        public String getString(final string pResId) {
            try {
                final int res = R.string.class.getDeclaredField(pResId.name()).getInt(null);
                return mContext.getString(res);
            } catch (final Exception e) {
                return super.getString(pResId);
            }
        }

        @Override
        public Bitmap getBitmap(final bitmap pResId) {
            try {
                final int res = R.drawable.class.getDeclaredField(pResId.name()).getInt(null);
                return BitmapFactory.decodeResource(mContext.getResources(), res);
            } catch (final Exception e) {
                return super.getBitmap(pResId);
            }
        }

        @Override
        public Drawable getDrawable(final bitmap pResId) {
            try {
                final int res = R.drawable.class.getDeclaredField(pResId.name()).getInt(null);
                return mContext.getResources().getDrawable(res);
            } catch (final Exception e) {
                return super.getDrawable(pResId);
            }
        }
    }

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private LocationManager locationManager;

    private List<Route> _list_route;
    private List<Poi> _list_poi;
    private List<Obstacle> _list_obstacle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapView map = (MapView) findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPQUESTOSM);
        map.getController().setZoom(15);
        //map.getController().setCenter(new GeoPoint(51.080414, 10.434239));
        map.getController().setCenter(new GeoPoint(51.05446, 13.73636));
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setUseDataConnection(true);
        map.getProjection();

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()

                                                         {

                                                             // This method will trigger on item Click of navigation menu
                                                             @Override
                                                             public boolean onNavigationItemSelected(MenuItem menuItem) {
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
                                                                     case R.id.start:
                                                                       // fehlt noch.....
                                                                         return true;

                                                                     case R.id.list_route:
                                                                         startListRouteActivity();
                                                                         return true;

                                                                     default:
                                                                         // fehlt noch.....
                                                                         return true;
                                                                 }
                                                             }
                                                         }
        );

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new GpsListener(this));

        DbManager dbManager = new DbManager(this);
        _list_route = dbManager.queryRouteList();
        _list_poi = dbManager.queryPoiList();
        _list_obstacle = dbManager.queryObstacleList();
        System.out.println("num route:" + _list_route.size());
        System.out.println("num_poi:" + _list_poi.size());
        System.out.println("num_obstacle:" + _list_obstacle.size());
        for (int i = 0; i < _list_poi.size(); ++i)
        {
            Poi poi = _list_poi.get(i);
            System.out.println(poi.type() +":"+poi.location().getLatitude() +":"+ poi.location().getLongitude() +":" + poi.name() +":"+ poi.address());
        }

        ArrayList overlayItemArray = new ArrayList<OverlayItem>();
        for (int i = 0; i < _list_poi.size(); ++i) {
            OverlayItem item = new OverlayItem(_list_poi.get(i).name(), _list_poi.get(i).address(),
                    new GeoPoint(_list_poi.get(i).location().getLatitude(), _list_poi.get(i).location().getLongitude()));
            switch (_list_poi.get(i).type()) {
                case 1:
                    item.setMarker(getResources().getDrawable(R.drawable.marker_default));
                    break;
                case 2:
                    item.setMarker(getResources().getDrawable(R.drawable.ic_five));
                    break;
            }

            overlayItemArray.add(item);
        }

        ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(overlayItemArray, null, new ResourceProxyImpl(this));

        // Add the overlay to the MapView
        map.getOverlays().add(itemizedIconOverlay);

        for (Route route : _list_route) {
            PathOverlay path = parseGpxFile(this, "tracks/"+route.id()+".gpx");
            switch (route.classification()) {
                case 1:
                    path.setColor(Color.RED);
                    break;

                case 2:
                    path.setColor(Color.YELLOW);
                    break;

                case 3:
                    path.setColor(Color.GREEN);
                    break;

                default:
                    path.setColor(Color.GRAY);
            }
            map.getOverlays().add(path);
        }

        map.postInvalidate();


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

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    public void startListRouteActivity() {

        Intent intent = new Intent(this, RouteListActivity.class);
        startActivity(intent);
    }

    /*
    public void startListPoiActivity() {

        Intent intent = new Intent(this, PoiListActivity.class);
        startActivity(intent);
    }
    */
}
