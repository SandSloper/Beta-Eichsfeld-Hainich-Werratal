package com.naturpark;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.naturpark.data.Obstacle;
import com.naturpark.data.Route;

import java.util.ArrayList;
import java.util.List;

public class RouteListActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private List<Route> _list_route;
    private List<Obstacle> _list_obstacle;

    private int _popup_menu_id;
    // used for filtering
    private String _region;
    private float _length_min;
    private float _length_max;
    private int _rating;

    boolean _sort_by_name;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Button button = (Button) findViewById(R.id.button_close_route_list);
        //button.setOnClickListener(this);

        _region = "";
        _length_min = 0;
        _length_max = 0;
        _rating = 0;
        _sort_by_name = false;
        _list_route = new DbManager(this).queryRouteList();
        _list_obstacle = new DbManager(this).queryObstacleList();
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

    @Override
    public void onClick(View view) {


        view.setBackgroundColor(Color.GRAY);

        //RelativeLayout layout = (RelativeLayout)view;
        //TextView textView = (TextView) tablerow.getChildAt(0);
        //Integer route_id = Integer.parseInt(textView.getText().toString());
        System.out.print("Seleceted Route ID:" + view.getId());

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Route", view.getId());
        startActivity(intent);
    }


    public void onClickHeaderRegion(View view) {
        PopupMenu menu = new PopupMenu(this, view);

        if (!_region.isEmpty()) {
            _region = "";
            init();

            return;
        }

        ArrayList<String> list_region = _get_list_region();

        for (int i = 0; i < list_region.size(); ++i)
            menu.getMenu().add(Menu.NONE, i, Menu.NONE, list_region.get(i));

        menu.setOnMenuItemClickListener(this);
        menu.show();
        _popup_menu_id = 1;
    }

    public void onClickHeaderName(View view) {
        _sort_by_name = true;
        _popup_menu_id = 0;

    }

    public void onClickHeaderLength(View view) {
        PopupMenu menu = new PopupMenu(this, view);

        if (_length_max != 0) {
            _length_min = 0;
            _length_max = 0;
            init();

            return;
        }

        menu.getMenu().add(10, 0, Menu.NONE, "   - 10 km");
        menu.getMenu().add(20, 10, Menu.NONE, "10 - 20 km");
        menu.getMenu().add(30, 20, Menu.NONE, "20 - 30 km");
        menu.getMenu().add(40, 30, Menu.NONE, "30 -    km");
        menu.setOnMenuItemClickListener(this);
        menu.show();
        _popup_menu_id = 3;
    }

    public void onClickHeaderRating(View view) {
        PopupMenu menu = new PopupMenu(this, view);

        if (_rating != 0) {
            _rating = 0;
            init();

            return;
        }
        menu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Ungeeingt");
        menu.getMenu().add(Menu.NONE, 2, Menu.NONE, "Bedingt");
        menu.getMenu().add(Menu.NONE, 3, Menu.NONE, "Geeignet");
        menu.setOnMenuItemClickListener(this);
        menu.show();
        _popup_menu_id = 4;
    }

    public boolean onMenuItemClick(MenuItem item) {
        System.out.println("item:" + _popup_menu_id + " " + +item.getItemId());

        switch (_popup_menu_id) {
            case 1: // Region
                System.out.println("region:" + item.getTitle());
                _region = item.getTitle().toString();
                init();
                break;
            case 2: // Name
                break;

            case 3: // Length
                _length_min = item.getItemId();
                _length_max = item.getGroupId();
                init();
                break;

            case 4:
                _rating = item.getItemId();
                init();
                break;
        }

        return true;
    }

    private void init() {
        LinearLayout list = (LinearLayout) findViewById(R.id.layout_route);
        list.removeAllViews();

        System.out.println("route size:%d\n" + _list_route.size());
        for (Route route : _list_route) {

            if (!_region.isEmpty() && !route.region().equals(_region))
                continue;

            if ((_length_max != 0) && (route.length() < _length_min  || route.length() > _length_max))
                continue;

            if ((_rating != 0) && (route.rating() != _rating))
                continue;

            if (route != _list_route.get(0)) {
                ImageView divider = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5);
                params.setMargins(2, 2, 2, 2);
                divider.setLayoutParams(params);
                divider.setBackgroundColor(Color.BLACK);
                list.addView(divider);
            }

            RelativeLayout row = new RelativeLayout(this);
            row.setId(route.id());
            row.setClickable(true);
            row.setOnClickListener(this);

            {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_LEFT);
                params.addRule(RelativeLayout.ALIGN_TOP);
                row.addView(_create_text_view(1, route.name(), 18), params);
            }
            {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_LEFT);
                params.addRule(RelativeLayout.BELOW, 1);
                row.addView(_create_text_view(2, route.region(), 16), params);
            }
            {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.BELOW, 1);
                row.addView(_create_text_view(3, "" + route.length() + " km", 16), params);
            }
            {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.BELOW, 2);
                row.addView(_create_text_view(4, ""+_get_quality_text(route.quality())+":", 14, _get_quality_color(route.quality())), params);

            }
            {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.BELOW, 2);
                row.addView(_create_text_view(5, ""+route.slope_avg() + "%/" + route.slope_max() + "%", 14), params);
            }
            {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(RelativeLayout.RIGHT_OF, 4);
                params.addRule(RelativeLayout.LEFT_OF, 5);
                params.addRule(RelativeLayout.BELOW, 2);
                row.addView(_create_text_view(6, "" + _get_obstacles(route.id()), 14), params);
            }

            list.addView(row);
        }
    }


    private TextView _create_text_view(int id, String text, int text_size) {

        TextView view = new TextView(this);
        view.setId(id);
        view.setText(text);
        view.setTextSize(text_size);

        return view;
    }

    private TextView _create_text_view(int id, String text, int text_size, int color) {

        TextView view = new TextView(this);
        view.setId(id);
        view.setText(text);
        view.setTextSize(text_size);
        view.setTextColor(color);

        return view;
    }

    private RelativeLayout.LayoutParams _create_params(int[] rules) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        for (int rule : rules)
        params.addRule(rule);

        return params;
    }


    private boolean _is_in_list(ArrayList list, String text) {
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).equals(text))
                return true;
        }

        return false;
    }

    private ArrayList _get_list_region() {
        ArrayList list = new ArrayList();

        for (Route route : _list_route) {
            if (! _is_in_list(list, route.region())) {
                list.add(route.region());
            }
        }

        return list;
    }

    private int _get_obstacles(int id) {
        int num = 0;

        for (Obstacle obstacle : _list_obstacle) {
            if (obstacle.route_id() == id)
                ++num;
        }

        return num;
    }

    private String _get_quality_text(int quality)
    {
        switch (quality) {
            case 1:
                return "Ungeeignet";
            case 2:
                return "Bedingt";
            case 3:
                return "Geeugnet";
            default:
                return "Unbekannt";
        }
    }

    private int _get_quality_color(int quality) {
        switch (quality) {
            case 1:
                return Color.RED;
            case 2:
                return Color.YELLOW;
            case 3:
                return Color.GREEN;
            default:
                return Color.GRAY;
        }
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
