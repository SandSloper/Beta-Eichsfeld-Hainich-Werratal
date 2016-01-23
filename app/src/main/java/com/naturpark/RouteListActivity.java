package com.naturpark;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class RouteListActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Route> _list_route;
    private List<Obstacle> _list_obstacle;

    // used for filtering
    private String _region;
    private float _length_min;
    private float _length_max;
    private int _quality;
    private int _grade_avg;
    private int _rating;

    boolean _sort_by_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        System.out.println("####################################################################### onCreate");

        _region = "";
        _length_min = 0;
        _length_max = 0;
        _quality = 0;
        _rating = 0;
        _grade_avg = 0;
        _sort_by_name = false;
        _list_route = new DbManager(this).queryRouteList();
        _list_obstacle = new DbManager(this).queryObstacleList();
        init();

        // Initializing Drawer Layout and ActionBarToggle
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

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

        findViewById(R.id.tablelayout).setVisibility(View.GONE);
        ImageButton actionButton = (ImageButton) findViewById(R.id.button_show_filter);
        actionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (findViewById(R.id.tablelayout).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.tablelayout).setVisibility(View.GONE);
                    ((ImageButton) findViewById(R.id.button_show_filter)).setImageDrawable(getResources().getDrawable(R.drawable.icon_arrow_down));
                }
                else {
                    findViewById(R.id.tablelayout).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.button_show_filter)).setImageDrawable(getResources().getDrawable(R.drawable.icon_arrow_up));
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("####################################################################### onStart");
    }

    @Override
    public void onClick(View view) {


        view.setBackgroundColor(Color.GRAY);

        System.out.print("Seleceted Route ID:" + view.getId());

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Route", view.getId());
        startActivity(intent);
    }


    public void onClickHeaderRegion(View view) {
        PopupMenu menu = new PopupMenu(this, view);

        if (!_region.isEmpty()) {
            _region = "";
            ((TextView)findViewById(R.id.textview_region)).setText("alle");
            init();

            return;
        }

        ArrayList<String> list_region = _get_list_region();

        for (int i = 0; i < list_region.size(); ++i)
            menu.getMenu().add(Menu.NONE, i, Menu.NONE, list_region.get(i));

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                _region = menuItem.getTitle().toString();
                ((TextView)findViewById(R.id.textview_region)).setText(_region);
                init();
                return true;
            }
        });
        menu.show();
    }

    public void onClickHeaderLength(View view) {
        PopupMenu menu = new PopupMenu(this, view);

        if (_length_max != 0) {
            _length_min = 0;
            _length_max = 0;
            ((TextView)findViewById(R.id.textview_length)).setText("alle");
            init();

            return;
        }

        menu.getMenu().add(10, 0, Menu.NONE, "   - 10 km");
        menu.getMenu().add(20, 10, Menu.NONE, "10 - 20 km");
        menu.getMenu().add(30, 20, Menu.NONE, "20 - 30 km");
        menu.getMenu().add(40, 30, Menu.NONE, "30 -    km");
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                _length_min = menuItem.getItemId();
                _length_max = menuItem.getGroupId();
                ((TextView) findViewById(R.id.textview_length)).setText(menuItem.getTitle());
                init();
                return true;
            }
        });
        menu.show();
    }

    public void onClickHeaderQuality(View view) {

        if (_quality != 0) {
            _quality = 0;
            ((TextView)findViewById(R.id.textview_quality)).setText("alle");
            init();

            return;
        }

        PopupMenu menu = new PopupMenu(this, view);
        menu.getMenu().add(Menu.NONE, 1, Menu.NONE, "sehr gut");
        menu.getMenu().add(Menu.NONE, 2, Menu.NONE, "gut");
        menu.getMenu().add(Menu.NONE, 3, Menu.NONE, "mittel");
        menu.getMenu().add(Menu.NONE, 4, Menu.NONE, "schlecht");
        menu.getMenu().add(Menu.NONE, 5, Menu.NONE, "herausfordernd");
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                _quality = menuItem.getItemId();
                ((TextView)findViewById(R.id.textview_quality)).setText(menuItem.getTitle());
                init();
                return true;
            }
        });
        menu.show();
    }

    public void onClickHeaderGrade(View view) {

        if (_grade_avg != 0) {
            _grade_avg = 0;
            ((TextView)findViewById(R.id.textview_grade_avg)).setText("alle");
            init();

            return;
        }

        PopupMenu menu = new PopupMenu(this, view);
        menu.getMenu().add(Menu.NONE, 1, Menu.NONE, "1%");
        menu.getMenu().add(Menu.NONE, 1, Menu.NONE, "2%");
        menu.getMenu().add(Menu.NONE, 1, Menu.NONE, "3%");
        menu.getMenu().add(Menu.NONE, 1, Menu.NONE, "5%");
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                init();
                _grade_avg = menuItem.getItemId();
                ((TextView) findViewById(R.id.textview_grade_avg)).setText(menuItem.getTitle());
                return true;
            }
        });
        menu.show();
    }

    public void onClickHeaderRating(View view) {
        PopupMenu menu = new PopupMenu(this, view);

        if (_rating != 0) {
            _rating = 0;
            ((TextView)findViewById(R.id.textview_quality)).setText("alle");
            init();

            return;
        }

        menu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Ungeeingt");
        menu.getMenu().add(Menu.NONE, 2, Menu.NONE, "Bedingt");
        menu.getMenu().add(Menu.NONE, 3, Menu.NONE, "Geeignet");
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                _rating = menuItem.getItemId();
                ((TextView) findViewById(R.id.textview_rating)).setText(menuItem.getTitle());
                init();
                return true;
            }
        });
        menu.show();
    }


    private void init() {
        LinearLayout list = (LinearLayout) findViewById(R.id.layout_route);
        list.removeAllViews();
        list.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        int cnt_route = 0;
        System.out.println("route size:%d\n" + _list_route.size());
        for (Route route : _list_route) {

            if (!_region.isEmpty() && !route.region().equals(_region))
                continue;

            if ((_length_max != 0) && (route.length() < _length_min  || route.length() > _length_max))
                continue;

            if ((_grade_avg != 0) && (route.slope_avg() > _grade_avg))
                continue;

            if ((_quality != 0) && (route.quality() != _quality))
                continue;

            if ((_rating != 0) && (route.rating() != _rating))
                continue;

            cnt_route++;

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
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_BOTTOM);;
                row.addView(_create_text_view(6, _get_rating_text(route.rating()), 14, _get_rating_color(route.rating())), params);
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
                row.addView(_create_text_view(4, _get_quality_text(route.quality()), 14), params);
            }
            {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.BELOW, 2);
                row.addView(_create_text_view(5, _get_obstacles(route.id()) + "   " +route.slope_avg() + "%/" + route.slope_max() + "%", 14), params);
            }

            list.addView(row);
        }

        ((TextView)findViewById(R.id.textview_summary)).setText(cnt_route + "/" + _list_route.size() + " ausgewählte Routen");
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

    private String _get_quality_text(int quality) {

        switch (quality) {
            case 1:
                return "sehr gut";
            case 2:
                return "gut";
            case 3:
                return "mittel";
            case 4:
                return "schlecht";
            case 5:
                return "herausfordernd";
            default:
                return "unbekannt";
        }
    }

    private String _get_rating_text(int rating)
    {
        switch (rating) {
            case 1:
                return "Ungeeignet";
            case 2:
                return "Bedingt";
            case 3:
                return "Geeignet";
            default:
                return "Unbekannt";
        }
    }

    private int _get_rating_color(int rating) {
        switch (rating) {
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
}
