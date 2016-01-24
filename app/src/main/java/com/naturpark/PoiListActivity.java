package com.naturpark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.MenuItemImpl;
import android.support.v7.widget.Toolbar;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PoiListActivity extends AppCompatActivity
        implements View.OnClickListener {

    private SharedPreferences _preferences;

    private List<PoiType> _list_poi_type;
    private List<Poi> _list_poi;

    // filter variables
    private List<Integer> _filtered_poi_types= new ArrayList<Integer>();
    private int _filtered_rating = 0;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("####################################################################################### PoiList::onCreate");
        setContentView(R.layout.activity_poi_type_list);

        _preferences = getSharedPreferences("naturpark.prf", MODE_PRIVATE);

        List<String> strings = new ArrayList<String>(Arrays.asList(_preferences.getString("FilteredPoiTypes", "").split(",")));
        for (String s: strings) {
            try {
                _filtered_poi_types.add(Integer.parseInt(s.trim()));
            }
            catch (NumberFormatException e) {
                /* nothing to do here, just cry */
            }
        }


        _filtered_rating = _preferences.getInt("FilteredPoiRating", 0);

        System.out.println("XXXXXXXXXX:" + _filtered_poi_types.toString());
        System.out.println("XXXXXXXXXX:" + _filtered_rating);

        _list_poi_type = new DbManager(this).queryPoiTypeList();
        _list_poi = new DbManager(this).queryPoiList();

        SharedPreferences.Editor editor = _preferences.edit();
        editor.putInt("SelectedPoi", 0);
        editor.commit();

        init();

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationViewListener(this, drawerLayout));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

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
                } else {
                    findViewById(R.id.tablelayout).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.button_show_filter)).setImageDrawable(getResources().getDrawable(R.drawable.icon_arrow_up));
                }
            }
        });

        ((TextView) findViewById(R.id.textview_type)).setText("[ " + (_filtered_poi_types.size() > 0 ? _get_poi_type(_filtered_poi_types.get(0)).name() : "") + (_filtered_poi_types.size() > 1 ? ", ... " : "") + " ]");
        ((TextView) findViewById(R.id.textview_rating)).setText(Poi.RatingStrings[_filtered_rating]);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("####################################################################################### PoiList::onStart");
    }
    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("####################################################################################### PoiList::onStop");
    }

    public void onClick(View view) {

        view.setBackgroundColor(Color.GRAY);

        System.out.print("Seleceted POI:" + view.getId());

        SharedPreferences.Editor editor = _preferences.edit();
        editor.putInt("SelectedRoute", 0);
        editor.putInt("SelectedPoi", view.getId());
        editor.commit();

        startActivity(new Intent(this, MainActivity.class));
    }

    public void onClickHeaderType(View view) {
        PopupMenu menu = new PopupMenu(this, view);

        for (PoiType poiType : _list_poi_type) {
            menu.getMenu().add(Menu.NONE, poiType.id(), Menu.NONE, poiType.name());
            menu.getMenu().findItem(poiType.id()).setCheckable(true);
            if (_filtered_poi_types.contains(poiType.id()))
                menu.getMenu().findItem(poiType.id()).setChecked(true);
        }
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                menuItem.setChecked(!menuItem.isChecked());
                if (menuItem.isChecked()) {
                    _filtered_poi_types.add(new Integer(menuItem.getItemId()));
                } else {
                    _filtered_poi_types.remove(new Integer(menuItem.getItemId()));
                }
                ((TextView) findViewById(R.id.textview_type)).setText("[ " + (_filtered_poi_types.size() > 0 ? _get_poi_type(_filtered_poi_types.get(0)).name() : "") + (_filtered_poi_types.size() > 1 ? ", ... " : "") + " ]");

                SharedPreferences.Editor editor = _preferences.edit();
                editor.putString("FilteredPoiTypes", _filtered_poi_types.toString().substring(1, _filtered_poi_types.toString().length() - 1));
                editor.commit();

                init();
                return true;
            }
        });
        menu.show();
    }

    public void onClickHeaderRating(View view) {
        PopupMenu menu = new PopupMenu(this, view);

        if (_filtered_rating != 0) {
            _filtered_rating = 0;
            ((TextView) findViewById(R.id.textview_rating)).setText("alle");
            init();

            return;
        }

        for (int i = 1; i < Poi.RatingStrings.length; ++i) {
            menu.getMenu().add(Menu.NONE, i, Menu.NONE, Poi.RatingStrings[i]);
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                _filtered_rating = menuItem.getItemId();
                ((TextView) findViewById(R.id.textview_rating)).setText(menuItem.getTitle());
                SharedPreferences.Editor editor = _preferences.edit();
                editor.putInt("FilteredPoiRating", _filtered_rating);
                editor.commit();
                init();
                return true;
            }
        });
        menu.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(!item.isChecked());

        /*_type = item.getItemId();*/
        return true;
    }

    public void init() {
        LinearLayout list = (LinearLayout) findViewById(R.id.layout_poi);
        list.removeAllViews();
        list.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        int cnt_poi = 0;
        System.out.println("list_poi size\n" + _list_poi.size());

        for (Poi poi : _list_poi) {

            if (!_filtered_poi_types.contains(poi.type())) {
                continue;
            }

            if (_filtered_rating != 0 && poi.rating_id() != _filtered_rating) {
                continue;
            }

            cnt_poi++;

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
                row.addView(_create_text_view(6, poi.rating(), 14, _get_rating_color(poi.rating_id())), params);
            }

            {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_LEFT);
                params.addRule(RelativeLayout.BELOW, 1);
                row.addView(_create_text_view(2, _get_poi_type(poi.type()).name(), 16), params);
            }
            list.addView(row);
        }

        ((TextView) findViewById(R.id.textview_summary)).setText(cnt_poi + " / " + _list_poi.size() + " ausgewählte POI");
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

    private PoiType _get_poi_type(int poi_type_id) {

        for (PoiType poiType : _list_poi_type) {
            if (poiType.id() == poi_type_id)
                return poiType;
        }

        /* we should never reach this code, if database is consistent */
        return null;
    }

}
