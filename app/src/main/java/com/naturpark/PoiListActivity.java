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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.naturpark.data.Poi;
import com.naturpark.data.PoiType;

import java.util.List;

public class PoiListActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private List<PoiType> _list_poi_type;
    private List<Poi> _list_poi;

    private int _type = 0;
    private int _classification = 0;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_type_list);


        _list_poi_type = new DbManager(this).queryPoiTypeList();
        _list_poi = new DbManager(this).queryPoiList();

        init();

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationViewListener(this, drawerLayout));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                    ((ImageButton) findViewById(R.id.button_show_filter)).setImageDrawable(getResources().getDrawable(R.drawable.shift_left));
                } else {
                    findViewById(R.id.tablelayout).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.button_show_filter)).setImageDrawable(getResources().getDrawable(R.drawable.shift_right));
                }
            }
        });
    }

    public void onClickHeaderType(View view) {
        PopupMenu menu = new PopupMenu(this, view);

        for (PoiType poiType : _list_poi_type) {
            menu.getMenu().add(Menu.NONE, poiType.id(), Menu.NONE, poiType.name());
        }
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                _type = menuItem.getItemId();
                ((TextView) findViewById(R.id.textview_type)).setText(menuItem.getTitle());
                init();
                return true;
            }
        });
        menu.show();
    }

    public void onClickHeaderRating(View view) {
        PopupMenu menu = new PopupMenu(this, view);

        if (_classification != 0) {
            _classification = 0;
            ((TextView) findViewById(R.id.textview_rating)).setText("alle");
            init();

            return;
        }

        menu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Ungeeingt");
        menu.getMenu().add(Menu.NONE, 2, Menu.NONE, "Bedingt");
        menu.getMenu().add(Menu.NONE, 3, Menu.NONE, "Geeignet");
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                _classification = menuItem.getItemId();
                ((TextView) findViewById(R.id.textview_rating)).setText(menuItem.getTitle());
                init();
                return true;
            }
        });
        menu.show();
    }

    public void init() {
        LinearLayout list = (LinearLayout) findViewById(R.id.layout_poi);
        list.removeAllViews();
        list.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        int cnt_route = 0;
        System.out.println("route size:%d\n" + _list_poi.size());

        for (Poi poi : _list_poi) {

            if (_type != 0 && poi.type() != _type) {
                continue;
            }

            if (_classification != 0 && poi.classification_id() != _classification) {
                continue;
            }

            cnt_route++;

            RelativeLayout row = new RelativeLayout(this);
            row.setId(poi.id());
            row.setClickable(true);
            row.setOnClickListener(this);

            {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_LEFT);
                params.addRule(RelativeLayout.ALIGN_TOP);
                row.addView(_create_text_view(1, poi.name(), 18), params);
            }

            {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_BOTTOM);;        int num = 0;
                row.addView(_create_text_view(6, poi.classification(), 14, _get_classification_color(poi.classification_id())), params);
            }

            {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_LEFT);
                params.addRule(RelativeLayout.BELOW, 1);
                row.addView(_create_text_view(2, _get_poi_type(poi.type()).name(), 16), params);
            }
            list.addView(row);
        }

        ((TextView)findViewById(R.id.textview_summary)).setText(cnt_route + "/" + _list_poi.size() + " ausgewählte POI");
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

    private String _get_classifcation_text(int rating)
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

    private int _get_classification_color(int rating) {
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

    private PoiType _get_poi_type(int poi_type_id) {

        for (PoiType poiType : _list_poi_type) {
            if (poiType.id() == poi_type_id)
                return poiType;
        }

        /* we should never reach this code, if database is consistent */
        return null;
    }

}
