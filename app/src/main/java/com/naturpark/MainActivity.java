package com.naturpark;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.naturpark.data.Obstacle;
import com.naturpark.data.Poi;
import com.naturpark.data.PoiType;
import com.naturpark.data.Route;

import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.naturpark.R.drawable.location;

public class MainActivity extends AppCompatActivity implements MapListener, View.OnLayoutChangeListener,MapEventsReceiver {

    public PathOverlay parseGpxFile(Route route) {

        PathOverlay pathOverlay = new PathOverlay(Color.BLUE, this);

        ArrayList<GeoPoint> track = route.getTrack(this);
        for (GeoPoint point : track) {
            pathOverlay.addPoint(point);
        }

        return pathOverlay;
    }

    @Override
    public boolean onScroll(ScrollEvent scrollEvent) {
        System.out.println("SCROOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOL");
        return false;
    }

    @Override
    public boolean onZoom(ZoomEvent zoomEvent) {
        System.out.println("ZOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOM");
        return false;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

        if (_route_id != 0 && map.getWidth() != 0) {
            map.zoomToBoundingBox(_get_route(_route_id).boundingBox(this));
            _route_id = 0;
        }

        System.out.println("_________________________________________________"+ _poi_id +" : "+ _list_poi.size());
        System.out.println("\n\n\n");
        Poi poi = _get_poi(_poi_id);
        if (poi != null && map.getWidth() != 0) {
            System.out.println("_________________________________________________"+ _poi_id);
            System.out.println("\n\n\n");

            map.getController().setZoom(map.getMaxZoomLevel() - 1);
            map.getController().setCenter(new GeoPoint(poi.location()));
            _poi_id = 0;
        }
    }

    // GPSTracker class
    GPSTracker gps;

    private boolean _creating;
    private SharedPreferences _preferences;

    private Toolbar toolbar;
    protected MapView map;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

     private List<Route> _list_route;
    private List<PoiType> _list_poi_type;
    private List<Poi> _list_poi;
    private List<Obstacle> _list_obstacle;

    // selected route or POI
    int _route_id;
    int _poi_id;

    // filter variables
    private List<Integer> _filtered_poi_types= new ArrayList<Integer>();
    private int _classification = 0;

    private DbManager dbHelper;
    private int type;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("####################################################################################### Main::onCreate");

        _creating = true;
        _preferences = getSharedPreferences("naturpark.prf", MODE_PRIVATE);

        GPSManager gps = new GPSManager(
                MainActivity.this);
        gps.start();

        dbHelper = new DbManager(this);

        setContentView(R.layout.activity_main);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPQUESTOSM);

        map.getController().setZoom(_preferences.getInt("ZoomLevel", 10));
        map.getController().setCenter(new GeoPoint(_preferences.getFloat("Latitude", (float) 51.080414), _preferences.getFloat("Longitude", (float) 10.434239)));

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setUseDataConnection(true);
        map.addOnLayoutChangeListener(this);

        _route_id = getIntent().getIntExtra("Route", 0);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!:route:" + _route_id);

        _list_route = dbHelper.queryRouteList();
        _list_poi_type = dbHelper.queryPoiTypeList();
        _list_poi = dbHelper.queryPoiList();
        _list_obstacle = dbHelper.queryObstacleList();
        System.out.println("num route:" + _list_route.size());
        System.out.println("num_poi_type:" + _list_poi_type.size());
        System.out.println("num_poi:" + _list_poi.size());
        System.out.println("num_obstacle:" + _list_obstacle.size());

        _addPoiToMap(map);
        _addObstaclesToMap(map);
        _addRoutesToMap(map);

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        map.getOverlays().add(0, mapEventsOverlay);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
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

    //Handling Map events
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {

        return false;
    }

    // TODO: must be fixed, is not working yet
    @Override
    public boolean longPressHelper(GeoPoint p) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Neues Hindernis");
        builder.setIcon(R.drawable.marker_stufe);

        final double latitude = p.getLatitude();
        final double longitude = p.getLongitude();

        final float lat_f = (float) latitude;
        final float lon_f = (float) longitude;

        String obstacle_types[] = new String[]{"Bitte wählen","Schranke", "Treppe", "Engstelle", "Stufe", "Rinne", "Poller", "Abhang"};
        final Spinner obstacle_type_spinner = new Spinner(this);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1, obstacle_types);
        obstacle_type_spinner.setAdapter(adapter);

        builder.setView(obstacle_type_spinner);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (obstacle_type_spinner.getSelectedItem().toString() == "Schranke"){
                    type = 1;
                    name = "Schranke";
                    System.out.println("--------------------------------------------------------"+type+name);
                }
                if (obstacle_type_spinner.getSelectedItem().toString() == "Treppe"){
                    type = 2;
                    name = "Treppe";
                }
                if (obstacle_type_spinner.getSelectedItem().toString() == "Engstelle"){
                    type = 3;
                    name = "Engstelle";
                }
                if (obstacle_type_spinner.getSelectedItem().toString() == "Stufe"){
                    type = 4;
                    name = "Stufe";
                }
                if (obstacle_type_spinner.getSelectedItem().toString() == "Rinne"){
                    type = 5;
                    name = "Rinne";
                }
                if (obstacle_type_spinner.getSelectedItem().toString() == "Poller"){
                    type = 6;
                    name = "Poller";
                }
                if (obstacle_type_spinner.getSelectedItem().toString() == "Abhang"){
                    type = 7;
                    name = "Abhang";
                }
                dbHelper.insertObstacle(type, lat_f, lon_f, name);
                Log.d("Insert: ", "Inserting ..");
                dbHelper.close();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
        return true;
    }

    @Override
    protected  void onStart() {
        super.onStart();

        System.out.println("####################################################################################### onStart");
    }

    @Override
    protected  void onResume() {
        super.onResume();

        System.out.println("####################################################################################### Main::onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("####################################################################################### Main::onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("####################################################################################### Main::onStop");

        SharedPreferences.Editor editor = _preferences.edit();
        editor.putFloat("Latitude", (float) map.getMapCenter().getLatitude());
        editor.putFloat("Longitude", (float)map.getMapCenter().getLongitude());
        editor.putInt("ZoomLevel", map.getZoomLevel());
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("####################################################################################### Main::onDestroy");
    }

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
            OverlayItem item = new OverlayItem(poi.name(), poi.info(),
                    new GeoPoint(poi.location().getLatitude(), poi.location().getLongitude()));
            PoiType poiType = _getPoiType(poi.type());
            if (poiType != null) {
                item.setMarker(getResources().getDrawable(getResources().getIdentifier(poiType.iconName(), "drawable", getPackageName())));

                if (_filtered_poi_types.contains(new Integer(poiType.id())))
                    overlayItemArray.add(item);
            }
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

        map.getOverlays().add(new ItemizedIconOverlay<OverlayItem>(overlayItemArray, gestureListener, new CustomResourceProxy(this)));
    }

    private void _addObstaclesToMap(MapView map) {
        ArrayList overlayItemArray = new ArrayList<OverlayItem>();

        for (Obstacle obstacle : _list_obstacle) {
            OverlayItem item = new OverlayItem(obstacle.name(), obstacle.name(),
                    new GeoPoint(obstacle.location().getLatitude(), obstacle.location().getLongitude()));
            switch (obstacle.type()) {
                case 1:
                    item.setMarker(getResources().getDrawable(R.drawable.marker_schranke));
                    break;
                case 2:
                    item.setMarker(getResources().getDrawable(R.drawable.marker_treppe));
                    break;
                case 3:
                    item.setMarker(getResources().getDrawable(R.drawable.marker_engstelle));
                    break;
                case 4:
                    item.setMarker(getResources().getDrawable(R.drawable.marker_stufe));
                    break;
                case 5:
                    item.setMarker(getResources().getDrawable(R.drawable.marker_rinne));
                    break;
                case 6:
                    item.setMarker(getResources().getDrawable(R.drawable.marker_poller));
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

        map.getOverlays().add(new ItemizedIconOverlay<OverlayItem>(overlayItemArray, gestureListener, new CustomResourceProxy(this)));
    }

    private void _addRoutesToMap(MapView map) {
        for (Route route : _list_route) {
            PathOverlay path = parseGpxFile(route);
            switch (route.rating()) {
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

            if (route.id() == _route_id)
                path.getPaint().setStrokeWidth(4);

            map.getOverlays().add(path);
        }
    }

    private Route _get_route(int id) {
        for (Route route : _list_route) {
            if (route.id() == id)
                return route;
        }

        return null;
    }

    public void onClickgetLocation (View view) {

        GPSTracker gpst = new GPSTracker(MainActivity.this);
        // check if GPS enabled
        if (gpst.canGetLocation()) {
            double latitude = gpst.getLatitude();
            double longitude = gpst.getLongitude();

            map.getController().setCenter(new GeoPoint(latitude, longitude));
            map.getController().setZoom(15);
            GeoPoint defaultPoint = new GeoPoint(latitude, longitude);
            map.getController().animateTo(defaultPoint);
            Marker startMarker = new Marker(map);
            startMarker.setPosition(new GeoPoint(latitude, longitude));
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            startMarker.setIcon(getResources().getDrawable(location));
            startMarker.setTitle("Ihr Standort:" + " Lat: " + gpst.getLatitude() + " Lon: " + gpst.getLongitude());
            map.getOverlays().add(startMarker);

        } else {
            gpst.stopUsingGPS();
        }
    }

}
