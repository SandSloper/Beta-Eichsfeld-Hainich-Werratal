package com.naturpark;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.naturpark.data.Obstacle;
import com.naturpark.data.Poi;
import com.naturpark.data.Route;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.List;

import com.naturpark.DbManager;
import com.naturpark.GpsListener;

import com.naturpark.data.PoiType;
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

                        if (tag.compareTo("trkpt") == 0) {
                            double lat = Double.parseDouble(parser.getAttributeValue(null, "lat"));
                            double lon = Double.parseDouble(parser.getAttributeValue(null, "lon"));
                            pathOverlay.addPoint(new GeoPoint(lat, lon));

                            //Log.i("", "   trackpoint= latitude=" + lat + " longitude=" + lon);

                        } else if (tag.compareTo("wpt") == 0) {
                            double lat = Double.parseDouble(parser.getAttributeValue(null, "lat"));
                            double lon = Double.parseDouble(parser.getAttributeValue(null, "lon"));
                            String description = parser.getAttributeValue(null, "description");
                            //Log.i("", "   waypoint=" + " latitude=" + lat + " longitude=" + lon + " " + description);
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

   //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private LocationManager locationManager;

    private List<Route> _list_route;
    private List<PoiType> _list_poi_type;
    private List<Poi> _list_poi;
    private List<Obstacle> _list_obstacle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapView map = (MapView) findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPQUESTOSM);
        map.getController().setZoom(14);
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
                                                                 System.out.println("xxxxxxxxxxxxxx"+ menuItem.getItemId());
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

                                                                     case R.id.list_poi_type:
                                                                         startListPoiTypeActivity();
                                                                         return true;

                                                                     default:
                                                                         // fehlt noch.....
                                                                         return true;
                                                                 }
                                                             }
                                                         }
        );

        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        DbManager dbManager = new DbManager(this, false);
        _list_route = dbManager.queryRouteList();
        _list_poi_type = dbManager.queryPoiTypeList();
        _list_poi = dbManager.queryPoiList();
        _list_obstacle = dbManager.queryObstacleList();
        System.out.println("num route:" + _list_route.size());
        System.out.println("num_poi_type:" + _list_poi_type.size());
        System.out.println("num_poi:" + _list_poi.size());
        System.out.println("num_obstacle:" + _list_obstacle.size());

        for (int i = 0; i < _list_poi.size(); ++i) {
            Poi poi = _list_poi.get(i);
            System.out.println(poi.type() + ":" + poi.location().getLatitude() + ":" + poi.location().getLongitude() + ":" + poi.name() + ":" + poi.address());
        }

        _addPoiToMap(map);
        _addObstaclesToMap(map);
        _addRoutesToMap(map);

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

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected  void onResume() {
        super.onResume();

        System.out.println("#######################################################################################");

        DbManager dbManager = new DbManager(this);
        _list_route.clear();
        _list_route = dbManager.queryRouteList();
        _list_poi_type.clear();
        _list_poi_type = dbManager.queryPoiTypeList();
        _list_poi.clear();
        _list_poi = dbManager.queryPoiList();
        _list_obstacle.clear();
        _list_obstacle = dbManager.queryObstacleList();

        MapView map = (MapView) findViewById(R.id.mapview);
        map.getOverlays().clear();
        _addPoiToMap(map);
        _addObstaclesToMap(map);
        _addRoutesToMap(map);
     }

    public void startListRouteActivity() {

        Intent intent = new Intent(this, RouteListActivity.class);
        startActivity(intent);
    }

    public void startListPoiTypeActivity() {

        Intent intent = new Intent(this, PoiTypeListActivity.class);
        startActivity(intent);
    }

    /*
    public void startListPoiActivity() {

        Intent intent = new Intent(this, PoiListActivity.class);
        startActivity(intent);
    }
    */

    private PoiType _getPoiType(int id) {
        for (PoiType poiType : _list_poi_type) {

            if (poiType.id() == id)
                return poiType;
        }

        return null;
    }

    private void _addPoiToMap(MapView map) {
        ArrayList overlayItemArray = new ArrayList<OverlayItem>();

        for (Poi poi : _list_poi) {
            OverlayItem item = new OverlayItem(poi.name(), poi.address(),
                    new GeoPoint(poi.location().getLatitude(), poi.location().getLongitude()));
            PoiType poiType = _getPoiType(poi.type());
            if (poiType != null)
                item.setMarker(getResources().getDrawable(getResources().getIdentifier(poiType.iconName(), "drawable", getPackageName())));

            if (poiType.is_visible())
                overlayItemArray.add(item);
        }

        ItemizedIconOverlay.OnItemGestureListener gestureListener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                //TextView item_info =  (TextView)findViewById(R.id.item_info_window);
                //item_info.setText(item.getTitle());
                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onItemLongPress(final int index, final OverlayItem item) {
                Toast.makeText(getApplicationContext(), item.getSnippet(), Toast.LENGTH_LONG).show();
                return true;
            }
        };

        map.getOverlays().add(new ItemizedIconOverlay<OverlayItem>(overlayItemArray, gestureListener, new ResourceProxyImpl(this)));
    }

    private void _addObstaclesToMap(MapView map) {
        ArrayList overlayItemArray = new ArrayList<OverlayItem>();

        for (Obstacle obstacle : _list_obstacle) {
            OverlayItem item = new OverlayItem(obstacle.name(), obstacle.name(),
                    new GeoPoint(obstacle.location().getLatitude(), obstacle.location().getLongitude()));
            switch (obstacle.type()) {
                case 1:
                    item.setMarker(getResources().getDrawable(R.drawable.marker_yellow));
                    break;
                case 2:
                    item.setMarker(getResources().getDrawable(R.drawable.marker_blue));
                    break;
                case 3:
                    item.setMarker(getResources().getDrawable(R.drawable.marker_red));
                    break;
                default:
                    item.setMarker(getResources().getDrawable(R.drawable.marker_default));
                    break;
            }

            overlayItemArray.add(item);
        }

        ItemizedIconOverlay.OnItemGestureListener gestureListener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onItemLongPress(final int index, final OverlayItem item) {
                Toast.makeText(getApplicationContext(), item.getSnippet(), Toast.LENGTH_LONG).show();
                return true;
            }
        };

        map.getOverlays().add(new ItemizedIconOverlay<OverlayItem>(overlayItemArray, gestureListener, new ResourceProxyImpl(this)));
    }

    private void _addRoutesToMap(MapView map) {
        for (Route route : _list_route) {
            PathOverlay path = parseGpxFile(this, "tracks/" + route.id() + ".gpx");
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
                    path.setColor(Color.RED);
            }
            map.getOverlays().add(path);
        }
    }
}
